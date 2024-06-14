/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.tags;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(DataProviderRunner.class)
public class FunctionsTest {

    // @formatter:off
    @Test
    @DataProvider(value = {
        "http://example.com?foo=foo1                                    | http://example.com                                    | foo | foo1",
        "http://example.com/page?foo=foo1                               | http://example.com/page                               | foo | foo1",
        "http://example.com/page?bar=bar1&roo=roo1&foo=foo1             | http://example.com/page?bar=bar1&roo=roo1             | foo | foo1",
        "/page?bar=bar1&roo=roo1&foo=foo1                               | /page?bar=bar1&roo=roo1                               | foo | foo1",
        "/page?bar=bar1&roo=roo1&foo=foo+1                              | /page?bar=bar1&roo=roo1                               | foo | foo 1",
        "http://example.com?foo=foo1#fragment                           | http://example.com#fragment                           | foo | foo1",
        "http://example.com/page?foo=foo1#fragment                      | http://example.com/page#fragment                      | foo | foo1",
        "http://example.com/page?bar=bar1&roo=roo1&foo=foo1#fragment    | http://example.com/page?bar=bar1&roo=roo1#fragment    | foo | foo1",
        "/page?bar=bar1&roo=roo1&foo=foo1#fragment                      | /page?bar=bar1&roo=roo1#fragment                      | foo | foo1",
        "/page?bar=bar1&roo=roo1&foo=foo+1#fragment                     | /page?bar=bar1&roo=roo1#fragment                      | foo | foo 1"
    }, splitBy = "\\|", trimValues = true)
    // @formatter:on
    public void addParameterToUrlStringTest(final String expectedUrl, final String inputUrl, final String parameterKey, final String parameterValue) {
        assertEquals(expectedUrl, Functions.addParameterToUrlString(inputUrl, parameterKey, parameterValue));
    }

}
