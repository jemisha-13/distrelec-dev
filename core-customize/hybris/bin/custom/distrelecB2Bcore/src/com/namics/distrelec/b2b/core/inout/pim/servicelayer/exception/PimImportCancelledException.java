/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception;

/**
 * Exception to signal a PIM import abortion.
 * 
 * @author ceberle, Namics AG
 * @since Namics Extensions 1.0
 */
public class PimImportCancelledException extends RuntimeException {

    public PimImportCancelledException(final String message) {
        super(message);
    }

}
