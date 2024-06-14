/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.category.converter;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@UnitTest
@RunWith(Parameterized.class)
public class CategoryDisplayDataConverterTest {

    @Parameter(0)
    public String expectedQueryStringWithoutParameter;

    @Parameter(1)
    public String inputQueryString;

    @Parameter(2)
    public String parameterNameToRemove;

    @Parameters
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][] {
                {"middle=m1&last=l1", "first=f1&middle=m1&last=l1", "first"}, 
                {"first=f1&last=l1", "first=f1&middle=m1&last=l1", "middle"}, 
                {"first=f1&middle=m1", "first=f1&middle=m1&last=l1", "last"},
                {"first=f1&middle=m1&last=l1", "first=f1&middle=m1&last=l1", "other"}
        });
    }

    @Test
    public void test() {
        final String actualQueryStringWithoutParameter = CategoryDisplayDataConverter.removeParameterFromQueryString(inputQueryString, parameterNameToRemove);
        assertEquals(expectedQueryStringWithoutParameter, actualQueryStringWithoutParameter);
    }
}
