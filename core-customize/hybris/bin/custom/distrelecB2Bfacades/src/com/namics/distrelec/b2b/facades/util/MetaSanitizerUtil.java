package com.namics.distrelec.b2b.facades.util;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Punctuation.DOUBLE_QUOTES;

/**
 * 
 * Utility class for sanitizing up content that will appear in HTML meta tags.
 * 
 */
public class MetaSanitizerUtil {

    /**
     * Removes all HTML tags and double quotes and returns a String
     * 
     * @param s
     *            String to be sanitized
     * @return String object
     */
    public static String sanitize(final String s) {
        if (StringUtils.isNotBlank(s)) {
            final String clean = Jsoup.parse(s).text();
            return clean.replace(DOUBLE_QUOTES, "&quot;");
        } else {
            return StringUtils.EMPTY;
        }
    }
}
