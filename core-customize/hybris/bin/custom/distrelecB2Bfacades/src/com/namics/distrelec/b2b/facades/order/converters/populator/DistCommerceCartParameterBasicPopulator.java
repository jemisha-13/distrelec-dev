package com.namics.distrelec.b2b.facades.order.converters.populator;

import de.hybris.platform.commercefacades.order.converters.populator.CommerceCartParameterBasicPopulator;
import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DistCommerceCartParameterBasicPopulator extends CommerceCartParameterBasicPopulator {

    @Override
    public void populate(AddToCartParams addToCartParams, CommerceCartParameter parameter) throws ConversionException {
        super.populate(addToCartParams, parameter);
        parameter.setReference(addToCartParams.getReference());
        parameter.setRecalculate(addToCartParams.getRecalculate());
        parameter.setSearchQuery(addToCartParams.getSearchQuery());
        parameter.setAddedFrom(addToCartParams.getAddedFrom());
    }
}
