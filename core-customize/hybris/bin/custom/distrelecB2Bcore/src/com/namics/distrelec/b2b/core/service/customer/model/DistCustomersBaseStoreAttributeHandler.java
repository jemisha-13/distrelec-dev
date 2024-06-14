/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer.model;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.site.DistrelecBaseStoreService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Dynamic attribute handler for support phone numbers.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DistCustomersBaseStoreAttributeHandler extends AbstractDynamicAttributeHandler<BaseStoreModel, B2BCustomerModel> {

    @Autowired
    private DistrelecBaseStoreService distrelecBaseStoreService;

    @Override
    public BaseStoreModel get(final B2BCustomerModel customer) {
        return getDistrelecBaseStoreService().getCustomersBaseStore(customer);
    }

    public DistrelecBaseStoreService getDistrelecBaseStoreService() {
        return distrelecBaseStoreService;
    }

    public void setDistrelecBaseStoreService(DistrelecBaseStoreService distrelecBaseStoreService) {
        this.distrelecBaseStoreService = distrelecBaseStoreService;
    }

}
