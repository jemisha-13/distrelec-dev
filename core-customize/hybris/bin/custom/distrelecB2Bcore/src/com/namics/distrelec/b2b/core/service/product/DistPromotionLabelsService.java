/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product;

import java.util.List;

import com.namics.distrelec.b2b.core.model.DistPromotionLabelModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.ProductCountryModel;

import de.hybris.platform.core.model.product.ProductModel;

/**
 * Service for {@link DistPromotionLabelModel}.
 * 
 * @author sivakumaran, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public interface DistPromotionLabelsService {

    /**
     * Returns all active Promotion labels for a product using the session salesOrg
     * 
     * @param product
     *            the source Product
     * @return all active promotion labels
     */
    List<DistPromotionLabelModel> getActivePromotionLabelsForProduct(ProductModel product);

    /**
     * Returns all active Promotion labels for a product.
     * 
     * @param product
     *            the source Product
     * @param salesOrgProduct
     *            the source DistSalesOrgProduct
     * 
     * @param productCountry
     *            the source ProductCountry
     * 
     * @return all active promotion labels
     */
    List<DistPromotionLabelModel> getActivePromotionLabels(ProductModel product, DistSalesOrgProductModel salesOrgProduct,
            ProductCountryModel productCountry);

    /**
     * Returns all existing promotion labels (can be restricte by maintainedInDistSalesOrgProduct or maintainedInProductCountry parameter).
     * 
     * @param maintainedInDistSalesOrgProduct
     *            restriction to just return all promotion labels maintained in DistSalesOrgProduct.
     * @param maintainedInProductCountry
     *            restriction to just return all promotion labels maintained in ProductCountry.
     * @return list of promotion labels
     */
    List<DistPromotionLabelModel> getAllPromotionLabels(Boolean maintainedInDistSalesOrgProduct, Boolean maintainedInProductCountry);

}
