/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.converter;

import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderPriceValueModel;
import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderValueModel;
import com.namics.distrelec.b2b.facades.productfinder.data.DistProductFinderFacetValueData;
import com.namics.distrelec.b2b.facades.productfinder.data.DistProductFinderValueData;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;
import de.hybris.platform.converters.Populator;

import java.util.Collections;

/**
 * Populator for product finder price values.
 *
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DistProductFinderPriceValuePopulator implements Populator<DistProductFinderValueModel, DistProductFinderValueData> {

    @Override
    public void populate(final DistProductFinderValueModel source, final DistProductFinderValueData target) {
        if (source instanceof DistProductFinderPriceValueModel) {
            final DistProductFinderFacetValueData facetValue = new DistProductFinderFacetValueData();
            facetValue.setKey(DistFactFinderExportColumns.PRICE.getValue());
            facetValue.setValue(((DistProductFinderPriceValueModel) source).getPrice().toString());
            target.setFacetValues(Collections.singletonList(facetValue));
        }
    }

}
