package com.namics.distrelec.b2b.storefront.controllers.pages;

import static java.util.Collections.singletonList;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.xml.ws.WebServiceException;

import de.hybris.platform.commerceservices.enums.CustomerType;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.enums.RegistrationType;
import com.namics.distrelec.b2b.core.inout.erp.exception.ErpCommunicationException;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.util.DistLogUtils;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Registration;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.RegistrationInfo;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.customer.exceptions.ExistingCustomerRegistrationException;
import com.namics.distrelec.b2b.facades.user.data.DistDepartmentData;
import com.namics.distrelec.b2b.facades.user.data.DistExistingCustomerRegisterData;
import com.namics.distrelec.b2b.facades.user.data.DistFunctionData;
import com.namics.distrelec.b2b.facades.user.data.DistRegisterData;
import com.namics.distrelec.b2b.facades.vat.eu.DistVatEUFacade;
import com.namics.distrelec.b2b.storefront.attributes.Attributes;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.forms.RegisterB2BForm;
import com.namics.distrelec.b2b.storefront.forms.RegisterForm;
import com.namics.distrelec.b2b.storefront.forms.UpdateUserProfileForm;
import com.namics.distrelec.b2b.storefront.forms.ValidateUidForm;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.enums.SiteChannel;

@Controller
@RequestMapping(CustomerRegistrationPageController.REQUEST_MAPPING)
public class CustomerRegistrationPageController extends AbstractRegisterPageController {

    public static final String REQUEST_MAPPING_PAGE = "registration";

    public static final String CHECKOUT_REGISTRATION_SUCCESS = "/checkout";

    public static final String REGISTRATION_SUCCESS_B2C = "/welcome";

    public static final String REQUEST_MAPPING = "/" + REQUEST_MAPPING_PAGE;

    private static final String EMPTY_STRING = DistConstants.Punctuation.EMPTY;

    private static final String COMMA = DistConstants.Punctuation.COMMA;

    private static final String BIZ_SALES_ORG = "7801";

    private static final String XI = "XI";

    private static final String GB = "GB";

    @Autowired
    private Validator validator;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private DistVatEUFacade distVatEUFacade;

    @Autowired
    @Qualifier("messageSource")
    private MessageSource messageSource;

    @ModelAttribute("departments")
    public Collection<DistDepartmentData> getDepartments() {
        return getUserFacade().getDepartments();
    }

    @ModelAttribute("functions")
    public Collection<DistFunctionData> getFunctions() {
        return getUserFacade().getFunctions();
    }

    @Override
    @ModelAttribute("countriesB2B")
    public List<CountryData> getCountries() {
        return getCustomerFacade().getCountriesForRegistration(SiteChannel.B2B);
    }

    @ModelAttribute("countriesB2C")
    public List<CountryData> getCountriesB2C() {
        return getCustomerFacade().getCountriesForRegistration(SiteChannel.B2C);
    }

    @Override
    protected AbstractPageModel getCmsPage() throws CMSItemNotFoundException {
        return getContentPageForLabelOrId(REQUEST_MAPPING_PAGE);
    }

    @Override
    protected String getView() {
        return ControllerConstants.Views.Pages.Account.AccountRegisterPage_Consolidated;
    }

    @Override
    protected String getSuccessRedirect(final Model model, final HttpServletRequest request, final HttpServletResponse response,
                                        final RedirectAttributes redirectAttr) {
        String referer = request.getHeader(REFERER);
        if (StringUtils.contains(referer, "/registration/checkout")) {
            return CHECKOUT_REGISTRATION_SUCCESS;
        }
        return REGISTRATION_SUCCESS_B2C;
    }

    @GetMapping
    public String doRegister(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        String emailToRegister = (String) request.getSession().getAttribute("emailToRegister");
        model.addAttribute("emailToRegister", emailToRegister);
        RegisterB2BForm registerB2BForm = (RegisterB2BForm) model.getAttribute("registerB2BForm");
        if (registerB2BForm != null) {
            model.addAttribute("isJobRoleShown", getCustomerFacade().isJobRoleShown(getRegistrationType(registerB2BForm.getRegistrationType()), CustomerType.B2B));
        }
        DigitalDatalayer digitalDataLayer = getDigitalDatalayer(model);
        if (digitalDataLayer.getPage().getPageInfo().getRegistration() == null) {
            digitalDataLayer.getPage().getPageInfo().setRegistration(new Registration());
        }

        return getDefaultRegistrationPage(model);
    }

