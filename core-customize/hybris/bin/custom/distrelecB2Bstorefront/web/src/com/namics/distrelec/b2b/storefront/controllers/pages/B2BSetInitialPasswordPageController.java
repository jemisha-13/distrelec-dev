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

import java.util.Collections;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.namics.distrelec.b2b.core.service.customer.DistCustomerAccountService;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.forms.SetInitialPwdForm;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Controller for the forgotten password pages. Supports requesting a password reset email as well as changing the password once you have
 * got the token that was sent via email.
 */
@Controller
@RequestMapping(value = "/account/password")
public class B2BSetInitialPasswordPageController extends AbstractPageController {
    private static final Logger LOG = Logger.getLogger(B2BSetInitialPasswordPageController.class);

    private static final String REDIRECT_LOGIN = "redirect:/login";

    private static final String REDIRECT_HOME = "redirect:/";

    private static final String SET_INITIAL_PWD_CMS_PAGE = "setInitialPassword";

    @Autowired
    private SimpleBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Autowired
    private ModelService modelService;

    @Autowired
    private DistCustomerAccountService distCustomerAccountService;

    @Autowired
    @Qualifier("httpSessionRequestCache")
    private HttpSessionRequestCache httpSessionRequestCache;

    @RequestMapping(value = "/setinitialpw", method = RequestMethod.GET)
    public String setInitialPassword(@RequestParam(required = false) final String token, final Model model,
                                     final HttpServletRequest request) throws CMSItemNotFoundException {
        if (StringUtils.isBlank(token)) {
            return REDIRECT_HOME;
        }
        addGlobalModelAttributes(model, request);
        if (!distCustomerAccountService.isInitialPasswordTokenValid(token, false)) {
            GlobalMessages.addErrorMessage(model, "setInitialPwd.token.invalidWithLink");
        }
        final SetInitialPwdForm form = new SetInitialPwdForm();
        form.setToken(token);
        model.addAttribute(form);
        final ContentPageModel initialPasswordPage = getContentPageForLabelOrId(SET_INITIAL_PWD_CMS_PAGE);
        storeCmsPageInModel(model, initialPasswordPage);
        setUpMetaDataForContentPage(model, initialPasswordPage);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                           getSimpleBreadcrumbBuilder().getBreadcrumbs(initialPasswordPage.getTitle(), initialPasswordPage.getTitle(Locale.ENGLISH)));
        return ControllerConstants.Views.Pages.Account.SetInitialPasswordPage;
    }

    @RequestMapping(value = "/setinitialpw", method = RequestMethod.POST)
    public String setInitialPassword(@Valid final SetInitialPwdForm form, final BindingResult bindingResult, final Model model,
                                     final RedirectAttributes redirectModel,
                                     final HttpServletRequest request) throws CMSItemNotFoundException {
        if (bindingResult.hasErrors()) {
            addGlobalModelAttributes(model, request);
            prepareErrorMessage(model, SET_INITIAL_PWD_CMS_PAGE, "form.global.error");
            return ControllerConstants.Views.Pages.Account.SetInitialPasswordPage;
        }
        if (!StringUtils.isBlank(form.getToken())) {
            try {
                distCustomerAccountService.setInitialPasswordAndActivateCustomer(form.getToken(), form.getPwd(), false);
                redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, Collections.singletonList("account.confirmation.password.updated"));
            } catch (final TokenInvalidatedException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("set password failed due to, " + e.getMessage(), e);
                }
                addGlobalModelAttributes(model, request);
                prepareErrorMessage(model, SET_INITIAL_PWD_CMS_PAGE, "setInitialPwd.token.invalidated");
                return ControllerConstants.Views.Pages.Account.SetInitialPasswordPage;
            } catch (final RuntimeException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("set password failed due to, " + e.getMessage(), e);
                }
                addGlobalModelAttributes(model, request);
                prepareErrorMessage(model, SET_INITIAL_PWD_CMS_PAGE, "setInitialPwd.token.invalid");
                return ControllerConstants.Views.Pages.Account.SetInitialPasswordPage;
            }
        }
        getSessionService().setAttribute(WebConstants.DO_NOT_STORE_REFERRER, Boolean.TRUE);
        return REDIRECT_LOGIN;
    }

    /**
     * Prepares the view to display an error message
     * 
     * @param model
     * @param pageId
     * @param error
     * @throws CMSItemNotFoundException
     */
    protected void prepareErrorMessage(final Model model, final String pageId, final String error) throws CMSItemNotFoundException {
        GlobalMessages.addErrorMessage(model, error);
        final ContentPageModel page = getContentPageForLabelOrId(pageId);
        storeCmsPageInModel(model, page);
        setUpMetaDataForContentPage(model, page);
    }

    public SimpleBreadcrumbBuilder getSimpleBreadcrumbBuilder() {
        return simpleBreadcrumbBuilder;
    }

    public void setSimpleBreadcrumbBuilder(final SimpleBreadcrumbBuilder simpleBreadcrumbBuilder) {
        this.simpleBreadcrumbBuilder = simpleBreadcrumbBuilder;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public DistCustomerAccountService getDistCustomerAccountService() {
        return distCustomerAccountService;
    }

    public void setDistCustomerAccountService(final DistCustomerAccountService distCustomerAccountService) {
        this.distCustomerAccountService = distCustomerAccountService;
    }

    public HttpSessionRequestCache getHttpSessionRequestCache() {
        return httpSessionRequestCache;
    }

    public void setHttpSessionRequestCache(final HttpSessionRequestCache httpSessionRequestCache) {
        this.httpSessionRequestCache = httpSessionRequestCache;
    }

}
