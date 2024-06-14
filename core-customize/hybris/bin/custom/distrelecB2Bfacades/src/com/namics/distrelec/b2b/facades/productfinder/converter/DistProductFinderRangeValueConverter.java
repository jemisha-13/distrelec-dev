/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.converter;

import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderRangeValueModel;
import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderSingleValueModel;
import com.namics.distrelec.b2b.facades.productfinder.data.DistProductFinderValueData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Converter for product finder values.
 *
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DistProductFinderRangeValueConverter extends DistProductFinderValueConverter<DistProductFinderRangeValueModel> {

    private static final Logger LOG = LogManager.getLogger(DistProductFinderRangeValueConverter.class);

    private Converter<DistProductFinderSingleValueModel, DistProductFinderValueData> singleValueConverter;

    @Override
    public void populate(final DistProductFinderRangeValueModel source, final DistProductFinderValueData target) {
        final DistProductFinderValueData fromValueData = getSingleValueConverter().convert(source.getFrom());
        final DistProductFinderValueData toValueData = getSingleValueConverter().convert(source.getTo());

        if (fromValueData.getFacetValues() == null || fromValueData.getFacetValues().isEmpty() || toValueData.getFacetValues() == null
                || toValueData.getFacetValues().isEmpty()) {
            LOG.error("Could not determine min or max value for DistProductFinderRangeValue with pk [" + source.getPk() + "] and name [" + source.getName()
                    + "]");
        } else {
            target.setMinValue(fromValueData.getFacetValues().get(0).getValue());
            target.setMaxValue(toValueData.getFacetValues().get(0).getValue());
            target.setMinMaxKey(fromValueData.getFacetValues().get(0).getKey());
        }

        super.populate(source, target);
    }

    public Converter<DistProductFinderSingleValueModel, DistProductFinderValueData> getSingleValueConverter() {
        return singleValueConverter;
    }

    @Required
    public void setSingleValueConverter(final Converter<DistProductFinderSingleValueModel, DistProductFinderValueData> singleValueConverter) {
        this.singleValueConverter = singleValueConverter;
    }

}
