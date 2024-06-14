/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.sitemap;

public enum EntityNames {

    CATEGORY("cat"), PRODUCT("product"), MANUFACTURER("manufacturer"), PRODUCT_FAMILY("product_family");

    private final String name;

    private EntityNames(final String name) {
        this.name = name;
    }

    public boolean equalsName(final String otherName) {
        return name.equals(otherName);
    }

    @Override
    public String toString() {
        return name;
    }
}
