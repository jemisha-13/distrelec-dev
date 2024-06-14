/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.service.exception;

/**
 * FactFinder specific exception.
 */
public class FactFinderException extends Exception {

    public FactFinderException(final String message) {
        super(message);
    }

    public FactFinderException(final Throwable cause) {
        super(cause);
    }

    public FactFinderException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
