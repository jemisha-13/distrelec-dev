/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.facet;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;

/**
 * POJO representing a badge item, used to display a selected FactFinder facet search filter.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class FilterBadgeData<STATE> extends BreadcrumbData<STATE> {

    private FactFinderFacetType type;
    private String facetValuePropertyName;
    private String facetValuePropertyNameArguments;
    private String facetValuePropertyNameArgumentSeparator;
    private String filterString;

    private boolean categoryFilter;

    public FactFinderFacetType getType() {
        return type;
    }

    public void setType(final FactFinderFacetType type) {
        this.type = type;
    }

    public String getFacetValuePropertyName() {
        return facetValuePropertyName;
    }

    public void setFacetValuePropertyName(final String facetValuePropertyName) {
        this.facetValuePropertyName = facetValuePropertyName;
    }

    public String getFacetValuePropertyNameArguments() {
        return facetValuePropertyNameArguments;
    }

    public void setFacetValuePropertyNameArguments(final String facetValuePropertyNameArguments) {
        this.facetValuePropertyNameArguments = facetValuePropertyNameArguments;
    }

    public String getFacetValuePropertyNameArgumentSeparator() {
        return facetValuePropertyNameArgumentSeparator;
    }

    public void setFacetValuePropertyNameArgumentSeparator(final String facetValuePropertyNameArgumentSeparator) {
        this.facetValuePropertyNameArgumentSeparator = facetValuePropertyNameArgumentSeparator;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public boolean isCategoryFilter() {
        return categoryFilter;
    }

    public void setCategoryFilter(final boolean categoryFilter) {
        this.categoryFilter = categoryFilter;
    }

    public String getFilterString() {
        return filterString;
    }

    public void setFilterString(final String filterString) {
        this.filterString = filterString;
    }

}
