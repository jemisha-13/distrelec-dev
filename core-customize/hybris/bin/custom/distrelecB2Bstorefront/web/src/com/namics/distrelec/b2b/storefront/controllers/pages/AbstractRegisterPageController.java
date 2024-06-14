/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.enums.RegistrationType;
import com.namics.distrelec.b2b.core.inout.erp.exception.ErpCommunicationException;
import com.namics.distrelec.b2b.core.service.newsletter.exception.InvalidTokenException;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.RegistrationInfo;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.User;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.customer.exceptions.ExistingCustomerRegistrationException;
import com.namics.distrelec.b2b.facades.newsletter.DistNewsletterFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.storesession.data.ChannelData;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.b2b.facades.user.data.DistExistingCustomerRegisterData;
import com.namics.distrelec.b2b.facades.user.data.DistRegisterData;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.forms.*;
import com.namics.distrelec.b2b.storefront.security.GUIDCookieStrategy;
import com.namics.distrelec.b2b.storefront.security.impl.DefaultAutoLoginStrategy;
import com.namics.distrelec.b2b.storefront.util.CaptchaUtilImpl;
import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.c2l.CountryModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Abstract Controller to handle registration.
 *
 * @author rmeier, Namics AG
 * @since Namics Extensions 1.0
 */
public abstract class AbstractRegisterPageController extends AbstractPageController {

    protected static final Logger LOG = LogManager.getLogger(AbstractRegisterPageController.class);

    private static final String ACCOUNT_REGISTER_SUCCESS_PAGE = "accountRegisterSuccessPage";
    public static final String CHECKOUT_ADDRESS_PAGE = "/cart/checkout";
    public static final String EXISTING_REGISTRATION_SUCCESS = "/registration/existingsuccess?userType=B2C&existing=true";
    public static final String BIZ_COUNTRY_CODE = "EX";

    protected static final String REGISTERED_USER_EMAIL = "REGISTERED_USER_EMAIL";
    protected static final String APPROVAL_BY_CUSTOMER_SERVICE_NEEDED = "APPROVAL_BY_CUSTOMER_SERVICE_NEEDED";

    private static final List<String> MANDATORY_COUNTRIES_VAT_ID = List.of("BE", "CZ", "FI", "HU", "IT", "LT", "LV", "NL", "PL", "RO", "SK", "SM");

    private static final String SPECIAL_CHARACTERS = "[-()/.^:,]";

    protected abstract AbstractPageModel getCmsPage() throws CMSItemNotFoundException;

    protected abstract String getView();

    protected static final PhoneNumberUtil PHONENUMBERUTIL = PhoneNumberUtil.getInstance();

    public static String phoneNumberToString(final PhoneNumber phoneNumber) {
        return PHONENUMBERUTIL.format(phoneNumber, PhoneNumberFormat.INTERNATIONAL);
    }

    @Autowired
    private DistUserFacade userFacade;

    @Autowired
    private DistNewsletterFacade newsletterFacade;

    @Autowired
    private GUIDCookieStrategy guidCookieStrategy;

    @Autowired
    @Qualifier("b2bCheckoutFacade")
    private DistCheckoutFacade checkoutFacade;

    @Autowired
    @Qualifier("httpSessionRequestCache")
    private HttpSessionRequestCache httpSessionRequestCache;

    @Autowired
    @Qualifier("autoLoginStrategy")
    DefaultAutoLoginStrategy autoLoginStrategy;

    @ModelAttribute("countries")
    public List<CountryData> getCountries() {
        return getCheckoutFacade().getDeliveryCountriesAndRegions();
    }

    @ModelAttribute("allChannels")
    public Collection<ChannelData> getAllChannels() {
        return getStoreSessionFacade().getAllChannels();
    }

    @ModelAttribute("currencies")
    public Collection<CurrencyData> getRegistrationCurrencies() {
        return getStoreSessionFacade().getAllCurrencies();
    }

    @ModelAttribute("titles")
    public Collection<TitleData> getTitles() {
        return getUserFacade().getTitles();
    }

    @ModelAttribute("skipCaptcha")
    public boolean getSkipCaptchaValidation() {
        return getConfigurationService().getConfiguration().getBoolean(CaptchaUtilImpl.SKIP_CAPTCHA_VALIDATION);
    }

    protected String getCheckoutSuccessRedirect(boolean isSuccessMessageShown, HttpServletRequest request) {
        request.getSession().setAttribute(WebConstants.CHECKOUT_REGISTER_SUCCESS, Boolean.TRUE);
        if (isSuccessMessageShown) {
            return CHECKOUT_ADDRESS_PAGE + DistConstants.Punctuation.QUESTION_MARK + DistConstants.Checkout.MESSAGE_PARAMETER
                    + "=account.confirmation.signin.title";
        }
        return CHECKOUT_ADDRESS_PAGE;
    }

    protected String getDefaultRegistrationPage(final Model model) throws CMSItemNotFoundException {
        storeCmsPageInModel(model, getCmsPage());
        setUpMetaDataForContentPage(model, (ContentPageModel) getCmsPage());
        final Breadcrumb loginBreadcrumbEntry = new Breadcrumb("#",
                getMessageSource().getMessage("header.link.register.select", null, getI18nService().getCurrentLocale()), null);
        model.addAttribute("breadcrumbs", Collections.singletonList(loginBreadcrumbEntry));
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePrimaryPageCategoryAndPageType(digitalDatalayer,
                DigitalDatalayerConstants.PageCategory.REGISTRATION + "|" + getCmsPage().getTitle(Locale.ENGLISH),
                DigitalDatalayerConstants.PageType.LOGINREGISTRATIONPAGE);
        return getView();
    }

