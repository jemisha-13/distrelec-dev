package com.namics.distrelec.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.cors.CorsConfiguration;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class DefaultOccCorsProcessorTest {

    @Spy
    DefaultOccCorsProcessor processor;

    @Mock
    ServerHttpRequest request;

    @Mock
    ServerHttpResponse response;

    @Mock
    CorsConfiguration config;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddsCacheHeadersForAllowedPreflight() {
        boolean allowed = true;
        boolean preFlightRequest = true;

        assertResponseIsCached(() -> {
            boolean actualAllowed = processor.handleInternal(request, response, config, preFlightRequest);

            assertTrue(actualAllowed);
        }, allowed, preFlightRequest);
    }

    @Test
    public void testNotAddCacheHeadersForAllowedNotPreflight() {
        boolean allowed = true;
        boolean preFlightRequest = false;

        assertResponseIsNotCached(() -> {
            boolean actualAllowed = processor.handleInternal(request, response, config, preFlightRequest);

            assertTrue(actualAllowed);
        }, allowed, preFlightRequest);
    }

    @Test
    public void testNotAddCacheHeadersForBlockedPreflight() {
        boolean allowed = false;
        boolean preFlightRequest = true;

        assertResponseIsNotCached(() -> {
            boolean actualAllowed = processor.handleInternal(request, response, config, preFlightRequest);

            assertFalse(actualAllowed);
        }, allowed, preFlightRequest);
    }

    @Test
    public void testNotAddCacheHeadersForBlockedNotPreflight() {
        boolean allowed = false;
        boolean preFlightRequest = false;

        assertResponseIsNotCached(() -> {
            boolean actualAllowed = processor.handleInternal(request, response, config, preFlightRequest);

            assertFalse(actualAllowed);
        }, allowed, preFlightRequest);
    }

    @Test
    public void testSetsNullAccessControlAllowOriginOnRejectRequest() throws Exception {
        HttpHeaders responseHeaders = mock(HttpHeaders.class);
        ServerHttpResponseWrapper responseWrapper = mock(ServerHttpResponseWrapper.class);

        doNothing().when(processor).superRejectRequest(responseWrapper);
        when(responseWrapper.getHeaders()).thenReturn(responseHeaders);

        processor.rejectRequest(responseWrapper);

        verify(responseHeaders).setAccessControlAllowOrigin("null");
        verify(responseWrapper).flushDelegate();
    }

    private void assertResponseIsCached(ProcessorCacheResponseTestCase testCase, boolean allowed, boolean preflight) {
        assertResponseCached(testCase, true, allowed, preflight);
    }

    private void assertResponseIsNotCached(ProcessorCacheResponseTestCase testCase, boolean allowed, boolean preflight) {
        assertResponseCached(testCase, false, allowed, preflight);
    }

    private void assertResponseCached(ProcessorCacheResponseTestCase testCase, boolean shouldBeCached, boolean allowed, boolean preflight) {
        HttpHeaders httpHeaders = shouldBeCached ? mockHttpHeaders() : null;

        try {
            doReturn(allowed).when(processor).superHandleInternal(eq(request), argThat(new WrappedResponseArgMatcher(response)), eq(config), eq(preflight));

            testCase.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (shouldBeCached) {
            verify(httpHeaders).set(DefaultOccCorsProcessor.CACHE_CONTROL_HEADER, DefaultOccCorsProcessor.CACHE_CONTROL_VALUE);
            verify(httpHeaders).setAccessControlMaxAge(DefaultOccCorsProcessor.MAX_AGE_TTL);
        } else {
            verify(response, never()).getHeaders();
        }
    }

    private HttpHeaders mockHttpHeaders() {
        HttpHeaders httpHeaders = mock(HttpHeaders.class);
        when(response.getHeaders()).thenReturn(httpHeaders);
        return httpHeaders;
    }

    private interface ProcessorCacheResponseTestCase {
        void execute() throws Exception;
    }

    private class WrappedResponseArgMatcher implements ArgumentMatcher<ServerHttpResponse> {

        private ServerHttpResponse expectedResponse;

        WrappedResponseArgMatcher(ServerHttpResponse expectedResponse) {
            this.expectedResponse = expectedResponse;
        }

        @Override
        public boolean matches(ServerHttpResponse object) {
            if (object instanceof ServerHttpResponseWrapper) {
                ServerHttpResponseWrapper responseWrapper = (ServerHttpResponseWrapper) object;
                ServerHttpResponse wrappedResponse = responseWrapper.getDelegate();
                return wrappedResponse == expectedResponse;
            }
            return false;
        }
    }
}
