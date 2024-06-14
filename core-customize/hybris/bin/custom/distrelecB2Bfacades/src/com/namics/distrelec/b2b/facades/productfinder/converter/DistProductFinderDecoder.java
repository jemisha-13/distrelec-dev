/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.converter;

import com.namics.distrelec.b2b.facades.productfinder.data.*;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;
import de.hybris.platform.converters.impl.AbstractConverter;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts data from a product finder view back to a valid SearchQueryData object.
 *
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DistProductFinderDecoder extends AbstractConverter<DistProductFinderData, SearchQueryData> {

    @Override
    public void populate(final DistProductFinderData source, final SearchQueryData target) {
        // TODO rlehmann - DISTRELEC-4343: check if it works this way!!
        if (StringUtils.isNotEmpty(source.getCategoryCode())) {
            // set category search
            target.setSearchType(DistSearchType.CATEGORY);
            target.setCode(source.getCategoryCode());
        }
        target.setFreeTextSearch("*");

        final List<SearchQueryTermData> searchQueryTerms = new ArrayList<SearchQueryTermData>();
        for (final DistProductFinderColumnData column : source.getColumns()) {
            for (final DistProductFinderGroupData group : column.getGroups()) {
                for (final DistProductFinderValueData value : group.getValues()) {
                    if (Boolean.TRUE.equals(value.getChecked())) {
                        if (value.getFacetValues() != null && !value.getFacetValues().isEmpty()) {
                            // Collect sinle and list values
                            for (final DistProductFinderFacetValueData facetValue : value.getFacetValues()) {
                                searchQueryTerms.add(createSingleValueTerm(facetValue));
                            }
                        } else {
                            // Get range value
                            searchQueryTerms.add(createRangeTerm(value));
                        }
                    }
                }

                if (group.getCustomValue() != null && Boolean.TRUE.equals(group.getCustomValue().getChecked())) {
                    searchQueryTerms.add(createRangeTerm(group.getCustomValue()));
                }
            }
        }
        target.setFilterTerms(searchQueryTerms);
    }

    private SearchQueryTermData createSingleValueTerm(final DistProductFinderFacetValueData facetValue) {
        final SearchQueryTermData term = new SearchQueryTermData();
        term.setKey(facetValue.getKey());
        term.setValue(facetValue.getValue());
        return term;
    }

    private SearchQueryTermData createRangeTerm(final DistProductFinderValueData value) {
        final SearchQueryTermData term = new SearchQueryTermData();
        term.setKey(value.getMinMaxKey());

        final StringBuilder range = new StringBuilder();
        range.append(value.getMinValue()).append(" - ").append(value.getMaxValue());
        term.setValue(range.toString());

        return term;
    }

}
