/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters.populator;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.facades.i18n.converter.DistCountryPopulator;

import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;

public class ProductCountryOfOriginPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends AbstractProductPopulator<SOURCE, TARGET> {

    @Autowired
    private DistCountryPopulator countryConverter;

    @Override
    public void populate(final SOURCE source, final TARGET target) {
        if (source.getCountryOfOrigin() != null) {
            target.setCountryOfOrigin(countryConverter.convert(source.getCountryOfOrigin()));
        }
    }

    public DistCountryPopulator getCountryConverter() {
        return countryConverter;
    }

    public void setCountryConverter(final DistCountryPopulator countryConverter) {
        this.countryConverter = countryConverter;
    }

}
