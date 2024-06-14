package com.namics.distrelec.b2b.storefront.filters;

import com.namics.distrelec.b2b.storefront.security.exceptions.AuthenticationCaptchaException;
import com.namics.distrelec.b2b.storefront.util.CaptchaUtil;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
public class CaptchaAuthenticationFilterTest {

    private static final String POST = "POST";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Mock
    private HttpServletResponse response;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private CaptchaUtil captchaUtil;
    @InjectMocks
    private CaptchaAuthenticationFilter authenticationFilter;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private HttpSession session;
    private MockHttpServletRequest request;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        session = new MockHttpSession();
        session.setAttribute(CaptchaAuthenticationFilter.SHOW_CAPTCHA, Boolean.FALSE);
        request = new MockHttpServletRequest();
        request.setAttribute(USERNAME, USERNAME);
        request.setAttribute(PASSWORD, PASSWORD);
        request.setMethod(POST);
        request.setSession(session);
    }

    @After
    public void tearDown(){
        session.invalidate();
        request.invalidate();
    }

    @Test
    public void testSuccessfulLogin(){
        final Authentication authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        final Authentication authenticationResult = authenticationFilter.attemptAuthentication(request, response);

        assertTrue(authenticationResult.isAuthenticated());
    }

    @Test
    public void testLoginFailsAsShowCaptchaIsTrue(){
        thrown.expect(AuthenticationCaptchaException.class);
        thrown.expectMessage("Invalid Captcha");
        session.setAttribute(CaptchaAuthenticationFilter.SHOW_CAPTCHA, Boolean.TRUE);
        final Authentication authenticationResult = authenticationFilter.attemptAuthentication(request, response);

        assertFalse(authenticationResult.isAuthenticated());
    }
}
