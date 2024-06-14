/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.util.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto.PimProductReferenceDto;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.util.ProductReferenceCreator;

import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;

public class ProductReferenceCreatorImpl implements ProductReferenceCreator {

    @Autowired
    private ModelService modelService;

    @Override
    public ProductReferenceModel create(final PimProductReferenceDto productReferenceDto, final ProductModel sourceProduct, final ProductModel targetProduct) {
        ProductReferenceModel productReference = null;
        productReference = modelService.create(ProductReferenceModel.class);
        productReference.setSource(sourceProduct);
        productReference.setTarget(targetProduct);
        productReference.setReferenceType(productReferenceDto.getProductReferenceType());
        productReference.setActive(Boolean.TRUE);
        productReference.setPreselected(Boolean.FALSE);
        productReference.setReplacementReason(productReferenceDto.getReplacementReason());
        return productReference;
    }

}
