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
package com.namics.distrelec.b2b.storefront.controllers.misc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.storefront.attributes.Attributes;
import com.namics.distrelec.b2b.storefront.controllers.AbstractController;
import com.namics.distrelec.b2b.storefront.controllers.util.ShopSettingsUtil;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * Controller for store session.
 */
@Controller
@RequestMapping("/_s")
public class StoreSessionController extends AbstractController {

    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    @Autowired
    private DistrelecCMSSiteService cmsSiteService;

    private static final Logger LOG = Logger.getLogger(StoreSessionController.class);

    @RequestMapping(value = "/language", method = { RequestMethod.GET, RequestMethod.POST })
    public String selectLanguage(@RequestParam("code")
    final String isoCode, final HttpServletRequest request, final HttpServletResponse response) {
        storeSessionFacade.setCurrentLanguage(isoCode);
        userFacade.syncSessionLanguage();
        writeCookie(request, response);
        return getReturnRedirectUrl(request);
    }

    @RequestMapping(value = "/channel", method = { RequestMethod.GET, RequestMethod.POST })
    public String selectChannel(@RequestParam(WebConstants.URL_PARAM_KEY_CHANNEL)
    final String channel, final HttpServletRequest request, final HttpServletResponse response) {
        storeSessionFacade.setCurrentChannel(channel);
        writeCookie(request, response);
        return getReturnRedirectUrl(request);
    }

    @RequestMapping(value = "/cookieMessageConfirmed", method = { RequestMethod.GET, RequestMethod.POST })
    public String confirmCookieMessage(final HttpServletRequest request, final HttpServletResponse response) {
        writeCookie(request, response);
        return getReturnRedirectUrl(request);
    }

    @RequestMapping(value = "/shopsettings", method = { RequestMethod.GET, RequestMethod.POST })
    public String saveShopSettings(@RequestParam(value = WebConstants.URL_PARAM_KEY_CHANNEL, required = false) final String channel,
                                   @RequestParam(value = WebConstants.URL_PARAM_KEY_COUNTRY) final String country,
                                   @RequestParam(value = WebConstants.URL_PARAM_KEY_COUNTRY_REDIRECT, required = false, defaultValue = "false") final boolean countryRedirect,
                                   @RequestParam(value = WebConstants.URL_PARAM_KEY_LANGUAGE, required = false) final String language,
                                   final HttpServletRequest request, final HttpServletResponse response) {
        if (countryRedirect) {
            // After a country switch, redirect back to root (without any query parameters)
            storeSessionFacade.setCurrentCountry(country);
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/");
        }

        final String currentCountryCode = storeSessionFacade.getCurrentCountry().getIsocode();

        if (!currentCountryCode.equalsIgnoreCase(country) || "distrelec_EX".equals(cmsSiteService.getCurrentSite().getUid())) {
            CMSSiteModel cmsSite = null;
            try {
                cmsSite = cmsSiteService.getSiteForCountryAndBrand(country, storeSessionFacade.getCurrentSalesOrg().getBrand());
            } catch (final CMSItemNotFoundException e) {
                RequestContextUtils.getOutputFlashMap(request).put("message", e.getMessage());
                return addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/404");
            }

            if (StringUtils.hasText(cmsSite.getRedirectURL())) {
                // External (iShop or ELFA based) shop
                return addFasterizeCacheControlParameter(REDIRECT_PREFIX + cmsSite.getRedirectURL());
            } else {
                // Internal (hybris based) shop
                final StringBuilder params = new StringBuilder();
                params.append("?").append(WebConstants.URL_PARAM_KEY_CHANNEL).append("=").append(channel);
                params.append("&").append(WebConstants.URL_PARAM_KEY_LANGUAGE).append("=").append(language);
                params.append("&").append(WebConstants.URL_PARAM_KEY_COUNTRY).append("=").append(country);
                params.append("&").append(WebConstants.URL_PARAM_KEY_COUNTRY_REDIRECT).append("=").append(true);

                // Redirect to new site using the same URI (/_s/shopsettings)
                String siteRedirectUrl;
                String siteUrl = distSiteBaseUrlResolutionService.getWebsiteUrlForSite(cmsSite, true, null);
                String headlessUrl = distSiteBaseUrlResolutionService.getHeadlessWebsiteUrlForSite(cmsSite, true, null);
                if (siteUrl.equals(headlessUrl)) {
                    // redirect to headless because headless is migrated
                    siteRedirectUrl = headlessUrl;
                } else {
                    String storefrontUrl = distSiteBaseUrlResolutionService.getStorefrontWebsiteUrlForSite(cmsSite, true, null);
                    siteRedirectUrl = storefrontUrl;
                }
                return addFasterizeCacheControlParameter(REDIRECT_PREFIX + siteRedirectUrl);
            }
        }

        if (language != null) {
            response.setLocale(new Locale(language));
            storeSessionFacade.setCurrentLanguage(language);
        }
        return getReturnRedirectUrl(request);
    }

