/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.controller;

import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.CDN_TTL;
import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.LOCAL_TTL;
import static de.hybris.platform.webservicescommons.cache.CacheControlDirective.PUBLIC;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.namics.distrelec.occ.core.store.data.StoreCountListData;
import com.namics.distrelec.occ.core.v2.helper.StoresHelper;

import de.hybris.platform.commercefacades.storefinder.StoreFinderFacade;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceDataList;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.commercewebservicescommons.dto.store.PointOfServiceListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.store.PointOfServiceWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.store.StoreCountListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.store.StoreFinderSearchPageWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@CacheControl(directive = PUBLIC, maxAge = LOCAL_TTL, sMaxAge = CDN_TTL)
@RequestMapping(value = "/{baseSiteId}/stores")
@Tag(name = "Stores")
public class StoresController extends BaseController {
    private static final String DEFAULT_SEARCH_RADIUS_METRES = "100000.0";

    private static final String DEFAULT_ACCURACY = "0.0";

    @Resource(name = "storesHelper")
    private StoresHelper storesHelper;

    @Resource(name = "storeFinderFacade")
    private StoreFinderFacade storeFinderFacade;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getStoreLocations", summary = "Get a list of store locations", description = "Lists all store locations that are near the location specified in a query or based on latitude and longitude.")
    @ApiBaseSiteIdParam
    public StoreFinderSearchPageWsDTO getStoreLocations(
                                                        @Parameter(description = "Location in natural language i.e. city or country.") @RequestParam(required = false) final String query,
                                                        @Parameter(description = "Coordinate that specifies the north-south position of a point on the Earth's surface.") @RequestParam(required = false) final Double latitude,
                                                        @Parameter(description = "Coordinate that specifies the east-west position of a point on the Earth's surface.") @RequestParam(required = false) final Double longitude,
                                                        @Parameter(description = "The current result page requested.") @RequestParam(defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
                                                        @Parameter(description = "The number of results returned per page.") @RequestParam(defaultValue = "0") final int pageSize,
                                                        @Parameter(description = "Sorting method applied to the return results.") @RequestParam(defaultValue = "asc") final String sort,
                                                        @Parameter(description = "Radius in meters. Max value: 40075000.0 (Earth's perimeter).") @RequestParam(defaultValue = DEFAULT_SEARCH_RADIUS_METRES) final double radius,
                                                        @Parameter(description = "Accuracy in meters.") @RequestParam(defaultValue = DEFAULT_ACCURACY) final double accuracy,
                                                        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
                                                        final HttpServletResponse response) {
        final StoreFinderSearchPageWsDTO result = storesHelper.locationSearch(query, latitude, longitude, currentPage, getPageSizeOrDefault(pageSize), sort,
                                                                              radius, accuracy,
                                                                              addPaginationField(fields));

        // X-Total-Count header
        setTotalCountHeader(response, result.getPagination());

        return result;
    }

    @RequestMapping(value = { "/country/{countryIso}" }, method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getStoresByCountry", summary = "Get a list of store locations for a given country", description = "Lists all store locations that are in the specified country.")
    @ApiBaseSiteIdParam
    public PointOfServiceListWsDTO getStoresByCountry(@Parameter(description = "Country ISO code", required = true) @PathVariable final String countryIso,
                                                      @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final PointOfServiceDataList pointsOfService = new PointOfServiceDataList();
        pointsOfService.setPointOfServices(storeFinderFacade.getPointsOfServiceForCountry(countryIso));

        return getDataMapper().map(pointsOfService, PointOfServiceListWsDTO.class, fields);
    }

    @RequestMapping(value = { "/country/{countryIso}/region/{regionIso}" }, method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getStoresByCountryAndRegion", summary = "Get a list of store locations for a given country and region", description = "Lists all store locations that are in the specified country and region.")
    @ApiBaseSiteIdParam
    public PointOfServiceListWsDTO getStoresByCountryAndRegion(@Parameter(description = "Country ISO code", required = true) @PathVariable final String countryIso,
                                                               @Parameter(description = "Region ISO code", required = true) @PathVariable final String regionIso,
                                                               @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final PointOfServiceDataList pointsOfService = new PointOfServiceDataList();
        pointsOfService.setPointOfServices(storeFinderFacade.getPointsOfServiceForRegion(countryIso, regionIso));

        return getDataMapper().map(pointsOfService, PointOfServiceListWsDTO.class, fields);
    }

    @RequestMapping(method = RequestMethod.HEAD)
    @Operation(operationId = "countStoreLocations", summary = "Get a header with the number of store locations.", description = "In the response header, the \"x-total-count\" indicates the number of "
                                                                                                                                + "all store locations that are near the location specified in a query, or based on latitude and longitude.")
    @ApiBaseSiteIdParam
    public void countStoreLocations(@Parameter(description = "Location in natural language i.e. city or country.") @RequestParam(required = false) final String query,
                                    @Parameter(description = "Coordinate that specifies the north-south position of a point on the Earth's surface.") @RequestParam(required = false) final Double latitude,
                                    @Parameter(description = "Coordinate that specifies the east-west position of a point on the Earth's surface.") @RequestParam(required = false) final Double longitude,
                                    @Parameter(description = "Radius in meters. Max value: 40075000.0 (Earth's perimeter).") @RequestParam(defaultValue = DEFAULT_SEARCH_RADIUS_METRES) final double radius,
                                    @Parameter(description = "Accuracy in meters.") @RequestParam(defaultValue = DEFAULT_ACCURACY) final double accuracy,
                                    final HttpServletResponse response) {
        final StoreFinderSearchPageData<PointOfServiceData> result = storesHelper.locationSearch(query, latitude, longitude, 0, 1, "asc", radius, accuracy);

        // X-Total-Count header
        setTotalCountHeader(response, result.getPagination());
    }

    @RequestMapping(value = "/{storeId}", method = RequestMethod.GET)
    @Operation(operationId = "getStoreLocation", summary = "Get a store location", description = "Returns store location based on its unique name.")
    @ApiBaseSiteIdParam
    @ResponseBody
    public PointOfServiceWsDTO getStoreLocation(
                                                @Parameter(description = "Store identifier (currently store name)", required = true) @PathVariable final String storeId,
                                                @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        return storesHelper.locationDetails(storeId, fields);
    }

    @RequestMapping(value = "/storescounts", method = RequestMethod.GET)
    @Operation(operationId = "getLocationCounts", summary = "Gets a store location count per country and regions", description = "Returns store counts in countries and regions")
    @ApiBaseSiteIdParam
    @ResponseBody
    public StoreCountListWsDTO getLocationCounts() {
        final StoreCountListData storeCountListData = new StoreCountListData();
        storeCountListData.setCountriesAndRegionsStoreCount(storeFinderFacade.getStoreCounts());
        return getDataMapper().map(storeCountListData, StoreCountListWsDTO.class);
    }
}
