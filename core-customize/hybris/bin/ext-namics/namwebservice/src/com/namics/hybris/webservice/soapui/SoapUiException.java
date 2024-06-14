/*
 * Copyright 2000-2011 Namics AG. All rights reserved.
 */

package com.namics.hybris.webservice.soapui;

/**
 * Exception of Soap UI Server
 * 
 * @author jweiss, Namics AG
 * 
 */
public class SoapUiException extends RuntimeException {

    public SoapUiException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SoapUiException(final String message) {
        super(message);
    }

}
