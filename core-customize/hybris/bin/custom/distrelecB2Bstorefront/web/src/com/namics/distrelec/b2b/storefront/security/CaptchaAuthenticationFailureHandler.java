package com.namics.distrelec.b2b.storefront.security;

import com.namics.distrelec.b2b.facades.constants.WebConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public class CaptchaAuthenticationFailureHandler {

    public static final String LOGIN_ATTEMPT = "loginAttempt";
    public static final String MAX_LOGIN_ATTEMPTS = "distrelec.maxLogin.attempts";

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ConfigurationService configurationService;

    public void onFailedLogin(HttpServletRequest request){
        // show captcha
        final int login_attempt = sessionService.getAttribute(LOGIN_ATTEMPT) == null ? 1
                : Integer.parseInt(sessionService.getAttribute(LOGIN_ATTEMPT).toString()) + 1;
        sessionService.setAttribute(LOGIN_ATTEMPT, login_attempt);
        if (login_attempt >= configurationService.getConfiguration().getInt(MAX_LOGIN_ATTEMPTS, 3)) {
            request.getSession().setAttribute(WebConstants.SHOW_CAPTCHA, Boolean.TRUE);
        } else {
            request.getSession().setAttribute(WebConstants.SHOW_CAPTCHA, Boolean.FALSE);
        }
    }

}
