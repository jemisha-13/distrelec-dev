/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import de.factfinder.webservice.ws71.FFsearch.AuthenticationToken;
import de.factfinder.webservice.ws71.FFsearch.Params;
import de.factfinder.webservice.ws71.FFsearch.Result;
import de.factfinder.webservice.ws71.FFsearch.SearchControlParams;
import de.factfinder.webservice.ws71.FFsearch.SearchPortType;
import de.factfinder.webservice.ws71.FFsearch.SuggestResult;
import de.factfinder.webservice.ws71.FFsearch.TrackingInformation;
import de.hybris.platform.servicelayer.session.SessionService;

public class CookieSearchPortTypeWrapper {

    public static String LB_COOKIE_NAME = "AWSALB";
    public static String HTTP_HEADER_COOKIE = "Cookie";
    public static String HTTP_HEADER_SET_COOKIE = "Set-Cookie";
    

    private SessionService sessionService;

    private SearchPortType searchWebserviceClient;

    public SuggestResult getSuggestions(Params searchParams, SearchControlParams controlParams, AuthenticationToken searchAuth) {
        SearchPortType port = getSearchWebserviceClient();
        SuggestResult suggestions = port.getSuggestions(searchParams, controlParams, searchAuth);

        // read cookie from response
        readAndStoreCookie(port);

        return suggestions;
    }

    public Result getResult(Params searchParams, SearchControlParams controlParams, AuthenticationToken searchAuth, TrackingInformation trackingInformation) {
        SearchPortType port = getSearchWebserviceClient();
        Result result = port.getResult(searchParams, controlParams, searchAuth, trackingInformation);

        readAndStoreCookie(port);

        return result;
    }


    private SearchPortType getSearchWebserviceClient() {

        String awsCookieValue = sessionService.getAttribute(LB_COOKIE_NAME);
        if (StringUtils.isNotEmpty(awsCookieValue)) {
            addCookie(searchWebserviceClient, awsCookieValue);
        }else
        {
            // cookie needs to be actively removed otherwise it will use the last value
            removeCookie(searchWebserviceClient);
        }

        return searchWebserviceClient;
    }

    private void removeCookie(SearchPortType searchWebserviceClient) {
        
        Map<String, Object> requestContext = ((BindingProvider) searchWebserviceClient).getRequestContext();

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
    private void addCookie(SearchPortType searchWebserviceClient, String value) {
        List<String> sessionCookie = Collections.singletonList(LB_COOKIE_NAME + "=" + value);
        Map<String, Object> requestContext = ((BindingProvider) searchWebserviceClient).getRequestContext();

        Map header = (Map) requestContext.get(MessageContext.HTTP_REQUEST_HEADERS);
        if (null == header) {
            header = new HashMap();
            requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, header);
        }
        header.put(HTTP_HEADER_COOKIE, sessionCookie);

    }

    /**
     * read the cookie from the http-header
     * 
     * @param port
     * 
     */
    private void readAndStoreCookie(SearchPortType port) {

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

    @Required
    public void setSearchWebserviceClient(final SearchPortType searchWebserviceClient) {
        this.searchWebserviceClient = searchWebserviceClient;
    }

    @Required
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

}
