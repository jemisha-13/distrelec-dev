/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.search;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * POJO with structured search parameters.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class SearchQueryData extends de.hybris.platform.commercefacades.search.data.SearchQueryData {

    private String code;
    private DistSearchType searchType;

    private List<SearchQueryTermData> filterTerms;
    private String sort;
    private String log;
    private AdvisorStatusData advisorStatus;
    private boolean tracking;
    private String sessionId;
    private boolean technicalView;

    private AdditionalSearchParams additionalSearchParams = new AdditionalSearchParams();
    private AdditionalControlParams additionalControlParams = new AdditionalControlParams();

    // BEGIN GENERATED CODE

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public String getFreeTextSearch() {
        return getValue();
    }

    public void setFreeTextSearch(final String freeTextSearch) {
        setValue(freeTextSearch);
    }

    public List<SearchQueryTermData> getFilterTerms() {
        return filterTerms;
    }

    public void setFilterTerms(final List<SearchQueryTermData> filterTerms) {
        this.filterTerms = filterTerms;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(final String sort) {
        this.sort = sort;
    }

    public String getLog() {
        return log;
    }

    public void setLog(final String log) {
        this.log = log;
    }

    public AdvisorStatusData getAdvisorStatus() {
        return advisorStatus;
    }

    public void setAdvisorStatus(final AdvisorStatusData advisorStatus) {
        this.advisorStatus = advisorStatus;
    }

    public AdditionalSearchParams getAdditionalSearchParams() {
        return additionalSearchParams;
    }

    public void setAdditionalSearchParams(final AdditionalSearchParams additionalSearchParams) {
        this.additionalSearchParams = additionalSearchParams;
    }

    public AdditionalControlParams getAdditionalControlParams() {
        return additionalControlParams;
    }

    public void setAdditionalControlParams(final AdditionalControlParams additionalControlParams) {
        this.additionalControlParams = additionalControlParams;
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

    public boolean isTracking() {
        return tracking;
    }

    public void setTracking(boolean tracking) {
        this.tracking = tracking;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isTechnicalView() {
        return technicalView;
    }

    public void setTechnicalView(boolean technicalView) {
        this.technicalView = technicalView;
    }

    // END GENERATED CODE

}
