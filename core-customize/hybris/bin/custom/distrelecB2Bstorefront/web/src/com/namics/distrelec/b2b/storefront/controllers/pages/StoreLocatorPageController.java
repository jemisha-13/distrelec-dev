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

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.StorefinderBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.ThirdPartyConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.storefinder.StoreFinderFacade;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.commerceservices.storefinder.helpers.DistanceHelper;
import de.hybris.platform.storelocator.exception.GeoLocatorException;
import de.hybris.platform.storelocator.exception.LocationServiceException;
import de.hybris.platform.storelocator.exception.MapServiceException;
import de.hybris.platform.storelocator.impl.GeometryUtils;
import de.hybris.platform.storelocator.location.Location;
import de.hybris.platform.storelocator.location.LocationService;

/**
 * Controller for store locator search and detail pages. Provides display data for these two pages. Search result amount is limited to the
 * {@link StoreLocatorPageController#INITIAL_LOCATIONS_TO_DISPLAY} value. Increasing number of displayed stores is possible by giving proper
 * argument for {@link StoreLocatorPageController#displayPOS(String, String, Model, HttpServletRequest)} method but is limited to max value
 * of {@link StoreLocatorPageController#MAX_LOCATIONS_TO_DISPLAY} stores.
 */
@Controller
@RequestMapping(value = "/store-finder")
public class StoreLocatorPageController extends AbstractPageController {
    protected static final Logger LOG = Logger.getLogger(StoreLocatorPageController.class);

    /**
     * We use this suffix pattern because of an issue with Spring 3.1 where a Uri value is incorrectly extracted if it contains on or more
     * '.' characters. Please see https://jira.springsource.org/browse/SPR-6164 for a discussion on the issue and future resolution.
     */
    private static final String LOCATION_QUERY_PATH_VARIABLE_PATTERN = "{locationQuery:.*}";
    private static final String STORE_LOCATION_PATH_VARIABLE_PATTERN = "{storeLocation:.*}";

    private static final String STORE_FINDER_CMS_PAGE = "storefinderPage";

    private static final int INITIAL_LOCATIONS_TO_DISPLAY = 5;
    private static final int LOCATIONS_INCREMENT = 5;
    private static final int MAX_LOCATIONS_TO_DISPLAY = 100;
    private static final String INITIAL_LOCATIONS_TO_DISPLAY_TEXT = "" + INITIAL_LOCATIONS_TO_DISPLAY; // NOPMD

    @Autowired
    private LocationService locationService;

    @Autowired
    private DistanceHelper distanceHelper;

    @Autowired
    private StorefinderBreadcrumbBuilder storefinderBreadcrumbBuilder;

    @Autowired
    private StoreFinderFacade storeFinderFacade;

    @ModelAttribute("googleApiVersion")
    public String getGoogleApiVersion() {
        return getConfigurationService().getConfiguration().getString(ThirdPartyConstants.Google.API_VERSION);
    }

    @ModelAttribute("googleApiKey")
    public String getGoogleApiKey(final HttpServletRequest request) {
        final String googleApiKey = getConfigurationService().getConfiguration()
                .getString(ThirdPartyConstants.Google.API_KEY_ID + "." + getBaseSiteService().getCurrentBaseSite().getUid());
        if (StringUtils.isEmpty(googleApiKey)) {
            LOG.warn("No Google API key found for server: " + request.getServerName());
        }
        return googleApiKey;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String searchPOS(@RequestParam(value = "q", defaultValue = "", required = false)
    final String locationQuery, @RequestParam(value = "more", defaultValue = INITIAL_LOCATIONS_TO_DISPLAY_TEXT, required = false)
    final int requestedPageSize, final Model model, final HttpServletRequest request)
            throws GeoLocatorException, MapServiceException, CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        if (request.getParameterMap().containsKey("q")) {
            if (StringUtils.isBlank(locationQuery)) {
                GlobalMessages.addErrorMessage(model, "form.global.error");
                model.addAttribute("errorNoResults", "storelocator.error.no.results.subtitle");
            }
        }

        final int pageSize = Math.min(MAX_LOCATIONS_TO_DISPLAY, requestedPageSize);
        final PageableData pageableData = preparePage(pageSize);
        return searchByLocationQuery(locationQuery, model, pageSize, pageableData);
    }

    private boolean isLimitExceeded(final StoreFinderSearchPageData<PointOfServiceData> searchResult) {
        return searchResult.getPagination().getNumberOfPages() > 1 && searchResult.getPagination().getPageSize() < MAX_LOCATIONS_TO_DISPLAY;
    }

