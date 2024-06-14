/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.eprocurement.ariba.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.storefront.eprocurement.security.exceptions.EProcurementCustomerException;
import com.namics.distrelec.b2b.storefront.security.AcceleratorAuthenticationProvider;
import com.namics.distrelec.b2b.storefront.security.exceptions.CompanyCountryException;

import de.hybris.platform.b2b.model.B2BCustomerModel;

public class AribaAuthenticationProvider extends AcceleratorAuthenticationProvider {

    @Override
    protected void additionalAuthenticationChecks(final UserDetails details, final AbstractAuthenticationToken authentication) {

        try {
            super.additionalAuthenticationChecks(details, authentication);
        } catch (final EProcurementCustomerException e) {
            // this exception is expected
            // TODO: refactor: the use of exception for normal behavior is not recommended
        } catch (final CompanyCountryException e) {
            // this exception is expected
            // TODO: refactor: the use of exception for normal behavior is not recommended

            // there is only one URL for all ariba customers so the county check is not possible here
            // they will be redirected to the correct country in
            // com.namics.distrelec.b2b.storefront.eprocurement.ariba.controllers.AribaAuthenticationController.login()

        }

        // check for ariba customer group
        final B2BCustomerModel b2bCustomerModel = getUserService().getUserForUID(details.getUsername(), B2BCustomerModel.class);
        if (getUserService().isMemberOfGroup(b2bCustomerModel, getUserService().getUserGroupForUID(DistConstants.User.ARIBACUSTOMERGROUP_UID))) {
            // customer ariba customer -> therefore OK
            return;
        }

        // if catch above fails it is not a e-procurement customer and should not be logged in
        throw new EProcurementCustomerException(ErrorLogCode.INVALID_USER_GROUP_ERROR.getCode() + ErrorSource.ARIBA + "Login attempt as "
                + details.getUsername() + " is rejected. User not in group:" + DistConstants.User.ARIBACUSTOMERGROUP_UID);
    }

}
