/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.eprocurement.ariba.filters;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthoritiesContainer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.web.filter.GenericFilterBean;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.eprocurement.service.ariba.DistAribaService;
import com.namics.distrelec.b2b.facades.constants.WebConstants;

import de.hybris.platform.core.model.user.UserModel;

/**
 * Filter to automatically authenticate Ariba customers.
 * 
 * @author dsivakumaran, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class AribaAuthenticationFilter extends GenericFilterBean {
    protected static final Logger LOG = Logger.getLogger(AribaAuthenticationFilter.class);

    private AuthenticationSuccessHandler authenticationSuccessHandler;
    private AuthenticationManager authenticationManager;
    private DistAribaService distAribaService;

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        try {
            final String token = req.getParameter(WebConstants.URL_PARAM_KEY_TOKEN);
            if (StringUtils.isNotBlank(token)) {
                final UserModel user = distAribaService.aribaLogin(token);

                final String aribaGroup = StringUtils.upperCase("ROLE_" + DistConstants.User.ARIBACUSTOMERGROUP_UID);
                final String eprocurementGroup = StringUtils.upperCase("ROLE_" + DistConstants.User.EPROCUREMENTGROUP_UID);

                final GrantedAuthority aribaAuthority = new SimpleGrantedAuthority(aribaGroup);
                final GrantedAuthority eProcurementAuthority = new SimpleGrantedAuthority(eprocurementGroup);
                final PreAuthenticatedAuthenticationToken preAuth = new PreAuthenticatedAuthenticationToken(user.getUid(), token);
                final GrantedAuthoritiesContainer container = new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails((HttpServletRequest) req,
                        Arrays.asList(aribaAuthority, eProcurementAuthority));
                preAuth.setDetails(container);

                final Authentication authResult = authenticationManager.authenticate(preAuth);

                SecurityContextHolder.getContext().setAuthentication(authResult);

                authenticationSuccessHandler.onAuthenticationSuccess((HttpServletRequest) req, (HttpServletResponse) res, authResult);
            }
        } catch (final AuthenticationException authException) {
            SecurityContextHolder.clearContext();
            distAribaService.logout();
            LOG.debug("Authentication of Ariba customer failed", authException);
        }

        chain.doFilter(req, res);
    }

    public void setAuthenticationSuccessHandler(final AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    public void setAuthenticationManager(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public DistAribaService getDistAribaService() {
        return distAribaService;
    }

    public void setDistAribaService(final DistAribaService distAribaService) {
        this.distAribaService = distAribaService;
    }
}
