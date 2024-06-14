/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.suggest;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class AutocompleteSuggestion {

    private String term;
    private AutocompleteResult res;

    // BEGIN GENERATED CODE

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public AutocompleteSuggestion() {
        this.res = new AutocompleteResult();
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(final String term) {
        this.term = term;
    }

    public AutocompleteResult getRes() {
        return res;
    }

    public void setRes(final AutocompleteResult res) {
        this.res = res;
    }

}
