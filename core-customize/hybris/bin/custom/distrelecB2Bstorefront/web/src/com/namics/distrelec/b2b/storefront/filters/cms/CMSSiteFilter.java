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
package com.namics.distrelec.b2b.storefront.filters.cms;

import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;
import com.namics.distrelec.b2b.storefront.filters.util.ContextInformationLoader;
import com.namics.distrelec.b2b.storefront.preview.DefaultCMSPageURLMappingHandler;
import com.namics.distrelec.b2b.storefront.preview.URLMappingHandler;
import com.namics.distrelec.b2b.storefront.servlets.util.FilterSpringUtil;
import de.hybris.platform.acceleratorcms.data.CmsPageRequestContextData;
import de.hybris.platform.acceleratorcms.services.CMSPageContextService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.cms2.misc.CMSFilter;
import de.hybris.platform.cms2.misc.UrlUtils;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.preview.CMSPreviewTicketModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.storesession.StoreSessionService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.jalo.JaloObjectNoLongerValidException;
import de.hybris.platform.jalo.c2l.LocalizableItem;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * Responsible for setting up application - to main responsibility belongs:
 * <p>
 * <ul>
 * <li>Matches current site by current URL</li>
 * <li>Setting current site in session (through {@link CMSSiteService} )</li>
 * <li>Setting current catalog version (through {@link CMSSiteService} )</li>
 * <li>Setting session catalog versions (through {@link CatalogService} )</li>
 * </ul>
 * </p>
 * <br/>
 * <b>Note</b>: In former versions (i.e. 4.1.1 and earlier) as a preview mechanism we used
 * {@link de.hybris.platform.cms2.misc.AbstractPreviewServlet} which actually is obsolete. All necessary logic was adapted and moved here.
 */
public class CMSSiteFilter extends OncePerRequestFilter implements CMSFilter {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(CMSSiteFilter.class);

    protected static final int MISSING_CMS_SITE_ERROR_STATUS = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    protected static final String MISSING_CMS_SITE_ERROR_MESSAGE = "Cannot find CMSSite associated with current URL";
    protected static final String INCORRECT_CMS_SITE_CHANNEL_ERROR_MESSAGE = "Matched CMSSite for current URL has unsupported channel";

    private CMSSiteService cmsSiteService;
    private UserService userService;
    private SessionService sessionService;
    private CMSPreviewService cmsPreviewService;
    private CommerceCommonI18NService commerceCommonI18NService;
    private BaseSiteService baseSiteService;
    private URLMappingHandler urlMappingHandler = new DefaultCMSPageURLMappingHandler();
    private ContextInformationLoader contextInformationLoader;

    @Autowired
    private CMSPageContextService cmsPageContextService;

    @Autowired
    private StoreSessionService storeSessionService;

    @Autowired
    private NamicsCommonI18NService namicsCommonI18NService;

    @Override
    protected void doFilterInternal(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse, final FilterChain filterChain)
            throws ServletException, IOException {
        final String requestURL = httpRequest.getRequestURL().toString();

        final CmsPageRequestContextData cmsPageRequestContextData = cmsPageContextService.initialiseCmsPageContextForRequest(
                httpRequest);

        // check whether exits valid preview data
        if (cmsPageRequestContextData.getPreviewData() == null) {
            // process normal request (i.e. normal browser non-cmscockpit request)
            if (processNormalRequest(httpRequest, httpResponse, contextInformationLoader)) {
                // proceed filters
                filterChain.doFilter(httpRequest, httpResponse);
            }
        } else if (StringUtils.contains(requestURL, PREVIEW_TOKEN)) {
            final String redirectURL = processPreviewRequest(httpRequest, contextInformationLoader);

            // redirect to computed URL
            redirectPreviewRequest(httpRequest, httpResponse, redirectURL);

            // next filter in chain won't be invoked!!!
        } else {
            if(shouldRedirectToSpecificURL(httpRequest, cmsPageRequestContextData)){
                String redirectURL = processPreviewRequest(httpRequest, contextInformationLoader);

                redirectPreviewRequest(httpRequest, httpResponse, redirectURL);
            }else {
                processPreviewData(httpRequest, cmsPageRequestContextData.getPreviewData());
                // proceed filters
                filterChain.doFilter(httpRequest, httpResponse);
            }
        }
    }

