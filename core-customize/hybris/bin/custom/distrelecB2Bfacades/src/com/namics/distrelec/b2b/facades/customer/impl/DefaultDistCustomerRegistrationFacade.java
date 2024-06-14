package com.namics.distrelec.b2b.facades.customer.impl;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Punctuation.COMMA;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.RegistrationType;
import com.namics.distrelec.b2b.core.inout.erp.exception.ErpCommunicationException;
import com.namics.distrelec.b2b.facades.bloomreach.DistBloomreachFacade;
import com.namics.distrelec.b2b.facades.customer.DistCustomerRegistrationFacade;
import com.namics.distrelec.b2b.facades.customer.converters.populator.DistB2BRegisterDataForExistingCustomerPopulator;
import com.namics.distrelec.b2b.facades.customer.converters.populator.DistB2BRegisterDataPopulator;
import com.namics.distrelec.b2b.facades.customer.converters.populator.DistRegisterDataForExistingCustomerPopulator;
import com.namics.distrelec.b2b.facades.customer.converters.populator.DistRegisterDataPopulator;
import com.namics.distrelec.b2b.facades.customer.exceptions.ExistingCustomerRegistrationException;
import com.namics.distrelec.b2b.facades.customer.exceptions.RegistrationException;
import com.namics.distrelec.b2b.facades.user.data.DistConsentData;
import com.namics.distrelec.b2b.facades.user.data.DistExistingCustomerRegisterData;
import com.namics.distrelec.b2b.facades.user.data.DistRegisterData;

import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.user.CustomerModel;

