/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters.populator;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.DistImage360Model;
import com.namics.distrelec.b2b.facades.product.data.DistImage360Data;

import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DistImage360Populator<SOURCE extends ProductModel, TARGET extends ProductData> extends AbstractProductPopulator<SOURCE, TARGET> {

    @Autowired
    @Qualifier("distImage360Converter")
    private Converter<DistImage360Model, DistImage360Data> distImage360Converter;

    @Override
    public void populate(final SOURCE source, final TARGET target) throws ConversionException {
        final Set<DistImage360Model> image360Models = source.getImages360();
        if (image360Models != null) {
            final List<DistImage360Data> images360 = new LinkedList<>();
            for (final DistImage360Model image360Model : image360Models) {
                final DistImage360Data image360Data = distImage360Converter.convert(image360Model);
                images360.add(image360Data);
            }
            target.setImages360(images360);
        }
    }

}
