/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.security.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * {@code AuthenticationCaptchaException}
 *
 */
public class AuthenticationCaptchaException extends AuthenticationException {

    /**
     * Create a new instance of {@code AuthenticationCaptchaException}
     *
     * @param msg
     */
    public AuthenticationCaptchaException(final String msg) {
        super(msg);
    }

    /**
     * Create a new instance of {@code AuthenticationCaptchaException}
     *
     * @since 4.5
     */
    public AuthenticationCaptchaException(final String msg, final Throwable th) {
        super(msg, th);
    }

}
