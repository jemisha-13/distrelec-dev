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
 *
*/
package com.namics.distrelec.b2b.storefront.controllers.pages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import de.hybris.platform.commercefacades.user.data.CustomerData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.util.DistLogUtils;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.forms.UpdateUserProfileForm;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;

//  Register Controller for mobile. Handles login and register for the account flow.

@Controller
@RequestMapping(value = "/register")
public class RegisterPageController extends AbstractRegisterPageController {
    @Autowired
    private Validator validator;

    @Override
    protected AbstractPageModel getCmsPage() throws CMSItemNotFoundException {
        return getContentPageForLabelOrId("register");
    }

    @Override
    protected String getView() {
        return ControllerConstants.Views.Pages.Account.AccountRegisterPage;
    }

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
    public String doRegister(@RequestParam(value = "emailToRegister", defaultValue = "") String emailToRegister, final Model model,
                             final HttpServletRequest request) throws CMSItemNotFoundException {
        request.getSession().setAttribute("emailToRegister", emailToRegister);
        addGlobalModelAttributes(model, request);
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + REGISTRATION);
    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
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

    @RequestMapping(value = "/updateuserInfo", method = RequestMethod.POST, produces = "application/json")
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
                customerData.setDepartment(updateUserProfileForm.getDepartmentCode());
                getCustomerFacade().updateSelectedProfileInformation(customerData);
            }
        } catch (final Exception ioe) {
            DistLogUtils.logError(LOG, "{} {} An error has occur while updating UserProfile object", ioe, ErrorLogCode.UPDATE_USER_ERROR, ErrorSource.HYBRIS);
            model.addAttribute("errorMsgKey", "updateUserProfileForm.process.error");
        }
        return ControllerConstants.Views.Fragments.Checkout.UpdateUserProfilePopup;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

}
