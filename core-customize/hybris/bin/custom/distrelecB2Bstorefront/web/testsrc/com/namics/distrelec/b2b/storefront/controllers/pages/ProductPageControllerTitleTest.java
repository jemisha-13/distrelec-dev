/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(Parameterized.class)
public class ProductPageControllerTitleTest {

    @InjectMocks
    ProductPageController productPageController;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
    }

    @Parameters
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][] {
                {"mpn1 - short title, Arduino", "short title", "mpn1", "Arduino"}, // Base case
                {"short title, Arduino", "short title", "   ", "Arduino"}, // Blank MPN
                {"short title, Arduino", "short title", null, "Arduino"}, //Null MPN
                {"mpn1 - short title", "short title", "mpn1", "   "}, //Blank ManufacturerName
                {"mpn1 - short title", "short title", "mpn1", null}, // Null Manufacturer Name
                {"short title", "short title", "   ", "   "}, // Blank MPN and ManufacturerName
                {"short title", "short title", null, null}, // Null MPN and ManufacturerName
                {"short title MPN1, Arduino", "short title MPN1", "mpn1", "Arduino"}, // shortProductTitle includes MPN (case insensitive)
                {"mpn1 - short title ARDUINO", "short title ARDUINO", "mpn1", "Arduino"}, // shortProductTitle includes ManufacturerName (case insensitive)
                {"short title MPN1 ARDUINO", "short title MPN1 ARDUINO", "mpn1", "Arduino"} // shortProductTitle includes both (case insensitive)
        });
    }

    @Parameter(0)
    public String expectedResult;

    @Parameter(1)
    public String productName;

    @Parameter(2)
    public String mpn;

    @Parameter(3)
    public String manufacturerName;

    @Test
    public void test() {
        assertEquals(expectedResult, productPageController.populateProductTitle(productName, mpn, manufacturerName));
    }

}