    protected String getRegistrationSuccessPage(final Model model) throws CMSItemNotFoundException {
        storeCmsPageInModel(model, getCmsPage());
        setUpMetaDataForContentPage(model, (ContentPageModel) getCmsPage());
        final Breadcrumb loginBreadcrumbEntry = new Breadcrumb("#",
                getMessageSource().getMessage("header.link.register.success", null, getI18nService().getCurrentLocale()), null);
        final ContentPageModel contentPage = getContentPageForLabelOrId(ACCOUNT_REGISTER_SUCCESS_PAGE);
        storeCmsPageInModel(model, contentPage);

        model.addAttribute("breadcrumbs", Collections.singletonList(loginBreadcrumbEntry));
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);

        final List<User> user = digitalDatalayer.getUser();
        if (CollectionUtils.isNotEmpty(user) && user.get(0).getRegistration() != null) {
            user.get(0).getRegistration().setStage("complete");
        }

        getDistDigitalDatalayerFacade().populatePrimaryPageCategoryAndPageType(digitalDatalayer,
                DigitalDatalayerConstants.PageCategory.REGISTRATION + "|" + getCmsPage().getTitle(Locale.ENGLISH),
                DigitalDatalayerConstants.PageType.LOGINREGISTRATIONPAGE);
        return getSuccessView();
    }

    protected String getSuccessView() {
        return ControllerConstants.Views.Pages.Account.AccountRegisterSuccessPage;
    }

    protected String getSuccessRedirect(final Model model, final HttpServletRequest request, final HttpServletResponse response,
                                        final RedirectAttributes redirectAttr) {
        if (getHttpSessionRequestCache().getRequest(request, response) != null) {
            return getHttpSessionRequestCache().getRequest(request, response).getRedirectUrl();
        }
        getSessionService().setAttribute(WebConstants.LOGIN_SUCCESS, Boolean.TRUE);
        return "/welcome";
    }

    /**
     * @return the new registration redirect URL.
     */
    protected String redirectToregister() {
        return addFasterizeCacheControlParameter(UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/registration");
    }

    /**
     * This method takes data from the registration form and create a new customer account and attempts to log in using the credentials of
     * this new user.
     *
     * @param form
     * @param bindingResult
     * @param model
     * @param request
     * @param response
     * @return true if there are no binding errors or the account does not already exists.
     * @throws CMSItemNotFoundException
     * @throws NumberParseException
     */
    protected String processB2CRegisterUserRequest(final RegisterForm form, final BindingResult bindingResult, final Model model,
                                                   final HttpServletRequest request, final HttpServletResponse response, final RegistrationType registrationType,
                                                   final RedirectAttributes redirectAttr) throws CMSItemNotFoundException, NumberParseException {

        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        final RegistrationInfo registrationInfo = new RegistrationInfo();
        registrationInfo.setRegType(B2C_LOWER);
        registrationInfo.setRegDate(new Date());
        model.addAttribute("registrationType", registrationType.toString());
        if ("IT".equals(getStoreSessionFacade().getCurrentCountry().getIsocode()) && StringUtils.isBlank(form.getCodiceFiscale())) {
            bindingResult.addError(new FieldError("registerForm", "codiceFiscale", null, false, new String[]{"register.codiceFiscale.invalid"}, null, null));
        }

        if (!bindingResult.hasErrors()) {
            if (!getCaptchaUtil().validateReCaptcha(request)) {
                model.addAttribute("captchaError", Boolean.TRUE);
                GlobalMessages.addErrorMessage(model, "form.captcha.error");
            }
        }

        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            return handleRegistrationError(model);
        }
        if (!StringUtils.isBlank(form.getPhoneNumber())) {
            if (BIZ_COUNTRY_CODE.equalsIgnoreCase(getCurrentCountry().getIsocode())) {
                final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(form.getPhoneNumber(), form.getCountryCode());
                form.setPhoneNumber(phoneNumberToString(phoneNumber));
            } else {
                final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(form.getPhoneNumber(), getCurrentCountry().getIsocode());
                form.setPhoneNumber(phoneNumberToString(phoneNumber));
            }

        }
        if (!StringUtils.isBlank(form.getMobileNumber())) {
            if (BIZ_COUNTRY_CODE.equalsIgnoreCase(getCurrentCountry().getIsocode())) {
                final PhoneNumber mobileNumber = PHONENUMBERUTIL.parse(form.getMobileNumber(), form.getCountryCode());
                form.setMobileNumber(phoneNumberToString(mobileNumber));
            } else {
                final PhoneNumber mobileNumber = PHONENUMBERUTIL.parse(form.getMobileNumber(), getCurrentCountry().getIsocode());
                form.setMobileNumber(phoneNumberToString(mobileNumber));
            }
        }
        final DistRegisterData data = createGeneralRegisterData(form);

        data.setVatId(form.getCodiceFiscale() != null ? form.getCodiceFiscale().toUpperCase() : null);
        data.setRegistrationType(registrationType);
        try {
            getCustomerFacade().registerNewCustomer(data, CustomerType.B2C);
        } catch (final DuplicateUidException e) {
            logError(LOG, "{} {} Registration failed due to Duplicate UID(Email): {}", e, ErrorLogCode.REGISTRATION_ERROR, ErrorSource.DB, data.getEmail());
            bindingResult.rejectValue("email", "registration.error.account.exists.title");
            GlobalMessages.addErrorMessage(model, "form.global.error");
            return handleRegistrationError(model);
        } catch (final ErpCommunicationException e) {
            logError(LOG, "{} {} Registration failed because of an ERP communication error for the customer with Email: {}", e, ErrorLogCode.REGISTRATION_ERROR,
                    ErrorSource.SAP_FAULT, data.getEmail());
            GlobalMessages.addErrorMessage(model, e.getCause() instanceof P1FaultMessage ? ((P1FaultMessage) e.getCause()).getFaultInfo().getFaultText()
                    : "register.form.error.erpcommunication");

            return handleRegistrationError(model);
        } catch (final Exception e) {
            logError(LOG, "{} Registration failed for the customer with Email: {}", e, ErrorLogCode.REGISTRATION_ERROR.getCode(), data.getEmail());
            GlobalMessages.addErrorMessage(model, "form.global.error");
            return handleRegistrationError(model);
        }

        request.getSession().setAttribute(REGISTERED_USER_EMAIL, form.getEmail());
        registrationInfo.setRegMsg(SUCCESS);
        getDistDigitalDatalayerFacade().populateRegistrationType(digitalDatalayer, registrationInfo);
        getDistDigitalDatalayerFacade().populatePrimaryPageCategoryAndPageType(digitalDatalayer,
                DigitalDatalayerConstants.PageCategory.REGISTRATION + "|" + getCmsPage().getTitle(Locale.ENGLISH),
                DigitalDatalayerConstants.PageType.LOGINREGISTRATIONPAGE);
        redirectAttr.addFlashAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        redirectAttr.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.signin.title");
        autoLoginStrategy.login(form.getEmail(), form.getPwd(), request, response, registrationType);
        try {
            //setting correct deliverymode after registration
            final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();
            String defaultDeliveryMode = getCheckoutFacade().getDefaultDeliveryMode();
            if ((null != customer && null == customer.getDefaultDeliveryMethod()) || (null != defaultDeliveryMode && !defaultDeliveryMode.isEmpty() && null != customer && null == customer.getDefaultDeliveryMethod() && defaultDeliveryMode.equalsIgnoreCase(customer.getDefaultDeliveryMethod()))) {
                getCheckoutFacade().setDeliveryMode(defaultDeliveryMode);
            }
        } catch (Exception ex) {
            LOG.error("Error while updating cart delivery mode after checkout", ex);
        }
        if (registrationType.equals(RegistrationType.CHECKOUT)) {
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + getCheckoutSuccessRedirect(true,request));
        }
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + getSuccessRedirect(model, request, response, redirectAttr));
    }

    /**
     * This method takes data from the registration form and create a new customer account and attempts to log in using the credentials of
     * this new user.
     *
     * @param form
     * @param bindingResult
     * @param model
     * @param request
     * @param response
     * @return true if there are no binding errors or the account does not already exists.
     * @throws CMSItemNotFoundException
     * @throws NumberParseException
     */
    protected String processB2BRegisterUserRequest(final RegisterB2BForm form, final BindingResult bindingResult, final Model model,
                                                   final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectAttr, final RegistrationType registrationType) throws CMSItemNotFoundException, NumberParseException {
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        final RegistrationInfo registrationInfo = new RegistrationInfo();
        registrationInfo.setRegType(B2B_LOWER);
        registrationInfo.setRegDate(new Date());
        String[] vat4EnableCountries = getConfigurationService().getConfiguration().getString("registration.vat4Countries").split(";");

        if (Arrays.stream(vat4EnableCountries).anyMatch(getStoreSessionFacade().getCurrentCountry().getIsocode()::equals) && (StringUtils.isBlank(form.getLegalEmail()) && StringUtils.isBlank(form.getVat4()))) {
            bindingResult.addError(new FieldError("registerForm", "vat4", null, false, new String[]{"vat.reg.error"}, null, null));
        }

        if (!bindingResult.hasErrors()) {
            if (StringUtils.isBlank(form.getVatId()) && mandatoryVatId()) {
                bindingResult.addError(new FieldError("registerForm", "vatId", null, false, new String[]{"register.vatId.validationMessage"}, null, null));
            } else if (!registrationType.name().equals(RegistrationType.CHECKOUT.name()) && !getCaptchaUtil().validateReCaptcha(request)) {
                model.addAttribute("captchaError", Boolean.TRUE);
                GlobalMessages.addErrorMessage(model, "form.captcha.error");
            }
        }

        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            return handleRegistrationError(model);
        }
        if (!StringUtils.isBlank(form.getPhoneNumber())) {
            if (BIZ_COUNTRY_CODE.equalsIgnoreCase(getCurrentCountry().getIsocode())) {
                final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(form.getPhoneNumber(), form.getCountryCode());
                form.setPhoneNumber(phoneNumberToString(phoneNumber));
            } else {
                final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(form.getPhoneNumber(), getCurrentCountry().getIsocode());
                form.setPhoneNumber(phoneNumberToString(phoneNumber));
            }

        }
        if (!StringUtils.isBlank(form.getMobileNumber())) {
            if (BIZ_COUNTRY_CODE.equalsIgnoreCase(getCurrentCountry().getIsocode())) {
                final PhoneNumber mobileNumber = PHONENUMBERUTIL.parse(form.getMobileNumber(), form.getCountryCode());
                form.setMobileNumber(phoneNumberToString(mobileNumber));
            } else {
                final PhoneNumber mobileNumber = PHONENUMBERUTIL.parse(form.getMobileNumber(), getCurrentCountry().getIsocode());
                form.setMobileNumber(phoneNumberToString(mobileNumber));
            }
        }
        try {
            DistRegisterData registerationData = createB2BRegisterData(form);
            registerationData.setRegistrationType(registrationType);
            getCustomerFacade().registerNewCustomer(registerationData, CustomerType.B2B);
        } catch (final DuplicateUidException e) {
            logError(LOG, "{} {} Registration failed due to Duplicate UID(Email): {}", e, ErrorLogCode.REGISTRATION_ERROR, ErrorSource.DB, form.getEmail());
            bindingResult.rejectValue("email", "registration.error.account.exists.title");
            GlobalMessages.addErrorMessage(model, "form.global.error");
            return handleRegistrationError(model);
        } catch (final ErpCommunicationException e) {
            logError(LOG, "{} {} Registration failed because of an ERP communication error for the customer with Email: {}",
                    e, ErrorLogCode.REGISTRATION_ERROR, ErrorSource.SAP_FAULT, form.getEmail());
            String message = null;
            if (e.getCause() instanceof P1FaultMessage) {
                final P1FaultMessage fault = (P1FaultMessage) e.getCause();
                message = StringUtils.isNotBlank(fault.getFaultInfo().getFaultText()) ? fault.getFaultInfo().getFaultText()
                        : fault.getFaultInfo().getFaultName();
            }

            GlobalMessages.addErrorMessage(model, StringUtils.isNotBlank(message) ? message : "register.form.error.erpcommunication");

            return handleRegistrationError(model);
        } catch (final Exception e) {
            logError(LOG, "{} Registration failed for the customer with Email: {}", e, ErrorLogCode.REGISTRATION_ERROR, form.getEmail());
            GlobalMessages.addErrorMessage(model, "form.global.error");
            return handleRegistrationError(model);
        }
        registrationInfo.setRegMsg(SUCCESS);
        getDistDigitalDatalayerFacade().populateRegistrationType(digitalDatalayer, registrationInfo);
        getDistDigitalDatalayerFacade().populatePrimaryPageCategoryAndPageType(digitalDatalayer,
                DigitalDatalayerConstants.PageCategory.REGISTRATION + "|" + getCmsPage().getTitle(Locale.ENGLISH),
                DigitalDatalayerConstants.PageType.LOGINREGISTRATIONPAGE);
        redirectAttr.addFlashAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        redirectAttr.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.signin.title");
        autoLoginStrategy.login(form.getEmail(), form.getPwd(), request, response, RegistrationType.STANDALONE);
        if (registrationType.equals(RegistrationType.CHECKOUT)) {
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + getCheckoutSuccessRedirect(true, request));
        }
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + getSuccessRedirect(model, request, response, redirectAttr));
    }

    protected boolean mandatoryVatId() {
        return MANDATORY_COUNTRIES_VAT_ID.contains(getStoreSessionFacade().getCurrentCountry().getIsocode()) || "7801".equals(getCurrentSalesOrg().getCode());
    }

    /**
     * Anonymous checkout process.
     * <p>
     * Creates a new guest customer and updates the session cart with this user. The session user will be anonymous and it's never updated
     * with this guest user.
     * <p>
     * If email is required, grab the email from the form and set it as uid with "guid|email" format.
     *
     * @throws de.hybris.platform.cms2.exceptions.CMSItemNotFoundException
     * @throws NumberParseException
     */
    protected String processAnonymousCheckoutUserRequest(final GuestForm form, final BindingResult bindingResult, final Model model,
                                                         final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectAttr) throws CMSItemNotFoundException, NumberParseException {
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("guestForm", form);
                GlobalMessages.addErrorMessage(model, "form.global.error");
                return handleRegistrationError(model);
            }//todo
            // else if (!getCaptchaUtil().validateReCaptcha(request)) {
