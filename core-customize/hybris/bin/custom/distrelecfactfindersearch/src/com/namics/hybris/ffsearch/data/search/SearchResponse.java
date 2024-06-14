package com.namics.hybris.ffsearch.data.search;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.factfinder.webservice.ws71.FFsearch.Result;

public class SearchResponse {

    private SearchRequest searchRequest;

    private Result searchResult;

    private boolean technicalView;

    private boolean timedOut;

    private boolean filtersRemovedGeneralSearch;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public SearchRequest getSearchRequest() {
        return searchRequest;
    }

    public boolean isFiltersRemovedGeneralSearch() {
        return filtersRemovedGeneralSearch;
    }

    public void setSearchRequest(final SearchRequest searchRequest) {
        this.searchRequest = searchRequest;
    }

    public Result getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(final Result searchResult) {
        this.searchResult = searchResult;
    }

    public boolean isTechnicalView() {
        return technicalView;
    }

    public void setTechnicalView(final boolean technicalView) {
        this.technicalView = technicalView;
    }

    public boolean isTimedOut() {
        return timedOut;
    }

    public void setTimedOut(boolean timedOut) {
        this.timedOut = timedOut;
    }

    public void setFiltersRemovedGeneralSearch(boolean filtersRemovedGeneralSearch) {
        this.filtersRemovedGeneralSearch = filtersRemovedGeneralSearch;
    }
}
