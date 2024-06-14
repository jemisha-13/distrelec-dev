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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.model.DistB2BRequestQuotationPermissionModel;
import com.namics.distrelec.b2b.core.service.customer.DistCustomerAccountService;
import com.namics.distrelec.b2b.core.util.DistLogUtils;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.b2b.facades.user.data.DistDepartmentData;
import com.namics.distrelec.b2b.facades.user.data.DistFunctionData;
import com.namics.distrelec.b2b.facades.user.data.DistSubUserData;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.AccountBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.forms.EmployeeRegisterB2BForm;
import com.namics.distrelec.b2b.storefront.forms.util.SelectOption;
import com.namics.distrelec.core.v2.forms.EmployeeSearchB2BForm;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.b2bcommercefacades.company.B2BUnitFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commerceservices.customer.CustomerService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * Controller for organization management.
 */
@Controller
@RequestMapping("/my-account/company")
public class CompanyPageController extends AbstractSearchPageController {

    protected static final Logger LOG = LogManager.getLogger(CompanyPageController.class);

    private static final String COMPANY_INFORMATION_CMS_PAGE = "company-information";

    private static final String COMPANY_USER_MANAGEMENT_CMS_PAGE = "company-user-management";

    private static final String REGISTER_EMPLOYEE_CMS_PAGE = "company-user-management-register-employee";

    private static final String CUSTOMER_ID_PATH_VARIABLE_PATTERN = "/{customerId:.*}";

    // Redirects
    private static final String REDIRECT_INFORMATION = addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/my-account/company/information");

    private static final String REDIRECT_USER_MANAGEMENT = addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/my-account/company/user-management");

    protected static final String REDIRECT_USER_DETAILS = addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/my-account/company/edit/employee/");

    // Default password for new sub users
    private static final String DISTRELEC_DEFAULT_PWD = "d1str3l3c_123";

    private static final List<String> USER_SORT_TYPE_LIST = Collections
                                                                       .unmodifiableList(Arrays.asList(new String[] { "byName:asc", "byName:desc",
                                                                                                                      "byStatus:desc", "byYearlyBudget:asc",
                                                                                                                      "byYearlyBudget:desc" }));

    private static final PhoneNumberUtil PHONENUMBERUTIL = PhoneNumberUtil.getInstance();

    public static String phoneNumberToString(final PhoneNumber phoneNumber) {
        return PHONENUMBERUTIL.format(phoneNumber, PhoneNumberFormat.INTERNATIONAL);
    }

    @Autowired
    private DistCustomerAccountService distCustomerAccountService;

    @Autowired
    private DistUserFacade userFacade;

    @Autowired
    private B2BUnitFacade b2bUnitFacade;

    @Autowired
    @Qualifier("accountBreadcrumbBuilder")
    private AccountBreadcrumbBuilder accountBreadcrumbBuilder;

    @Autowired
    @Qualifier("b2bCheckoutFacade")
    private DistCheckoutFacade checkoutFacade;

    @Autowired
    private DistCustomerFacade distCustomerFacade;

    @Autowired
    @Qualifier("b2BCustomerConverter")
    private Converter<B2BCustomerModel, CustomerData> b2bCustomerConverter;

    @Autowired
    private CustomerService customerService;

    @ModelAttribute("titles")
    public Collection<TitleData> getTitles() {
        return getUserFacade().getTitles();
    }

    @ModelAttribute("departments")
    public Collection<DistDepartmentData> getDepartments() {
        return getUserFacade().getDepartments();
    }

    @ModelAttribute("functions")
    public Collection<DistFunctionData> getFunctions() {
        return getUserFacade().getFunctions();
    }

    @ModelAttribute("countries")
    public Collection<CountryData> getCountries() {
        return getCheckoutFacade().getDeliveryCountriesAndRegions();
    }

