/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.converter;

import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderManufacturerValueModel;
import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderValueModel;
import com.namics.distrelec.b2b.facades.productfinder.data.DistProductFinderFacetValueData;
import com.namics.distrelec.b2b.facades.productfinder.data.DistProductFinderValueData;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;
import de.hybris.platform.converters.Populator;

import java.util.Collections;

/**
 * Populator for product finder manufacturer values.
 *
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DistProductFinderManufacturerValuePopulator implements Populator<DistProductFinderValueModel, DistProductFinderValueData> {

    @Override
    public void populate(final DistProductFinderValueModel source, final DistProductFinderValueData target) {
        if (source instanceof DistProductFinderManufacturerValueModel) {
            final DistProductFinderFacetValueData facetValue = new DistProductFinderFacetValueData();
            facetValue.setKey(DistFactFinderExportColumns.MANUFACTURER.getValue());
            facetValue.setValue(((DistProductFinderManufacturerValueModel) source).getManufacturer().getName());
            target.setFacetValues(Collections.singletonList(facetValue));
        }
    }

}
