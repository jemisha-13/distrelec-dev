/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to detect search robots.
 * 
 * @author ascherrer, Namics AG
 * @since Distrelec 3.1.14
 * 
 */
public class SearchRobotDetector {
    private static final Logger LOG = Logger.getLogger(SearchRobotDetector.class.getName());
    private static final String HTTP_HEADER_USER_AGENT = "User-Agent";

    private List<String> searchRobotUserAgents = new ArrayList<String>();

    public boolean isSearchRobot(final HttpServletRequest request) {
        if (getSearchRobotUserAgents() != null && !getSearchRobotUserAgents().isEmpty()) {
            final String currentUserAgent = request.getHeader(HTTP_HEADER_USER_AGENT);
            if (StringUtils.isNotBlank(currentUserAgent)) {
                for (final String userAgent : getSearchRobotUserAgents()) {
                    if (currentUserAgent.contains(userAgent)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Search robot detected [" + currentUserAgent + "]");
                        }
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public List<String> getSearchRobotUserAgents() {
        return searchRobotUserAgents;
    }

    public void setSearchRobotUserAgents(final List<String> searchRobotUserAgents) {
        this.searchRobotUserAgents = searchRobotUserAgents;
    }

}
