/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.facet;

import de.factfinder.webservice.ws71.FFsearch.FilterStyle;
import de.factfinder.webservice.ws71.FFsearch.Group;

/**
 * Possible types of a facet.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public enum FactFinderFacetType {

    // @formatter:off
    CHECKBOX("checkbox"),
    SLIDER("slider"),
    BOOLEAN_CHECKBOX("boolean_checkbox");
    // @formatter:on

    private String value;

    public String getValue() {
        return value;
    }

    public static FactFinderFacetType fromFacet(final Group facet) {
        if (FilterStyle.SLIDER.equals(facet.getFilterStyle())) {
            return FactFinderFacetType.SLIDER;
        } else if ("boolean".equals(facet.getUnit())) {
            // if unit is boolean return BOOLEAN_CHECKBOX type
            return FactFinderFacetType.BOOLEAN_CHECKBOX;
        } else {
            return FactFinderFacetType.CHECKBOX;
        }
    }

    private FactFinderFacetType(final String value) {
        this.value = value;
    }

}
