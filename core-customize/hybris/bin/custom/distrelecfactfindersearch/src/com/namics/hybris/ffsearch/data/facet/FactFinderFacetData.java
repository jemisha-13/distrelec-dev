/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.facet;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.hybris.platform.commerceservices.search.facetdata.FacetData;

/**
 * POJO representing a FactFinder specific facet.
 * 
 * We're not extending from {@link FacetData} here, since we need to override some fields e.g. the values list of type
 * {@link FactFinderFacetValueData}.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 * @param <STATE>
 */
public class FactFinderFacetData<STATE> {

    private String code;
    private String name;
    private FactFinderFacetType type;
    private List<FactFinderFacetValueData<STATE>> values;
    private Boolean hasSelectedElements;
    private Boolean hasMinMaxFilters;
    private String unit;
    private Boolean isViable;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    // BEGIN GENERATED CODE

    public String getCode() {
        return code;
    }

    public Boolean getHasMinMaxFilters() {
        return hasMinMaxFilters;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public FactFinderFacetType getType() {
        return type;
    }

    public void setType(final FactFinderFacetType type) {
        this.type = type;
    }

    public List<FactFinderFacetValueData<STATE>> getValues() {
        return values;
    }

    public void setValues(final List<FactFinderFacetValueData<STATE>> values) {
        this.values = values;
    }

    public Boolean getHasSelectedElements() {
        return hasSelectedElements;
    }

    public void setHasSelectedElements(final Boolean expanded) {
        this.hasSelectedElements = expanded;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getIsViable() {
        return isViable;
    }

    public void setIsViable(Boolean isViable) {
        this.isViable = isViable;
    }

    public void setHasMinMaxFilters(Boolean hasMinMaxFilters) {
        this.hasMinMaxFilters = hasMinMaxFilters;
    }
}
