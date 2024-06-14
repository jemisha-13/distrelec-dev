/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.hybris.ffsearch.util;

import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.owasp.encoder.Encode;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Filters given string to prevent cross-site scripting
 */
public class XSSFilterUtil {


    private static List<Pattern> xssPatterns;

    static{
        final ConfigurationService configService = Registry.getApplicationContext().getBean("configurationService", ConfigurationService.class);
        List<String> configProps = new ArrayList<>();
        Iterator<String> configKeys = configService.getConfiguration().getKeys("xss.filter.rule");
        configKeys.forEachRemaining(configProps::add);
        Iterator<String> storefrontConfigKeys = configService.getConfiguration().getKeys("distrelecB2Bstorefront.xss.filter.rule");
        storefrontConfigKeys.forEachRemaining(configProps::add);
        xssPatterns = configProps.stream().map(p -> Pattern.compile(configService.getConfiguration().getString(p))).collect(Collectors.toList());
    }

    /**
     * Filters for xss.
     * 
     * @param value
     *            to be sanitized
     * @return sanitized content
     */
    public static String filter(final String value) {


        if (value == null) {
            return null;
        }
        String sanitized = value;
        sanitized = sanitized.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        sanitized = sanitized.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        sanitized = sanitized.replaceAll("'", "&#39;");
        sanitized = sanitized.replaceAll(",", "&#44;");
        sanitized = sanitized.replaceAll("\"", "&quot;");
        sanitized = sanitized.replaceAll("eval\\((.*)\\)", "");
        sanitized = sanitized.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        return sanitized;
    }

    public static String filterUsingRules(final String value) {

        String cleanValue = null;
        if (value != null) {
            cleanValue = Normalizer.normalize(value, Normalizer.Form.NFD);

            for (Pattern xssPattern : xssPatterns) {
                cleanValue = xssPattern.matcher(cleanValue).replaceAll("");
            }
        }
        String encodedCleanValue = Encode.forHtmlContent(cleanValue);
        return encodedCleanValue;
    }

    public static String filterParameter(final String originalValue) {
        if (originalValue != null) {
            // remove heading and leading double quotes
            String cleanValue = originalValue.replaceAll("^\"+(.*)\"+$", "$1");
            return cleanValue;
        }
        return originalValue;
    }

    /**
     * Filters for xss for search
     * 
     * @param value
     *            to be sanitized
     * @return sanitized content
     */
    public static String filterForSearch(final String value) {
        if (value == null) {
            return null;
        }
        String sanitized = value;
        sanitized = sanitized.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        // DISTRELEC-6864
        // sanitized = sanitized.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        sanitized = sanitized.replaceAll("eval\\((.*)\\)", "");
        sanitized = sanitized.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        return sanitized;
    }

    public static String sanitiseQueryString(final HttpServletRequest httpServletRequest) {
        String decodedQueryString;

        try {
            decodedQueryString = URLDecoder.decode(httpServletRequest.getQueryString(), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // We shall ignore this error
            decodedQueryString = httpServletRequest.getQueryString();
        }

        String encodedQueryString = Encode.forHtml(decodedQueryString);

        return encodedQueryString;
    }

    public static String filterQueryString(final HttpServletRequest request) {
        if (null != request.getParameterMap()) {
            StringBuilder qsBuilder = new StringBuilder();
            boolean isFirstParam = true;

            for (final String paramName : request.getParameterMap().keySet()) {
                String encodedParamName = Encode.forUriComponent(paramName);
                if (isFirstParam) {
                    isFirstParam = false;
                } else {
                    qsBuilder.append("&");
                }
                qsBuilder.append(encodedParamName).append("=");
                String[] paramValues = request.getParameterValues(paramName);
                if ( null != paramValues && paramValues.length > 0) {
                    boolean isFirstValue = true;
                    for (String paramValue : paramValues) {
                        String encodedParamValue = Encode.forUriComponent(paramValue);
                        if (isFirstValue) {
                            isFirstValue = false;
                        } else {
                            qsBuilder.append("&").append(encodedParamName).append("=");
                        }
                        qsBuilder.append(encodedParamValue);
                    }
                }
            }

            return qsBuilder.toString();
        }
        return null;
    }
}
