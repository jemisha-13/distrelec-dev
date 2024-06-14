/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.data;

import java.util.List;

/**
 * POJO to display a single search attribute within a product finder group.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DistProductFinderValueData {

    private String name;
    private Boolean checked;
    private Boolean disabled;
    private List<DistProductFinderFacetValueData> facetValues;
    private String minMaxKey;
    private String minValue;
    private String maxValue;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(final Boolean checked) {
        this.checked = checked;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(final Boolean disabled) {
        this.disabled = disabled;
    }

    public List<DistProductFinderFacetValueData> getFacetValues() {
        return facetValues;
    }

    public void setFacetValues(final List<DistProductFinderFacetValueData> facetValues) {
        this.facetValues = facetValues;
    }

    public String getMinMaxKey() {
        return minMaxKey;
    }

    public void setMinMaxKey(final String minMaxKey) {
        this.minMaxKey = minMaxKey;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(final String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(final String maxValue) {
        this.maxValue = maxValue;
    }

}
