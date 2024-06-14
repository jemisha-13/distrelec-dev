/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.constants.DistConfigConstants.SearchExperienceConfig;
import com.namics.distrelec.b2b.core.enums.SearchExperience;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.environment.RuntimeEnvironmentService;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.product.DistPriceDataFactory;
import com.namics.distrelec.b2b.facades.search.FusionSearchService;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetData;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetValueData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.facet.FilterBadgeData;
import com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.suggest.AutocompleteCategoryResult;
import com.namics.hybris.ffsearch.data.suggest.AutocompleteSuggestion;
import com.namics.hybris.ffsearch.data.suggest.CategorySuggestion;
import com.namics.hybris.ffsearch.data.suggest.ProductSuggestion;
import com.namics.hybris.ffsearch.service.FactFinderAutocompleteService;
import com.namics.hybris.ffsearch.service.FactFinderSearchService;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfFilter;
import de.factfinder.webservice.ws71.FFsearch.ArrayOfFilterValue;
import de.factfinder.webservice.ws71.FFsearch.Filter;
import de.factfinder.webservice.ws71.FFsearch.FilterValue;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.AutocompleteSuggestionData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

/**
 * Default implementation of {@link ProductSearchFacade}.
 *
 * @param <ITEM>
 *            Type of data to be returned as a search result.
 */
