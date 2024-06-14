/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.filter;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.constants.Europe1Constants;
import de.hybris.platform.europe1.enums.UserTaxGroup;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Request filter that sets session attributes for Europe1 price factory.
 */
public class Europe1AttributesFilter extends OncePerRequestFilter {
    private BaseStoreService baseStoreService;

    private SessionService sessionService;

    private CMSSiteService cmsSiteService;

    private UserService userService;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
                                                                                                                                         throws ServletException,
                                                                                                                                         IOException {
        setUserTaxGroupAttribute();
        setUserPriceGroupAttribute();
        filterChain.doFilter(request, response);
    }

    protected void setUserTaxGroupAttribute() {
        final UserModel currentUser = getUserService().getCurrentUser();
        if (currentUser.getEurope1PriceFactory_UTG() != null) {
            getSessionService().getCurrentSession().setAttribute(Europe1Constants.PARAMS.UTG, currentUser.getEurope1PriceFactory_UTG());
        } else {
            final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
            if (currentSite != null && currentSite.getSalesOrg() != null && currentSite.getUserTaxGroup() != null) {
                getSessionService().getCurrentSession().setAttribute(Europe1Constants.PARAMS.UTG, currentSite.getUserTaxGroup());
            }
        }

        final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
        if (currentBaseStore != null) {
            final UserTaxGroup taxGroup = currentBaseStore.getTaxGroup();
            if (taxGroup != null) {
                getSessionService().setAttribute(Europe1Constants.PARAMS.UTG, taxGroup);
            }
        }
    }

    protected void setUserPriceGroupAttribute() {
        final UserModel currentUser = getUserService().getCurrentUser();
        if (currentUser.getEurope1PriceFactory_UPG() != null) {
            getSessionService().setAttribute(Europe1Constants.PARAMS.UPG, currentUser.getEurope1PriceFactory_UPG());
        } else {
            CMSSiteModel currentSite = (CMSSiteModel) getCmsSiteService().getCurrentSite();
            if (currentSite != null && currentSite.getSalesOrg() != null && currentSite.getUserPriceGroup() != null) {
                getSessionService().setAttribute(Europe1Constants.PARAMS.UPG, currentSite.getUserPriceGroup());
            }
        }
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    protected BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    @Required
    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    protected SessionService getSessionService() {
        return sessionService;
    }

    @Required
    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }
}
