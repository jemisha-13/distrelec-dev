/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parser for fetching the value of a price row such as
 * <code>|CHF;Gross;100=81|CHF;Gross;10=71|CHF;Gross;1=70|CHF;Gross;2=48|CHF;Gross;Min=70|CHF;Net;Min=21|CHF;Net;100=21|CHF;Net;10=57|CHF;Net;1=21|CHF;Net;2=76|EUR;Gross;100=59|EUR;Gross;10=38|</code>
 * for a given currency and net/gross value.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class PriceValueParser {

    private static final Logger LOG = LoggerFactory.getLogger(PriceValueParser.class);

    /**
     * Parses the value of for the given filter value and price row.
     * 
     * @param priceFilterPattern
     *            the price filter value to look for, e.g. "EUR;Net;Min"
     * @param priceRowValue
     *            the price row to parse, e.g.
     *            <code>|CHF;Gross;100=81|CHF;Gross;10=71|CHF;Gross;1=70|CHF;Gross;2=48|CHF;Gross;Min=70|CHF;Net;Min=21|CHF;Net;100=21|CHF;Net;10=57|CHF;Net;1=21|CHF;Net;2=76|EUR;Gross;100=59|EUR;Gross;10=38|</code>
     * @return Value of for the given priceFilterValue, e.g. <code>70</code>. The empty string "" if none could be parsed.
     */
    public static String getPrice(final String priceFilterPattern, final String priceRowValue) {
        try {
            final Matcher priceMatcher = Pattern.compile("\\|" + priceFilterPattern + "=([^\\|]+)\\|").matcher(priceRowValue);
            if (priceMatcher.find()) {
                return priceMatcher.group(1);
            }
        } catch (final PatternSyntaxException e) {
            LOG.error("Failed to parse priceRow for priceFilterPattern [{}]. ", priceFilterPattern, e);
        } catch (final IllegalStateException e) {
            LOG.error("Failed to parse priceRow for priceFilterPattern [{}]. ", priceFilterPattern, e);
        }
        return StringUtils.EMPTY;
    }

}
