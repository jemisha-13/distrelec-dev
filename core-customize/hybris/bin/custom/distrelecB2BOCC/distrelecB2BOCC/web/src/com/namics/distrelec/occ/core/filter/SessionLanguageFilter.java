/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.filter.OncePerRequestFilter;

import com.namics.distrelec.occ.core.context.ContextInformationLoader;

import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

/**
 * Filter sets language in session context basing on request parameters:<br>
 * <ul>
 * <li><b>lang</b> - set current {@link LanguageModel}</li>
 * </ul>
 */
public class SessionLanguageFilter extends OncePerRequestFilter {
    private ContextInformationLoader contextInformationLoader;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private UserFacade userFacade;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
                                                                                                                                         throws ServletException,
                                                                                                                                         IOException {
        getContextInformationLoader().setLanguageFromRequest(request);
        userFacade.syncSessionLanguage();
        i18NService.setLocalizationFallbackEnabled(true);
        filterChain.doFilter(request, response);
    }

    protected ContextInformationLoader getContextInformationLoader() {
        return contextInformationLoader;
    }

    @Required
    public void setContextInformationLoader(final ContextInformationLoader contextInformationLoader) {
        this.contextInformationLoader = contextInformationLoader;
    }
}
