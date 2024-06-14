/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.namics.distrelec.b2b.core.bloomreach.enums.BloomreachTouchpoint;
import com.namics.distrelec.b2b.core.bloomreach.exception.DistBloomreachBatchException;
import com.namics.distrelec.b2b.core.bloomreach.exception.DistBloomreachExportException;
import com.namics.distrelec.b2b.core.bomtool.data.BomToolImportData;
import com.namics.distrelec.b2b.core.obsolescence.service.DistObsolescenceService;
import com.namics.distrelec.b2b.core.security.SanitizationService;
import com.namics.distrelec.b2b.core.service.company.DistB2BCommerceUnitService;
import com.namics.distrelec.b2b.facades.bloomreach.DistBloomreachFacade;
import com.namics.distrelec.b2b.facades.bloomreach.data.DistBloomreachConsentData;
import com.namics.distrelec.b2b.facades.bomtool.DistBomToolFacade;
import com.namics.distrelec.b2b.facades.customer.DistCustomerRegistrationFacade;
import com.namics.distrelec.b2b.facades.customer.DistUserDashboardFacade;
import com.namics.distrelec.b2b.facades.order.DistB2BOrderFacade;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.payment.ws.dto.DistPaymentOptionsWsDTO;
import com.namics.distrelec.b2b.facades.user.data.DistMarketingConsentData;
import com.namics.distrelec.b2b.facades.user.data.ObsolescenceTempData;
import com.namics.distrelec.b2b.facades.user.data.UserDashboardContentData;
import com.namics.distrelec.occ.core.bomtool.ws.dto.BomToolImportWsDTO;
import com.namics.distrelec.occ.core.constants.YcommercewebservicesConstants;
import com.namics.distrelec.occ.core.order.ws.dto.OrderApprovalDecisionWsDTO;
import com.namics.distrelec.occ.core.security.SecuredAccessConstants;
import com.namics.distrelec.occ.core.user.ws.dto.*;
import com.namics.distrelec.occ.core.v2.annotations.B2ERestricted;
import com.namics.distrelec.occ.core.v2.forms.OrderApprovalDecisionForm;
import com.namics.distrelec.occ.core.v2.forms.OrderApprovalForm;
import com.namics.distrelec.occ.core.v2.forms.UpdateProfileInfoForm;
import com.namics.distrelec.occ.core.v2.helper.UsersHelper;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData;
import de.hybris.platform.b2bwebservicescommons.dto.order.OrderApprovalListWsDTO;
import de.hybris.platform.b2bwebservicescommons.dto.order.OrderApprovalWsDTO;
import de.hybris.platform.cmsoccaddon.mapping.converters.DataToWsConverter;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.customergroups.CustomerGroupFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoDatas;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commercefacades.user.data.UserGroupDataList;
import de.hybris.platform.commercefacades.user.exceptions.PasswordMismatchException;
import de.hybris.platform.commercefacades.user.ws.dto.MyCompanyWsDTO;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commercewebservicescommons.dto.order.DeliveryModeListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.DeliveryModeWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserGroupListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserSignUpWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping(value = "/{baseSiteId}/users")
@CacheControl(directive = CacheControlDirective.PRIVATE)
@Tag(name = "Users")
public class UsersController extends BaseCommerceController {
    private static final Logger LOG = LoggerFactory.getLogger(UsersController.class);

    public static final String USER_MAPPER_CONFIG = "firstName,lastName,titleCode,currency(isocode),language(isocode)";

    @Resource(name = "wsCustomerFacade")
    private CustomerFacade customerFacade;

    @Resource(name = "wsCustomerGroupFacade")
    private CustomerGroupFacade customerGroupFacade;

    @Resource(name = "putUserDTOValidator")
    private Validator putUserDTOValidator;

    @Resource(name = "distRegistrationValidator")
    private Validator distRegistrationValidator;

    @Resource(name = "replaceEmailDTOValidator")
    private Validator replaceEmailDTOValidator;

