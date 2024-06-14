/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.util;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;

public class CookieUtils {

    /**
     * Iterates through the given array of cookies and returns the one with the same name as cookieName. If no cookie with the same name was
     * found, null will be returned.
     * 
     * @param cookies
     * @param cookieName
     * @return Cookie with name same name as cookieName
     */
    public static Cookie getCookieForName(final Cookie[] cookies, final String cookieName) {
        if (ArrayUtils.isNotEmpty(cookies)) {
            for (int n = 0; n < cookies.length; n++) {
                if (StringUtils.equals(cookieName, cookies[n].getName())) {
                    return cookies[n];
                }
            }
        }
        return null;
    }

}
