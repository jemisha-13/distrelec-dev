package com.namics.distrelec.occ.core.v2.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.namics.distrelec.b2b.core.service.customer.DistCustomerAccountService;
import com.namics.distrelec.b2b.facades.user.ws.dto.RegisterB2BEmployeeWsDTO;
import com.namics.distrelec.core.v2.forms.EmployeeSearchB2BForm;
import com.namics.distrelec.mapping.converters.CustomerDataToWsDTOConverter;
import com.namics.distrelec.occ.core.security.SecuredAccessConstants;
import com.namics.distrelec.occ.core.v2.annotations.B2ERestricted;
import com.namics.distrelec.occ.core.v2.annotations.BelongToSameCompanyRestriction;
import com.namics.distrelec.occ.core.v2.helper.UsersHelper;

import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.CustomerService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/user-management")
@CacheControl(directive = CacheControlDirective.PRIVATE)
@Tag(name = "User Org Management")
public class DistUserManagementController extends BaseCommerceController {

    private static final Logger LOG = LoggerFactory.getLogger(DistUserManagementController.class);

    private static final List<String> USER_SORT_TYPE_LIST = List.of("byName:asc", "byName:desc", "byStatus:desc", "byYearlyBudget:asc",
                                                                    "byYearlyBudget:desc");

    @Resource(name = "employeeBudgetValidator")
    private Validator employeeBudgetValidator;

    @Resource(name = "registerB2BEmployeeValidator")
    private Validator registerB2BEmployeeValidator;

    @Resource
    private UsersHelper usersHelper;

    @Autowired
    private DistCustomerAccountService distCustomerAccountService;

    @Autowired
    private CustomerDataToWsDTOConverter customerDataToWsDTOConverter;

    @Autowired
    private CustomerService customerService;

