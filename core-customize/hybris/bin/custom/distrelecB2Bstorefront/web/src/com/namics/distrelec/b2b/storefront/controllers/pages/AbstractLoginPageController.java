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

import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.forms.LoginForm;
import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CountryModel;
import org.fest.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

/**
 * Abstract base class for login page controllers.
 */
public abstract class AbstractLoginPageController extends AbstractPageController {
    protected static final String SPRING_SECURITY_LAST_USERNAME = "SPRING_SECURITY_LAST_USERNAME";
    protected static final String SPRING_SECURITY_REMEMBER_ME = "SPRING_SECURITY_REMEMBER_ME";
    protected static final String SHOW_CAPTCHA = WebConstants.SHOW_CAPTCHA;

    @Autowired
    @Qualifier("defaultDistUserFacade")
    private UserFacade userFacade;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private DistCheckoutFacade checkoutFacade;

    public String getLogin(final boolean loginError, final HttpSession session, final Model model, final String user) throws CMSItemNotFoundException {
        if (!getUserService().isAnonymousUser(getUserService().getCurrentUser()) || checkoutFacade.isAnonymousCheckout()) {
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + getSuccessRedirect());
        }

        final LoginForm loginForm = new LoginForm();

        final String username = (String) session.getAttribute(SPRING_SECURITY_LAST_USERNAME);
        if (username != null && !loginError) {
            loginForm.setJ_username(username);
            session.removeAttribute(SPRING_SECURITY_LAST_USERNAME);
        } else {
            loginForm.setJ_username(user);
        }
        final String rememberMe = (String) session.getAttribute(SPRING_SECURITY_REMEMBER_ME);
        if (rememberMe != null) {
            session.removeAttribute(SPRING_SECURITY_REMEMBER_ME);
        }

        // Captcha
        final Boolean showCaptcha = (Boolean) session.getAttribute(SHOW_CAPTCHA);
        if (showCaptcha != null) {
            model.addAttribute("showLoginCaptcha", showCaptcha);
        }

        loginForm.set_spring_security_remember_me(Boolean.valueOf(rememberMe));
        AbstractPageModel loginCmsPage = getLoginCmsPage();
        storeCmsPageInModel(model, loginCmsPage);
        setUpMetaDataForContentPage(model, (ContentPageModel) loginCmsPage);

        final Breadcrumb loginBreadcrumbEntry = new Breadcrumb("#",
                getMessageSource().getMessage("header.link.login", null, getI18nService().getCurrentLocale()), null);
        model.addAttribute("breadcrumbs", Collections.singletonList(loginBreadcrumbEntry));

        if (loginError) {
            if (Boolean.TRUE.equals(getSessionService().getAttribute(WebConstants.LOGIN_WRONG_COUNTRY))) {
                getSessionService().removeAttribute(WebConstants.LOGIN_WRONG_COUNTRY);

                String[] countryLabels = getCountryLabelsFromSession();
                String redirectSite = getRedirectSiteFromSession();
                String countryMessageLabel = "login.error.account.wrong.country." + redirectSite;

                if (messageExists(countryMessageLabel)) {
                    GlobalMessages.addErrorMessage(model, countryMessageLabel, countryLabels);
                } else {
                    GlobalMessages.addErrorMessage(model, "login.error.account.wrong.country", countryLabels);
                }
            } else if (Boolean.TRUE.equals(getSessionService().getAttribute(WebConstants.WRONG_CAPTCHA))) {
                getSessionService().removeAttribute(WebConstants.WRONG_CAPTCHA);
                GlobalMessages.addErrorMessage(model, "validate.error.captcha.invalid");
            } else if (Boolean.TRUE.equals(getSessionService().getAttribute(WebConstants.ACCOUNT_NOT_ACTIVE))) {
                getSessionService().removeAttribute(WebConstants.ACCOUNT_NOT_ACTIVE);

                if (Boolean.TRUE.equals(getSessionService().getAttribute(WebConstants.ACCOUNT_MIGRATED))) {
                    getSessionService().removeAttribute(WebConstants.ACCOUNT_MIGRATED);

                    String[] countryLabels = getCountryLabelsFromSession();
                    GlobalMessages.addErrorMessage(model, "login.login.migrated", countryLabels);
                } else {
                    GlobalMessages.addErrorMessage(model, "login.login.redirect");
                }
            } else if (Boolean.TRUE.equals(getSessionService().getAttribute(WebConstants.DUPLICATE_EMAIL))) {

                GlobalMessages.addErrorMessage(model, "login.error.duplicate.email.title");
            } else {
                GlobalMessages.addErrorMessage(model, "login.error.account.not.found.title");
            }
        }
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePrimaryPageCategoryAndPageType(digitalDatalayer, DigitalDatalayerConstants.PageCategory.LOGIN,
                DigitalDatalayerConstants.PageType.LOGINREGISTRATIONPAGE);
        model.addAttribute(loginForm);

        return getLoginView();
    }

    @ModelAttribute("titles")
    public Collection<TitleData> getTitles() {
        return getUserFacade().getTitles();
    }

    protected abstract String getLoginView();

    protected abstract String getSuccessRedirect();

    protected abstract AbstractPageModel getLoginCmsPage() throws CMSItemNotFoundException;

    private String[] getCountryLabelsFromSession() {
        final CountryModel redirectCountry = getSessionService().getAttribute(WebConstants.REDIRECT_COUNTRY);
        final CMSSiteModel redirectSite = getCmsSiteService().getSiteForCountry(redirectCountry);
        final String correctUrl = getDistSiteBaseUrlResolutionService().getStorefrontWebsiteUrlForSite(redirectSite, false, null);
        final String countryName = redirectCountry.getName();

        ApplicationContext context = Registry.getApplicationContext();
        Locale currentLocale = getI18nService().getCurrentLocale();
        String siteName = null;
        String siteNameLabel = "login.site." + redirectSite.getUid();
        if (messageExists(siteNameLabel)){
            siteName = context.getMessage(siteNameLabel, null, currentLocale);
        }
        getSessionService().removeAttribute(WebConstants.REDIRECT_COUNTRY);
        return Arrays.array(countryName, correctUrl, siteName);
    }

    private String getRedirectSiteFromSession() {
        String redirectSite = getSessionService().getAttribute(WebConstants.REDIRECT_SITE);
        getSessionService().removeAttribute(WebConstants.REDIRECT_SITE);
        return redirectSite;
    }

    private boolean messageExists(String messageLabel){
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
        try{
            messageSourceAccessor.getMessage(messageLabel);
            return true;
        }catch (NoSuchMessageException e){
            // Do nothing
        }
        return false;
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    public void setUserFacade(final UserFacade userFacade) {
        this.userFacade = userFacade;
    }

}
