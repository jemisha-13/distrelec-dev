/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.batch.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.jalo.Item;

@UnitTest
@RunWith(DataProviderRunner.class)
public class SapPlantProfilesValueTranslatorTest {

    @InjectMocks
    private final SapPlantProfilesValueTranslator sapPlantProfilesValueTranslator = new SapPlantProfilesValueTranslator();

    @Test
    // @formatter:off
    @DataProvider(value = {
            // input                        : expectedOutput
            "7311                           : 7311", // without &
            "7374&FM00                      : 7374&FM00", // with &
            "7331|7351|7371&FM00|7374&FM00  : 7331|7351|7371&FM00|7374&FM00", // More than one value
            "7351|7371&                     : 7351|7371", // Remove trailing &
            "7331|7351|7371&|7374&FM00      : 7331|7351|7371|7374&FM00" // Remove & when followed by separator
        }, splitBy = ":", trimValues = true)
    // @formatter:on
    public void test(final String input, final String expectedOutput) {
        final Item mockedItem = Mockito.mock(Item.class);
        final String actualOutput = (String) sapPlantProfilesValueTranslator.importValue(input, mockedItem);
        assertEquals(expectedOutput, actualOutput);
        Mockito.verifyZeroInteractions(mockedItem);
    }

    @Test
    public void testForNull() {
        final Item mockedItem = Mockito.mock(Item.class);
        assertNull(sapPlantProfilesValueTranslator.importValue(null, mockedItem));
        assertNull(sapPlantProfilesValueTranslator.importValue("   ", mockedItem));
        Mockito.verifyZeroInteractions(mockedItem);
    }

}
