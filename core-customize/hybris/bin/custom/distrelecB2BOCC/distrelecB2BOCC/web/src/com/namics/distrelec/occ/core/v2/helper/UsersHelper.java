package com.namics.distrelec.occ.core.v2.helper;

import static java.util.Objects.nonNull;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.namics.distrelec.b2b.core.bloomreach.enums.BloomreachTouchpoint;
import com.namics.distrelec.b2b.core.bloomreach.exception.DistBloomreachBatchException;
import com.namics.distrelec.b2b.core.bloomreach.exception.DistBloomreachExportException;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.exception.ErpCommunicationException;
import com.namics.distrelec.b2b.core.security.SanitizationService;
import com.namics.distrelec.b2b.facades.bloomreach.DistBloomreachFacade;
import com.namics.distrelec.b2b.facades.order.DistB2BOrderFacade;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.payment.ws.dto.DistPaymentOptionsWsDTO;
import com.namics.distrelec.b2b.facades.phone.DistPhoneNumberFacade;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.b2b.facades.user.data.DistConsentData;
import com.namics.distrelec.b2b.facades.user.data.DistDepartmentData;
import com.namics.distrelec.b2b.facades.user.data.DistFunctionData;
import com.namics.distrelec.b2b.facades.user.data.DistMarketingConsentData;
import com.namics.distrelec.b2b.facades.user.data.DistSubUserData;
import com.namics.distrelec.b2b.facades.user.ws.dto.RegisterB2BEmployeeWsDTO;
import com.namics.distrelec.b2b.core.model.DistB2BRequestQuotationPermissionModel;
import com.namics.distrelec.occ.core.order.ws.dto.OrderApprovalDecisionWsDTO;
import com.namics.distrelec.occ.core.v2.forms.OrderApprovalDecisionForm;
import com.namics.distrelec.occ.core.v2.forms.OrderApprovalForm;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData;
import de.hybris.platform.b2bwebservicescommons.dto.company.B2BOrderApprovalsData;
import de.hybris.platform.cmsoccaddon.mapping.converters.DataToWsConverter;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.exceptions.PasswordMismatchException;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentModeListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentModeWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.oauth2.token.OAuthRevokeTokenService;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.exceptions.WorkflowActionDecideException;

/**
 * Utility or helper class for user related.
 */
@Component
public class UsersHelper extends AbstractHelper {
    private static final String CREDIT_CARD_PAYMENT_MODE = "CreditCard";

    private static final List<String> ORDER_HISTORY_SORT_TYPE_LIST_MIN = List.of("byDate", "byStatus", "byTotalPrice");

    private static final Logger LOG = LoggerFactory.getLogger(UsersHelper.class);

    @Autowired
    private DistUserFacade userFacade;

    @Autowired
    @Qualifier("b2bOrderFacade")
    private DistB2BOrderFacade orderFacade;

    @Autowired
    private UserService userService;

    @Autowired
    private I18NService i18nService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    @Qualifier("b2BCustomerConverter")
    private Converter<B2BCustomerModel, CustomerData> b2bCustomerConverter;

    @Autowired
    @Qualifier("customerDataToWsDTOConverter")
    private DataToWsConverter<CustomerData, UserWsDTO> customerDataToWsDTOConverter;

    @Autowired
    private OAuthRevokeTokenService oauthRevokeTokenService;

    @Autowired
    private DistPhoneNumberFacade distPhoneNumberFacade;

    @Autowired
    private SanitizationService sanitizationService;

    @Autowired
    private DistBloomreachFacade distBloomreachFacade;

