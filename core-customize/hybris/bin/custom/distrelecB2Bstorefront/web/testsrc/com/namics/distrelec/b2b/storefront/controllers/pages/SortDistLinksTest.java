/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.namics.distrelec.b2b.storefront.seo.DistLink;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(DataProviderRunner.class)
public class SortDistLinksTest {

    private static final Logger LOG = LogManager.getLogger(SortDistLinksTest.class);

    @DataProvider
    public static Collection<List<List<String>>> getLists() {
     // @formatter:off
        return Arrays.asList(
                Arrays.asList(Arrays.asList("Sverige", "België", "Danmark"), Arrays.asList("België", "Danmark", "Sverige")), //Normal
                Arrays.asList(Arrays.asList("Sverige", "België", "Österreich"), Arrays.asList("België", "Österreich", "Sverige")), //Umlaut
                Arrays.asList(Arrays.asList(null, "België", "Österreich"), Arrays.asList(null, "België", "Österreich")) // Null First
        );
     // @formatter:on
    }

    @Test
    @UseDataProvider("getLists")
    public void sortDistLinksTest(final List<String> input, final List<String> expected) {

        LOG.info("input: {} - expected: {}", Arrays.toString(input.toArray()), Arrays.toString(expected.toArray()));
        final List<DistLink> headerResult = input.stream().map(s -> {
            final DistLink link = new DistLink();
            link.setCountryName(s);
            return link;
        }).collect(Collectors.toList());
        final Locale locale = Locale.GERMAN;
        AbstractPageController.sortDistLinks(headerResult, locale);

        final List<String> actualCountryNamesList = headerResult.stream().map(dl -> dl.getCountryName()).collect(Collectors.toList());
        
        assertEquals(expected, actualCountryNamesList);
    }

}
