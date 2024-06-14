package com.namics.distrelec.b2b.storefront.security.filters;

import com.namics.distrelec.b2b.storefront.security.wrappers.DistXSSRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DistXSSWrapperFilter implements Filter {

    private static final String URLS_EXCLUSION_INIT_PARAM_NAME = "excludedURLs";
    private List<Pattern> excludedURLs;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.excludedURLs = filterConfig.getInitParameter(URLS_EXCLUSION_INIT_PARAM_NAME) == null ? Collections.emptyList()
                : Stream.of(filterConfig.getInitParameter(URLS_EXCLUSION_INIT_PARAM_NAME).split(";")).map(regex -> Pattern.compile(regex))
                .collect(Collectors.toList());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            if (!skip(request)) {
                request = new DistXSSRequestWrapper((HttpServletRequest) request);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
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
