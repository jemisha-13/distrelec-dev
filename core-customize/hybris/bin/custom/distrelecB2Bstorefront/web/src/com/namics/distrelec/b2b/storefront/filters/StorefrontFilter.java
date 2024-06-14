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
package com.namics.distrelec.b2b.storefront.filters;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.facades.storesession.data.ShoppingSettingsCookieData;
import com.namics.distrelec.b2b.storefront.attributes.Attributes;
import com.namics.distrelec.b2b.storefront.controllers.util.ShopSettingsUtil;
import com.namics.distrelec.b2b.storefront.history.BrowseHistory;
import com.namics.distrelec.b2b.storefront.history.BrowseHistoryEntry;
import com.namics.distrelec.b2b.storefront.util.SearchRobotDetector;
import de.hybris.platform.cms2.misc.CMSFilter;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Filter that initializes the session for the distrelecB2Bstorefront
 */
public class StorefrontFilter extends GenericFilterBean {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(StorefrontFilter.class);

    private StoreSessionFacade storeSessionFacade;
    private BrowseHistory browseHistory;

    @Autowired
    private SearchRobotDetector searchRobotDetector;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private DistrelecCMSSiteService cmsSiteService;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        final HttpSession session = httpRequest.getSession();
        final String queryString = httpRequest.getQueryString();
        final String shopSettingsString = Attributes.SHOP_SETTINGS.getValueFromCookies(httpRequest);
        ShoppingSettingsCookieData cookieData = null;
        if (shopSettingsString != null) {
        	cookieData = ShopSettingsUtil.readShopSettingsCookie(shopSettingsString);
        }
        String channel = null;
        String currency = null;
        String language = null;
        String country = null;
        String itemsPerPage = null;
        Boolean useTechnicalView = null;
        Boolean useIconView = null;
        Boolean useListView = null;
        Boolean autoApplyFilter = null;
        Boolean useDetailView=null;
        if (isSessionNotInitialized(session, queryString)) {
            initDefaults(httpRequest);

            Boolean cookieMessageConfirmed = Boolean.FALSE;
            useTechnicalView = Boolean.FALSE;
            useIconView = Boolean.FALSE;
            useListView = Boolean.FALSE;
            autoApplyFilter = Boolean.FALSE;
            useDetailView=Boolean.TRUE;
            // get settings from cookie if available
            if (shopSettingsString != null) {
                if (cookieData != null) {
                    channel = cookieData.getChannel();
                    language = cookieData.getLanguage();
                    country = cookieData.getCountry();
                    itemsPerPage = cookieData.getItemsPerPage();
                    cookieMessageConfirmed = cookieData.getCookieMessageConfirmed();
                    useIconView = cookieData.getUseIconView() != null ? cookieData.getUseIconView() : Boolean.FALSE;
                    useListView = cookieData.getUseListView() != null ? cookieData.getUseListView() : Boolean.FALSE;
                    autoApplyFilter = cookieData.getAutoApplyFilter() != null ? cookieData.getAutoApplyFilter() : Boolean.FALSE;
                    useDetailView=cookieData.getUseDetailView()!=null ? cookieData.getUseDetailView() : Boolean.TRUE;
                } else {
                    LOG.error("Cannot parse shop settings cookie!");
                }
                final CurrencyData currencyData = getStoreSessionFacade().getDefaultCurrency();
                if (currencyData != null) {
                    currency = currencyData.getIsocode();
                }
            }

            Attributes.SHOP_SETTINGS.setValue(httpRequest, httpResponse,
                    ShopSettingsUtil.createCookieWithSessionValues(StringUtils.isNotBlank(channel)?channel:getStoreSessionFacade().getCurrentChannel().getType(),
                            getStoreSessionFacade().getCurrentLanguage().getIsocode(), getStoreSessionFacade().getCurrentCountry().getIsocode(),
                            cookieMessageConfirmed, useIconView, useListView,useDetailView, autoApplyFilter, itemsPerPage));

            markSessionInitialized(session);
        }