    protected DigitalDatalayer getDigitalDatalayer(final Model model) {
        if (!model.containsAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER)) {
            model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, new DigitalDatalayer());
        }
        return (DigitalDatalayer) model.asMap().get(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER);
    }

    @PostMapping(value = "/b2b")
    public String doRegisterB2B(@Valid final RegisterB2BForm form, final BindingResult bindingResult, final Model model, final HttpServletRequest request,
                                final HttpServletResponse response,
                                final RedirectAttributes redirectAttr) throws CMSItemNotFoundException, NumberParseException {
        setCountryForBiz(form);
        setPhoneNumberOnForm(form, bindingResult);
        if (StringUtils.isNotBlank(form.getCompany())) {
            form.setCompany(StringEscapeUtils.unescapeHtml(form.getCompany()));
        }
        setVATIDError(form, bindingResult);
        model.addAttribute("registerB2BForm", form);
        model.addAttribute("registrationType", form.getRegistrationType());
        addGlobalModelAttributes(model, request);
        selectFrom(model, "b2b", form.getExistingCustomer());
        DigitalDatalayer digitalDataLayer = getDigitalDatalayer(model);
        if (digitalDataLayer.getPage().getPageInfo().getRegistration() == null) {
            digitalDataLayer.getPage().getPageInfo().setRegistration(new Registration());
        }
        if (form.getLegalEmail() != null && form.getLegalEmail().equals(COMMA)) {
            form.setLegalEmail(EMPTY_STRING);
        }
        if (form.getExistingCustomer()) {
            return processExistingB2BRegisterUserRequest(null, form, bindingResult, model, request, response, redirectAttr,
                                                         getRegistrationType(form.getRegistrationType()));
        } else {
            return processB2BRegisterUserRequest(form, bindingResult, model, request, response, redirectAttr, getRegistrationType(form.getRegistrationType()));
        }
    }

    @PostMapping(value = "/b2b/async")
    public @ResponseBody ResponseEntity<RegistrationResponse> doRegisterB2BAsync(@Valid final RegisterB2BForm form, final BindingResult bindingResult,
                                                                                 final Model model, final HttpServletRequest request,
                                                                                 final HttpServletResponse response,
                                                                                 final RedirectAttributes redirectAttr) throws CMSItemNotFoundException,
                                                                                                                        NumberParseException {
        setCountryForBiz(form);
        setPhoneNumberOnForm(form, bindingResult);
        if (StringUtils.isNotBlank(form.getCompany())) {
            form.setCompany(StringEscapeUtils.unescapeHtml(form.getCompany()));
        }
        setVATIDError(form, bindingResult);
        model.addAttribute("registerB2BForm", form);
        addGlobalModelAttributes(model, request);
        selectFrom(model, "b2b", form.getExistingCustomer());
        DigitalDatalayer digitalDataLayer = getDigitalDatalayer(model);
        if (digitalDataLayer.getPage().getPageInfo().getRegistration() == null) {
            digitalDataLayer.getPage().getPageInfo().setRegistration(new Registration());
        }
        if (form.getLegalEmail() != null && form.getLegalEmail().equals(COMMA)) {
            form.setLegalEmail(EMPTY_STRING);
        }
        if (form.getExistingCustomer()) {
            return processExistingB2BRegisterUserRequestAsync(form, bindingResult, model, request, response, redirectAttr,
                                                              getRegistrationType(form.getRegistrationType()));
        } else {
            return processB2BRegisterUserRequestAsync(form, bindingResult, model, request, response, redirectAttr,
                                                      getRegistrationType(form.getRegistrationType()));
        }
    }

    protected ResponseEntity<RegistrationResponse> processExistingB2BRegisterUserRequestAsync(final RegisterB2BForm form, final BindingResult bindingResult,
                                                                                              final Model model, final HttpServletRequest request,
                                                                                              final HttpServletResponse response,
                                                                                              final RedirectAttributes redirectAttr,
                                                                                              final RegistrationType registrationType)
                                                                                                                                       throws CMSItemNotFoundException {
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        final RegistrationInfo registrationInfo = new RegistrationInfo();
        model.addAttribute("isExistingUser", true);
        registrationInfo.setRegType(EXISTING + B2B);
        registrationInfo.setRegDate(new Date());

        if (getCustomerFacade().isJobRoleShown(getRegistrationType(form.getRegistrationType()), CustomerType.B2B) && !isFunctionCodeValid(form.getFunctionCode())) {
            bindingResult.addError(new FieldError("registerForm", "functionCode", null, false, new String[]{"register.function.invalid"}, null, null));
        }

        if (bindingResult.hasErrors()) {
            return createValidationErrorsResponse(bindingResult, singletonList("form.global.error"));
        } else if (!getCaptchaUtil().validateReCaptcha(request)) {
            model.addAttribute("captchaError", Boolean.TRUE);
            return createGlobalErrorsResponse(singletonList("form.captcha.error"));
        }

        parsePhoneNumber(form, bindingResult);

        final DistExistingCustomerRegisterData data = createB2BRegisterDataForExistingCustomer(form);
        data.setRegistrationType(registrationType);
        final boolean updateCurrentUserOnly = true;
        try {
            getCustomerFacade().registerExistingCustomer(data, updateCurrentUserOnly, CustomerType.B2B);
            redirectAttr.addFlashAttribute("showDoubleOptInPopup", isConsentConfirmationRequired() && data.isNewsletterOption());
        } catch (final DuplicateUidException e) {
            logError(LOG, "{} {} Registration failed due to Duplicate UID(Email): {}\nDuplicateUidException: ", e, ErrorLogCode.REGISTRATION_ERROR,
                     ErrorSource.MODEL_SAVING_EXCEPTION_HYBRIS, data.getEmail());
            String errorMessageKey = null;
            if (e.getMessage() != null && e.getMessage().contains("Registration for an existing")) {
                errorMessageKey = "registration.error.existing.contact.hybris";
            } else {
                bindingResult.rejectValue("email", "registration.error.account.exists.title", new Object[] {},
                                          resolveMessage("registration.error.account.exists.title"));
                errorMessageKey = "form.global.error";
            }
            return createValidationErrorsResponse(bindingResult, Arrays.asList(errorMessageKey));
        } catch (final ExistingCustomerRegistrationException e) {
            logError(LOG, "{} {} Registration failed due to Existing UID(Email): {} " + data.getEmail() + "\nExistingCustomerRegistrationException: ", e,
                     ErrorLogCode.REGISTRATION_ERROR, ErrorSource.MODEL_SAVING_EXCEPTION_HYBRIS, data.getEmail());
            String errorMessageKey = null;
            if (e.getReason() != null) {
                errorMessageKey = e.getReason().getMessageKey() != null ? resolveMessage(e.getReason().getMessageKey()) : e.getReason().getValue();
            } else {
                errorMessageKey = resolveMessage("form.global.error");
            }
            return createGlobalErrorsResponse(Arrays.asList(errorMessageKey));
        } catch (final ErpCommunicationException e) {
            logError(LOG, "{} {} Registration failed because of an ERP communication error for the customer with Email: {}\nErpCommunicationException: ", e,
                     ErrorLogCode.REGISTRATION_ERROR, ErrorSource.SAP_FAULT, data.getEmail());
            return createGlobalErrorsResponse(Arrays.asList("register.form.error.erpcommunication"));
        } catch (final Exception e) {
            logError(LOG, "{} {} Registration failed for the customer with Email: {} Exception: ", e, ErrorLogCode.REGISTRATION_ERROR, data.getEmail());
            return ResponseEntity.badRequest().body(new RegistrationResponse(Arrays.asList(e.getMessage())));
        }
        registrationInfo.setRegMsg(SUCCESS);
        getDistDigitalDatalayerFacade().populateRegistrationType(digitalDatalayer, registrationInfo);
        getDistDigitalDatalayerFacade().populatePrimaryPageCategoryAndPageType(digitalDatalayer,
                                                                               DigitalDatalayerConstants.PageCategory.REGISTRATION + "|"
                                                                                                 + getCmsPage().getTitle(Locale.ENGLISH),
                                                                               DigitalDatalayerConstants.PageType.LOGINREGISTRATIONPAGE);
        redirectAttr.addFlashAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        request.getSession().setAttribute(REGISTERED_USER_EMAIL, form.getEmail());
        request.getSession().setAttribute(APPROVAL_BY_CUSTOMER_SERVICE_NEEDED, Boolean.valueOf(StringUtils.isEmpty(data.getContactId())));
        redirectAttr.addFlashAttribute("existingCustomerId", data.getCustomerId());
        redirectAttr.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.signin.title");
        autoLoginStrategy.login(form.getEmail(), form.getPwd(), request, response, registrationType);
        if (registrationType.equals(RegistrationType.CHECKOUT)) {
            // Add attribute to session, as it disappears from RedirectAttributes
            getSessionService().setAttribute("showDoubleOptInPopup", isConsentConfirmationRequired() && data.isNewsletterOption());
            return createSuccessResponse(getCheckoutSuccessRedirect(true, request), redirectAttr, request, response);
        }
        return createSuccessResponse(getSuccessRedirect(model, request, response, redirectAttr), redirectAttr, request, response);
    }

    protected ResponseEntity<RegistrationResponse> processB2BRegisterUserRequestAsync(final RegisterB2BForm form, final BindingResult bindingResult,
                                                                                      final Model model,
                                                                                      final HttpServletRequest request, final HttpServletResponse response,
                                                                                      final RedirectAttributes redirectAttr,
                                                                                      final RegistrationType registrationType) throws CMSItemNotFoundException,
                                                                                                                               NumberParseException {
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        final RegistrationInfo registrationInfo = new RegistrationInfo();
        registrationInfo.setRegType(B2B_LOWER);
        registrationInfo.setRegDate(new Date());
        String[] vat4EnableCountries = getConfigurationService().getConfiguration().getString("registration.vat4Countries").split(";");

        if (Arrays.stream(vat4EnableCountries).anyMatch(getStoreSessionFacade().getCurrentCountry().getIsocode()::equals)
                && (StringUtils.isBlank(form.getLegalEmail()) && StringUtils.isBlank(form.getVat4()))) {
            bindingResult.addError(new FieldError("registerForm", "vat4", null, false, new String[] { "vat.reg.error" }, null, null));
        }

        if (getCustomerFacade().isJobRoleShown(getRegistrationType(form.getRegistrationType()), CustomerType.B2B) && !isFunctionCodeValid(form.getFunctionCode())) {
            bindingResult.addError(new FieldError("registerForm", "functionCode", null, false, new String[]{"register.function.invalid"}, null, null));
        }

        if (!bindingResult.hasErrors()) {
            if (StringUtils.isBlank(form.getVatId()) && mandatoryVatId()) {
                bindingResult.addError(new FieldError("registerForm", "vatId", null, false, new String[] { "register.vatId.validationMessage" }, null, null));
            } else if (!registrationType.name().equals(RegistrationType.CHECKOUT.name()) && !getCaptchaUtil().validateReCaptcha(request)) {
                model.addAttribute("captchaError", Boolean.TRUE);
                return createGlobalErrorsResponse(singletonList("form.captcha.error"));
            }
        }

        parsePhoneNumber(form, bindingResult);

        if (bindingResult.hasErrors()) {
            return createValidationErrorsResponse(bindingResult, singletonList("form.global.error"));
        }

        DistRegisterData registrationData = createB2BRegisterData(form);

        // Check Marketing cookie
        String marketingCookie = Attributes.FF_ENSIGTHEN_MARKETING_COOKIE.getValueFromCookies(request);
        registrationData.setMarketingCookieEnabled("1".equals(marketingCookie) ? true : false);

        try {
            registrationData.setRegistrationType(registrationType);
            getCustomerFacade().registerNewCustomer(registrationData, CustomerType.B2B);
            redirectAttr.addFlashAttribute("showDoubleOptInPopup", isConsentConfirmationRequired() && registrationData.isNewsletterOption());
        } catch (final DuplicateUidException e) {
            logError(LOG, "{} {} Registration failed due to Duplicate UID(Email): {}", e, ErrorLogCode.REGISTRATION_ERROR, ErrorSource.DB, form.getEmail());
            bindingResult.rejectValue("email", "registration.error.account.exists.title", new Object[] {},
                                      resolveMessage("registration.error.account.exists.title"));
            return createValidationErrorsResponse(bindingResult, Arrays.asList("form.global.error"));
        } catch (final ErpCommunicationException e) {
            logError(LOG, "{} {} Registration failed because of an ERP communication error for the customer with Email: {}",
                     e, ErrorLogCode.REGISTRATION_ERROR, ErrorSource.SAP_FAULT, form.getEmail());
            String message = null;
            if (e.getCause() instanceof P1FaultMessage) {
                final P1FaultMessage fault = (P1FaultMessage) e.getCause();
                message = StringUtils.isNotBlank(fault.getFaultInfo().getFaultText()) ? fault.getFaultInfo().getFaultText()
                                                                                      : fault.getFaultInfo().getFaultName();
            }

            if (StringUtils.isNotEmpty(message)) {
                if (message.endsWith("Tax code 1 is not valid")) {
                    bindingResult.rejectValue("vatId", "register.vatId.validationMessage", new Object[] {}, resolveMessage("register.vatId.validationMessage"));
                    return createValidationErrorsResponse(bindingResult, Arrays.asList("form.global.error"));
                }
            }

            String errorMessage = StringUtils.isNotBlank(message) ? message : resolveMessage("register.form.error.erpcommunication");
            Collection<RegistrationResponse.ValidationError> validationErrors = bindingResult.getFieldErrors().stream()
                                                                                             .map(fieldError -> new RegistrationResponse.ValidationError(fieldError.getField(),
                                                                                                                                                         fieldError.getDefaultMessage()))
                                                                                             .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new RegistrationResponse(validationErrors, Arrays.asList(errorMessage)));
        } catch (final Exception e) {
            logError(LOG, "{} Registration failed for the customer with Email: {}", e, ErrorLogCode.REGISTRATION_ERROR, form.getEmail());
            return createGlobalErrorsResponse(Arrays.asList("form.global.error"));
        }
        registrationInfo.setRegMsg(SUCCESS);
        getDistDigitalDatalayerFacade().populateRegistrationType(digitalDatalayer, registrationInfo);
        getDistDigitalDatalayerFacade().populatePrimaryPageCategoryAndPageType(digitalDatalayer,
                                                                               DigitalDatalayerConstants.PageCategory.REGISTRATION + "|"
                                                                                                 + getCmsPage().getTitle(Locale.ENGLISH),
                                                                               DigitalDatalayerConstants.PageType.LOGINREGISTRATIONPAGE);
        redirectAttr.addFlashAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        redirectAttr.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.signin.title");
        autoLoginStrategy.login(form.getEmail(), form.getPwd(), request, response, registrationType);
        getSessionService().setAttribute(DistConstants.Session.IS_NEW_REGISTRATION, Boolean.TRUE);
        if (registrationType.equals(RegistrationType.CHECKOUT)) {
            // Add attribute to session, as it disappears from RedirectAttributes
            getSessionService().setAttribute("showDoubleOptInPopup", isConsentConfirmationRequired() && registrationData.isNewsletterOption());
            return createSuccessResponse(getCheckoutSuccessRedirect(false, request), redirectAttr, request, response);
        }
        return createSuccessResponse(getSuccessRedirect(model, request, response, redirectAttr), redirectAttr, request, response);
    }

    private boolean isFunctionCodeValid(String functionCode) {
        return StringUtils.isNotBlank(functionCode) && functionCode.length() < 3;
    }

    private void parsePhoneNumber(final RegisterForm form, final BindingResult bindingResult) {
        String defaultRegion = getCurrentCountry().getIsocode();
        if (BIZ_COUNTRY_CODE.equalsIgnoreCase(getCurrentCountry().getIsocode())) {
            defaultRegion = form.getCountryCode();
        }
        if (StringUtils.equals(XI, defaultRegion)) {
            defaultRegion = GB;
        }

        try {
            if (!StringUtils.isBlank(form.getPhoneNumber())) {
                final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(form.getPhoneNumber(), defaultRegion);
                form.setPhoneNumber(phoneNumberToString(phoneNumber));
            }

            if (!StringUtils.isBlank(form.getMobileNumber())) {
                final PhoneNumber mobileNumber = PHONENUMBERUTIL.parse(form.getMobileNumber(), defaultRegion);
                form.setMobileNumber(phoneNumberToString(mobileNumber));
            }
        } catch (NumberParseException e) {
            String message = messageSource.getMessage("register.phoneNumber.invalid", new Object[] { null, form.getTelephoneNumber() },
                                                      getI18nService().getCurrentLocale());
            bindingResult.rejectValue("telephoneNumber", "register.phoneNumber.invalid", new Object[] {}, message);
        }
    }

    private ResponseEntity<RegistrationResponse> createValidationErrorsResponse(BindingResult bindingResult, Collection<String> globalErrors) {
        Collection<String> errorMessages = resolveMessages(globalErrors);
        Collection<RegistrationResponse.ValidationError> validationErrors = bindingResult.getFieldErrors().stream()
                                                                                         .map(fieldError -> new RegistrationResponse.ValidationError(fieldError.getField(),
                                                                                                                                                     fieldError.getDefaultMessage()))
                                                                                         .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(new RegistrationResponse(validationErrors, errorMessages));
    }

    private ResponseEntity<RegistrationResponse> createGlobalErrorsResponse(Collection<String> globalErrors) {
        Collection<String> errorMessages = resolveMessages(globalErrors);
        return ResponseEntity.badRequest().body(new RegistrationResponse(errorMessages));
    }

    private ResponseEntity<RegistrationResponse> createSuccessResponse(String redirectTo, RedirectAttributes redirectAttributes, HttpServletRequest request,
                                                                       HttpServletResponse response) {
        FlashMap outputFlashMap = RequestContextUtils.getOutputFlashMap(request);
        outputFlashMap.putAll(redirectAttributes.getFlashAttributes());
        outputFlashMap.setTargetRequestPath(redirectTo);
        FlashMapManager flashMapManager = RequestContextUtils.getFlashMapManager(request);
        flashMapManager.saveOutputFlashMap(outputFlashMap, request, response);
        return ResponseEntity.ok(new RegistrationResponse(redirectTo));
    }

    private List<String> resolveMessages(Collection<String> messageKeys) {
        return messageKeys.stream()
                          .map(this::resolveMessage)
                          .collect(Collectors.toList());
    }

    // This is dummy method added as message was not getting displayed on FE due to some issue
    private void setVATIDError(RegisterB2BForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError error = bindingResult.getFieldError("vatId");
            if (error != null && error.getDefaultMessage() != null) {
                form.setVatIdMessage(error.getDefaultMessage());
            }
        }

        CountryData currentCountry = getStoreSessionFacade().getCurrentCountry();
        if (isVatValidatedExternaly(currentCountry.getIsocode())) {
            boolean success = distVatEUFacade.validateVatNumber(form.getGroupVatId(), getVatPrefix(currentCountry.getIsocode()));
            if (!success) {
                String errorMessageCode = "vat.validation.error." + currentCountry.getIsocode();
                bindingResult.rejectValue("vatId",
                                          errorMessageCode,
                                          getMessageSource().getMessage(errorMessageCode, new Object[] {}, getI18nService().getCurrentLocale()));
            }
        }
    }

    private String getVatPrefix(String countryCode) {
        switch (countryCode) {
            case "GR":
                return "EL";
            default:
                return countryCode;
        }
    }

    private boolean isVatValidatedExternaly(String countryCode) {
        switch (countryCode) {
            case "AT":
            case "BE":
            case "CZ":
            case "DK":
            case "EE":
            case "FI":
            case "FR":
            case "DE":
            case "HU":
            case "IT":
            case "LV":
            case "LT":
            case "NL":
            case "PL":
            case "RO":
            case "SK":
            case "BG":
            case "HR":
            case "CY":
            case "GR":
            case "IE":
            case "LU":
            case "MT":
            case "PT":
            case "SI":
            case "ES":
            case "XI":
                return true;
            default:
                return false;
        }
    }

    private boolean isPhoneValidation() {
        return getConfigurationService().getConfiguration().getBoolean("phone.number.validity.check", false);
    }
    // This is dummy method added as message was not getting displayed on FE due to some issue

    private void setCountryForBiz(RegisterForm form) {
        final DistSalesOrgModel currentSalesOrg = getDistSalesOrgService().getCurrentSalesOrg();
        if (BIZ_SALES_ORG.equals(currentSalesOrg.getCode()) && StringUtils.isBlank(form.getCountryCode())) {
            String countryIsoCode = getStoreSessionFacade().getCurrentCountry().getIsocode();
            form.setCountryCode(countryIsoCode);
        }
    }

    private void setPhoneNumberOnForm(RegisterForm form, final BindingResult bindingResult) {
        try {
            if (null != bindingResult.getFieldErrors()
                    && bindingResult.getFieldErrors().stream().filter(err -> err.getField().equalsIgnoreCase("telephoneNumber")).count() > 0) {
                return;
            }
            String countryIsoCode = form.getCountryCode();
            if (countryIsoCode.equals(XI)) {
                countryIsoCode = GB;
            }
            final String telNumber = form.getTelephoneNumber();
            final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            if (null != telNumber && isPhoneValidation()) {
                final PhoneNumber numberProto = phoneUtil.parse(telNumber, countryIsoCode);
                boolean isValidForRegion = phoneUtil.isValidNumberForRegion(numberProto, countryIsoCode);
                boolean isPossibleNumber = phoneUtil.isPossibleNumber(numberProto);
                PhoneNumberType phoneType = phoneUtil.getNumberType(numberProto);
                if (isValidForRegion && isPossibleNumber) {
                    if (PhoneNumberType.MOBILE.equals(phoneType)) {
                        form.setMobileNumber(telNumber);
                    } else if (PhoneNumberType.FIXED_LINE.equals(phoneType)) {
                        form.setPhoneNumber(telNumber);
                    } else if (!PhoneNumberType.MOBILE.equals(phoneType) && !PhoneNumberType.FIXED_LINE.equals(phoneType)) {
                        form.setPhoneNumber(telNumber);
                    }
                } else {
                    setValidationError(phoneUtil, bindingResult, countryIsoCode);
                }
            } else {
                String validNumber = getValidPhoneNumber(telNumber);
                if (null != validNumber && validNumber.length() > 8) {
                    form.setPhoneNumber(validNumber);
                }
            }
        } catch (Exception e) {
            LOG.error("Error while setting up phone number", e);
        }
    }

    private void setValidationError(final PhoneNumberUtil phoneUtil, final BindingResult bindingResult, final String countryIsoCode) {
        final PhoneNumber exNr = phoneUtil.getExampleNumber(countryIsoCode);
        String exampleInt = exNr == null ? "" : phoneUtil.format(exNr, PhoneNumberFormat.NATIONAL);
        String exampleNational = exNr == null ? "" : phoneUtil.format(exNr, PhoneNumberFormat.INTERNATIONAL);
        Object[] messageParams = { exampleInt, exampleNational };
        bindingResult.rejectValue("telephoneNumber",
                                  "register.phoneNumber.invalid",
                                  messageParams,
                                  getMessageSource().getMessage("register.phoneNumber.invalid", messageParams, getI18nService().getCurrentLocale()));
    }

    private String getValidPhoneNumber(final String inputNumber) {
        return inputNumber.replaceAll(getConfigurationService().getConfiguration().getString("valid.phone.number.regex", "[^0-9+]"), StringUtils.EMPTY);
    }

    @PostMapping(value = "/b2c")
    public String doRegisterB2C(@Valid final RegisterForm form, final BindingResult bindingResult, final Model model, final HttpServletRequest request,
                                final HttpServletResponse response,
                                final RedirectAttributes redirectAttr) throws CMSItemNotFoundException, NumberParseException {
        setCountryForBiz(form);
        setPhoneNumberOnForm(form, bindingResult);
        addGlobalModelAttributes(model, request);
        model.addAttribute("registrationType", form.getRegistrationType());
        selectFrom(model, "b2c", form.getExistingCustomer());
        DigitalDatalayer digitalDataLayer = getDigitalDatalayer(model);
        if (digitalDataLayer.getPage().getPageInfo().getRegistration() == null) {
            digitalDataLayer.getPage().getPageInfo().setRegistration(new Registration());
        }
        if (form.getExistingCustomer()) {
            return processExistingB2CRegisterUserRequest(null, form, bindingResult, model, request, response, redirectAttr,
                                                         getRegistrationType(form.getRegistrationType()));
        } else {
            return processB2CRegisterUserRequest(form, bindingResult, model, request, response, getRegistrationType(form.getRegistrationType()), redirectAttr);
        }
    }

    @PostMapping(value = "/b2c/async")
    public @ResponseBody ResponseEntity<RegistrationResponse> doRegisterB2CAsync(@Valid final RegisterForm form, final BindingResult bindingResult,
                                                                                 final Model model, final HttpServletRequest request,
                                                                                 final HttpServletResponse response,
                                                                                 final RedirectAttributes redirectAttr) throws CMSItemNotFoundException,
                                                                                                                        NumberParseException {
        setCountryForBiz(form);
        setPhoneNumberOnForm(form, bindingResult);
        addGlobalModelAttributes(model, request);
        selectFrom(model, "b2c", form.getExistingCustomer());
        DigitalDatalayer digitalDataLayer = getDigitalDatalayer(model);
        if (digitalDataLayer.getPage().getPageInfo().getRegistration() == null) {
            digitalDataLayer.getPage().getPageInfo().setRegistration(new Registration());
        }
        if (form.getExistingCustomer()) {
            return processExistingB2CRegisterUserRequestAsync(null, form, bindingResult, model, request, response, redirectAttr,
                                                              getRegistrationType(form.getRegistrationType()));
        } else {
            return processB2CRegisterUserRequestAsync(form, bindingResult, model, request, response, getRegistrationType(form.getRegistrationType()),
                                                      redirectAttr);
        }
    }

    protected ResponseEntity<RegistrationResponse> processExistingB2CRegisterUserRequestAsync(final String referer, final RegisterForm form,
                                                                                              final BindingResult bindingResult,
                                                                                              final Model model, final HttpServletRequest request,
                                                                                              final HttpServletResponse response,
                                                                                              final RedirectAttributes redirectAttr,
                                                                                              final RegistrationType registrationType) throws CMSItemNotFoundException,
                                                                                                                                       NumberParseException {
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        final RegistrationInfo registrationInfo = new RegistrationInfo();
        registrationInfo.setRegType(EXISTING + B2C);
        registrationInfo.setRegDate(new Date());
        model.addAttribute("isExistingUser", true);
        model.addAttribute("existingCustomerId", form.getCustomerId());
        if (bindingResult.hasErrors()) {
            return createValidationErrorsResponse(bindingResult, singletonList("form.global.error"));
        } else if (!getCaptchaUtil().validateReCaptcha(request)) {
            model.addAttribute("captchaError", Boolean.TRUE);
            return createGlobalErrorsResponse(singletonList("form.captcha.error"));
        }

        parsePhoneNumber(form, bindingResult);

        final DistExistingCustomerRegisterData data = createGeneralRegisterDataForExistingCustomer(form);
        data.setRegistrationType(registrationType);
        try {
            getCustomerFacade().registerExistingCustomer(data, false, CustomerType.B2C);
            redirectAttr.addFlashAttribute("showDoubleOptInPopup", isConsentConfirmationRequired() && data.isNewsletterOption());
        } catch (final DuplicateUidException e) {
            logError(LOG, "{} {} Registration failed due to Duplicate UID(Email): {}", e, ErrorLogCode.REGISTRATION_ERROR,
                     ErrorSource.MODEL_SAVING_EXCEPTION_HYBRIS, data.getEmail());
            bindingResult.rejectValue("email", "registration.error.account.exists.title", new Object[] {},
                                      resolveMessage("registration.error.account.exists.title"));
            return createValidationErrorsResponse(bindingResult, singletonList("registration.error.existing.account.already.present"));
        } catch (final ExistingCustomerRegistrationException e) {
            logError(LOG, "{} {} Registration failed due to Existing UID(Email): {}\nExistingCustomerRegistrationException: ", e,
                     ErrorLogCode.REGISTRATION_ERROR, ErrorSource.MODEL_SAVING_EXCEPTION_HYBRIS, data.getEmail());
            return createGlobalErrorsResponse(singletonList(e.getReason().getMessageKey()));
        } catch (final ErpCommunicationException e) {
            logError(LOG, "{} {} Registration failed because of an ERP communication error for the customer with Email: {}", e, ErrorLogCode.REGISTRATION_ERROR,
                     ErrorSource.SAP_FAULT, data.getEmail());
            return createGlobalErrorsResponse(singletonList("register.form.error.erpcommunication"));
        } catch (final Exception e) {
            logError(LOG, "{} Registration failed for the customer with Email: {}", e, ErrorLogCode.REGISTRATION_ERROR, data.getEmail());
            return createGlobalErrorsResponse(singletonList("form.global.error"));
        }
        registrationInfo.setRegMsg(SUCCESS);
        getDistDigitalDatalayerFacade().populateRegistrationType(digitalDatalayer, registrationInfo);
        getDistDigitalDatalayerFacade().populatePrimaryPageCategoryAndPageType(digitalDatalayer,
                                                                               DigitalDatalayerConstants.PageCategory.REGISTRATION + "|"
                                                                                                 + getCmsPage().getTitle(Locale.ENGLISH),
                                                                               DigitalDatalayerConstants.PageType.LOGINREGISTRATIONPAGE);
        redirectAttr.addFlashAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        request.getSession().setAttribute(REGISTERED_USER_EMAIL, form.getEmail());
        request.getSession().setAttribute(APPROVAL_BY_CUSTOMER_SERVICE_NEEDED, StringUtils.isEmpty(data.getContactId()));
        redirectAttr.addFlashAttribute("existingCustomerId", data.getCustomerId());
        redirectAttr.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.signin.title");
        autoLoginStrategy.login(form.getEmail(), form.getPwd(), request, response, RegistrationType.STANDALONE);
        return createSuccessResponse(getSuccessRedirect(model, request, response, redirectAttr), redirectAttr, request, response);
    }

    protected ResponseEntity<RegistrationResponse> processB2CRegisterUserRequestAsync(final RegisterForm form, final BindingResult bindingResult,
                                                                                      final Model model,
                                                                                      final HttpServletRequest request, final HttpServletResponse response,
                                                                                      final RegistrationType registrationType,
                                                                                      final RedirectAttributes redirectAttr) throws CMSItemNotFoundException,
                                                                                                                             NumberParseException {

        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        final RegistrationInfo registrationInfo = new RegistrationInfo();
        registrationInfo.setRegType(B2C_LOWER);
        registrationInfo.setRegDate(new Date());
        // Italian b2c stuff
        if ("IT".equals(getStoreSessionFacade().getCurrentCountry().getIsocode()) && StringUtils.isBlank(form.getCodiceFiscale())) {
            bindingResult.addError(new FieldError("registerForm", "codiceFiscale", null, false, new String[] { "register.codiceFiscale.invalid" }, null, null));
        }

        if (getCountriesB2C().stream().noneMatch(countryData -> countryData.getIsocode().equals(form.getCountryCode()))) {
            bindingResult.addError(new FieldError("registerForm", "countryCode", null, false, new String[] { "register.country.invalid" }, null, null));
        }

        if (!bindingResult.hasErrors()) {
            if (!getCaptchaUtil().validateReCaptcha(request)) {
                model.addAttribute("captchaError", Boolean.TRUE);
                return createGlobalErrorsResponse(singletonList("form.captcha.error"));
            }
        }

        parsePhoneNumber(form, bindingResult);

        if (bindingResult.hasErrors()) {
            return createValidationErrorsResponse(bindingResult, singletonList("form.global.error"));
        }

        final DistRegisterData data = createGeneralRegisterData(form);

        // Check Marketing cookie
        String marketingCookie = Attributes.FF_ENSIGTHEN_MARKETING_COOKIE.getValueFromCookies(request);
        data.setMarketingCookieEnabled("1".equals(marketingCookie) ? true : false);

        data.setVatId(form.getCodiceFiscale() != null ? form.getCodiceFiscale().toUpperCase() : null);
        data.setRegistrationType(registrationType);
        try {
            getCustomerFacade().registerNewCustomer(data, CustomerType.B2C);
            redirectAttr.addFlashAttribute("showDoubleOptInPopup", isConsentConfirmationRequired() && data.isNewsletterOption());
        } catch (final DuplicateUidException e) {
            logError(LOG, "{} {} Registration failed due to Duplicate UID(Email): {}", e, ErrorLogCode.REGISTRATION_ERROR, ErrorSource.DB, data.getEmail());
            bindingResult.rejectValue("email", "registration.error.account.exists.title", new Object[] {},
                                      resolveMessage("registration.error.account.exists.title"));
            return createValidationErrorsResponse(bindingResult, Arrays.asList("form.global.error"));
        } catch (final ErpCommunicationException e) {
            logError(LOG, "{} {} Registration failed because of an ERP communication error for the customer with Email: {}", e, ErrorLogCode.REGISTRATION_ERROR,
                     ErrorSource.SAP_FAULT, data.getEmail());
            String errorMessage = e.getCause() instanceof P1FaultMessage ? ((P1FaultMessage) e.getCause()).getFaultInfo().getFaultText()
                                                                         : resolveMessage("register.form.error.erpcommunication");
            return ResponseEntity.badRequest().body(new RegistrationResponse(Arrays.asList(errorMessage)));
        } catch (final Exception e) {
            logError(LOG, "{} Registration failed for the customer with Email: {}", e, ErrorLogCode.REGISTRATION_ERROR.getCode(), data.getEmail());
            return createGlobalErrorsResponse(Arrays.asList("form.global.error"));
        }

        request.getSession().setAttribute(REGISTERED_USER_EMAIL, form.getEmail());
        registrationInfo.setRegMsg(SUCCESS);
        getDistDigitalDatalayerFacade().populateRegistrationType(digitalDatalayer, registrationInfo);
        getDistDigitalDatalayerFacade().populatePrimaryPageCategoryAndPageType(digitalDatalayer,
                                                                               DigitalDatalayerConstants.PageCategory.REGISTRATION + "|"
                                                                                                 + getCmsPage().getTitle(Locale.ENGLISH),
                                                                               DigitalDatalayerConstants.PageType.LOGINREGISTRATIONPAGE);
        redirectAttr.addFlashAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        redirectAttr.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.signin.title");
        autoLoginStrategy.login(form.getEmail(), form.getPwd(), request, response, registrationType);
        getSessionService().setAttribute(DistConstants.Session.IS_NEW_REGISTRATION, Boolean.TRUE);
        if (registrationType.equals(RegistrationType.CHECKOUT)) {
            // Add attribute to session, as it disappears from RedirectAttributes
            getSessionService().setAttribute("showDoubleOptInPopup", isConsentConfirmationRequired() && data.isNewsletterOption());
            return createSuccessResponse(getCheckoutSuccessRedirect(false, request), redirectAttr, request, response);
        }
        return createSuccessResponse(getSuccessRedirect(model, request, response, redirectAttr), redirectAttr, request, response);
    }

    private RegistrationType getRegistrationType(String registrationType) {
        return ((registrationType != null && registrationType.equalsIgnoreCase("checkout")) ? RegistrationType.CHECKOUT : RegistrationType.STANDALONE);
    }

    @PostMapping(value = "/updateuserInfo", produces = "application/json")
    public String updateuserInfo(@RequestParam(value = "updateUserInfoJson", required = true) final String updateuserInfoJson, final Model model,
                                 final HttpServletRequest request) throws CMSItemNotFoundException {
        try {
            final UpdateUserProfileForm updateUserProfileForm = new ObjectMapper().readValue(updateuserInfoJson, UpdateUserProfileForm.class);
            final BindingResult bindingResult = new BeanPropertyBindingResult(updateUserProfileForm, "updateUserProfileForm");
            ValidationUtils.invokeValidator(validator, updateUserProfileForm, bindingResult);
            if (bindingResult.hasErrors()) {
                LOG.error("{} {} Binding ERRORS: {}", ErrorLogCode.UPDATE_USER_ERROR, ErrorSource.HYBRIS_BINDING_ERROR, bindingResult.getFieldErrors());
                model.addAttribute("errorMsgKey", "form.global.error");
            } else {
                final CustomerData customerData = new CustomerData();
                customerData.setFunctionCode(updateUserProfileForm.getFunctionCode());
                getCustomerFacade().updateSelectedProfileInformation(customerData);
            }
        } catch (final Exception ioe) {
            DistLogUtils.logError(LOG, "{} {} An error has occur while updating UserProfile object", ioe, ErrorLogCode.UPDATE_USER_ERROR,
                                  ErrorSource.HYBRIS_BINDING_ERROR);
            model.addAttribute("errorMsgKey", "updateUserProfileForm.process.error");
        }
        return ControllerConstants.Views.Fragments.Checkout.UpdateUserProfilePopup;
    }

    @GetMapping(value = "/success")
    public String success(final Model model, final HttpSession session, final HttpServletRequest request) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        final String registeredUserEmail = (String) session.getAttribute(REGISTERED_USER_EMAIL);
        model.addAttribute("registeredEmail", registeredUserEmail);
        model.addAttribute("approvalByCustomerServiceNeeded", session.getAttribute(APPROVAL_BY_CUSTOMER_SERVICE_NEEDED));
        if (registeredUserEmail != null) {
            session.removeAttribute(REGISTERED_USER_EMAIL);
            session.removeAttribute(APPROVAL_BY_CUSTOMER_SERVICE_NEEDED);
            return getRegistrationSuccessPage(model);
        }
        return getDefaultRegistrationPage(model);
    }

    @GetMapping(value = "/existingsuccess")
    public String success(@RequestParam("userType") final String userType, @RequestParam("existing") final String existing, final Model model,
                          final HttpSession session, final HttpServletRequest request) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        final String registeredUserEmail = (String) session.getAttribute(REGISTERED_USER_EMAIL);
        model.addAttribute("registeredEmail", registeredUserEmail);
        model.addAttribute("existing", existing);
        model.addAttribute("approvalByCustomerServiceNeeded", session.getAttribute(APPROVAL_BY_CUSTOMER_SERVICE_NEEDED));
        if ("B2B".equalsIgnoreCase(userType)) {
            model.addAttribute("b2bUser", true);
        }
        if (registeredUserEmail != null) {
            session.removeAttribute(REGISTERED_USER_EMAIL);
            session.removeAttribute(APPROVAL_BY_CUSTOMER_SERVICE_NEEDED);
            return getRegistrationSuccessPage(model);
        }
        return getDefaultRegistrationPage(model);
    }

    @PostMapping(value = "/validateUid")
    public ResponseEntity<?> validateUid(@Valid ValidateUidForm validateUidForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        boolean doesCustomerExist = getCustomerFacade().doesCustomerExistForUid(validateUidForm.getUid());
        return doesCustomerExist ? ResponseEntity.status(HttpStatus.CONFLICT).build() : ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "/validateVat")
    @ResponseBody
    public RegistrationResponse.VatValidationResponse validateVat(@RequestParam("vatNumber") final String vatNumber,
                                                                  @RequestParam("countryCode") final String countryCode) throws WebServiceException {

        boolean validationResult = getVatEUFacade().validateVatNumber(vatNumber, countryCode);

        if (validationResult) {
            return new RegistrationResponse.VatValidationResponse(true, null);
        }

        String errorMessage = messageSource.getMessage("vat.validation.error." + countryCode, new Object[] {}, getI18nService().getCurrentLocale());
        return new RegistrationResponse.VatValidationResponse(false, errorMessage);
    }

    public DistVatEUFacade getVatEUFacade() {
        return distVatEUFacade;
    }

    public void setVatEUFacade(final DistVatEUFacade distVatEUFacade) {
        this.distVatEUFacade = distVatEUFacade;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

    @Override
    protected String redirectToregister() {
        return addFasterizeCacheControlParameter(UrlBasedViewResolver.REDIRECT_URL_PREFIX + REQUEST_MAPPING);
    }

    private String resolveMessage(String messageKey) {
        return messageSource.getMessage(messageKey, new Object[] {}, getI18nService().getCurrentLocale());
    }

    @Override
    public void addGlobalModelAttributes(final Model model, final HttpServletRequest request) {
        super.addGlobalModelAttributes(model, request);
        addForms(model);
    }

    public static class RegistrationResponse {

        private final Collection<ValidationError> validationErrors;

        private final Collection<String> globalErrors;

        private final String redirectTo;

        public RegistrationResponse(String redirectTo) {
            this(Collections.emptyList(), Collections.emptyList(), redirectTo);
        }

        public RegistrationResponse(Collection<String> globalErrors) {
            this(Collections.emptyList(), globalErrors, null);
        }

        public RegistrationResponse(Collection<ValidationError> validationErrors, Collection<String> globalErrors) {
            this(validationErrors, globalErrors, null);
        }

        public RegistrationResponse(Collection<ValidationError> validationErrors, Collection<String> globalErrors, String redirectTo) {
            this.validationErrors = validationErrors;
            this.globalErrors = globalErrors;
            this.redirectTo = redirectTo;
        }

        public Collection<ValidationError> getValidationErrors() {
            return validationErrors;
        }

        public Collection<String> getGlobalErrors() {
            return globalErrors;
        }

        public String getRedirectTo() {
            return redirectTo;
        }

        public static class ValidationError {

            private final String fieldName;

            private final String errorMessage;

            public ValidationError(String fieldName, String errorMessage) {
                this.fieldName = fieldName;
                this.errorMessage = errorMessage;
            }

            public String getFieldName() {
                return fieldName;
            }

            public String getErrorMessage() {
                return errorMessage;
            }
        }

        public static final class VatValidationResponse {

            private final boolean success;

            private final String errorMessage;

            public VatValidationResponse(boolean success, String errorMessage) {
                this.success = success;
                this.errorMessage = errorMessage;
            }

            public boolean isSuccess() {
                return success;
            }

            public String getErrorMessage() {
                return errorMessage;
            }
        }

    }
}
