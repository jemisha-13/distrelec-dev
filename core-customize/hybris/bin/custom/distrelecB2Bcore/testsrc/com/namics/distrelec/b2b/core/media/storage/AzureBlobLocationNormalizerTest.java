package com.namics.distrelec.b2b.core.media.storage;

import org.junit.Test;

import static de.hybris.platform.testframework.Assert.assertEquals;

public class AzureBlobLocationNormalizerTest {

    AzureBlobLocationNormalizerImpl azureBlobLocationNormalizer = new AzureBlobLocationNormalizerImpl();

    @Test
    public void testNormalizeLocation() throws Exception {
        String initialLocation = "http://127.0.0.1:10000/webshopimages/würth.jpg";

        String normalizedLocation = azureBlobLocationNormalizer.normalizeLocation(initialLocation);

        assertEquals("http://127.0.0.1:10000/webshopimages/wu%CC%88rth.jpg", normalizedLocation);
    }

    @Test
    public void testNormalizeLocationWithSpaces() throws Exception {
        String initialLocation = "http://127.0.0.1:10000/webshopimages/AQUA-STARK%20VRIDSTRÖMST%2016A.jpg";

        String normalizedLocation = azureBlobLocationNormalizer.normalizeLocation(initialLocation);

        assertEquals("http://127.0.0.1:10000/webshopimages/AQUA-STARK%20VRIDSTRO%CC%88MST%2016A.jpg", normalizedLocation);
    }

    @Test
    public void testNormalizeLocationWithPlusSign() throws Exception {
        String initialLocation = "http://127.0.0.1:10000/documents/devolo-dLAN1200+-CH-ger-tds.pdf";

        String normalizedLocation = azureBlobLocationNormalizer.normalizeLocation(initialLocation);

        assertEquals("http://127.0.0.1:10000/documents/devolo-dLAN1200+-CH-ger-tds.pdf", normalizedLocation);
    }
}
