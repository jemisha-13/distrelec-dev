/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.request;

import java.util.List;

/**
 * Data Object for aggregated filters.
 * 
 * @author rlehmann, Namics AG
 * @since Distrelec 2.0.22
 * 
 */
public class AggregatedFilterData {
    private String name;
    private List<String> values;
    private Boolean substring;
    private Boolean exclude;

    // BEGIN GENERATED CODE

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(final List<String> values) {
        this.values = values;
    }

    public Boolean getSubstring() {
        return substring;
    }

    public void setSubstring(final Boolean substring) {
        this.substring = substring;
    }

    public Boolean getExclude() {
        return exclude;
    }

    public void setExclude(final Boolean exclude) {
        this.exclude = exclude;
    }

    // END GENERATED CODE

}
