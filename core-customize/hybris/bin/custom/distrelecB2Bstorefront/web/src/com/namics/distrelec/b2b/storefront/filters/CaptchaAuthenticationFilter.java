/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.filters;

import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.storefront.security.exceptions.AuthenticationCaptchaException;
import com.namics.distrelec.b2b.storefront.util.CaptchaUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CaptchaAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger LOG = Logger.getLogger(CaptchaAuthenticationFilter.class);
    public static final String SHOW_CAPTCHA = WebConstants.SHOW_CAPTCHA;

    @Autowired
    protected CaptchaUtil captchaUtil;

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException {

        final Boolean showCaptcha = (Boolean) request.getSession().getAttribute(SHOW_CAPTCHA);
        if (Boolean.TRUE.equals(showCaptcha) && !captchaUtil.validateReCaptcha(request)) {
            throw new AuthenticationCaptchaException("Invalid Captcha");
        }

        return super.attemptAuthentication(request, response);
    }

}
