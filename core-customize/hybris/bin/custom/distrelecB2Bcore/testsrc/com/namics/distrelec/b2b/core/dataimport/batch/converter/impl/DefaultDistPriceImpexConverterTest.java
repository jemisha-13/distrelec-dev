package com.namics.distrelec.b2b.core.dataimport.batch.converter.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;

import static de.hybris.platform.testframework.Assert.assertEquals;

@UnitTest
public class DefaultDistPriceImpexConverterTest {

    @Test()
    public void testZeroPriceRowPattern() {
        Map<String, Boolean> matrix = new LinkedHashMap<>();
        matrix.put("", true);
        matrix.put("0", true);
        matrix.put("0000", true);
        matrix.put("0000.0", true);
        matrix.put("1.3", false);

        matrix.forEach((input, result) -> assertEquals(DefaultDistPriceImpexConverter.PRICE_PATTERN.matcher(input).matches(), result));

    }
}
