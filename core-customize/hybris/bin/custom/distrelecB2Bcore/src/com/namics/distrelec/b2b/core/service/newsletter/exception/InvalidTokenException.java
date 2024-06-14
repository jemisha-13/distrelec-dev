/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.newsletter.exception;

/**
 * Exception for a token validation failure.
 * 
 * @author pnueesch, Namics AG
 */
public class InvalidTokenException extends RuntimeException {

    /**
     * Constructor with the specified detail message.
     * 
     * @param message
     *            of the exception
     */
    public InvalidTokenException(final String message) {
        super(message);
    }

}
