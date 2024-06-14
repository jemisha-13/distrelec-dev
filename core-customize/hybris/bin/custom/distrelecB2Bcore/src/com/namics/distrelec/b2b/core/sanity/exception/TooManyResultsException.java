/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.sanity.exception;

/**
 * Signalizes too many results of a sanity check query.
 * 
 * @author pforster, Namics AG
 * @version 3.0.0
 */
public class TooManyResultsException extends RuntimeException {

    public TooManyResultsException(final String message) {
        super(message);
    }

}
