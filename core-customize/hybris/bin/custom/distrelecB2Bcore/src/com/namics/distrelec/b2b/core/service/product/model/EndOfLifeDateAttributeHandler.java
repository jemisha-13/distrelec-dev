/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.model;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

/**
 * Dynamic attribute handler for endOfLifeDate.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class EndOfLifeDateAttributeHandler extends AbstractDynamicAttributeHandler<Date, ProductModel> {

    @Autowired
    private DistSalesOrgProductService distSalesOrgProductService;

    @Override
    public Date get(final ProductModel product) {
        final DistSalesOrgProductModel salesOrgProduct = getDistSalesOrgProductService().getCurrentSalesOrgProduct(product);
        if (salesOrgProduct != null) {
            return salesOrgProduct.getEndOfLifeDate();
        } else {
            return null;
        }
    }

    public DistSalesOrgProductService getDistSalesOrgProductService() {
        return distSalesOrgProductService;
    }

    public void setDistSalesOrgProductService(DistSalesOrgProductService distSalesOrgProductService) {
        this.distSalesOrgProductService = distSalesOrgProductService;
    }

}
