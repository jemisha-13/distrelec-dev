/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.converter.recommendation;

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

import com.namics.hybris.ffsearch.data.recommendation.RecommendationRequest;
import com.namics.hybris.ffsearch.data.recommendation.RecommendationResponse;
import com.namics.hybris.ffsearch.service.impl.FactFinderAuthentication;
import com.namics.hybris.ffsearch.service.impl.FactFinderTrackingServiceImpl;

import de.factfinder.webservice.ws71.FFrecommender.RecommenderPortType;
import de.factfinder.webservice.ws71.FFrecommender.RecommenderResult;
import de.factfinder.webservice.ws71.FFtracking.TrackingPortType;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Population Converter which uses the FactFinder instance to convert a {@link RecommendationRequest} to a {@link RecommendationResponse}.
 * This is also known as "executing a recommendation request and fetching the result".
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class RecommendationRequestConverter extends AbstractPopulatingConverter<RecommendationRequest, RecommendationResponse> {

    private static final Logger LOG = LogManager.getLogger(RecommendationRequestConverter.class);
    
    public static String LB_COOKIE_NAME = "AWSALB";
    public static String HTTP_HEADER_COOKIE = "Cookie";
    public static String HTTP_HEADER_SET_COOKIE = "Set-Cookie";
    @Autowired
    private SessionService sessionService;
    
    private RecommenderPortType recommendationWebserviceClient;
    private FactFinderAuthentication authentication;

    @Override
    public void populate(final RecommendationRequest source, final RecommendationResponse target) {
        try {
            RecommenderPortType port = getRecommendationWebserviceClient();
            final RecommenderResult recommendationResult = port.getRecommendation(10, source.getProductCode(),
                    source.getChannel(), false, getAuthentication().getRecommendationAuth());
            readAndStoreCookie(((BindingProvider) port));
            target.setRecommendations(recommendationResult);
        } catch (final Exception e) {
            LOG.error("Unable to fetch campaign for product [{}] in FactFinder.", source.getProductCode(), e);
            target.setRecommendations(new RecommenderResult());
        }
        super.populate(source, target);
    }

    @Override
    protected RecommendationResponse createTarget() {
        final RecommendationResponse response = new RecommendationResponse();
        response.setRecommendations(new RecommenderResult());
        return response;
    }

    // / BEGIN GENERATED CODE

    protected RecommenderPortType getRecommendationWebserviceClient() {
        String awsCookieValue = sessionService.getAttribute(LB_COOKIE_NAME);
        if (StringUtils.isNotEmpty(awsCookieValue)) {
            addCookie(((BindingProvider) recommendationWebserviceClient), awsCookieValue);
        }else
        {
            // cookie needs to be actively removed otherwise it will use the last value
            removeCookie(((BindingProvider) recommendationWebserviceClient));
        }
        return recommendationWebserviceClient;
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
    public void setRecommendationWebserviceClient(final RecommenderPortType recommendationWebserviceClient) {
        this.recommendationWebserviceClient = recommendationWebserviceClient;
    }

    protected FactFinderAuthentication getAuthentication() {
        return authentication;
    }

    @Required
    public void setAuthentication(final FactFinderAuthentication authentication) {
        this.authentication = authentication;
    }

}