        // override setting from url params if available
        if (StringUtils.isNotEmpty(request.getParameter(WebConstants.URL_PARAM_KEY_CHANNEL))) {
            channel = request.getParameter(WebConstants.URL_PARAM_KEY_CHANNEL);
        }
        if (StringUtils.isNotEmpty(request.getParameter(WebConstants.URL_PARAM_KEY_LANGUAGE))) {
            language = request.getParameter(WebConstants.URL_PARAM_KEY_LANGUAGE);
        }
        if (StringUtils.isNotEmpty(request.getParameter(WebConstants.URL_PARAM_KEY_COUNTRY))) {
            country = request.getParameter(WebConstants.URL_PARAM_KEY_COUNTRY);
        }
        if (StringUtils.isNotEmpty(request.getParameter(WebConstants.PAGE_SIZE))) {
            itemsPerPage = request.getParameter(WebConstants.PAGE_SIZE);
        }

        if (isModifiedAndValidForSession(country, channel, currency, language)) {
            // set settings to session if not null
            if (StringUtils.isNotEmpty(country)) {
                getStoreSessionFacade().setCurrentCountry(country);
            }
            if (StringUtils.isNotEmpty(channel)) {
                getStoreSessionFacade().setCurrentChannel(channel);
            }
            if (StringUtils.isNotEmpty(currency)) {
                getStoreSessionFacade().setCurrentCurrency(currency);
                if (!getUserFacade().isAnonymousUser()) {
                    getUserFacade().syncSessionCurrency();
                }
            }
            if (StringUtils.isNotEmpty(language)) {
                getStoreSessionFacade().setCurrentLanguage(language);
                if (!getUserFacade().isAnonymousUser()) {
                    getUserFacade().syncSessionLanguage();
                }
            }
            if (getStoreSessionFacade().isUseTechnicalView() == null) {
                getStoreSessionFacade().setUseTechnicalView(useTechnicalView);
            }
            if (getStoreSessionFacade().isUseIconView() == null) {
                getStoreSessionFacade().setUseIconView(useIconView);
            }
            if (getStoreSessionFacade().isUseListView() == null) {
                getStoreSessionFacade().setUseListView(useListView);
            }
            if (getStoreSessionFacade().isAutoApplyFilter() == null) {
                getStoreSessionFacade().setAutoApplyFilter(autoApplyFilter);
            }

            if (getStoreSessionFacade().getItemsperpage() == null) {
                getStoreSessionFacade().setItemsperPage(WebConstants.ITEMS_PER_PAGE);
            }
            
            if (getStoreSessionFacade().isUseDetailView() == null) {
                getStoreSessionFacade().setUseDetailView(useDetailView);
            }

            Attributes.SHOP_SETTINGS.setValue(httpRequest, httpResponse,
                    ShopSettingsUtil.createCookieWithSessionValues(getStoreSessionFacade().getCurrentChannel().getType(),
                            getStoreSessionFacade().getCurrentLanguage().getIsocode(), getStoreSessionFacade().getCurrentCountry().getIsocode(), (cookieData!=null && null!= cookieData.getCookieMessageConfirmed())?cookieData.getCookieMessageConfirmed():Boolean.FALSE,
                            getStoreSessionFacade().isUseIconView(), getStoreSessionFacade().isUseListView(),getStoreSessionFacade().isUseDetailView(),
                            getStoreSessionFacade().isAutoApplyFilter(), itemsPerPage));
        }

        if (isGetMethod(httpRequest)) {
            getBrowseHistory().addBrowseHistoryEntry(new BrowseHistoryEntry(httpRequest.getRequestURI(), null));
        }

