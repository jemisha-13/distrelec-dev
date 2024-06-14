/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.converter;

import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderListValueModel;
import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderSingleValueModel;
import com.namics.distrelec.b2b.facades.productfinder.data.DistProductFinderFacetValueData;
import com.namics.distrelec.b2b.facades.productfinder.data.DistProductFinderValueData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * Converter for product finder values.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DistProductFinderListValueConverter extends DistProductFinderValueConverter<DistProductFinderListValueModel> {

    private Converter<DistProductFinderSingleValueModel, DistProductFinderValueData> singleValueConverter;

    @Override
    public void populate(final DistProductFinderListValueModel source, final DistProductFinderValueData target) {
        final List<DistProductFinderFacetValueData> facetValues = new ArrayList<DistProductFinderFacetValueData>();
        for (final DistProductFinderSingleValueModel singleValue : source.getMergedValues()) {
            final DistProductFinderValueData singleValueData = getSingleValueConverter().convert(singleValue);
            if (singleValueData.getFacetValues() != null) {
                facetValues.addAll(singleValueData.getFacetValues());
            }
        }
        target.setFacetValues(facetValues);

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
