package com.namics.hybris.ffsearch.data.paging;

import java.util.ArrayList;
import java.util.List;

import com.namics.distrelec.b2b.core.service.search.pagedata.DistPaginationData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;

public class FactFinderPaginationData<STATE> extends DistPaginationData {

    private String currentProductCode;

    private STATE nextQuery;

    private STATE prevQuery;

    private String firstUrl;

    private String lastUrl;

    private int pageSizeStep;

    private DistSearchType searchType;

    // Contains the category or manufacturer code
    private String code;

    private int totalNumberOfCatalogPlusResults;

    private String catalogPlusQueryString;

    private boolean filtersRemovedGeneralSearch;

    private List<ProductsPerPageOption> productsPerPageOptions = new ArrayList<>();

    public int getPageSizeStep() {
        return pageSizeStep;
    }

    public boolean isFiltersRemovedGeneralSearch() {
        return filtersRemovedGeneralSearch;
    }

    public void setPageSizeStep(final int pageSizeStep) {
        this.pageSizeStep = pageSizeStep;
    }

    public STATE getNextQuery() {
        return nextQuery;
    }

    public void setNextQuery(final STATE nextQuery) {
        this.nextQuery = nextQuery;
    }

    public STATE getPrevQuery() {
        return prevQuery;
    }

    public void setPrevQuery(final STATE prevQuery) {
        this.prevQuery = prevQuery;
    }

    public String getCurrentProductCode() {
        return currentProductCode;
    }

    public void setCurrentProductCode(final String currentProductCode) {
        this.currentProductCode = currentProductCode;
    }

    public DistSearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(final DistSearchType searchType) {
        this.searchType = searchType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public int getTotalNumberOfCatalogPlusResults() {
        return totalNumberOfCatalogPlusResults;
    }

    public void setTotalNumberOfCatalogPlusResults(final int totalNumberOfCatalogPlusResults) {
        this.totalNumberOfCatalogPlusResults = totalNumberOfCatalogPlusResults;
    }

    public String getCatalogPlusQueryString() {
        return catalogPlusQueryString;
    }

    public void setCatalogPlusQueryString(final String catalogPlusQueryString) {
        this.catalogPlusQueryString = catalogPlusQueryString;
    }

    @Override
    public String getFirstUrl() {
        return firstUrl;
    }

    @Override
    public void setFirstUrl(final String firstUrl) {
        this.firstUrl = firstUrl;
    }

    @Override
    public String getLastUrl() {
        return lastUrl;
    }

    @Override
    public void setLastUrl(final String lastUrl) {
        this.lastUrl = lastUrl;
    }

    public List<ProductsPerPageOption> getProductsPerPageOptions() {
        return productsPerPageOptions;
    }

    public void setProductsPerPageOptions(final List<ProductsPerPageOption> productsPerPageOptions) {
        this.productsPerPageOptions = productsPerPageOptions;
    }

    public void setFiltersRemovedGeneralSearch(boolean filtersRemovedGeneralSearch) {
        this.filtersRemovedGeneralSearch = filtersRemovedGeneralSearch;
    }
}
