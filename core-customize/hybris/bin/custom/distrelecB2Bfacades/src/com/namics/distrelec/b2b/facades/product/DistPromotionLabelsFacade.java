/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product;

import com.namics.distrelec.b2b.facades.product.data.DistPromotionLabelData;

import java.util.List;

/**
 * Promotion labels facade interface.
 * 
 * @author sivakumaran, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public interface DistPromotionLabelsFacade {

    /**
     * Returns all active Promotion labels for a product using the session salesOrg
     * 
     * @param code
     *            the code of the product
     * @return a list of DTO's containing the code, label and rank
     */
    List<DistPromotionLabelData> getActivePromotionLabelsForProductCode(final String code);

}
