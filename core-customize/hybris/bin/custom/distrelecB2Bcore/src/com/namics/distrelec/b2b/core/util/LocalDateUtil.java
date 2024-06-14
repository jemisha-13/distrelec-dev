package com.namics.distrelec.b2b.core.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class LocalDateUtil {

    private static final ZoneId defaultZoneId;

    static {
        defaultZoneId = ZoneId.systemDefault();

    }

    public static Date convertLocalDateToDate(LocalDate date) {
        return Date.from(date.atStartOfDay(defaultZoneId).toInstant());
    }

    public static LocalDate convertDateToLocalDate(Date date) {
        return date.toInstant().atZone(defaultZoneId).toLocalDate();
    }

    public static LocalDateTime convertDateToLocalDateTime(Date date) {
        return date.toInstant().atZone(defaultZoneId).toLocalDateTime();
    }

    public static LocalDate skipWeekendToNextWorkingDate(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            return date.plusDays(2);
        }
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return date.plusDays(1);
        }
        return date;
    }

    public static Date skipWeekendToNextWorkingDateAndConvertToDate(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            return convertLocalDateToDate(date.plusDays(2));
        }
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return convertLocalDateToDate(date.plusDays(1));
        }
        return convertLocalDateToDate(date);
    }
}
