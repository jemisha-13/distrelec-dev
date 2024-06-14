/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.customer.exceptions;

/**
 * {@code DuplicateEmailException}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.5
 */
public class DuplicateEmailException extends RuntimeException {

    /**
     * Create a new instance of {@code DuplicateEmailException}
     */
    public DuplicateEmailException() {
    }

    /**
     * Create a new instance of {@code DuplicateEmailException}
     * 
     * @param message
     */
    public DuplicateEmailException(final String message) {
        super(message);
    }

    /**
     * Create a new instance of {@code DuplicateEmailException}
     * 
     * @param cause
     */
    public DuplicateEmailException(final Throwable cause) {
        super(cause);
    }

    /**
     * Create a new instance of {@code DuplicateEmailException}
     * 
     * @param message
     * @param cause
     */
    public DuplicateEmailException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
