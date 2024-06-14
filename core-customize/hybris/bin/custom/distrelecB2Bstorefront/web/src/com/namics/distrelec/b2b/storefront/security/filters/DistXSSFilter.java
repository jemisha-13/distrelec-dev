/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.storefront.security.filters;

import de.hybris.platform.servicelayer.web.XSSFilter;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@code DistXSSFilter}
 * <p>
 * Custom XSS filter that support exclusion rules. If the requested URL matches at least one of the exclusion rules, then, the default XSS
 * filter behavior is skipped.<br/>
 * The exclusion rules must be defined in the web.xml as "init-param" with name excludedURLs<br/>
 * </p>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.0
 */
public class DistXSSFilter extends XSSFilter {

    protected static Logger LOG = Logger.getLogger(DistXSSFilter.class);

    private static final String URLS_EXCLUSION_INIT_PARAM_NAME = "excludedURLs";
    private List<Pattern> excludedURLs;

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.web.XSSFilter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        this.excludedURLs = filterConfig.getInitParameter(URLS_EXCLUSION_INIT_PARAM_NAME) == null ? Collections.emptyList()
                : Stream.of(filterConfig.getInitParameter(URLS_EXCLUSION_INIT_PARAM_NAME).split(";")).map(regex -> Pattern.compile(regex))
                        .collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.web.XSSFilter#initPatternsAndHeaders(boolean, java.util.Map, java.util.Map)
     */
    @Override
    protected void initPatternsAndHeaders(final boolean enabled, final Map<String, String> patternDefinitions, final Map<String, String> headers) {
        if (MapUtils.isNotEmpty(headers)) {
            headers.entrySet().stream().filter(entry -> StringUtils.isBlank(entry.getValue())).forEach(entry -> headers.remove(entry.getKey()));
        }
        super.initPatternsAndHeaders(enabled, patternDefinitions, headers);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.web.XSSFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse,
     * javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        if (skip(request)) {
            filterChain.doFilter(request, response);
        } else {
            super.doFilter(request, response, filterChain);
        }
    }

    /**
     * Checks whether or not we should skip the provided HTTP request.
     * 
     * @param httpRequest
     *            the HTTP request to check
     * @return {@code true} if the HTTP request should be skipped, {@code false} otherwise.
     */
    private boolean skip(final ServletRequest httpRequest) {
        return requestURLMatches(getHttpRequestURL(httpRequest));
    }

    /**
     * 
     * @param httpRequestURL
     * @return {@code true} if the HTTP request URL should be skipped, {@code false} otherwise.
     */
    private boolean requestURLMatches(final String httpRequestURL) {
        return httpRequestURL != null && !excludedURLs.isEmpty() && excludedURLs.stream().anyMatch(pattern -> pattern.matcher(httpRequestURL).matches());
    }

    /**
     * Retrieve the HTTP request URL from the {@link HttpServletRequest}.
     * 
     * @param request
     *            the source {@link HttpServletRequest}.
     * @return the HTTP request URL if the source request is an instance of {@link HttpServletRequest}, otherwise {@code null}
     */
    private String getHttpRequestURL(final ServletRequest request) {
        return (request instanceof HttpServletRequest) ? (((HttpServletRequest) request).getRequestURL().toString()) : null;
    }
}
