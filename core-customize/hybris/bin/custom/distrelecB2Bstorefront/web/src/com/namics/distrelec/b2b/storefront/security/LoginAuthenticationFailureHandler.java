/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.namics.distrelec.b2b.storefront.security;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.storefront.security.exceptions.AuthenticationCaptchaException;
import com.namics.distrelec.b2b.storefront.security.exceptions.DuplicateEmailAuthenticationException;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Handler for login authentication, see {@link SimpleUrlAuthenticationFailureHandler}.
 */
public class LoginAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger LOG = LogManager.getLogger(LoginAuthenticationFailureHandler.class);

    public static final String LOGIN_NOT_YET_APPROVED = "/login/unapproved";

    private final String REDIRECT_RESEND_ACCOUNT_ACTIVATION = "/register/doubleoptin/request";
    private static final String REDIRECT_LOGIN = "forward:/login/redirect";
    public static final String J_USERNAME = "j_username";
    public static final String DEACTIVATED_P4C = "_deactivated_P4C";
    public static final String SPRING_SECURITY_REMEMBER_ME = "_spring_security_remember_me";
    public static final String ERROR = "error";
    public static final String TRUE = "true";
    public static final String QD = "qd";


    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private CaptchaAuthenticationFailureHandler captchaAuthenticationFailureHandler;

    @Autowired
    @Qualifier("httpSessionRequestCache")
    private RequestCache requestCache;

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception)
            throws IOException, ServletException {
        // Store the j_username in the session
    	   request.getSession().setAttribute("SPRING_SECURITY_LAST_USERNAME", request.getParameter(J_USERNAME));
           request.getSession().setAttribute("SPRING_SECURITY_REMEMBER_ME", request.getParameter(SPRING_SECURITY_REMEMBER_ME));


        final SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest == null) {
            requestCache.saveRequest(request, response);
        }

        captchaAuthenticationFailureHandler.onFailedLogin(request);

        // DISTRELEC-10351
        try {
            final UserModel user = getUserService().getUserForUID(request.getParameter("j_username") + "_deactivated_P4C");
            LOG.warn("{} {} Deactivated user[{},{}] tried to login on {}", DistConstants.ErrorLogCode.INACTIVE_USER_ERROR, DistConstants.ErrorSource.HYBRIS, user.getUid(), user.getPk(),
                    new Date());
            response.sendRedirect(WebConstants.DEACTIVATED_CONTACT_URL);
            return;
        } catch (final UnknownIdentifierException uie) {
            // NOP
        }

        if (exception instanceof AuthenticationCaptchaException) {
            sessionService.setAttribute(WebConstants.WRONG_CAPTCHA, Boolean.TRUE);
        } else if (exception instanceof DuplicateEmailAuthenticationException) {
            sessionService.setAttribute(WebConstants.DUPLICATE_EMAIL, Boolean.TRUE);
        } else if (exception instanceof DisabledException){
            sessionService.setAttribute(WebConstants.ACCOUNT_NOT_ACTIVE, Boolean.TRUE);
            checkIfUserIsMigrated(request);
        }
        super.onAuthenticationFailure(request, response, exception);
    }

    private void checkIfUserIsMigrated(HttpServletRequest request) {
        UserModel user = getUserService().getUserForUID(request.getParameter("j_username"));
        if(user instanceof B2BCustomerModel){
            B2BCustomerModel customer = (B2BCustomerModel)user;
            if(customer.getDeactivationReason() != null) {
                switch (customer.getDeactivationReason()) {
                    case MIGRATION:
                        final CountryModel country = customer.getDefaultB2BUnit().getCountry();
                        sessionService.setAttribute(WebConstants.REDIRECT_COUNTRY, country);
                        sessionService.setAttribute(WebConstants.REDIRECT_SITE, customer.getCustomersBaseSite().getUid());
                        sessionService.setAttribute(WebConstants.ACCOUNT_MIGRATED, Boolean.TRUE);
                        break;
                }
            }
        }
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
