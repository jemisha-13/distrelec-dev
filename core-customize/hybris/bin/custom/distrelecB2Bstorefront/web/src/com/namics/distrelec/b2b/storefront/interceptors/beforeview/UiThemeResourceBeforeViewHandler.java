package com.namics.distrelec.b2b.storefront.interceptors.beforeview;

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hybris.platform.acceleratorstorefrontcommons.interceptors.BeforeViewHandler;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import de.hybris.platform.acceleratorfacades.device.DeviceDetectionFacade;
import de.hybris.platform.acceleratorfacades.device.data.DeviceData;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.enums.SiteTheme;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;

/**
 * Interceptor to setup the paths to the UI resource paths in the model before passing it to the view.
 * 
 * Sets up the path to the web accessible UI resources for the following: * The current site * The current theme * The common resources
 * 
 * All of these paths are qualified by the current UiExperienceLevel
 */
public class UiThemeResourceBeforeViewHandler implements BeforeViewHandler {
    protected static final String COMMON = "common";

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private UiExperienceService uiExperienceService;

    @Autowired
    private DeviceDetectionFacade deviceDetectionFacade;

    private String defaultThemeName;

    protected String getDefaultThemeName() {
        return defaultThemeName;
    }

    @Required
    public void setDefaultThemeName(final String defaultThemeName) {
        this.defaultThemeName = defaultThemeName;
    }

    @Override
    public void beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelAndView modelAndView) {
        final CMSSiteModel currentSite = cmsSiteService.getCurrentSite();

        final String siteName = currentSite.getUid();
        final String themeName = getThemeNameForSite(currentSite);
        final String uiExperienceCode = uiExperienceService.getUiExperienceLevel().getCode();
        final String uiExperienceCodeLower = StringUtils.lowerCase(uiExperienceCode);
        final String contextPath = request.getContextPath();

        final String siteResourcePath = contextPath + "/_ui/" + uiExperienceCodeLower + "/site-" + siteName;
        final String themeResourcePath = contextPath + "/_ui/" + uiExperienceCodeLower + "/theme-" + themeName;
        final String commonResourcePath = contextPath + "/_ui/" + uiExperienceCodeLower + "/" + COMMON;

        modelAndView.addObject("siteResourcePath", siteResourcePath);
        modelAndView.addObject("themeResourcePath", themeResourcePath);
        modelAndView.addObject("commonResourcePath", commonResourcePath);

        modelAndView.addObject("uiExperienceLevel", uiExperienceCode);

        final String detectedUiExperienceCode = uiExperienceService.getDetectedUiExperienceLevel().getCode();
        modelAndView.addObject("detectedUiExperienceCode", detectedUiExperienceCode);

        final UiExperienceLevel overrideUiExperienceLevel = uiExperienceService.getOverrideUiExperienceLevel();
        if (overrideUiExperienceLevel == null) {
            modelAndView.addObject("uiExperienceOverride", Boolean.FALSE);
        } else {
            modelAndView.addObject("uiExperienceOverride", Boolean.TRUE);
            modelAndView.addObject("overrideUiExperienceCode", overrideUiExperienceLevel.getCode());
        }

        final DeviceData currentDetectedDevice = deviceDetectionFacade.getCurrentDetectedDevice();
        modelAndView.addObject("detectedDevice", currentDetectedDevice);
    }

    protected String getThemeNameForSite(final CMSSiteModel site) {
        final SiteTheme theme = site.getTheme();
        if (theme != null) {
            final String themeCode = theme.getCode();
            if (themeCode != null && !themeCode.isEmpty()) {
                return themeCode;
            }
        }
        return getDefaultThemeName();
    }
}
