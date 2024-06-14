/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.common;

import junit.framework.Assert;

import org.junit.Test;

public class PriceValueParserTest {

    private static final String PRICE_ROW_VALUE = "|CHF;Gross;100=81|CHF;Gross;10=71|CHF;Gross;1=70|CHF;Gross;2=48|CHF;Gross;Min=70|CHF;Net;Min=21|CHF;Net;100=21|CHF;Net;10=57|CHF;Net;1=21|CHF;Net;2=76|EUR;Gross;100=59|EUR;Gross;10=38|EUR;Gross;1=90|EUR;Gross;2=13|EUR;Gross;Min=90|EUR;Net;Min=45|EUR;Net;100=55|EUR;Net;10=75|EUR;Net;1=45|EUR;Net;2=54|";

    private static final String PRICE_FILTER_PATTERN = "CHF;Gross;Min";

    @Test
    public void testParsePrice() {
        Assert.assertEquals("", PriceValueParser.getPrice(PRICE_FILTER_PATTERN, "|CHF;Gross;100=81|CHF;Gross;10=71|"));
        Assert.assertEquals("", PriceValueParser.getPrice("YEN;Gross;Min", PRICE_ROW_VALUE));
        Assert.assertEquals("", PriceValueParser.getPrice("Banane;Banane;", PRICE_ROW_VALUE));
        Assert.assertEquals("70", PriceValueParser.getPrice(PRICE_FILTER_PATTERN, PRICE_ROW_VALUE));
    }

    @Test
    public void testParsePriceWithouthFilterPattern() {
        Assert.assertEquals("", PriceValueParser.getPrice("", PRICE_ROW_VALUE));
    }

    @Test
    public void testParsePriceDecimal() {
        // Init
        final String priceFilterPattern = "CHF;Net;Min";
        final String priceRowValue = "|CHF;Net;1=17.8|CHF;Net;5=16|CHF;Net;Min=17.8|";

        // Action
        final String priceValue = PriceValueParser.getPrice(priceFilterPattern, priceRowValue);

        // Evaluation
        Assert.assertEquals("Price value not as expected", "17.8", priceValue);
    }

    @Test
    public void testParsePriceLowerThan1() {
        // Init
        final String priceFilterPattern = "CHF;Net;Min";
        final String priceRowValue = "|CHF;Net;1=0.1|CHF;Net;5=.2|CHF;Net;Min=.3|";

        // Action
        final String priceValue = PriceValueParser.getPrice(priceFilterPattern, priceRowValue);

        // Evaluation
        Assert.assertEquals("Price value not as expected", ".3", priceValue);
    }

}
