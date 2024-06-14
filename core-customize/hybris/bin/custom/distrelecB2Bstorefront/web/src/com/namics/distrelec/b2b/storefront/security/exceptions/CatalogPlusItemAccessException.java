/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.security.exceptions;

import de.hybris.platform.servicelayer.exceptions.SystemException;

/**
 * {@code CatalogPlusItemAccessException}
 * 
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class CatalogPlusItemAccessException extends SystemException {

    /**
     * Create a new instance of {@code CatalogPlusItemAccessException}
     * 
     * @param message
     */
    public CatalogPlusItemAccessException(final String message) {
        super(message);
    }

    /**
     * Create a new instance of {@code CatalogPlusItemAccessException}
     * 
     * @param cause
     */
    public CatalogPlusItemAccessException(final Throwable cause) {
        super(cause);
    }

    /**
     * Create a new instance of {@code CatalogPlusItemAccessException}
     * 
     * @param message
     * @param cause
     */
    public CatalogPlusItemAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
