package com.namics.distrelec.b2b.facades.legal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.facades.legal.impl.DefaultDistQualityAndLegalFacade;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDistQualityAndLegalFacadeTest {

    @InjectMocks
    private DefaultDistQualityAndLegalFacade distQualityAndLegalFacade;

    @Mock
    private DistQualityAndLegalParser distQualityAndLegalParser;

    @Mock
    private DistProductService productService;

    private static final String file = "file";

    private static final String validCode = "30326080";

    private static final List<String> rawCodes = Arrays.asList("test1", "303-26-080", "303-30-090", "123-ab");

    private static final List<String> invalidCodes = Arrays.asList("test1", "123-ab");

    private static final List<String> existingCodes = Arrays.asList("30326080", "30330090");

    @Before
    public void setup() throws IOException, DistQualityAndLegalInvalidFileUploadException, SQLException {
        when(distQualityAndLegalParser.getProductCodesFromFile(file)).thenReturn(rawCodes);
        when(productService.findExistingProductCodes(existingCodes)).thenReturn(existingCodes);
    }

    @Test
    public void testGetProductCodes() throws IOException, DistQualityAndLegalInvalidFileUploadException {
        assertEquals(distQualityAndLegalFacade.getProductCodes(file), rawCodes);
    }

    @Test
    public void testFindExistingProductCodes() throws DistQualityAndLegalInvalidFileUploadException {
        assertEquals(distQualityAndLegalFacade.findExistingProductCodes(existingCodes), existingCodes);
    }

    @Test
    public void testFilterInvalidProductCodes() {
        assertEquals(distQualityAndLegalFacade.filterInvalidProductCodes(rawCodes, existingCodes), invalidCodes);
    }

    @Test
    public void testCleanup() {
        assertEquals(distQualityAndLegalFacade.cleanup(rawCodes.get(1)), validCode);
    }
}
