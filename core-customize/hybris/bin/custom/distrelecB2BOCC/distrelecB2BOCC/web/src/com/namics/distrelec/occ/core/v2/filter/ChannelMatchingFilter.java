/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Filter that puts channel from the requested url into the session.
 */
public class ChannelMatchingFilter extends AbstractUrlMatchingFilter
{
    public static final String CHANNEL_REQUEST_PARAM = "channel";
    public static final List<SiteChannel> SITE_CHANNELS = Arrays.asList(SiteChannel.B2C, SiteChannel.B2B);

    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private SessionService sessionService;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException
    {
        final String channelValue = getChannelValue(request);
        if(StringUtils.isNotEmpty(channelValue) && getCmsSiteService().getCurrentSite() != null) {
            for (SiteChannel siteChannel : SITE_CHANNELS) {
                if (channelValue.equals(siteChannel.toString()))
                {
                    sessionService.setAttribute(DistConstants.Session.CHANNEL, siteChannel);
                    getStoreSessionFacade().setCurrentChannel(channelValue);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getChannelValue(final HttpServletRequest request)
    {
        Map<String, String[]> map = request.getParameterMap();
        String channelValue = StringUtils.EMPTY;
        if (map.containsKey(CHANNEL_REQUEST_PARAM)) channelValue = map.get(CHANNEL_REQUEST_PARAM)[0];
        return channelValue;
    }

    public DistrelecStoreSessionFacade getStoreSessionFacade()
    {
        return storeSessionFacade;
    }
    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }
}
