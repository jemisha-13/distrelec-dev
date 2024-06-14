package com.namics.distrelec.b2b.core.dataimport.batch.converter;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.jalo.Item;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

@UnitTest
@RunWith(DataProviderRunner.class)
public class SapWeighUnitValueTranslatorTest {

    @InjectMocks
    private final SapWeighUnitValueTranslator sapWeightUnitValueTranslator = new SapWeighUnitValueTranslator();

    @Test
    // @formatter:off
    @DataProvider(value = {
        // input                        : expectedOutput
        "GR                             : null" // GR must be ignored
    }, splitBy = ":", trimValues = true)
    // @formatter:on
    public void test(final String input, final String expectedOutput) {
        final Item mockedItem = Mockito.mock(Item.class);
        final String actualOutput = (String) sapWeightUnitValueTranslator.importValue(input, mockedItem);
        assertEquals(expectedOutput, actualOutput);
        Mockito.verifyZeroInteractions(mockedItem);
    }
}
