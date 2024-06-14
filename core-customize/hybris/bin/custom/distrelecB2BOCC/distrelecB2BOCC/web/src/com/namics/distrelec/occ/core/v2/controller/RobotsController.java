package com.namics.distrelec.occ.core.v2.controller;

import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.CDN_TTL;
import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.LOCAL_TTL;
import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.ENVIRONMENT_ISPROD;
import static de.hybris.platform.webservicescommons.cache.CacheControlDirective.PUBLIC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import com.namics.distrelec.occ.core.readonly.ReadOnly;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@CacheControl(directive = PUBLIC, maxAge = LOCAL_TTL, sMaxAge = CDN_TTL)
@Tag(name = "Robots")
public class RobotsController extends BaseController {

    @Autowired
    CMSSiteService cmsSiteService;

    @Autowired
    ConfigurationService configurationService;

    @Autowired
    DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    @ReadOnly
    @RequestMapping(value = "/{baseSiteId}/robots.txt", method = RequestMethod.GET)
    @ApiBaseSiteIdParam
    public String getRobots(Model model) {
        CMSSiteModel cmsSite = cmsSiteService.getCurrentSite();
        String siteBaseUrl = distSiteBaseUrlResolutionService.getWebsiteUrlForSite(cmsSite, true, "");
        model.addAttribute("siteBaseURL", siteBaseUrl);

        boolean isProd = isProd();
        model.addAttribute("isProd", isProd);

        return "pages/misc/miscRobotsPage";
    }

    boolean isProd() {
        boolean isProd = configurationService.getConfiguration().getBoolean(ENVIRONMENT_ISPROD, false);
        return isProd;
    }
}
