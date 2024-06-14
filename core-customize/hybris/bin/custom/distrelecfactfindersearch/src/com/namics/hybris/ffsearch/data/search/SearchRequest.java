/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.search;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.factfinder.webservice.ws71.FFsearch.Params;
import de.factfinder.webservice.ws71.FFsearch.SearchControlParams;

/**
 * POJO for a search request.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0, refactore Distrelec 2.0.22
 * 
 */
public class SearchRequest {

    private DistSearchType searchType;

    /**
     * additional code for some search types (e.g. manufacturer code, category code, ..)
     */
    private String code;

    private Params searchParams;

    private SearchControlParams controlParams;

    private String sessionId;

    // BEGIN GENERATED CODE

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
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

    public Params getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(final Params searchParams) {
        this.searchParams = searchParams;
    }

    public SearchControlParams getControlParams() {
        return controlParams;
    }

    public void setControlParams(final SearchControlParams controlParams) {
        this.controlParams = controlParams;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
