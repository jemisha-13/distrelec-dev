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

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.util.DistLogUtils;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.customer.exceptions.DuplicateEmailException;
import com.namics.distrelec.b2b.facades.storesession.data.ShoppingSettingsCookieData;
import com.namics.distrelec.b2b.storefront.attributes.Attributes;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.controllers.util.ShopSettingsUtil;
import com.namics.distrelec.b2b.storefront.forms.ForgottenPwdForm;
import com.namics.distrelec.b2b.storefront.forms.UpdatePwdForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Collections;
import java.util.Locale;

/**
 * Controller for the forgotten password pages. Supports requesting a password reset email as well as changing the password once you have
 * got the token that was sent via email.
 */
@Controller
@RequestMapping(value = "/login/pw")
public class PasswordResetPageController extends AbstractPageController {
    private static final Logger LOG = LogManager.getLogger(PasswordResetPageController.class);

    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final String REDIRECT_HOME = "redirect:/";

    private static final String UPDATE_PWD_CMS_PAGE = "updatePassword";
    private static final String FORGOT_PASSWORD_CMS_PAGE = "forgottenPassword";
    private static final String PW_RESET_ATTEMPT = "pwResetAttempt";
    public static final String SHOW_CAPTCHA = "showResetCaptcha";
    private static final String MAX_PW_RESET_ATTEMPTS = "distrelec.maxPwReset.attempts";

    @Autowired
    private SimpleBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Autowired
    private DistCustomerFacade customerFacade;

