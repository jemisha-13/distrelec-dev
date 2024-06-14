/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.helper.impl;

import com.namics.distrelec.b2b.facades.productfinder.data.DistProductFinderFacetValueData;
import com.namics.distrelec.b2b.facades.productfinder.data.DistProductFinderValueData;
import com.namics.distrelec.b2b.facades.productfinder.helper.DistProductFinderCompareHelper;
import com.namics.distrelec.b2b.facades.productfinder.helper.DistProductFinderHelper;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetData;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetType;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetValueData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@class DistProductFinderHelper} class.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DefaultDistProductFinderHelper implements DistProductFinderHelper {

    private DistProductFinderCompareHelper distProductFinderCompareHelper;

    @Override
    public boolean isValueAvailable(final DistProductFinderValueData value, final FactFinderProductSearchPageData<?, ?> searchPageData) {
        if (value.getMinValue() == null) {
            return isAnyAvailable(value, searchPageData);
        } else {
            return isRangeAvailable(value, searchPageData);
        }
    }

    private boolean isAnyAvailable(final DistProductFinderValueData value, final FactFinderProductSearchPageData<?, ?> searchPageData) {
        if (value.getFacetValues() != null) {
            for (final DistProductFinderFacetValueData facetValueData : value.getFacetValues()) {
                if (isFacetAvailable(facetValueData.getKey(), facetValueData.getValue(), searchPageData)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isFacetAvailable(final String key, final String value, final FactFinderProductSearchPageData<?, ?> searchPageData) {
        final FactFinderFacetData<?> facet = getFacet(searchPageData, key);
        if (facet != null) {
            for (final FactFinderFacetValueData<?> facetValue : facet.getValues()) {
                if (facet.getType().equals(FactFinderFacetType.SLIDER)) {
                    distProductFinderCompareHelper.isBetween(value, facetValue.getAbsoluteMinValue(), facetValue.getAbsoluteMaxValue());
                } else {
                    if (facetValue.getCode().equals(value)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isRangeAvailable(final DistProductFinderValueData value, final FactFinderProductSearchPageData<?, ?> searchPageData) {
        final FactFinderFacetData<?> facet = getFacet(searchPageData, value.getMinMaxKey());

        if (facet != null) {
            if (facet.getType().equals(FactFinderFacetType.SLIDER)) {
                return isRangeForSliderAvailable(value, facet);
            } else {
                return isRangeForDefaultAvailable(value, facet);
            }
        }

        return false;
    }

    private boolean isRangeForSliderAvailable(final DistProductFinderValueData value, final FactFinderFacetData<?> facet) {
        if (facet.getValues().isEmpty()) {
            return false;
        }

        // Get Slider facet value
        final FactFinderFacetValueData<?> facetValue = facet.getValues().get(0);

        return distProductFinderCompareHelper.isLowerThanOrEqual(value.getMinValue(), facetValue.getAbsoluteMaxValue())
                && distProductFinderCompareHelper.isGreaterThanOrEqual(value.getMaxValue(), facetValue.getAbsoluteMinValue());
    }

    private boolean isRangeForDefaultAvailable(final DistProductFinderValueData value, final FactFinderFacetData<?> facet) {
        final List<DistProductFinderFacetValueData> rangeFacetValues = new ArrayList<DistProductFinderFacetValueData>();

        for (final FactFinderFacetValueData<?> factFinderFacetValue : facet.getValues()) {
            if (distProductFinderCompareHelper.isBetween(factFinderFacetValue.getCode(), value.getMinValue(), value.getMaxValue())) {
                final DistProductFinderFacetValueData rangeFacetValue = new DistProductFinderFacetValueData();
                rangeFacetValue.setKey(value.getMinMaxKey());
                rangeFacetValue.setValue(factFinderFacetValue.getCode());
                rangeFacetValues.add(rangeFacetValue);
            }
        }

        if (rangeFacetValues.isEmpty()) {
            return false;
        } else {
            value.setMinMaxKey(null);
            value.setMinValue(null);
            value.setMaxValue(null);
            value.setFacetValues(rangeFacetValues);
            return true;
        }
    }

    private FactFinderFacetData<?> getFacet(final FactFinderProductSearchPageData<?, ?> searchPageData, final String facetCode) {
        if (searchPageData.getOtherFacets() != null) {
            for (final FactFinderFacetData<?> facet : searchPageData.getOtherFacets()) {
                if (facet.getCode().equals(facetCode)) {
                    return facet;
                }
            }
        }

        return null;
    }

    public DistProductFinderCompareHelper getDistProductFinderCompareHelper() {
        return distProductFinderCompareHelper;
    }

    @Required
    public void setDistProductFinderCompareHelper(final DistProductFinderCompareHelper distProductFinderCompareHelper) {
        this.distProductFinderCompareHelper = distProductFinderCompareHelper;
    }

}
