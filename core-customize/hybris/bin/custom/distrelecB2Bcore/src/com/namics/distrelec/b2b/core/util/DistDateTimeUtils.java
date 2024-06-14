/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.namics.hybris.toolbox.DateTimeUtils;

public class DistDateTimeUtils extends DateTimeUtils {

    public static Date getDateAtMidnightStart() {
        return getDateAtMidnightStart(new Date());
    }

    public static Date getDateAtMidnightEnd() {
        return getDateAtMidnightEnd(new Date());
    }

    public static Date getDateAtMidnightStart(final Date date) {
        Calendar calStart = getCalender();
        calStart.setTime(date);
        calStart.set(Calendar.HOUR_OF_DAY, 0);
        calStart.set(Calendar.MINUTE, 0);
        calStart.set(Calendar.SECOND, 0);
        calStart.set(Calendar.MILLISECOND, 0);
        return calStart.getTime();
    }

    public static Date getDateAtMidnightEnd(final Date date) {
        Calendar calStart = getCalender();
        calStart.setTime(date);
        calStart.set(Calendar.HOUR_OF_DAY, 23);
        calStart.set(Calendar.MINUTE, 59);
        calStart.set(Calendar.SECOND, 59);
        calStart.set(Calendar.MILLISECOND, 999);
        return calStart.getTime();
    }

    public static boolean isDateBetweenDateRange(final Date date, final Date startDate, final Date endDate) {
        return date.after(startDate) && date.before(endDate);
    }

    /**
     * @return the Calendar instance with right time zone
     */
    protected static GregorianCalendar getCalender() {
        final GregorianCalendar calendar = new GregorianCalendar();
        // make sure the right time zone - European Central Time is selected
        calendar.setTimeZone(TimeZone.getTimeZone("ECT"));
        return calendar;
    }

    /**
     * Evo payment date format
     */
    public static String getDateForDirectDebit() {
        Date myDate = new Date();
        return new SimpleDateFormat("dd.MM.yyyy").format(myDate);

    }
}
