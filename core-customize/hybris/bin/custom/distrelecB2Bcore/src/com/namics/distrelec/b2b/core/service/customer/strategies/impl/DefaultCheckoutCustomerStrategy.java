/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer.strategies.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.customer.strategies.CheckoutCustomerStrategy;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Default implementation of CheckoutCustomerStrategy.
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class DefaultCheckoutCustomerStrategy extends de.hybris.platform.commerceservices.strategies.impl.DefaultCheckoutCustomerStrategy
                                             implements CheckoutCustomerStrategy {

    @Autowired
    private SessionService sessionService;

    @Override
    public boolean isAnonymousCheckout() {
        if (!Boolean.TRUE.equals(getSessionService().getAttribute(ANONYMOUS_CHECKOUT))) {
            return false;
        }
        return getUserService().isAnonymousUser(getUserService().getCurrentUser());
    }

    @Override
    public B2BCustomerModel getCurrentUserForCheckout() {
        if (isAnonymousCheckout() && getCartService().hasSessionCart()) {
            if(getUserService().isAnonymousUser(getCartService().getSessionCart().getUser())){
                return null;
            }
            return (B2BCustomerModel) getCartService().getSessionCart().getUser();
        }
        return (B2BCustomerModel) getUserService().getCurrentUser();
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean isNotLimitedUserType(CustomerModel customer) {
        return !isEShopGroup(customer) && !CustomerType.GUEST.equals(customer.getCustomerType());
    }

    @Override
    public boolean isEShopGroup() {
        return isEShopGroup((CustomerModel) getUserService().getCurrentUser());
    }

    @Override
    public boolean isEShopGroup(CustomerModel customer) {
        return getUserService().isMemberOfGroup(customer, getUserService().getUserGroupForUID(DistConstants.User.B2BEESHOPGROUP_UID));
    }
}
