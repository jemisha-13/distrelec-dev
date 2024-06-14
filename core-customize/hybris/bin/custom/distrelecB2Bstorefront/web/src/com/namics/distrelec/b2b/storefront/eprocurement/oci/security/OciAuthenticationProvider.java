/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.eprocurement.oci.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.storefront.eprocurement.security.exceptions.EProcurementCustomerException;
import com.namics.distrelec.b2b.storefront.security.AcceleratorAuthenticationProvider;

import de.hybris.platform.b2b.model.B2BCustomerModel;

/**
 * Additional authentication checks for e-procurement customers.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class OciAuthenticationProvider extends AcceleratorAuthenticationProvider {

    @Override
    protected void additionalAuthenticationChecks(final UserDetails details, final AbstractAuthenticationToken authentication) {

        try {
            super.additionalAuthenticationChecks(details, authentication);
        } catch (final EProcurementCustomerException e) {
            final B2BCustomerModel b2bCustomerModel = getUserService().getUserForUID(details.getUsername(), B2BCustomerModel.class);
            if (getUserService().isMemberOfGroup(b2bCustomerModel, getUserService().getUserGroupForUID(DistConstants.User.OCICUSTOMERGROUP_UID))) {
                // customer is OCI customer -> therefore OK
                return;
            }
        }

        // if catch above fails it is not a e-procurement customer and should not be logged in
        throw new EProcurementCustomerException("Login attempt as " + details.getUsername() + " is rejected");
    }
}
