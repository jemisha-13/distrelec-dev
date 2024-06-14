package com.namics.distrelec.b2b.storefront.controllers.misc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.storefront.security.CaptchaAuthenticationFailureHandler;
import com.namics.distrelec.b2b.storefront.security.GUIDCookieStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@UnitTest
public class AuthenticationControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private GUIDCookieStrategy guidCookieStrategy;
    @Mock
    private SessionService sessionService;
    @Mock
    private I18NService i18NService;
    @Mock
    private CaptchaAuthenticationFailureHandler captchaAuthenticationFailureHandler;
    @InjectMocks
    private AuthenticationController controller;

    private MockMvc mockMvc;
    private MockHttpServletRequest request;
    private MockHttpSession session;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        request = new MockHttpServletRequest();
        session = new MockHttpSession();

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        request.setSession(session);
    }

    @After
    public void tearDown() {
        session.invalidate();
        request.invalidate();
    }


    @Test
    public void testSuccessfulAuthentication() throws Exception {
        session.setAttribute("SHOW_CAPTCHA", Boolean.FALSE);

        final AuthenticationController.AuthenticationRequestData data = new AuthenticationController.AuthenticationRequestData();
        data.setUsername("Username");
        data.setPassword("Password");
        data.setCaptchaResponse("OK");

        final MvcResult result = mockMvc.perform(post("/authentication")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(data)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        final String content = result.getResponse().getContentAsString();
        assertTrue(StringUtils.isNotEmpty(content));
        assertEquals("{\"success\":true,\"captchaRequired\":false,\"errorMsg\":null}", content);
    }

    private static String asJsonString(final Object obj) {
        String jsoncontent = "";
        try {
            final ObjectMapper mapper = new ObjectMapper();
            jsoncontent = mapper.writeValueAsString(obj);
        } catch (final Exception ignored) {

        }
        return StringUtils.isNotEmpty(jsoncontent) ? jsoncontent : StringUtils.EMPTY;
    }
}

