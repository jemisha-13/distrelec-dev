/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author datneerajs, Namics AG
 * @since Distrelec 1.1 DISTRELEC-9400
 */
public class SupplierleadTimeUtils {

    public static int getSupplierleadTime(final int leadTimeErp) {

        if (leadTimeErp == 0)
            return 0;

        // Getting today's calendar
        final Calendar calendarToday = Calendar.getInstance(TimeZone.getDefault());

        // Getting ERP Lead time and multiplying it with 1.1days as standard lead time increase
        int adjustedLeadtime = (int) Math.round(leadTimeErp * 1.1);

        // Adding adjusted lead time to the adjusted calendar
        final Calendar calendarAdjusted = Calendar.getInstance(TimeZone.getDefault());
        calendarAdjusted.add(Calendar.DAY_OF_MONTH, adjustedLeadtime);

        if (calendarAdjusted.get(Calendar.DAY_OF_WEEK) > 2) {
            calendarAdjusted.add(Calendar.DAY_OF_MONTH, 7);
        }

        // Neeraj : commented as not returning correct week difference
        // Weeks.weeksBetween(new DateTime(calendarToday.getTime()), new DateTime(calendarAdjusted.getTime())).getWeeks();

        int diff = 0; // Returning number of weeks as lead time
        
        if (calendarToday.getWeekYear() == calendarAdjusted.getWeekYear()) {// means same year
            diff = calendarAdjusted.get(Calendar.WEEK_OF_YEAR) - calendarToday.get(Calendar.WEEK_OF_YEAR);
        } else { // means year has been changed
            diff = calendarToday.getWeeksInWeekYear() - calendarToday.get(Calendar.WEEK_OF_YEAR) + calendarAdjusted.get(Calendar.WEEK_OF_YEAR);
        }

        return diff;
    }

}
