/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Custom Converter for ProductReferenceModel.
 *
 * @author sivakumaran, Namics AG
 * @author ceberle, Namics AG
 * @since Namics Extensions 1.0
 */
public class ProductReferenceConverter extends AbstractConverter<ProductReferenceModel, ProductReferenceData> {

    private Converter<ProductModel, ProductData> productReferenceTargetConverter;

    @Override
    public void populate(final ProductReferenceModel source, final ProductReferenceData target) {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        target.setDescription(source.getDescription());
        target.setPreselected(source.getPreselected());
        target.setQuantity(source.getQuantity());
        target.setReferenceType(source.getReferenceType());

        target.setTarget(getProductReferenceTargetConverter().convert(source.getTarget()));
    }

    public Converter<ProductModel, ProductData> getProductReferenceTargetConverter() {
        return productReferenceTargetConverter;
    }

    @Required
    public void setProductReferenceTargetConverter(final Converter<ProductModel, ProductData> productReferenceTargetConverter) {
        this.productReferenceTargetConverter = productReferenceTargetConverter;
    }

}