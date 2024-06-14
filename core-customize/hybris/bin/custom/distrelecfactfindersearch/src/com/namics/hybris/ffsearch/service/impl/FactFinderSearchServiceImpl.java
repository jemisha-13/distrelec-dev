/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.service.impl;

import static com.namics.hybris.ffsearch.util.DistFactFinderUtils.getCategoryCodeFFAttribute;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService;
import com.namics.distrelec.b2b.core.service.search.pagedata.SearchPageableData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData;
import com.namics.hybris.ffsearch.data.search.AdditionalControlParams;
import com.namics.hybris.ffsearch.data.search.AdditionalSearchParams;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;
import com.namics.hybris.ffsearch.data.search.SearchResponse;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;
import com.namics.hybris.ffsearch.service.FactFinderSearchService;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * The FactFinder search service implementation of {@link FactFinderSearchService}.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0, refactored in Distrelec 2.0.22
 * 
 * @param <ITEM>
 */
public class FactFinderSearchServiceImpl<ITEM>
        implements FactFinderSearchService<SearchQueryData, ITEM, FactFinderProductSearchPageData<SearchQueryData, ITEM>, FactFinderPaginationData> {

    private Converter<SearchQueryPageableData<SearchQueryData>, SearchRequest> searchRequestConverter;
    private Converter<SearchRequest, SearchResponse> searchServiceConverter;
    private Converter<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> searchResponseConverter;
    private Converter<SearchResponse, FactFinderPaginationData> searchResponsePagingConverter;

    private DistEProcurementCustomerConfigService distEProcurementService;
    
    @Autowired
    private ConfigurationService configurationService;


    @Override
    public FactFinderProductSearchPageData<SearchQueryData, ITEM> search(final SearchQueryData searchQueryData, final PageableData pageableData,
            final boolean disableWebUseAttributeCount, final boolean generateASN, final String log, final Map<String, List<String>> otherSearchParams) {

        createAdditionalSearchParam(searchQueryData, log, Boolean.valueOf(disableWebUseAttributeCount), otherSearchParams);
        createAdditionalControlParam(searchQueryData, generateASN, false, true);

        return doSearch(searchQueryData, pageableData);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.hybris.ffsearch.service.FactFinderSearchService#search(java.lang.Object,
     * de.hybris.platform.commerceservices.search.pagedata.PageableData, boolean, boolean, boolean, java.lang.String, java.util.Map)
     */
    @Override
    public FactFinderProductSearchPageData<SearchQueryData, ITEM> search(final SearchQueryData searchQueryData, PageableData pageableData,
            final boolean disableWebUseAttributeCount, boolean generateASN, final boolean idsOnly, final String log,
            final Map<String, List<String>> otherSearchParams) {
        createAdditionalSearchParam(searchQueryData, log, Boolean.valueOf(disableWebUseAttributeCount), otherSearchParams);
        createAdditionalControlParam(searchQueryData, generateASN, idsOnly, true);

        return doSearch(searchQueryData, pageableData);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.hybris.ffsearch.service.FactFinderSearchService#search(java.lang.Object,
     * de.hybris.platform.commerceservices.search.pagedata.PageableData, boolean, boolean, boolean, boolean, java.lang.String,
     * java.util.Map)
     */
    @Override
    public FactFinderProductSearchPageData<SearchQueryData, ITEM> search(final SearchQueryData searchQueryData, PageableData pageableData,
            final boolean disableWebUseAttributeCount, boolean generateASN, final boolean idsOnly, final boolean useCampaigns, final String log,
            final Map<String, List<String>> otherSearchParams) {
        createAdditionalSearchParam(searchQueryData, log, Boolean.valueOf(disableWebUseAttributeCount), otherSearchParams);
        createAdditionalControlParam(searchQueryData, generateASN, idsOnly, useCampaigns);

        return doSearch(searchQueryData, pageableData);
    }

    @Override
    public FactFinderPaginationData doPaginationSearch(final SearchQueryData searchQueryData, final PageableData pageableData,
            final boolean disableWebUseAttributeCount) {
        validateParameterNotNull(searchQueryData, "SearchQueryData cannot be null");

        createAdditionalSearchParam(searchQueryData, searchQueryData.getLog(), Boolean.valueOf(disableWebUseAttributeCount), MapUtils.EMPTY_MAP);
        createAdditionalControlParam(searchQueryData, false, false, true);

        final SearchResponse searchResponse = executeSearch(searchQueryData, pageableData);
        return getSearchResponsePagingConverter().convert(searchResponse);
    }

    protected FactFinderProductSearchPageData<SearchQueryData, ITEM> doSearch(final SearchQueryData searchQueryData, final PageableData pageableData) {
        validateParameterNotNull(searchQueryData, "SearchQueryData cannot be null");

        // populate session-id for load-balancing

        final SearchResponse searchResponse = executeSearch(searchQueryData, pageableData);
        if (pageableData instanceof SearchPageableData) {
            searchResponse.setTechnicalView(((SearchPageableData) pageableData).isTechnicalView());
        }

        return getSearchResponseConverter().convert(searchResponse);
    }

    protected SearchResponse executeSearch(final SearchQueryData searchQueryData, final PageableData pageableData) {
        applyBuyableFilterTerm(searchQueryData);
        applyEProcurementFilter(searchQueryData);

        // Create the SearchQueryPageableData that contains our parameters
        final SearchQueryPageableData<SearchQueryData> searchQueryPageableData = buildSearchQueryAndPageableData(searchQueryData, pageableData);
        // Build up the search request
        final SearchRequest searchRequest = getSearchRequestConverter().convert(searchQueryPageableData);
        // Execute the search
        return getSearchServiceConverter().convert(searchRequest);
    }

    private SearchQueryPageableData<SearchQueryData> buildSearchQueryAndPageableData(final SearchQueryData searchQueryData, final PageableData pageableData) {

        final SearchQueryPageableData<SearchQueryData> searchQueryPageableData = createSearchQueryPageableData();

        if (StringUtils.isBlank(searchQueryData.getFreeTextSearch())) {
            searchQueryData.setFreeTextSearch("*");
        }
        if (searchQueryData.getFilterTerms() == null) {
            searchQueryData.setFilterTerms(Collections.<SearchQueryTermData> emptyList());
        }
        if (pageableData != null && StringUtils.isNotBlank(pageableData.getSort())) {
            searchQueryData.setSort(pageableData.getSort());
        }
        searchQueryPageableData.setSearchQueryData(searchQueryData);
        searchQueryPageableData.setPageableData(pageableData);
        return searchQueryPageableData;
    }

    private SearchQueryPageableData<SearchQueryData> createSearchQueryPageableData() {
        return new SearchQueryPageableData<SearchQueryData>();
    }

    private void createAdditionalSearchParam(final SearchQueryData searchQueryData, final String log, final Boolean disableWebUseAttributeCount,
            final Map<String, List<String>> otherParams) {
        AdditionalSearchParams additionalSearchParams = searchQueryData.getAdditionalSearchParams();
        if (additionalSearchParams == null) {
            additionalSearchParams = new AdditionalSearchParams();
        }
        additionalSearchParams.setLog(log);
        additionalSearchParams.setDisableWebUseAttributeCount(disableWebUseAttributeCount);
        additionalSearchParams.setOtherParams(otherParams);
        searchQueryData.setAdditionalSearchParams(additionalSearchParams);
    }

    private void createAdditionalControlParam(final SearchQueryData searchQueryData, final boolean generateASN, final boolean idsOnly,
            final boolean useCampaigns) {
        AdditionalControlParams additionalControlParams = searchQueryData.getAdditionalControlParams();
        if (additionalControlParams == null) {
            additionalControlParams = new AdditionalControlParams();
        }
        additionalControlParams.setGenerateASN(generateASN);
        additionalControlParams.setIdsOnly(idsOnly);
        additionalControlParams.setUseCampaigns(useCampaigns);
        additionalControlParams.setUsePersonalization(getConfigurationService().getConfiguration().getBoolean("ff.personalisation.feature", false));
        searchQueryData.setAdditionalControlParams(additionalControlParams);
    }

    private void applyBuyableFilterTerm(final SearchQueryData searchQueryData) {
        // Apply only if no text search
        if (!DistSearchType.TEXT.equals(searchQueryData.getSearchType())) {
            // Check if already applied
            if (searchQueryData.getFilterTerms() != null) {
                for (final SearchQueryTermData term : searchQueryData.getFilterTerms()) {
                    if (DistFactFinderExportColumns.BUYABLE.getValue().equals(term.getKey())) {
                        return;
                    }
                }
            }

            // Not yet applied
            final List<SearchQueryTermData> terms = new ArrayList<SearchQueryTermData>();
            if (searchQueryData.getFilterTerms() != null) {
                terms.addAll(searchQueryData.getFilterTerms());
            }
            terms.add(createSearchQueryTerm(DistFactFinderExportColumns.BUYABLE.getValue(), "1"));
            searchQueryData.setFilterTerms(terms);
        }
    }

    private void applyEProcurementFilter(final SearchQueryData searchQueryData) {
        final List<CategoryModel> allowedCategories = getDistEProcurementService().getAllowedCategories();
        if (!allowedCategories.isEmpty()) {
            final List<SearchQueryTermData> terms = new ArrayList<SearchQueryTermData>(searchQueryData.getFilterTerms());
            for (final CategoryModel category : allowedCategories) {
                terms.add(createSearchQueryTerm(getCategoryCodeFFAttribute(category.getLevel()), category.getCode()));
            }
            searchQueryData.setFilterTerms(terms);
        }
    }

    private SearchQueryTermData createSearchQueryTerm(final String key, final String value) {
        final SearchQueryTermData buyableTerm = new SearchQueryTermData();
        buyableTerm.setKey(key);
        buyableTerm.setValue(value);
        return buyableTerm;
    }

    // BEGIN GENERATED CODE

    protected Converter<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> getSearchResponseConverter() {
        return searchResponseConverter;
    }

    @Required
    public void setSearchResponseConverter(final Converter<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> searchResponseConverter) {
        this.searchResponseConverter = searchResponseConverter;
    }

    protected Converter<SearchResponse, FactFinderPaginationData> getSearchResponsePagingConverter() {
        return searchResponsePagingConverter;
    }

    @Required
    public void setSearchResponsePagingConverter(final Converter<SearchResponse, FactFinderPaginationData> searchPaginationResponseConverter) {
        this.searchResponsePagingConverter = searchPaginationResponseConverter;
    }

    protected Converter<SearchQueryPageableData<SearchQueryData>, SearchRequest> getSearchRequestConverter() {
        return searchRequestConverter;
    }

    @Required
    public void setSearchRequestConverter(final Converter<SearchQueryPageableData<SearchQueryData>, SearchRequest> searchRequestConverter) {
        this.searchRequestConverter = searchRequestConverter;
    }

    protected Converter<SearchRequest, SearchResponse> getSearchServiceConverter() {
        return searchServiceConverter;
    }

    @Required
    public void setSearchServiceConverter(final Converter<SearchRequest, SearchResponse> searchServiceConverter) {
        this.searchServiceConverter = searchServiceConverter;
    }

    public DistEProcurementCustomerConfigService getDistEProcurementService() {
        return distEProcurementService;
    }

    @Required
    public void setDistEProcurementService(final DistEProcurementCustomerConfigService distEProcurementService) {
        this.distEProcurementService = distEProcurementService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
    
    

}
