package com.namics.distrelec.b2b.core.util;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

/**
 * Utilities for writing db agnostic queries.
 */
public interface DistSqlUtils {

    String ORACLE_DATE_TIME_PATTERN = "YYYY-MM-DD HH24:MI:SS";

    String chr(String character);

    String concat(String... elements);

    String locate(String haystack, String needle, int startPos, int occurrences);

    /**
     * Returns an alter table sql query that increases a size of an varchar attribute.
     */
    String getIncreaseVarcharColumnQuery(AttributeDescriptorModel attrDescriptor, int newSize);

    String isNull(String expression, String defaultValue);

    String now();

    String substring(String expression, String startPos, String length);

    String replaceRegexp(String expression, String pattern, String replacement);

    String stringAgg(String expression);

    String stringAgg(String expression, String connector, String orderBy);

    String toNvarchar(String character);

    String toNvarchar(String character, String format);

    String toTimestamp(String date, String format);

    int booleanToTinyint(Boolean value);

    String where(String where);

    String length(String lengthOf);

    /**
     * Adds N prefix for mssql to correctly translate special characters so they can be correctly used in conditions.
     * Otherwise, a greek ohm character will be considered as 'O' etc.
     */
    String utf8(String stringExpression);
}
