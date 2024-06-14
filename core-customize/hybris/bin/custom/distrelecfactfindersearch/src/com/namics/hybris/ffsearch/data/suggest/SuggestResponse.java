/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.suggest;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfResultSuggestion;

public class SuggestResponse {

    private SuggestRequest suggestRequest;
    private ArrayOfResultSuggestion suggestResults;

    // BEGIN GENERATED CODE

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public SuggestRequest getSuggestRequest() {
        return suggestRequest;
    }

    public void setSuggestRequest(final SuggestRequest suggestRequest) {
        this.suggestRequest = suggestRequest;
    }

    public ArrayOfResultSuggestion getSuggestResults() {
        return suggestResults;
    }

    public void setSuggestResults(final ArrayOfResultSuggestion suggestResults) {
        this.suggestResults = suggestResults;
    }

}
