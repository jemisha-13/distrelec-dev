package com.namics.distrelec.b2b.core.cdn.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.core.cdn.DistrelecCDNService;
import com.namics.distrelec.b2b.core.cdn.dao.DistRadwareHistoryDAO;
import com.namics.distrelec.b2b.core.model.radware.DistRadwareCacheHistoryModel;
import com.namics.distrelec.b2b.core.radware.DistrelecRadwareAPIClient;
import com.namics.distrelec.b2b.core.radware.exception.DistRadwareAPIException;
import com.namics.distrelec.b2b.core.service.radware.data.Authentication;
import com.namics.distrelec.b2b.core.service.radware.data.AuthenticationOptions;
import com.namics.distrelec.b2b.core.service.radware.data.AuthenticationResponse;
import com.namics.distrelec.b2b.core.service.radware.data.RadwareApp;
import com.namics.distrelec.b2b.core.service.radware.data.RadwareAppListResponse;
import de.hybris.platform.servicelayer.config.ConfigurationService;

public class DistrelecCDNServiceImpl implements DistrelecCDNService {

    private static final Logger LOG = LogManager.getLogger(DistrelecCDNServiceImpl.class);

    private static final String BLANK_STRING = "";

    private static final String NAME_ATTRIBUTE = "name";

    private static final String NAME_ATTRIBUTE_VALUE = "access_token";

    @Autowired
    private DistrelecRadwareAPIClient distrelecRadwareAPIClient;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DistRadwareHistoryDAO distRadwareHistoryDAO;

    @Override
    public void registerCDNCacheClearRequest(String websiteUid) throws JsonProcessingException, DistRadwareAPIException {
        final String radwareSessionToken = callRadwareAuthenticationAPI();
        final String authToken = getAccessToken(radwareSessionToken);
        final RadwareAppListResponse radwareAppList = getAppListsForRadware(authToken);
        final String eligibleRadwareApps = configurationService.getConfiguration().getString("radware.api." + websiteUid + ".app.code");

        final List<String> eligibleRadwareAppCodes = Arrays.stream(eligibleRadwareApps.split(","))
                                                           .map(String::trim).collect(Collectors.toList());
        for (final String radwareAppCode : eligibleRadwareAppCodes) {
            if (isCacheClearingRequired(radwareAppCode)) {
                try {
                    LOG.debug("Updating time ...");
                    distRadwareHistoryDAO.updateRadwareCacheClearFlag(radwareAppCode, Boolean.TRUE);
                } catch (Exception ex) {
                    LOG.error("Error in Clearing Radware CDN Cache for website " + websiteUid + " and app code " + radwareAppCode);
                }
            }
        }

    }

    @Override
    public void clearCDNCache() throws JsonProcessingException, DistRadwareAPIException {
        List<DistRadwareCacheHistoryModel> radwareClearHistory = distRadwareHistoryDAO.getRadwareAppsForClearingCache(Boolean.TRUE);
        if (!radwareClearHistory.isEmpty()) {
            final String radwareSessionToken = callRadwareAuthenticationAPI();
            final String authToken = getAccessToken(radwareSessionToken);
            final RadwareAppListResponse radwareAppList = getAppListsForRadware(authToken);
            for (DistRadwareCacheHistoryModel radwareData : radwareClearHistory) {
                distrelecRadwareAPIClient.clearCDNCacheOnRadware(getAppCode(radwareAppList, radwareData.getRadwareId()), authToken);
                distRadwareHistoryDAO.updateRadwareCacheClearFlag(radwareData.getRadwareId(), Boolean.FALSE);
            }
        }
    }

    private boolean isCacheClearingRequired(String radwareAppCode) {
        Date previousCacheClearTime = distRadwareHistoryDAO.getLastCacheClearDate(radwareAppCode);
        Date currentDate = new Date();
        long difference = currentDate.getTime() - previousCacheClearTime.getTime();
        long minutesDifference = TimeUnit.MILLISECONDS.toMinutes(difference);
        LOG.debug("Difference in date and time" + minutesDifference);
        int cacheTimeDfference = Integer.valueOf(configurationService.getConfiguration().getString("radware.cache.difference"));
        return minutesDifference > cacheTimeDfference;
    }

    private String getAppCode(final RadwareAppListResponse radwareAppList, final String name) {
        for (final RadwareApp radwareApp : radwareAppList.getContent()) {
            if (name.equalsIgnoreCase(radwareApp.getName())) {
                return radwareApp.getId();
            }
        }
        return BLANK_STRING;

    }

    private RadwareAppListResponse getAppListsForRadware(final String authToken) throws JsonProcessingException, DistRadwareAPIException {
        final String appListAPIResponse = distrelecRadwareAPIClient.getRadwareAppListResponse(authToken);
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final RadwareAppListResponse radwareAppList = objectMapper.readValue(appListAPIResponse, RadwareAppListResponse.class);
        return radwareAppList;
    }

    private String getAccessToken(final String radwareSessionToken) throws DistRadwareAPIException {
        final String authorizationHtml = callAuthorizationAPI(radwareSessionToken);
        final Document doc = Jsoup.parse(authorizationHtml);
        final Elements inputs = doc.getElementsByAttribute(NAME_ATTRIBUTE);
        String accessToken = BLANK_STRING;
        for (final org.jsoup.nodes.Element input : inputs) {
            if (NAME_ATTRIBUTE_VALUE.equalsIgnoreCase(input.attr(NAME_ATTRIBUTE))) {
                accessToken = input.attr("value");
                break;
            }
        }
        return accessToken;
    }

    private String callRadwareAuthenticationAPI() throws JsonProcessingException, DistRadwareAPIException {
        final String request = createRadwareAuthenticationRequest();
        final String response = distrelecRadwareAPIClient.getRadwareSessionToken(request);
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final AuthenticationResponse responseObject = objectMapper.readValue(response, AuthenticationResponse.class);
        LOG.debug("API Response" + responseObject.getSessionToken());
        return responseObject.getSessionToken();
    }

    private String createRadwareAuthenticationRequest() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Authentication authentication = new Authentication();
        authentication.setUsername(configurationService.getConfiguration().getString("radware.api.user"));
        authentication.setPassword(configurationService.getConfiguration().getString("radware.api.password"));
        final AuthenticationOptions options = new AuthenticationOptions();
        options.setMultiOptionalFactorEnroll(Boolean.TRUE);
        options.setWarnBeforePasswordExpired(Boolean.TRUE);
        authentication.setOptions(options);
        return objectMapper.writeValueAsString(authentication);
    }

    private String callAuthorizationAPI(final String sessionToken) throws DistRadwareAPIException {
        final String authResponse = distrelecRadwareAPIClient.getAuthTokenResponse(sessionToken);
        LOG.debug("authResponse::" + authResponse);
        return authResponse;
    }

}
