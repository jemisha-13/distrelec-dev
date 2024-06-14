/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.facet;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;

/**
 * POJO representing Facet Values.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 * @param <STATE>
 */
public class FactFinderFacetValueData<STATE> extends FacetValueData<STATE> {

    private double absoluteMaxValue;
    private double absoluteMinValue;
    private double selectedMaxValue;
    private double selectedMinValue;

    private String queryFilter;
    private String propertyName;
    private String propertyNameArguments;
    private String propertyNameArgumentSeparator;
    private String filterString;

    // BEGIN GENERATED CODE

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public double getAbsoluteMaxValue() {
        return absoluteMaxValue;
    }

    public void setAbsoluteMaxValue(final double absoluteMaxValue) {
        this.absoluteMaxValue = absoluteMaxValue;
    }

    public double getAbsoluteMinValue() {
        return absoluteMinValue;
    }

    public void setAbsoluteMinValue(final double absoluteMinValue) {
        this.absoluteMinValue = absoluteMinValue;
    }

    public double getSelectedMaxValue() {
        return selectedMaxValue;
    }

    public void setSelectedMaxValue(final double selectedMaxValue) {
        this.selectedMaxValue = selectedMaxValue;
    }

    public double getSelectedMinValue() {
        return selectedMinValue;
    }

    public void setSelectedMinValue(final double selectedMinValue) {
        this.selectedMinValue = selectedMinValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyNameArguments() {
        return propertyNameArguments;
    }

    public void setPropertyNameArguments(final String propertyNameArguments) {
        this.propertyNameArguments = propertyNameArguments;
    }

    public String getPropertyNameArgumentSeparator() {
        return propertyNameArgumentSeparator;
    }

    public void setPropertyNameArgumentSeparator(final String propertyNameArgumentSeparator) {
        this.propertyNameArgumentSeparator = propertyNameArgumentSeparator;
    }

    public String getFilterString() {
        return filterString;
    }

    public void setFilterString(final String filterString) {
        this.filterString = filterString;
    }

    public String getQueryFilter() {
        return queryFilter;
    }

    public void setQueryFilter(final String queryFilter) {
        this.queryFilter = queryFilter;
    }

}
