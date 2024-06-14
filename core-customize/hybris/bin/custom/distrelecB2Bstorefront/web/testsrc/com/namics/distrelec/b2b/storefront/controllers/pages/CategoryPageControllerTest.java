/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.mock.web.MockHttpServletRequest;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.TestUtils;
import com.namics.distrelec.b2b.storefront.seo.DistLink;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(DataProviderRunner.class)
public class CategoryPageControllerTest {

    private static final Logger LOG = LogManager.getLogger(CategoryPageControllerTest.class);

    @InjectMocks
    private final CategoryPageController categoryPageController = new CategoryPageController();

    @Test
    // @formatter:off
    @DataProvider(value = {
        // Expected Page Number | query
        "5                      | q=*&filter_Buyable=1&filter_Category1=Semiconductors&page=5",
        "1                      | q=*&filter_Buyable=1&filter_Category1=Semiconductors&page=-1",
        "1                      | q=*&filter_Buyable=1&filter_Category1=Semiconductors",
        "1                      | q=*&filter_Buyable=1&filter_Category1=Semiconductors&page=NOT_A_NUMBER"
    }, splitBy = "\\|", trimValues = true)
    // @formatter:on
    public void getPageNumberTest(final String expectedPageNumber, final String query) {
        final MockHttpServletRequest request = TestUtils.createMockHttpServletRequest(query);
        final int actualPageNumber = categoryPageController.getPageNumber(request);
        assertEquals(Integer.valueOf(expectedPageNumber).intValue(), actualPageNumber);
    }

    @Test
    // @formatter:off
    @DataProvider(value = {
        // Expected Canonical                                       | query
        "http://www.distrelec.ch/en/power/c/cat-L1D_379521?page=5   | q=*&filter_Buyable=1&filter_Category1=Semiconductors&page=5",
        "http://www.distrelec.ch/en/power/c/cat-L1D_379521          | q=*&filter_Buyable=1&filter_Category1=Semiconductors&page=1",
        "http://www.distrelec.ch/en/power/c/cat-L1D_379521          | q=*&filter_Buyable=1&filter_Category1=Semiconductors&page=-1",
        "http://www.distrelec.ch/en/power/c/cat-L1D_379521          | q=*&filter_Buyable=1&filter_Category1=Semiconductors",
        "http://www.distrelec.ch/en/power/c/cat-L1D_379521          | q=*&filter_Buyable=1&filter_Category1=Semiconductors&page=NOT_A_NUMBER"
    }, splitBy = "\\|", trimValues = true)
    public void setupCanonicalHreflangTest(final String expectedCanonical, final String query) {
        final HttpServletRequest request = TestUtils.createMockHttpServletRequest("http", "www.distrelec.ch", "/en/power/c/cat-L1D_379521", query);
        final List<DistLink> result = categoryPageController.setupCanonicalHreflang(request, new ArrayList<DistLink>());
        assertEquals(1, result.size());
        assertEquals(AbstractPageController.LINK_CANONICAL_RELATIONSHIP, result.get(0).getRel());
        assertEquals(expectedCanonical, result.get(0).getHref());
    }

}
