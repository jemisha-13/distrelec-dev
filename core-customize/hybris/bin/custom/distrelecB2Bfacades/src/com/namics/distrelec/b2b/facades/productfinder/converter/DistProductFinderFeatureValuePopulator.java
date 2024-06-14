/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.converter;

import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderFeatureValueModel;
import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderValueModel;
import com.namics.distrelec.b2b.facades.productfinder.data.DistProductFinderFacetValueData;
import com.namics.distrelec.b2b.facades.productfinder.data.DistProductFinderValueData;
import de.hybris.platform.converters.Populator;
import org.springframework.util.StringUtils;

import java.util.Collections;

import static com.namics.distrelec.b2b.core.constants.DistConstants.FactFinder.FACTFINDER_UNIT_PREFIX;

/**
 * Populator for product finder feature values.
 *
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DistProductFinderFeatureValuePopulator implements Populator<DistProductFinderValueModel, DistProductFinderValueData> {

    @Override
    public void populate(final DistProductFinderValueModel source, final DistProductFinderValueData target) {
        if (source instanceof DistProductFinderFeatureValueModel) {
            final String classificationAttributeName = ((DistProductFinderFeatureValueModel) source).getClassificationAttribute().getName();
            if (StringUtils.hasText(classificationAttributeName)) {
                final StringBuilder key = new StringBuilder(classificationAttributeName);
                if (((DistProductFinderFeatureValueModel) source).getClassificationAttributeUnit() != null) {
                    key.append(FACTFINDER_UNIT_PREFIX)
                            .append(((DistProductFinderFeatureValueModel) source).getClassificationAttributeUnit().getSymbol());
                }
                final String value = ((DistProductFinderFeatureValueModel) source).getValue();

                final DistProductFinderFacetValueData facetValue = new DistProductFinderFacetValueData();
                facetValue.setKey(key.toString());
                facetValue.setValue(value);
                target.setFacetValues(Collections.singletonList(facetValue));
            }
        }
    }

}
