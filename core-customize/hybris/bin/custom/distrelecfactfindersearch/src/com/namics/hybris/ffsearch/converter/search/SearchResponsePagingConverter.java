package com.namics.hybris.ffsearch.converter.search;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchResponse;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfSearchRecord;
import de.factfinder.webservice.ws71.FFsearch.PageLink;
import de.factfinder.webservice.ws71.FFsearch.Params;
import de.factfinder.webservice.ws71.FFsearch.Result;
import de.factfinder.webservice.ws71.FFsearch.SearchRecord;
import de.factfinder.webservice.ws71.FFsearch.String2StringMap.Entry;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

public class SearchResponsePagingConverter extends AbstractPopulatingConverter<SearchResponse, FactFinderPaginationData<SearchQueryData>> {

    private Populator<Params, SearchQueryData> responseQueryDataPopulator;

    private Populator<SearchResponse, SearchQueryData> responseQueryDataSortPopulator;

    @Override
    public void populate(final SearchResponse source, final FactFinderPaginationData<SearchQueryData> target) {

        // if it is a manufacturer search (manufacturer on search request is set) set it also on the pagination data and the next- and
        // prev-Links
        String code = "";
        if (source.getSearchRequest() != null) {
            code = source.getSearchRequest().getCode();
            target.setCode(source.getSearchRequest().getCode());
            target.setSearchType(source.getSearchRequest().getSearchType());
        }

        final PageLink nextLink = source.getSearchResult().getPaging() != null ? source.getSearchResult().getPaging().getNextLink() : null;
        if (nextLink != null) {
            final SearchQueryData nextQuery = createQueryData();
            getResponseQueryDataPopulator().populate(nextLink.getSearchParams(), nextQuery);
            getResponseQueryDataSortPopulator().populate(source, nextQuery);

            nextQuery.setSearchType(source.getSearchRequest().getSearchType());
            nextQuery.setCode(code);

            target.setNextQuery(nextQuery);
            target.setNextPageNr(nextLink.getNumber());
            target.setSort(nextQuery.getSort());
        }

        if (source.getSearchResult().getPaging() != null) {
            // prevLink
            final PageLink prevLink = source.getSearchResult().getPaging().getPreviousLink();
            if (prevLink != null) {
                final SearchQueryData prevQuery = createQueryData();
                getResponseQueryDataPopulator().populate(prevLink.getSearchParams(), prevQuery);
                getResponseQueryDataSortPopulator().populate(source, prevQuery);

                prevQuery.setSearchType(source.getSearchRequest().getSearchType());
                prevQuery.setCode(code);

                target.setPrevQuery(prevQuery);
                target.setPrevPageNr(prevLink.getNumber());
                target.setSort(prevQuery.getSort());
            }
            if (source.getSearchResult().getPaging().getCurrentPage() != null) {
                target.setCurrentPage(source.getSearchResult().getPaging().getCurrentPage());
            }
        }

        target.setCurrentProductCode(getProductCode(source.getSearchResult()));
        target.setFiltersRemovedGeneralSearch(source.isFiltersRemovedGeneralSearch());
    }

    private String getProductCode(final Result searchResult) {
        final ArrayOfSearchRecord results = searchResult.getRecords();
        if (results != null && CollectionUtils.isNotEmpty(results.getSearchRecord())) {
            final SearchRecord searchRecord = results.getSearchRecord().get(0);
            return getValue(DistFactFinderExportColumns.PRODUCT_NUMBER.getValue(), searchRecord);
        }
        return StringUtils.EMPTY;
    }

    private String getValue(final String key, final SearchRecord searchRecord) {
        for (final Entry entry : searchRecord.getRecord().getEntry()) {
            if (StringUtils.equalsIgnoreCase(entry.getKey(), key)) {
                return entry.getValue();
            }
        }
        return StringUtils.EMPTY;
    }

    @Override
    protected FactFinderPaginationData createTarget() {
        return new FactFinderPaginationData();
    }

    protected SearchQueryData createQueryData() {
        return new SearchQueryData();
    }

    protected Populator<Params, SearchQueryData> getResponseQueryDataPopulator() {
        return responseQueryDataPopulator;
    }

    @Required
    public void setResponseQueryDataPopulator(final Populator<Params, SearchQueryData> responseQueryDataPopulator) {
        this.responseQueryDataPopulator = responseQueryDataPopulator;
    }

    protected Populator<SearchResponse, SearchQueryData> getResponseQueryDataSortPopulator() {
        return responseQueryDataSortPopulator;
    }

    @Required
    public void setResponseQueryDataSortPopulator(final Populator<SearchResponse, SearchQueryData> responseQueryDataSortPopulator) {
        this.responseQueryDataSortPopulator = responseQueryDataSortPopulator;
    }
}
