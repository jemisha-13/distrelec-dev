/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.newsletter.DistNewsletterFacade;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.b2b.facades.user.data.DistDepartmentData;
import com.namics.distrelec.b2b.facades.user.data.DistFunctionData;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.forms.NewsletterSubscribeForm;
import com.namics.distrelec.b2b.storefront.response.NewsletterSubscribeResponse;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.user.data.TitleData;

/**
 * Page controller for newsletter registration.
 *
 * @author pnueesch, Namics AG
 * @since Distrelec 1.0
 */
@Controller
@RequestMapping("/**/newsletter")
public class NewsletterRegisterPageController extends AbstractPageController {

    private static final Logger LOG = LogManager.getLogger(NewsletterRegisterPageController.class);

    public static final String NEWSLETTER_REGISTER_CMS_PAGE = "NewsletterRegisterPage";

    public static final String NEWSLETTER_SUCCESS_CMS_PAGE = "NewsletterSuccessPage";

    public static final String NEWSLETTER_CONFIRMATION_CMS_PAGE = "NewsletterConfirmationPage";

    public static final String NEWSLETTER_REGISTER_FORM = "newsletterRegisterForm";

    @Autowired
    private SimpleBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Autowired
    private DistNewsletterFacade newsletterFacade;

    @Autowired
    private DistUserFacade distUserFacade;

    @ModelAttribute("titles")
    public Collection<TitleData> getTitles() {
        return getDistUserFacade().getTitles();
    }

    @ModelAttribute("roles")
    public List<DistFunctionData> getRoles() {
        return getNewsletterFacade().getRoles();
    }

    @ModelAttribute("divisions")
    public List<DistDepartmentData> getDivisions() {
        return getNewsletterFacade().getDivisions();
    }

    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
    public ResponseEntity<NewsletterSubscribeResponse> subscribeNewsletter(@Valid NewsletterSubscribeForm form, BindingResult bindingResult,
                                                                           HttpServletRequest request) {
        try {
            boolean isCaptchaValid = getCaptchaUtil().validateReCaptcha(request);
            if (bindingResult.hasErrors()) {
                String errorMessage = getMessageSource().getMessage("form.global.error", null,
                                                                    getI18nService().getCurrentLocale());
                NewsletterSubscribeResponse response = new NewsletterSubscribeResponse.Builder()
                                                                                                .withErrorMessage(errorMessage).build();
                return badRequest().body(response);
            }

            if (!isCaptchaValid) {
                String errorMessage = getMessageSource().getMessage("form.captcha.error", null,
                                                                    getI18nService().getCurrentLocale());
                NewsletterSubscribeResponse response = new NewsletterSubscribeResponse.Builder()
                                                                                                .withErrorMessage(errorMessage).build();
                return badRequest().body(response);
            }

            boolean isNewsletterSubscriptionHandled = false;

            if (isNewsletterSubscriptionHandled) {
                return ok(new NewsletterSubscribeResponse.Builder()
                                                                   .isDoubleOptIn(isShowDoubleOptInPopupForEmail(form.getEmail())).withErrorMessage("")
                                                                   .build());
            } else {
                String errorMessage = getMessageSource().getMessage("newsletter.error.general", null,
                                                                    getI18nService().getCurrentLocale());
                NewsletterSubscribeResponse response = new NewsletterSubscribeResponse.Builder()
                                                                                                .withErrorMessage(errorMessage).build();
                return badRequest().body(response);
            }

        } catch (Exception e) {
            LOG.error("Error in Newsletter Subscription::" + e.getMessage());
            String errorMessage = getMessageSource().getMessage("newsletter.error.general", null,
                                                                getI18nService().getCurrentLocale());
            NewsletterSubscribeResponse response = new NewsletterSubscribeResponse.Builder()
                                                                                            .withErrorMessage(errorMessage).build();
            return badRequest().body(response);
        }
    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String successSubscribtion(final Model model, final HttpServletRequest request)
                                                                                           throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        populatePage(model, NEWSLETTER_SUCCESS_CMS_PAGE);
        return getSuccessView();
    }

    @RequestMapping(value = "/confirm", method = RequestMethod.GET)
    public String confirmSubscribtion(final Model model, final HttpServletRequest request)
                                                                                           throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        populatePage(model, NEWSLETTER_CONFIRMATION_CMS_PAGE);
        return getConfirmationView();
    }

    protected void populatePage(final Model model, final String pageId) throws CMSItemNotFoundException {
        final ContentPageModel contentPage = getContentPageForLabelOrId(pageId);
        storeCmsPageInModel(model, contentPage);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, getSimpleBreadcrumbBuilder()
                                                                                     .getBreadcrumbs(contentPage.getTitle(),
                                                                                                     contentPage.getTitle(Locale.ENGLISH)));
    }

    protected String getView() {
        return ControllerConstants.Views.Pages.Newsletter.NewsletterRegisterPage;
    }

    protected String getSuccessView() {
        return ControllerConstants.Views.Pages.Newsletter.NewsletterSuccessPage;
    }

    protected String getConfirmationView() {
        return ControllerConstants.Views.Pages.Newsletter.NewsletterConfirmationPage;
    }

    public SimpleBreadcrumbBuilder getSimpleBreadcrumbBuilder() {
        return simpleBreadcrumbBuilder;
    }

    public void setSimpleBreadcrumbBuilder(final SimpleBreadcrumbBuilder simpleBreadcrumbBuilder) {
        this.simpleBreadcrumbBuilder = simpleBreadcrumbBuilder;
    }

    public DistNewsletterFacade getNewsletterFacade() {
        return newsletterFacade;
    }

    public void setNewsletterFacade(final DistNewsletterFacade newsletterFacade) {
        this.newsletterFacade = newsletterFacade;
    }

    public DistUserFacade getDistUserFacade() {
        return distUserFacade;
    }

    public void setDistUserFacade(final DistUserFacade distUserFacade) {
        this.distUserFacade = distUserFacade;
    }

}
