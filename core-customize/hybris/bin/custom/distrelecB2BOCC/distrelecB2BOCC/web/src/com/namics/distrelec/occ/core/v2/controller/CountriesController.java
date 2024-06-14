/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.controller;

import java.util.List;
import java.util.Locale;

import com.namics.distrelec.occ.core.readonly.ReadOnly;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.occ.core.user.data.CountryDataList;
import com.namics.distrelec.occ.core.user.data.RegionDataList;

import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.enums.CountryType;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commercewebservicescommons.annotation.SecurePortalUnauthenticatedAccess;
import de.hybris.platform.commercewebservicescommons.dto.user.CountryListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.RegionListWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/{baseSiteId}/countries")
@CacheControl(directive = CacheControlDirective.PRIVATE, maxAge = 120)
@Tag(name = "Countries")
public class CountriesController extends BaseCommerceController {

    @Autowired
    private I18NFacade i18NFacade;

    @Autowired
    private DistCheckoutFacade checkoutFacade;

    @Autowired
    private DistCustomerFacade customerFacade;

    @ReadOnly
    @SecurePortalUnauthenticatedAccess
    @GetMapping
    @Cacheable(value = "countriesCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'getCountries',#baseSiteId,#type,#fields)")
    @Operation(operationId = "getCountries", summary = "Get a list of countries.", description = "If the value of type equals to shipping, then return shipping countries. If the value of type equals to billing, then return billing countries."
                                                                                         + " If the value of type is not given, return all countries. The list is sorted alphabetically.")
    @ApiBaseSiteIdParam
    public CountryListWsDTO getCountries(
                                         @Parameter(description = "The type of countries.", schema = @Schema(allowableValues = {"SHIPPING", "BILLING"})) @RequestParam(required = false) final String type,
                                         @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        if (StringUtils.isNotBlank(type) && !CountryType.SHIPPING.toString().equalsIgnoreCase(type) && !CountryType.BILLING.toString().equalsIgnoreCase(type)) {
            throw new IllegalStateException(String.format("The value of country type : [%s] is invalid", type));
        }

        final CountryDataList dataList = new CountryDataList();
        dataList.setCountries(checkoutFacade.getCountries(StringUtils.isNotBlank(type) ? CountryType.valueOf(type) : null));
        return getDataMapper().map(dataList, CountryListWsDTO.class, fields);
    }

    @ReadOnly
    @GetMapping("/registration")
    @Cacheable(value = "countriesCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'getRegistrationCountries',#baseSiteId,#registrationType,#fields)")
    @Operation(operationId = "getRegistrationCountries", summary = "Get a list of countries for registration.")
    @ApiBaseSiteIdParam
    public CountryListWsDTO getRegistrationCountries(
                                                     @Parameter(description = "The type of countries.") @RequestParam final String registrationType,
                                                     @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        if (StringUtils.isBlank(registrationType) || (!SiteChannel.B2B.toString().equalsIgnoreCase(registrationType) && !SiteChannel.B2C.toString().equalsIgnoreCase(registrationType))) {
            throw new IllegalStateException(String.format("The value of registration type : [%s] is invalid", registrationType));
        }

        final CountryDataList dataList = new CountryDataList();
        dataList.setCountries(customerFacade.getCountriesForRegistration(SiteChannel.valueOf(registrationType)));
        return getDataMapper().map(dataList, CountryListWsDTO.class, fields);
    }

    @ReadOnly
    @SecurePortalUnauthenticatedAccess
    @GetMapping("/{countyIsoCode}/regions")
    @ResponseStatus(value = HttpStatus.OK)
    @Cacheable(value = "countriesCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'getRegionsForCountry',#countyIsoCode,#fields)")
    @Operation(operationId = "getCountryRegions", summary = "Fetch the list of regions for the provided country.", description = "Lists all regions.")
    @ApiBaseSiteIdParam
    public RegionListWsDTO getCountryRegions(@Parameter(description = "An ISO code for a country", required = true) @PathVariable final String countyIsoCode,
                                             @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final RegionDataList regionDataList = new RegionDataList();
        regionDataList.setRegions(i18NFacade.getRegionsForCountryIso(countyIsoCode.toUpperCase(Locale.ENGLISH)));

        return getDataMapper().map(regionDataList, RegionListWsDTO.class, fields);
    }

    @ReadOnly
    @GetMapping("/delivery/guest")
    @ResponseStatus(value = HttpStatus.OK)
    @Cacheable(value = "countriesCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'getGuestCheckoutCountries',#baseSiteId,#fields)")
    @Operation(operationId = "getGuestCheckoutCountries", summary = "Get a list of countries for guest checkout delivery.")
    @ApiBaseSiteIdParam
    public CountryListWsDTO getGuestCheckoutDeliveryCountries(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        List<CountryData> countries = getDistCheckoutFacade().getDeliveryCountriesForGuestCheckout(SiteChannel.B2C);
        final CountryDataList dataList = new CountryDataList();
        dataList.setCountries(countries);
        return getDataMapper().map(dataList, CountryListWsDTO.class, fields);
    }

    @ReadOnly
    @GetMapping("/delivery-countries")
    @ResponseBody
    @Operation(operationId = "getDeliveryCountries", summary = "Get a list of delivery countries.")
    @ApiBaseSiteIdParam
    public CountryListWsDTO getDeliveryCountries(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        List<CountryData> countries = getDistCheckoutFacade().getDeliveryCountriesAndRegions();
        final CountryDataList dataList = new CountryDataList();
        dataList.setCountries(countries);
        return getDataMapper().map(dataList, CountryListWsDTO.class, fields);
    }
}
