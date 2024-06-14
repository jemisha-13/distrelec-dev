/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.exception;

/**
 * Exception for handling FactFinder upload services.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class DistFactFinderExportUploadException extends Exception {

    /** {@inheritDoc} */
    public DistFactFinderExportUploadException() {
        super();
    }

    /** {@inheritDoc} */
    public DistFactFinderExportUploadException(final String message) {
        super(message);
    }

    /** {@inheritDoc} */
    public DistFactFinderExportUploadException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /** {@inheritDoc} */
    public DistFactFinderExportUploadException(final Throwable cause) {
        super(cause);
    }

}
