/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.controller;

import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.CDN_TTL;
import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.LOCAL_TTL;
import static de.hybris.platform.webservicescommons.cache.CacheControlDirective.PUBLIC;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.namics.distrelec.occ.core.readonly.ReadOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.occ.core.basesite.data.BaseSiteDataList;
import com.namics.distrelec.occ.core.dto.basesite.BaseSiteMutableListWsDTO;

import de.hybris.platform.commercefacades.basesite.data.BaseSiteData;
import de.hybris.platform.commercefacades.basesites.BaseSiteFacade;
import de.hybris.platform.commercewebservicescommons.annotation.SecurePortalUnauthenticatedAccess;
import de.hybris.platform.commercewebservicescommons.dto.basesite.BaseSiteListWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping(value = "/basesites")
@Tag(name = "Base Sites")
public class BaseSitesController extends BaseController {

    private static final String HOST_PREFIX = "https://";

    @Resource(name = "baseSiteFacade")
    private BaseSiteFacade baseSiteFacade;

    @Autowired
    private DistUserService distUserService;

    /**
     * Returns base site data which is not supposed to be changed during runtime and it is cached.
     */
    @ReadOnly
    @SecurePortalUnauthenticatedAccess
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @CacheControl(directive = PUBLIC, maxAge = LOCAL_TTL, sMaxAge = CDN_TTL)
    @Operation(operationId = "getBaseSites", summary = "Get all base sites.", description = "Get all base sites with corresponding base stores details in FULL mode.")
    public BaseSiteListWsDTO getBaseSites(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final BaseSiteDataList baseSiteDataList = getBaseSiteDataList();
        return getDataMapper().map(baseSiteDataList, BaseSiteListWsDTO.class, fields);
    }

    /**
     * Returns mutable base site data which is not cached.
     */
    @ReadOnly
    @RequestMapping(value = "/mutable", method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getMutableBaseSiteData", summary = "Get all base site mutable data.")
    public BaseSiteMutableListWsDTO getMutableBaseSiteData(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
                                                           HttpServletRequest request, HttpServletResponse response) {
        final BaseSiteDataList baseSiteDataList = getBaseSiteDataList();

        if (isMaintenanceActive(baseSiteDataList)) {
            // do not cache at cdn because it needs to be checked for all customers as internal bypasses maintenance
            response.setHeader("Cache-Control", "no-store, max-age=0, stale-if-error=0");
        } else {
            // it can be cached by cdn for short time
            response.setHeader("Cache-Control", "public, max-age=60, stale-while-revalidate=60, stale-if-error=0");
        }

        resetMaintenanceFlagsForInternalIps(request, baseSiteDataList);
        return getDataMapper().map(baseSiteDataList, BaseSiteMutableListWsDTO.class, fields);
    }

    protected BaseSiteDataList getBaseSiteDataList() {
        final List<BaseSiteData> allBaseSites = baseSiteFacade.getAllBaseSites();
        final List<String> internalIps = getInternalIpsList();
        final BaseSiteDataList baseSiteDataList = new BaseSiteDataList();
        baseSiteDataList.setBaseSites(allBaseSites);
        baseSiteDataList.setInternalIps(internalIps);
        return baseSiteDataList;
    }

    protected List<String> getInternalIpsList() {
        String internalIps = distUserService.getInternalIps();
        String[] internalIpsArray = internalIps.split(",");
        return List.of(internalIpsArray);
    }

    protected void resetMaintenanceFlagsForInternalIps(HttpServletRequest request, BaseSiteDataList baseSiteDataList) {
        boolean accessFromInternalIp = distUserService.accessFromInternalIp(request);

        if (accessFromInternalIp) {
            baseSiteDataList.getBaseSites().stream()
                            .filter(baseSite -> baseSite.isMaintenanceActive())
                            .forEach(baseSite -> baseSite.setMaintenanceActive(false));
        }
    }

    boolean isMaintenanceActive(BaseSiteDataList baseSiteDataList) {
        boolean maintenanceActive = baseSiteDataList.getBaseSites().stream()
                                                    .anyMatch(BaseSiteData::isMaintenanceActive);
        return maintenanceActive;
    }

    @ReadOnly
    @GetMapping("/match")
    @ResponseBody
    @CacheControl(directive = PUBLIC, maxAge = LOCAL_TTL, sMaxAge = CDN_TTL)
    @Operation(operationId = "getBaseSideIdForHost", summary = "Get base site ID for a host.", description = "Get base site ID for a host based on URL patterns on a base site")
    public String getBaseSideIdForHost(@RequestParam String host) {
        String matchingURL = HOST_PREFIX.concat(host);
        Optional<BaseSiteData> result = baseSiteFacade.getAllBaseSites()
                                                      .stream()
                                                      .filter(baseSite -> isBaseSiteMatchingTheHost(baseSite, matchingURL))
                                                      .findFirst();
        return result.isPresent() ? result.get().getUid() : null;
    }

    private boolean isBaseSiteMatchingTheHost(BaseSiteData baseSite, String url) {
        return emptyIfNull(baseSite.getUrlPatterns()).stream()
                                                     .anyMatch(urlPattern -> Pattern.matches(urlPattern, url));
    }

}
