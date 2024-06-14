/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.response;

import org.apache.commons.collections.CollectionUtils;

import com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData;
import com.namics.hybris.ffsearch.data.search.SearchResponse;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfFilter;
import de.factfinder.webservice.ws71.FFsearch.ArrayOfGroup;
import de.factfinder.webservice.ws71.FFsearch.Filter;
import de.factfinder.webservice.ws71.FFsearch.Group;
import de.factfinder.webservice.ws71.FFsearch.GroupElement;
import de.factfinder.webservice.ws71.FFsearch.Result;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

/**
 * Populator for pagination information.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * @param <ITEM>
 */
public class ResponsePaginationPopulator<ITEM> implements Populator<SearchResponse, SearchPageData<ITEM>> {

    private static final String SERVICE_PLUS_FILTER = "catalogPlus";
    private static final String SERVICE_PLUS = "Service+";

    private Populator<SearchResponse, SearchPageData<ITEM>> productsPerPageOptionPopulator;

    @Override
    public void populate(final SearchResponse source, final SearchPageData<ITEM> target) {
        target.setPagination(buildPaginationData(source));
        productsPerPageOptionPopulator.populate(source, target);
    }

    protected FactFinderPaginationData buildPaginationData(final SearchResponse source) {
        final FactFinderPaginationData paginationData = createPaginationData();
        final Result result = source.getSearchResult();
        if (result.getPaging() == null) {
            return paginationData;
        }
        paginationData.setTotalNumberOfResults(result.getResultCount().longValue());
        paginationData.setTotalNumberOfCatalogPlusResults(getTotalNumberOfCatalogPlusResults(result.getSearchParams().getFilters(), result.getGroups()));
        paginationData.setCatalogPlusQueryString(getCatalogPlusQueryString(result.getGroups()));
        paginationData.setPageSize(result.getPaging().getResultsPerPage().intValue());
        paginationData.setCurrentPage(result.getPaging().getCurrentPage().intValue());
        paginationData.setNumberOfPages(result.getPaging().getPageCount().intValue());
        paginationData.setPageSizeStep(result.getPaging().getPagingConf().getDefaultResultsPerPage().intValue());
        return paginationData;
    }

    protected int getTotalNumberOfCatalogPlusResults(final ArrayOfFilter selectedFilters, final ArrayOfGroup filters) {
        boolean showResultCount = true;
        // check if the user has already filtered by the catalogPlus facet. If yes we don not display the additional total record count, as
        // we can use the normal one.
        if (selectedFilters != null && CollectionUtils.isNotEmpty(selectedFilters.getFilter())) {
            for (final Filter selectedFilter : selectedFilters.getFilter()) {
                if (SERVICE_PLUS_FILTER.equals(selectedFilter.getName())) {
                    showResultCount = false;
                    break;
                }
            }
        }

        if (showResultCount) {
            if (filters != null && CollectionUtils.isNotEmpty(filters.getGroup())) {
                for (final Group filter : filters.getGroup()) {
                    if (SERVICE_PLUS.equals(filter.getName()) && filter.getElements() != null
                            && CollectionUtils.isNotEmpty(filter.getElements().getGroupElement())) {
                        return filter.getElements().getGroupElement().get(0).getRecordCount().intValue();
                    }
                }
            }
        }
        return 0;
    }

    protected String getCatalogPlusQueryString(final ArrayOfGroup filters) {
        if (filters != null && CollectionUtils.isNotEmpty(filters.getGroup())) {
            for (final Group filter : filters.getGroup()) {
                if (SERVICE_PLUS.equals(filter.getName()) && filter.getElements() != null && CollectionUtils.isNotEmpty(filter.getElements().getGroupElement())) {
                    final GroupElement filterValue = filter.getElements().getGroupElement().get(0);
                    final StringBuffer buffer = new StringBuffer();
                    buffer.append("/search?q=");
                    buffer.append(filterValue.getName());
                    buffer.append("&filter_");
                    buffer.append(filterValue.getAssociatedFieldName());
                    buffer.append('=');
                    buffer.append(filterValue.getName());
                    return buffer.toString();
                }
            }
        }
        return null;
    }

    protected FactFinderPaginationData createPaginationData() {
        return new FactFinderPaginationData();
    }

    public Populator<SearchResponse, SearchPageData<ITEM>> getProductsPerPageOptionPopulator() {
        return productsPerPageOptionPopulator;
    }

    public void setProductsPerPageOptionPopulator(final Populator<SearchResponse, SearchPageData<ITEM>> productsPerPageOptionPopulator) {
        this.productsPerPageOptionPopulator = productsPerPageOptionPopulator;
    }
}