        getSessionService().setAttribute(DistConstants.Session.IS_STOREFRONT_REQUEST, Boolean.TRUE);
        chain.doFilter(request, response);
    }

    private boolean isModifiedAndValidForSession(final String country, final String channel, final String currency, final String language) {
        // at least one modified?
        if (StringUtils.isNotEmpty(country) || StringUtils.isNotEmpty(channel) || StringUtils.isNotEmpty(currency) || StringUtils.isNotEmpty(language)) {
            final String newCountry = StringUtils.isNotEmpty(country) ? country : getStoreSessionFacade().getCurrentCountry().getIsocode();
            final String newChannel = StringUtils.isNotEmpty(channel) ? channel : getStoreSessionFacade().getCurrentChannel().getType();
            final String newCurrency = StringUtils.isNotEmpty(currency) ? currency : getStoreSessionFacade().getCurrentCurrency().getIsocode();
            final String newLanguage = StringUtils.isNotEmpty(language) ? language : getStoreSessionFacade().getCurrentLanguage().getIsocode();

            final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
            final List<BaseStoreModel> stores = currentSite.getStores();

            if (CollectionUtils.isNotEmpty(stores)) {
                for (final BaseStoreModel store : stores) {
                    if (isValidChannel(newChannel, store.getChannel())) {
                        if (containsValidCountry(newCountry, store.getDeliveryCountries())) {
                            if (containsValidCurrency(newCurrency, currentSite.getRegistrationCurrencies())) {
                                if (containsValidLanguage(newLanguage, store.getLanguages())) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }

        }
        return false;
    }

    private boolean isValidChannel(final String channelCode, final SiteChannel siteChannel) {
        if (siteChannel != null) {
            if (StringUtils.equalsIgnoreCase(channelCode, siteChannel.getCode())) {
                return getStoreSessionFacade().isChannelSwitchAllowed(siteChannel);
            }
        }
        return false;
    }

    private boolean containsValidCountry(final String countryCode, final Collection<CountryModel> countries) {
        if (CollectionUtils.isNotEmpty(countries)) {
            for (final CountryModel country : countries) {
                if (StringUtils.equalsIgnoreCase(countryCode, country.getIsocode())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsValidCurrency(final String currencyCode, final Collection<CurrencyModel> currencies) {
        if (CollectionUtils.isNotEmpty(currencies)) {
            for (final CurrencyModel currency : currencies) {
                if (StringUtils.equalsIgnoreCase(currencyCode, currency.getIsocode())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsValidLanguage(final String languageCode, final Collection<LanguageModel> languages) {
        if (CollectionUtils.isNotEmpty(languages)) {
            for (final LanguageModel language : languages) {
                if (StringUtils.equalsIgnoreCase(languageCode, language.getIsocode())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isGetMethod(final HttpServletRequest httpRequest) {
        return "GET".equalsIgnoreCase(httpRequest.getMethod());
    }

    protected boolean isRequestSecure(final HttpServletRequest httpRequest) {
        return httpRequest.isSecure();
    }

    protected boolean isSessionNotInitialized(final HttpSession session, final String queryString) {
        return session.isNew() || StringUtils.contains(queryString, CMSFilter.CLEAR_CMSSITE_PARAM) || !isSessionInitialized(session);
    }

    protected void initDefaults(final HttpServletRequest request) {
        List<Locale> locales;
        if (getSearchRobotDetector().isSearchRobot(request)) {
            // use default site language for crawlers
            locales = null;
        } else {
            locales = Collections.list(request.getLocales());
        }
        getStoreSessionFacade().initializeSession(locales);
    }

    protected boolean isSessionInitialized(final HttpSession session) {
        return session.getAttribute(this.getClass().getName()) != null;
    }

    protected void markSessionInitialized(final HttpSession session) {
        session.setAttribute(this.getClass().getName(), "initialized");
    }

    protected DistrelecStoreSessionFacade getStoreSessionFacade() {
        return (DistrelecStoreSessionFacade) storeSessionFacade;
    }

    @Required
    public void setStoreSessionFacade(final StoreSessionFacade storeSessionFacade) {
        this.storeSessionFacade = storeSessionFacade;
    }

    protected BrowseHistory getBrowseHistory() {
        return browseHistory;
    }

    @Required
    public void setBrowseHistory(final BrowseHistory browseHistory) {
        this.browseHistory = browseHistory;
    }

    public SearchRobotDetector getSearchRobotDetector() {
        return searchRobotDetector;
    }

    public void setSearchRobotDetector(SearchRobotDetector searchRobotDetector) {
        this.searchRobotDetector = searchRobotDetector;
    }

    protected SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
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
}
