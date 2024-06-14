/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.export.exception;

/**
 * Exception thrown when transformation of FlexibleSearch query failed.
 * 
 * @author ceberle, Namics AG
 * @since Namics Distrelec 1.1
 */
public class DistCsvTransformationException extends RuntimeException {

    public DistCsvTransformationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
