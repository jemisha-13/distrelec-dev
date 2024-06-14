/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.export.job.exception;

/**
 * Signals a error in an export job.
 * 
 * @author ceberle, Namics AG
 * @since Namics Distrelec 1.1
 */
public class DistFlexibleSearchExportJobException extends RuntimeException {

    public DistFlexibleSearchExportJobException(final String message) {
        super(message);
    }

    public DistFlexibleSearchExportJobException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