    @ModelAttribute("filterStates")
    public List<SelectOption> getListFilterStates() {
        final List<SelectOption> FILTER_STATES = new ArrayList<>();
        if (FILTER_STATES.isEmpty()) {
            FILTER_STATES.add(new SelectOption(DistConstants.Company.USER_CONFIRMATION_PENDING,
                                               getMessageSource().getMessage("text.account.status.confirmationpending", null,
                                                                             getI18nService().getCurrentLocale())));
            FILTER_STATES.add(new SelectOption(DistConstants.Company.ACTIVE,
                                               getMessageSource().getMessage("text.account.status.active", null, getI18nService().getCurrentLocale())));
            // FILTER_STATES.add(new SelectOption(DistConstants.Company.DEACTIVATED,
            // getMessageSource().getMessage("text.account.status.deactivated", null,
            // getI18nService().getCurrentLocale())));
        }
        return FILTER_STATES;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String company() throws CMSItemNotFoundException {
        return REDIRECT_INFORMATION;
    }

    @RequestMapping(value = { "/information" }, method = RequestMethod.GET)
    public String myCompany(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        String currentUserId = getUserService().getCurrentUser().getUid();
        LOG.debug("Getting Account details for user" + currentUserId);
        storeCmsPageInModel(model, getContentPageForLabelOrId(COMPANY_INFORMATION_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(COMPANY_INFORMATION_CMS_PAGE));
        LOG.debug("Fetching parent Unit" + currentUserId);
        final B2BUnitData unitData = getB2bUnitFacade().getParentUnit();
        model.addAttribute("companyInformationVatId", unitData.getVatID());

        final String erpCustomerId = unitData.getErpCustomerId();
        model.addAttribute("companyInformationCustomerId", erpCustomerId);
        LOG.debug("Get Unit Data details" + currentUserId);
        // Distrelec 3476 : get the right Address for the department,
        if (unitData.getAddresses() != null && unitData.getAddresses().size() > 0) {
            final CustomerType unitType = ((B2BCustomerModel) getUserService().getCurrentUser()).getDefaultB2BUnit().getCustomerType();

            boolean addressFound = false;
            for (final AddressData addressData : unitData.getAddresses()) {
                if (addressData.isBillingAddress()) {

                    // for B2B: billing address of the unit, there is only one
                    // Distrelec 3538 : for B2B Key Account: billing address with same id as company id.
                    if (CustomerType.B2B == unitType || CustomerType.B2B_KEY_ACCOUNT == unitType && erpCustomerId != null
                            && erpCustomerId.equals((addressData).getErpAddressID())) {
                        addressFound = true;
                        model.addAttribute("companyInformationAdr", addressData);
                        break;
                    }
                }
            }
            // If non was found, use the first one.
            if (!addressFound) {
                model.addAttribute("companyInformationAdr", unitData.getAddresses().get(0));
            }
        }
        LOG.debug("Add company Info" + currentUserId);
        addCompanyInfo(model);
        model.addAttribute("breadcrumbs", getAccountBreadcrumbBuilder().getBreadcrumbs("text.account.accountDetails", "text.account.company.information"));
        LOG.debug("Add Global Attributes" + currentUserId);
        addGlobalModelAttributes(model, request);
        LOG.debug("Add Global Attributes Done" + currentUserId);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        LOG.debug("Data Populated. Returning from controller" + currentUserId);
        return ControllerConstants.Views.Pages.Company.CompanyInformationPage;
    }

    private void addCompanyInfo(Model model) {
        final B2BCustomerModel currentCustomer = (B2BCustomerModel) getUserService().getCurrentUser();
        final B2BUnitModel unit = currentCustomer.getDefaultB2BUnit();
        model.addAttribute("companyName", unit.getName());
        model.addAttribute("companyName2", unit.getCompanyName2());
        model.addAttribute("companyName3", unit.getCompanyName3());
    }

    @RequestMapping(value = "/user-management", method = { RequestMethod.GET, RequestMethod.POST })
    public String organizationManagement(@Valid final EmployeeSearchB2BForm form, final Model model,
                                         final HttpServletRequest request) throws CMSItemNotFoundException {

        handleSort(form, model);
        storeCmsPageInModel(model, getContentPageForLabelOrId(COMPANY_USER_MANAGEMENT_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(COMPANY_USER_MANAGEMENT_CMS_PAGE));
        model.addAttribute("breadcrumbs", getAccountBreadcrumbBuilder().getBreadcrumbs("text.account.accountDetails", "text.account.company.userManagement"));
        model.addAttribute("b2bCustomers", getCustomerFacade().searchEmployees(form.getName(), form.getStateCode(), form.getSort()));
        model.addAttribute("sortCode", form.getSort());
        model.addAttribute("sortTypeList", USER_SORT_TYPE_LIST);
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return ControllerConstants.Views.Pages.Company.UserManagementPage;
    }

    @RequestMapping(value = "/create/newemployee", method = RequestMethod.GET)
    public String registerNewEmployee(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {

        model.addAttribute(new EmployeeRegisterB2BForm());
        model.addAttribute("budgetCurrency", getStoreSessionFacade().getCurrentCurrency().getIsocode());
        storeCmsPageInModel(model, getContentPageForLabelOrId(REGISTER_EMPLOYEE_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(REGISTER_EMPLOYEE_CMS_PAGE));
        model.addAttribute("breadcrumbs", getAccountBreadcrumbBuilder().getBreadcrumbs("text.account.accountDetails", "text.account.company.userManagement"));
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return ControllerConstants.Views.Pages.Company.RegisterNewEmployee;

    }

    @RequestMapping(value = "/create/newemployee", method = RequestMethod.POST)
    public String registerNewEmployee(@Valid final EmployeeRegisterB2BForm form, final BindingResult bindingResult, final Model model,
                                      final RedirectAttributes redirectModel,
                                      final HttpServletRequest request) throws CMSItemNotFoundException, NumberParseException {

        if (!StringUtils.isBlank(form.getPhoneNumber())) {
            final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(form.getPhoneNumber(), getCurrentCountry().getIsocode());
            form.setPhoneNumber(phoneNumberToString(phoneNumber));
        }
        if (!StringUtils.isBlank(form.getMobileNumber())) {
            final PhoneNumber mobileNumber = PHONENUMBERUTIL.parse(form.getMobileNumber(), getCurrentCountry().getIsocode());
            form.setMobileNumber(phoneNumberToString(mobileNumber));
        }

        validateBudget(form, bindingResult);
        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            addGlobalModelAttributes(model, request);
            return handleRegistrationError(model);
        }

        final DistSubUserData data = new DistSubUserData();
        data.setTitleCode(form.getTitleCode());
        data.setFirstName(form.getFirstName());
        data.setLastName(form.getLastName());
        data.setLogin(form.getEmail());
        data.setPassword(DISTRELEC_DEFAULT_PWD);
        data.setPhoneNumber(form.getPhoneNumber());
        data.setMobileNumber(form.getMobileNumber());
        data.setFaxNumber(form.getFaxNumber());
        data.setFunctionCode(form.getFunctionCode());
        data.setDepartmentCode(form.getDepartmentCode());

        data.setBudgetWithoutLimit(form.isBudgetWithoutLimit());
        data.setBudgetPerOrder(form.getBudgetPerOrder());
        data.setYearlyBudget(form.getYearlyBudget());
        data.setResidualBudget(form.getResidualBudget());

        data.setRequestQuotationPermission(form.isRequestQuotationPermission());

        try {
            getCustomerFacade().createB2BEmployee(data);
        } catch (final DuplicateUidException e) {
            logError(LOG, "{} {} Registration failed due to Duplicate UID(Email): {}", e, ErrorLogCode.REGISTRATION_ERROR, ErrorSource.DB, data.getEmail());
            bindingResult.rejectValue("email", "registration.error.account.exists.title");
            GlobalMessages.addErrorMessage(model, "form.global.error");
            addGlobalModelAttributes(model, request);
            return handleRegistrationError(model);
        } catch (final Exception e) {
            logError(LOG, "{} Registration failed for UserId: {} ", e, ErrorLogCode.REGISTRATION_ERROR, data.getEmail());
        }
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, Collections.singletonList("account.confirmation.employee.register"));
        return REDIRECT_USER_MANAGEMENT;
    }

    @RequestMapping(value = "/edit/employee" + CUSTOMER_ID_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    public String editEmployee(final Model model, @PathVariable("customerId") final String customerId, final RedirectAttributes redirectModel,
                               final HttpServletRequest request) {
        try {
            final B2BCustomerModel customer = (B2BCustomerModel) customerService.getCustomerByCustomerId(customerId);
            final String customerUid = customer.getUid();
            final B2BCustomerModel currentUser = (B2BCustomerModel) getUserService().getCurrentUser();
            if (!currentUser.getDefaultB2BUnit().equals(customer.getDefaultB2BUnit())) {
                throw new AccessDeniedException("The user " + currentUser.getUid() + " is not allowed to modify the user " + customer.getUid()
                                                + " because it is not a sub user of this company.");
            }

            final CustomerData customerData = b2bCustomerConverter.convert(customer);
            final EmployeeRegisterB2BForm form = new EmployeeRegisterB2BForm();
            form.setTitleCode(customerData.getTitleCode());
            form.setFirstName(customerData.getFirstName());
            form.setLastName(customerData.getLastName());
            form.setEmail(customer.getEmail());
            form.setFunctionCode(customerData.getFunctionCode());
            if (customerData.getContactAddress() != null) {
                form.setMobileNumber(customerData.getContactAddress().getCellphone());
                form.setPhoneNumber(customerData.getContactAddress().getPhone());
                form.setFaxNumber(customerData.getContactAddress().getFax());
                form.setDepartmentCode(customerData.getContactAddress().getDepartmentCode());
            }

            // Budget Data
            if (customerData.getBudget() != null) {
                form.setBudgetWithoutLimit(false);
                form.setBudgetPerOrder(customerData.getBudget().getOrderBudget());
                form.setYearlyBudget(customerData.getBudget().getOriginalYearlyBudget());
                form.setResidualBudget(customerData.getBudget().getYearlyBudget());
                model.addAttribute("budgetCurrency", customerData.getBudget().getCurrency().getIsocode());
                model.addAttribute("residualBudget", customerData.getBudget().getYearlyBudget());
            } else {
                form.setBudgetWithoutLimit(true);
                model.addAttribute("budgetCurrency", getStoreSessionFacade().getCurrentCurrency().getIsocode());
            }

            // Permission Data
            if (CollectionUtils.isNotEmpty(customerData.getPermissions())) {
                for (final B2BPermissionData permission : customerData.getPermissions()) {
                    if (DistB2BRequestQuotationPermissionModel._TYPECODE.equals(permission.getB2BPermissionTypeData().getCode()) && permission.isActive()) {
                        form.setRequestQuotationPermission(true);
                    }
                }
            }

            model.addAttribute("employeeRegisterB2BForm", form);
            model.addAttribute("employeeUid", customerUid);
            model.addAttribute("employeeCustomerId", customer.getCustomerID());
            boolean employeeStatus = false;
            if (customer.getActive().booleanValue()) {
                employeeStatus = true;
            }
            model.addAttribute("employeeStatus", Boolean.valueOf(employeeStatus));
            storeCmsPageInModel(model, getContentPageForLabelOrId(REGISTER_EMPLOYEE_CMS_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(REGISTER_EMPLOYEE_CMS_PAGE));
            model.addAttribute("breadcrumbs",
                               getAccountBreadcrumbBuilder().getBreadcrumbs("text.account.accountDetails", "text.account.company.userManagement"));
            addGlobalModelAttributes(model, request);
            final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
            getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
            return ControllerConstants.Views.Pages.Company.EditEmployeePage;
        } catch (final UnknownIdentifierException e) {
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("account.company.user.notfound"));
            return REDIRECT_USER_MANAGEMENT;
        } catch (final IllegalArgumentException e) {
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("account.company.user.notfound"));
            return REDIRECT_USER_MANAGEMENT;
        } catch (final AccessDeniedException e) {
            LOG.warn(e);
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("account.company.user.notfound"));
            return REDIRECT_USER_MANAGEMENT;
        } catch (final Exception e) {
            LOG.error(e);
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("account.company.user.global.load.error"));
            return REDIRECT_USER_MANAGEMENT;
        }
    }

    @RequestMapping(value = "/edit/employee" + CUSTOMER_ID_PATH_VARIABLE_PATTERN, method = RequestMethod.POST)
    public String editEmployee(@Valid final EmployeeRegisterB2BForm form, final BindingResult bindingResult, final Model model,
                               @PathVariable("customerId") final String customerId, final RedirectAttributes redirectModel,
                               final HttpServletRequest request) {
        try {
            if (!StringUtils.isBlank(form.getPhoneNumber())) {
                final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(form.getPhoneNumber(), getCurrentCountry().getIsocode());
                form.setPhoneNumber(phoneNumberToString(phoneNumber));
            }
            if (!StringUtils.isBlank(form.getMobileNumber())) {
                final PhoneNumber mobileNumber = PHONENUMBERUTIL.parse(form.getMobileNumber(), getCurrentCountry().getIsocode());
                form.setMobileNumber(phoneNumberToString(mobileNumber));
            }

            final B2BCustomerModel customer = (B2BCustomerModel) customerService.getCustomerByCustomerId(customerId);
            final B2BCustomerModel currentUser = (B2BCustomerModel) getUserService().getCurrentUser();
            if (!currentUser.getDefaultB2BUnit().equals(customer.getDefaultB2BUnit())) {
                throw new AccessDeniedException("The user " + currentUser.getUid() + " is not allowed to modify the user " + customer.getUid()
                                                + " because it is not a sub user of this company.");
            }

            validateBudget(form, bindingResult);
            if (bindingResult.hasErrors()) {
                GlobalMessages.addErrorMessage(model, "form.global.error");
                addGlobalModelAttributes(model, request);
                return handleRegistrationError(model);
            }

            final DistSubUserData data = new DistSubUserData();
            data.setTitleCode(form.getTitleCode());
            data.setFirstName(form.getFirstName());
            data.setLastName(form.getLastName());
            data.setEmail(form.getEmail());
            data.setPhoneNumber(form.getPhoneNumber());
            data.setMobileNumber(form.getMobileNumber());
            data.setFaxNumber(form.getFaxNumber());
            data.setFunctionCode(form.getFunctionCode());
            data.setBudgetWithoutLimit(form.isBudgetWithoutLimit());
            data.setDepartmentCode(form.getDepartmentCode());
            data.setBudgetPerOrder(form.getBudgetPerOrder());
            data.setYearlyBudget(form.getYearlyBudget());
            data.setRequestQuotationPermission(form.isRequestQuotationPermission());

            try {
                getCustomerFacade().updateB2BEmployee(data, customer);
            } catch (final DuplicateUidException e) {
                DistLogUtils.logError(LOG, "{} {} Registration failed due to Duplicate UID(Email): {}", e, ErrorLogCode.REGISTRATION_ERROR, ErrorSource.HYBRIS,
                                      data.getEmail());
                bindingResult.rejectValue("email", "registration.error.account.exists.title");
                GlobalMessages.addErrorMessage(model, "form.global.error");
                addGlobalModelAttributes(model, request);
                return handleRegistrationError(model);
            }
            final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
            getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
            redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, Collections.singletonList("account.confirmation.employee.edited"));
            return REDIRECT_USER_MANAGEMENT;
        } catch (final UnknownIdentifierException e) {
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("account.company.user.notfound"));
            return REDIRECT_USER_MANAGEMENT;
        } catch (final IllegalArgumentException e) {
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("account.company.user.notfound"));
            return REDIRECT_USER_MANAGEMENT;
        } catch (final AccessDeniedException e) {
            LOG.warn(e);
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("account.company.user.notfound"));
            return REDIRECT_USER_MANAGEMENT;
        } catch (final Exception e) {
            LOG.error(e);
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("account.company.user.global.save.error"));
            return REDIRECT_USER_MANAGEMENT;
        }
    }

    @RequestMapping(value = "/resend/activation" + CUSTOMER_ID_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    public String resendActivationEmail(@PathVariable("customerId") final String customerId,
                                        final RedirectAttributes redirectModel) {
        try {
            B2BCustomerModel customer = (B2BCustomerModel) customerService.getCustomerByCustomerId(customerId);
            String customerUid = customer.getUid();
            getCustomerFacade().resendSetInitialPasswordEmail(customerUid);
            redirectModel.addFlashAttribute(GlobalMessages.INFO_MESSAGES_HOLDER, Collections.singletonList("account.confirmation.employee.resendactivation"));
        } catch (final DuplicateUidException e) {
            DistLogUtils.logWarn(LOG, "{} {} Resending activation email failed for CustomerID: {}", e, ErrorLogCode.EMAIL_ERROR, ErrorSource.HYBRIS,
                                 customerId);
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("form.global.error"));
        }
        return buildRedirectionUrl(customerId);
    }

    private String buildRedirectionUrl(final String customerId) {
        String character = StringUtils.EMPTY;
        if (REDIRECT_USER_DETAILS.contains(QUESTION_MARK)) {
            character = QUESTION_MARK;
        } else if (REDIRECT_USER_DETAILS.contains(AMPERSAND)) {
            character = AMPERSAND;
        }
        return chompTo(REDIRECT_USER_DETAILS, character) + customerId + character + NO_CACHE_EQUALS_TRUE;
    }

    private String chompTo(final String text, final String character) {
        return text.contains(character) ? StringUtils.left(text, text.indexOf(character)) : text;
    }

    @RequestMapping(value = "/deactivate/user" + CUSTOMER_ID_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    public String deactivateUser(@PathVariable("customerId") final String customerId, final RedirectAttributes redirectModel) {
        B2BCustomerModel customer = (B2BCustomerModel) customerService.getCustomerByCustomerId(customerId);
        String customerUid = customer.getUid();
        if (!getDistCustomerAccountService().deactivateCustomer(customerUid)) {
            redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER,
                                            Collections.singletonList("account.confirmation.employee.deactivatingnotsuccessful"));
        } else {
            redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, Collections.singletonList("account.confirmation.employee.deactivated"));
        }

        return REDIRECT_USER_MANAGEMENT;
    }

    @RequestMapping(value = "/delete/user" + CUSTOMER_ID_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    public String deleteUser(@PathVariable("customerId") final String customerId, final RedirectAttributes redirectModel) {
        B2BCustomerModel customer = (B2BCustomerModel) customerService.getCustomerByCustomerId(customerId);
        String customerUid = customer.getUid();
        if (!distCustomerFacade.deleteSubUser(customerUid)) {
            redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER,
                                            Collections.singletonList("account.confirmation.employee.deletingnotsuccessful"));
            // account.confirmation.employee.deactivatingnotsuccessful
        } else {
            redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, Collections.singletonList("account.confirmation.employee.deleted"));
        }

        return REDIRECT_USER_MANAGEMENT;
    }

    protected void validateBudget(final EmployeeRegisterB2BForm form, final BindingResult bindingResult) {
        if (!form.isBudgetWithoutLimit()) {
            final BigDecimal perOrder = form.getBudgetPerOrder();
            final BigDecimal yearly = form.getYearlyBudget();
            if (perOrder == null && yearly == null) {
                bindingResult.rejectValue("budgetPerOrder", "registration.error.budget.null");
                bindingResult.rejectValue("yearlyBudget", "registration.error.budget.null");
            }
        }
    }

    protected String handleRegistrationError(final Model model) throws CMSItemNotFoundException {
        storeCmsPageInModel(model, getContentPageForLabelOrId(REGISTER_EMPLOYEE_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(REGISTER_EMPLOYEE_CMS_PAGE));
        model.addAttribute("breadcrumbs", getAccountBreadcrumbBuilder().getBreadcrumbs("text.account.accountDetails", "text.account.company.userManagement"));
        return ControllerConstants.Views.Pages.Company.RegisterNewEmployee;
    }

    protected void handleSort(final EmployeeSearchB2BForm form, final Model model) {
        if (form.getSort() != null) {
            if (!USER_SORT_TYPE_LIST.contains(form.getSort())) {
                form.setSort("byName:asc");
            }
        } else {
            form.setSort("byName:asc");
        }

        model.addAttribute("employeeSearchB2BForm", form);
    }

    @InitBinder
    protected void initBinder(final HttpServletRequest request, final ServletRequestDataBinder binder) {
        final CustomNumberEditor numberEditor = new CustomNumberEditor(BigDecimal.class, true);
        binder.registerCustomEditor(BigDecimal.class, numberEditor);
    }

    public DistCustomerAccountService getDistCustomerAccountService() {
        return distCustomerAccountService;
    }

    public void setDistCustomerAccountService(final DistCustomerAccountService distCustomerAccountService) {
        this.distCustomerAccountService = distCustomerAccountService;
    }

    public DistUserFacade getUserFacade() {
        return userFacade;
    }

    public void setUserFacade(final DistUserFacade userFacade) {
        this.userFacade = userFacade;
    }

    public AccountBreadcrumbBuilder getAccountBreadcrumbBuilder() {
        return accountBreadcrumbBuilder;
    }

    public void setAccountBreadcrumbBuilder(final AccountBreadcrumbBuilder accountBreadcrumbBuilder) {
        this.accountBreadcrumbBuilder = accountBreadcrumbBuilder;
    }

    public DistCheckoutFacade getCheckoutFacade() {
        return checkoutFacade;
    }

    public void setCheckoutFacade(final DistCheckoutFacade checkoutFacade) {
        this.checkoutFacade = checkoutFacade;
    }

    public B2BUnitFacade getB2bUnitFacade() {
        return b2bUnitFacade;
    }

    public void setB2bUnitFacade(final B2BUnitFacade b2bUnitFacade) {
        this.b2bUnitFacade = b2bUnitFacade;
    }

}
