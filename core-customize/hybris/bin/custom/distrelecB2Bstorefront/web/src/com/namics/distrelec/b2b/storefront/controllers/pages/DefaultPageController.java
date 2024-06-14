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
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.ContentPageBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Error handler to show a CMS managed error page. This is the catch-all controller that handles all GET requests that are not handled by
 * other controllers.
 */
@Controller
public class DefaultPageController extends AbstractPageController {
    private static final String ERROR_CMS_PAGE = "notFound";

    private UrlPathHelper urlPathHelper;

    @Autowired
    private SimpleBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Autowired
    private ContentPageBreadcrumbBuilder contentPageBreadcrumbBuilder;

    @RequestMapping(value = "/**", method = RequestMethod.GET)
    public String get(final Model model, final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException, IOException {
        final DistContentPageMappingModel mapping = getContentPageMappingForRequest(request);
        if (mapping != null) {
            String prefix = BooleanUtils.isTrue(mapping.getPermanent()) ? REDIRECT_PERMANENT_PREFIX : REDIRECT_PREFIX;
            return addFasterizeCacheControlParameter(prefix + mapping.getDestinationURL());
        }

        addGlobalModelAttributes(model, request);
        // Check for CMS Page where label or id is like /page
        final ContentPageModel pageForRequest = getContentPageForRequest(request);
        if (pageForRequest != null) {
            storeCmsPageInModel(model, pageForRequest);
            setUpMetaDataForContentPage(model, pageForRequest);
            model.addAttribute(WebConstants.BREADCRUMBS_KEY, getContentPageBreadcrumbBuilder().getBreadcrumbs(pageForRequest, request));
            return getViewForPage(pageForRequest);
        }
        setWtErrors(HttpServletResponse.SC_NOT_FOUND + " " + request.getRequestURL());

        // No page found - display the notFound page with error from controller
        final ContentPageModel errorPage = getContentPageForLabelOrId(ERROR_CMS_PAGE);
        storeCmsPageInModel(model, errorPage);
        setUpMetaDataForContentPage(model, errorPage);
        model.addAttribute(WebConstants.MODEL_KEY_ADDITIONAL_BREADCRUMB, getSimpleBreadcrumbBuilder().getBreadcrumbs(errorPage.getTitle(),errorPage.getTitle(Locale.ENGLISH)));
        GlobalMessages.addErrorMessage(model, "system.error.page.not.found");
        // set response code to 404
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return ControllerConstants.Views.Pages.Error.ErrorNotFoundPage;
    }

    protected DistContentPageMappingModel getContentPageMappingForRequest(final HttpServletRequest request) {
        final String lookupPathForRequest = getLookupPathForRequest(request);
        return getCmsPageService().getContentPageMappingForShortURL(lookupPathForRequest);
    }

    /**
     * Lookup the CMS Content Page for this request.
     * 
     * @param request
     *            The request
     * @return the CMS content page
     * @throws CMSItemNotFoundException
     */
    protected ContentPageModel getContentPageForRequest(final HttpServletRequest request) throws CMSItemNotFoundException {

        final String lookupPathForRequest = getLookupPathForRequest(request);

        // check if this mapping should have a page
        final List<String> mappings = Arrays
                .asList(StringUtils.split(getConfigurationService().getConfiguration().getString("distrelec.mappings.withoutPage"), ","));
        if (mappings.contains(lookupPathForRequest)) {
            return null;
        }
        // Lookup the CMS Content Page by label. Note that the label value must begin with a '/'.
        return getCmsPageService().getPageForLabel(lookupPathForRequest);
    }

    protected String getLookupPathForRequest(final HttpServletRequest request) {
        // Get the path for this request.
        // Note that the path begins with a '/'
        return getUrlPathHelper().getLookupPathForRequest(request);
    }

    @ExceptionHandler(CMSItemNotFoundException.class)
    public String handleCMSItemNotFoundException(final Exception exception, final HttpServletRequest request) {
        final Object uuid = java.util.UUID.randomUUID();
        final String uuidString = uuid.toString();
        ERROR_PAGE_LOG.error("A technical error occured [uuid: {}]. {}" ,uuidString,exception.getMessage());
        request.setAttribute("uuid", uuidString);
        request.setAttribute("exception", exception);

        return FORWARD_PREFIX + "/" + "notFound";
    }

    public UrlPathHelper getUrlPathHelper() {
        if (urlPathHelper == null) {
            urlPathHelper = new UrlPathHelper();
        }
        return urlPathHelper;
    }

    public void setUrlPathHelper(final UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

    public SimpleBreadcrumbBuilder getSimpleBreadcrumbBuilder() {
        return simpleBreadcrumbBuilder;
    }

    public void setSimpleBreadcrumbBuilder(final SimpleBreadcrumbBuilder simpleBreadcrumbBuilder) {
        this.simpleBreadcrumbBuilder = simpleBreadcrumbBuilder;
    }

    public ContentPageBreadcrumbBuilder getContentPageBreadcrumbBuilder() {
        return contentPageBreadcrumbBuilder;
    }

    public void setContentPageBreadcrumbBuilder(final ContentPageBreadcrumbBuilder contentPageBreadcrumbBuilder) {
        this.contentPageBreadcrumbBuilder = contentPageBreadcrumbBuilder;
    }
}
