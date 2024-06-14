/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms.util;

import java.io.Serializable;

/**
 * {@code SelectOption}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.5
 */
public class SelectOption implements Serializable {

    private final String code;
    private final String name;

    /**
     * Create a new instance of {@code SelectOption}
     *
     * @param code
     * @param name
     */
    public SelectOption(final String code, final String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}
