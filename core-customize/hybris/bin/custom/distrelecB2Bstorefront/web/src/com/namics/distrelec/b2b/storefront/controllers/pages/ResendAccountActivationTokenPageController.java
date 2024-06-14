/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import com.namics.distrelec.b2b.core.service.customer.DistCustomerAccountService;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.RegistrationInfo;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.ThirdPartyConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.forms.ResendAccountActivationTokenForm;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * Controller for the resend account activation token page. Supports requesting a new account activation token via email.
 */
@Controller
@RequestMapping(value = "/register/doubleoptin")
public class ResendAccountActivationTokenPageController extends AbstractPageController {

    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final String REDIRECT_RESEND_ACCOUNT_ACTIVATION_TOKEN = "redirect:/register/doubleoptin/request";

    private static final String REDIRECT_CHECKOUT = "redirect:/checkout/address";
    private static final String RESEND_ACCOUNT_ACTIVATION_TOKEN_CMS_PAGE = "resendAccountActivationToken";

    private static final String TOKEN_RESEND_ATTEMPT = "tokenResendAttempt";
    public static final String SHOW_CAPTCHA = "showResendTokenCaptcha";
    private static final String MAX_TOKEN_RESEND_ATTEMPTS = "distrelec.maxTokenResend.attempts";
    private static final String REDIRECT_WELCOME = "redirect:/welcome";
    private static final Logger LOG = LogManager.getLogger(AbstractPageController.class);
    @Autowired
    private SimpleBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Autowired
    private DistCustomerAccountService distCustomerAccountService;

    @Autowired
    @Qualifier("b2bCartFacade")
    private DistB2BCartFacade b2bCartFacade;

