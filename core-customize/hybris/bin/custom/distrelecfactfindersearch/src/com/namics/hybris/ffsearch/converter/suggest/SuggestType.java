/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.converter.suggest;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Supported types of suggestions.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public enum SuggestType {

    PRODUCT("productName"), CATEGORY("category"), MANUFACTURER("brand"), TERM("searchTerm"), DIRECT_ORDER("DirectOrder");

    private final String value;

    private static final Map<String, SuggestType> LOOKUP = Maps.newHashMap();

    static {
        for (final SuggestType d : SuggestType.values()) {
            LOOKUP.put(d.getStringValue(), d);
        }
    }

    public static SuggestType get(final String value) {
        return LOOKUP.get(value);
    }

    private SuggestType(final String value) {
        this.value = value;
    }

    public String getStringValue() {
        return this.value;
    }
}
