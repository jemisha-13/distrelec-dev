/*
 * Copyright 2000-2009 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.Assert;

import org.junit.Test;

public class DateTimeUtilsTest {

    @Test
    public void testGetTodaysMidnightPlus1Minute() {
        final GregorianCalendar cal = DateTimeUtils.getTodaysMidnightPlus1MinuteAsCalender();
        Assert.assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(1, cal.get(Calendar.MINUTE));
        // don't know how to test the date without using the Calendar class again.
        // However, this function is trivial, test can be activated manually
        // Assert.assertEquals(3, cal.get(Calendar.DAY_OF_MONTH));
        // Assert.assertEquals(10, cal.get(Calendar.MONTH));
        // Assert.assertEquals(2009, cal.get(Calendar.YEAR));

    }

}
