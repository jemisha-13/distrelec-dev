/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters.populator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerData;

import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DistProductManufacturerPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends AbstractProductPopulator<SOURCE, TARGET> {

    @Autowired
    @Qualifier("distManufacturerConverter")
    private Converter<DistManufacturerModel, DistManufacturerData> distManufacturerConverter;

    @Override
    public void populate(final SOURCE source, final TARGET target) throws ConversionException {
        final DistManufacturerModel manufacturer = source.getManufacturer();
        if (manufacturer != null) {
            target.setDistManufacturer(distManufacturerConverter.convert(manufacturer));
        }
    }

    public Converter<DistManufacturerModel, DistManufacturerData> getDistManufacturerConverter() {
        return distManufacturerConverter;
    }

    public void setDistManufacturerConverter(final Converter<DistManufacturerModel, DistManufacturerData> distManufacturerConverter) {
        this.distManufacturerConverter = distManufacturerConverter;
    }

}
