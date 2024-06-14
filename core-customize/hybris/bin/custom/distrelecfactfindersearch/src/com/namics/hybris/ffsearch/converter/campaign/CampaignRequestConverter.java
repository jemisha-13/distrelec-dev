/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.converter.campaign;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.namics.hybris.ffsearch.data.campaign.CampaignRequest;
import com.namics.hybris.ffsearch.data.campaign.CampaignResponse;
import com.namics.hybris.ffsearch.service.impl.FactFinderAuthentication;
import com.namics.hybris.ffsearch.service.impl.FactFinderTrackingServiceImpl;

import de.factfinder.webservice.ws71.FFcampaign.ArrayOfCampaign;
import de.factfinder.webservice.ws71.FFcampaign.CampaignPortType;
import de.factfinder.webservice.ws71.FFtracking.TrackingPortType;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Population Converter which uses the FactFinder instance to convert a {@link CampaignRequest} to a {@link CampaignResponse}. This is also
 * known as "executing a campaign request and fetching the result".
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class CampaignRequestConverter extends AbstractPopulatingConverter<CampaignRequest, CampaignResponse> {

    private static final Logger LOG = LogManager.getLogger(CampaignRequestConverter.class);
    
    public static String LB_COOKIE_NAME = "AWSALB";
    public static String HTTP_HEADER_COOKIE = "Cookie";
    public static String HTTP_HEADER_SET_COOKIE = "Set-Cookie";
    @Autowired
    private SessionService sessionService;
    private CampaignPortType campaignWebserviceClient;
    private FactFinderAuthentication authentication;

    @Override
    public void populate(final CampaignRequest source, final CampaignResponse target) {
        try {
            CampaignPortType port =getCampaignWebserviceClient();
            final ArrayOfCampaign campaignResult = port.getProductCampaigns(source.getChannel(), source.getProductCode(), false,
                    getAuthentication().getCampaignAuth());
            target.setCampaigns(campaignResult);
            readAndStoreCookie(((BindingProvider) port));
        } catch (final Exception e) {
            LOG.error("Unable to fetch campaign for product [{}] in FactFinder.", source.getProductCode(), e);
            target.setCampaigns(new ArrayOfCampaign());
        }
        super.populate(source, target);
    }

    @Override
    protected CampaignResponse createTarget() {
        final CampaignResponse response = new CampaignResponse();
        response.setCampaigns(new ArrayOfCampaign());
        return response;
    }
    
    // BEGIN GENERATED CODE

    protected CampaignPortType getCampaignWebserviceClient() {
        String awsCookieValue = sessionService.getAttribute(LB_COOKIE_NAME);
        if (StringUtils.isNotEmpty(awsCookieValue)) {
            addCookie(((BindingProvider) campaignWebserviceClient), awsCookieValue);
        }else
        {
            // cookie needs to be actively removed otherwise it will use the last value
            removeCookie(((BindingProvider) campaignWebserviceClient));
        }
        return campaignWebserviceClient;
    }
    
    /**
     * 
     * @param port
     */
    private void readAndStoreCookie(final BindingProvider port)
    {

        final Map<String, List<String>> header = (Map<String, List<String>>) port.getResponseContext()
                .get(MessageContext.HTTP_RESPONSE_HEADERS);
        final List<String> cookies = header.get(HTTP_HEADER_SET_COOKIE);
        if (null != cookies)
        {
            for (final String cookie : cookies)
            {
                final String[] split = cookie.split("=");
                if (LB_COOKIE_NAME.equals(split[0]))
                {
                    sessionService.setAttribute(LB_COOKIE_NAME, split[1]);
                }
            }
        }

    }

    /**
     * 
     * @param port
     */
    private void removeCookie(final BindingProvider port)
    {

        final Map<String, Object> requestContext = port.getRequestContext();

        final Map header = (Map) requestContext.get(MessageContext.HTTP_REQUEST_HEADERS);
        if (null != header)
        {
            header.remove(HTTP_HEADER_COOKIE);
        }


    }

    /**
     * Adds the cookie to the http-header
     *
     * @param port
     * @param value
     */
    private void addCookie(final BindingProvider port, final String value)
    {
        final List<String> sessionCookie = Collections.singletonList(LB_COOKIE_NAME + "=" + value);
        final Map<String, Object> requestContext = port.getRequestContext();

        Map header = (Map) requestContext.get(MessageContext.HTTP_REQUEST_HEADERS);
        if (null == header)
        {
            header = new HashMap();
            requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, header);
        }
        header.put(HTTP_HEADER_COOKIE, sessionCookie);

    }

    @Required
    public void setCampaignWebserviceClient(final CampaignPortType campaignWebserviceClient) {
        this.campaignWebserviceClient = campaignWebserviceClient;
    }

    protected FactFinderAuthentication getAuthentication() {
        return authentication;
    }

    @Required
    public void setAuthentication(final FactFinderAuthentication authentication) {
        this.authentication = authentication;
    }

}
