/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.filter;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.GenericSearchConstants.LOG;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.webservicescommons.util.YSanitizer;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.facades.storesession.NamicsStoreSessionFacade;
import com.namics.distrelec.occ.core.exceptions.InvalidResourceException;

/**
 * Filter that resolves base site id from the requested url and activates it.
 */
public class BaseSiteMatchingFilter extends AbstractUrlMatchingFilter {
    private static final Logger LOG = Logger.getLogger(BaseSiteMatchingFilter.class);

    private String regexp;

    private BaseSiteService baseSiteService;

    private NamicsStoreSessionFacade namicsStoreSessionFacade;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain)
                                                                   throws ServletException,
                                                                   IOException {
        final String baseSiteID = getBaseSiteValue(request, regexp);

        if (baseSiteID != null) {
            final BaseSiteModel requestedBaseSite = getBaseSiteService().getBaseSiteForUID(baseSiteID);
            if (requestedBaseSite != null) {
                final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();

                if (!requestedBaseSite.equals(currentBaseSite)) {
                    getBaseSiteService().setCurrentBaseSite(requestedBaseSite, true);
                }
                final CountryModel country = ((CMSSiteModel) requestedBaseSite).getCountry();
                if (Objects.nonNull(country)) {

                    getNamicsStoreSessionFacade().setCurrentCountry(country.getIsocode());
                    if (CollectionUtils.isNotEmpty(country.getRegions())) {
                        getNamicsStoreSessionFacade().setCurrentRegion(country.getRegions().iterator().next().getIsocode());
                    }

                }
            } else if (!request.getRequestURI().endsWith("/report/csp-report")) {
                final InvalidResourceException ex = new InvalidResourceException(YSanitizer.sanitize(baseSiteID));
                LOG.debug(ex.getMessage());
                throw ex;
            }
        }

        filterChain.doFilter(request, response);
    }

    protected String getRegexp() {
        return regexp;
    }

    @Required
    public void setRegexp(final String regexp) {
        this.regexp = regexp;
    }

    protected BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    @Required
    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    protected NamicsStoreSessionFacade getNamicsStoreSessionFacade() {
        return namicsStoreSessionFacade;
    }

    public void setNamicsStoreSessionFacade(final NamicsStoreSessionFacade namicsStoreSessionFacade) {
        this.namicsStoreSessionFacade = namicsStoreSessionFacade;
    }
}
