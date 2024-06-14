package com.distrelec.fusionsearch.response.result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Map;

import org.junit.Test;
import org.mockito.InjectMocks;

import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class FusionToFFColumnResultPopulatorTest extends AbstractSearchResultPopulatorTest {

    @InjectMocks
    FusionToFFColumnResultPopulator fusionToFFColumnResultPopulator;

    @Test
    public void testConvertManufacturer() {
        String manufacturer = "man";

        assertFusionAttributeConverted(FusionToFFColumnResultPopulator.DIST_MANUFACTURER_PROPERTY, manufacturer, DistFactFinderExportColumns.MANUFACTURER);
    }

    @Test
    public void testConvertProductFamily() {
        String productFamily = "family";

        assertFusionAttributeConverted(FusionToFFColumnResultPopulator.PRODUCT_FAMILY_PROPERTY, productFamily, DistFactFinderExportColumns.PRODUCT_FAMILY_NAME);
    }

    @Test
    public void testConvertUrlAsProductUrl() {
        String url = "/p/productUrl";

        assertFusionAttributeConverted(FusionToFFColumnResultPopulator.URL_PROPERTY, url, DistFactFinderExportColumns.PRODUCT_URL);
    }

    private void assertFusionAttributeConverted(String fusionAttrName, String fusionAttrValue, DistFactFinderExportColumns ffColumn) {
        Map<String, Object> doc = Map.of(fusionAttrName, fusionAttrValue);

        // prerequisites
        assertNotEquals(ffColumn.getValue(), fusionAttrName);

        fusionToFFColumnResultPopulator.populate(doc, searchResult);

        Map<String, Object> values = searchResult.getValues();
        assertEquals(1, values.size());
        assertEquals(fusionAttrValue, values.get(ffColumn.getValue()));
    }

}
