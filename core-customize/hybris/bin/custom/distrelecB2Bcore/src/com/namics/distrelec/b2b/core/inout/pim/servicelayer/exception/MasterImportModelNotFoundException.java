/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception;

/**
 * Signals an exception when a model was not found in the language import but should exist due to the master import.
 * 
 * @author csieber, Namics AG
 * @since Distrelec 1.0
 */
public class MasterImportModelNotFoundException extends RuntimeException {

    public MasterImportModelNotFoundException(final String message) {
        super(message);
    }

    public MasterImportModelNotFoundException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
