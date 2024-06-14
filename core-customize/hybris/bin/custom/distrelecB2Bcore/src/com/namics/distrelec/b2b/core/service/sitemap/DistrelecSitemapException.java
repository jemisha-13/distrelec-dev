/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.sitemap;

/**
 * Exception for handling Sitemap generation problems.
 * 
 * @author csieber, Namics AG
 * @since Namics Extensions 1.0
 */
public class DistrelecSitemapException extends Exception {
    /**
     * Constructor with the specified detail message.
     * 
     * @param message
     *            of the Exception
     */
    public DistrelecSitemapException(final String message) {
        super(message);
    }

    /**
     * Constructor with the specified detail message and exception.
     * 
     * @param message
     *            of the Exception
     * @param cause
     *            of the Exception
     */
    public DistrelecSitemapException(final String message, final Exception cause) {
        super(message, cause);
    }
}
