package com.namics.distrelec.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.DefaultCorsProcessor;

public class DefaultOccCorsProcessor extends DefaultCorsProcessor {

    static final int MAX_AGE_TTL = 10800;

    private static final int STALE_WHILE_REVALIDATE_TTL = 86400;

    static final String CACHE_CONTROL_HEADER = "Cache-Control";

    static final String CACHE_CONTROL_VALUE = "public, max-age=" + MAX_AGE_TTL + ", stale-while-revalidate=" + STALE_WHILE_REVALIDATE_TTL
                                              + ", stale-if-error=0";

    @Override
    protected void rejectRequest(ServerHttpResponse response) throws IOException {
        // append null access control allow origin
        HttpHeaders responseHeaders = response.getHeaders();
        responseHeaders.setAccessControlAllowOrigin("null");

        superRejectRequest(response);

        // response wrapper is expected
        ServerHttpResponseWrapper responseWrapper = (ServerHttpResponseWrapper) response;
        flush(responseWrapper);
    }

    @Override
    protected boolean handleInternal(ServerHttpRequest request, ServerHttpResponse response, CorsConfiguration config,
                                     boolean preFlightRequest) throws IOException {
        ServerHttpResponseWrapper responseWrapper = new ServerHttpResponseWrapper(response);

        boolean isAllowed = superHandleInternal(request, responseWrapper, config, preFlightRequest);

        if (isAllowed) {
            if (preFlightRequest) {
                // cache responses of preflight requests
                HttpHeaders responseHeaders = response.getHeaders();
                responseHeaders.set(CACHE_CONTROL_HEADER, CACHE_CONTROL_VALUE);
                responseHeaders.setAccessControlMaxAge(MAX_AGE_TTL);
            }

            flush(responseWrapper);
        }

        return isAllowed;
    }

    private void flush(ServerHttpResponseWrapper responseWrapper) throws IOException {
        responseWrapper.flushDelegate();
    }

    /**
     * Returns true if the request is allowed. The request could be either a full or a preflight.
     * <p>
     * It is created to be mocked and used by unit tests.
     */
    boolean superHandleInternal(ServerHttpRequest request, ServerHttpResponse response, CorsConfiguration config,
                                boolean preFlightRequest) throws IOException {
        return super.handleInternal(request, response, config, preFlightRequest);
    }

    /**
     * It is created to be mocked and used by unit tests.
     */
    void superRejectRequest(ServerHttpResponse response) throws IOException {
        super.rejectRequest(response);
    }
}
