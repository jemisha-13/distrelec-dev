package com.namics.distrelec.b2b.facades.bloomreach.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.core.bloomreach.client.DistBloomreachAPIClient;
import com.namics.distrelec.b2b.core.bloomreach.enums.BloomreachAction;
import com.namics.distrelec.b2b.core.bloomreach.enums.BloomreachCommand;
import com.namics.distrelec.b2b.core.bloomreach.enums.BloomreachConsentCategory;
import com.namics.distrelec.b2b.core.bloomreach.enums.BloomreachEvent;
import com.namics.distrelec.b2b.core.bloomreach.enums.BloomreachTouchpoint;
import com.namics.distrelec.b2b.core.bloomreach.exception.DistBloomreachBatchException;
import com.namics.distrelec.b2b.core.bloomreach.exception.DistBloomreachExportException;
import com.namics.distrelec.b2b.core.service.bloomreach.data.Command;
import com.namics.distrelec.b2b.core.service.bloomreach.data.ConsentConfirmationProperties;
import com.namics.distrelec.b2b.core.service.bloomreach.data.ConsentProperties;
import com.namics.distrelec.b2b.core.service.bloomreach.data.CustomerAttributeData;
import com.namics.distrelec.b2b.core.service.bloomreach.data.CustomerIds;
import com.namics.distrelec.b2b.core.service.bloomreach.data.CustomerProperties;
import com.namics.distrelec.b2b.core.service.bloomreach.data.Data;
import com.namics.distrelec.b2b.core.service.bloomreach.data.DoubleOptinConsent;
import com.namics.distrelec.b2b.core.service.bloomreach.data.ExportConsentsResponseData;
import com.namics.distrelec.b2b.core.service.bloomreach.data.ExportConsentsRoot;
import com.namics.distrelec.b2b.core.service.bloomreach.data.Properties;
import com.namics.distrelec.b2b.core.service.bloomreach.data.Root;
import com.namics.distrelec.b2b.core.service.bloomreach.data.SoftCustomerIds;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.b2b.core.util.DistUtils;
import com.namics.distrelec.b2b.facades.bloomreach.DistBloomreachFacade;
import com.namics.distrelec.b2b.facades.bloomreach.data.DistBloomreachConsentData;
import com.namics.distrelec.b2b.facades.user.data.DistConsentData;
import com.namics.distrelec.b2b.facades.user.data.DistMarketingConsentData;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.site.BaseSiteService;

public class DistBloomreachFacadeImpl implements DistBloomreachFacade {

    private static final Logger LOG = LoggerFactory.getLogger(DistBloomreachFacadeImpl.class);

    private static final String UNLIMITED_VALIDITY = "unlimited";

    private static final String CONSENT_TYPE = "consent";

    private static final String VALID_MODE = "valid";

    private static final String CHECKOUT_PLACEMENT = "order_confirmation";

    @Autowired
    private DistBloomreachAPIClient distBloomreachAPIClient;

    @Autowired
    private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private DistUserService userService;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public void sendBatchRequestToBloomreach(final String requestString) throws DistBloomreachBatchException {
        distBloomreachAPIClient.callBatchRequest(requestString);
    }

    @Override
    public DistBloomreachConsentData exportCustomerConsentsFromBloomreach(final String email) throws IOException, DistBloomreachExportException {
        final String requestString = createBloomreachExportConsentsRequest(email, getBloomreachConsentCategories());
        final String customerConsents = distBloomreachAPIClient.exportCustomerConsents(requestString);
        final ObjectMapper objectMapper = new ObjectMapper();
        final ExportConsentsResponseData exportResponse = objectMapper.readValue(customerConsents, ExportConsentsResponseData.class);
        return getBloomreachDataFromResponse(email, exportResponse);
    }

