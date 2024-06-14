package com.namics.distrelec.b2b.core.inout.erp.impl;

import static org.junit.Assert.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ErpErrorHandlerServiceImplTest {

    private ErpErrorHandlerServiceImpl sapErrorHandlerService = new ErpErrorHandlerServiceImpl();

    @Test
    public void testIsProductStatusMisalignmentExceptionReturnTrue() {
        String message = "SAP ERR:V1,028 :Material 302-04-687 has status: Inactive Our Decis";

        boolean result = sapErrorHandlerService.isProductStatusMisalignmentException(message);

        assertTrue(result);
    }

    @Test
    public void testIsProductStatusMisalignmentExceptionReturnFalse() {
        String message = "SAP ERR:V1,028 :Material 302-04-687 has status: Inactive Our Account";

        boolean result = sapErrorHandlerService.isProductStatusMisalignmentException(message);

        assertFalse(result);
    }

    @Test
    public void testExtractProductCodeFromMessageProductCodeExtracted() {
        String message = "SAP ERR:V1,028 :Material 302-04-687 has status: Inactive Our Decis";

        String result = sapErrorHandlerService.extractProductCodeFromMessage(message);

        assertEquals("30204687", result);
    }

    @Test
    public void testExtractProductCodeFromMessageProductCodeNotExist() {
        String message = "SAP ERR:V1,028 :Material code has status: Inactive Our Decis";

        String result = sapErrorHandlerService.extractProductCodeFromMessage(message);

        assertEquals(StringUtils.EMPTY, result);
    }

    @Test
    public void testExtractProductCodeWhenTemporaryQBlockExceptionAndProductCodeExist() {
        String message = "SAP ERR:V1,028 :Material 302-03-250 has status: Temporary Q Block";

        String result = sapErrorHandlerService.extractProductCodeFromMessage(message);

        assertEquals("30203250", result);
    }

    @Test
    public void testExtractProductCodeWhenTemporaryQBlockExceptionAndProductCodeNotExist() {
        String message = "SAP ERR:V1,028 :Material has status: Temporary Q Block";

        String result = sapErrorHandlerService.extractProductCodeFromMessage(message);

        assertEquals(StringUtils.EMPTY, result);
    }

    @Test
    public void testIsTemporaryQBlockExceptionReturnTrue() {
        String message = "SAP ERR:V1,028 :Material 302-03-250 has status: Temporary Q Block";

        boolean result = sapErrorHandlerService.isTemporaryQualityBlockException(message);

        assertTrue(result);
    }

    @Test
    public void testIsTemporaryQBlockExceptionReturnFalse() {
        String message = "SAP ERR:V1,028 :Material 302-03-250 has status: Temporary Q Response";

        boolean result = sapErrorHandlerService.isTemporaryQualityBlockException(message);

        assertFalse(result);
    }
}
