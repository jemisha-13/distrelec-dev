/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.suggest;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * {@code TermSuggestion}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
@SuppressWarnings("PMD")
public class TermSuggestion extends AbstractSuggestion {

    private int count;
    private String query;

    /**
     * Create a new instance of {@code TermSuggestion}
     */
    public TermSuggestion() {
        super();
    }

    // BEGIN GENERATED CODE

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(final String query) {
        this.query = query;
    }
}
