/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception;

/**
 * Exception is thrown when no ID was found on an XML element.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class ElementIdNotFoundException extends RuntimeException {

    public ElementIdNotFoundException(final String message) {
        super(message);
    }

    public ElementIdNotFoundException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
