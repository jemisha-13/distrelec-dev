/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.service.cxml;


public class CxmlException extends Exception {

    public CxmlException(String message) {
        super(message);
    }

    public CxmlException(Exception e) {
        super(e);
    }

}
