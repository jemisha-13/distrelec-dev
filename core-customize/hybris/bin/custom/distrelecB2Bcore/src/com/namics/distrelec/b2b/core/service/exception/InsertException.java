/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.exception;

/**
 * Exception for insertion failures.
 * 
 * @author daehusir, Distrelec
 */
public class InsertException extends RuntimeException {

    /**
     * Constructor with the specified detail message and exception.
     * 
     * @param message
     *            of the exception
     * @param cause
     *            of the exception
     */
    public InsertException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