    public List<CCPaymentInfoData> fetchCCPaymentInfosForCurrentUser(List<DistPaymentModeData> paymentModes) {
        List<CCPaymentInfoData> ccPaymentInfoDataList = new ArrayList<>();
        CustomerData customerData = getUser();
        if (StringUtils.isEmpty(customerData.getDefaultPaymentMode())) {
            if (CREDIT_CARD_PAYMENT_MODE.equalsIgnoreCase(getDefaultPaymentMode(paymentModes))) {
                setDefaultPaymentInfo(customerData.getCcPaymentInfos());
            }
        }
        if (CollectionUtils.isNotEmpty(customerData.getCcPaymentInfos())) {
            ccPaymentInfoDataList.addAll(customerData.getCcPaymentInfos());
        }
        return ccPaymentInfoDataList;
    }

    private void setDefaultPaymentInfo(List<CCPaymentInfoData> ccPaymentInfos) {
        if (CollectionUtils.isNotEmpty(ccPaymentInfos)) {
            if (ccPaymentInfos.size() == 1) {
                (ccPaymentInfos.iterator().next()).setDefaultPaymentInfo(true);
            }
        }
    }

    public void setDefaultPaymentMode(List<DistPaymentModeData> paymentModes) {
        if (CollectionUtils.isNotEmpty(paymentModes) && CollectionUtils.size(paymentModes) == 1) {
            paymentModes.iterator().next().setDefaultPaymentMode(true);
        }
    }

    private String getDefaultPaymentMode(List<DistPaymentModeData> paymentModes) {
        if (CollectionUtils.isNotEmpty(paymentModes)) {
            for (DistPaymentModeData paymentMode : paymentModes) {
                if (BooleanUtils.isTrue(paymentMode.getDefaultPaymentMode())) {
                    return paymentMode.getCode();
                }
            }
        }
        return null;
    }

    public void setInvoicePaymentModeAttributes(DistPaymentOptionsWsDTO distPaymentOptionsWsDTO) {
        distPaymentOptionsWsDTO.setCanRequestInvoicePaymentMode(userFacade.canRequestInvoicePaymentMode());
        distPaymentOptionsWsDTO.setInvoicePaymentModeRequested(userFacade.isInvoicePaymentModeRequested());
    }

    public List<DistDepartmentData> fetchUserDepartments() {
        return userFacade.getDepartments();

    }

    public List<DistFunctionData> fetchUserFunctions() {
        return userFacade.getFunctions();
    }

    /**
     * Find the order approval requests.
     *
     * @param orderApprovalForm
     *            the form parameters to use for query filter
     * @param wfActionTypes
     *            the array of {@code WorkflowActionType}
     * @param wfActionStatus
     *            the array of {@code WorkflowActionStatus}
     */
    public B2BOrderApprovalsData getOrderApprovals(final OrderApprovalForm orderApprovalForm, final WorkflowActionType[] wfActionTypes,
                                                   final WorkflowActionStatus[] wfActionStatus) {
        if (nonNull(orderApprovalForm.getSort())) {
            final String[] tab = orderApprovalForm.getSort().split(":");
            if (tab.length > 1) {
                orderApprovalForm.setSort(tab[0]);
            }

            if (tab.length >= 2) {
                if ("asc".equalsIgnoreCase(tab[1]) || "desc".equalsIgnoreCase(tab[1])) {
                    orderApprovalForm.setSortType(tab[1].toUpperCase());
                }
            }

            if (!ORDER_HISTORY_SORT_TYPE_LIST_MIN.contains(orderApprovalForm.getSort())) {
                orderApprovalForm.setSort("byDate");
            }
        } else {
            orderApprovalForm.setSort("byDate");
        }
        final PageableData pageableData = createPageableData(orderApprovalForm.getPage(), orderApprovalForm.getPageSize(), orderApprovalForm.getSort(),
                                                             orderApprovalForm.getShow(), orderApprovalForm.getSortType());
        return createOrderApprovalsList(orderFacade.getPagedOrdersForApproval(wfActionTypes, wfActionStatus, pageableData));
    }

