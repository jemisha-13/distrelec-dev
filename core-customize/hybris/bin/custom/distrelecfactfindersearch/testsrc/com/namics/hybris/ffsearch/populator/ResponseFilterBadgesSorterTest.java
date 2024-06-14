/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.namics.hybris.ffsearch.data.facet.FilterBadgeData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.populator.response.helper.ResponseFilterBadgesSorter;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class ResponseFilterBadgesSorterTest {

    private ResponseFilterBadgesSorter<SearchQueryData> breadcrumbSorter;

    private static final String BADGE_ITEM_SORTING_PREFERENCE = "category1,category2,category3,category4,category5,category6,category7,manufacturer,price";

    @Before
    public void setUp() {
        this.breadcrumbSorter = new ResponseFilterBadgesSorter<SearchQueryData>();
        this.breadcrumbSorter.setFixedOrderingPreference(Lists.newArrayList(StringUtils.split(BADGE_ITEM_SORTING_PREFERENCE, ',')));
    }

    @Test
    public void testSortCategories() {

        final List<FilterBadgeData<SearchQueryData>> breadcrumbs = new ArrayList<FilterBadgeData<SearchQueryData>>();

        final FilterBadgeData<SearchQueryData> manufacturer = new FilterBadgeData<SearchQueryData>();
        manufacturer.setFacetCode("manufacturer");

        final FilterBadgeData<SearchQueryData> category1 = new FilterBadgeData<SearchQueryData>();
        category1.setFacetCode("category1");

        final FilterBadgeData<SearchQueryData> bananeBanane = new FilterBadgeData<SearchQueryData>();
        bananeBanane.setFacetCode("bananeBanane");

        final FilterBadgeData<SearchQueryData> category3 = new FilterBadgeData<SearchQueryData>();
        category3.setFacetCode("category3");

        final FilterBadgeData<SearchQueryData> ananasAnanas = new FilterBadgeData<SearchQueryData>();
        ananasAnanas.setFacetCode("ananasAnanas");

        final FilterBadgeData<SearchQueryData> price = new FilterBadgeData<SearchQueryData>();
        price.setFacetCode("price");

        final FilterBadgeData<SearchQueryData> kokosnussKokosnuss = new FilterBadgeData<SearchQueryData>();
        kokosnussKokosnuss.setFacetCode("kokosnussKokosnuss");

        breadcrumbs.add(manufacturer);
        breadcrumbs.add(bananeBanane);
        breadcrumbs.add(category1);
        breadcrumbs.add(category3);
        breadcrumbs.add(ananasAnanas);
        breadcrumbs.add(kokosnussKokosnuss);
        breadcrumbs.add(price);

        final int breadcrumbCount = breadcrumbs.size();

        breadcrumbSorter.sortBreadcrumbs(breadcrumbs);

        Assert.assertTrue(breadcrumbCount == 7);
        Assert.assertEquals("We should not loose any breadcrumbs.", breadcrumbCount, breadcrumbs.size());
        Assert.assertEquals("Category 1 should be on 1st place", "category1", breadcrumbs.get(0).getFacetCode());
        Assert.assertEquals("Category 3 should be on 2nd place", "category3", breadcrumbs.get(1).getFacetCode());
        Assert.assertEquals("Manufacturer should be on 3rd place", "manufacturer", breadcrumbs.get(2).getFacetCode());
        Assert.assertEquals("Price should be on 4th place", "price", breadcrumbs.get(3).getFacetCode());
        Assert.assertEquals("bananeBanane should be on 5th place", "bananeBanane", breadcrumbs.get(4).getFacetCode());
        Assert.assertEquals("kokosnussKokosnuss should be on 7th place", "kokosnussKokosnuss", breadcrumbs.get(6).getFacetCode());

    }
}
