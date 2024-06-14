/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.exception;

/**
 * Exception for handling FactFinder export services.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class DistFactFinderExportServiceException extends Exception {

    /** {@inheritDoc} */
    public DistFactFinderExportServiceException() {
        super();
    }

    /** {@inheritDoc} */
    public DistFactFinderExportServiceException(final String message) {
        super(message);
    }

    /** {@inheritDoc} */
    public DistFactFinderExportServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /** {@inheritDoc} */
    public DistFactFinderExportServiceException(final Throwable cause) {
        super(cause);
    }

}
