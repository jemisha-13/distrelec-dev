/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor.exceptions;

import de.hybris.platform.servicelayer.interceptor.Interceptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

/**
 * Customized Exception to override getMessage() methode.
 * 
 * @author pnueesch, Namics AG
 * 
 */
public class DistValidationInterceptorException extends InterceptorException {

    private String message;

    public DistValidationInterceptorException(final String message) {
        this(message, null, null);
        this.message = message;
    }

    public DistValidationInterceptorException(final String message, final Throwable cause) {
        this(message, cause, null);
        this.message = message;
    }

    public DistValidationInterceptorException(final String message, final Interceptor inter) {
        this(message, null, inter);
        this.message = message;
    }

    public DistValidationInterceptorException(final String message, final Throwable cause, final Interceptor inter) {
        super(message, cause);
        this.setInterceptor(inter);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
