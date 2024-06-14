package com.namics.distrelec.b2b.storefront.util;

import com.namics.distrelec.b2b.storefront.controllers.pages.AbstractPageControllerTest;
import com.namics.distrelec.b2b.storefront.controllers.pages.RegisterPageController;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
public class CaptchaUtilImplTest extends AbstractPageControllerTest<RegisterPageController> {

    private static final String WHITE_LISTED_ENV_KEY = "recaptcha.whitelisted.environments";
    private static final String WHITE_LISTED_IPS_KEY = "recaptcha.whitelisted.ips";
    private static final String CURRENT_ENVIRONMENT_KEY = "environment.current";
    private static final String WHITE_LISTED_IPS = "52.50.116.94,127.0.0.1";
    private static final String WHITELISTED_ENVIRONMENTS = "env-hp-q,env-development";

    CaptchaUtilImpl captchaUtil;

    @Mock
    protected ConfigurationService configurationService;
    @Mock
    protected Configuration configuration;
    @Mock
    protected HttpServletRequest request;
    @InjectMocks
    private RegisterPageController registerPageController;
    protected MockMvc mockMvc;


    /*
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(registerPageController).build();
        super.setUp();

        captchaUtil = new CaptchaUtilImpl();
        captchaUtil.setConfigurationService(configurationService);
        registerPageController.setCaptchaUtil(captchaUtil);
    }

    private void setUp(final boolean valid) {
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString(WHITE_LISTED_ENV_KEY, StringUtils.EMPTY)).thenReturn(WHITELISTED_ENVIRONMENTS);
        when(configuration.getString(WHITE_LISTED_IPS_KEY, StringUtils.EMPTY)).thenReturn(WHITE_LISTED_IPS);
        final String[] environments = WHITELISTED_ENVIRONMENTS.split(",");
        when(configuration.getString(CURRENT_ENVIRONMENT_KEY, StringUtils.EMPTY)).thenReturn(valid ? environments[Math.abs(new Random().nextInt(environments.length))] : "invalid-environment");
        final String[] ips = WHITE_LISTED_IPS.split(",");
        when(request.getRemoteAddr()).thenReturn(valid ? ips[Math.abs(new Random().nextInt(ips.length))] : "unkonwn-ip");
        when(request.getHeader("X-Forwarded-For")).thenReturn(valid ? ips[Math.abs(new Random().nextInt(ips.length))] : "");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.storefront.controllers.pages.AbstractPageControllerTest#getController()
     */
    @Override
    protected RegisterPageController getController() {
        return registerPageController;
    }


    /**
     * Test method for {@link CaptchaUtilImpl#validateReCaptcha(javax.servlet.http.HttpServletRequest, java.lang.String)}.
     */
    @Test
    public final void testisWhitelisted() {
        setUp(true);
        assertTrue("The captcha must be valid", captchaUtil.isWhitelisted(request));
    }
}
