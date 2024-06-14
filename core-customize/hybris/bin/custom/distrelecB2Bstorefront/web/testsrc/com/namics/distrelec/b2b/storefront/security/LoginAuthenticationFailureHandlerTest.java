package com.namics.distrelec.b2b.storefront.security;

import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.storefront.security.exceptions.AuthenticationCaptchaException;
import com.namics.distrelec.b2b.storefront.security.exceptions.DuplicateEmailAuthenticationException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.web.savedrequest.RequestCache;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@UnitTest
public class LoginAuthenticationFailureHandlerTest {
    @Mock
    private UserService userService;
    @Mock
    private SessionService sessionService;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private CaptchaAuthenticationFailureHandler captchaAuthenticationFailureHandler;
    @Mock
    private RequestCache requestCache;
    @InjectMocks
    private LoginAuthenticationFailureHandler loginAuthenticationFailureHandler;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String USER_UID = "neil.clarke@distrelec.com";
    private static final String J_USERNAME = LoginAuthenticationFailureHandler.J_USERNAME;
    private static final String SPRING_SECURITY_REMEMBER_ME = LoginAuthenticationFailureHandler.SPRING_SECURITY_REMEMBER_ME;
    private static final String FALSE = "false";
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        response = new MockHttpServletResponse();

        request = new MockHttpServletRequest();
        request.setParameter(J_USERNAME, USER_UID);
        request.setParameter(SPRING_SECURITY_REMEMBER_ME, FALSE);
    }

    @After
    public void tearDown() {
        request.invalidate();
        response = null;
    }

    private void doAnswerOnSessionServiceSetAttribute(final String attributeKey, final Boolean attributeValue) {
        doAnswer(invocation -> {
            assertEquals(attributeKey, invocation.getArguments()[0]);
            assertEquals(attributeValue, invocation.getArguments()[1]);
            return null;
        }).when(sessionService).setAttribute(attributeKey, attributeValue);
    }

    @Test
    public final void testLoginWrongCaptcha() throws IOException, ServletException {
        when(userService.getUserForUID(anyString())).thenThrow(UnknownIdentifierException.class);
        doAnswerOnSessionServiceSetAttribute(WebConstants.WRONG_CAPTCHA, Boolean.TRUE);
        loginAuthenticationFailureHandler.onAuthenticationFailure(request, response, new AuthenticationCaptchaException("Wrong Captcha"));
        assertEquals(401, response.getStatus());
        assertEquals("Unauthorized", response.getErrorMessage());
    }


    @Test
    public final void testLoginDuplicateEmail() throws IOException, ServletException {
        when(userService.getUserForUID(anyString())).thenThrow(UnknownIdentifierException.class);
        doAnswerOnSessionServiceSetAttribute(WebConstants.DUPLICATE_EMAIL, Boolean.TRUE);
        loginAuthenticationFailureHandler.onAuthenticationFailure(request, response, new DuplicateEmailAuthenticationException("Duplicate Email"));
        assertEquals(401, response.getStatus());
        assertEquals("Unauthorized", response.getErrorMessage());
    }

    @Test
    public final void testLoginInactiveAccount() throws IOException, ServletException {
        doAnswerOnSessionServiceSetAttribute(WebConstants.ACCOUNT_NOT_ACTIVE, Boolean.TRUE);
        B2BCustomerModel b2BCustomerModel = mock(B2BCustomerModel.class);
        when(userService.getUserForUID(eq(USER_UID))).thenReturn(b2BCustomerModel);
        when(userService.getUserForUID(eq(USER_UID + "_deactivated_P4C"))).thenThrow(UnknownIdentifierException.class);
        loginAuthenticationFailureHandler.setUserService(userService);
        loginAuthenticationFailureHandler.onAuthenticationFailure(request, response, new DisabledException("Inactive Account"));
        assertEquals(401, response.getStatus());
        assertEquals("Unauthorized", response.getErrorMessage());
    }
}
