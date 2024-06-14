/*
 * Copyright 2000-2009 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * DateTimeUtils.
 * 
 * @author mbaumgartner, namics ag
 * @since MGB PIM 1.0
 * 
 *        Special Time formats Can be used for, but not tested in, Cronjobs
 * 
 *        REVIEW AScherrer move DateUtils of testsrc to here to have one class?
 * 
 */
public class DateTimeUtils {
    public static SortedMap<String, String> monthMap = new TreeMap<String, String>();
    public static SortedMap<String, String> dayMap = new TreeMap<String, String>();

    static {
        monthMap.put("01", "Januar");
        monthMap.put("02", "Februar");
        monthMap.put("03", "März");
        monthMap.put("04", "April");
        monthMap.put("05", "Mai");
        monthMap.put("06", "Juni");
        monthMap.put("07", "Juli");
        monthMap.put("08", "August");
        monthMap.put("09", "September");
        monthMap.put("10", "Oktober");
        monthMap.put("11", "November");
        monthMap.put("12", "Dezember");
        dayMap.put("01", "01");
        dayMap.put("02", "02");
        dayMap.put("03", "03");
        dayMap.put("04", "04");
        dayMap.put("05", "05");
        dayMap.put("06", "06");
        dayMap.put("07", "07");
        dayMap.put("08", "08");
        dayMap.put("09", "09");
        dayMap.put("10", "10");
        dayMap.put("11", "11");
        dayMap.put("12", "12");
        dayMap.put("13", "13");
        dayMap.put("14", "14");
        dayMap.put("15", "15");
        dayMap.put("16", "16");
        dayMap.put("17", "17");
        dayMap.put("18", "18");
        dayMap.put("19", "19");
        dayMap.put("20", "20");
        dayMap.put("21", "21");
        dayMap.put("22", "22");
        dayMap.put("23", "23");
        dayMap.put("24", "24");
        dayMap.put("25", "25");
        dayMap.put("26", "26");
        dayMap.put("27", "27");
        dayMap.put("28", "28");
        dayMap.put("29", "29");
        dayMap.put("30", "30");
        dayMap.put("31", "31");

    }

    /**
     * @return 00:01 of the current date as Gregorian Calendar
     */
    public static GregorianCalendar getTodaysMidnightPlus1MinuteAsCalender() {
        final GregorianCalendar calendar = new GregorianCalendar();
        // make sure the right time zone - European Central Time is selected
        calendar.setTimeZone(TimeZone.getTimeZone("ECT"));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 1);
        return calendar;
    }

    /**
     * @return 00:01 of the current date as Date
     */
    public static Date getTodaysMidnightPlus1MinuteAsDate() {
        return getTodaysMidnightPlus1MinuteAsCalender().getTime();
    }

    /**
     * Vergleicht zwei Daten und gibt true zur�ck, wenn beiden das gleiche Datum haben oder beide <code>null</code> sind.
     */
    public static boolean isEqualsDateNullSave(final Date date1, final Date date2) {
        if (date1 == null & date2 == null) {
            // Beide null
            return true;
        }
        if (date1 != null && date2 != null) {
            // Beide nicht null
            return date1.equals(date2);
        } else {
            // Eines von beiden null
            return false;
        }

    }

    /**
     * Generiert ein Datum aufgrund desübergebenen Datums und der Sprachangabe.
     */
    public static String generateDateString(final Date date, final int languageId) {
        final DateFormat formater = DateFormat.getDateInstance(DateFormat.MEDIUM, new Locale("de", "CH"));
        final String dateAsString = formater.format(date);

        return dateAsString;
    }

    /**
     * Generates a date string representation from time now, like "200912310400987".
     */
    public static String createDateString() {
        final Date now = new Date();
        final DateFormat format = new SimpleDateFormat("yyyyMMddHHmmssS");
        return format.format(now);
    }

    /**
     * Converts a number to string and fills it with prepended zero values '0'<br>
     * 
     * <pre>
     * 3  => 0003
     * 23 => 0024
     * </pre>
     * 
     * @param number
     *            The number to convert
     * @param digits
     *            How many digits the converted number has as result.
     * @return The number as string, like '004'
     */
    public static String convertNumber(final int number, final int digits) {
        String lineNumber = String.valueOf(number);
        // Fill with prepended zero values '0'
        // 3 => 0003
        // 23 => 0024
        for (int i = lineNumber.length(); i < digits; i++) {
            lineNumber = "0" + lineNumber;
        }
        return lineNumber;
    }
}
