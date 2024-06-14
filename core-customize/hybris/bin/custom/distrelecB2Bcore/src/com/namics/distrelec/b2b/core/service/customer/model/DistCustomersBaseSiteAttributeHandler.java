/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer.model;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

/**
 * Dynamic attribute handler for retrieving the customers base site.
 * 
 * @author rahusi, Distrelec
 * @since Distrelec 1.0
 */
public class DistCustomersBaseSiteAttributeHandler extends AbstractDynamicAttributeHandler<BaseSiteModel, B2BCustomerModel> {

    @Autowired
    private DistrelecCMSSiteService distrelecCMSSiteService;

    @Override
    public BaseSiteModel get(final B2BCustomerModel customer) {
        if (customer != null) {
            final B2BUnitModel unit = customer.getDefaultB2BUnit();
            if (unit != null && unit.getBillingAddress() != null && unit.getSalesOrg() != null) {
                return getDistrelecCMSSiteService().getSiteForCountryAndSalesOrg(unit.getBillingAddress().getCountry(), unit.getSalesOrg());
            }
        }
        return null;
    }

    public DistrelecCMSSiteService getDistrelecCMSSiteService() {
        return distrelecCMSSiteService;
    }

    public void setDistrelecCMSSiteService(DistrelecCMSSiteService distrelecCMSSiteService) {
        this.distrelecCMSSiteService = distrelecCMSSiteService;
    }

}
