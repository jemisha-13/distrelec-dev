/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.response.helper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.namics.hybris.ffsearch.data.facet.FilterBadgeData;

/**
 * Sorts FactFinder filter items according to the fixed, hierarchical preference as defined in
 * https://wiki.namics.com/display/distrelint/C13-Facets.
 * 
 * The fixed sorting is read from a property file.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class ResponseFilterBadgesSorter<ITEM> {

    private @Value("#{'${distrelecfactfindersearch.populators.breadcrumb.sorting}'.split(',')}")
    List<String> fixedOrderingPreference;

    /**
     * Sort given list according to https://wiki.namics.com/display/distrelint/C13-Facets. <br/>
     * 
     * 1.) Categories should in Sub-Super Category order <br/>
     * 2.) Manufacturers <br/>
     * 3.) Prices <br/>
     * 4.) All other facets, in the preference as fetched from FactFinder <br/>
     * 
     * @param breadcrumbs
     *            list of items to be sorted
     */
    public void sortBreadcrumbs(final List<FilterBadgeData<ITEM>> breadcrumbs) {

        // build up list which contains the keys to be sorted, in the correct order
        final Set<String> orderingPreference = Sets.newLinkedHashSet(fixedOrderingPreference);
        for (final FilterBadgeData<ITEM> breadcrumb : breadcrumbs) {
            orderingPreference.add(StringUtils.lowerCase(breadcrumb.getFacetCode()));
        }

        // order breadcrumbs by considering the ordering preference definded above
        final Ordering<String> byCategoriesFirst = Ordering.explicit(Lists.newArrayList(orderingPreference));
        Collections.sort(breadcrumbs, new Comparator<FilterBadgeData<ITEM>>() {
            @Override
            public int compare(final FilterBadgeData<ITEM> left, final FilterBadgeData<ITEM> right) {
                return byCategoriesFirst.compare(StringUtils.lowerCase(left.getFacetCode()), StringUtils.lowerCase(right.getFacetCode()));
            }
        });
    }

    public void setFixedOrderingPreference(final List<String> fixedOrderingPreference) {
        this.fixedOrderingPreference = fixedOrderingPreference;
    }

}
