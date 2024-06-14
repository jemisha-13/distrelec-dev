package com.namics.distrelec.b2b.core.mail.internal.converter;

import com.namics.distrelec.b2b.core.mail.internal.data.MissingImageMediaFileData;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistMissingImageMediaFileConverterTest {

    private static final String INTERNAL_URL_WITH_WEBSHOPIMAGES_FOLDER = "/Web/WebShopImages/landscape_large/ho/ne/iPhone.jpg";

    private static final String INTERNAL_URL_WITH_STREAMS_FOLDER = "/Web/Streams/po/rt/HomeMatic.mpg";

    private static final String INTERNAL_URL_WITH_DOWNLOADS_FOLDER = "/Web/Downloads/zz/er/buzzer.pdf";

    private static final String INTERNAL_URL_WITH_UNKNOWN_FOLDER = "/Web/Medias/landscape_large/ho/ne/iPhone.jpg";

    private static final String INVALID_INTERNAL_URL = "/Web/iPhone.jpg";

    @InjectMocks
    private final DistMissingImageMediaFileConverter distMissingImageMediaFileConverter = new DistMissingImageMediaFileConverter();

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testPopulationWithWebShopImagesFolder() {
        MissingImageMediaFileData mediaFileData = new MissingImageMediaFileData();
        distMissingImageMediaFileConverter.populate(INTERNAL_URL_WITH_WEBSHOPIMAGES_FOLDER, mediaFileData);
        assertEquals(mediaFileData.getMediaFolder(), "WebShopImages");
        assertEquals(mediaFileData.getMediaPath(), "landscape_large/ho/ne/iPhone.jpg");
        assertEquals(mediaFileData.getMediaLocation(), "images/landscape_large/ho/ne/");
        assertEquals(mediaFileData.getMediaName(), "iPhone.jpg");
    }

    @Test
    public void testPopulationWithStreamsFolder() {
        MissingImageMediaFileData mediaFileData = new MissingImageMediaFileData();
        distMissingImageMediaFileConverter.populate(INTERNAL_URL_WITH_STREAMS_FOLDER, mediaFileData);
        assertEquals(mediaFileData.getMediaFolder(), "Streams");
        assertEquals(mediaFileData.getMediaPath(), "po/rt/HomeMatic.mpg");
        assertEquals(mediaFileData.getMediaLocation(), "videos/po/rt/");
        assertEquals(mediaFileData.getMediaName(), "HomeMatic.mpg");
    }

    @Test
    public void testPopulationWithDownloadsFolder() {
        MissingImageMediaFileData mediaFileData = new MissingImageMediaFileData();
        distMissingImageMediaFileConverter.populate(INTERNAL_URL_WITH_DOWNLOADS_FOLDER, mediaFileData);
        assertEquals(mediaFileData.getMediaFolder(), "Downloads");
        assertEquals(mediaFileData.getMediaPath(), "zz/er/buzzer.pdf");
        assertEquals(mediaFileData.getMediaLocation(), "documents/zz/er/");
        assertEquals(mediaFileData.getMediaName(), "buzzer.pdf");
    }

    @Test
    public void testPopulationWithUnknownMediaFolder() {
        MissingImageMediaFileData mediaFileData = new MissingImageMediaFileData();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Unknown media folder: Medias");
        distMissingImageMediaFileConverter.populate(INTERNAL_URL_WITH_UNKNOWN_FOLDER, mediaFileData);
    }

    @Test
    public void testPopulationWithInvalidInternalURL() {
        MissingImageMediaFileData mediaFileData = new MissingImageMediaFileData();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid internal URL: " + INVALID_INTERNAL_URL);
        distMissingImageMediaFileConverter.populate(INVALID_INTERNAL_URL, mediaFileData);
    }
}
