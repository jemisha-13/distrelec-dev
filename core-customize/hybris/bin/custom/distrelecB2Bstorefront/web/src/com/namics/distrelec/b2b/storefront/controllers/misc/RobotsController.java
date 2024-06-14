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

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.ENVIRONMENT_ISPROD;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.namics.distrelec.b2b.storefront.controllers.AbstractController;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * Controller for web robots instructions.
 */
@Controller
public class RobotsController extends AbstractController {

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private ConfigurationService configurationService;

    private static final Logger LOG = LogManager.getLogger(RobotsController.class);

    @RequestMapping(value = "/robots.txt", method = RequestMethod.GET)
    public String getRobots(final Model model, final HttpServletRequest request) {

        boolean useHttps = false;
        final boolean httpsOnly = cmsSiteService.getCurrentSite().isHttpsOnly();
        if (httpsOnly && request.isSecure()) {
            useHttps = false;
        } else if (httpsOnly && !request.isSecure()) {
            useHttps = true;
        } else if (!httpsOnly && request.isSecure()) {
            useHttps = true;
        }
        final URL requestURL = getURLRequest(request);
        model.addAttribute("siteBaseURL", getBaseUrl(requestURL));
        model.addAttribute("isProd", getConfigurationService().getConfiguration().getBoolean(ENVIRONMENT_ISPROD, Boolean.FALSE));
        return useHttps ? ControllerConstants.Views.Pages.Misc.MiscRobotsSecurePage : ControllerConstants.Views.Pages.Misc.MiscRobotsPage;
    }

    protected URL getURLRequest(final HttpServletRequest request) {
        URL url = null;
        try {
            url = new URL(request.getRequestURL().toString());
        } catch (final MalformedURLException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Problems while setting the <link> content. This url is incorrect:{}", url, e);
            }
            return null;
        }
        return url;
    }

    private StringBuilder getBaseUrl(final URL url) {
        final StringBuilder baseUrl = new StringBuilder();
        baseUrl.append(getHomeURL(url));
        if (url.getPort() != 0 && url.getPort() != -1) {
            baseUrl.append(":").append(url.getPort());
        }
        return baseUrl;
    }

    private String getHomeURL(final URL url) {
        return url.getProtocol() + "://" + url.getHost();
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
