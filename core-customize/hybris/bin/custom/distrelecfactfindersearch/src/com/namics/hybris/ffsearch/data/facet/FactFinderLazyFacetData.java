/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.facet;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * POJO for facets which which can be additionally loaded.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 * 
 * @param <STATE>
 */
public class FactFinderLazyFacetData<STATE> {

    private String name;
    private String unit;
    private STATE query;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    // BEGIN GENERATED CODE

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(final String unit) {
        this.unit = unit;
    }

    public STATE getQuery() {
        return query;
    }

    public void setQuery(final STATE query) {
        this.query = query;
    }

}
