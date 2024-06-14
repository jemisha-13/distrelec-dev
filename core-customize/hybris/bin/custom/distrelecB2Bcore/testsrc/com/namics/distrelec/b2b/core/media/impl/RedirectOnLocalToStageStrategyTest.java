package com.namics.distrelec.b2b.core.media.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Environment;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RedirectOnLocalToStageStrategyTest {

    @InjectMocks
    private RedirectOnLocalToStageStrategy redirectOnLocalToStageStrategy;

    @Mock
    private Configuration config;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private HttpServletResponse httpResponse;

    @Before
    public void setUp() {
        when(configurationService.getConfiguration()).thenReturn(config);
    }

    @Test
    public void testRedirectIfLocal() throws Exception {
        String requestUri = "/requestUri";
        String queryString = "query=string";

        when(config.getString(Environment.ENVIRONMENT_KEY)).thenReturn(Environment.LOCAL_ENV_DEVELOPMENT);
        when(httpRequest.getRequestURI()).thenReturn(requestUri);
        when(httpRequest.getQueryString()).thenReturn(queryString);

        boolean isRedirected = redirectOnLocalToStageStrategy.redirectIfLocal(httpRequest, httpResponse);

        assertTrue(isRedirected);
        String expectedRedirect = Environment.TEST_MEDIA_DOMAIN_HTTPS + requestUri + "?" + queryString;
        verify(httpResponse).sendRedirect(expectedRedirect);
    }

    @Test
    public void testNotRedirectIfNotLocal() throws Exception {
        boolean isRedirected = redirectOnLocalToStageStrategy.redirectIfLocal(httpRequest, httpResponse);

        assertFalse(isRedirected);
        verify(httpResponse, never()).sendRedirect(anyString());
    }
}