//                model.addAttribute("guestForm", form);
//                GlobalMessages.addErrorMessage(model, "form.captcha.error");
//                return handleRegistrationError(model);
//            }

            final DistRegisterData data = new DistRegisterData();
            data.setLogin(form.getEmail());
            data.setEmail(form.getEmail());
            final CurrencyData currency = getCurrentCurrency();
            data.setCurrencyCode(currency.getIsocode() == null ? "EUR" : currency.getIsocode());

            getCustomerFacade().createGuestUserForAnonymousCheckout(getMessageSource().getMessage("text.guest.customer", null, getI18nService().getCurrentLocale()), data);
            getGuidCookieStrategy().setCookie(request, response);
            getSessionService().setAttribute(WebConstants.ANONYMOUS_CHECKOUT, Boolean.TRUE);

        } catch (final DuplicateUidException e) {
            logError(LOG, "{} {} Registration failed because of an ERP communication error for the customer with Email: {}\nDuplicateUidException: ", e,
                    ErrorLogCode.REGISTRATION_ERROR, ErrorSource.MODEL_SAVING_EXCEPTION_HYBRIS, form.getEmail());
            GlobalMessages.addErrorMessage(model, "form.global.error");
            return handleRegistrationError(model);
        } catch (final InvalidTokenException ite) {
            LOG.info(ErrorLogCode.REGISTRATION_ERROR.getCode() + "Email [" + form.getEmail() + "] already subcribed for newsletter.");
        }

        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + getSuccessRedirect(model, request, response, redirectAttr));
    }

    /**
     * Simulate guest checkout with new registered user if registration is done during checkout.
     *
     * @param customerId
     * @param request
     * @param response
     */
    protected void simulateGuestCheckoutForRegisteredCustomer(final String customerId, final HttpServletRequest request, final HttpServletResponse response) {
        getGuidCookieStrategy().setCookie(request, response);
        getSessionService().setAttribute(WebConstants.ANONYMOUS_CHECKOUT, Boolean.TRUE);
        final CustomerData customerData = getCustomerFacade().getCustomerDataForUid(customerId);
        getCustomerFacade().updateCartWithGuestForAnonymousCheckout(customerData);
    }

    /**
     * @param referer
     * @param form
     * @param bindingResult
     * @param model
     * @param request
     * @param response
     * @return
     * @throws CMSItemNotFoundException
     * @throws NumberParseException
     */
    protected String processExistingB2CRegisterUserRequest(final String referer, final RegisterForm form, final BindingResult bindingResult,
                                                           final Model model, final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectAttr,
                                                           final RegistrationType registrationType) throws CMSItemNotFoundException, NumberParseException {
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        final RegistrationInfo registrationInfo = new RegistrationInfo();
        registrationInfo.setRegType(EXISTING + B2C);
        registrationInfo.setRegDate(new Date());
        model.addAttribute("registrationType", registrationType.toString());
        model.addAttribute("isExistingUser", true);
        model.addAttribute("existingCustomerId", form.getCustomerId());
        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            return handleExistingRegistrationError(model);
        } else if (!getCaptchaUtil().validateReCaptcha(request)) {
            model.addAttribute("captchaError", Boolean.TRUE);
            GlobalMessages.addErrorMessage(model, "form.captcha.error");
            return handleExistingRegistrationError(model);
        }

        if (!StringUtils.isBlank(form.getPhoneNumber())) {
            if (BIZ_COUNTRY_CODE.equalsIgnoreCase(getCurrentCountry().getIsocode())) {
                final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(form.getPhoneNumber(), form.getCountryCode());
                form.setPhoneNumber(phoneNumberToString(phoneNumber));
            } else {
                final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(form.getPhoneNumber(), getCurrentCountry().getIsocode());
                form.setPhoneNumber(phoneNumberToString(phoneNumber));
            }

        }
        if (!StringUtils.isBlank(form.getMobileNumber())) {
            if (BIZ_COUNTRY_CODE.equalsIgnoreCase(getCurrentCountry().getIsocode())) {
                final PhoneNumber mobileNumber = PHONENUMBERUTIL.parse(form.getMobileNumber(), form.getCountryCode());
                form.setMobileNumber(phoneNumberToString(mobileNumber));
            } else {
                final PhoneNumber mobileNumber = PHONENUMBERUTIL.parse(form.getMobileNumber(), getCurrentCountry().getIsocode());
                form.setMobileNumber(phoneNumberToString(mobileNumber));
            }
        }

        final DistExistingCustomerRegisterData data = createGeneralRegisterDataForExistingCustomer(form);
        data.setRegistrationType(registrationType);
        try {
            getCustomerFacade().registerExistingCustomer(data, false, CustomerType.B2C);
        } catch (final DuplicateUidException e) {
            logError(LOG, "{} {} Registration failed due to Duplicate UID(Email): {}", e, ErrorLogCode.REGISTRATION_ERROR,
                    ErrorSource.MODEL_SAVING_EXCEPTION_HYBRIS, data.getEmail());
            bindingResult.rejectValue("email", "registration.error.account.exists.title");
            GlobalMessages.addErrorMessage(model, "registration.error.existing.account.already.present");
            return handleExistingRegistrationError(model);
        } catch (final ExistingCustomerRegistrationException e) {
            logError(LOG, "{} {} Registration failed due to Existing UID(Email): {}\nExistingCustomerRegistrationException: ", e,
                    ErrorLogCode.REGISTRATION_ERROR, ErrorSource.MODEL_SAVING_EXCEPTION_HYBRIS, data.getEmail());
            GlobalMessages.addErrorMessage(model, e.getReason().getMessageKey());
            return handleExistingRegistrationError(model);
        } catch (final ErpCommunicationException e) {
            logError(LOG, "{} {} Registration failed because of an ERP communication error for the customer with Email: {}", e, ErrorLogCode.REGISTRATION_ERROR,
                    ErrorSource.SAP_FAULT, data.getEmail());
            GlobalMessages.addErrorMessage(model, "register.form.error.erpcommunication");
            return handleExistingRegistrationError(model);
        } catch (final Exception e) {
            logError(LOG, "{} Registration failed for the customer with Email: {}", e, ErrorLogCode.REGISTRATION_ERROR, data.getEmail());
            GlobalMessages.addErrorMessage(model, "form.global.error");
            return handleExistingRegistrationError(model);
        }

        registrationInfo.setRegMsg(SUCCESS);
        getDistDigitalDatalayerFacade().populateRegistrationType(digitalDatalayer, registrationInfo);
        getDistDigitalDatalayerFacade().populatePrimaryPageCategoryAndPageType(digitalDatalayer,
                DigitalDatalayerConstants.PageCategory.REGISTRATION + "|" + getCmsPage().getTitle(Locale.ENGLISH),
                DigitalDatalayerConstants.PageType.LOGINREGISTRATIONPAGE);
        redirectAttr.addFlashAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        request.getSession().setAttribute(REGISTERED_USER_EMAIL, form.getEmail());
        request.getSession().setAttribute(APPROVAL_BY_CUSTOMER_SERVICE_NEEDED, Boolean.valueOf(StringUtils.isEmpty(data.getContactId())));
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + EXISTING_REGISTRATION_SUCCESS);
    }

    /**
     * @param referer
     * @param form
     * @param bindingResult
     * @param model
     * @param request
     * @param response
     * @return
     * @throws CMSItemNotFoundException
     * @throws NumberParseException
     */
    protected String processExistingB2BRegisterUserRequest(final String referer, final RegisterB2BForm form, final BindingResult bindingResult,
                                                           final Model model, final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectAttr, final RegistrationType registrationType)
            throws CMSItemNotFoundException, NumberParseException {
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        final RegistrationInfo registrationInfo = new RegistrationInfo();
        model.addAttribute("isExistingUser", true);
        registrationInfo.setRegType(EXISTING + B2B);
        registrationInfo.setRegDate(new Date());
        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            return handleExistingRegistrationError(model);
        } else if (!getCaptchaUtil().validateReCaptcha(request)) {
            model.addAttribute("captchaError", Boolean.TRUE);
            GlobalMessages.addErrorMessage(model, "form.captcha.error");
            return handleExistingRegistrationError(model);
        }

        if (!StringUtils.isBlank(form.getPhoneNumber())) {
            if (BIZ_COUNTRY_CODE.equalsIgnoreCase(getCurrentCountry().getIsocode())) {
                final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(form.getPhoneNumber(), form.getCountryCode());
                form.setPhoneNumber(phoneNumberToString(phoneNumber));
            } else {
                final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(form.getPhoneNumber(), getCurrentCountry().getIsocode());
                form.setPhoneNumber(phoneNumberToString(phoneNumber));
            }
        }

        if (!StringUtils.isBlank(form.getMobileNumber())) {
            if (BIZ_COUNTRY_CODE.equalsIgnoreCase(getCurrentCountry().getIsocode())) {
                final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(form.getMobileNumber(), form.getCountryCode());
                form.setMobileNumber(phoneNumberToString(phoneNumber));
            } else {
                final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(form.getMobileNumber(), getCurrentCountry().getIsocode());
                form.setMobileNumber(phoneNumberToString(phoneNumber));
            }
        }

        final DistExistingCustomerRegisterData data = createB2BRegisterDataForExistingCustomer(form);
        data.setRegistrationType(registrationType);
        final boolean updateCurrentUserOnly = true;
        try {
            getCustomerFacade().registerExistingCustomer(data, updateCurrentUserOnly, CustomerType.B2B);
        } catch (final DuplicateUidException e) {
            logError(LOG, "{} {} Registration failed due to Duplicate UID(Email): {}\nDuplicateUidException: ", e, ErrorLogCode.REGISTRATION_ERROR,
                    ErrorSource.MODEL_SAVING_EXCEPTION_HYBRIS, data.getEmail());
            if (e.getMessage() != null && e.getMessage().contains("Registration for an existing")) {
                GlobalMessages.addErrorMessage(model, "registration.error.existing.contact.hybris");
            } else {
                bindingResult.rejectValue("email", "registration.error.account.exists.title");
                GlobalMessages.addErrorMessage(model, "form.global.error");
            }
            return handleExistingRegistrationError(model);
        } catch (final ExistingCustomerRegistrationException e) {
            logError(LOG, "{} {} Registration failed due to Existing UID(Email): {} " + data.getEmail() + "\nExistingCustomerRegistrationException: ", e,
                    ErrorLogCode.REGISTRATION_ERROR, ErrorSource.MODEL_SAVING_EXCEPTION_HYBRIS, data.getEmail());
            if (e.getReason() != null) {
                GlobalMessages.addErrorMessage(model, e.getReason().getMessageKey() != null ? e.getReason().getMessageKey() : e.getReason().getValue());
            } else {
                GlobalMessages.addErrorMessage(model, "form.global.error");
            }

            return handleExistingRegistrationError(model);
        } catch (final ErpCommunicationException e) {
            logError(LOG, "{} {} Registration failed because of an ERP communication error for the customer with Email: {}\nErpCommunicationException: ", e,
                    ErrorLogCode.REGISTRATION_ERROR, ErrorSource.SAP_FAULT, data.getEmail());
            GlobalMessages.addErrorMessage(model, "register.form.error.erpcommunication");
            return handleExistingRegistrationError(model);
        } catch (final Exception e) {
            logError(LOG, "{} {} Registration failed for the customer with Email: {} Exception: ", e, ErrorLogCode.REGISTRATION_ERROR, data.getEmail());
            GlobalMessages.addErrorMessage(model, e.getMessage());
            return handleExistingRegistrationError(model);
        }
        registrationInfo.setRegMsg(SUCCESS);
        getDistDigitalDatalayerFacade().populateRegistrationType(digitalDatalayer, registrationInfo);
        getDistDigitalDatalayerFacade().populatePrimaryPageCategoryAndPageType(digitalDatalayer,
                DigitalDatalayerConstants.PageCategory.REGISTRATION + "|" + getCmsPage().getTitle(Locale.ENGLISH),
                DigitalDatalayerConstants.PageType.LOGINREGISTRATIONPAGE);
        redirectAttr.addFlashAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        request.getSession().setAttribute(REGISTERED_USER_EMAIL, form.getEmail());
        request.getSession().setAttribute(APPROVAL_BY_CUSTOMER_SERVICE_NEEDED, StringUtils.isEmpty(data.getContactId()));
        redirectAttr.addFlashAttribute("existingCustomerId", data.getCustomerId());
        redirectAttr.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.signin.title");
        autoLoginStrategy.login(form.getEmail(), form.getPwd(), request, response, RegistrationType.STANDALONE);
        try {
            //setting correct deliverymode after registration
            final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();
            String defaultDeliveryMode = getCheckoutFacade().getDefaultDeliveryMode();
            if ((null != customer && null == customer.getDefaultDeliveryMethod()) || (null != defaultDeliveryMode && !defaultDeliveryMode.isEmpty() && null != customer && null == customer.getDefaultDeliveryMethod() && defaultDeliveryMode.equalsIgnoreCase(customer.getDefaultDeliveryMethod()))) {
                getCheckoutFacade().setDeliveryMode(defaultDeliveryMode);
            }
        } catch (Exception ex) {
            LOG.error("Error while updating cart delivery mode after checkout", ex);
        }
        if (registrationType.equals(RegistrationType.CHECKOUT)) {
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + getCheckoutSuccessRedirect(true, request));
        }
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + getSuccessRedirect(model, request, response, redirectAttr));
    }

    private boolean validateRegion(final String countryCode, final String regionCode) {
        if (StringUtils.isNotEmpty(countryCode) && StringUtils.isEmpty(regionCode)) {
            try {

                final CountryModel country = getCommonI18NService().getCountry(countryCode);
                if (country != null && CollectionUtils.isNotEmpty(country.getRegions())) {
                    return false;
                }
            } catch (final Exception e) {
                return false;
            }
        }
        return true;
    }

    private String handleRegistrationError(final Model model) throws CMSItemNotFoundException {
        storeCmsPageInModel(model, getCmsPage());
        setUpMetaDataForContentPage(model, (ContentPageModel) getCmsPage());
        return getView();
    }

    private String handleExistingRegistrationError(final Model model) throws CMSItemNotFoundException {

        if (!model.containsAttribute("checkoutRegisterForm")) {
            CheckoutRegisterForm checkoutRegisterForm = new CheckoutRegisterForm();
            checkoutRegisterForm.setBusinessCustomer(true);
            checkoutRegisterForm.setExistingCustomer(false);
            model.addAttribute("checkoutRegisterForm", checkoutRegisterForm);
        }

        if (!model.containsAttribute("checkoutb2bRegisterForm")) {
            model.addAttribute("checkoutb2bRegisterForm", new CheckoutB2BRegisterForm());
        }

        storeCmsPageInModel(model, getCmsPage());
        setUpMetaDataForContentPage(model, (ContentPageModel) getCmsPage());
        return getView();
    }

    /**
     * Create a new register data for B2B customer registration
     *
     * @param form the B2B register form
     * @return a new instance of {@code DistRegisterData}
     */
    protected DistRegisterData createB2BRegisterData(final RegisterB2BForm form) {
        final DistRegisterData data = createGeneralRegisterData(form);
        // b2b stuff
        final String salesOrgCode = getCurrentSalesOrg().getCode();
        String orgnumber = form.getOrganizationalNumber();
        data.setDuns(form.getDuns());
        Iterator<String> splittedCompanyNameIterator = getCustomerFacade().formatCompanyName(form.getCompany()).iterator();
        data.setCompanyName(splittedCompanyNameIterator.next());
        data.setCompanyName2(splittedCompanyNameIterator.hasNext() ? splittedCompanyNameIterator.next() : StringUtils.EMPTY);
        data.setCompanyName3(splittedCompanyNameIterator.hasNext() ? splittedCompanyNameIterator.next() : StringUtils.EMPTY);
        data.setFunctionCode(form.getFunctionCode());
        data.setCompanyMatch(form.getCompanyMatch());
        data.setVatId(form.getVatId());

        if (salesOrgCode.equals("7650") && null != orgnumber) {
            orgnumber = orgnumber.replaceAll("\\s+", "");
        }
        data.setOrganizationalNumber(orgnumber);
        data.setCustomerType(CustomerType.B2B);
        data.setRegistrationType(RegistrationType.STANDALONE);
        data.setInvoiceEmail(StringUtils.isBlank(form.getInvoiceEmail()) ? form.getEmail() : form.getInvoiceEmail());
        data.setPhoneNumber(form.getPhoneNumber());
        return data;
    }

    /**
     * @param form
     * @return DistRegisterData
     */
    protected DistRegisterData createGeneralRegisterData(final RegisterForm form) {
        final DistRegisterData data = new DistRegisterData();
        data.setFirstName(form.getFirstName());
        data.setLastName(form.getLastName());
        data.setLogin(form.getEmail());
        data.setEmail(form.getEmail());
        data.setPassword(form.getPwd());
        data.setTitleCode(form.getTitleCode());
        data.setCustomerId(form.getCustomerId());
        data.setAdditionalAddressCompany(form.getAdditionalAddress());
        data.setCountryCode(form.getCountryCode());
        data.setCurrencyCode(form.getCurrencyCode());
        data.setPhoneNumber(form.getPhoneNumber());
        data.setMobileNumber(form.getMobileNumber());
        data.setFaxNumber(form.getFaxNumber());
        data.setStreetName("");
        data.setPostalCode("");
        data.setTown("");
        data.setNewsletterOption(form.isMarketingConsent());
        data.setCustomerId(form.getCustomerId());
        data.setPhoneMarketingOption(form.isMarketingConsent());
        data.setNpsConsent(form.isMarketingConsent());
        data.setInvoiceEmail(form.getInvoiceEmail());
        data.setVat4(form.getVat4());
        data.setLegalEmail(form.getLegalEmail());
        getSessionService().getCurrentSession().setAttribute("IS_MARKETING_REG", true);
        data.setPhoneConsent(form.isPhoneConsent());
        data.setPostConsent(form.isPostConsent());
        data.setSmsConsent(form.isSmsConsent());
        data.setPersonalisationConsent(form.isPersonalisationConsent());
        data.setProfilingConsent(form.isProfilingConsent());
        data.setPersonalisedRecommendationConsent(form.isPersonalisedRecommendationConsent());
        data.setCustomerSurveysConsent(form.isCustomerSurveysConsent());
        return data;
    }

    /**
     * Create a new register data for existing B2B customer registration
     *
     * @param form the B2B register form
     * @return a new instance of {@code DistRegisterData}
     */
    protected DistExistingCustomerRegisterData createB2BRegisterDataForExistingCustomer(final RegisterB2BForm form) {
        final DistExistingCustomerRegisterData data = createGeneralRegisterDataForExistingCustomer(form);
        final String salesOrgCode = getCurrentSalesOrg().getCode();
        String orgnumber = form.getOrganizationalNumber();
        data.setCompanyName(form.getCompany());
        data.setFunctionCode(form.getFunctionCode());
        data.setVatId(form.getVatId());
        data.setInvoiceEmail(form.getInvoiceEmail());
        if (salesOrgCode.equals("7650") && null != orgnumber) {
            orgnumber = orgnumber.replaceAll("\\s+", "");
        }
        data.setOrganizationalNumber(orgnumber);
        data.setPhoneNumber(form.getPhoneNumber());
        data.setPhoneConsent(form.isPhoneConsent());
        data.setPostConsent(form.isPostConsent());
        data.setSmsConsent(form.isSmsConsent());
        data.setPersonalisationConsent(form.isPersonalisationConsent());
        data.setProfilingConsent(form.isProfilingConsent());
        data.setPersonalisedRecommendationConsent(form.isPersonalisedRecommendationConsent());
        data.setCustomerSurveysConsent(form.isCustomerSurveysConsent());
        return data;

    }

    /**
     * Create a new general register data for existing customer registration
     *
     * @param form the general register form
     * @return a new instance of {@code DistRegisterData}
     */
    protected DistExistingCustomerRegisterData createGeneralRegisterDataForExistingCustomer(final RegisterForm form) {
        final DistExistingCustomerRegisterData data = new DistExistingCustomerRegisterData();
        data.setFirstName(form.getFirstName());
        data.setLastName(form.getLastName());
        data.setLogin(form.getEmail());
        data.setPassword(form.getPwd());
        data.setEmail(form.getEmail());
        data.setTitleCode(form.getTitleCode());
        data.setCustomerId(form.getCustomerId());
        data.setPhoneNumber(form.getPhoneNumber());
        data.setMobileNumber(form.getMobileNumber());
        data.setFaxNumber(form.getFaxNumber());
        data.setNewsletterOption(form.isMarketingConsent());
        data.setNpsConsent(form.isMarketingConsent());
        data.setPhoneMarketingOption(form.isMarketingConsent());
        data.setCustomerId(form.getCustomerId());
        data.setPhoneConsent(form.isPhoneConsent());
        data.setPostConsent(form.isPostConsent());
        data.setSmsConsent(form.isSmsConsent());
        data.setPersonalisationConsent(form.isPersonalisationConsent());
        data.setProfilingConsent(form.isProfilingConsent());
        data.setPersonalisedRecommendationConsent(form.isPersonalisedRecommendationConsent());
        data.setCustomerSurveysConsent(form.isCustomerSurveysConsent());
        return data;

    }

    /**
     * @param b2b
     * @return
     */
    protected RegisterForm getRegistrationForm(final boolean b2b) {
        final RegisterForm form = b2b ? new RegisterB2BForm() : new RegisterForm();
        final CurrencyData currency = getStoreSessionFacade().getDefaultCurrency();

        if (currency != null) {
            form.setCurrencyCode(currency.getIsocode());
        }
        return form;
    }

    /**
     * Clone the field errors belonging the {@code sourceObjectName}, if any, to the new {@code targetObjectName}.
     *
     * @param bindingResult
     * @param sourceObjectName
     * @param targetObjectName
     */
    protected void cloneBindingErrors(final BindingResult bindingResult, final String sourceObjectName, final String targetObjectName) {
        if (bindingResult == null || sourceObjectName == null || targetObjectName == null) {
            return;
        }

        bindingResult.getFieldErrors()
                .stream()
                .filter(field -> sourceObjectName.equals(field.getObjectName()))
                .forEach(field -> bindingResult.addError(createNewFieldError(targetObjectName, field)));
    }

    private FieldError createNewFieldError(String targetObjectName, FieldError field) {
        return new FieldError(targetObjectName, field.getField(), field.getRejectedValue(), field.isBindingFailure(), field.getCodes(), field.getArguments(), field.getDefaultMessage());
    }

    /**
     * Add different registration forms to the model.
     *
     * @param model
     */
    protected void addForms(final Model model) {
        // B2B form
        if (!model.containsAttribute("registerB2BForm")) {
            model.addAttribute("registerB2BForm", getRegistrationForm(true));
        }
        // B2C form
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", getRegistrationForm(false));
        }
    }


    /**
     * Add to the {@link Model} the form ID.
     *
     * @param model
     * @param type     the form type, i.e., b2b or b2c
     * @param existing
     */
    protected void selectFrom(final Model model, final String type, final boolean existing) {
        model.addAttribute("formID", new StringBuilder("form-").append(type).append(existing ? "-existing" : "").toString());
    }

    // Getters & Setters

    public DistUserFacade getUserFacade() {
        return userFacade;
    }

    public void setUserFacade(final DistUserFacade userFacade) {
        this.userFacade = userFacade;
    }

    public DistNewsletterFacade getNewsletterFacade() {
        return newsletterFacade;
    }

    public void setNewsletterFacade(final DistNewsletterFacade newsletterFacade) {
        this.newsletterFacade = newsletterFacade;
    }

    public GUIDCookieStrategy getGuidCookieStrategy() {
        return guidCookieStrategy;
    }

    public void setGuidCookieStrategy(final GUIDCookieStrategy guidCookieStrategy) {
        this.guidCookieStrategy = guidCookieStrategy;
    }

    public DistCheckoutFacade getCheckoutFacade() {
        return checkoutFacade;
    }

    public void setCheckoutFacade(final DistCheckoutFacade checkoutFacade) {
        this.checkoutFacade = checkoutFacade;
    }

    public HttpSessionRequestCache getHttpSessionRequestCache() {
        return httpSessionRequestCache;
    }

    public void setHttpSessionRequestCache(final HttpSessionRequestCache httpSessionRequestCache) {
        this.httpSessionRequestCache = httpSessionRequestCache;
    }

    public DefaultAutoLoginStrategy getAutoLoginStrategy() {
        return autoLoginStrategy;
    }

    public void setAutoLoginStrategy(DefaultAutoLoginStrategy autoLoginStrategy) {
        this.autoLoginStrategy = autoLoginStrategy;
    }
}
