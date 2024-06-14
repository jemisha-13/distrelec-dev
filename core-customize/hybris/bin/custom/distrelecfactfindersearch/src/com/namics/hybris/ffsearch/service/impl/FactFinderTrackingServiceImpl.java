/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.namics.hybris.ffsearch.data.tracking.FactFinderTrackingData;
import com.namics.hybris.ffsearch.service.FactFinderChannelService;
import com.namics.hybris.ffsearch.service.FactFinderTrackingService;

import de.factfinder.webservice.ws71.FFsearch.SearchPortType;
import de.factfinder.webservice.ws71.FFtracking.String2StringMap;
import de.factfinder.webservice.ws71.FFtracking.TrackingPortType;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * The FactFinder tracking service implementation of {@link FactFinderTrackingService}.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class FactFinderTrackingServiceImpl implements FactFinderTrackingService {

    private static final Logger LOG = LogManager.getLogger(FactFinderTrackingServiceImpl.class);
    public static String LB_COOKIE_NAME = "AWSALB";
    public static String HTTP_HEADER_COOKIE = "Cookie";
    public static String HTTP_HEADER_SET_COOKIE = "Set-Cookie";
    @Autowired
    private SessionService sessionService;
    
    private FactFinderAuthentication authentication;
    private FactFinderChannelService channelService;
    private TrackingPortType trackingWebserviceClient;

    @Override
    public boolean trackData(final FactFinderTrackingData data) {
        try {
            final String2StringMap logInformation = convert(data);
            TrackingPortType port = getTrackingWebserviceClient();
            boolean result = port.logInformation(getChannelService().getCurrentFactFinderChannel(), logInformation,
                    getAuthentication().getTrackingAuth());
            readAndStoreCookie(port);
            return result;
        } catch (final Exception e) {
            LOG.error("Unable to log tracking information [{}] to in FactFinder.", ReflectionToStringBuilder.toString(data), e);
            return false;
        }
    }
    

    private String2StringMap convert(final FactFinderTrackingData source) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final String2StringMap target = new String2StringMap();
        final Map<String, String> data = BeanUtils.describe(source);
        for (final String key : data.keySet()) {
            // Avoid null values in Tracking-Request
            if (data.get(key) == null) {
                continue;
            }
            final String2StringMap.Entry entry = new String2StringMap.Entry();
            entry.setKey(key);
            entry.setValue(data.get(key));
            target.getEntry().add(entry);
        }
        return target;
    }
    
    /**
     * read the cookie from the http-header
     * 
     * @param port
     * 
     */
    private void readAndStoreCookie(TrackingPortType port) {

        Map<String, List<String>> header = (Map<String, List<String>>) ((BindingProvider) port).getResponseContext().get(MessageContext.HTTP_RESPONSE_HEADERS);
        List<String> cookies = header.get(HTTP_HEADER_SET_COOKIE);
        if (null != cookies) {
            for (String cookie : cookies) {
                String[] split = cookie.split("=");
                if (LB_COOKIE_NAME.equals(split[0])) {
                    sessionService.setAttribute(LB_COOKIE_NAME, split[1]);
                }
            }
        }

    }
private void removeCookie(TrackingPortType port) {
        
        Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();

        Map header = (Map) requestContext.get(MessageContext.HTTP_REQUEST_HEADERS);
        if (null != header) {
            header.remove(HTTP_HEADER_COOKIE);
        }

        
    }

    /**
     * Adds the cookie to the http-header
     * 
     * @param searchWebserviceClient
     * @param sessionid
     */
    private void addCookie(TrackingPortType port, String value) {
        List<String> sessionCookie = Collections.singletonList(LB_COOKIE_NAME + "=" + value);
        Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();

        Map header = (Map) requestContext.get(MessageContext.HTTP_REQUEST_HEADERS);
        if (null == header) {
            header = new HashMap();
            requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, header);
        }
        header.put(HTTP_HEADER_COOKIE, sessionCookie);

    }
    // BEGIN GENERATED CODE

    protected FactFinderAuthentication getAuthentication() {
        return authentication;
    }

    @Required
    public void setAuthentication(final FactFinderAuthentication authentication) {
        this.authentication = authentication;
    }

    protected FactFinderChannelService getChannelService() {
        return channelService;
    }

    @Required
    public void setChannelService(final FactFinderChannelService channelService) {
        this.channelService = channelService;
    }

    protected TrackingPortType getTrackingWebserviceClient() {
        String awsCookieValue = sessionService.getAttribute(LB_COOKIE_NAME);
        if (StringUtils.isNotEmpty(awsCookieValue)) {
            addCookie(trackingWebserviceClient, awsCookieValue);
        }else
        {
            // cookie needs to be actively removed otherwise it will use the last value
            removeCookie(trackingWebserviceClient);
        }
        return trackingWebserviceClient;
    }

    public void setTrackingWebserviceClient(final TrackingPortType trackingWebserviceClient) {
        this.trackingWebserviceClient = trackingWebserviceClient;
    }

}
