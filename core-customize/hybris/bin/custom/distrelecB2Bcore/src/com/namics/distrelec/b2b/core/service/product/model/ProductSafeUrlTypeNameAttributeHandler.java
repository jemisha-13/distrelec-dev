package com.namics.distrelec.b2b.core.service.product.model;

import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

public class ProductSafeUrlTypeNameAttributeHandler extends AbstractDynamicAttributeHandler<String, ProductModel> {

    @Override
    public String get(ProductModel product) {
        String rawName = product.getTypeName();
        return DistUtils.urlSafe(rawName);
    }
}