    @Resource(name = "replacePasswordDTOValidator")
    private Validator replacePasswordDTOValidator;

    @Resource(name = "orderApprovalDecisionFormValidator")
    private Validator orderApprovalDecisionFormValidator;

    @Resource(name = "updateUserDTOValidator")
    private Validator updateUserDTOValidator;

    @Resource(name = "updateProfileInfoFormValidator")
    private Validator updateProfileInfoFormValidator;

    @Resource(name = "userAccountActiveValidator")
    private Validator userAccountActiveValidator;

    @Autowired
    private DistCustomerRegistrationFacade distCustomerRegistrationFacade;

    @Autowired
    private DistUserDashboardFacade distUserDashboardFacade;

    @Autowired
    private UsersHelper usersHelper;

    @Autowired
    private DistB2BCommerceUnitService b2BCommerceUnitService;

    @Autowired
    @Qualifier(value = "myCompanyToWsConverter")
    private DataToWsConverter<B2BUnitModel, MyCompanyWsDTO> myCompanyToWsConverter;

    @Autowired
    private DistObsolescenceService distObsolescenceService;

    @Autowired
    private DistBomToolFacade distBomToolFacade;

    @Autowired
    private DistB2BOrderFacade distB2BOrderFacade;

    @Autowired
    private DistBloomreachFacade distBloomreachFacade;

    @Autowired
    private SanitizationService sanitizationService;

