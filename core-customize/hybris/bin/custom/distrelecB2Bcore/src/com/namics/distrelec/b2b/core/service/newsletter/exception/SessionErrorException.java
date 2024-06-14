/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.newsletter.exception;

/**
 * Exception for an error occurs during session handling.
 * 
 * @author pnueesch, Namics AG
 */
public class SessionErrorException extends RuntimeException {

    /**
     * Constructor with the specified detail message.
     * 
     * @param message
     *            of the exception
     */
    public SessionErrorException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
