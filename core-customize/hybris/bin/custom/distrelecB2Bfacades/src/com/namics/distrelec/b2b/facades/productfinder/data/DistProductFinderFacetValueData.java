/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.data;

/**
 * POJO needed by product finder to create search filters.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DistProductFinderFacetValueData {

    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

}
