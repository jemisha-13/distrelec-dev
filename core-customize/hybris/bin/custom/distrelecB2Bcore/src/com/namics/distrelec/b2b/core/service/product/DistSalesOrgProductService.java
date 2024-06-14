/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;

import de.hybris.platform.core.model.product.ProductModel;

/**
 * Service with some basic functions for the {@link DistSalesOrgProductModel}
 * 
 * @author sivakumaran, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public interface DistSalesOrgProductService {

    /**
     * Returns the DistSalesOrgProduct for the the product and the salesOrg.
     * 
     * @param product
     *            source product
     * @param salesOrg
     *            source salesOrg
     * @return
     */
    DistSalesOrgProductModel getSalesOrgProduct(final ProductModel product, final DistSalesOrgModel salesOrg);

    /**
     * Returns the DistSalesOrgProduct for the the product and the current salesOrg.
     * 
     * @param product
     *            source product
     * @param salesOrg
     *            source salesOrg
     * @return
     */
    DistSalesOrgProductModel getCurrentSalesOrgProduct(final ProductModel product);
}
