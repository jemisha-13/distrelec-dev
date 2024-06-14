/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception;

/**
 * Signals an exception when XML content is converted.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class ElementConverterException extends RuntimeException {

    public ElementConverterException(final String message) {
        super(message);
    }

    public ElementConverterException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
