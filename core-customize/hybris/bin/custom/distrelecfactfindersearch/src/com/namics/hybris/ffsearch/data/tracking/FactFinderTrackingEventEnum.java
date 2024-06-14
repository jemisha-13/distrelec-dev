/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.tracking;

public enum FactFinderTrackingEventEnum {

    // @formatter:off
    CLICK("click"),
    CART("cart"),
    CHECKOUT("checkout"),
    LOGIN("login"),
    RECOMMENDATION_CLICK("recommendationClick"),
    FEEDBACK("feedback");
    // @formatter:on

    private String value;

    private FactFinderTrackingEventEnum(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }

}
