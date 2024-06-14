/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.event.DistFeedbackEvent;
import com.namics.distrelec.b2b.core.util.DistLogUtils;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.feedback.DistFeedbackFacade;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.ContentPageBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.forms.FeedbackForm;
import com.namics.hybris.ffsearch.util.XSSFilterUtil;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commerceservices.url.UrlResolver;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping(FeedbackPageController.FEEDBACK_REQUEST_MAPPING_URL)
public class FeedbackPageController extends AbstractPageController {
    private static final Logger LOG = LogManager.getLogger(FeedbackPageController.class);

    protected static final String FEEDBACK_REQUEST_MAPPING_URL = "/**/feedback";

    private static final String FEEDBACK_CMS_PAGE = "feedback";
    private static final String SEARCH_FEEDBACK_SENT_PAGE_ID = "searchFeedbackSent";

    @Autowired
    @Qualifier("contentPageUrlResolver")
    private UrlResolver<ContentPageModel> contentPageUrlResolver;

    @Autowired
    private DistFeedbackFacade distFeedbackFacade;

    @Autowired
    private ContentPageBreadcrumbBuilder contentPageBreadcrumbBuilder;

    @RequestMapping(method = RequestMethod.GET)
    public String feedbackPage(final Model model, final HttpServletRequest request, final HttpServletResponse response)
            throws CMSItemNotFoundException, UnsupportedEncodingException {
        final String redirection = checkRequestUrl(request, response, getContentPageUrlResolver().resolve(getFeedbackPage()));
        if (StringUtils.isNotEmpty(redirection)) {
            return redirection;
        }

        addGlobalModelAttributes(model, request);
        storeCmsPageInModel(model, getFeedbackPage());
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, getContentPageBreadcrumbBuilder().getBreadcrumbs(getFeedbackPage(), request));
        return getViewForPage(model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String sendFeedback(final Model model, @Valid
    final FeedbackForm feedback, final BindingResult bindingResult, final HttpServletRequest request) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        try {
            final boolean isCaptchaValid = getCaptchaUtil().validateReCaptcha(request);
            if (isCaptchaValid && !bindingResult.hasErrors()) {
                getDistFeedbackFacade().sendFeedback(createEvent(feedback));
                storeCmsPageInModel(model, getFeedbackSentPage());
            } else {
                if (bindingResult.hasErrors()) {
                    GlobalMessages.addErrorMessage(model, "formfeedback.failed");
                } else if (!isCaptchaValid) {
                    GlobalMessages.addErrorMessage(model, "formfeedback.captchaError");
                }
                model.addAttribute("sentFeedback", feedback);
                storeCmsPageInModel(model, getFeedbackPage());
            }
        } catch (final IllegalArgumentException e) {
            DistLogUtils.logWarn(LOG, "{} {} Some error hase occured while sending feedback email for emailID: {} ", e, ErrorLogCode.EMAIL_ERROR,
                    ErrorSource.HYBRIS, feedback.getEmail());
            GlobalMessages.addErrorMessage(model, "formfeedback.failed");
            model.addAttribute("sentFeedback", feedback);
            storeCmsPageInModel(model, getFeedbackPage());
        }

        model.addAttribute(WebConstants.BREADCRUMBS_KEY, getContentPageBreadcrumbBuilder().getBreadcrumbs(getFeedbackPage(), request));
        return getViewForPage(model);
    }

    protected DistFeedbackEvent createEvent(final FeedbackForm form) {
        final DistFeedbackEvent event = new DistFeedbackEvent(XSSFilterUtil.filter(form.getName()), XSSFilterUtil.filter(form.getEmail()),
                XSSFilterUtil.filter(form.getPhone()), XSSFilterUtil.filter(form.getFeedback()));
        event.setEmailDisplayName(getMessageSource().getMessage("feedback.displayName", null, "Feedback", getI18nService().getCurrentLocale()));
        event.setEmailSubjectMsg(getMessageSource().getMessage("feedback.emailSubject", null, "Feedback", getI18nService().getCurrentLocale()));
        event.setFromDisplayName(getMessageSource().getMessage("feedback.fromDisplayName", null, "Feedback from", getI18nService().getCurrentLocale()));
        return event;
    }

    protected ContentPageModel getFeedbackPage() throws CMSItemNotFoundException {
        return getContentPageForLabelOrId(FEEDBACK_CMS_PAGE);
    }

    protected ContentPageModel getFeedbackSentPage() throws CMSItemNotFoundException {
        return getContentPageForLabelOrId(SEARCH_FEEDBACK_SENT_PAGE_ID);
    }

    public UrlResolver<ContentPageModel> getContentPageUrlResolver() {
        return contentPageUrlResolver;
    }

    public void setContentPageUrlResolver(final UrlResolver<ContentPageModel> contentPageUrlResolver) {
        this.contentPageUrlResolver = contentPageUrlResolver;
    }

    public DistFeedbackFacade getDistFeedbackFacade() {
        return distFeedbackFacade;
    }

    public void setDistFeedbackFacade(final DistFeedbackFacade distFeedbackFacade) {
        this.distFeedbackFacade = distFeedbackFacade;
    }

    public ContentPageBreadcrumbBuilder getContentPageBreadcrumbBuilder() {
        return contentPageBreadcrumbBuilder;
    }

    public void setContentPageBreadcrumbBuilder(final ContentPageBreadcrumbBuilder contentPageBreadcrumbBuilder) {
        this.contentPageBreadcrumbBuilder = contentPageBreadcrumbBuilder;
    }

}