    private boolean shouldRedirectToSpecificURL(HttpServletRequest httpRequest, CmsPageRequestContextData cmsPageRequestContextData) {
        return httpRequest.getRequestURI().equals("/") && !(cmsPageRequestContextData.getPage() == null) && !(cmsPageRequestContextData.getPage() instanceof ContentPageModel);
    }

    private void redirectPreviewRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String redirectURL) throws IOException {
        if (redirectURL.charAt(0) == '/') {
            final String contextPath = httpRequest.getContextPath();
            final String encodedRedirectUrl = httpResponse.encodeRedirectURL(contextPath + redirectURL);
            httpResponse.sendRedirect(encodedRedirectUrl);
        } else {
            final String encodedRedirectUrl = httpResponse.encodeRedirectURL(redirectURL);
            httpResponse.sendRedirect(encodedRedirectUrl);
        }
    }

    protected void processPreviewData(final HttpServletRequest httpRequest, final PreviewDataModel previewDataModel) {
        previewDataModel.setLanguage(filterPreviewLanguageForSite(httpRequest, previewDataModel));

        namicsCommonI18NService.setCurrentCountry(previewDataModel.getActiveSite().getCountry());

        storeSessionService.setCurrentLanguage(previewDataModel.getLanguage().getIsocode());

        setFallbackLanguage(httpRequest, Boolean.TRUE);

        //load necessary information
        contextInformationLoader.initializePreviewRequest(previewDataModel);
        //load fake context information
        contextInformationLoader.loadFakeContextInformation(httpRequest, previewDataModel);
    }

    /**
     * Processing normal request (i.e. when user goes directly to that application - not from cmscockpit)
     * <p/>
     * <b>Note:</b> <br/>
     * We preparing application by setting correct:
     * <ul>
     * <li>Current Site</li>
     * <li>Current Catalog Versions</li>
     * <li>Enabled language fallback</li>
     * </ul>
     *
     * @see ContextInformationLoader#initializeSiteFromRequest(String)
     * @see ContextInformationLoader#setCatalogVersions()
     * @param httpRequest
     *            current request
     * @param httpResponse
     * @param informationLoader
     *            default context information loader
     * @throws java.io.IOException
     */
    protected boolean processNormalRequest(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse,
            final ContextInformationLoader informationLoader) throws IOException {

        final String queryString = httpRequest.getQueryString();
        final String currentRequestURL = httpRequest.getRequestURL().toString();

        // set current site
        CMSSiteModel cmsSiteModel = getCurrentCmsSite();
        if (cmsSiteModel == null || StringUtils.contains(queryString, CLEAR_CMSSITE_PARAM)) {
            final String absoluteURL = StringUtils.removeEnd(currentRequestURL, "/") + (StringUtils.isBlank(queryString) ? "" : "?" + queryString);

            cmsSiteModel = contextInformationLoader.initializeSiteFromRequest(absoluteURL);

            if (cmsSiteModel == null) {
                // resolve site from referer
                String refererURL = httpRequest.getHeader(HttpHeaders.REFERER);

                cmsSiteModel = contextInformationLoader.initializeSiteFromRequest(refererURL);
            }
        }

        if (cmsSiteModel == null) {
            // Failed to lookup CMS site
            httpResponse.sendError(MISSING_CMS_SITE_ERROR_STATUS, MISSING_CMS_SITE_ERROR_MESSAGE);
            return false;
        } else if (!SiteChannel.B2B.equals(cmsSiteModel.getChannel()) && !SiteChannel.B2C.equals(cmsSiteModel.getChannel())) // Restrict to
                                                                                                                             // B2B or B2C
                                                                                                                             // channel
        {
            // CMS site that we looked up was for an unsupported channel
            httpResponse.sendError(MISSING_CMS_SITE_ERROR_STATUS, INCORRECT_CMS_SITE_CHANNEL_ERROR_MESSAGE);
            return false;
        }

        informationLoader.setCatalogVersions();
        // set fall back language enabled
        setFallbackLanguage(httpRequest, Boolean.TRUE);

        return true;
    }

    /**
     * Processing preview request (i.e. request with additional parameters like {@link CMSFilter#PREVIEW_TOKEN} requested from cmscockpit) )
     * <p/>
     * <b>Note:</b> Processing preview data in order to generate target URL, and load necessary information in user session
     * <ul>
     * <li>Initialize information (Active CMSSite, Catalog versions,Current catalog version ) information getting from valid Preview Data</li>
     * <li>Load all fake information (like: User, User group, Language, Time ...)
     * <li>Generating target URL according to Preview Data
     * </ul>
     *
     * @param httpRequest
     *            current request
     * @param informationLoader
     *            default context information loader
     *
     * @return target URL
     */
    protected String processPreviewRequest(final HttpServletRequest httpRequest, final ContextInformationLoader informationLoader) {
        final PreviewDataModel previewDataModel = getPreviewData(getPreviewTicketId(httpRequest), httpRequest);

        processPreviewData(httpRequest, previewDataModel);

        // generate destination URL
        final String destinationURL = generatePreviewUrl(httpRequest, previewDataModel);

        // persist changes
        previewDataModel.setResourcePath(destinationURL);
        informationLoader.storePreviewData(previewDataModel);

        final CMSPreviewTicketModel ticket = getCmsPreviewService().createPreviewTicket(previewDataModel);
        String parameterDelimiter = "?";
        if (StringUtils.contains(destinationURL, "?")) {
            parameterDelimiter = "&";
        }
        return destinationURL + parameterDelimiter + PREVIEW_TICKET_ID_PARAM + "=" + ticket.getId();
    }

    /**
     * Filters the preview language to a language supported by the site. If the requested preview language is not supported, returns the
     * default site language instead.
     *
     * @param httpRequest
     * @param previewDataModel
     * @return LanguageModel the filtered language for previewing
     */
    protected LanguageModel filterPreviewLanguageForSite(final HttpServletRequest httpRequest, final PreviewDataModel previewDataModel) {
        final BaseSiteModel previewSite = previewDataModel.getActiveSite() == null ? getCurrentCmsSite() : previewDataModel
                .getActiveSite();
        getBaseSiteService().setCurrentBaseSite(previewSite, false);
        final Collection<LanguageModel> siteLanguages = getCommerceCommonI18NService().getAllLanguages();
        if (siteLanguages.contains(previewDataModel.getLanguage()))
        {
            // The preview language is supported
            return previewDataModel.getLanguage();
        }
        return getCommerceCommonI18NService().getDefaultLanguage();
    }

    /**
     * Enables or disables language fall back
     * <p/>
     *
     * @param httpRequest
     *            current request
     * @param enabled
     *            enabled or disabled
     */
    protected void setFallbackLanguage(final HttpServletRequest httpRequest, final Boolean enabled) {
        final SessionService sessionService = getSessionService();
        if (sessionService != null) {
            sessionService.setAttribute(LocalizableItem.LANGUAGE_FALLBACK_ENABLED, enabled);
            sessionService.setAttribute(AbstractItemModel.LANGUAGE_FALLBACK_ENABLED_SERVICE_LAYER, enabled);
        }
    }

    /**
     * Generates target URL accordingly to valid Preview Data passed as a parameter
     * <p/>
     *
     * @param httpRequest
     *            current request
     * @param previewDataModel
     *            valid data model contains all necessary information
     * @return target URL
     */
    protected String generatePreviewUrl(final HttpServletRequest httpRequest, final PreviewDataModel previewDataModel) {
        String generatedPreviewUrl = StringUtils.EMPTY;
        if (previewDataModel != null) {

            if (StringUtils.isBlank(generatedPreviewUrl)) {
                final AbstractPageModel abstractPageModel = previewDataModel.getPage();
                if (abstractPageModel == null) {
                    generatedPreviewUrl = previewDataModel.getResourcePath();
                } else {
                    generatedPreviewUrl = getURLMappingHandler(httpRequest).getPageUrl(httpRequest, previewDataModel);
                }

            }
        }
        if (StringUtils.isBlank(generatedPreviewUrl)) {
            generatedPreviewUrl = UrlUtils.extractHostInformationFromRequest(httpRequest, getCMSSiteService().getCurrentSite());
        }

        return generatedPreviewUrl;
    }

    /**
     * Retrieves current mapping handler in order to generate proper target URL for CMS Page
     * <p/>
     *
     * @param httpRequest
     *            current request
     * @return current mapping handler
     */
    protected URLMappingHandler getURLMappingHandler(final HttpServletRequest httpRequest) {
        URLMappingHandler urlMappingHandler = FilterSpringUtil.getSpringBean(httpRequest, "cmsPageUrlHandlerMapping", URLMappingHandler.class);
        if (urlMappingHandler == null) {
            urlMappingHandler = new DefaultCMSPageURLMappingHandler();
        }
        return urlMappingHandler;
    }

    /**
     * Retrieves current mapping handler in order to generate proper target URL for CMS Page
     * <p/>
     *
     * @return current mapping handler
     */
    protected URLMappingHandler getURLMappingHandler() {
        return urlMappingHandler;
    }

    @Required
    public void setUrlMappingHandler(final URLMappingHandler urlMappingHandler) {
        this.urlMappingHandler = urlMappingHandler;
    }

    @Required
    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    @Required
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    @Required
    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Required
    public void setCmsPreviewService(final CMSPreviewService cmsPreviewService) {
        this.cmsPreviewService = cmsPreviewService;
    }

    @Required
    public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService) {
        this.commerceCommonI18NService = commerceCommonI18NService;
    }

    @Required
    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    @Required
    public void setContextInformationLoader(final ContextInformationLoader contextInformationLoader) {
        this.contextInformationLoader = contextInformationLoader;
    }

    protected CMSSiteService getCMSSiteService() {
        return cmsSiteService;
    }

    protected UserService getUserService() {
        return userService;
    }

    protected SessionService getSessionService() {
        return sessionService;
    }

    protected CMSPreviewService getCmsPreviewService() {
        return cmsPreviewService;
    }

    protected CommerceCommonI18NService getCommerceCommonI18NService() {
        return commerceCommonI18NService;
    }

    protected CMSSiteModel getCurrentCmsSite() {
        try {
            return getCMSSiteService().getCurrentSite();
        } catch (final JaloObjectNoLongerValidException ignore) {
            return null;
        }
    }

    protected BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    /**
     * Retrieves {@link CMSFilter#PREVIEW_TICKET_ID_PARAM} from current request
     *
     * @param httpRequest
     *            current request
     * @return current ticket id
     */
    protected String getPreviewTicketId(final HttpServletRequest httpRequest) {
        String id = httpRequest.getParameter(PREVIEW_TICKET_ID_PARAM);
        if (StringUtils.isBlank(id)) {
            id = getSessionService().getAttribute(PREVIEW_TICKET_ID_PARAM);
        }
        return id;
    }

    /**
     * Retrieves current Preview Data according to given ticked id
     *
     * @param ticketId
     *            current ticket id
     * @param httpRequest
     *            current request
     * @return current Preview Data attached to given ticket if any otherwise null
     */
    protected PreviewDataModel getPreviewData(final String ticketId, final HttpServletRequest httpRequest) {
        PreviewDataModel ret = null;
        final CMSPreviewTicketModel previewTicket = getCmsPreviewService().getPreviewTicket(ticketId);
        if (previewTicket != null) {
            ret = previewTicket.getPreviewData();
        }
        return ret;
    }
}
