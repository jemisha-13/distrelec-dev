/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.service.impl;

import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.collect.Maps;
import com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.FactFinder;

import de.hybris.platform.util.Config;

/**
 * Bean for creating {@link de.factfinder.webservice.ws71.FFsearch.AuthenticationToken}s.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class FactFinderAuthentication {

    private static final String PREFIX = "FACT-FINDER";
    private static final String SUFFIX = "FACT-FINDER";

    public de.factfinder.webservice.ws71.FFsearch.AuthenticationToken getSearchAuth() {
        final de.factfinder.webservice.ws71.FFsearch.AuthenticationToken auth = new de.factfinder.webservice.ws71.FFsearch.AuthenticationToken();
        final Map.Entry<String, String> credentials = getCredentials();
        auth.setUsername(getUsername());
        auth.setTimestamp(credentials.getKey());
        auth.setPassword(credentials.getValue());
        return auth;
    }

    public de.factfinder.webservice.ws71.FFcampaign.AuthenticationToken getCampaignAuth() {
        final  de.factfinder.webservice.ws71.FFcampaign.AuthenticationToken auth = new de.factfinder.webservice.ws71.FFcampaign.AuthenticationToken();
        final Map.Entry<String, String> credentials = getCredentials();
        auth.setUsername(getUsername());
        auth.setTimestamp(credentials.getKey());
        auth.setPassword(credentials.getValue());
        return auth;
    }

    public de.factfinder.webservice.ws71.FFrecommender.AuthenticationToken getRecommendationAuth() {
        final de.factfinder.webservice.ws71.FFrecommender.AuthenticationToken auth = new de.factfinder.webservice.ws71.FFrecommender.AuthenticationToken();
        final Map.Entry<String, String> credentials = getCredentials();
        auth.setUsername(getUsername());
        auth.setTimestamp(credentials.getKey());
        auth.setPassword(credentials.getValue());
        return auth;
    }

    public de.factfinder.webservice.ws71.FFtracking.AuthenticationToken getTrackingAuth() {
        final de.factfinder.webservice.ws71.FFtracking.AuthenticationToken auth = new de.factfinder.webservice.ws71.FFtracking.AuthenticationToken();
        final Map.Entry<String, String> credentials = getCredentials();
        auth.setUsername(getUsername());
        auth.setTimestamp(credentials.getKey());
        auth.setPassword(credentials.getValue());
        return auth;
    }

    public de.factfinder.webservice.ws71.FFimport.AuthenticationToken getIndexManagementAuth() {
        final de.factfinder.webservice.ws71.FFimport.AuthenticationToken auth = new de.factfinder.webservice.ws71.FFimport.AuthenticationToken();
        final Map.Entry<String, String> credentials = getCredentialsIndexManagement();
        auth.setUsername(getUsernameIndexManagement());
        auth.setTimestamp(credentials.getKey());
        auth.setPassword(credentials.getValue());
        return auth;
    }

    private String getUsername() {
        return Config.getString(FactFinder.WEBSERVICE_USER, "namics");
    }

    private String getUsernameIndexManagement() {
        return Config.getString(FactFinder.IMPORTER_USER, "namics");
    }

    private Map.Entry<String, String> getCredentials() {
        return getCredentials(Config.getString(FactFinder.WEBSERVICE_PASSWORD, ""));
    }

    private Map.Entry<String, String> getCredentialsIndexManagement() {
        return getCredentials(Config.getString(FactFinder.IMPORTER_PASSWORD, ""));
    }

    private Map.Entry<String, String> getCredentials(final String passwordMd5) {
        final String timestamp = String.valueOf(System.currentTimeMillis());
        final String hash = DigestUtils.md5Hex(PREFIX + timestamp + passwordMd5 + SUFFIX);
        return Maps.immutableEntry(timestamp, hash);
    }
}
