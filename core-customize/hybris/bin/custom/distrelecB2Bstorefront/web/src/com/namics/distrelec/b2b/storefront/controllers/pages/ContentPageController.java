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

import com.namics.distrelec.b2b.core.model.DistContentPageMappingModel;
import com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator.UrlUtils;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.ContentPageBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.etag.ETagCalculationService;
import com.namics.distrelec.b2b.storefront.etag.ETagHeaderService;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.user.UserFacade;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

/**
 * Controller for a content page.
 */
@Controller
@RequestMapping(value = "/**/cms")
public class ContentPageController extends AbstractPageController {

    private static final Logger LOG = LogManager.getLogger(ContentPageController.class);

    private static final String PAGE_LABEL_PATH_VARIABLE_PATTERN = "/{pageLabel:.*}";

    public static final String EMAIL_PARAMETER_NAME_FOR_UNSUBSCRIBE = "email";

    @Autowired
    @Qualifier("contentPageUrlResolver")
    private DistUrlResolver<ContentPageModel> contentPageUrlResolver;

    @Autowired
    private ContentPageBreadcrumbBuilder contentPageBreadcrumbBuilder;

    @Autowired
    private ETagCalculationService eTagCalculationService;

    @Autowired
    private ETagHeaderService eTagHeaderService;

    @Autowired
    private UserFacade userFacade;

    private UrlPathHelper urlPathHelper;

    @RequestMapping(value = PAGE_LABEL_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    public String get(@PathVariable("pageLabel") final String pageLabel,
                      final Model model,
                      final HttpServletRequest request,
                      final ServletWebRequest servletWebRequest,
                      final HttpServletResponse response)
            throws UnsupportedEncodingException, CMSItemNotFoundException {
        final DistContentPageMappingModel mapping = getContentPageMappingForRequest(request);
        if (mapping != null) {
            LOG.info(String.format("Redirect from {} to {}", mapping.getShortURL(), mapping.getDestinationURL()));
            String prefix = BooleanUtils.isTrue(mapping.getPermanent()) ? REDIRECT_PERMANENT_PREFIX : REDIRECT_PREFIX;
            return addFasterizeCacheControlParameter(prefix + mapping.getDestinationURL());
        }

        final ContentPageModel contentPageModel = getContentPageForLabel(pageLabel);

        if (userFacade.isAnonymousUser()) {
            eTagHeaderService.setCacheControlHeader(response);
            if (contentPageModel != null) {
                String eTag = eTagCalculationService.calculateContentPageETag(contentPageModel);
                if (servletWebRequest.checkNotModified(eTag)) {
                    return null;
                }
            }
        }

        addGlobalModelAttributes(model, request);

        if (contentPageModel != null) {

            addUnsubscribedEmailToModel(request, model);

            final String redirection = checkRequestUrl(request, response, getContentPageUrlResolver().resolve(contentPageModel));
            if (StringUtils.isNotEmpty(redirection)) {
                return redirection;
            }
            storeCmsPageInModel(model, contentPageModel);
            setUpMetaDataForContentPage(model, contentPageModel);
            model.addAttribute(WebConstants.BREADCRUMBS_KEY, getContentPageBreadcrumbBuilder().getBreadcrumbs(contentPageModel, request));
            model.addAttribute("metaRobots", "index, follow");
            model.addAttribute("pageType", PageType.ContentPage);
            return getViewForPage(contentPageModel);
        }

        // No page found - throw exception
        throw new CMSItemNotFoundException("No page with label [" + pageLabel + "] found.");
    }

    protected DistContentPageMappingModel getContentPageMappingForRequest(final HttpServletRequest request) {
        final String lookupPathForRequest = getLookupPathForRequest(request);
        return getCmsPageService().getContentPageMappingForShortURL(lookupPathForRequest);
    }

    protected void addUnsubscribedEmailToModel(final HttpServletRequest request, final Model model) {
        final Optional<String> emailToUnsubscribe = UrlUtils.getParameter(request, EMAIL_PARAMETER_NAME_FOR_UNSUBSCRIBE);
        if (emailToUnsubscribe.isPresent()) {
            LOG.info("emailToUnsubscribe: {}", emailToUnsubscribe.get());
            model.addAttribute(EMAIL_PARAMETER_NAME_FOR_UNSUBSCRIBE, emailToUnsubscribe.get());
        }
    }

    /**
     * Lookup the CMS Content Page for the label
     * 
     * @param label
     *            The page label
     * @return the CMS content page
     */
    protected ContentPageModel getContentPageForLabel(final String label) {
        ContentPageModel contentPageModel;
        try {
            getSessionService().setAttribute(WebConstants.CURRENT_CMS_PAGE, label);
            contentPageModel = getCmsPageService().getPageForLabel(label);
            return contentPageModel;
        } catch (final CMSItemNotFoundException ignore) {
            // Ignore exception
        }
        try {
            getSessionService().setAttribute(WebConstants.CURRENT_CMS_PAGE, "/".concat(label));
            contentPageModel = getCmsPageService().getPageForLabel("/".concat(label));
            return contentPageModel;
        } catch (final CMSItemNotFoundException ignore) {
            // Ignore exception
        }
        return null;
    }

    @ExceptionHandler(CMSItemNotFoundException.class)
    public String handleCMSItemNotFoundException(final Exception exception, final HttpServletRequest request, final HttpServletResponse response) {
        // try to redirect first
        final DistContentPageMappingModel mapping = getContentPageMappingForRequest(request);
        if (mapping != null) {
            String prefix = BooleanUtils.isTrue(mapping.getPermanent()) ? REDIRECT_PERMANENT_PREFIX : REDIRECT_PREFIX;
            return addFasterizeCacheControlParameter(prefix + mapping.getDestinationURL());
        }

        final Object uuid = java.util.UUID.randomUUID();
        final String uuidString = uuid.toString();
        ERROR_PAGE_LOG.error("A technical error occured [uuid: {}].",uuidString);
        request.setAttribute("uuid", uuidString);
        request.setAttribute("exception", exception);

        return FORWARD_PREFIX + "/" + "notFound";
    }

    protected String getLookupPathForRequest(final HttpServletRequest request) {
        // Get the path for this request.
        // Note that the path begins with a '/'
        return getUrlPathHelper().getLookupPathForRequest(request);
    }

    public UrlPathHelper getUrlPathHelper() {
        if (urlPathHelper == null) {
            urlPathHelper = new UrlPathHelper();
        }
        return urlPathHelper;
    }

    public DistUrlResolver<ContentPageModel> getContentPageUrlResolver() {
        return contentPageUrlResolver;
    }

    public void setContentPageUrlResolver(final DistUrlResolver<ContentPageModel> contentPageUrlResolver) {
        this.contentPageUrlResolver = contentPageUrlResolver;
    }

    public ContentPageBreadcrumbBuilder getContentPageBreadcrumbBuilder() {
        return contentPageBreadcrumbBuilder;
    }

    public void setContentPageBreadcrumbBuilder(final ContentPageBreadcrumbBuilder contentPageBreadcrumbBuilder) {
        this.contentPageBreadcrumbBuilder = contentPageBreadcrumbBuilder;
    }

}