    protected B2BOrderApprovalsData createOrderApprovalsList(final SearchPageData<B2BOrderApprovalData> result) {
        final B2BOrderApprovalsData approvalsData = new B2BOrderApprovalsData();

        approvalsData.setOrderApprovals(result.getResults());
        approvalsData.setSorts(result.getSorts());
        approvalsData.setPagination(result.getPagination());

        return approvalsData;
    }

    public B2BOrderApprovalData getOrderApprovalDetails(final String workflowActionCode) {
        try {
            return orderFacade.getOrderApprovalDetailsForCode(workflowActionCode);
        } catch (final UnknownIdentifierException e) {
            LOG.warn("Attempted to load an order approval that does not exist or is not visible!", e);
            throw new UnknownIdentifierException(getErrorMessage("text.account.orderApproval.canNotLoad"));
        }
    }

    public void updateLogin(final String newLogin, final String password, final Errors errors) {
        try {
            final boolean isValidEmail = EmailValidator.getInstance().isValid(newLogin);
            getB2bCustomerFacade().changeUid(newLogin.toLowerCase(new Locale(getStoreSessionFacade().getCurrentLanguage().getIsocode())), password);

            final CustomerData customerData = getB2bCustomerFacade().getCurrentCustomer();
            customerData.setDisplayUid(newLogin);
            if (isValidEmail) {
                customerData.setEmail(newLogin);
                if (nonNull(customerData.getContactAddress())) {
                    customerData.getContactAddress().setEmail(newLogin);
                }
            }

            getB2bCustomerFacade().updateProfile(customerData);
            final String newUid = customerData.getUid().toLowerCase();
            final Authentication oldAuthentication = SecurityContextHolder.getContext().getAuthentication();
            final UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(newUid, null,
                                                                                                                  oldAuthentication.getAuthorities());
            newAuthentication.setDetails(oldAuthentication.getDetails());
            SecurityContextHolder.getContext().setAuthentication(newAuthentication);
            oauthRevokeTokenService.revokeUserAccessTokens(customerData.getUid(), List.of());

        } catch (final DuplicateUidException e) {
            errors.rejectValue("newLogin", "text.account.loginData.emailNotChanged", new Object[] {}, "Your email was not updated");
            throw new WebserviceValidationException(errors);
        } catch (final PasswordMismatchException passwordMismatchException) {
            errors.rejectValue("password", "profile.currentPassword.invalid", new Object[] {}, "Please enter your current password");
            throw new WebserviceValidationException(errors);
        }
    }

    public void updateProfile(final UserWsDTO user, final CustomerData customerData) {
        if (nonNull(user.getContactAddress())) {
            parseContactNumbers(user.getContactAddress());
        }

        if (nonNull(user.getTitleCode())) {
            customerData.setTitleCode(user.getTitleCode());
        }
        if (nonNull(user.getFirstName())) {
            customerData.setFirstName(user.getFirstName());
        }
        if (nonNull(user.getLastName())) {
            customerData.setLastName(user.getLastName());
        }
        if (nonNull(user.getFunctionCode())) {
            customerData.setFunctionCode(user.getFunctionCode());
        }

        if (nonNull(customerData.getContactAddress())) {
            final AddressData contactAddress = customerData.getContactAddress();
            fromDataUpdate(user, customerData, contactAddress);
        } else {
            final AddressData contactAddress = new AddressData();
            fromDataUpdate(user, customerData, contactAddress);
        }
        try {
            getB2bCustomerFacade().updateProfile(customerData);
        } catch (final DuplicateUidException e) {
            LOG.error("{} {} Can not update user profile due to Duplicate UID(Email): {}", e, DistConstants.ErrorSource.DB,
                      customerData.getEmail());
        } catch (final ErpCommunicationException e) {
            LOG.error("{} {} Can not update user profile because of an ERP communication error for the customer with Email: {}", e,
                      DistConstants.ErrorSource.SAP_FAULT, customerData.getEmail());

        } catch (final Exception e) {
            LOG.error("{} Can not update user profile for the customer with Email: {}", e,
                      customerData.getEmail());
        }
    }

