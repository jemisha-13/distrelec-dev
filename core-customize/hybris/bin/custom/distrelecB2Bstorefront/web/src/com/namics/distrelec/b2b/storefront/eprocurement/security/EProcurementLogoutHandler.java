/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.eprocurement.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementService;

public class EProcurementLogoutHandler extends SecurityContextLogoutHandler {

    private static final Logger LOG = Logger.getLogger(EProcurementLogoutHandler.class);

    @Autowired
    private DistEProcurementService distEProcurementService;

    @Autowired
    private RedirectStrategy redirectStrategy;

    @Override
    public void logout(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) {
        if (!distEProcurementService.isEProcurementCustomer()) {
            super.logout(request, response, authentication);
        } else {
            try {
                redirectStrategy.sendRedirect(request, response, "/");
            } catch (final IOException e) {
                LOG.error("Could not redirect E-Procurement customer, from Logout to Homepage.", e);
            }
        }
    }

    public DistEProcurementService getDistEProcurementService() {
        return distEProcurementService;
    }

    public void setDistEProcurementService(final DistEProcurementService distEProcurementService) {
        this.distEProcurementService = distEProcurementService;
    }

    public RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    public void setRedirectStrategy(final RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

}
