/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.storesession;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.namics.distrelec.b2b.facades.storesession.data.ChannelData;
import com.namics.distrelec.b2b.facades.storesession.data.SalesOrgData;
import com.namics.distrelec.occ.core.basesite.data.DistrelecSiteSettingsDataList;

import de.hybris.platform.commercefacades.basestore.data.BaseStoreData;
import de.hybris.platform.commerceservices.enums.SiteChannel;

/**
 * DistrelecStoreSessionFacade extends NamicsStoreSessionFacade.
 *
 * @author rmeier, Namics AG
 * @since Distrelec Extensions 1.0
 */
public interface DistrelecStoreSessionFacade extends NamicsStoreSessionFacade {

    /**
     * Returns the current language as {@link java.util.Locale}
     *
     * @return the current {@code Locale}.
     */
    Locale getCurrentLocale();

    /**
     * Gets current sales organisation stored in session.
     *
     * @return current sales organisation for the current store.
     */
    SalesOrgData getCurrentSalesOrg();

    /**
     * Gets current channel stored in session.
     *
     * @return current channel
     */
    ChannelData getCurrentChannel();

    /**
     * Gets a map of all settings of all sites.
     *
     * @return all site settings
     */
    Collection<Map<String, Map<String, String>>> getAllSiteSettings();

    /**
     * @return all usable channels for the current store. .
     */
    Collection<ChannelData> getAllChannels();

    /**
     * @param channel
     */
    void setCurrentChannel(String channel);

    /**
     * Check whether switch to given channel is allowed for current customer.
     *
     * @param channel
     */
    boolean isChannelSwitchAllowed(SiteChannel channel);

    /**
     * Initialise the session
     */
    @Override
    void initializeSession(final HttpServletRequest request);

    /**
     * Gets the value if the technical view is activated.
     *
     * @return Boolean.TRUE if the technical view is activated
     */
    Boolean isUseTechnicalView();

    /**
     * set the value for the technical view
     *
     * @param useTechnicalView
     *            Boolean.TRUE, if the technical view is activated
     */
    void setUseTechnicalView(Boolean useTechnicalView);

    /**
     * Gets the value if the icon view is activated.
     *
     * @return Boolean.TRUE if the icon view is activated
     */
    Boolean isUseIconView();

    /**
     * set the value for the icon view
     *
     * @param useIconView
     *            Boolean.TRUE, if the icon view is activated
     */
    void setUseIconView(Boolean useIconView);

    /**
     * Gets the value if the icon list is activated.
     *
     * @return Boolean.TRUE if the list view is activated
     */
    Boolean isUseListView();

    /**
     * set the value for the list view
     *
     * @param useListView
     *            Boolean.TRUE, if the list view is activated
     */
    void setUseListView(Boolean useListView);

    /**
     * @return {@code true} is the auto apply filter is activated.
     */
    Boolean isAutoApplyFilter();

    /**
     * set the value for the auto apply filter.
     *
     * @param autoApplyFilter
     */
    void setAutoApplyFilter(final Boolean autoApplyFilter);

    /**
     * set itemsPerPage for current session
     */
    void setItemsperPage(final String itemsPerPage);

    /**
     * returns itemsPerPage for current session
     */
    String getItemsperpage();

    Map<String, String> getPageViewPerCategoryMap();

    /**
     * set the value for the Detail view
     *
     * @param useDetailView
     *            Boolean.TRUE, if the Detail view is activated
     */
    void setUseDetailView(Boolean useDetailView);

    /**
     * Gets the value if the Detail view is activated.
     *
     * @return Boolean.TRUE if the Detail view is activated
     */
    Boolean isUseDetailView();

    /**
     * Get list of extra countries that should be available on site etiings dropdown
     * Get list of extra countries that should be available on site etiings dropdown
     *
     * @return
     */
    Set<String> getSiteSettingsExtraCountries();

    String getCurrentLanguageIsoCode();

    /**
     * Get current base store data
     */
    BaseStoreData getCurrentBaseStore();

    /**
     * Gets a map of all settings of all sites. return all site settings
     *
     * @return DistrelecSiteSettingsDataList
     */
    DistrelecSiteSettingsDataList getAllSiteSettingsForOCC();

    boolean isCurrentShopExport();

    boolean isCurrentShopElfa();

    boolean isCurrentShopItaly();

    boolean areQuotationsEnabledForCurrentBaseStore();

}
