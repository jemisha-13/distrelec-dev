/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.eprocurement.oci.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.web.filter.GenericFilterBean;

import com.namics.distrelec.b2b.facades.constants.WebConstants;

import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Use URL parameters username and password to authenticate current OCI customer.
 * 
 * @author pbueschi, Namics AG
 */
public class OciAuthenticationFilter extends GenericFilterBean {
    protected static final Logger LOG = Logger.getLogger(OciAuthenticationFilter.class);

    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    private AuthenticationManager authenticationManager;

    @Autowired
    private SessionService sessionService;

    @Autowired
    @Qualifier("httpSessionRequestCache")
    private RequestCache requestCache;

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {

        final String[] credentials = new String[2];
        credentials[0] = req.getParameter(WebConstants.URL_PARAM_KEY_USERNAME);
        credentials[1] = req.getParameter(WebConstants.URL_PARAM_KEY_PASSWORD);

        try {
            final UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(credentials[0], credentials[1]);
            authRequest.setDetails(this.authenticationDetailsSource.buildDetails((HttpServletRequest) req));
            final Authentication authResult = authenticationManager.authenticate(authRequest);
            SecurityContextHolder.getContext().setAuthentication(authResult);
            // For OCI authentication, we disable the restore of previous request.
            getSessionService().setAttribute(WebConstants.DO_NOT_STORE_REFERRER, Boolean.TRUE);
            requestCache.removeRequest((HttpServletRequest) req, (HttpServletResponse) res);
            authenticationSuccessHandler.onAuthenticationSuccess((HttpServletRequest) req, (HttpServletResponse) res, authResult);
        } catch (final AuthenticationException authException) {
            SecurityContextHolder.clearContext();
            LOG.debug("Authentication of customer " + credentials[0] + " failed", authException);
        }

        chain.doFilter(req, res);
    }

    public void setAuthenticationSuccessHandler(final AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    public void setAuthenticationManager(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }
}
