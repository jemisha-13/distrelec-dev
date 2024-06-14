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
package com.namics.distrelec.b2b.storefront.interceptors;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;

import de.hybris.platform.servicelayer.user.UserService;

/**
 * Spring MVC interceptor that validates that the spring security user and the hybris session user are in sync. If the spring security user
 * and the hybris session user are not in sync then the session is invalidated and the visitor is redirect to the homepage.
 */
public class SecurityCheckInterceptor extends AbstractOncePerRequestPreHandleInterceptor {
    private static final Logger LOG = LogManager.getLogger(SecurityCheckInterceptor.class);

    @Autowired
    private UserService userService;

    @Override
    protected boolean preHandleOnce(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws IOException {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            final Object principal = authentication.getPrincipal();
            if (principal instanceof String) {
                final String springSecurityUserId = (String) principal;

                final String hybrisUserId = userService.getCurrentUser().getUid();
                if (!springSecurityUserId.equals(hybrisUserId)) {
                    LOG.error("{} {} User miss-match springSecurityUserId [{}] hybris session user [{}]. Invalidating session.",
                            ErrorLogCode.SPRING_SECURITY_ERROR, ErrorSource.HYBRIS, springSecurityUserId, hybrisUserId);

                    // Invalidate session and redirect to the root page
                    request.getSession().invalidate();
                    response.sendRedirect(request.getContextPath() + "/");
                    return false;
                }
            }

        }
        return true;
    }
}