    /**
     * This method aims to change the shop settings asynchronously. This method is only for Export shop
     *
     * @param channel the new channel
     * @param country the new country
     * @param language the new language
     * @param request
     * @param response
     * @return the <tt>OK</tt> if the operation was successfully, <tt>NOK</tt> otherwise.
     */
    @RequestMapping(value = "/shopsettings-async", method = { RequestMethod.GET, RequestMethod.POST }, produces = { "text/plain" })
    public @ResponseBody String saveShopSettingsAsync(@RequestParam(value = WebConstants.URL_PARAM_KEY_CHANNEL, required = false) final String channel,
                                                      @RequestParam(value = WebConstants.URL_PARAM_KEY_COUNTRY) final String country,
                                                      @RequestParam(value = WebConstants.URL_PARAM_KEY_LANGUAGE, required = false) final String language,
                                                      final HttpServletRequest request, final HttpServletResponse response) {

        if (!storeSessionFacade.getCurrentCountry().getIsocode().equalsIgnoreCase(country)) {
            storeSessionFacade.setCurrentCountry(country);
        }

        if (channel != null && !storeSessionFacade.getCurrentChannel().getType().equalsIgnoreCase(channel)) {
            storeSessionFacade.setCurrentChannel(channel);
        }

        if (language != null) {
            response.setLocale(new Locale(language));
            storeSessionFacade.setCurrentLanguage(language);
        }

        return "OK";
    }
    
    /**
     * This method aims to change the shop settings asynchronously. This method is only for Export shop
     *
     * @param channel the new channel
     * @param country the new country
     * @param language the new language
     * @param request
     * @param response
     * @return the <tt>OK</tt> if the operation was successfully, <tt>NOK</tt> otherwise.
     */
    @RequestMapping(value = "/shopsettings-toggle", method = { RequestMethod.GET, RequestMethod.POST }, produces = { "text/plain" })
    public @ResponseBody String saveShopSettingsForToggle(@RequestParam(value = WebConstants.URL_PARAM_KEY_CHANNEL, required = false) final String channel,
                                                      @RequestParam(value = WebConstants.URL_PARAM_KEY_COUNTRY) final String country,
                                                      @RequestParam(value = WebConstants.URL_PARAM_KEY_LANGUAGE, required = false) final String language,
                                                      final HttpServletRequest request, final HttpServletResponse response) {

        if (!storeSessionFacade.getCurrentCountry().getIsocode().equalsIgnoreCase(country)) {
            storeSessionFacade.setCurrentCountry(country);
        }

        if (channel != null && !storeSessionFacade.getCurrentChannel().getType().equalsIgnoreCase(channel)) {
            storeSessionFacade.setCurrentChannel(channel);
        }

        if (language != null) {
            response.setLocale(new Locale(language));
            storeSessionFacade.setCurrentLanguage(language);
        }
        writeCookie(request, response);
        return getReturnRedirectUrlToggle(request);
    }

    @GetMapping(value = "/allsitesettings")
    public @ResponseBody Collection<Map<String, Map<String, String>>> getAllSiteSettings() {
        return storeSessionFacade.getAllSiteSettings();
    }

    protected void writeCookie(final HttpServletRequest request, final HttpServletResponse response) {
        final String channel = storeSessionFacade.getCurrentChannel().getUid();
        final String language = storeSessionFacade.getCurrentLanguage().getIsocode();
        final String country = storeSessionFacade.getCurrentCountry().getIsocode();
        final String itemsPerPage = storeSessionFacade.getItemsperpage();
        final Boolean useIconView = storeSessionFacade.isUseIconView();
        final Boolean useListView = storeSessionFacade.isUseListView();
        final Boolean useDetailView=storeSessionFacade.isUseDetailView();
        final Boolean autoApplyFilter = storeSessionFacade.isAutoApplyFilter();
        Attributes.SHOP_SETTINGS.setValue(request, response, ShopSettingsUtil.createCookieWithSessionValues(channel, language, country, Boolean.TRUE,
                useIconView, useListView,useDetailView, autoApplyFilter, itemsPerPage));
    }

    protected String getReturnRedirectUrl(final HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (StringUtils.hasText(referer)) {
            try {
                final URL url = new URL(referer);
                final String newlanguageCode = storeSessionFacade.getCurrentLanguage().getIsocode();
                if (!url.getPath().startsWith("/" + newlanguageCode + "/")) {
                    referer = referer.replaceFirst("/([a-z]{2})/", "/" + newlanguageCode + "/");
                }

                return addFasterizeCacheControlParameter(REDIRECT_PREFIX + referer);
            } catch (final MalformedURLException e) {
                LOG.error("Could not parse URL " + referer, e);
            }
        }
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + '/');
    }
    
    protected String getReturnRedirectUrlToggle(final HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (StringUtils.hasText(referer)) {
            try {
                final URL url = new URL(referer);
                final String newlanguageCode = storeSessionFacade.getCurrentLanguage().getIsocode();
                if (!url.getPath().startsWith("/" + newlanguageCode + "/")) {
                    referer = referer.replaceFirst("/([a-z]{2})/", "/" + newlanguageCode + "/");
                }

                return addFasterizeCacheControlParameter(referer);
            } catch (final MalformedURLException e) {
                LOG.error("Could not parse URL " + referer, e);
            }
        }
        return addFasterizeCacheControlParameter("/");
    }

    @ExceptionHandler(UnknownIdentifierException.class)
    public String handleUnknownIdentifierException(final UnknownIdentifierException exception, final HttpServletRequest request) {
        RequestContextUtils.getOutputFlashMap(request).put("message", exception.getMessage());
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/404");
    }

    public DistrelecStoreSessionFacade getStoreSessionFacade() {
        return storeSessionFacade;
    }

    public void setStoreSessionFacade(final DistrelecStoreSessionFacade storeSessionFacade) {
        this.storeSessionFacade = storeSessionFacade;
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    public void setUserFacade(final UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    public DistrelecCMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final DistrelecCMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public DistSiteBaseUrlResolutionService getDistSiteBaseUrlResolutionService() {
        return distSiteBaseUrlResolutionService;
    }

    public void setDistSiteBaseUrlResolutionService(DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService) {
        this.distSiteBaseUrlResolutionService = distSiteBaseUrlResolutionService;
    }
}
