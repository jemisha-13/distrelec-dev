/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.filter;

import org.apache.commons.collections.Predicate;

import de.hybris.platform.core.model.product.ProductModel;

/**
 * class definition to be used in DefaultDistProductService.<br />
 * if one can not set the original product, the unit test will not work.
 * 
 * @author rlehmann, Namics AG
 * @since Distrelect 2.0.20 (DISTRELEC-4581)
 * 
 */
public class ShowSimilarProductsPredicate implements Predicate {

    private ProductModel originalProduct;

    public ShowSimilarProductsPredicate(ProductModel originalProduct) {
        this.originalProduct = originalProduct;
    }

    @Override
    public boolean evaluate(Object paramObject) {
        // Do nothing.. has to be overwritten
        return false;
    }

    public ProductModel getOriginalProduct() {
        return originalProduct;
    }

}
