/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Helper class to work with search filter params. Maybe this can be done better with ServletRequestParameterPropertyValues and an
 * InitBinder.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class WebUtil {

    private static final Logger LOG = Logger.getLogger(WebUtil.class);
    private static final String ROTATIONAL_SPEED_FILTER = "Rotational Speed";
    private static final String SUP_START = "<sup>";
    private static final String SUP_END = "</sup>";
    private static final String HYPHEN = "-";
    private static final int ZERO = 0;
    public static final String STAR = "*";
    public static final char EQUALS = '=';
    public static final char AMPERSAND = '&';

    /**
     * Return a ListMultimap containing all parameters with the given prefix. For example, with a prefix of "filter_", "filter_param1" and
     * "filter_param2" result in a ListMultimap with "param1" and "param2" as keys.
     * 
     * @param request
     *            HTTP request in which to look for parameters
     * @param prefix
     *            the beginning of parameter names (if this is null or the empty string, all parameters will match)
     * @return ListMultimap containing request parameters without the prefix
     */
    public static ListMultimap<String, String> getParamsStartingWith(final HttpServletRequest request, final String prefix) {
        final ListMultimap<String, String> filterParams = ArrayListMultimap.create();
        for (final Map.Entry<String, Object> param : WebUtils.getParametersStartingWith(request, prefix).entrySet()) {
            if (ObjectUtils.isArray(param.getValue())) {
                final String[] paramValues = (String[]) param.getValue();
                for (final String value : paramValues) {
                    filterParams.put(param.getKey(), value);
                }
            } else if (param.getValue() instanceof String) {
                filterParams.put(param.getKey(), param.getValue().toString());
            }
        }
        return filterParams;
    }

    /**
     * Return a query string containing all query parameters with the given prefix, joined by query '&' and prefixed by the given
     * queryPrefix. If no resultPrefix is defined, '*' will be used!
     * 
     * @param request
     *            HTTP request in which to look for parameters
     * @param resultPrefix
     *            string which should be placed before the parsed query parameters.
     * @param paramPrefix
     *            the beginning of parameter names (if this is null or the empty string, all parameters will match)
     * @return String containing request parameters without the prefix, joined by '&' and their key/values separated by '=',
     * 
     */
    public static String getQueryParamsStartingWith(final HttpServletRequest request, final String resultPrefix, final String paramPrefix) {
        final ListMultimap<String, String> filterParams = getParamsStartingWith(request, paramPrefix);
        final StringBuilder query = new StringBuilder();
        if (StringUtils.isBlank(resultPrefix)) {
            query.append(STAR);
        } else {
            query.append(urlEncode(resultPrefix));
        }
        for (final Map.Entry<String, String> param : filterParams.entries()) {
            query.append(AMPERSAND);
            if(isRotationalSpeedFilter(param.getKey())) {
                query.append(param.getKey()).append(EQUALS).append(reinstateHtmlForExponent(param.getValue()));
            } else {
                query.append(urlEncode(param.getKey())).append(EQUALS).append(urlEncode(param.getValue()));
            }
        }
        return query.toString();
    }

    private static String reinstateHtmlForExponent(String value) {
       final StringBuilder htmlBuilder = new StringBuilder(value.substring(ZERO, value.lastIndexOf(HYPHEN)));
       final String exponent = value.substring(value.lastIndexOf(HYPHEN), value.length());
       htmlBuilder.append(SUP_START + exponent + SUP_END);
       return htmlBuilder.toString();
    }

    public static String urlEncode(final String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            LOG.error("Could not encode text [" + text + "]", e);
            return text;
        }
    }

    private static boolean isRotationalSpeedFilter(final String key){
        return ROTATIONAL_SPEED_FILTER.equalsIgnoreCase(key);
    }
}
