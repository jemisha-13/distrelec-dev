/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.eprocurement.cxml.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.storefront.eprocurement.security.exceptions.EProcurementCustomerException;
import com.namics.distrelec.b2b.storefront.security.AcceleratorAuthenticationProvider;

import de.hybris.platform.b2b.model.B2BCustomerModel;

public class CxmlAuthenticationProvider extends AcceleratorAuthenticationProvider {

    @Override
    protected void additionalAuthenticationChecks(final UserDetails details, final AbstractAuthenticationToken authentication) {

        try {
            super.additionalAuthenticationChecks(details, authentication);
        } catch (final EProcurementCustomerException e) {
            final B2BCustomerModel b2bCustomerModel = getUserService().getUserForUID(details.getUsername(), B2BCustomerModel.class);
            if (getUserService().isMemberOfGroup(b2bCustomerModel, getUserService().getUserGroupForUID(DistConstants.User.CXMLCUSTOMERGROUP_UID))) {
                // customer is CXML customer -> therefore OK
                return;
            }
        }

        // if catch above fails it is not a e-procurement customer and should not be logged in
        throw new EProcurementCustomerException(
                ErrorLogCode.INVALID_USER_GROUP_ERROR.getCode() + ErrorSource.CXML + " Login attempt as " + details.getUsername() + " is rejected");
    }
}
