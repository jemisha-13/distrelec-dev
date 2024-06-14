/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.eprocurement.security.exceptions;

import org.springframework.security.authentication.AccountStatusException;

/**
 * Exception for e-procurement customer login.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class EProcurementCustomerException extends AccountStatusException {

    public EProcurementCustomerException(final String msg) {
        super(msg);
    }

}
