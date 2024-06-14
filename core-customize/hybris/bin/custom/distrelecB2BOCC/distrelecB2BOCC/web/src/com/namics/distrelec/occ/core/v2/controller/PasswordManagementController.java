/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.controller;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.occ.core.security.SecuredAccessConstants;
import com.namics.distrelec.occ.core.user.ws.dto.PasswordUpdateDTO;
import com.namics.distrelec.occ.core.user.ws.dto.PasswordUpdateResult;
import com.namics.distrelec.occ.core.v2.forms.SetInitialPasswordForm;

import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.commercewebservicescommons.annotation.SecurePortalUnauthenticatedAccess;
import de.hybris.platform.commercewebservicescommons.dto.user.ResetPasswordWsDTO;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/{baseSiteId}")
@CacheControl(directive = CacheControlDirective.NO_STORE)
@Tag(name = "Password Management")
public class PasswordManagementController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(PasswordManagementController.class);

    @Resource(name = "setInitialPasswordFormValidator")
    private Validator setInitialPasswordFormValidator;

    @Resource(name = "passwordUpdateDTOValidator")
    private Validator passwordUpdateDTOValidator;

    @Autowired
    private DistCustomerFacade distCustomerFacade;

    @SecurePortalUnauthenticatedAccess
    @Secured({ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT" })
    @RequestMapping(value = "/forgottenpasswordtokens", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(operationId = "doRestorePassword", summary = "Generates a token to restore a customer's forgotten password.", description = "Generates a token to restore a customer's forgotten password.")
    @ApiBaseSiteIdParam
    public void doRestorePassword(
                                  @Parameter(description = "Customer's user id. Customer user id is case insensitive.", required = true) @RequestParam final String userId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("doRestorePassword: user unique property: {}", sanitize(userId));
        }
        try {
            distCustomerFacade.forgottenPassword(userId);
        } catch (final UnknownIdentifierException unknownIdentifierException) {
            LOG.warn("User with unique property: {} does not exist in the database.", sanitize(userId));
        }
    }

    @Secured({ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT" })
    @RequestMapping(value = "checkout/forgottenpasswordtokens", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(operationId = "doRestorePasswordCheckout", summary = "Generates a token to restore a customer's forgotten password on checkout.", description = "Generates a token to restore a customer's forgotten password on checkout.")
    @ApiBaseSiteIdParam
    public void checkoutRestorePassword(
                                        @Parameter(description = "Customer's user id. Customer user id is case insensitive.", required = true) @RequestParam final String userId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("doRestorePassword: user unique property: {}", sanitize(userId));
        }
        try {
            distCustomerFacade.checkoutForgottenPassword(userId);
        } catch (final UnknownIdentifierException unknownIdentifierException) {
            LOG.warn("User with unique property: {} does not exist in the database.", sanitize(userId));
        }
    }

    @SecurePortalUnauthenticatedAccess
    @Secured({ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT" })
    @RequestMapping(value = "/resetpassword", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(operationId = "doResetPassword", summary = "Reset password after customer's clicked forgotten password link.", description = "Reset password after customer's clicked forgotten password link.")
    @ApiBaseSiteIdParam
    public void doResetPassword(
                                @Parameter(description = "Request body parameter that contains details such as token and new password", required = true) @RequestBody final ResetPasswordWsDTO resetPassword)
                                                                                                                                                                                                              throws TokenInvalidatedException {
        LOG.debug("Executing method doResetPassword");
        distCustomerFacade.updatePassword(resetPassword.getToken(), resetPassword.getNewPassword());

    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "/password/update")
    @ApiBaseSiteIdParam
    public PasswordUpdateResult updateUserPassword(@RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        validate(passwordUpdateDTO, "passwordUpdateDTO", passwordUpdateDTOValidator);
        if (!StringUtils.isBlank(passwordUpdateDTO.getToken())) {
            try {
                distCustomerFacade.updatePassword(passwordUpdateDTO.getToken(), passwordUpdateDTO.getPassword());
                distCustomerFacade.confirmDoubleOptforResetPwd(passwordUpdateDTO.getToken());
                return PasswordUpdateResult.SUCCESS;
            } catch (final TokenInvalidatedException e) {
                LOG.debug("Update password failed due to, {}", e.getMessage());
                return PasswordUpdateResult.TOKEN_INVALIDATED;
            } catch (final RuntimeException e) {
                LOG.debug("Update password failed due to, {}", e.getMessage());
                return PasswordUpdateResult.ERROR;
            }
        }
        return PasswordUpdateResult.TOKEN_EMPTY;
    }

    @Secured({ SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @RequestMapping(value = "/account/password/set-initial-password", method = RequestMethod.POST)
    @Operation(operationId = "setInitialPassword", summary = "Set initial password", description = "Set initial password and activate customer")
    @ApiBaseSiteIdParam
    public ResponseEntity<?> setInitialPassword(@Parameter(description = "Set initial password form", required = true) @RequestBody final SetInitialPasswordForm setInitialPasswordForm) {
        validate(setInitialPasswordForm, "setInitialPasswordForm", setInitialPasswordFormValidator);
        try {
            distCustomerFacade.setInitialPasswordAndActivateCustomer(setInitialPasswordForm);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (final TokenInvalidatedException e) {
            LOG.debug("Set password failed due to: " + e.getMessage(), e);
            Errors errors = new BeanPropertyBindingResult(setInitialPasswordForm, "SetInitialPasswordForm");
            errors.rejectValue("token", "setInitialPwd.token.invalidated");
            throw new WebserviceValidationException(errors);
        } catch (final RuntimeException e) {
            LOG.debug("Set password failed due to: " + e.getMessage(), e);
            Errors errors = new BeanPropertyBindingResult(setInitialPasswordForm, "SetInitialPasswordForm");
            errors.rejectValue("token", "setInitialPwd.token.invalid");
            throw new WebserviceValidationException(errors);
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @GetMapping(value = "/change/password/token-validation")
    @Operation(operationId = "isPasswordChangeTokenValid", summary = "Check if the token is valid when changing password", description = "Check if the token is valid when changing password")
    @ApiBaseSiteIdParam
    public boolean isPasswordChangeTokenValid(@Parameter(description = "Token", required = true) @RequestParam final String token) {
        return distCustomerFacade.validateResetPasswordToken(token);
    }

    @Secured({ SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @RequestMapping(value = "/initial/password/token-validation", method = RequestMethod.GET)
    @Operation(operationId = "isTokenValid", summary = "Check if the token is valid", description = "Check if the token is valid")
    @ApiBaseSiteIdParam
    public boolean isInitialPasswordTokenValid(@Parameter(description = "Token", required = true) @RequestParam final String token,
                                               @Parameter(description = "migration", required = true) @RequestParam boolean migration) {
        return distCustomerFacade.validateInitialPasswordToken(token, migration);
    }

    @Secured({ SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @RequestMapping(value = "/account/activation-link", method = RequestMethod.POST)
    @Operation(operationId = "generateActivationLink", summary = "Generates new activation link")
    @ApiBaseSiteIdParam
    public String generateActivationLink(@Parameter(description = "Email", required = true) @RequestParam String email) {
        return distCustomerFacade.generateRsActivationLink(email);
    }
}