    private void fromDataUpdate(final UserWsDTO user, final CustomerData customerData, final AddressData contactAddress) {
        if (nonNull(user.getFirstName())) {
            contactAddress.setFirstName(user.getFirstName());
        }
        if (nonNull(user.getLastName())) {
            contactAddress.setLastName(user.getLastName());
        }
        if (nonNull(user.getTitleCode())) {
            contactAddress.setTitleCode(user.getTitleCode());
        }
        if (nonNull(user.getContactAddress())) {
            contactAddress.setDepartmentCode(user.getContactAddress().getDepartmentCode());
            contactAddress.setPhone(user.getContactAddress().getPhone());
            contactAddress.setCellphone(user.getContactAddress().getCellphone());
            contactAddress.setFax(user.getContactAddress().getFax());
            customerData.setContactAddress(contactAddress);
        }
    }

    public UserWsDTO getUser(final String fields) {
        final CustomerData customerData = getB2bCustomerFacade().getCurrentCustomer();
        UserWsDTO userWsDTO = customerDataToWsDTOConverter.convert(customerData, fields);
        setCustomerType(userWsDTO, customerData);
        setBaseSite(userWsDTO);
        setCustomerChannel(userWsDTO, customerData);
        return userWsDTO;
    }

    private void setCustomerType(UserWsDTO userWsDTO, CustomerData customerData) {
        if (nonNull(customerData.getCustomerType())) {
            userWsDTO.setCustomerType(customerData.getCustomerType().getCode());
        }
    }

    private void setCustomerChannel(UserWsDTO userWsDTO, CustomerData customerData) {
        if (CustomerType.GUEST.equals(customerData.getCustomerType()) || CustomerType.B2C.equals(customerData.getCustomerType())) {
            userWsDTO.setDerivedChannel(CustomerType.B2C.getCode());
        } else {
            userWsDTO.setDerivedChannel(CustomerType.B2B.getCode());
        }
    }

    private void setBaseSite(UserWsDTO userWsDTO) {
        B2BCustomerModel userModel = (B2BCustomerModel) userService.getCurrentUser();
        if (nonNull(userModel) && nonNull(userModel.getCustomersBaseSite())) {
            userWsDTO.setCustomersBaseSite(userModel.getCustomersBaseSite().getUid());
        }
    }

    public void setQuotationPermission(final UserWsDTO user, final CustomerData customerData) {
        CollectionUtils.emptyIfNull(customerData.getPermissions()).stream()
                       .filter(permission -> DistB2BRequestQuotationPermissionModel._TYPECODE.equals(permission.getB2BPermissionTypeData().getCode()) && permission.isActive())
                       .findFirst()
                       .ifPresent(permission -> user.setRequestQuotationPermission(true));
    }

    public void registerNewEmployee(final RegisterB2BEmployeeWsDTO registerB2BEmployeeWsDTO, final Errors errors) throws Exception {
        parsePhoneNumbers(registerB2BEmployeeWsDTO);
        final DistSubUserData data = populateSubUserData(registerB2BEmployeeWsDTO);
        data.setLogin(registerB2BEmployeeWsDTO.getEmail());
        data.setPassword(generateRandomPassword());
        data.setResidualBudget(registerB2BEmployeeWsDTO.getResidualBudget());

        try {
            getB2bCustomerFacade().createB2BEmployee(data);
        } catch (final DuplicateUidException e) {
            LOG.error("Registration failed due to Duplicate UID(Email): {}", data.getEmail(), e);
            errors.rejectValue("email", "registration.error.account.exists.title");
            throw new WebserviceValidationException(errors);
        } catch (final Exception e) {
            LOG.error("Registration failed for UserId: {} ", data.getEmail(), e);
            throw new Exception(getErrorMessage("form.global.error"));
        }
    }

