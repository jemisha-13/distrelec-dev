package com.namics.distrelec.b2b.core.service.product.model;

import java.util.Locale;

import com.namics.distrelec.b2b.core.model.AbstractDynamicLocalizedAttributeHandler;
import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.core.model.product.ProductModel;

public class ProductSafeUrlNameAttributeHandler extends AbstractDynamicLocalizedAttributeHandler<String, ProductModel> {

    @Override
    public String get(ProductModel product) {
        String rawName = product.getName();
        return DistUtils.urlSafe(rawName);
    }

    @Override
    public String get(ProductModel product, Locale loc) {
        String rawName = product.getName(loc);
        return DistUtils.urlSafe(rawName);
    }
}
