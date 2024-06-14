/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.namics.distrelec.b2b.core.event.DistSupportEvent;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.support.DistSupportFacade;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.ContentPageBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.forms.SupportForm;
import com.namics.hybris.ffsearch.util.XSSFilterUtil;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

@Controller
@RequestMapping("/**/support")
public class SupportPageController extends AbstractPageController {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(SupportPageController.class);

    private static final String SUPPORT_CMS_PAGE = "support";

    @Autowired
    @Qualifier("contentPageUrlResolver")
    private UrlResolver<ContentPageModel> contentPageUrlResolver;

    @Autowired
    private DistUserFacade distUserFacade;

    @Autowired
    private DistSupportFacade distSupportFacade;

    @Autowired
    private ContentPageBreadcrumbBuilder contentPageBreadcrumbBuilder;

    @ModelAttribute("support")
    public SupportForm loadModel() {
        return new SupportForm();
    }

    @ModelAttribute("titles")
    public Collection<TitleData> getTitles() {
        return getDistUserFacade().getTitles();
    }

    @ModelAttribute("contactByValues")
    public Collection<String> getContactByValues() {
        return getDistSupportFacade().getContactByValues();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String show(final Model model, final HttpServletRequest request, final HttpServletResponse response)
            throws CMSItemNotFoundException, UnsupportedEncodingException {
        final String redirection = checkRequestUrl(request, response, getContentPageUrlResolver().resolve(getSupportPage()));
        if (StringUtils.isNotEmpty(redirection)) {
            return redirection;
        }

        addGlobalModelAttributes(model, request);
        populatePage(model, request);
        
        // Adobe Analytics- Populate DTM breadcrumbs coming from CMS
        populateDTMCMSBreadcrumbs(model, request);
     	
        return getViewForPage(model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String send(final Model model, @ModelAttribute("support")
    @Valid
    final SupportForm support, final BindingResult bindingResult, final HttpServletRequest request) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        final boolean isCaptchaValid = getCaptchaUtil().validateReCaptcha(request);
        if (isCaptchaValid && !bindingResult.hasErrors()) {
            getDistSupportFacade().sendSupport(createEvent(support));
            GlobalMessages.addConfMessage(model, "support.success");
            model.addAttribute("support", new SupportForm());
        } else {
            if (bindingResult.hasErrors()) {
                GlobalMessages.addErrorMessage(model, "support.failed");
            } else if (!isCaptchaValid) {
                GlobalMessages.addErrorMessage(model, "support.captchaError");
            }
        }

        populatePage(model, request);
        return getViewForPage(model);
    }

    protected DistSupportEvent createEvent(final SupportForm form) {
        final DistSupportEvent event = new DistSupportEvent(XSSFilterUtil.filter(form.getTitle()), XSSFilterUtil.filter(form.getFirstname()),
                XSSFilterUtil.filter(form.getLastname()), XSSFilterUtil.filter(form.getEmail()), XSSFilterUtil.filter(form.getPhone()),
                XSSFilterUtil.filter(form.getComment()), XSSFilterUtil.filter(form.getContactBy()));
        event.setEmailDisplayName(getMessageSource().getMessage("support.displayName", null, "Address Change", getI18nService().getCurrentLocale()));
        event.setEmailSubjectMsg(getMessageSource().getMessage("support.emailSubject", null, "Distrelec Feedback", getI18nService().getCurrentLocale()));
        event.setFromDisplayName(getMessageSource().getMessage("support.fromDisplayName", null, "Support from", getI18nService().getCurrentLocale()));
        event.setContactBy(getMessageSource().getMessage("support.contactBy." + form.getContactBy(), null, "Support by", getI18nService().getCurrentLocale()));
        return event;
    }

    protected ContentPageModel getSupportPage() throws CMSItemNotFoundException {
        return getContentPageForLabelOrId(SUPPORT_CMS_PAGE);
    }

    protected void populatePage(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        final ContentPageModel supportPage = getSupportPage();
        storeCmsPageInModel(model, supportPage);
        setUpMetaDataForContentPage(model, supportPage);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, getContentPageBreadcrumbBuilder().getBreadcrumbs(supportPage, request));
    }

    public UrlResolver<ContentPageModel> getContentPageUrlResolver() {
        return contentPageUrlResolver;
    }

    public void setContentPageUrlResolver(final UrlResolver<ContentPageModel> contentPageUrlResolver) {
        this.contentPageUrlResolver = contentPageUrlResolver;
    }

    public DistUserFacade getDistUserFacade() {
        return distUserFacade;
    }

    public void setDistUserFacade(final DistUserFacade distUserFacade) {
        this.distUserFacade = distUserFacade;
    }

    public DistSupportFacade getDistSupportFacade() {
        return distSupportFacade;
    }

    public void setDistSupportFacade(final DistSupportFacade distSupportFacade) {
        this.distSupportFacade = distSupportFacade;
    }

    public ContentPageBreadcrumbBuilder getContentPageBreadcrumbBuilder() {
        return contentPageBreadcrumbBuilder;
    }

    public void setContentPageBreadcrumbBuilder(final ContentPageBreadcrumbBuilder contentPageBreadcrumbBuilder) {
        this.contentPageBreadcrumbBuilder = contentPageBreadcrumbBuilder;
    }

}
