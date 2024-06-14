/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.export.columns;

public enum DistFactFinderExportPromotionColumnAttributes {

    // @formatter:off
    HOTOFFER("hotOffer"),
    NO_MOVER("noMover"),
    TOP("top"),
    HIT("hit"),
    USED("used"),
    OFFER("offer"),
    BESTSELLER("bestseller"),
    NEW("new"),
    CALIBRATIONSERVICE("calibrationService");
    // @formatter:on

    private String value;

    private DistFactFinderExportPromotionColumnAttributes(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
