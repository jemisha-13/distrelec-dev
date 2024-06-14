/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * Custom Converter for the target ProductModel of a product reference.
 *
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class ProductReferenceTargetConverter extends AbstractPopulatingConverter<ProductModel, ProductData> {

    @Override
    protected ProductData createTarget() {
        return new ProductData();
    }

}
