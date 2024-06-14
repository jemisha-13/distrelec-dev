/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.converter;

import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderValueModel;
import com.namics.distrelec.b2b.facades.productfinder.data.DistProductFinderValueData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

/**
 * Converter for product finder values.
 *
 * @param <VALUE>
 *
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DistProductFinderValueConverter<VALUE extends DistProductFinderValueModel> extends AbstractPopulatingConverter<VALUE, DistProductFinderValueData> {

    @Override
    public void populate(final VALUE source, final DistProductFinderValueData target) {
        target.setName(source.getName());

        super.populate(source, target);
    }

}
