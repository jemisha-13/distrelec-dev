/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.namics.distrelec.occ.core.eventtracking;

import static java.util.Optional.ofNullable;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author stevo.slavic
 *
 */
public class DefaultRawEventEnricher implements RawEventEnricher {
    private static final Logger LOG = Logger.getLogger(DefaultRawEventEnricher.class);

    private static final String ANONYMOUS_USER = "anonymous";

    private final UserService userService;

    private final BaseSiteService baseSiteService;

    private ObjectMapper objectMapper;

    public DefaultRawEventEnricher(final UserService userService, final BaseSiteService baseSiteService, final ObjectMapper objectMapper) {
        this.userService = userService;
        this.baseSiteService = baseSiteService;
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        this.objectMapper = objectMapper;
    }

    /**
     * @see RawEventEnricher#enrich(String,
     *      HttpServletRequest)
     */
    @Override
    public String enrich(final String json, final HttpServletRequest req) {
        final HttpSession session = req.getSession();
        final String sessionId = session.getId();
        final String timestamp = getCurrentTimestamp();
        String userId = null;
        String userEmail = null;

        final String baseSiteId = getSiteId();
        try {
            final Map<String, Object> jsonObjectMap = objectMapper.readValue(json, Map.class);
            String requestUserId = (String) jsonObjectMap.get("uid");
   			if(StringUtils.isNotBlank(requestUserId) && !ANONYMOUS_USER.equalsIgnoreCase(requestUserId)) {
                userService.setCurrentUser(userService.getUser(requestUserId));
            }
            jsonObjectMap.put("session_id", sessionId);
            jsonObjectMap.put("timestamp", timestamp);
            final UserModel user = userService.getCurrentUser();
            if (user != null && CustomerModel.class.isAssignableFrom(user.getClass())) {
                userId = ((CustomerModel) user).getCustomerID();
                userEmail = ((CustomerModel) user).getContactEmail();
            }
            userId = StringUtils.trimToEmpty(userId);
            userEmail = StringUtils.trimToEmpty(userEmail);
            jsonObjectMap.put("user_id", userId);
            jsonObjectMap.put("user_email", userEmail);
            jsonObjectMap.put("base_site_id", baseSiteId);
            jsonObjectMap.put("ip_address", getIpAddressFromRequest(req));
            jsonObjectMap.put("user_agent", getUserAgentAddressFromRequest(req));

            return objectMapper.writeValueAsString(jsonObjectMap);
        } catch (IOException e) {
            LOG.warn("Unexpected error occurred parsing json. ", e);
            return json;
        }
    }

    protected String getCurrentTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000); // seconds since Unix epoch
    }

    protected String getSiteId() {
        return getCurrentBaseSiteModel().isPresent() ? getCurrentBaseSiteModel().get().getUid() : StringUtils.EMPTY;
    }

    protected Optional<BaseSiteModel> getCurrentBaseSiteModel() {
        return ofNullable(baseSiteService.getCurrentBaseSite());
    }

    private String getIpAddressFromRequest(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress;
    }

    private String getUserAgentAddressFromRequest(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

}
