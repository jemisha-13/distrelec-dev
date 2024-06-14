package com.namics.distrelec.b2b.core.security.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class IpAddressServiceImplUnitTest {

    @Spy
    private IpAddressServiceImpl ipAddressServiceImpl;

    @Test
    public void testGetClientIpAddressWithOneIpAddress() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);

        // when
        when(ipAddressServiceImpl.getRequest()).thenReturn(request);
        when(request.getHeader("X-Forwarded-By")).thenReturn("93.94.71.94");
        String ipAddress = ipAddressServiceImpl.getClientIpAddress();

        // then
        assertThat(ipAddress, equalTo("X-Forwarded-By : 93.94.71.94"));
    }

    @Test
    public void testGetClientIpAddressWithMultipleIpAddress() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);

        // when
        when(ipAddressServiceImpl.getRequest()).thenReturn(request);
        when(request.getHeader("X-Forwarded-By")).thenReturn("93.94.71.94");
        when(request.getHeader("HTTP_CLIENT_IP")).thenReturn("93.94.71.95");
        when(request.getHeader("HTTP_FORWARDED")).thenReturn("93.94.71.96");

        String ipAddress = ipAddressServiceImpl.getClientIpAddress();

        // then
        assertThat(ipAddress, equalTo("X-Forwarded-By : 93.94.71.94 | HTTP_CLIENT_IP : 93.94.71.95 | HTTP_FORWARDED : 93.94.71.96"));
    }

    @Test
    public void testGetClientIpAddressWhenRequestIsNull() {
        // when
        String ipAddress = ipAddressServiceImpl.getClientIpAddress();

        // then
        assertThat(ipAddress, is(StringUtils.EMPTY));
    }
}