    @Secured({ SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP,
               SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    @Operation(operationId = "createUser", summary = " Registers a customer", description = "Registers a customer. Requires the following "
                                                                                            + "parameters: login, password, firstName, lastName, titleCode.")
    @ApiBaseSiteIdParam
    public UserWsDTO createUser(
                                @Parameter(description = "User's object for registration.", required = true) @RequestBody final UserSignUpWsDTO form,
                                @Parameter(schema = @Schema(defaultValue = "false")) @RequestParam(defaultValue = "false") final boolean registerGuest,
                                @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields, final HttpServletRequest httpRequest,
                                final HttpServletResponse httpResponse) throws Exception {
        validate(form, "user", distRegistrationValidator);
        if (BooleanUtils.isFalse(registerGuest)) {
            final RegisterData registerData = getDataMapper().map(form, RegisterData.class);
            distCustomerRegistrationFacade.register(registerData, form.getCustomerType());
            final String userId = form.getUid().toLowerCase(Locale.ENGLISH);
            httpResponse.setHeader(YcommercewebservicesConstants.LOCATION, getAbsoluteLocationURL(httpRequest, userId));
            final CustomerData customerData = customerFacade.getUserForUID(userId);
            final UserWsDTO user = getDataMapper().map(customerData, UserWsDTO.class, fields);
            usersHelper.setQuotationPermission(user, customerData);
            return user;
        } else {
            return convertGuestToB2C(form, fields);
        }

    }

    private UserWsDTO convertGuestToB2C(final UserSignUpWsDTO form, final String fields) throws Exception {
        final OrderData order = distB2BOrderFacade.getOrderDetailsForGUID(form.getGuid());
        if (order == null || !StringUtils.equals(form.getUid(), order.getB2bCustomerData().getEmail())) {
            throw new Exception("lightboxreturnrequest.process.error");
        }
        try {
            getB2bCustomerFacade().convertGuestToB2CAndRegisterInBloomreach(form.getPassword(), form.getGuid());
            return getUser(fields);
        } catch (final Exception e) {
            LOG.error("Error happened during converting guest user to B2C", e);
            throw new Exception("lightboxreturnrequest.process.error");
        }
    }

    protected String getAbsoluteLocationURL(final HttpServletRequest httpRequest, final String uid) {
        final String requestURL = httpRequest.getRequestURL().toString();
        final String encodedUid = UriUtils.encodePathSegment(uid, StandardCharsets.UTF_8.name());
        return UriComponentsBuilder.fromHttpUrl(requestURL).pathSegment(encodedUid).build().toString();
    }

    @Secured({ SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP,
               SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    @Operation(operationId = "activateUser", summary = " Activates offline customer")
    @ApiBaseSiteIdParam
    public UserWsDTO activateUser(@Parameter(description = "User's object for registration.", required = true) @RequestBody final UserSignUpWsDTO form,
                                  @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws Exception {
        distCustomerRegistrationFacade.activate(getDataMapper().map(form, RegisterData.class), form.getCustomerType());
        return getDataMapper().map(customerFacade.getUserForUID(form.getUid()), UserWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getUser", summary = "Get customer profile", description = "Returns customer profile.")
    @ApiBaseSiteIdAndUserIdParam
    public UserWsDTO getUser(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final UserWsDTO user = usersHelper.getUser(fields);
        final Errors errors = validateObject(user, "userWsDTO", userAccountActiveValidator);
        if (errors.hasErrors()) {
            throw new WebserviceValidationException(getErrorMessage(errors.getNestedPath()));
        }
        return user;
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "replaceUser", summary = "Updates customer profile", description = "Updates customer profile. Attributes not provided in the request body will be defined again (set to null or default).")
    @ApiBaseSiteIdAndUserIdParam
    public void replaceUser(@Parameter(description = "User's object", required = true) @RequestBody final UserWsDTO user) throws DuplicateUidException {
        validate(user, "user", putUserDTOValidator);

        final CustomerData customer = customerFacade.getCurrentCustomer();
        LOG.debug("replaceUser: userId={}", customer.getUid());

        getDataMapper().map(user, customer, USER_MAPPER_CONFIG, true);
        customerFacade.updateFullProfile(customer);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "updateUser", summary = "Updates customer profile", description = "Updates customer profile. Only attributes provided in the request body will be changed.")
    @ApiBaseSiteIdAndUserIdParam
    public void updateUser(@Parameter(description = "User's object.", required = true) @RequestBody final UserWsDTO user) throws DuplicateUidException {
        sanitizeInputFields(user);
        validate(user, "user", updateUserDTOValidator);
        final CustomerData customerData = getB2bCustomerFacade().getCurrentCustomer();
        LOG.debug("updateUser: userId={}", customerData.getUid());
        usersHelper.updateProfile(user, customerData);
        getDataMapper().map(user, customerData, USER_MAPPER_CONFIG, false);
        customerFacade.updateFullProfile(customerData);
        try {
            distBloomreachFacade.updateCustomerInBloomreach(usersHelper.getConsentDataObject(customerData));
        } catch (DistBloomreachBatchException | JsonProcessingException e) {
            LOG.error("Error occurred in Bloomreach during update of customer: {}", customerData.getUid(), e);
        }
    }

    private void sanitizeInputFields(UserWsDTO user) {
        user.setFirstName(sanitizationService.removePeriods(user.getFirstName()));
        user.setLastName(sanitizationService.removePeriods(user.getLastName()));
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @RequestMapping(value = "/{userId}/update-profile-info", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "updateProfileInfo", summary = "Updates selected profile information", description = "Updates customer profile with function and department code")
    @ApiBaseSiteIdAndUserIdParam
    public void updateProfileInfo(@Parameter(description = "Update profile info form", required = true) @RequestBody final UpdateProfileInfoForm updateProfileInfoForm)
                                                                                                                                                                        throws Exception {
        validate(updateProfileInfoForm, "updateProfileInfoForm", updateProfileInfoFormValidator);
        final CustomerData customerData = new CustomerData();
        customerData.setFunctionCode(updateProfileInfoForm.getFunctionCode());
        customerData.setDepartment(updateProfileInfoForm.getDepartmentCode());
        try {
            getB2bCustomerFacade().updateSelectedProfileInformation(customerData);
        } catch (final Exception ex) {
            LOG.error("An error has occur while updating UserProfile object", ex);
            throw new Exception("lightboxreturnrequest.process.error");
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "removeUser", summary = "Delete customer profile.", description = "Removes customer profile.")
    @ApiBaseSiteIdAndUserIdParam
    public void removeUser() {
        final CustomerData customer = customerFacade.closeAccount();
        LOG.debug("removeUser: userId={}", customer.getUid());
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @RequestMapping(value = "/{userId}/email", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "replaceUserEmail", summary = "Changes customer's email.", description = "Changes a customer's email. Requires the customer's current password.")
    @ApiBaseSiteIdAndUserIdParam
    public void replaceUserEmail(@Parameter(description = "Replace email form.", required = true) @RequestBody final ReplaceLoginWsDTO form) {
        final Errors errors = validateObject(form, "email", replaceEmailDTOValidator);
        getB2bCustomerFacade().removeForgotPasswordToken();
        usersHelper.updateLogin(form.getNewLogin(), form.getPassword(), errors);
        final CustomerData customerData = getB2bCustomerFacade().getCurrentCustomer();
        try {
            distBloomreachFacade.updateCustomerInBloomreach(usersHelper.getConsentDataObject(customerData));
        } catch (DistBloomreachBatchException | JsonProcessingException e) {
            LOG.error("Error occurred in Bloomreach during update of customer: {}", customerData.getUid(), e);
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @RequestMapping(value = "/{userId}/password", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @Operation(operationId = "replaceUserPassword", summary = "Changes customer's password", description = "Changes customer's password.")
    @ApiBaseSiteIdAndUserIdParam
    public void replaceUserPassword(@Parameter(description = "Replace password form.", required = true) @RequestBody final ReplacePasswordWsDTO form) {
        final Errors errors = validateObject(form, "password", replacePasswordDTOValidator);
        if (form.getNewPassword().equals(form.getCheckNewPassword())) {
            try {
                getB2bCustomerFacade().changePassword(form.getCurrentPassword(), form.getNewPassword());
            } catch (final PasswordMismatchException localException) {
                errors.rejectValue("currentPassword", "profile.currentPassword.invalid", new Object[] {}, "Please enter your current password");
                throw new WebserviceValidationException(errors);
            }
        } else {
            errors.rejectValue("checkNewPassword", "validation.checkPwd.equals", new Object[] {}, "Password and password confirmation do not match.");
            throw new WebserviceValidationException(errors);
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{userId}/customergroups", method = RequestMethod.GET)
    @Operation(operationId = "getUserCustomerGroups", summary = "Get all customer groups of a customer.", description = "Returns all customer groups of a customer.")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public UserGroupListWsDTO getUserCustomerGroups(@Parameter(description = "User identifier.", required = true) @PathVariable final String userId,
                                                    @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final UserGroupDataList userGroupDataList = new UserGroupDataList();
        userGroupDataList.setUserGroups(customerGroupFacade.getCustomerGroupsForUser(userId));
        return getDataMapper().map(userGroupDataList, UserGroupListWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{userId}/dashboardContents", method = RequestMethod.GET)
    @Operation(operationId = "getUserDashboardContents", summary = "Get all Dashboard contents of a customer.")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public UserDashboardContentWsDTO getUserDashboardContents(@Parameter(description = "User identifier.", required = true) @PathVariable final String userId,
                                                              @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final UserDashboardContentData userDashboardContent = new UserDashboardContentData();
        if (!distUserDashboardFacade.isOciCustomer() && !distUserDashboardFacade.isAribaCustomer()) {
            userDashboardContent.setOpenOrdersCount(distUserDashboardFacade.getOpenOrdersCount());
            userDashboardContent.setNewInvoicesCount(distUserDashboardFacade.getNewInvoicesCount());
            userDashboardContent.setAppReqCount(distUserDashboardFacade.getApprovalRequestsCount());
            userDashboardContent.setQuoteCount(distUserDashboardFacade.getOfferedQuoteCount());
        }
        return getDataMapper().map(userDashboardContent, UserDashboardContentWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @RequestMapping(value = "/{userId}/communication-preferences", method = RequestMethod.GET)
    @Operation(operationId = "getCommunicationPreferences", summary = "Get all communication preferences of a customer.")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public DistMarketingConsentWsDTO getCommunicationPreferences(@Parameter(description = "User identifier.", required = true) @PathVariable final String userId,
                                                                 @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final CustomerData customer = customerFacade.getCurrentCustomer();
        DistMarketingConsentWsDTO marketingConsentWsDTO = new DistMarketingConsentWsDTO();
        try {
            final DistBloomreachConsentData bloomreachConsentData = distBloomreachFacade.exportCustomerConsentsFromBloomreach(customer.getEmail());
            marketingConsentWsDTO = getDataMapper().map(bloomreachConsentData, DistMarketingConsentWsDTO.class, fields);
        } catch (IOException | DistBloomreachExportException e) {
            LOG.error("Consents could not be exported from Bloomreach for customer: {}", customer.getEmail(), e);
        }
        marketingConsentWsDTO.setOptedForObsolescence(customer.isOptedForObsolescence());
        marketingConsentWsDTO.setAllCatSelected(customer.isAllObsolCatSelected());
        marketingConsentWsDTO.setCategories(getDataMapper().mapAsList(customer.getCategories(), ObsolescenceCategoryWsDTO.class, fields));
        return marketingConsentWsDTO;
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @RequestMapping(value = "/{userId}/request-invoice-payment-mode", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "requestInvoicePaymentMode", summary = "Requests invoice payment mode for a user")
    @ApiBaseSiteIdAndUserIdParam
    public void requestInvoicePaymentMode() {
        getUserFacade().requestInvoicePaymentMode();
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @ResponseBody
    @RequestMapping(value = "/{userId}/paymentmodes", method = RequestMethod.GET)
    @Operation(operationId = "getPaymentModes", summary = "Gets all available payment modes for a user")
    @ApiBaseSiteIdAndUserIdParam
    public DistPaymentOptionsWsDTO getPaymentModesForUser(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final List<DistPaymentModeData> paymentModes = getUserFacade().getSupportedPaymentModesForUser();
        final DistPaymentOptionsWsDTO response = usersHelper.getDistPaymentOptionsWsDTO(fields, paymentModes);
        final PaymentDetailsListWsDTO paymentDetailsListWsDTO = new PaymentDetailsListWsDTO();
        final List<CCPaymentInfoData> creditCards = getUserFacade().getCCPaymentInfos(true);
        if (CollectionUtils.isNotEmpty(creditCards)) {
            paymentDetailsListWsDTO.setPayments(getDataMapper().mapAsList(creditCards, PaymentDetailsWsDTO.class, fields));
        } else {
            final List<CCPaymentInfoData> ccPaymentInfos = usersHelper.fetchCCPaymentInfosForCurrentUser(paymentModes);
            paymentDetailsListWsDTO.setPayments(getDataMapper().mapAsList(ccPaymentInfos, PaymentDetailsWsDTO.class, fields));
        }
        response.setCcPaymentInfos(paymentDetailsListWsDTO);
        usersHelper.setInvoicePaymentModeAttributes(response);
        return response;
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @ResponseBody
    @RequestMapping(value = "/{userId}/deliverymodes", method = RequestMethod.GET)
    @Operation(operationId = "getDeliveryModes", summary = "Gets all available delivery modes for a user")
    @ApiBaseSiteIdAndUserIdParam
    public DeliveryModeListWsDTO getDeliveryModesForUser(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final List<DeliveryModeData> deliveryInfoDatas = getUserFacade().getSupportedDeliveryModesForUser();
        final var deliveryModeListWsDTO = new DeliveryModeListWsDTO();
        deliveryModeListWsDTO.setDeliveryModes(getDataMapper().mapAsList(deliveryInfoDatas, DeliveryModeWsDTO.class, fields));
        return deliveryModeListWsDTO;
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @RequestMapping(value = "/{userId}/set-payment-option", method = RequestMethod.PUT)
    @Operation(operationId = "setDefaultPaymentOption", summary = "Set default payment option", description = "Set default payment option")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public ResponseEntity<?> updateDefaultPaymentOption(@Parameter(description = "User identifier.", required = true) @PathVariable final String userId,
                                                        @Parameter(description = "Payment Option", required = true) @RequestParam final String paymentOption) {
        boolean isPaymentOptionSet = false;
        if (StringUtils.isNotBlank(paymentOption)) {
            isPaymentOptionSet = getUserFacade().setDefaultPaymentMode(paymentOption);
        }
        final HttpStatus finalStatus = isPaymentOptionSet ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(finalStatus).build();
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @RequestMapping(value = "/{userId}/set-payment-info/{paymentInfoId}", method = RequestMethod.POST)
    @Operation(operationId = "setDefaultPaymentInfo", summary = "Set default payment info", description = "Set default credit card")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public PaymentDetailsListWsDTO updateDefaultPaymentInfo(@Parameter(description = "Payment Info ID", required = true) @PathVariable final String paymentInfoId,
                                                            @Parameter(description = "Payment Option", required = true) @RequestParam final String paymentOption,
                                                            @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        if (StringUtils.isNotBlank(paymentOption) && StringUtils.isNotBlank(paymentInfoId)) {
            final boolean isSaved = getUserFacade().setDefaultPaymentMode(paymentOption);
            if (isSaved) {
                getUserFacade().setDefaultPaymentInfo(paymentInfoId);
            }
        }
        final CCPaymentInfoDatas paymentInfoDataList = new CCPaymentInfoDatas();
        paymentInfoDataList.setPaymentInfos(getUserFacade().getCCPaymentInfos(Boolean.TRUE));
        return getDataMapper().map(paymentInfoDataList, PaymentDetailsListWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @RequestMapping(value = "/{userId}/remove-payment-info/{paymentInfoId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "removePaymentInfo", summary = "Delete payment info", description = "Removes credit card")
    @ApiBaseSiteIdAndUserIdParam
    public void removePaymentInfo(@Parameter(description = "Payment Info ID", required = true) @PathVariable final String paymentInfoId) {
        LOG.debug("removePaymentInfo: id={}", sanitize(paymentInfoId));
        getUserFacade().removeCCPaymentInfo(paymentInfoId);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @RequestMapping(value = "/{userId}/set-delivery-option", method = RequestMethod.PUT)
    @Operation(operationId = "setDefaultShippingOption", summary = "Set default shipping option", description = "Set default shipping option")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public ResponseEntity<?> updateDefaultDeliveryOption(@Parameter(description = "User identifier.", required = true) @PathVariable final String userId,
                                                         @Parameter(description = "Shipping Option", required = true) @RequestParam final String shippingOption) {
        boolean isShippingOptionSet = false;
        if (StringUtils.isNotBlank(shippingOption)) {
            isShippingOptionSet = getUserFacade().setDefaultDeliveryMode(shippingOption);
        }
        final HttpStatus finalStatus = isShippingOptionSet ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(finalStatus).build();
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @RequestMapping(value = "/{userId}/contacts-of-customer", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(operationId = "getContactsOfCustomer", summary = "Retrieves list of customer contacts for orderedBy in order history")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public List<UserWsDTO> getContactsOfCustomer(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        List<CustomerData> contactsOfCustomerDataList = new ArrayList<>();
        if (usersHelper.isCompanyOrderAdminUser()) {
            contactsOfCustomerDataList = getB2bCustomerFacade().searchEmployees(null, null, null, true);
        }
        return getDataMapper().mapAsList(contactsOfCustomerDataList, UserWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BCUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @B2ERestricted
    @RequestMapping(value = "/{userId}/company-information", method = RequestMethod.GET)
    @Operation(operationId = "myCompany", summary = "returns information about customer companies", description = "returns information about company")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public MyCompanyWsDTO myCompany(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        return myCompanyToWsConverter.convert(b2BCommerceUnitService.getParentUnit(), fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @RequestMapping(value = "/{userId}/communication-preferences", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(operationId = "updateCommunicationPreferences", summary = "Save Communication Preferences", description = "Save Communication Preferences")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public ResponseEntity<String> updateCommunicationPreferences(@Parameter(description = "User identifier.", required = true) @PathVariable final String userId,
                                                                 @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
                                                                 @Parameter(description = "Consent profile", required = true) @RequestBody final ConsentProfileWsDTO consentProfileWsDTO) throws Exception {

        final B2BCustomerModel currentUser = usersHelper.getCurrentUser();
        final DistMarketingConsentData marketingConsent = populateConsentProfileToMarketingConsentData(consentProfileWsDTO, currentUser);
        String message = "";
        if (CollectionUtils.isNotEmpty(consentProfileWsDTO.getObsoleCategories())) {
            try {
                final List<ObsolescenceTempData> obsoleCategories = getDataMapper().mapAsList(consentProfileWsDTO.getObsoleCategories(),
                                                                                              ObsolescenceTempData.class,
                                                                                              fields);
                populateConsentProfileToObsolescenceTempData(consentProfileWsDTO, obsoleCategories);
                distObsolescenceService.changeObsolPreference(obsoleCategories);
                message = getMessageSource().getMessage("text.account.loginData.confirmationUpdated", null, getI18nService().getCurrentLocale());
            } catch (final Exception e) {
                LOG.error("Can not update user profile for the customer with Email: {}", currentUser.getUid());
                message = getMessageSource().getMessage("form.global.error", null, getI18nService().getCurrentLocale());
            }
            boolean isUpdated = usersHelper.sendUpdatePreferenceCenterRequest(currentUser, marketingConsent, BloomreachTouchpoint.MYACCOUNT);
            if (!isUpdated) {
                throw new Exception("lightboxreturnrequest.process.error");
            }
        } else {
            distObsolescenceService.saveObsolescenceCategoriesForCustomer(currentUser, marketingConsent.isEmailConsent());
        }
        if (BooleanUtils.isTrue(currentUser.getConsentConditionRequired())) {
            getUserFacade().updateConsentConditionsRequiredFlag(currentUser, false);
            boolean status = usersHelper.sendUpdateOrRegistrationRequestToBloomreach(currentUser, marketingConsent);
            String messageKey = status ? "text.account.loginData.confirmationUpdated" : "lightboxreturnrequest.process.error";
            message = getMessageSource().getMessage(messageKey, null, getI18nService().getCurrentLocale());
        }
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_B2BAPPROVERGROUP,
               SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @B2ERestricted
    @RequestMapping(value = "/{userId}/order-approval", method = RequestMethod.POST)
    @Operation(operationId = "orderApprovals", summary = "The list of order approval requests for a admin B2B customer")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public OrderApprovalListWsDTO getApprovalLists(@Parameter(description = "Order approval form") @RequestBody final OrderApprovalForm orderApprovalForm,
                                                   @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        if (isOrderApprovalEnabled()) {
            final List<WorkflowActionStatus> workflowActionStatusList = new ArrayList<>(Arrays.asList(WorkflowActionStatus.values()));
            workflowActionStatusList.remove(WorkflowActionStatus.COMPLETED);
            return getDataMapper().map(usersHelper.getOrderApprovals(orderApprovalForm, new WorkflowActionType[] { WorkflowActionType.START },
                                                                     workflowActionStatusList.toArray(new WorkflowActionStatus[0])),
                                       OrderApprovalListWsDTO.class, fields);
        } else {
            throw new NotFoundException("Order approvals are not enabled by current BaseStore!");
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_B2BAPPROVERGROUP,
               SecuredAccessConstants.ROLE_B2BCUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @B2ERestricted
    @RequestMapping(value = "/{userId}/order-approval-details/workflow/{workflowActionCode:.*}", method = RequestMethod.GET)
    @Operation(operationId = "orderApprovalDetails", summary = "Get order approval details", description = "Returns order approval details")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public OrderApprovalWsDTO getOrderApprovalDetails(@Parameter(description = "Workflow Action Code", required = true) @PathVariable final String workflowActionCode,
                                                      @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        if (isOrderApprovalEnabled()) {
            final B2BOrderApprovalData orderApprovalData = usersHelper.getOrderApprovalDetails(workflowActionCode);
            return getDataMapper().map(orderApprovalData, OrderApprovalWsDTO.class, fields);
        } else {
            throw new NotFoundException("Order approval details are not enabled by current BaseStore!");
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_B2BAPPROVERGROUP,
               SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @B2ERestricted
    @RequestMapping(value = "/{userId}/order-approval/approval-decision", method = RequestMethod.POST)
    @Operation(operationId = "orderApprovalDecision", summary = "Returns the order approval request page URL", description = "The admin customer may accept/reject an order approval request")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public OrderApprovalDecisionWsDTO orderApprovalDecision(@Parameter(description = "Order approval decision form") @RequestBody final OrderApprovalDecisionForm orderApprovalDecisionForm) {
        if (isOrderApprovalEnabled()) {
            validate(orderApprovalDecisionForm, "orderApprovalDecisionForm", orderApprovalDecisionFormValidator);
            return usersHelper.orderApprovalDecision(orderApprovalDecisionForm);
        } else {
            throw new NotFoundException("Order approvals are not enabled by current BaseStore!");
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BCUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @B2ERestricted
    @RequestMapping(value = "/{userId}/order-approval-requests", method = RequestMethod.POST)
    @Operation(operationId = "orderApprovalRequests", summary = "The list of order approval requests for a normal B2B customer")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public OrderApprovalListWsDTO getOrderApprovalRequests(@Parameter(description = "Order approval form") @RequestBody final OrderApprovalForm orderApprovalForm,
                                                           @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        if (isOrderApprovalEnabled()) {
            return getDataMapper().map(usersHelper.getOrderApprovals(orderApprovalForm, new WorkflowActionType[] { WorkflowActionType.START },
                                                                     WorkflowActionStatus.values()),
                                       OrderApprovalListWsDTO.class, fields);
        } else {
            throw new NotFoundException("Order approval requests are not enabled by current BaseStore!");
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{userId}/savedBomEntries", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(operationId = "getSavedBomEntries", summary = "Get saved BOM entries of a customer.", description = "Get saved BOM entries of a customer.")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public List<BomToolImportWsDTO> savedBomEntries(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final List<BomToolImportData> bomEntries = distBomToolFacade.getSavedBomToolEntries();
        return getDataMapper().mapAsList(bomEntries, BomToolImportWsDTO.class, fields);
    }

    private DistMarketingConsentData populateConsentProfileToMarketingConsentData(final ConsentProfileWsDTO consentProfileWsDTO,
                                                                                  final B2BCustomerModel currentUser) {
        final DistMarketingConsentData marketingConsent = getDataMapper().map(consentProfileWsDTO, DistMarketingConsentData.class);
        marketingConsent.setUid(currentUser.getEmail());
        marketingConsent.setPhoneNumber(currentUser.getContactAddress().getPhone1());
        marketingConsent.setMobileNumber(currentUser.getContactAddress().getCellphone());
        return marketingConsent;
    }

    private void populateConsentProfileToObsolescenceTempData(final ConsentProfileWsDTO consentProfileWsDTO,
                                                              final List<ObsolescenceTempData> obsoleCategories) {
        for (final ObsolescenceTempData category : obsoleCategories) {
            category.setAllCatSelected(consentProfileWsDTO.isAllCatSelected());
            category.setOptedForObsol(true);
        }
    }

    private boolean isOrderApprovalEnabled() {
        final BaseStoreModel store = getBaseStoreService().getCurrentBaseStore();
        return store != null && Boolean.TRUE.equals(store.getOrderApprovalEnabled());
    }
}
