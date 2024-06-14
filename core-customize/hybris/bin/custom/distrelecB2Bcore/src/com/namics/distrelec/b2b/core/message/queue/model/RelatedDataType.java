/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.message.queue.model;

import java.util.stream.Stream;

/**
 * {@code RelatedDataType}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.1
 */
public enum RelatedDataType {

    /**
     * Related categories type
     */
    RELATED_CATEGORY("RELATED_CATEGORY"),
    /**
     * Related manufacturers type
     */
    RELATED_MANUFACTURER("RELATED_MANUFACTURER"),
    /**
     * Related products type
     */
    RELATED_PRODUCT("RELATED_PRODUCT"),
    /**
     * New arrival products
     */
    NEW_ARRIVAL_PRODUCT("NEW_ARRIVAL_PRODUCT"),
    /**
     * Top seller related products type
     */
    TOP_SELLER_PRODUCT("TOP_SELLER_PRODUCT");

    private String code;

    /**
     * Create a new instance of {@code RelatedDataType}
     * 
     * @param code
     */
    private RelatedDataType(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static RelatedDataType findByCode(String code) {
        return Stream.of(values())
                     .filter(e -> e.getCode().equals(code))
                     .findFirst()
                     .orElse(null);
    }
}
