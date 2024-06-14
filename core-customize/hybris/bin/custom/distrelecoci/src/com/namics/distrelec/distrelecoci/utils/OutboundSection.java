/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.distrelecoci.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.namics.distrelec.distrelecoci.exception.OciException;

/**
 * OutboundSection
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class OutboundSection implements Serializable {
    private final Map<String, String> fields;
    private final String hookUrlFieldname;

    public OutboundSection(final Map<String, String> map, final String hookurlFieldName) throws OciException {
        this.fields = new HashMap<>();

        for (final Map.Entry<String, String> mapEntry : map.entrySet()) {
            if (mapEntry.getKey() != null) {
                this.fields.put(mapEntry.getKey().toUpperCase(Locale.getDefault()), mapEntry.getValue());
            }
        }

        if (StringUtils.isEmpty(hookurlFieldName)) {
            throw new OciException("Parameter for hook_url field name is null or empty", 3);
        }

        this.hookUrlFieldname = hookurlFieldName.toUpperCase(Locale.getDefault());
    }

    public String getField(final String name) {
        if (name != null) {
            return this.fields.get(name.toUpperCase(Locale.getDefault()));
        }
        return null;
    }

    public String getHookURLFieldName() {
        return this.hookUrlFieldname;
    }

    /**
     * Fields
     * 
     * @author dathusir, Distrelec
     * @since Distrelec 1.0
     * 
     */
    public static class Fields {
        public static final String OK_CODE = "~OkCode";
        public static final String TARGET = "~Target";
        public static final String CALLER = "~Caller";

    }
}