public class DefaultDistProductSearchFacade<ITEM extends ProductData> implements ProductSearchFacade<ITEM> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistProductSearchFacade.class);

    private FactFinderSearchService<SearchQueryData, SearchResultValueData, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>, FactFinderPaginationData> productSearchService;

    private Converter<FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>, FactFinderProductSearchPageData<SearchStateData, ITEM>> productSearchPageConverter;

    private Converter<FactFinderPaginationData<SearchQueryData>, FactFinderPaginationData<SearchStateData>> productPaginationConverter;

    private Converter<String, SearchQueryData> searchQueryDecoder;

    private Converter<AutocompleteSuggestion, AutocompleteSuggestionData> autocompleteSuggestionConverter;

    private FactFinderAutocompleteService<AutocompleteSuggestion> autocompleteService;

    private FactFinderAutocompleteService<AutocompleteSuggestion> directOrderAutocompleteService;

    private DistPriceDataFactory priceDataFactory;

    private DistCategoryService distCategoryService;

    private DistCategoryFacade distCategoryFacade;

    private DistrelecStoreSessionFacade distrelecStoreSessionFacade;

    private BaseStoreService baseStoreService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private RuntimeEnvironmentService runtimeEnvironmentService;

    @Autowired
    private FusionSearchService fusionSearchService;

    private static String urlEncode(final String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }

    @Override
    public SearchExperience getSearchExperienceFromCurrentBaseStore() {
        String currentLanguage = distrelecStoreSessionFacade.getCurrentLanguageIsoCode();

        SearchExperience currentSearchExperience = null;
        if (currentLanguage != null) {
            BaseStoreModel currentBaseStore = baseStoreService.getCurrentBaseStore();

            if (!runtimeEnvironmentService.isHeadless()) {
                Map<String, SearchExperience> accSearchExperiences = currentBaseStore.getSearchExperienceAccMap();
                currentSearchExperience = accSearchExperiences.get(currentLanguage);
            }
            if (currentSearchExperience == null) {
                Map<String, SearchExperience> headlessSearchExperiences = currentBaseStore.getSearchExperienceMap();
                currentSearchExperience = headlessSearchExperiences.get(currentLanguage);
            }
        }

        if (currentSearchExperience == null) {
            // get default search experience
            if (runtimeEnvironmentService.isHeadless()) {
                currentSearchExperience = SearchExperienceConfig.DEFAULT_HEADLESS_SEARCH_EXPERIENCE;
            } else {
                String searchExperience = configurationService.getConfiguration()
                                                              .getString(
                                                                         SearchExperienceConfig.DEFAULT_ACC_SEARCH_EXPERIENCE_CONFIG,
                                                                         SearchExperienceConfig.DEFAULT_ACC_SEARCH_EXPERIENCE.getCode());
                currentSearchExperience = SearchExperience.valueOf(searchExperience);
            }
        }
        return currentSearchExperience;
    }

    @Override
    public FactFinderProductSearchPageData<SearchStateData, ITEM> search(final SearchStateData searchState, final PageableData pageableData) {
        return search(false, searchState, pageableData, DistSearchType.TEXT, null, false, "internal", MapUtils.EMPTY_MAP);
    }

    @Override
    public FactFinderProductSearchPageData<SearchStateData, ITEM> search(final boolean tracking, final SearchStateData searchState,
                                                                         final PageableData pageableData, final DistSearchType searchType, final String code,
                                                                         final boolean generateASN, final String log,
                                                                         final Map<String, List<String>> otherParams) {
        // DISTRELEC-5477: set disable web attribute count to true ->
        // webuseAttributeCount=-1
        return search(tracking, searchState, pageableData, searchType, code, generateASN, true, log, otherParams);
    }

    @Override
    public FactFinderProductSearchPageData<SearchStateData, ITEM> search(final boolean tracking, final SearchStateData searchState,
                                                                         final PageableData pageableData, final DistSearchType searchType,
                                                                         final String code, final boolean generateASN, final String log,
                                                                         final Map<String, List<String>> otherParams, String sessionId) {
        // DISTRELEC-5477: set disable web attribute count to true ->
        // webuseAttributeCount=-1
        return search(tracking, searchState, pageableData, searchType, code, generateASN, true, log, otherParams, sessionId);
    }

    @Override
    public FactFinderProductSearchPageData<SearchStateData, ITEM> search(final boolean tracking, final SearchStateData searchState,
                                                                         final PageableData pageableData, final DistSearchType searchType, final String code,
                                                                         final boolean generateASN, final boolean idsOnly,
                                                                         final boolean disableWebUseAttributeCount, final String log,
                                                                         final Map<String, List<String>> otherSearchParams) {
        Assert.notNull(searchState, "SearchStateData must not be null.");
        return search(decodeState(tracking, searchState, searchType, code), pageableData, generateASN, idsOnly, disableWebUseAttributeCount, log,
                      otherSearchParams);
    }

    @Override
    public FactFinderProductSearchPageData<SearchStateData, ITEM> search(final boolean tracking, final SearchStateData searchState,
                                                                         final PageableData pageableData, final DistSearchType searchType,
                                                                         final String code, final boolean generateASN, final boolean idsOnly,
                                                                         final boolean disableWebUseAttributeCount, final String log,
                                                                         final Map<String, List<String>> otherSearchParams, final String sessionId) {
        Assert.notNull(searchState, "SearchStateData must not be null.");
        return search(decodeState(tracking, searchState, searchType, code, sessionId), pageableData, generateASN, idsOnly, disableWebUseAttributeCount, log,
                      otherSearchParams);
    }
    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.facades.search.ProductSearchFacade#search(boolean,
     * de.hybris.platform.commercefacades.search.data.SearchStateData, de.hybris.platform.commerceservices.search.pagedata.PageableData,
     * com.namics.hybris.ffsearch.data.search.DistSearchType, java.lang.String, boolean, boolean, boolean, boolean, java.lang.String,
     * java.util.Map)
     */

    @Override
    public FactFinderProductSearchPageData<SearchStateData, ITEM> search(final boolean tracking, final SearchStateData searchState,
                                                                         final PageableData pageableData, final DistSearchType searchType, final String code,
                                                                         final boolean generateASN, final boolean idsOnly,
                                                                         final boolean useCampaigns, final boolean disableWebUseAttributeCount,
                                                                         final String log, final Map<String, List<String>> otherSearchParams) {
        Assert.notNull(searchState, "SearchStateData must not be null.");
        return search(decodeState(tracking, searchState, searchType, code), pageableData, generateASN, idsOnly, useCampaigns, disableWebUseAttributeCount, log,
                      otherSearchParams);
    }
    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.facades.search.ProductSearchFacade#search(com.namics.hybris.ffsearch.data.search.SearchQueryData,
     * de.hybris.platform.commerceservices.search.pagedata.PageableData, boolean, boolean, boolean, boolean, java.lang.String,
     * java.util.Map)
     */

    @Override
    public FactFinderProductSearchPageData<SearchStateData, ITEM> search(final SearchQueryData searchQuery, final PageableData pageableData,
                                                                         final boolean generateASN, final boolean idsOnly, final boolean useCampaigns,
                                                                         final boolean disableWebUseAttributeCount, final String log,
                                                                         final Map<String, List<String>> otherParams) {
        Assert.notNull(searchQuery, "searchQuery must not be null.");
        // execute search and convert results
        return getProductSearchPageConverter()
                                              .convert(getProductSearchService().search(searchQuery, pageableData, false, generateASN, idsOnly, useCampaigns,
                                                                                        log, otherParams));
    }

    @Override
    public FactFinderProductSearchPageData<SearchStateData, ITEM> search(final boolean tracking, final SearchStateData searchState,
                                                                         final PageableData pageableData, final DistSearchType searchType, final String code,
                                                                         final boolean generateASN,
                                                                         final boolean disableWebUseAttributeCount, final String log,
                                                                         final Map<String, List<String>> otherParams) {
        Assert.notNull(searchState, "SearchStateData must not be null.");

        SearchQueryData searchQueryData = decodeState(tracking, searchState, searchType, code);
        SearchExperience searchExperience = searchState.getSearchExperience();
        if (searchExperience == null) {
            searchExperience = getSearchExperienceFromCurrentBaseStore();
        }

        switch (searchExperience) {
            case FACTFINDER:
                return search(searchQueryData, pageableData, generateASN, disableWebUseAttributeCount, log, otherParams);
            case FUSION:
                return fusionSearchService.search(searchQueryData, pageableData);
            default:
                throw new IllegalArgumentException();
        }
    }

    // End convenience methods

    @Override
    public FactFinderProductSearchPageData<SearchStateData, ITEM> search(final boolean tracking, final SearchStateData searchState,
                                                                         final PageableData pageableData, final DistSearchType searchType,
                                                                         final String code, final boolean generateASN,
                                                                         final boolean disableWebUseAttributeCount, final String log,
                                                                         final Map<String, List<String>> otherParams,
                                                                         String sessionId) {
        Assert.notNull(searchState, "SearchStateData must not be null.");
        return search(decodeState(tracking, searchState, searchType, code, sessionId), pageableData, generateASN, disableWebUseAttributeCount, log,
                      otherParams);
    }

    @Override
    public FactFinderProductSearchPageData<SearchStateData, ITEM> search(final SearchQueryData searchQuery, final PageableData pageableData,
                                                                         final boolean generateASN, final boolean disableWebUseAttributeCount, final String log,
                                                                         final Map<String, List<String>> otherParams) {
        Assert.notNull(searchQuery, "searchQuery must not be null.");
        // execute search and convert results
        return getProductSearchPageConverter().convert(getProductSearchService().search(searchQuery, pageableData, false, generateASN, log, otherParams));
    }
    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.facades.search.ProductSearchFacade#search(com.namics.hybris.ffsearch.data.search.SearchQueryData,
     * de.hybris.platform.commerceservices.search.pagedata.PageableData, boolean, boolean, boolean, java.lang.String, java.util.Map)
     */

    @Override
    public FactFinderProductSearchPageData<SearchStateData, ITEM> search(final SearchQueryData searchQuery, final PageableData pageableData,
                                                                         final boolean generateASN, final boolean idsOnly,
                                                                         final boolean disableWebUseAttributeCount, final String log,
                                                                         final Map<String, List<String>> otherParams) {
        Assert.notNull(searchQuery, "searchQuery must not be null.");
        // execute search and convert results
        return getProductSearchPageConverter()
                                              .convert(getProductSearchService().search(searchQuery, pageableData, false, generateASN, idsOnly, log,
                                                                                        otherParams));
    }

    @Override
    public AutocompleteSuggestion getAutocompleteSuggestions(final String input, final ArrayOfFilter arrayOfFilter) {
        final AutocompleteSuggestion suggestion = getAutocompleteService().getAutocompleteSuggestions(input, arrayOfFilter);
        cleanCategorySuggestion(suggestion);
        updateFormattedPriceValue(suggestion);
        return suggestion;
    }

    @Override
    public AutocompleteSuggestion getDirectOrderAutocompleteSuggestions(final String input, final ArrayOfFilter arrayOfFilter) {
        final AutocompleteSuggestion suggestion = getDirectOrderAutocompleteService().getAutocompleteSuggestions(input, arrayOfFilter);
        updateFormattedPriceValue(suggestion);
        return suggestion;
    }

    private void cleanCategorySuggestion(final AutocompleteSuggestion suggestion) {
        final AutocompleteCategoryResult categories = suggestion.getRes().getCats();
        final List<CategorySuggestion> removeList = new ArrayList<CategorySuggestion>();
        for (final CategorySuggestion category : categories.getList()) {
            final String catUrl = category.getUrl();
            if (catUrl != null && catUrl.length() > 0) {
                final String catCode = catUrl.substring(catUrl.lastIndexOf("/") + 1);
                final CategoryModel categoryModel = getDistCategoryService().getCategoryForCode(catCode);
                if (categoryModel != null && distCategoryFacade.isCategoryEmptyForCurrentSite(categoryModel)) {
                    removeList.add(category);
                }
            }
        }
        if (removeList.size() > 0) {
            categories.getList().removeAll(removeList);
        }
    }

    private void updateFormattedPriceValue(final AutocompleteSuggestion suggestion) {
        for (final ProductSuggestion productSuggestion : suggestion.getRes().getProds().getList()) {
            productSuggestion.setFormattedPriceValue(getPriceDataFactory().formatPriceValue(productSuggestion.getPrice(), productSuggestion.getCurrencyIso()));
        }
    }

    @Override
    public FactFinderPaginationData getPagination(final boolean tracking, final SearchStateData searchState, final PageableData pageableData,
                                                  final DistSearchType searchType, final String code) {
        Assert.notNull(searchState, "SearchStateData must not be null.");
        return getProductPaginationConverter()
                                              .convert(getProductSearchService().doPaginationSearch(decodeState(tracking, searchState, searchType, code),
                                                                                                    pageableData, false));
    }

    @Override
    public FactFinderFacetData<SearchStateData> loadAdditionalFacet(final SearchStateData searchState, final PageableData pageableData,
                                                                    final String additionalFacetCode, final DistSearchType searchType, final String code) {
        final SearchQueryData searchQuery = decodeState(false, searchState, searchType, code);
        searchQuery.getAdditionalSearchParams().setAdditionalFacetCode(additionalFacetCode);

        final FactFinderProductSearchPageData<SearchStateData, ITEM> searchPageData = getProductSearchPageConverter()
                                                                                                                     .convert(getProductSearchService().search(searchQuery,
                                                                                                                                                               pageableData,
                                                                                                                                                               false,
                                                                                                                                                               true,
                                                                                                                                                               null,
                                                                                                                                                               MapUtils.EMPTY_MAP));

        if (searchPageData != null && CollectionUtils.isNotEmpty(searchPageData.getOtherFacets())) {
            for (final FactFinderFacetData<SearchStateData> facet : searchPageData.getOtherFacets()) {
                if (additionalFacetCode.equals(facet.getCode())) {
                    return facet;
                }
            }
        }

        LOG.error("Could not load additional facet for query [" + searchState.getQuery().getValue() + "] and facet code [" + additionalFacetCode + "]");
        return null;
    }

    @Override
    public SearchQueryData decodeState(final boolean tracking, final SearchStateData searchState, final DistSearchType searchType, final String code) {
        return decodeState(tracking, searchState, searchType, code, null);
    }

    @Override
    public SearchQueryData decodeState(final boolean tracking, final SearchStateData searchState, final DistSearchType searchType, final String code,
                                       final String sessionId) {
        final SearchQueryData searchQueryData = getSearchQueryDecoder().convert(searchState.getQuery().getValue());
        searchQueryData.setCode(code);
        searchQueryData.setSearchType(searchType);
        searchQueryData.setTracking(tracking);
        searchQueryData.setSessionId(sessionId);
        return searchQueryData;
    }

    protected FactFinderSearchService<SearchQueryData, SearchResultValueData, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>, FactFinderPaginationData> getProductSearchService() {
        return productSearchService;
    }

    @Required
    public void setProductSearchService(
                                        final FactFinderSearchService<SearchQueryData, SearchResultValueData, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>, FactFinderPaginationData> productSearchService) {
        this.productSearchService = productSearchService;
    }

    protected Converter<FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>, FactFinderProductSearchPageData<SearchStateData, ITEM>> getProductSearchPageConverter() {
        return productSearchPageConverter;
    }

    @Required
    public void setProductSearchPageConverter(
                                              final Converter<FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>, FactFinderProductSearchPageData<SearchStateData, ITEM>> productSearchPageConverter) {
        this.productSearchPageConverter = productSearchPageConverter;
    }

    protected Converter<FactFinderPaginationData<SearchQueryData>, FactFinderPaginationData<SearchStateData>> getProductPaginationConverter() {
        return productPaginationConverter;
    }

    @Required
    public void setProductPaginationConverter(
                                              final Converter<FactFinderPaginationData<SearchQueryData>, FactFinderPaginationData<SearchStateData>> productPaginationConverter) {
        this.productPaginationConverter = productPaginationConverter;
    }

    protected Converter<String, SearchQueryData> getSearchQueryDecoder() {
        return searchQueryDecoder;
    }

    @Required
    public void setSearchQueryDecoder(final Converter<String, SearchQueryData> searchQueryDecoder) {
        this.searchQueryDecoder = searchQueryDecoder;
    }

    protected Converter<AutocompleteSuggestion, AutocompleteSuggestionData> getAutocompleteSuggestionConverter() {
        return autocompleteSuggestionConverter;
    }

    @Required
    public void setAutocompleteSuggestionConverter(final Converter<AutocompleteSuggestion, AutocompleteSuggestionData> autocompleteSuggestionConverter) {
        this.autocompleteSuggestionConverter = autocompleteSuggestionConverter;
    }

    protected FactFinderAutocompleteService<AutocompleteSuggestion> getAutocompleteService() {
        return autocompleteService;
    }

    @Required
    public void setAutocompleteService(final FactFinderAutocompleteService<AutocompleteSuggestion> autocompleteService) {
        this.autocompleteService = autocompleteService;
    }

    public FactFinderAutocompleteService<AutocompleteSuggestion> getDirectOrderAutocompleteService() {
        return directOrderAutocompleteService;
    }

    @Required
    public void setDirectOrderAutocompleteService(final FactFinderAutocompleteService<AutocompleteSuggestion> directOrderAutocompleteService) {
        this.directOrderAutocompleteService = directOrderAutocompleteService;
    }

    public DistPriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    @Required
    public void setPriceDataFactory(final DistPriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    public DistCategoryService getDistCategoryService() {
        return distCategoryService;
    }

    @Required
    public void setDistCategoryService(final DistCategoryService distCategoryService) {
        this.distCategoryService = distCategoryService;
    }

    @Override
    public ArrayOfFilter createCategoryRestrictionsArray(final String categoryRestrictions) {
        ArrayOfFilter filterArray = null;
        if (categoryRestrictions != null && !StringUtils.isEmpty(categoryRestrictions)) {
            final String[] filterCatCodes = categoryRestrictions.split("\\s*,\\s*");
            filterArray = new ArrayOfFilter();
            for (final String code : filterCatCodes) {
                final CategoryModel category = distCategoryService.getCategory(code);
                if (category != null) {
                    final Filter filter = new Filter();
                    if (category.getLevel() != null) {
                        filter.setName("Category" + category.getLevel());
                    } else {
                        filter.setName("Category1");
                    }
                    // FilterValue
                    final ArrayOfFilterValue valueArray = new ArrayOfFilterValue();
                    final FilterValue filterValue = new FilterValue();
                    filterValue.setValue(urlEncode(category.getName()));
                    valueArray.getFilterValue().add(filterValue);
                    filter.setValueList(valueArray);

                    filterArray.getFilter().add(filter);
                }
            }
        }
        return filterArray;
    }

    @Override
    public void addFilterstrings(final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData) {
        if (searchPageData != null && searchPageData.getFilters() != null && searchPageData.getFilters().size() > 0) {
            for (final FilterBadgeData<SearchStateData> filterData : searchPageData.getFilters()) {
                // filterData.setFilterString("filter_" +
                // urlEncode(filterData.getFacetCode()) + "=" +
                // urlEncode(filterData.getFacetValueName()));
                filterData.setFilterString("filter_" + urlEncode(filterData.getFacetCode()) + "=" + urlEncode(filterData.getFacetValueCode()));
            }
        }

        if (searchPageData != null && searchPageData.getOtherFacets() != null && searchPageData.getOtherFacets().size() > 0) {
            for (final FactFinderFacetData<SearchStateData> facetData : searchPageData.getOtherFacets()) {
                for (final FactFinderFacetValueData<SearchStateData> valueData : facetData.getValues()) {
                    // valueData.setFilterString("filter_" +
                    // urlEncode(facetData.getCode()) + "=" +
                    // urlEncode(valueData.getName()));
                    // if (valueData.getQueryFilter() != null &&
                    // valueData.getQueryFilter().length() > 0) {
                    valueData.setFilterString(valueData.getQueryFilter());
                    // } else {
                    // valueData.setFilterString("filter_" +
                    // urlEncode(facetData.getCode()) + "=" +
                    // urlEncode(valueData.getCode()));
                    // }
                }
            }
        }
    }

    public DistCategoryFacade getDistCategoryFacade() {
        return distCategoryFacade;
    }

    public void setDistCategoryFacade(final DistCategoryFacade distCategoryFacade) {
        this.distCategoryFacade = distCategoryFacade;
    }

    @Required
    public void setDistrelecStoreSessionFacade(final DistrelecStoreSessionFacade distrelecStoreSessionFacade) {
        this.distrelecStoreSessionFacade = distrelecStoreSessionFacade;
    }

    @Required
    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setRuntimeEnvironmentService(RuntimeEnvironmentService runtimeEnvironmentService) {
        this.runtimeEnvironmentService = runtimeEnvironmentService;
    }
}
