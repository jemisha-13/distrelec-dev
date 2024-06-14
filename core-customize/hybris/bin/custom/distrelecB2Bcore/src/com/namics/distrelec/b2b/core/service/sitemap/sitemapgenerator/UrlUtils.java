/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */
 package com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class UrlUtils {

    static void checkUrl(final String url, final String baseUrl) {
        // Is there a better test to use here?
        if (!url.startsWith(baseUrl)) {
            throw new RuntimeException("Url " + url + " doesn't start with base URL " + baseUrl);
        }
    }

    static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }
    
    /**
     * Extract a query string parameter without triggering http parameters processing by the servlet container.
     *
     * @param request
     *            the request
     * @param name
     *            the parameter to get the value.
     * @return Optional of the parameter value
     */
    public static Optional<String> getParameter(final HttpServletRequest request, final String name) {
        final List<NameValuePair> list = URLEncodedUtils.parse(StringUtils.defaultString(request.getQueryString()), StandardCharsets.UTF_8);
        if (list != null) {
            for (final NameValuePair nv : list) {
                if (name.equals(nv.getName())) {
                    return Optional.ofNullable(nv.getValue());
                }
            }
        }
        return Optional.empty();
    }

}
