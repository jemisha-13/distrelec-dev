/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util;

import java.util.HashMap;

/**
 * HashMap<String, String> with key in lower cases.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class CaseInsensitiveMap extends HashMap<String, String> {

    @Override
    public String put(final String key, final String value) {
        return super.put(key.toLowerCase(), value);
    }

    @Override
    public String get(final Object key) {
        return super.get(((String) key).toLowerCase());
    }
}
