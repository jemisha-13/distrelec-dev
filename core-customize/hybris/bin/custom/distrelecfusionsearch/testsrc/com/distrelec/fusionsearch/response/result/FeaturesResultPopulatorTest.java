package com.distrelec.fusionsearch.response.result;

import static com.distrelec.fusionsearch.response.result.FeaturesResultPopulator.DISPLAY_FIELDS_PROPERTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.mockito.InjectMocks;

import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

public class FeaturesResultPopulatorTest extends AbstractSearchResultPopulatorTest {

    @InjectMocks
    FeaturesResultPopulator featuresResultPopulator;

    @Test
    public void testConvertProductFeatures() {
        String displayFields = "[{\"code\":\"disledcolourtxt\",\"attributeName\":\"LED Colour\",\"value\":\"Green / Yellow\",\"fieldType\":\"string\"},{\"code\":\"disledpitchnum\",\"attributeName\":\"LED Pitch\",\"value\":\"2.5\",\"unit\":\"mm\",\"fieldType\":\"double\"}]";
        String featuresString = "|LED Colour=Green / Yellow|LED Pitch~~mm=2.5|";

        Map<String, Object> doc = Map.of(DISPLAY_FIELDS_PROPERTY, displayFields);

        featuresResultPopulator.populate(doc, searchResult);

        Map<String, Object> values = searchResult.getValues();
        assertEquals(1, values.size());
        String actualFeaturesString = (String) values.get(DistFactFinderExportColumns.TECHNICAL_ATTRIBUTES.getValue());
        assertEquals(featuresString, actualFeaturesString);
    }

    @Test
    public void testConvertIfNullDisplayFields() {
        Map<String, Object> doc = Map.of();
        featuresResultPopulator.populate(doc, searchResult);
        assertTrue(searchResult.getValues().isEmpty());
    }
}
