/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.security.exceptions;

import org.springframework.security.authentication.AccountStatusException;

public class CompanyCountryException extends AccountStatusException {

    public CompanyCountryException(final String msg) {
        super(msg);
    }

}
