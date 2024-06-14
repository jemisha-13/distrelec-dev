package com.namics.distrelec.b2b.core.interceptor;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@UnitTest
public class DistProductTypeNameNormalizedPrepareInterceptorTest {

    DistProductTypeNameNormalizedPrepareInterceptor prepareInterceptor;

    @Before
    public void setUp() {
        prepareInterceptor = new DistProductTypeNameNormalizedPrepareInterceptor();
    }

    @Test
    public void testNormalizeTypeName() {
        String typeName = " AB.C/123; ";
        String expectedTypeNameNormalized = "ABC123";

        String typeNameNormalized = prepareInterceptor.normalizeTypeName(typeName);

        assertEquals(expectedTypeNameNormalized, typeNameNormalized);
    }

    @Test
    public void testNormalizeNullTypeName() {
        String typeNameNormalized = prepareInterceptor.normalizeTypeName(null);

        assertNull(typeNameNormalized);
    }
}