    @GetMapping(value = "/request")
    public String getPasswordRequest(final Model model, final HttpSession session, final HttpServletRequest request) throws CMSItemNotFoundException {

        final int pw_reset_attempt = getSessionService().getAttribute(PW_RESET_ATTEMPT) == null ? 1
                : Integer.parseInt(getSessionService().getAttribute(PW_RESET_ATTEMPT).toString());
        final int max_pw_reset_attempts = getConfigurationService().getConfiguration().getInt(MAX_PW_RESET_ATTEMPTS, 3);
        if (pw_reset_attempt > max_pw_reset_attempts) {
            model.addAttribute(SHOW_CAPTCHA, Boolean.TRUE);
        } else {
            model.addAttribute(SHOW_CAPTCHA, Boolean.FALSE);
        }

        final String username = (String) session.getAttribute("SPRING_SECURITY_LAST_USERNAME");
        final ForgottenPwdForm form = new ForgottenPwdForm();
        if (username != null && username.contains("@")) {
            form.setEmail(username);
        }
        model.addAttribute(form);

        preparePage(model, FORGOT_PASSWORD_CMS_PAGE);
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.RESETPASSWORDPAGE);
        return ControllerConstants.Views.Pages.Password.PasswordResetRequestPage;
    }

    @PostMapping(value = "/request")
    public String requestPassword(@Valid final ForgottenPwdForm form, final BindingResult bindingResult, final Model model, final RedirectAttributes redirectModel, final HttpServletRequest request,
                                  final HttpSession session) throws CMSItemNotFoundException {
        getSessionService().setAttribute(WebConstants.DO_NOT_STORE_REFERRER, Boolean.TRUE);
        return passwordRequest(model, form, bindingResult, redirectModel, request, session);
    }

    protected String passwordRequest(final Model model, final ForgottenPwdForm form, final BindingResult bindingResult, final RedirectAttributes redirectModel,
                                     final HttpServletRequest request, final HttpSession session) throws CMSItemNotFoundException {
        final int pw_reset_attempt = getSessionService().getAttribute(PW_RESET_ATTEMPT) == null ? 1
                : Integer.parseInt(getSessionService().getAttribute(PW_RESET_ATTEMPT).toString());
        final int max_pw_reset_attempts = getConfigurationService().getConfiguration().getInt(MAX_PW_RESET_ATTEMPTS, 3);
        if (pw_reset_attempt > max_pw_reset_attempts) {
            model.addAttribute(SHOW_CAPTCHA, Boolean.TRUE);
        } else {
            model.addAttribute(SHOW_CAPTCHA, Boolean.FALSE);
        }

        if (bindingResult.hasErrors()) {
            preparePage(model, FORGOT_PASSWORD_CMS_PAGE);
        } else if (pw_reset_attempt > max_pw_reset_attempts && !getCaptchaUtil().validateReCaptcha(request)) {
            model.addAttribute("captchaError", Boolean.TRUE);
            preparePage(model, FORGOT_PASSWORD_CMS_PAGE);
        } else {
            try {
                getSessionService().setAttribute(PW_RESET_ATTEMPT, Integer.valueOf(pw_reset_attempt + 1));
                getCustomerFacade().forgottenPassword(StringUtils.lowerCase(form.getEmail()));
                redirectModel.addFlashAttribute(GlobalMessages.INFO_MESSAGES_HOLDER, "account.confirmation.forgotten.password.link.sent");
            } catch (final UnknownIdentifierException uie) {
                DistLogUtils.logError(LOG, "{} {} An error occured for passwordRequest for Email: {}", uie, ErrorLogCode.EMAIL_ERROR, ErrorSource.HYBRIS,
                        form.getEmail());
                redirectModel.addFlashAttribute(GlobalMessages.INFO_MESSAGES_HOLDER, "account.confirmation.forgotten.password.link.sent.onfail");
            } catch (final DuplicateEmailException duplicateEmailException) {
                LOG.error("An error occured due to Duplicate Email: {}", form.getEmail());
                redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, "account.confirmation.forgotten.password.duplicate.email");
            }

            return REDIRECT_LOGIN;
        }
        final String username = (String) session.getAttribute("SPRING_SECURITY_LAST_USERNAME");
        if (!model.containsAttribute("forgottenPwdForm")) {
            final ForgottenPwdForm userForm = new ForgottenPwdForm();
            if (username != null && username.contains("@")) {
                userForm.setEmail(username);
            }
            model.addAttribute(userForm);
        }

        addGlobalModelAttributes(model, request);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, simpleBreadcrumbBuilder.getBreadcrumbsFromProperty("forgottenPwd.title"));
        return ControllerConstants.Views.Pages.Password.PasswordResetRequestPage;
    }

    @GetMapping(value = "/change")
    public String getChangePassword(@RequestParam(required = false) final String token, @RequestParam(value = "lang", required = false) final String language, final Model model, final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectModel, final HttpSession session) throws CMSItemNotFoundException {
        boolean valid = customerFacade.validateResetPasswordToken(token);
        ShoppingSettingsCookieData shoppingSettingsCookieData = getShopSettingsCookie(request);
        if (shoppingSettingsCookieData != null) {
            shoppingSettingsCookieData.setLanguage(language);
            Attributes.SHOP_SETTINGS.setValue(request, response, ShopSettingsUtil.createCookieWithSessionValues(shoppingSettingsCookieData.getChannel(), shoppingSettingsCookieData.getLanguage(), shoppingSettingsCookieData.getCountry(),
                    shoppingSettingsCookieData.getCookieMessageConfirmed(), shoppingSettingsCookieData.getUseIconView(), shoppingSettingsCookieData.getUseListView(), shoppingSettingsCookieData.getUseDetailView(), shoppingSettingsCookieData.getAutoApplyFilter(), shoppingSettingsCookieData.getItemsPerPage()));
        }

        if (valid) {
            if (StringUtils.isBlank(token)) {
                return REDIRECT_HOME;
            }
            addGlobalModelAttributes(model, request);
            final UpdatePwdForm form = new UpdatePwdForm();
            form.setToken(token);
            model.addAttribute(form);
            preparePage(model, UPDATE_PWD_CMS_PAGE);
            return ControllerConstants.Views.Pages.Password.PasswordResetChangePage;
        }
        GlobalMessages.addErrorMessage(model, "account.confirmation.forgotten.password.link.expired");
        return getPasswordRequest(model, session, request);
    }

    @PostMapping(value = "/change")
    public String changePassword(@Valid final UpdatePwdForm form, final BindingResult bindingResult, final Model model, final RedirectAttributes redirectModel, final HttpServletRequest request)
            throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        if (bindingResult.hasErrors()) {
            preparePage(model, UPDATE_PWD_CMS_PAGE, true);
            return ControllerConstants.Views.Pages.Password.PasswordResetChangePage;
        }
        if (!StringUtils.isBlank(form.getToken())) {
            try {
                getCustomerFacade().updatePassword(form.getToken(), form.getPwd());
                customerFacade.confirmDoubleOptforResetPwd(form.getToken());
                redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, Collections.singletonList("account.confirmation.password.updated"));
            } catch (final TokenInvalidatedException e) {
                LOG.debug("Update password failed due to, {}", e.getMessage());
                redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("updatePwd.token.invalidated"));
                return ControllerConstants.Views.Pages.Password.PasswordResetChangePage;
            } catch (final RuntimeException e) {
                LOG.debug("Update password failed due to, {}", e.getMessage());
                redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("updatePwd.token.invalid"));
                return ControllerConstants.Views.Pages.Password.PasswordResetChangePage;
            }
        }
        getSessionService().setAttribute(WebConstants.DO_NOT_STORE_REFERRER, Boolean.TRUE);
        return REDIRECT_LOGIN;
    }

    protected void preparePage(final Model model, final String page, final boolean showErrorMsg) throws CMSItemNotFoundException {
        if (showErrorMsg) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
        }
        final ContentPageModel contentPage = getContentPageForLabelOrId(page);
        storeCmsPageInModel(model, contentPage);
        setUpMetaDataForContentPage(model, contentPage);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, simpleBreadcrumbBuilder.getBreadcrumbs(contentPage.getTitle(), contentPage.getTitle(Locale.ENGLISH)));
    }

    private void preparePage(final Model model, final String page) throws CMSItemNotFoundException {
        preparePage(model, page, false);
    }

}