    @Override
    public String getPreferenceCenterUpdates(final DistMarketingConsentData updatedconsents, final DistConsentData consentData,
                                             final BloomreachTouchpoint touchpoint) throws IOException, DistBloomreachExportException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final DistBloomreachConsentData existingBloomreachConsents = exportCustomerConsentsFromBloomreach(consentData.getUid());
        final Root command = new Root();
        final List<Command> commands = getUpdatedCommandForPreferenceCenter(existingBloomreachConsents, updatedconsents, consentData, touchpoint);
        if (CollectionUtils.isNotEmpty(commands)) {
            command.setCommands(commands);
            return objectMapper.writeValueAsString(command);
        }
        return StringUtils.EMPTY;
    }

    @Override
    public void updateCustomerInBloomreach(final DistConsentData consentData) throws DistBloomreachBatchException, JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Root command = new Root();
        final List<Command> commands = new ArrayList<>();

        final Command commandObject = new Command();
        commandObject.setName(BloomreachCommand.CUSTOMER_COMMAND.commandName);
        commandObject.setCommand_id(BloomreachCommand.CUSTOMER_COMMAND.commandId);

        final Data commandData = new Data();
        commandData.setCustomer_ids(createCustomerIds(consentData));
        commandData.setEvent_type(BloomreachEvent.REGISTRATION.eventType);
        final CustomerProperties customerProperties = createCustomerProperties(consentData, BloomreachTouchpoint.MYACCOUNT);
        commandData.setProperties(customerProperties);
        commandObject.setData(commandData);
        commands.add(commandObject);
        command.setCommands(commands);
        sendBatchRequestToBloomreach(objectMapper.writeValueAsString(command));

    }

    private List<Command> getNonConfirmPreferenceCenterCommand(final DistBloomreachConsentData existingBloomreachConsents,
                                                               final DistMarketingConsentData updatedconsents, final DistConsentData consentData,
                                                               final BloomreachTouchpoint touchpoint) {
        final List<Command> commands = new ArrayList<>();
        if (updatedconsents.isEmailConsent() != existingBloomreachConsents.isEmailConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isEmailConsent(), BloomreachConsentCategory.EMAIL, consentData,
                                              touchpoint));
        }

        if (updatedconsents.isKnowHowConsent() != existingBloomreachConsents.isContentNewsConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isKnowHowConsent(), BloomreachConsentCategory.CONTENT_NEWS,
                                              consentData,
                                              touchpoint));
        }

        if (updatedconsents.isSaleAndClearanceConsent() != existingBloomreachConsents.isSalesAndClearanceConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isSaleAndClearanceConsent(), BloomreachConsentCategory.SALES_CLEARANCE,
                                              consentData,
                                              touchpoint));
        }

        if (updatedconsents.isNewsLetterConsent() != existingBloomreachConsents.isNewBrandsConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isNewsLetterConsent(), BloomreachConsentCategory.NEW_BRANDS,
                                              consentData,
                                              touchpoint));
        }

        if (updatedconsents.isPersonalisedRecommendationConsent() != existingBloomreachConsents.isPersonalisedRecommendationsConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isPersonalisedRecommendationConsent(),
                                              BloomreachConsentCategory.PERSONALISED_RECOMMENDATIONS,
                                              consentData,
                                              touchpoint));
        }

        if (updatedconsents.isCustomerSurveysConsent() != existingBloomreachConsents.isNpsConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isCustomerSurveysConsent(), BloomreachConsentCategory.NPS,
                                              consentData,
                                              touchpoint));
        }

        if (updatedconsents.isPersonalisationConsent() != existingBloomreachConsents.isPersonalisationConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isPersonalisationConsent(), BloomreachConsentCategory.PERSONALISATION,
                                              consentData,
                                              touchpoint));
        }

        if (updatedconsents.isProfilingConsent() != existingBloomreachConsents.isProfilingConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isProfilingConsent(), BloomreachConsentCategory.PROFILING,
                                              consentData,
                                              touchpoint));
        }

        if (updatedconsents.isPhoneConsent() != existingBloomreachConsents.isPhoneConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isPhoneConsent(), BloomreachConsentCategory.PHONE, consentData,
                                              touchpoint));
        }

        if (updatedconsents.isSmsConsent() != existingBloomreachConsents.isSmsMarketingConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isSmsConsent(), BloomreachConsentCategory.SMS, consentData,
                                              touchpoint));
        }

        if (updatedconsents.isPostConsent() != existingBloomreachConsents.isPaperConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isPostConsent(), BloomreachConsentCategory.PAPER, consentData,
                                              touchpoint));
        }
        return commands;

    }

    private List<Command> getUpdatedCommandForPreferenceCenter(final DistBloomreachConsentData existingBloomreachConsents,
                                                               final DistMarketingConsentData updatedconsents, final DistConsentData consentData,
                                                               final BloomreachTouchpoint touchpoint) {

        if (!isConsentConfirmationRequired()) {
            return getNonConfirmPreferenceCenterCommand(existingBloomreachConsents, updatedconsents, consentData, touchpoint);
        }

        return getConfirmPreferenceCenterCommand(existingBloomreachConsents, updatedconsents, consentData, touchpoint);

    }

    private List<String> getUpdatedConsentCategoriesList(final DistBloomreachConsentData existingBloomreachConsents,
                                                         final DistMarketingConsentData updatedconsents) {

        final List<String> updatedCategories = new ArrayList<>();
        if (updatedconsents.isEmailConsent() != existingBloomreachConsents.isEmailConsent()) {
            updatedCategories.add(BloomreachConsentCategory.EMAIL.category);
        }
        if (updatedconsents.isKnowHowConsent() != existingBloomreachConsents.isContentNewsConsent()) {
            updatedCategories.add(BloomreachConsentCategory.CONTENT_NEWS.category);
        }

        if (updatedconsents.isSaleAndClearanceConsent() != existingBloomreachConsents.isSalesAndClearanceConsent()) {
            updatedCategories.add(BloomreachConsentCategory.SALES_CLEARANCE.category);
        }

        if (updatedconsents.isNewsLetterConsent() != existingBloomreachConsents.isNewBrandsConsent()) {
            updatedCategories.add(BloomreachConsentCategory.NEW_BRANDS.category);
        }

        if (updatedconsents.isPersonalisedRecommendationConsent() != existingBloomreachConsents.isPersonalisedRecommendationsConsent()) {
            updatedCategories.add(BloomreachConsentCategory.PERSONALISED_RECOMMENDATIONS.category);
        }

        if (updatedconsents.isCustomerSurveysConsent() != existingBloomreachConsents.isNpsConsent()) {
            updatedCategories.add(BloomreachConsentCategory.NPS.category);
        }
        return updatedCategories;

    }

    private List<Command> getConfirmPreferenceCenterCommand(final DistBloomreachConsentData existingBloomreachConsents,
                                                            final DistMarketingConsentData updatedconsents, final DistConsentData consentData,
                                                            final BloomreachTouchpoint touchpoint) {
        final List<Command> commands = new ArrayList<>();

        if (updatedconsents.isEmailConsent() != existingBloomreachConsents.isEmailConsent() && updatedconsents.isEmailConsent()) {
            commands.add(createConfirmationConsentCommand(consentData, getUpdatedConsentCategoriesList(existingBloomreachConsents, updatedconsents),
                                                          touchpoint));
        }

        if (existingBloomreachConsents.isEmailConsent()
                    && updatedconsents.isEmailConsent() != existingBloomreachConsents.isEmailConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isEmailConsent(), BloomreachConsentCategory.EMAIL,
                                              consentData,
                                              touchpoint));
        }

        if (existingBloomreachConsents.isEmailConsent()
                && updatedconsents.isKnowHowConsent() != existingBloomreachConsents.isContentNewsConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isKnowHowConsent(), BloomreachConsentCategory.CONTENT_NEWS,
                                              consentData,
                                              touchpoint));
        }

        if (existingBloomreachConsents.isEmailConsent()
                && updatedconsents.isSaleAndClearanceConsent() != existingBloomreachConsents.isSalesAndClearanceConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isSaleAndClearanceConsent(), BloomreachConsentCategory.SALES_CLEARANCE,
                                              consentData,
                                              touchpoint));
        }

        if (existingBloomreachConsents.isEmailConsent()
                && updatedconsents.isNewsLetterConsent() != existingBloomreachConsents.isNewBrandsConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isNewsLetterConsent(), BloomreachConsentCategory.NEW_BRANDS,
                                              consentData,
                                              touchpoint));
        }

        if (existingBloomreachConsents.isEmailConsent()
                && updatedconsents.isPersonalisedRecommendationConsent() != existingBloomreachConsents.isPersonalisedRecommendationsConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isPersonalisedRecommendationConsent(),
                                              BloomreachConsentCategory.PERSONALISED_RECOMMENDATIONS,
                                              consentData,
                                              touchpoint));
        }

        if (existingBloomreachConsents.isEmailConsent()
                && updatedconsents.isCustomerSurveysConsent() != existingBloomreachConsents.isNpsConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isCustomerSurveysConsent(), BloomreachConsentCategory.NPS,
                                              consentData,
                                              touchpoint));
        }

        if (updatedconsents.isPersonalisationConsent() != existingBloomreachConsents.isPersonalisationConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isPersonalisationConsent(), BloomreachConsentCategory.PERSONALISATION,
                                              consentData,
                                              touchpoint));
        }

        if (updatedconsents.isProfilingConsent() != existingBloomreachConsents.isProfilingConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isProfilingConsent(), BloomreachConsentCategory.PROFILING,
                                              consentData,
                                              touchpoint));
        }

        if (updatedconsents.isPhoneConsent() != existingBloomreachConsents.isPhoneConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isPhoneConsent(), BloomreachConsentCategory.PHONE, consentData,
                                              touchpoint));
        }

        if (updatedconsents.isSmsConsent() != existingBloomreachConsents.isSmsMarketingConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isSmsConsent(), BloomreachConsentCategory.SMS, consentData,
                                              touchpoint));
        }

        if (updatedconsents.isPostConsent() != existingBloomreachConsents.isPaperConsent()) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, updatedconsents.isPostConsent(), BloomreachConsentCategory.PAPER, consentData,
                                              touchpoint));
        }

        return commands;

    }

    @Override
    public String createBloomreachRegistrationRequest(final DistConsentData consentData) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Root command = new Root();
        final BloomreachTouchpoint touchpoint = consentData.isRsCustomer() ? BloomreachTouchpoint.RS_REGISTRATION : BloomreachTouchpoint.REGISTRATION;
        if (consentData.isActiveSubscription() && isConsentConfirmationRequired()) {
            command.setCommands(getConfirmationCommandList(consentData, touchpoint));
        } else {
            command.setCommands(getNonConfirmationCommandList(consentData, touchpoint));
        }
        return objectMapper.writeValueAsString(command);
    }

    private List<Command> getNonConfirmationCommandList(final DistConsentData consentData, final BloomreachTouchpoint touchpoint) {
        final List<Command> commands = new ArrayList<>();

        commands.add(createConsentCommand(BloomreachEvent.CONSENT, consentData.isActiveSubscription(), BloomreachConsentCategory.EMAIL, consentData,
                                          touchpoint));
        commands.add(createConsentCommand(BloomreachEvent.CONSENT, consentData.isActiveSubscription(), BloomreachConsentCategory.CONTENT_NEWS,
                                          consentData, touchpoint));
        commands.add(createConsentCommand(BloomreachEvent.CONSENT, consentData.isActiveSubscription(), BloomreachConsentCategory.NPS, consentData,
                                          touchpoint));
        commands.add(createConsentCommand(BloomreachEvent.CONSENT, consentData.isActiveSubscription(),
                                          BloomreachConsentCategory.PERSONALISED_RECOMMENDATIONS, consentData, touchpoint));
        commands.add(createConsentCommand(BloomreachEvent.CONSENT, consentData.isActiveSubscription(), BloomreachConsentCategory.SALES_CLEARANCE,
                                          consentData, touchpoint));
        commands.add(createConsentCommand(BloomreachEvent.CONSENT, consentData.isActiveSubscription(), BloomreachConsentCategory.NEW_BRANDS,
                                          consentData, touchpoint));
        commands.addAll(getCommonCommandsForRegistration(consentData, touchpoint));
        return commands;
    }

    private List<Command> getConfirmationCommandList(final DistConsentData consentData, final BloomreachTouchpoint touchpoint) {
        final List<Command> commands = new ArrayList<>();
        commands.add(createConfirmationConsentCommand(consentData, getBloomreachConfirmationCategories(), touchpoint));
        commands.addAll(getCommonCommandsForRegistration(consentData, touchpoint));
        return commands;
    }

    private List<Command> getCommonCommandsForRegistration(final DistConsentData consentData, final BloomreachTouchpoint touchpoint) {
        final List<Command> commands = new ArrayList<>();
        commands.add(createCustomerCommand(consentData, touchpoint));
        commands.add(createRegistrationCommand(consentData, touchpoint));
        commands.add(createConsentCommand(BloomreachEvent.CONSENT, consentData.isSmsPermissions(), BloomreachConsentCategory.SMS, consentData,
                                          touchpoint));
        commands.add(createConsentCommand(BloomreachEvent.CONSENT, consentData.isPhonePermission(), BloomreachConsentCategory.PHONE, consentData,
                                          touchpoint));
        commands.add(createConsentCommand(BloomreachEvent.CONSENT, consentData.isPaperPermission(), BloomreachConsentCategory.PAPER, consentData,
                                          touchpoint));
        commands.add(createConsentCommand(BloomreachEvent.CONSENT, consentData.isPersonalisationSubscription(),
                                          BloomreachConsentCategory.PERSONALISATION, consentData, touchpoint));
        commands.add(createConsentCommand(BloomreachEvent.CONSENT, consentData.isProfilingSubscription(), BloomreachConsentCategory.PROFILING,
                                          consentData, touchpoint));
        return commands;
    }

    private boolean isConsentConfirmationRequired() {
        boolean isConfirmationRequired = false;
        try {
            final String[] baseSites = getConfigurationService().getConfiguration()
                                                                .getString("ymkt.customer.sevice.consent.confirmation.required.shop")
                                                                .split(",");
            final UserModel currentUser = getUserService().getCurrentUser();
            final boolean isAnonymous = getUserService().isAnonymousUser(currentUser);
            if (baseSites.length > 0) {
                if (!isAnonymous && currentUser instanceof B2BCustomerModel) {
                    final B2BCustomerModel customer = (B2BCustomerModel) currentUser;
                    final String customerBaseSite = (null != customer.getCustomersBaseSite())
                                                                                              ? customer.getCustomersBaseSite()
                                                                                                        .getUid()
                                                                                              : "";
                    isConfirmationRequired = (null != getCurrentBaseSiteUid())
                                                                               ? Arrays.asList(baseSites).contains(getCurrentBaseSiteUid())
                                                                               : Arrays.asList(baseSites).contains(customerBaseSite);
                } else {
                    isConfirmationRequired = Arrays.asList(baseSites).contains(getCurrentBaseSiteUid());
                }
            }
        } catch (final Exception ex) {
            LOG.info("Exception in checking confirmation flag", ex);
        }
        return isConfirmationRequired;
    }

    private String getCurrentBaseSiteUid() {
        if (getBaseSiteService() != null && getBaseSiteService().getCurrentBaseSite() != null) {
            return getBaseSiteService().getCurrentBaseSite().getUid();
        }
        LOG.warn("no current base site available");
        return null;
    }

    @Override
    public String createBloomreachSubscriptionRequest(final DistConsentData consentData) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Root command = new Root();
        final List<Command> commands = new ArrayList<>();
        final BloomreachTouchpoint touchpoint = getBloomreachTouchpoint(consentData.getPlacement());
        commands.add(createCustomerCommand(consentData, touchpoint));
        if (consentData.isActiveSubscription()) {
            if (!isConsentConfirmationRequired()) {
                commands.add(createConsentCommand(BloomreachEvent.CONSENT, true, BloomreachConsentCategory.EMAIL, consentData, touchpoint));
                commands.add(createConsentCommand(BloomreachEvent.CONSENT, true, BloomreachConsentCategory.CONTENT_NEWS, consentData,
                                                  touchpoint));
                commands.add(createConsentCommand(BloomreachEvent.CONSENT, true, BloomreachConsentCategory.NPS, consentData, touchpoint));
                commands.add(createConsentCommand(BloomreachEvent.CONSENT, true, BloomreachConsentCategory.PERSONALISED_RECOMMENDATIONS,
                                                  consentData, touchpoint));
                commands.add(createConsentCommand(BloomreachEvent.CONSENT, true, BloomreachConsentCategory.SALES_CLEARANCE, consentData,
                                                  touchpoint));
                commands.add(createConsentCommand(BloomreachEvent.CONSENT, true, BloomreachConsentCategory.NEW_BRANDS, consentData, touchpoint));
            } else {
                commands.add(createConfirmationConsentCommand(consentData, getBloomreachConfirmationCategories(), touchpoint));
            }
        }
        if (consentData.getPlacement() != null && CHECKOUT_PLACEMENT.equalsIgnoreCase(consentData.getPlacement())) {
            commands.add(createConsentCommand(BloomreachEvent.CONSENT, consentData.isPersonalisationSubscription(), BloomreachConsentCategory.PERSONALISATION,
                                              consentData, touchpoint));
        }
        command.setCommands(commands);
        return objectMapper.writeValueAsString(command);
    }

    private BloomreachTouchpoint getBloomreachTouchpoint(final String placement) {
        BloomreachTouchpoint touchpoint;
        switch (placement) {
            case "order_confirmation":
                touchpoint = BloomreachTouchpoint.ORDER_CONFIRMATION;
                break;
            case "guest":
                touchpoint = BloomreachTouchpoint.GUEST;
                break;
            case "popup":
                touchpoint = BloomreachTouchpoint.POPUP;
                break;
            case "myaccount":
                touchpoint = BloomreachTouchpoint.MYACCOUNT;
                break;
            case "registration":
                touchpoint = BloomreachTouchpoint.REGISTRATION;
                break;
            case "subuser":
                touchpoint = BloomreachTouchpoint.SUBUSER;
                break;
            default:
                touchpoint = BloomreachTouchpoint.FOOTER;
                break;
        }
        return touchpoint;
    }

    private Command createCustomerCommand(final DistConsentData consentData, final BloomreachTouchpoint touchpoint) {
        final Command command = new Command();
        command.setName(BloomreachCommand.CUSTOMER_COMMAND.commandName);
        command.setCommand_id(BloomreachCommand.CUSTOMER_COMMAND.commandId);

        final Data commandData = new Data();

        commandData.setCustomer_ids(createCustomerIds(consentData));
        if (isSubscription(touchpoint)) {
            commandData.setProperties(createProperties(consentData, touchpoint));
        } else {
            commandData.setProperties(createCustomerProperties(consentData, touchpoint));
        }

        command.setData(commandData);
        return command;

    }

    private Properties createProperties(final DistConsentData consentData, final BloomreachTouchpoint touchpoint) {
        final Properties properties = new Properties();
        populateMandatoryProperties(properties, consentData, touchpoint);
        return properties;
    }

    private ConsentConfirmationProperties createConsentConfirmationProperties(final DistConsentData consentData, final BloomreachTouchpoint touchpoint) {
        final ConsentConfirmationProperties properties = new ConsentConfirmationProperties();
        populateMandatoryProperties(properties, consentData, touchpoint);
        return properties;
    }

    private void populateMandatoryProperties(final Properties properties, final DistConsentData consentData, final BloomreachTouchpoint touchpoint) {
        properties.setEmail(consentData.getUid());
        properties.setLanguage(DistUtils.getCurrentLanguageIsocode());
        final BaseSiteModel currentSite = getBaseSiteService().getCurrentBaseSite();
        properties.setWeb_store_url(getUrlForHeadless(currentSite));
        properties.setPlacement(touchpoint.touchpoint);
    }

    private CustomerProperties createCustomerProperties(final DistConsentData consentData, final BloomreachTouchpoint touchpoint) {
        final CustomerProperties customerProperties = new CustomerProperties();
        populateMandatoryProperties(customerProperties, consentData, touchpoint);
        populateCustomerProperties(customerProperties, consentData);
        return customerProperties;
    }

    private void populateCustomerProperties(final CustomerProperties customerProperties, final DistConsentData consentData) {
        customerProperties.setFirst_name(consentData.getFirstName());
        customerProperties.setLast_name(consentData.getLastName());
        customerProperties.setPhone(consentData.getPhoneNumber());
        customerProperties.setMobile(consentData.getMobileNumber());
        customerProperties.setTitle(consentData.getTitleCode());
        customerProperties.setCountry(consentData.getCountryCode());

    }

    private ConsentProperties createConsentProperties(final DistConsentData consentData, final BloomreachTouchpoint touchpoint) {
        final ConsentProperties consentProperties = new ConsentProperties();
        populateMandatoryProperties(consentProperties, consentData, touchpoint);
        populateCustomerProperties(consentProperties, consentData);
        consentProperties.setValid_until(UNLIMITED_VALIDITY);
        return consentProperties;
    }

    private String getUrlForHeadless(final BaseSiteModel currentSite) {
        return distSiteBaseUrlResolutionService.getHeadlessWebsiteUrlForSite(currentSite, true, "/");
    }

    private boolean isSubscription(final BloomreachTouchpoint touchpoint) {
        return BloomreachTouchpoint.FOOTER.equals(touchpoint) || BloomreachTouchpoint.ORDER_CONFIRMATION.equals(touchpoint) 
                       || BloomreachTouchpoint.POPUP.equals(touchpoint);
    }

    private Command createRegistrationCommand(final DistConsentData consentData, final BloomreachTouchpoint touchpoint) {
        final Command command = new Command();
        command.setName(BloomreachCommand.CONSENT_COMMAND.commandName);
        command.setCommand_id(BloomreachCommand.CONSENT_COMMAND.commandId);

        final Data commandData = new Data();
        commandData.setCustomer_ids(createCustomerIds(consentData));
        commandData.setEvent_type(BloomreachEvent.REGISTRATION.eventType);

        commandData.setProperties(createConsentProperties(consentData, touchpoint));
        command.setData(commandData);
        return command;

    }

    private CustomerIds createCustomerIds(final DistConsentData consentData) {
        final CustomerIds bloomreachId = new CustomerIds();
        bloomreachId.setEmail_id(consentData.getUid());
        bloomreachId.setErp_contact_id(consentData.getErpContactId());
        return bloomreachId;
    }

    private Command createConsentCommand(final BloomreachEvent eventType, final boolean accepted, final BloomreachConsentCategory consentCategory,
                                         final DistConsentData consentData, final BloomreachTouchpoint touchpoint) {
        final Command command = new Command();
        command.setName(BloomreachCommand.CONSENT_COMMAND.commandName);
        command.setCommand_id(BloomreachCommand.CONSENT_COMMAND.commandId + "_" + consentCategory.category);

        final Data commandData = new Data();
        commandData.setCustomer_ids(createCustomerIds(consentData));
        commandData.setEvent_type(eventType.eventType);

        final ConsentProperties properties = createConsentProperties(consentData, touchpoint);
        properties.setAction(accepted ? BloomreachAction.ACCEPT.action : BloomreachAction.REJECT.action);
        properties.setCategory(consentCategory.category);
        commandData.setProperties(properties);
        command.setData(commandData);
        return command;

    }

    private Command createConfirmationConsentCommand(final DistConsentData consentData, final List<String> consentCategories,
                                                     final BloomreachTouchpoint touchpoint) {
        final Command command = new Command();
        command.setName(BloomreachCommand.CONSENT_COMMAND.commandName);
        command.setCommand_id(BloomreachCommand.CONSENT_COMMAND.commandId);

        final Data commandData = new Data();
        commandData.setCustomer_ids(createCustomerIds(consentData));
        commandData.setEvent_type(BloomreachEvent.DOUBLE_OPT_IN.eventType);

        final ConsentConfirmationProperties consentConfirmProperties = createConsentConfirmationProperties(consentData, touchpoint);
        final List<DoubleOptinConsent> optinCategories = new ArrayList<>();
        for (final String category : consentCategories) {
            final DoubleOptinConsent consent = new DoubleOptinConsent();
            consent.setAction(BloomreachAction.ACCEPT.action);
            consent.setCategory(category);
            consent.setValid_until(UNLIMITED_VALIDITY);
            optinCategories.add(consent);
        }
        consentConfirmProperties.setConsent_list(optinCategories);
        consentConfirmProperties.setAction(BloomreachAction.NEW.action);
        commandData.setProperties(consentConfirmProperties);
        command.setData(commandData);
        return command;

    }

    /*
     * Important : If we change this method to add category please could you also change the getBloomreachDataFromResponse . Sequence should be maintained .
     * Since bloomreach API doesn't return Category Id in response we need to do this else we will have to call API for every category
     */
    private List<String> getBloomreachConsentCategories() {
        final List<String> exportCategories = new ArrayList<>();
        exportCategories.add(BloomreachConsentCategory.EMAIL.category);
        exportCategories.add(BloomreachConsentCategory.CONTENT_NEWS.category);
        exportCategories.add(BloomreachConsentCategory.SALES_CLEARANCE.category);
        exportCategories.add(BloomreachConsentCategory.NEW_BRANDS.category);
        exportCategories.add(BloomreachConsentCategory.PERSONALISED_RECOMMENDATIONS.category);
        exportCategories.add(BloomreachConsentCategory.NPS.category);
        exportCategories.add(BloomreachConsentCategory.PERSONALISATION.category);
        exportCategories.add(BloomreachConsentCategory.PROFILING.category);
        exportCategories.add(BloomreachConsentCategory.PAPER.category);
        exportCategories.add(BloomreachConsentCategory.PHONE.category);
        exportCategories.add(BloomreachConsentCategory.SMS.category);
        return exportCategories;
    }

    private List<String> getBloomreachConfirmationCategories() {
        final List<String> exportCategories = new ArrayList<>();
        exportCategories.add(BloomreachConsentCategory.EMAIL.category);
        exportCategories.add(BloomreachConsentCategory.CONTENT_NEWS.category);
        exportCategories.add(BloomreachConsentCategory.SALES_CLEARANCE.category);
        exportCategories.add(BloomreachConsentCategory.NEW_BRANDS.category);
        exportCategories.add(BloomreachConsentCategory.PERSONALISED_RECOMMENDATIONS.category);
        exportCategories.add(BloomreachConsentCategory.NPS.category);
        return exportCategories;
    }

    /*
     * Important : If we change this method to set new category please could you also change the getBloomreachConsentCategories . Sequence should be maintained
     * .
     * Since bloomreach API doesn't return Category Id in response we need to do this else we will have to call API for every category
     */
    private DistBloomreachConsentData getBloomreachDataFromResponse(final String email, final ExportConsentsResponseData exportResponse) {

        final DistBloomreachConsentData bloomreachConsentData = new DistBloomreachConsentData();
        bloomreachConsentData.setUid(email);
        bloomreachConsentData.setEmailConsent(exportResponse.getResults().get(0).isValue());
        bloomreachConsentData.setContentNewsConsent(exportResponse.getResults().get(1).isValue());
        bloomreachConsentData.setSalesAndClearanceConsent(exportResponse.getResults().get(2).isValue());
        bloomreachConsentData.setNewBrandsConsent(exportResponse.getResults().get(3).isValue());
        bloomreachConsentData.setPersonalisedRecommendationsConsent(exportResponse.getResults().get(4).isValue());
        bloomreachConsentData.setNpsConsent(exportResponse.getResults().get(5).isValue());
        bloomreachConsentData.setPersonalisationConsent(exportResponse.getResults().get(6).isValue());
        bloomreachConsentData.setProfilingConsent(exportResponse.getResults().get(7).isValue());
        bloomreachConsentData.setPaperConsent(exportResponse.getResults().get(8).isValue());
        bloomreachConsentData.setPhoneConsent(exportResponse.getResults().get(9).isValue());
        bloomreachConsentData.setSmsMarketingConsent(exportResponse.getResults().get(10).isValue());
        return bloomreachConsentData;

    }

    private String createBloomreachExportConsentsRequest(final String email, final List<String> exportCategories) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final ExportConsentsRoot exportRoot = new ExportConsentsRoot();
        final SoftCustomerIds bloomreachId = new SoftCustomerIds();
        bloomreachId.setEmail_id(email);
        exportRoot.setCustomer_ids(bloomreachId);
        exportRoot.setAttributes(getCustomerAttributes(exportCategories));
        return objectMapper.writeValueAsString(exportRoot);
    }

    private List<CustomerAttributeData> getCustomerAttributes(final List<String> categories) {
        final List<CustomerAttributeData> attributeList = new ArrayList<>();
        for (final String consentCategory : categories) {
            attributeList.add(createCustomerAttribute(CONSENT_TYPE, consentCategory, VALID_MODE));
        }
        return attributeList;
    }

    private CustomerAttributeData createCustomerAttribute(final String type, final String categoryName, final String mode) {
        final CustomerAttributeData customerAttribute = new CustomerAttributeData();
        customerAttribute.setType(type);
        customerAttribute.setCategory(categoryName);
        customerAttribute.setMode(mode);
        return customerAttribute;
    }

    /**
     * @return the distBloomreachAPIClient
     */
    public DistBloomreachAPIClient getDistBloomreachAPIClient() {
        return distBloomreachAPIClient;
    }

    /**
     * @param distBloomreachAPIClient
     *            the distBloomreachAPIClient to set
     */
    public void setDistBloomreachAPIClient(final DistBloomreachAPIClient distBloomreachAPIClient) {
        this.distBloomreachAPIClient = distBloomreachAPIClient;
    }

    /**
     * @return the distSiteBaseUrlResolutionService
     */
    public DistSiteBaseUrlResolutionService getDistSiteBaseUrlResolutionService() {
        return distSiteBaseUrlResolutionService;
    }

    /**
     * @param distSiteBaseUrlResolutionService
     *            the distSiteBaseUrlResolutionService to set
     */
    public void setDistSiteBaseUrlResolutionService(final DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService) {
        this.distSiteBaseUrlResolutionService = distSiteBaseUrlResolutionService;
    }

    /**
     * @return the baseSiteService
     */
    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    /**
     * @param baseSiteService
     *            the baseSiteService to set
     */
    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    protected DistUserService getUserService() {
        return userService;
    }

}