    @Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @B2ERestricted
    @RequestMapping(method = RequestMethod.POST)
    @Operation(operationId = "getSubUsersForSearchCriteria", summary = "The list of b2b users for an admin B2B customer", description = "The list of b2b users for an admin B2B customer")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public List<UserWsDTO> getSubUsersForSearchCriteria(@Parameter(description = "Employee search B2B form") @RequestBody final EmployeeSearchB2BForm form,
                                                        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        handleSort(form);
        List<CustomerData> b2bCustomers = getB2bCustomerFacade().searchEmployees(form.getName(), form.getStateCode(), form.getSort());
        return b2bCustomers.stream().map(cust -> customerDataToWsDTOConverter.convert(cust, fields)).collect(Collectors.toList());
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @B2ERestricted
    @PostMapping(value = "/create-employee")
    @Operation(operationId = "createNewEmployee", summary = "Create new B2B employee", description = "Registers new employee.")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewEmployee(@Parameter(description = "Register B2B employee form") @RequestBody final RegisterB2BEmployeeWsDTO registerB2BEmployeeWsDTO)
                                                                                                                                                               throws Exception {
        final Errors errors = validateObject(registerB2BEmployeeWsDTO, "registerB2BEmployeeWsDTO", registerB2BEmployeeValidator);
        employeeBudgetValidator.validate(registerB2BEmployeeWsDTO, errors);
        if (errors.hasErrors()) {
            throw new WebserviceValidationException(errors);
        }
        getUsersHelper().registerNewEmployee(registerB2BEmployeeWsDTO, errors);
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @B2ERestricted
    @BelongToSameCompanyRestriction
    @GetMapping(value = "/employee-details/{customerId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(operationId = "getEmployeeDetails", summary = "Get employee details", description = "Returns B2B employee details.")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public UserWsDTO getEmployeeDetails(@Parameter(description = "Customer ID", required = true) @PathVariable final String customerId,
                                        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
                                                                                                                             throws Exception {
        CustomerModel customer = customerService.getCustomerByCustomerId(customerId);
        String customerUid = customer.getUid();
        return getUsersHelper().getEmployeeDetails(customerUid, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @B2ERestricted
    @BelongToSameCompanyRestriction
    @PostMapping(value = "/edit-employee/{customerId}")
    @Operation(operationId = "updateEmployee", summary = "Edit B2B employee", description = "Updates B2B employee.")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseStatus(HttpStatus.OK)
    public void updateEmployee(@Parameter(description = "Customer ID", required = true) @PathVariable final String customerId,
                               @Parameter(description = "Register B2B employee form") @RequestBody final RegisterB2BEmployeeWsDTO registerB2BEmployeeWsDTO)
                                                                                                                                                            throws Exception {
        final Errors errors = validateObject(registerB2BEmployeeWsDTO, "registerB2BEmployeeWsDTO", registerB2BEmployeeValidator);
        employeeBudgetValidator.validate(registerB2BEmployeeWsDTO, errors);
        if (errors.hasErrors()) {
            throw new WebserviceValidationException(errors);
        }
        CustomerModel customer = customerService.getCustomerByCustomerId(customerId);
        String customerUid = customer.getUid();
        getUsersHelper().updateEmployee(customerUid, registerB2BEmployeeWsDTO, errors);
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @B2ERestricted
    @BelongToSameCompanyRestriction
    @PostMapping(value = "/deactivate/{customerId}")
    @Operation(operationId = "deactivateUser", summary = "Deactivate user", description = "Deactivates user.")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public ResponseEntity<String> deactivateUser(@Parameter(description = "Customer ID", required = true) @PathVariable final String customerId) {
        CustomerModel customer = customerService.getCustomerByCustomerId(customerId);
        String customerUid = customer.getUid();
        if (getDistCustomerAccountService().deactivateCustomer(customerUid)) {
            String message = getMessageSource().getMessage("account.confirmation.employee.deactivated", null, getI18nService().getCurrentLocale());
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } else {
            String message = getMessageSource().getMessage("account.confirmation.employee.deactivatingnotsuccessful", null,
                                                           getI18nService().getCurrentLocale());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @B2ERestricted
    @BelongToSameCompanyRestriction
    @DeleteMapping(value = "/delete/{customerId}")
    @Operation(operationId = "deleteUser", summary = "Delete user", description = "Removes sub-user.")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public ResponseEntity<String> deleteUser(@Parameter(description = "Customer ID", required = true) @PathVariable final String customerId) {
        CustomerModel customer = customerService.getCustomerByCustomerId(customerId);
        String customerUid = customer.getUid();
        if (getB2bCustomerFacade().deleteSubUser(customerUid)) {
            String message = getMessageSource().getMessage("account.confirmation.employee.deleted", null, getI18nService().getCurrentLocale());
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } else {
            String message = getMessageSource().getMessage("account.confirmation.employee.deletingnotsuccessful", null, getI18nService().getCurrentLocale());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @B2ERestricted
    @BelongToSameCompanyRestriction
    @PostMapping(value = "/resend-activation/{customerId}")
    @Operation(operationId = "resendActivation", summary = "Resend activation email", description = "Resends initial password email.")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public ResponseEntity<String> resendActivation(@Parameter(description = "Customer ID", required = true) @PathVariable final String customerId) {
        CustomerModel customer = customerService.getCustomerByCustomerId(customerId);
        String customerUid = customer.getUid();
        try {
            getB2bCustomerFacade().resendSetInitialPasswordEmail(customerUid);
            String message = getMessageSource().getMessage("account.confirmation.employee.resendactivation", null, getI18nService().getCurrentLocale());
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (final DuplicateUidException e) {
            LOG.warn("Resending activation email failed for CustomerUID: {}", customerUid, e);
            String message = getMessageSource().getMessage("form.global.error", null, getI18nService().getCurrentLocale());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }

    private void handleSort(final EmployeeSearchB2BForm form) {
        if (form.getSort() != null) {
            if (!USER_SORT_TYPE_LIST.contains(form.getSort())) {
                form.setSort("byName:asc");
            }
        } else {
            form.setSort("byName:asc");
        }
    }

    public UsersHelper getUsersHelper() {
        return usersHelper;
    }

    public DistCustomerAccountService getDistCustomerAccountService() {
        return distCustomerAccountService;
    }
}
