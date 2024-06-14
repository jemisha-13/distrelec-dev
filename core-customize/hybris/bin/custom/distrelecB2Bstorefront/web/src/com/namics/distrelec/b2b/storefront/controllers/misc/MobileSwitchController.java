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
package com.namics.distrelec.b2b.storefront.controllers.misc;

import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import com.namics.distrelec.b2b.storefront.attributes.Attributes;
import com.namics.distrelec.b2b.storefront.controllers.AbstractController;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for the mobile page switch.
 */
@Controller
public class MobileSwitchController extends AbstractController {

    protected static final Logger LOG = Logger.getLogger(MobileSwitchController.class);

    @Autowired
    private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    @Autowired
    private CMSSiteService cmsSiteService;

    @RequestMapping(value = "/switchMobile", method = RequestMethod.GET)
    public String switchMobile(final HttpServletRequest request, final HttpServletResponse response) {
        // remove forcedesktop cookie
        final Cookie[] cookies = request.getCookies();
        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals(Attributes.FORCE_DESKTOP.getName())) {
                cookies[i].setPath("/");
                cookies[i].setMaxAge(0);
                cookies[i].setValue("");
                response.addCookie(cookies[i]);
                break;
            }
        }
        // redirect to the mobile page
        return addFasterizeCacheControlParameter(
                REDIRECT_PREFIX + distSiteBaseUrlResolutionService.getMobileUrlForSite(cmsSiteService.getCurrentSite(), request.isSecure(), ""));
    }

}