    private DistSubUserData populateSubUserData(final RegisterB2BEmployeeWsDTO registerB2BEmployeeWsDTO) {
        DistSubUserData data = new DistSubUserData();
        data.setTitleCode(registerB2BEmployeeWsDTO.getTitleCode());
        data.setFirstName(sanitizationService.removePeriods(registerB2BEmployeeWsDTO.getFirstName()));
        data.setLastName(sanitizationService.removePeriods(registerB2BEmployeeWsDTO.getLastName()));
        data.setPhoneNumber(registerB2BEmployeeWsDTO.getPhoneNumber());
        data.setMobileNumber(registerB2BEmployeeWsDTO.getMobileNumber());
        data.setFaxNumber(registerB2BEmployeeWsDTO.getFaxNumber());
        data.setFunctionCode(registerB2BEmployeeWsDTO.getFunctionCode());
        data.setDepartmentCode(registerB2BEmployeeWsDTO.getDepartmentCode());

        data.setBudgetWithoutLimit(registerB2BEmployeeWsDTO.isBudgetWithoutLimit());
        data.setBudgetPerOrder(registerB2BEmployeeWsDTO.getBudgetPerOrder());
        data.setYearlyBudget(registerB2BEmployeeWsDTO.getYearlyBudget());
        data.setRequestQuotationPermission(registerB2BEmployeeWsDTO.isRequestQuotationPermission());
        return data;
    }

    private String generateRandomPassword() {
        return RandomStringUtils.random(12, 0, 0, true, true, null, new SecureRandom());
    }

    public UserWsDTO getEmployeeDetails(final String customerUid, String fields) throws Exception {
        try {
            final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getUserForUID(customerUid);
            final B2BCustomerModel currentUser = (B2BCustomerModel) getUserService().getCurrentUser();
            if (!currentUser.getDefaultB2BUnit().equals(customer.getDefaultB2BUnit())) {
                throw new AccessDeniedException("The user " + currentUser.getUid() + " is not allowed to modify the user " + customer.getUid()
                                                + " because it is not a sub user of this company.");
            }

            final CustomerData customerData = b2bCustomerConverter.convert(customer);
            return customerDataToWsDTOConverter.convert(customerData, fields);
        } catch (final UnknownIdentifierException e) {
            LOG.error("The given user can not be found", e);
            throw new UnknownIdentifierException("account.company.user.notfound");
        } catch (final IllegalArgumentException e) {
            LOG.error("The given user can not be found", e);
            throw new IllegalArgumentException("account.company.user.notfound");
        } catch (final AccessDeniedException e) {
            LOG.warn("The given user can not be found", e);
            throw new AccessDeniedException("account.company.user.notfound");
        } catch (final Exception e) {
            LOG.error("Can not load the data of this customer", e);
            throw new Exception("account.company.user.global.load.error");
        }
    }

    public void updateEmployee(final String customerUid, final RegisterB2BEmployeeWsDTO registerB2BEmployeeWsDTO, final Errors errors) throws Exception {
        try {
            parsePhoneNumbers(registerB2BEmployeeWsDTO);

            final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getUserForUID(customerUid);
            final B2BCustomerModel currentUser = (B2BCustomerModel) getUserService().getCurrentUser();
            if (!currentUser.getDefaultB2BUnit().equals(customer.getDefaultB2BUnit())) {
                throw new AccessDeniedException("The user " + currentUser.getUid() + " is not allowed to modify the user " + customer.getUid()
                                                + " because it is not a sub user of this company.");
            }

            final DistSubUserData data = populateSubUserData(registerB2BEmployeeWsDTO);
            data.setEmail(registerB2BEmployeeWsDTO.getEmail());

            try {
                getB2bCustomerFacade().updateB2BEmployee(data, customer);
            } catch (final DuplicateUidException e) {
                LOG.error("Registration failed due to Duplicate UID(Email): {}", data.getEmail(), e);
                throw new DuplicateUidException("An account already exists for this email address");
            }
        } catch (final UnknownIdentifierException e) {
            LOG.error("The given user can not be found", e);
            throw new UnknownIdentifierException(getErrorMessage("account.company.user.notfound"));
        } catch (final IllegalArgumentException e) {
            LOG.error("The given user can not be found", e);
            throw new IllegalArgumentException(getErrorMessage("account.company.user.notfound"));
        } catch (final AccessDeniedException e) {
            LOG.warn("The given user can not be found", e);
            throw new AccessDeniedException(getErrorMessage("account.company.user.notfound"));
        } catch (final DuplicateUidException e) {
            errors.rejectValue("email", "registration.error.account.exists.title");
            throw new WebserviceValidationException(errors);
        } catch (final Exception e) {
            LOG.error("Can not save the data of this user, because of a global error", e);
            throw new Exception(getErrorMessage("account.company.user.global.save.error"));
        }
    }

