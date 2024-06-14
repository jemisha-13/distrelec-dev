package com.namics.distrelec.b2b.core.service.product.model;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

@Deprecated
public class CalibrationAvailableAttributeHandler extends AbstractDynamicAttributeHandler<Boolean, ProductModel> {

    /**
     * Returns true if calibrated version of the product is available.
     */
    @Override
    public Boolean get(ProductModel productModel) {
        throw new UnsupportedOperationException("legacy");
    }
}
