/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.export.exception;

/**
 * Exception thrown when executing a FlexibleSearch query failed.
 * 
 * @author ceberle, Namics AG
 * @since Namics Distrelec 1.1
 */
public class DistFlexibleSearchExecutionException extends RuntimeException {

    public DistFlexibleSearchExecutionException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
