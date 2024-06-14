/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.storefront.util;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * {@code URLUtil}
 * 
 *
 * @author Santosh De-Massari, foryouandyourcustomers
 * @since Distrelec 5.10
 */
public class URLUtil {

    public static String mergeQueryStrings(String... vargs) {
        ArrayList normalizedQs = new ArrayList<String>();
        for (String q : vargs) {
            String normalizedQueryString = normalizeQueryString(q);
            if(StringUtils.isNotEmpty(normalizedQueryString)) {
                normalizedQs.add(normalizeQueryString(q));
            }
        }
        return "?" + StringUtils.join(normalizedQs, "&");
    }

    private final static String normalizeQueryString(String q) {
        if (q == null || q.isEmpty()) {
            return "";
        }
        if (q.startsWith("?") || q.startsWith("&")) {
            q = q.substring(1);
        }
        if (q.endsWith("?") || q.endsWith("&")) {
            q = q.substring(0, q.length() - 1);
        }
        return q;
    }
}