    protected String searchByLocationQuery(final String locationQuery, final Model model, final int pageSize, final PageableData pageableData)
            throws CMSItemNotFoundException {
        // Do the location search
        final StoreFinderSearchPageData<PointOfServiceData> searchResult = getStoreFinderFacade().locationSearch(locationQuery, pageableData);
        model.addAttribute("storeSearchPageData", searchResult);

        model.addAttribute("locationQuery", StringEscapeUtils.escapeHtml(searchResult.getLocationText()));

        // Decide if we are allowed to expand the result page size
        if (isLimitExceeded(searchResult)) {
            model.addAttribute("showSeeMore", Boolean.TRUE);
            model.addAttribute("seeMoreLimit", Integer.valueOf(pageSize + LOCATIONS_INCREMENT));
        } else {
            model.addAttribute("showSeeMore", Boolean.FALSE);
        }
        storeCmsPageInModel(model, getStoreFinderPage());
        setUpMetaDataForContentPage(model, (ContentPageModel) getStoreFinderPage());
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, getStorefinderBreadcrumbBuilder().getBreadcrumbs(getBreadcrumbData(locationQuery, null)));
        return ControllerConstants.Views.Pages.StoreFinder.StoreFinderSearchPage;
    }

    @RequestMapping(value = "/" + LOCATION_QUERY_PATH_VARIABLE_PATTERN + "/" + STORE_LOCATION_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    public String displayPOS(@PathVariable("storeLocation")
    final String storeLocation, @PathVariable("locationQuery")
    final String locationQuery, final Model model, final HttpServletRequest request) throws LocationServiceException, CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);

        final PointOfServiceData pointOfServiceData = getStoreFinderFacade().getPointOfServiceForName(storeLocation);
        model.addAttribute("store", pointOfServiceData);
        model.addAttribute("locationQuery", locationQuery);
        model.addAttribute("storeLocation", storeLocation);

        final Location center = getLocationService().getLocation(locationQuery, null, null, null, null, true);
        final Location dest = getLocationService().getLocationByName(storeLocation);
        final double distance = GeometryUtils.getElipticalDistanceKM(center.getGPS(), dest.getGPS());

        model.addAttribute("formattedDistance", getDistanceHelper().getDistanceStringForLocation(storeLocation, distance));
        model.addAttribute("center", center);

        AbstractPageModel storeFinderPage = getStoreFinderPage();
        storeCmsPageInModel(model, storeFinderPage);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, getStorefinderBreadcrumbBuilder().getBreadcrumbs(getBreadcrumbData(locationQuery, storeLocation)));
        setMetaRobots(model,(ContentPageModel)storeFinderPage);

        final String metaDescription = getSiteName() + " " + getMessageSource().getMessage("storeFinder.meta.description.results", null, getI18nService().getCurrentLocale()) + " " + locationQuery;
        setUpMetaData(model, metaDescription);
        updatePageTitle(locationQuery, model);

        return ControllerConstants.Views.Pages.StoreFinder.StoreFinderDetailsPage;
    }

    protected PageableData preparePage(final int pageSize) {
        final PageableData pageableData = new PageableData();
        pageableData.setCurrentPage(0); // Always on the first page
        pageableData.setPageSize(pageSize); // Adjust pageSize to see more
        return pageableData;
    }

    protected AbstractPageModel getStoreFinderPage() throws CMSItemNotFoundException {
        return getContentPageForLabelOrId(STORE_FINDER_CMS_PAGE);
    }

    protected java.util.Map<String, String> getBreadcrumbData(final String locationQuery, final String storeLocation) {
        final java.util.Map<String, String> result = new HashMap<String, String>();
        result.put(StorefinderBreadcrumbBuilder.LOCATION_QUERY_KEY, locationQuery);
        result.put(StorefinderBreadcrumbBuilder.STORE_LOCATION_KEY, storeLocation);
        return result;
    }

    protected void updatePageTitle(final String searchText, final Model model) {
        storeContentPageTitleInModel(model, getPageTitleResolver().resolveContentPageTitle(
                getMessageSource().getMessage("storeFinder.meta.title", null, getI18nService().getCurrentLocale()) + " " + searchText));
    }

    public LocationService getLocationService() {
        return locationService;
    }

    public void setLocationService(final LocationService locationService) {
        this.locationService = locationService;
    }

    @Bean(name = "distanceHelper", autowire = Autowire.BY_TYPE)
    public DistanceHelper getDistanceHelper() {
        return distanceHelper;
    }

    public void setDistanceHelper(final DistanceHelper distanceHelper) {
        this.distanceHelper = distanceHelper;
    }

    public StorefinderBreadcrumbBuilder getStorefinderBreadcrumbBuilder() {
        return storefinderBreadcrumbBuilder;
    }

    public void setStorefinderBreadcrumbBuilder(final StorefinderBreadcrumbBuilder storefinderBreadcrumbBuilder) {
        this.storefinderBreadcrumbBuilder = storefinderBreadcrumbBuilder;
    }

    public StoreFinderFacade getStoreFinderFacade() {
        return storeFinderFacade;
    }

    public void setStoreFinderFacade(final StoreFinderFacade storeFinderFacade) {
        this.storeFinderFacade = storeFinderFacade;
    }

}
