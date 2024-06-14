/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Test class for SearchRobotDetector.
 * 
 * @author ascherrer, Namics AG
 * @since Distrelec 3.1.14
 * 
 */
@UnitTest
public class SearchRobotDetectorTest {
    private final SearchRobotDetector searchRobotDetector = new SearchRobotDetector();

    private static final String USERAGENT_CHROME = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.65 Safari/537.36";
    private static final String USERAGENT_GOOGLE = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
    private static final String USERAGENT_YAHOO = "Mozilla/5.0 (compatible; Yahoo! Slurp; http://help.yahoo.com/help/us/ysearch/slurp)";

    @Before
    public void init() {
        final List<String> searchRobotUserAgents = new ArrayList<String>();
        searchRobotUserAgents.add("adidxbot");
        searchRobotUserAgents.add("AdsBot-Google");
        searchRobotUserAgents.add("AhrefsBot");
        searchRobotUserAgents.add("archive.org_bot");
        searchRobotUserAgents.add("Baiduspider");
        searchRobotUserAgents.add("bingbot");
        searchRobotUserAgents.add("facebookexternalhit");
        searchRobotUserAgents.add("Exabot");
        searchRobotUserAgents.add("GomezAgent");
        searchRobotUserAgents.add("Googlebot");
        searchRobotUserAgents.add("rogerbot-crawler");
        searchRobotUserAgents.add("Sogou web spider");
        searchRobotUserAgents.add("ca-crawler");
        searchRobotUserAgents.add("YandexBot");
        searchRobotUserAgents.add("YandexImages");
        searchRobotUserAgents.add("Yahoo! Slurp");
        searchRobotDetector.setSearchRobotUserAgents(searchRobotUserAgents);
    }

    @Test
    public void detectChrome() {
        final HttpServletRequest request = new FakedUserAgentRequest(new MockMultipartHttpServletRequest(), USERAGENT_CHROME);
        Assert.assertFalse("Chrome browser detection failed.", searchRobotDetector.isSearchRobot(request));
    }

    @Test
    public void detectGoogle() {
        final HttpServletRequest request = new FakedUserAgentRequest(new MockMultipartHttpServletRequest(), USERAGENT_GOOGLE);
        Assert.assertTrue("Google bot detection failed.", searchRobotDetector.isSearchRobot(request));
    }

    @Test
    public void detectYahoo() {
        final HttpServletRequest request = new FakedUserAgentRequest(new MockMultipartHttpServletRequest(), USERAGENT_YAHOO);
        Assert.assertTrue("Yahoo bot detection failed.", searchRobotDetector.isSearchRobot(request));
    }

    /**
     * It's not possible to simply set a header value (user agent). A wrapper must be used.
     */
    public class FakedUserAgentRequest extends HttpServletRequestWrapper {
        private final String userAgent;

        public FakedUserAgentRequest(final HttpServletRequest request, final String userAgent) {
            super(request);
            this.userAgent = userAgent;
        }

        @Override
        public String getHeader(final String name) {
            if ("User-Agent".equals(name)) {
                return userAgent;
            }
            return super.getHeader(name);
        }
    }
}
