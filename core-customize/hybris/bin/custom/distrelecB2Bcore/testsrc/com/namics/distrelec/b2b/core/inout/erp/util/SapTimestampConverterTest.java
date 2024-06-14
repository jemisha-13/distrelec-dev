/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

/**
 * Test the timestamp conversion methods in {@link SoapConversionHelper}.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 * 
 */
public class SapTimestampConverterTest {

    @Test
    public void testSapFormatToDate() {
        // init
        final BigInteger bigInt = BigInteger.valueOf(20131231235930l);

        // action
        final Date date = SoapConversionHelper.convertTimestampFromSap(bigInt);
        // evaluation
        assertNotNull("date is null", date);

        final Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTime(date);
        assertEquals("year not converted correctly", 2013, cal.get(Calendar.YEAR));
        assertEquals("month not converted correctly", 12, cal.get(Calendar.MONTH) + 1);
        assertEquals("day not converted correctly", 31, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals("hour not converted correctly", 23, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals("minute not converted correctly", 59, cal.get(Calendar.MINUTE));
        assertEquals("second not converted correctly", 30, cal.get(Calendar.SECOND));
    }

    @Test
    public void testDateToSapFormat() {
        // init
        final Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2013, 12 - 1, 31, 23, 59, 30);
        final Date date = cal.getTime();
        // action
        final BigInteger bigInt = SoapConversionHelper.convertTimestampToSap(date);
        // evaluation
        assertNotNull("bigInt is null", bigInt);
        assertEquals("bigInt not converted correctly", BigInteger.valueOf(20131231235930l), bigInt);
    }
}
