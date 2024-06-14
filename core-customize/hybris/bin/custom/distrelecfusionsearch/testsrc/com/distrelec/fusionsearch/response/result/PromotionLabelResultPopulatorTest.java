package com.distrelec.fusionsearch.response.result;

import static com.distrelec.fusionsearch.response.result.PromotionLabelResultPopulator.ACTIVE_PROMOTION_LABELS_PROPERTY;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.mockito.InjectMocks;

import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class PromotionLabelResultPopulatorTest extends AbstractSearchResultPopulatorTest {

    @InjectMocks
    PromotionLabelResultPopulator resultPopulator;

    @Test
    public void testTransformActivePromotionLabels() {
        String activePromoLabels = "top|New";
        String expectedPromoLabelsJson = "[{\"code\":\"top\",\"label\":\"New\",\"nameEN\":null,\"rank\":null,\"priority\":null,\"active\":true}]";

        Map<String, Object> doc = Map.of(ACTIVE_PROMOTION_LABELS_PROPERTY, activePromoLabels);

        resultPopulator.populate(doc, searchResult);

        Map<String, Object> values = searchResult.getValues();
        assertEquals(1, values.size());
        assertEquals(expectedPromoLabelsJson, values.get(DistFactFinderExportColumns.PROMOTIONLABELS.getValue()));

    }
}