public class DefaultDistCustomerRegistrationFacade extends DefaultDistCustomerFacade implements DistCustomerRegistrationFacade {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistCustomerRegistrationFacade.class);

    protected static final String EXISTING = "EXISTING";

    protected static final String B2B = "B2B";

    protected static final String B2C = "B2C";

    private static final String SUCCESS_MESSAGE_REGISTRATION = "SUCCESS REGISTRATION CUSTOMER : ";

    private static final String ACCOUNT_EXISTS_ERROR_MESSAGE = "registration.error.account.exists.title";

    private static final String EXCEPTION_DUPLICATED_UID = "Duplicated UID of customer : ";

    private static final String EXCEPTION_EXISTING_CUSTOMER = "Customer is existing : ";

    private static final String EXCEPTION_HANDLE_NEWSLETTER_REGIST = "Error in handleNewsletterRegistration for UserID : ";

    private static final String EXCEPTION_ERP_COMMUNICATION = "Registration failed because of an ERP communication error for the customer with Email : {} and messageCode: {}";

    private static final String EXCEPTION = "Register customer failed for : ";

    @Autowired
    private DistRegisterDataPopulator distRegisterDataPopulator;

    @Autowired
    private DistB2BRegisterDataPopulator distB2BRegisterDataPopulator;

    @Autowired
    private DistRegisterDataForExistingCustomerPopulator distRegisterDataForExistingCustomerPopulator;

    @Autowired
    private DistB2BRegisterDataForExistingCustomerPopulator distB2BRegisterDataForExistingCustomerPopulator;

    @Autowired
    private DistBloomreachFacade distBloomreachFacade;

    @Override
    public void register(final RegisterData registerData, final CustomerType customerType) throws Exception {
        checkAddress(registerData);
        if (CustomerType.B2B.equals(customerType)) {
            registerB2BCustomer(registerData);
        } else {
            registerB2CCustomer(registerData);
        }
    }

    @Override
    public void activate(RegisterData registerData, CustomerType customerType) throws Exception {
        getCommonI18NService().setCurrentLanguage(getCommonI18NService().getLanguage("de"));
        final DistExistingCustomerRegisterData data = new DistExistingCustomerRegisterData();
        if (CustomerType.B2B.equals(customerType)) {
            distB2BRegisterDataForExistingCustomerPopulator.populate(registerData, data);
        } else {
            distRegisterDataForExistingCustomerPopulator.populate(registerData, data);
        }
        activateAccount(data, customerType);
    }

    private void activateAccount(final DistExistingCustomerRegisterData data, final CustomerType customerType) throws Exception {
        try {
            activateCustomer(data, customerType);
        } catch (final DuplicateUidException e) {
            logDuplicateUidException(data, e);
        } catch (final ExistingCustomerRegistrationException e) {
            logExistingCustomerRegistrationException(data, e);
        } catch (final ErpCommunicationException e) {
            logErpCommunicationException(data.getEmail(), e);
        } catch (final Exception e) {
            logException(e, data.getEmail());
        }
    }

    private void checkAddress(final RegisterData registerData) {
        if (registerData.getPostalCode() == null) {
            registerData.setPostalCode("");
        }
        if (registerData.getStreetName() == null) {
            registerData.setStreetName("");
        }
        if (registerData.getTown() == null) {
            registerData.setTown("");
        }
    }

    private void registerB2BCustomer(final RegisterData registerData) throws Exception {
        setLegalMailIfIsRequired(registerData);
        if (BooleanUtils.isTrue(registerData.getExistingCustomer())) {
            processExistingB2BRegisterUserRequestAsync(registerData, getRegistrationType(registerData.getRegistrationType()));
        } else {
            processB2BRegisterUserRequestAsync(registerData, getRegistrationType(registerData.getRegistrationType()));
        }
    }

    private void registerB2CCustomer(final RegisterData registerData) throws Exception {
        setLegalMailIfIsRequired(registerData);
        processB2CRegisterUserRequestAsync(registerData, getRegistrationType(registerData.getRegistrationType()));
    }

    private void setLegalMailIfIsRequired(final RegisterData registerData) {
        if (registerData.getLegalEmail() != null && registerData.getLegalEmail().equals(COMMA)) {
            registerData.setLegalEmail(StringUtils.EMPTY);
        }
    }

    private void processExistingB2BRegisterUserRequestAsync(final RegisterData registerData, final RegistrationType registrationType) throws Exception {
        final DistExistingCustomerRegisterData data = new DistExistingCustomerRegisterData();
        distB2BRegisterDataForExistingCustomerPopulator.populate(registerData, data);
        try {
            data.setRegistrationType(registrationType);
            registerExistingCustomer(data, Boolean.TRUE, CustomerType.B2B);
            LOG.info(SUCCESS_MESSAGE_REGISTRATION + data.getEmail());
        } catch (final DuplicateUidException e) {
            logDuplicateUidException(data, e);
        } catch (final ExistingCustomerRegistrationException e) {
            logExistingCustomerRegistrationException(data, e);
        } catch (final ErpCommunicationException e) {
            logErpCommunicationException(data.getEmail(), e);
        } catch (final Exception e) {
            logException(e, data.getEmail());
        }
        registerData.setUid(registerData.getLogin());
        handleExistingCustomerNewsletterRegistration(registerData, data);
    }

    private void processB2CRegisterUserRequestAsync(final RegisterData registerData, final RegistrationType registrationType) throws Exception {
        final DistRegisterData data = new DistRegisterData();
        distRegisterDataPopulator.populate(registerData, data);
        data.setVatId(StringUtils.upperCase(registerData.getCodiceFiscale()));
        data.setRegistrationType(registrationType);
        try {
            registerNewCustomer(data, CustomerType.B2C);
            LOG.info(SUCCESS_MESSAGE_REGISTRATION + data.getEmail());
        } catch (final DuplicateUidException e) {
            logDuplicateUidException(data, e);
        } catch (final ErpCommunicationException e) {
            logErpCommunicationException(data.getEmail(), e);
        } catch (final Exception e) {
            logException(e, data.getEmail());
        }
        handleNewsletterRegistration(registerData, data);
    }

    private void processB2BRegisterUserRequestAsync(final RegisterData registerData, final RegistrationType registrationType) throws Exception {
        final DistRegisterData registrationData = new DistRegisterData();
        distB2BRegisterDataPopulator.populate(registerData, registrationData);
        try {
            registrationData.setRegistrationType(registrationType);
            registerNewCustomer(registrationData, CustomerType.B2B);
            LOG.info(SUCCESS_MESSAGE_REGISTRATION + registrationData.getEmail());
        } catch (final DuplicateUidException e) {
            logDuplicateUidException(registrationData, e);
        } catch (final ErpCommunicationException e) {
            logErpCommunicationException(registrationData.getEmail(), e);
        } catch (final Exception e) {
            logException(e, registrationData.getEmail());
        }
        handleNewsletterRegistration(registerData, registrationData);
    }

    private RegistrationType getRegistrationType(final String registrationType) {
        return (StringUtils.equalsIgnoreCase("checkout", registrationType) ? RegistrationType.CHECKOUT : RegistrationType.STANDALONE);
    }

    /**
     * @param form
     */
    private void handleExistingCustomerNewsletterRegistration(final RegisterData form, final DistExistingCustomerRegisterData registrationData) {
        final DistConsentData consentData = new DistConsentData();
        consentData.setUid(form.getUid());
        consentData.setFirstName(form.getFirstName());
        consentData.setLastName(form.getLastName());
        consentData.setTitleCode(form.getTitleCode());
        consentData.setErpContactId(registrationData.getErpContactId());
        consentData.setRole(form.getFunctionCode());
        consentData.setPhoneNumber(registrationData.getPhoneNumber());
        consentData.setMobileNumber(registrationData.getMobileNumber());
        consentData.setPhonePermission(registrationData.isPhoneConsent());
        consentData.setSmsPermissions(registrationData.isSmsConsent());
        consentData.setPaperPermission(registrationData.isPostConsent());
        consentData.setPersonalisationSubscription(registrationData.isPersonalisationConsent());
        consentData.setProfilingSubscription(registrationData.isProfilingConsent());
        consentData.setCountryCode(form.getCountryCode());
        handleNewsletterRegistration(consentData, form.getMarketingConsent());
    }

    /**
     * @param form
     */
    private void handleNewsletterRegistration(final RegisterData form, final DistRegisterData registrationData) {
        final DistConsentData distConsentData = new DistConsentData();
        distConsentData.setUid(form.getLogin());
        distConsentData.setFirstName(form.getFirstName());
        distConsentData.setLastName(form.getLastName());
        distConsentData.setTitleCode(form.getTitleCode());
        distConsentData.setErpContactId(registrationData.getErpContactId());
        distConsentData.setRole(registrationData.getFunctionCode());
        distConsentData.setPhoneNumber(registrationData.getPhoneNumber());
        distConsentData.setMobileNumber(registrationData.getMobileNumber());
        distConsentData.setPhonePermission(registrationData.isPhoneConsent());
        distConsentData.setSmsPermissions(registrationData.isSmsConsent());
        distConsentData.setPaperPermission(registrationData.isPostConsent());
        distConsentData.setPersonalisationSubscription(registrationData.isPersonalisationConsent());
        distConsentData.setProfilingSubscription(registrationData.isProfilingConsent());
        distConsentData.setCountryCode(registrationData.getCountryCode());
        handleNewsletterRegistration(distConsentData, form.getMarketingConsent());
    }

    private void handleNewsletterRegistration(final DistConsentData consentData, final boolean isNewsletterOption) {
        try {
            if (getUserService().getCurrentUser() instanceof CustomerModel) {
                consentData.setActiveSubscription(isNewsletterOption);
                consentData.setNpsSubscription(isNewsletterOption);
                consentData.setIsAnonymousUser(Boolean.FALSE);
                consentData.setIsRegistration(true);
                final String registrationRequest = distBloomreachFacade.createBloomreachRegistrationRequest(consentData);
                distBloomreachFacade.sendBatchRequestToBloomreach(registrationRequest);
            }
        } catch (final Exception e) {
            LOG.error(EXCEPTION_HANDLE_NEWSLETTER_REGIST + consentData.getUid(), e, DistConstants.ErrorLogCode.REGISTRATION_ERROR, consentData.getUid());
        }
    }

    private void logException(final Exception e, final String email) throws Exception {
        LOG.error("Registration failed for customer with email: {}", email, e);
        throw new Exception(EXCEPTION + email);
    }

    private void logDuplicateUidException(final DistExistingCustomerRegisterData data, final DuplicateUidException e) throws DuplicateUidException {
        String errorMessageKey;
        if (e.getMessage() != null && e.getMessage().contains("Registration for an existing")) {
            errorMessageKey = "registration.error.existing.contact.hybris";
        } else {
            errorMessageKey = ACCOUNT_EXISTS_ERROR_MESSAGE;
        }
        LOG.error(EXCEPTION_DUPLICATED_UID + data.getEmail() + "\n errorMessageKey: " + errorMessageKey, e, DistConstants.ErrorLogCode.REGISTRATION_ERROR,
                  DistConstants.ErrorSource.MODEL_SAVING_EXCEPTION_HYBRIS, data.getEmail());
        throw new RegistrationException(errorMessageKey);
    }

    private void logExistingCustomerRegistrationException(final DistExistingCustomerRegisterData data,
                                                          final ExistingCustomerRegistrationException e) throws ExistingCustomerRegistrationException {
        String errorMessageKey;
        if (e.getReason() != null) {
            errorMessageKey = e.getReason().getMessageKey() != null ? e.getReason().getMessageKey() : e.getReason().getValue();
        } else {
            errorMessageKey = "form.global.error";
        }
        LOG.error(EXCEPTION_EXISTING_CUSTOMER + data.getEmail() + "\n errorMessageKey: " + errorMessageKey, e,
                  DistConstants.ErrorLogCode.REGISTRATION_ERROR, DistConstants.ErrorSource.MODEL_SAVING_EXCEPTION_HYBRIS, data.getEmail());
        throw new RegistrationException(errorMessageKey);
    }

    private void logErpCommunicationException(final String email, final ErpCommunicationException e) {
        String messageCode = null;
        if (e.getCause() instanceof final P1FaultMessage fault) {
            messageCode = StringUtils.isNotBlank(fault.getFaultInfo().getFaultText()) ? fault.getFaultInfo().getFaultText()
                                                                                      : fault.getFaultInfo().getFaultName();
        }
        if (StringUtils.isNotEmpty(messageCode) && messageCode.endsWith("Tax code 1 is not valid")) {
            messageCode = "registration.vatId.validationMessage";
        }
        LOG.error(EXCEPTION_ERP_COMMUNICATION, email, messageCode, e);
        throw new RegistrationException(messageCode, e);
    }

    private void logDuplicateUidException(final DistRegisterData registrationData, final DuplicateUidException e) throws DuplicateUidException {
        LOG.error(EXCEPTION_DUPLICATED_UID + registrationData.getEmail() + "\n message: " + e.getMessage(), e, DistConstants.ErrorLogCode.REGISTRATION_ERROR,
                  DistConstants.ErrorSource.MODEL_SAVING_EXCEPTION_HYBRIS, registrationData.getEmail());
        throw new RegistrationException(ACCOUNT_EXISTS_ERROR_MESSAGE);
    }
}
