package com.namics.distrelec.b2b.storefront.security;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;

import static com.namics.distrelec.b2b.storefront.security.CaptchaAuthenticationFailureHandler.LOGIN_ATTEMPT;
import static com.namics.distrelec.b2b.storefront.security.CaptchaAuthenticationFailureHandler.MAX_LOGIN_ATTEMPTS;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@UnitTest
public class CaptchaAuthenticationFailureHandlerTest {

    @Mock
    private SessionService sessionService;
    @Mock
    private ConfigurationService configurationService;
    @InjectMocks
    private CaptchaAuthenticationFailureHandler handler;

    private HttpSession session;
    private MockHttpServletRequest request;
    private PropertiesConfiguration configuration;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        session = new MockHttpSession();
        request = new MockHttpServletRequest();
        configuration = new PropertiesConfiguration();
        request.setSession(session);
    }

    @After
    public void tearDown(){
        session.invalidate();
        request.invalidate();
    }

    @Test
    public void testOnLoginAttemptOne(){
        configuration.setProperty(MAX_LOGIN_ATTEMPTS,3);
        when(sessionService.getAttribute(LOGIN_ATTEMPT)).thenReturn(1);
        when(configurationService.getConfiguration()).thenReturn(configuration);

        handler.onFailedLogin(request);
        assertNotNull(session.getAttribute("SHOW_CAPTCHA"));
        assertEquals(Boolean.FALSE, session.getAttribute("SHOW_CAPTCHA"));
    }

    @Test
    public void testOnLoginAttemptFour(){
        configuration.setProperty("distrelec.maxLogin.attempts", 4);

        when(sessionService.getAttribute(LOGIN_ATTEMPT)).thenReturn(4);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        handler.onFailedLogin(request);
        assertNotNull(session.getAttribute("SHOW_CAPTCHA"));
        assertEquals(Boolean.TRUE, session.getAttribute("SHOW_CAPTCHA"));
    }
}
