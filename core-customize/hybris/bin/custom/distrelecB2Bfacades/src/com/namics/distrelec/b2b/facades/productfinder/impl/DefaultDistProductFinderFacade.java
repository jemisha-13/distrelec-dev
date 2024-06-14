/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.impl;

import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderConfigurationModel;
import com.namics.distrelec.b2b.core.service.productfinder.DistProductFinderService;
import com.namics.distrelec.b2b.facades.productfinder.DistProductFinderFacade;
import com.namics.distrelec.b2b.facades.productfinder.data.*;
import com.namics.distrelec.b2b.facades.productfinder.helper.DistProductFinderDataCreator;
import com.namics.distrelec.b2b.facades.productfinder.helper.DistProductFinderHelper;
import com.namics.distrelec.b2b.facades.search.converter.SearchStateConverter;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.facet.FilterBadgeData;
import com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;
import com.namics.hybris.ffsearch.service.FactFinderSearchService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

/**
 * Default implementation of product finder facade.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DefaultDistProductFinderFacade implements DistProductFinderFacade {

    private DistProductFinderService distProductFinderService;
    private FactFinderSearchService<SearchQueryData, SearchResultValueData, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>, FactFinderPaginationData> productSearchService;

    private Converter<DistProductFinderData, SearchQueryData> distProductFinderDecoder;
    private SearchStateConverter searchStateConverter;

    private DistProductFinderDataCreator distProductFinderDataCreator;
    private DistProductFinderHelper distProductFinderHelper;

    @Override
    public DistProductFinderData getProductFinderData(final CategoryModel category,
            final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData, final boolean refine) {

        final DistProductFinderConfigurationModel configuration = distProductFinderService.getProductFinderConfiguration(category);
        if (configuration == null) {
            return null;
        }

        DistProductFinderData productFinderData = null;
        if (refine) {
            productFinderData = refineProductFinderData(configuration, searchPageData);
        } else {
            productFinderData = distProductFinderDataCreator.createProductFinderData(configuration, searchPageData);
        }

        updateTotalNumberOfResults(productFinderData, searchPageData);
        updateUrl(productFinderData, searchPageData.getCurrentQuery());
        return productFinderData;
    }

    @Override
    public void updateProductFinderData(final DistProductFinderData productFinderData) {
        final SearchQueryData searchQueryData = getDistProductFinderDecoder().convert(productFinderData);
        final FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> searchPageData = getProductSearchService().search(searchQueryData, null,
                true, true, null, MapUtils.EMPTY_MAP);
        updateProductFinderData(productFinderData, searchPageData);
        updateTotalNumberOfResults(productFinderData, searchPageData);
        updateUrl(productFinderData, searchPageData.getCurrentQuery());
    }

    @Override
    public String getProductFinderRefineSearchUrl(final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData) {
        final String searchUrl = searchPageData.getCurrentQuery().getUrl();
        return UriComponentsBuilder.fromUriString(searchUrl).queryParam(REFINE_PAGE_PARAM, Boolean.TRUE.toString()).build().toUriString();
    }

    private void updateProductFinderData(final DistProductFinderData productFinderData, final FactFinderProductSearchPageData<?, ?> searchPageData) {
        for (final DistProductFinderColumnData column : productFinderData.getColumns()) {
            for (final DistProductFinderGroupData group : column.getGroups()) {
                for (final DistProductFinderValueData value : group.getValues()) {
                    if (!Boolean.TRUE.equals(value.getChecked())) {
                        value.setDisabled(Boolean.valueOf(!distProductFinderHelper.isValueAvailable(value, searchPageData)));
                    }
                }

                updateCustomValue(group);
            }
        }
    }

    private void updateCustomValue(final DistProductFinderGroupData group) {
        Boolean customValueDisabled = Boolean.TRUE;

        if (group.getCustomValue() != null) {
            for (final DistProductFinderValueData value : group.getValues()) {
                if (value.getDisabled() == null || value.getDisabled().equals(Boolean.FALSE)) {
                    customValueDisabled = Boolean.FALSE;
                    break;
                }
            }

            group.getCustomValue().setDisabled(customValueDisabled);
        }

    }

    private DistProductFinderData refineProductFinderData(final DistProductFinderConfigurationModel configuration,
            final FactFinderProductSearchPageData<SearchStateData, ProductData> filteredSearchPageData) {

        final SearchQueryData unfilteredSearchQueryData = new SearchQueryData();

        // TODO rlehmann - DISTRELEC-4343: test this method
        if (StringUtils.isNotEmpty(configuration.getCategory().getCode())) {
            // set category search
            unfilteredSearchQueryData.setCode(configuration.getCategory().getCode());
            unfilteredSearchQueryData.setSearchType(DistSearchType.CATEGORY);
        }
        unfilteredSearchQueryData.setFreeTextSearch("*");
        unfilteredSearchQueryData.setFilterTerms(Collections.<SearchQueryTermData> emptyList());
        final FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> unfilteredSearchPageData = getProductSearchService().search(
                unfilteredSearchQueryData, null, true, true, null, MapUtils.EMPTY_MAP);

        final DistProductFinderData productFinderData = distProductFinderDataCreator.createProductFinderData(configuration, unfilteredSearchPageData);
        checkProductFinderValues(productFinderData, filteredSearchPageData);
        updateProductFinderData(productFinderData, filteredSearchPageData);
        return productFinderData;
    }

    private void checkProductFinderValues(final DistProductFinderData productFinderData,
            final FactFinderProductSearchPageData<SearchStateData, ProductData> filteredSearchPageData) {

        for (final DistProductFinderColumnData column : productFinderData.getColumns()) {
            for (final DistProductFinderGroupData group : column.getGroups()) {
                boolean anyValueChecked = false;

                for (final DistProductFinderValueData value : group.getValues()) {
                    if (CollectionUtils.isNotEmpty(value.getFacetValues())) {
                        if (containsAllFacetValues(filteredSearchPageData.getFilters(), value.getFacetValues())) {
                            value.setChecked(Boolean.TRUE);
                            anyValueChecked = true;
                        }
                    } else {
                        if (containsRangeValue(filteredSearchPageData.getFilters(), value)) {
                            value.setChecked(Boolean.TRUE);
                            anyValueChecked = true;
                        }
                    }
                }

                if (!anyValueChecked && group.getCustomValue() != null) {
                    updateCustomValue(filteredSearchPageData, group.getCustomValue());
                }
            }
        }
    }

    private void updateCustomValue(final FactFinderProductSearchPageData<SearchStateData, ProductData> filteredSearchPageData,
            final DistProductFinderValueData customValue) {

        for (final FilterBadgeData<SearchStateData> filter : filteredSearchPageData.getFilters()) {
            if (filter.getFacetCode().equals(customValue.getMinMaxKey())) {
                final String rangeParts[] = filter.getFacetValueCode().split(" \\- ");
                if (rangeParts.length == 2) {
                    customValue.setChecked(Boolean.TRUE);
                    customValue.setMinValue(rangeParts[0]);
                    customValue.setMaxValue(rangeParts[1]);
                    break;
                }
            }
        }
    }

    private boolean containsRangeValue(final List<FilterBadgeData<SearchStateData>> filters, final DistProductFinderValueData value) {
        if (filters != null) {
            for (final FilterBadgeData<SearchStateData> filter : filters) {
                if (filter.getFacetCode().equals(value.getMinMaxKey())) {
                    final String range = value.getMinValue() + " - " + value.getMaxValue();
                    if (filter.getFacetValueName().equals(range)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean containsAllFacetValues(final List<FilterBadgeData<SearchStateData>> filters, final List<DistProductFinderFacetValueData> facetValues) {
        for (final DistProductFinderFacetValueData facetValue : facetValues) {
            if (!containsFacetValue(filters, facetValue)) {
                return false;
            }
        }

        return true;
    }

    private boolean containsFacetValue(final List<FilterBadgeData<SearchStateData>> filters, final DistProductFinderFacetValueData facetValue) {
        if (filters == null) {
            return false;
        }

        for (final FilterBadgeData<SearchStateData> filter : filters) {
            if (filter.getFacetCode().equals(facetValue.getKey()) && filter.getFacetValueCode().equals(facetValue.getValue())) {
                return true;
            }
        }

        return false;
    }

    private void updateTotalNumberOfResults(final DistProductFinderData productFinderData, final FactFinderProductSearchPageData<?, ?> searchPageData) {
        if (searchPageData.getPagination() != null) {
            productFinderData.setTotalNumberOfResults(searchPageData.getPagination().getTotalNumberOfResults());
        }
    }

    private void updateUrl(final DistProductFinderData productFinderData, final SearchStateData searchStateData) {
        final String url = UriComponentsBuilder.fromUriString(searchStateData.getUrl()).queryParam(RESULT_PAGE_PARAM, Boolean.TRUE.toString()).build()
                .toUriString();
        productFinderData.setSearchUrl(url);
    }

    private void updateUrl(final DistProductFinderData productFinderData, final SearchQueryData searchQueryData) {
        final SearchStateData searchStateData = searchStateConverter.convert(searchQueryData);
        updateUrl(productFinderData, searchStateData);
    }

    public DistProductFinderService getDistProductFinderService() {
        return distProductFinderService;
    }

    @Required
    public void setDistProductFinderService(final DistProductFinderService distProductFinderService) {
        this.distProductFinderService = distProductFinderService;
    }

    public FactFinderSearchService<SearchQueryData, SearchResultValueData, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>, FactFinderPaginationData> getProductSearchService() {
        return productSearchService;
    }

    @Required
    public void setProductSearchService(
            final FactFinderSearchService<SearchQueryData, SearchResultValueData, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>, FactFinderPaginationData> productSearchService) {
        this.productSearchService = productSearchService;
    }

    public Converter<DistProductFinderData, SearchQueryData> getDistProductFinderDecoder() {
        return distProductFinderDecoder;
    }

    @Required
    public void setDistProductFinderDecoder(final Converter<DistProductFinderData, SearchQueryData> distProductFinderDecoder) {
        this.distProductFinderDecoder = distProductFinderDecoder;
    }

    public SearchStateConverter getSearchStateConverter() {
        return searchStateConverter;
    }

    @Required
    public void setSearchStateConverter(final SearchStateConverter searchStateConverter) {
        this.searchStateConverter = searchStateConverter;
    }

    public DistProductFinderDataCreator getDistProductFinderDataCreator() {
        return distProductFinderDataCreator;
    }

    @Required
    public void setDistProductFinderDataCreator(final DistProductFinderDataCreator distProductFinderDataCreator) {
        this.distProductFinderDataCreator = distProductFinderDataCreator;
    }

    public DistProductFinderHelper getDistProductFinderHelper() {
        return distProductFinderHelper;
    }

    @Required
    public void setDistProductFinderHelper(final DistProductFinderHelper distProductFinderHelper) {
        this.distProductFinderHelper = distProductFinderHelper;
    }

}