    @RequestMapping(value = "/request", method = RequestMethod.GET)
    public String getAccountActivationTokenRequest(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {

        final int token_resend_attempt = getSessionService().getAttribute(TOKEN_RESEND_ATTEMPT) == null ? 1
                : Integer.parseInt(getSessionService().getAttribute(TOKEN_RESEND_ATTEMPT).toString());
        final int max_token_resend_attempts = getConfigurationService().getConfiguration().getInt(MAX_TOKEN_RESEND_ATTEMPTS, 3);
        if (token_resend_attempt > max_token_resend_attempts) {
            model.addAttribute(SHOW_CAPTCHA, Boolean.TRUE);
        } else {
            model.addAttribute(SHOW_CAPTCHA, Boolean.FALSE);
        }

        model.addAttribute(new ResendAccountActivationTokenForm());
        final ContentPageModel resendActivationPage = getContentPageForLabelOrId(RESEND_ACCOUNT_ACTIVATION_TOKEN_CMS_PAGE);
        storeCmsPageInModel(model, resendActivationPage);
        setUpMetaDataForContentPage(model, resendActivationPage);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                getSimpleBreadcrumbBuilder().getBreadcrumbs(resendActivationPage.getTitle(), resendActivationPage.getTitle(Locale.ENGLISH)));
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.RESETTOKEN);
        return ControllerConstants.Views.Pages.Account.ResendAccountActivationToken;
    }

    @RequestMapping(value = "/request", method = RequestMethod.POST)
    public String newAccountActivationTokenRequest(@Valid
    final ResendAccountActivationTokenForm form, final BindingResult bindingResult, final Model model, final RedirectAttributes redirectModel,
            final HttpServletRequest request) throws CMSItemNotFoundException {

        final int token_resend_attempt = getSessionService().getAttribute(TOKEN_RESEND_ATTEMPT) == null ? 1
                : Integer.parseInt(getSessionService().getAttribute(TOKEN_RESEND_ATTEMPT).toString());
        final int max_token_resend_attempts = getConfigurationService().getConfiguration().getInt(MAX_TOKEN_RESEND_ATTEMPTS, 3);
        if (token_resend_attempt > max_token_resend_attempts) {
            model.addAttribute(SHOW_CAPTCHA, Boolean.TRUE);
        } else {
            model.addAttribute(SHOW_CAPTCHA, Boolean.FALSE);
        }

        final ContentPageModel resendActivationPage = getContentPageForLabelOrId(RESEND_ACCOUNT_ACTIVATION_TOKEN_CMS_PAGE);

        if (bindingResult.hasErrors()) {
            prepareErrorMessage(model, resendActivationPage);
        } else if (token_resend_attempt > max_token_resend_attempts && !getCaptchaUtil().validateReCaptcha(request)) {
            model.addAttribute("captchaError", Boolean.TRUE);
            prepareErrorMessage(model, resendActivationPage);
        } else {
            try {
                getSessionService().setAttribute(TOKEN_RESEND_ATTEMPT, Integer.valueOf(token_resend_attempt + 1));
                getDistCustomerAccountService().resendAccountActivationToken(form.getEmail());
                redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, Collections.singletonList("account.confirmation.token.link.sent"));
            } catch (final UnknownIdentifierException unknownIdentifierException) {
                GlobalMessages.addConfMessage(model, "account.confirmation.token.link.sent.onfail");
                form.setEmail("");
                addGlobalModelAttributes(model, request);
                return ControllerConstants.Views.Pages.Account.ResendAccountActivationToken;
            }
            return REDIRECT_LOGIN;
        }

        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.RESETTOKEN);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                getSimpleBreadcrumbBuilder().getBreadcrumbs(resendActivationPage.getTitle(), resendActivationPage.getTitle(Locale.ENGLISH)));
        return ControllerConstants.Views.Pages.Account.ResendAccountActivationToken;
    }

    @RequestMapping(value = "/activation", method = RequestMethod.GET)
    public String activateAccountRequest(final Model model, @RequestParam(required = false)
    final String token, final RedirectAttributes redirectModel, final HttpServletRequest request) throws CMSItemNotFoundException {
        if (StringUtils.isBlank(token)) {
            return REDIRECT_RESEND_ACCOUNT_ACTIVATION_TOKEN;
        }
        try {
            final B2BCustomerModel customer = (B2BCustomerModel) getDistCustomerAccountService().activateCustomer(token);
            redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, Collections.singletonList("account.confirmation.activated"));
            // add Email address to session
            request.getSession().setAttribute("SPRING_SECURITY_LAST_USERNAME", customer.getEmail());
            redirectModel.addFlashAttribute(ThirdPartyConstants.IntelliAd.REGISTRATION_TRACKING, Boolean.TRUE);
            // populate datalayer
            try {
                final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
                final RegistrationInfo registrationInfo = new RegistrationInfo();
                registrationInfo.setRegType(B2B + ACTIVATION);
                registrationInfo.setRegDate(new Date());
                registrationInfo.setRegMsg(SUCCESS);
                getDistDigitalDatalayerFacade().populateRegistrationType(digitalDatalayer, registrationInfo);
            } catch (Exception ex) {
                LOG.error("Error While Populating DL Registration Data", ex);
            }
            final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
            getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.RESETTOKEN);
            if (b2bCartFacade.hasSessionCart()) {
                return REDIRECT_CHECKOUT + "?qd=" + encodeToUTF8(Base64.encodeBase64String(customer.getEmail().getBytes()));
            }

            return REDIRECT_LOGIN + "?qd=" + encodeToUTF8(Base64.encodeBase64String(customer.getEmail().getBytes()));
        } catch (final TokenInvalidatedException e) {
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("account.confirmation.token.notvalid"));
            return REDIRECT_RESEND_ACCOUNT_ACTIVATION_TOKEN;
        } catch (final IllegalArgumentException e) {
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("account.confirmation.token.notvalid"));
            return REDIRECT_RESEND_ACCOUNT_ACTIVATION_TOKEN;
        }
    }

    /**
     * Prepares the view to display an error message
     * 
     * @param model
     * @param page
     * @throws CMSItemNotFoundException
     */
    protected void prepareErrorMessage(final Model model, final ContentPageModel contentPage) throws CMSItemNotFoundException {
        GlobalMessages.addErrorMessage(model, "form.global.error");
        storeCmsPageInModel(model, contentPage);
        setUpMetaDataForContentPage(model, contentPage);
    }

    public SimpleBreadcrumbBuilder getSimpleBreadcrumbBuilder() {
        return simpleBreadcrumbBuilder;
    }

    public void setSimpleBreadcrumbBuilder(final SimpleBreadcrumbBuilder simpleBreadcrumbBuilder) {
        this.simpleBreadcrumbBuilder = simpleBreadcrumbBuilder;
    }

    public DistCustomerAccountService getDistCustomerAccountService() {
        return distCustomerAccountService;
    }

    public void setDistCustomerAccountService(final DistCustomerAccountService distCustomerAccountService) {
        this.distCustomerAccountService = distCustomerAccountService;
    }
}
