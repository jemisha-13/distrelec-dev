/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.message.queue.model;

import java.util.stream.Stream;

/**
 * {@code RowType}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.1
 */
public enum RowType {

    /**
     * 
     */
    PRODUCT("product"),
    /**
     * 
     */
    CATEGORY("category"),
    /**
     * 
     */
    MANUFACTURER("manufacturer");

    private String code;

    /**
     * Create a new instance of {@code RowType}
     * 
     * @param code
     */
    private RowType(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static RowType findByCode(String code) {
        return Stream.of(values())
                     .filter(e -> e.getCode().equals(code))
                     .findFirst()
                     .orElse(null);
    }
}
