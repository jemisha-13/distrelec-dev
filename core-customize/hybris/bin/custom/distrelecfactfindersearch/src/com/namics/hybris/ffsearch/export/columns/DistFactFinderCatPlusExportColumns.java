/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.export.columns;

/**
 * FactFinder Catalog+ export columns with their names. Centralizes referencing the export coulmns.
 * 
 * @author ceberle, Namics AG
 * @since Namics Distrelec 1.1
 */
public enum DistFactFinderCatPlusExportColumns {

    // @formatter:off
    MANUFACTURER_AID("ManufacturerAid");
    // @formatter:on

    private String value;

    private DistFactFinderCatPlusExportColumns(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
