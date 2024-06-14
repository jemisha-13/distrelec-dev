package com.distrelec.fusionsearch.response.result;

import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.LANDSCAPE_MEDIUM;
import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.LANDSCAPE_SMALL;
import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.PORTRAIT_SMALL;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.mockito.InjectMocks;

import com.google.gson.Gson;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class AdditionalImageUrlResultPopulatorTest extends AbstractSearchResultPopulatorTest {

    @InjectMocks
    AdditionalImageUrlResultPopulator additionalImageUrlResultPopulator;

    @Test
    public void testPopulateAdditionalImageUrls() {
        String landscapeSmall = "/landSmall.jpg";
        String landscapeMedium = "/landMedium.jpg";
        String portraitSmall = "/portraitSmall.jpg";

        Map<String, Object> doc = Map.of("imageUrl_landscape_small", landscapeSmall, "ImageUrl_landscape_medium", landscapeMedium, "imageurl_portrait_small",
                                         portraitSmall);

        additionalImageUrlResultPopulator.populate(doc, searchResult);

        Map<String, Object> values = searchResult.getValues();
        assertEquals(1, values.size());

        String additionalImageUrlsJson = (String) values.get(DistFactFinderExportColumns.ADDITIONAL_IMAGE_URLS.getValue());

        Gson gson = new Gson();
        Map<String, String> additionalImageUrls = gson.fromJson(additionalImageUrlsJson, Map.class);
        assertEquals(3, additionalImageUrls.size());
        assertEquals(landscapeSmall, additionalImageUrls.get(LANDSCAPE_SMALL));
        assertEquals(landscapeMedium, additionalImageUrls.get(LANDSCAPE_MEDIUM));
        assertEquals(portraitSmall, additionalImageUrls.get(PORTRAIT_SMALL));
    }
}
