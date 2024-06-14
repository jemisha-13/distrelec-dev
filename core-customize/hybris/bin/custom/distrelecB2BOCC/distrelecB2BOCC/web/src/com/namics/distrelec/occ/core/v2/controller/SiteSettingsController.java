package com.namics.distrelec.occ.core.v2.controller;

import com.namics.distrelec.occ.core.readonly.ReadOnly;
import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.CDN_TTL;
import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.LOCAL_TTL;
import static de.hybris.platform.webservicescommons.cache.CacheControlDirective.NO_CACHE;
import static de.hybris.platform.webservicescommons.cache.CacheControlDirective.PUBLIC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.occ.core.basesite.data.DistrelecSiteSettingsDataList;

import de.hybris.platform.commercewebservicescommons.dto.basesite.DistrelecSiteSettingsDataListWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;

@Controller
@RequestMapping(value = "/{baseSiteId}/allsitesettings")
public class SiteSettingsController extends BaseController {

    @Autowired
    private DistrelecStoreSessionFacade distrelecStoreSessionFacade;

    @Autowired
    private DistUserFacade distUserFacade;

    @ReadOnly
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @CacheControl(directive = PUBLIC, maxAge = LOCAL_TTL, sMaxAge = CDN_TTL)
    @ApiBaseSiteIdParam
    @Operation(operationId = "getAllSiteSettings", summary = "Get all site Settings", description = "Get all site settings from the store session. Use only for anonymous users so user specific responses doesn't get cached!")
    public ResponseEntity<DistrelecSiteSettingsDataListWsDTO> getAllSiteSettings(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        if (!distUserFacade.isAnonymousUser()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        DistrelecSiteSettingsDataList list = distrelecStoreSessionFacade.getAllSiteSettingsForOCC();
        return ResponseEntity.ok(getDataMapper().map(list, DistrelecSiteSettingsDataListWsDTO.class, fields));
    }

    /**
     * Returns mutable all site settings data which is not cached. Intended to be used for logged in users and wherever we need it without cache.
     */
    @ReadOnly
    @RequestMapping(value = "/mutable", method = RequestMethod.GET)
    @ResponseBody
    @CacheControl(directive = NO_CACHE)
    @ApiBaseSiteIdParam
    @Operation(operationId = "getAllSiteSettings", summary = "Get all site Settings", description = "Get all site settings from the store session")
    public ResponseEntity<DistrelecSiteSettingsDataListWsDTO> getAllSiteSettingsMutable(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        DistrelecSiteSettingsDataList list = distrelecStoreSessionFacade.getAllSiteSettingsForOCC();
        return ResponseEntity.ok(getDataMapper().map(list, DistrelecSiteSettingsDataListWsDTO.class, fields));
    }
}