    public OrderApprovalDecisionWsDTO orderApprovalDecision(final OrderApprovalDecisionForm orderApprovalDecisionForm) {
        OrderApprovalDecisionWsDTO orderApprovalDecisionWsDTO = new OrderApprovalDecisionWsDTO();
        try {
            String errorMessageKey = null;
            final String decision = orderApprovalDecisionForm.getApproverSelectedDecision() != null
                                                                                                    ? orderApprovalDecisionForm.getApproverSelectedDecision()
                                                                                                                               .trim().toUpperCase()
                                                                                                    : null;

            // Checking errors
            if (StringUtils.isEmpty(decision) || (!"APPROVE".equals(decision) && !"REJECT".equals(decision))) {
                errorMessageKey = "text.account.orderApproval.missingDecision";
            } else {
                if ("APPROVE".equals(decision) && !"true".equalsIgnoreCase(orderApprovalDecisionForm.getTermsAndConditions())) {
                    errorMessageKey = "text.account.orderApproval.acceptTermsAndConditions";
                } else if ("REJECT".equals(decision) && StringUtils.isEmpty(orderApprovalDecisionForm.getComments())) {
                    errorMessageKey = "text.account.orderApproval.addApproverComments";
                }
            }

            if (errorMessageKey != null) {
                orderApprovalDecisionWsDTO.setErrorMessage(getErrorMessage(errorMessageKey));
                try {
                    final B2BOrderApprovalData orderApprovalDetails = orderFacade.getOrderApprovalDetailsForCode(orderApprovalDecisionForm.getWorkFlowActionCode());
                    if (orderApprovalDetails != null && orderApprovalDetails.getB2bOrderData() != null) {
                        orderApprovalDecisionWsDTO.setContinueUrl("/my-account/order-approval-details/workflow/"
                                                                  + orderApprovalDecisionForm.getWorkFlowActionCode());
                        return orderApprovalDecisionWsDTO;
                    } else {
                        orderApprovalDecisionWsDTO.setContinueUrl("/my-account/order-approval");
                        return orderApprovalDecisionWsDTO;
                    }
                } catch (final Exception e) {
                    LOG.error("Can not save order approval decision.", e);
                    orderApprovalDecisionWsDTO.setContinueUrl("/my-account/order-approval");
                    return orderApprovalDecisionWsDTO;
                }
            }

            // No error found, then proceed approval
            final B2BOrderApprovalData b2bOrderApprovalData = new B2BOrderApprovalData();
            b2bOrderApprovalData.setSelectedDecision(decision);
            b2bOrderApprovalData.setApprovalComments(orderApprovalDecisionForm.getComments());
            b2bOrderApprovalData.setWorkflowActionModelCode(orderApprovalDecisionForm.getWorkFlowActionCode());

            try {
                orderFacade.setOrderApprovalDecision(b2bOrderApprovalData);
            } catch (final WorkflowActionDecideException e) {
                orderApprovalDecisionWsDTO.setErrorMessage(getErrorMessage("text.account.orderApproval.decisionAlreadyDone"));
                orderApprovalDecisionWsDTO.setContinueUrl("/my-account/order-approval");
                return orderApprovalDecisionWsDTO;
            }

            if ("REJECT".equals(decision)) {
                orderApprovalDecisionWsDTO.setErrorMessage(getErrorMessage("text.account.orderApproval.rejected"));
                orderApprovalDecisionWsDTO.setContinueUrl("/my-account/order-approval");
                return orderApprovalDecisionWsDTO;
            } else {
                final B2BOrderApprovalData orderApprovalDetails = orderFacade.getOrderApprovalDetailsForCode(orderApprovalDecisionForm.getWorkFlowActionCode());
                orderApprovalDecisionWsDTO.setCheckoutUser(orderApprovalDetails.getB2bOrderData().getB2bCustomerData().getUid());
                orderApprovalDecisionWsDTO.setOrderApprovalConfirmed(true);
                orderApprovalDecisionWsDTO.setContinueUrl("/checkout/orderConfirmation");
                return orderApprovalDecisionWsDTO;
            }

        } catch (final UnknownIdentifierException e) {
            LOG.warn("Attempted to load an order approval that does not exist or is not visible!", e);
            orderApprovalDecisionWsDTO.setErrorMessage(getErrorMessage("text.account.orderApproval.canNotLoad"));
            orderApprovalDecisionWsDTO.setContinueUrl("/my-account/order-approval");
            return orderApprovalDecisionWsDTO;
        }
    }

