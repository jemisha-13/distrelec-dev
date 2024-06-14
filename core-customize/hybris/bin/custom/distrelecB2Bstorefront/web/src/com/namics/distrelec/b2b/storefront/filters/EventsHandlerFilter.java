package com.namics.distrelec.b2b.storefront.filters;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class EventsHandlerFilter extends OncePerRequestFilter {

    private static final String Origin_REQUEST_NAME = "Origin";

    private static final String DISTRELEC_PATTERN = "distrelec";

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        // YTODO Auto-generated method stub

        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Headers", "content-type");
        response.addHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,PATCH,PUT,POST,DELETE");
        response.addHeader("Access-Control-Expose-Headers",
                           "origin, content-type, accept, authorization, cache-control, if-none-match, x-anonymous-consents, occ-personalization-id, occ-personalization-time");
        if (StringUtils.isBlank(response.getHeader("Access-Control-Allow-Origin")) && request.getHeader(Origin_REQUEST_NAME) != null
                && org.apache.commons.lang3.StringUtils.containsIgnoreCase(request.getHeader(Origin_REQUEST_NAME), DISTRELEC_PATTERN)) {
            response.addHeader("Access-Control-Allow-Origin", request.getHeader(Origin_REQUEST_NAME));
        }
        filterChain.doFilter(request, response);

    }

}
