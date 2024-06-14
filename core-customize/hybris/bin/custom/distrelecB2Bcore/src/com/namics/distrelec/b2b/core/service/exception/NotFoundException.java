/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.exception;

/**
 * Exception for item not found failure.
 * 
 * @author daehusir, Distrelec
 */
public class NotFoundException extends RuntimeException {

    /**
     * Constructor with the specified detail message and exception.
     * 
     * @param message
     *            of the exception
     * @param cause
     *            of the exception
     */
    public NotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with the specified detail message.
     * 
     * @param message
     *            of the exception
     */
    public NotFoundException(final String message) {
        super(message);
    }

}