    public DistPaymentOptionsWsDTO getDistPaymentOptionsWsDTO(String fields, List<DistPaymentModeData> paymentModes) {
        var response = new DistPaymentOptionsWsDTO();
        final PaymentModeListWsDTO paymentModeList = new PaymentModeListWsDTO();
        setDefaultPaymentMode(paymentModes);
        paymentModeList.setPaymentModes(getDataMapper().mapAsList(paymentModes, PaymentModeWsDTO.class, fields));
        response.setPaymentOptions(paymentModeList);
        return response;
    }

    private void parseContactNumbers(final AddressWsDTO contactAddress) {
        if (StringUtils.isNotBlank(contactAddress.getPhone())) {
            contactAddress.setPhone(distPhoneNumberFacade.formatPhoneNumberForCurrentCountry(contactAddress.getPhone()));
        }
        if (StringUtils.isNotBlank(contactAddress.getCellphone())) {
            contactAddress.setCellphone(distPhoneNumberFacade.formatPhoneNumberForCurrentCountry(contactAddress.getCellphone()));
        }
    }

    private void parsePhoneNumbers(final RegisterB2BEmployeeWsDTO registerB2BEmployeeWsDTO) {
        if (StringUtils.isNotBlank(registerB2BEmployeeWsDTO.getPhoneNumber())) {
            registerB2BEmployeeWsDTO.setPhoneNumber(distPhoneNumberFacade.formatPhoneNumberForCurrentCountry(registerB2BEmployeeWsDTO.getPhoneNumber()));
        }

        if (StringUtils.isNotBlank(registerB2BEmployeeWsDTO.getMobileNumber())) {
            registerB2BEmployeeWsDTO.setMobileNumber(distPhoneNumberFacade.formatPhoneNumberForCurrentCountry(registerB2BEmployeeWsDTO.getMobileNumber()));
        }
    }

    private String getErrorMessage(String messageKey) {
        return messageSource.getMessage(messageKey, null, i18nService.getCurrentLocale());
    }

