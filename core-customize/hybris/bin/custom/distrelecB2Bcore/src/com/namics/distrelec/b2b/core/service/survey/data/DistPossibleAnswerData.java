/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.survey.data;

/**
 * {@code DistPossibleAnswerData}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DistPossibleAnswerData {

    private String code;
    private String name;

    /**
     * Create a new instance of {@code DistPossibleAnswerData}
     */
    public DistPossibleAnswerData() {
        super();
    }

    /**
     * Create a new instance of {@code DistPossibleAnswerData}
     */
    public DistPossibleAnswerData(final String code, final String name) {
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
     * @param code
     *            the code to set
     */
    public void setCode(final String code) {
        this.code = code;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }
}
