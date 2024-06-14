package com.namics.distrelec.security;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpResponse;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class ServerHttpResponseWrapperTest {

    @InjectMocks
    ServerHttpResponseWrapper responseWrapper;

    @Mock
    ServerHttpResponse delegate;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDoNotFlushDelegateOnFlushMethod() throws Exception {
        responseWrapper.flush();

        verify(delegate, never()).flush();
    }

    @Test
    public void testFlushDelegateOnFlushDelegateMethod() throws Exception {
        responseWrapper.flushDelegate();

        verify(delegate).flush();
    }

    @Test
    public void testGetHeadersFromDelegate() {
        HttpHeaders httpHeaders = mock(HttpHeaders.class);
        when(delegate.getHeaders()).thenReturn(httpHeaders);

        HttpHeaders actualHttpHeaders = responseWrapper.getHeaders();
        assertEquals(httpHeaders, actualHttpHeaders);
    }
}
