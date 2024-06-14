/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.security.exceptions;

import de.hybris.platform.servicelayer.exceptions.SystemException;


/**
 * {@code PunchoutException}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class PunchoutException extends SystemException {

    /**
     * Create a new instance of {@code PunchoutException}
     * @param message
     */
    public PunchoutException(String message) {
        super(message);
    }

    /**
     * Create a new instance of {@code PunchoutException}
     * @param cause
     */
    public PunchoutException(Throwable cause) {
        super(cause);
    }

    /**
     * Create a new instance of {@code PunchoutException}
     * @param message
     * @param cause
     */
    public PunchoutException(String message, Throwable cause) {
        super(message, cause);
    }

}
