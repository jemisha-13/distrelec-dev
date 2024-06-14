package com.namics.hybris.ffsearch.data.facet;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;
import com.namics.hybris.ffsearch.data.search.DistSearchType;

import de.factfinder.webservice.ws71.FFsearch.SearchResultArticleNumberStatus;
import de.factfinder.webservice.ws71.FFsearch.SearchResultStatus;
import de.hybris.platform.commercefacades.search.data.SearchStateData;

public class FactFinderProductSearchPageData<STATE, RESULT> extends FactFinderFacetSearchPageData<STATE, RESULT> {

    private String code;

    private DistSearchType searchType;

    private String freeTextSearch;

    private String keywordRedirectUrl;

    private SearchResultStatus resultStatus;

    private SearchResultArticleNumberStatus resultArticleNumberStatus;

    private Map<String, String> sortableAttributeMap = new HashMap<String, String>();

    private List<String> attributeHeaders;

    private boolean technicalView;

    private String currencyIso;

    private boolean timedOut;

    private boolean punchedOut;

    private Collection<CategoryDisplayData<SearchStateData>> categoryDisplayData;

    private boolean productListLevel;

    private boolean notFound;

    private List<Breadcrumb> breadcrumbs;

    private String sessionId;

    private List<RESULT> punchedOutProducts;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public DistSearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(final DistSearchType searchType) {
        this.searchType = searchType;
    }

    public String getFreeTextSearch() {
        return freeTextSearch;
    }

    public void setFreeTextSearch(final String freeTextSearch) {
        this.freeTextSearch = freeTextSearch;
    }

    public String getKeywordRedirectUrl() {
        return keywordRedirectUrl;
    }

    public void setKeywordRedirectUrl(final String keywordRedirectUrl) {
        this.keywordRedirectUrl = keywordRedirectUrl;
    }

    public SearchResultStatus getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(final SearchResultStatus resultStatus) {
        this.resultStatus = resultStatus;
    }

    public Map<String, String> getSortableAttributeMap() {
        return sortableAttributeMap;
    }

    public void setSortableAttributeMap(final Map<String, String> sortableAttributeMap) {
        this.sortableAttributeMap = sortableAttributeMap;
    }

    public List<String> getAttributeHeaders() {
        return attributeHeaders;
    }

    public void setAttributeHeaders(final List<String> attributeHeaders) {
        this.attributeHeaders = attributeHeaders;
    }

    public boolean isTechnicalView() {
        return technicalView;
    }

    public void setTechnicalView(final boolean technicalView) {
        this.technicalView = technicalView;
    }

    public String getCurrencyIso() {
        return currencyIso;
    }

    public void setCurrencyIso(final String currencyIso) {
        this.currencyIso = currencyIso;
    }

    public boolean isTimedOut() {
        return timedOut;
    }

    public void setTimedOut(final boolean timedOut) {
        this.timedOut = timedOut;
    }

    public SearchResultArticleNumberStatus getResultArticleNumberStatus() {
        return resultArticleNumberStatus;
    }

    public void setResultArticleNumberStatus(final SearchResultArticleNumberStatus resultArticleNumberStatus) {
        this.resultArticleNumberStatus = resultArticleNumberStatus;
    }

    public boolean isPunchedOut() {
        return punchedOut;
    }

    public void setPunchedOut(boolean punchedOut) {
        this.punchedOut = punchedOut;
    }

    public boolean isNotFound() {
        return notFound;
    }

    public void setNotFound(boolean notFound) {
        this.notFound = notFound;
    }

    public Collection<CategoryDisplayData<SearchStateData>> getCategoryDisplayData() {
        return categoryDisplayData;
    }

    public void setCategoryDisplayData(Collection<CategoryDisplayData<SearchStateData>> categoryDisplayData) {
        this.categoryDisplayData = categoryDisplayData;
    }

    public boolean isProductListLevel() {
        return productListLevel;
    }

    public void setProductListLevel(boolean productListLevel) {
        this.productListLevel = productListLevel;
    }

    public List<Breadcrumb> getBreadcrumbs() {
        return breadcrumbs;
    }

    public void setBreadcrumbs(List<Breadcrumb> breadcrumbs) {
        this.breadcrumbs = breadcrumbs;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<RESULT> getPunchedOutProducts() {
        return punchedOutProducts;
    }

    public void setPunchedOutProducts(List<RESULT> punchedOutProducts) {
        this.punchedOutProducts = punchedOutProducts;
    }
}
