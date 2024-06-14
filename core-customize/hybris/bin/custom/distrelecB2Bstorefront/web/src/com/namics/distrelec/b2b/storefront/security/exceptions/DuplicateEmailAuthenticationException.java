/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.security.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * {@code DuplicateEmailAuthenticationException}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.5
 */
public class DuplicateEmailAuthenticationException extends AuthenticationException {

    /**
     * Create a new instance of {@code DuplicateEmailAuthenticationException}
     *
     * @param message
     */
    public DuplicateEmailAuthenticationException(final String message) {
        super(message);
    }

    /**
     * Create a new instance of {@code DuplicateEmailAuthenticationException}
     *
     * @param message
     * @param cause
     */
    public DuplicateEmailAuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
