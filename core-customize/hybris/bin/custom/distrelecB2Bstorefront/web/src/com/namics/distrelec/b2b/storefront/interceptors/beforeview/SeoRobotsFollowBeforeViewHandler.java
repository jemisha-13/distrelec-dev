/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.storefront.interceptors.beforeview;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.namics.distrelec.b2b.storefront.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.interceptors.BeforeViewHandler;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * {@code SeoRobotsFollowBeforeViewHandler}
 * 
 */
public class SeoRobotsFollowBeforeViewHandler implements BeforeViewHandler {

    private static final Pattern INDEX_FOLLOW_PATTERN = Pattern.compile("(.+/p/.+)|(.+/c/.+)|(.+/manufacturer/.+)|(.+/cms/.+)");
    private static final String INDEX_FOLLOW = "index, follow";
    private static final String NOINDEX_FOLLOW = "noindex, follow";
    private static final String NOINDEX_NOFOLLOW = "noindex, nofollow";

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.storefront.interceptors.beforeview.BeforeViewHandler#beforeView(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, org.springframework.web.servlet.ModelAndView)
     */
    @Override
    public void beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelAndView modelAndView) {
        // Check to see if the controller has specified a Index/Follow directive for robots
        if ((modelAndView != null && !modelAndView.getModel().containsKey(ThirdPartyConstants.SeoRobots.META_ROBOTS))) {
                // Build a default directive
                String robotsValue = NOINDEX_NOFOLLOW;

                if (RequestMethod.GET.name().equalsIgnoreCase(request.getMethod())) {
                    robotsValue = INDEX_FOLLOW_PATTERN.matcher(request.getRequestURI()).matches() ? INDEX_FOLLOW : NOINDEX_FOLLOW;
                }

                modelAndView.addObject("metaRobots", robotsValue);
        }
    }
}