    public boolean sendUpdatePreferenceCenterRequest(final B2BCustomerModel currentUser, final DistMarketingConsentData marketingConsent,
                                                     final BloomreachTouchpoint touchpoint) {
        try {
            final String updatePreferenceCenterRequest = distBloomreachFacade.getPreferenceCenterUpdates(marketingConsent,
                                                                                                         getConsentDataObject(currentUser),
                                                                                                         touchpoint);
            if (StringUtils.isNotBlank(updatePreferenceCenterRequest)) {
                distBloomreachFacade.sendBatchRequestToBloomreach(updatePreferenceCenterRequest);
            }
            return true;
        } catch (IOException | DistBloomreachExportException | DistBloomreachBatchException e) {
            LOG.error("Communication preferences could not be updated for customer: {}", currentUser.getUid(), e);
            return false;
        }
    }

    private DistConsentData getConsentDataObject(final B2BCustomerModel currentUser) {
        final DistConsentData consentData = new DistConsentData();
        consentData.setUid(currentUser.getUid());
        consentData.setErpContactId(currentUser.getErpContactID());
        return consentData;
    }

    public DistConsentData getConsentDataObject(final CustomerData updatedCustomer) {
        final DistConsentData consentData = new DistConsentData();
        consentData.setFirstName(updatedCustomer.getFirstName());
        consentData.setLastName(updatedCustomer.getLastName());
        consentData.setTitleCode(updatedCustomer.getTitleCode());
        consentData.setUid(updatedCustomer.getUid());
        consentData.setErpContactId(updatedCustomer.getContactId());
        if (updatedCustomer.getContactAddress() != null) {
            consentData.setPhoneNumber(updatedCustomer.getContactAddress().getPhone());
            consentData.setMobileNumber(updatedCustomer.getContactAddress().getCellphone());
            if (updatedCustomer.getContactAddress().getCountry() != null) {
                consentData.setCountryCode(updatedCustomer.getContactAddress().getCountry().getIsocode());
            }
        }
        if (StringUtils.isBlank(consentData.getCountryCode()) && CollectionUtils.isNotEmpty(updatedCustomer.getApprovers())) {
            consentData.setCountryCode(getCountryCodeForSubUser(updatedCustomer));
        }
        return consentData;
    }

    private String getCountryCodeForSubUser(final CustomerData updatedCustomer) {
        if (updatedCustomer.getUnit() != null && updatedCustomer.getUnit().getCountry() != null) {
            return updatedCustomer.getUnit().getCountry().getIsocode();
        }
        return null;
    }

    public boolean sendUpdateOrRegistrationRequestToBloomreach(final B2BCustomerModel currentUser, final DistMarketingConsentData marketingConsent) {
        if (currentUser.isRegisteredAsGuest()) {
            return sendUpdatePreferenceCenterRequest(currentUser, marketingConsent, BloomreachTouchpoint.GUEST);
        } else if (CollectionUtils.isNotEmpty(currentUser.getApprovers())) {
            return sendUpdatePreferenceCenterRequest(currentUser, marketingConsent, BloomreachTouchpoint.SUBUSER);
        } else if (currentUser.isRsCustomer()) {
            final CustomerData customerData = getB2bCustomerFacade().getCurrentCustomer();
            final DistConsentData consentData = getRsConsentData(customerData, marketingConsent);
            return getB2bCustomerFacade().createUserInBloomreach(consentData);
        } else {
            return false;
        }
    }

    private DistConsentData getRsConsentData(final CustomerData customerData, final DistMarketingConsentData marketingConsent) {
        final DistConsentData consentData = getConsentDataObject(customerData);
        consentData.setRsCustomer(true);
        consentData.setPhonePermission(marketingConsent.isPhoneConsent());
        consentData.setSmsPermissions(marketingConsent.isSmsConsent());
        consentData.setPaperPermission(marketingConsent.isPostConsent());
        consentData.setPersonalisationSubscription(marketingConsent.isPersonalisationConsent());
        consentData.setProfilingSubscription(marketingConsent.isProfilingConsent());
        consentData.setActiveSubscription(marketingConsent.isEmailConsent());
        return consentData;
    }
}
