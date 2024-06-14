package com.namics.distrelec.occ.core.v2.controller;

import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.CDN_TTL;
import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.LOCAL_TTL;
import static de.hybris.platform.webservicescommons.cache.CacheControlDirective.PUBLIC;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.namics.distrelec.occ.core.readonly.ReadOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.namics.distrelec.b2b.core.model.DistContentPageMappingModel;
import com.namics.distrelec.b2b.core.service.DistCmsPageService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.occ.core.rewrite.ws.dto.DistContentPageMappingWsDTO;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "Mapping rules")
public class DistContentPageMappingController extends BaseController {

    private final Pattern CONTENT_PAGE_PATTERN = Pattern.compile(".*/cms/([^?]+)(\\?.*)?");

    @Autowired
    DistCmsPageService distCmsPageService;

    @Autowired
    @Qualifier("contentPageUrlResolver")
    DistUrlResolver<ContentPageModel> contentPageUrlResolver;

    @ReadOnly
    @GetMapping(value = "/{baseSiteId}/mapping-rules")
    @CacheControl(directive = PUBLIC, maxAge = LOCAL_TTL, sMaxAge = CDN_TTL)
    @ResponseBody
    @Operation(operationId = "getContentPageMappings", summary = "Get content page mappings.", description = "Get content page mappings rewrite rules.")
    public List<DistContentPageMappingWsDTO> getContentPageMappings(@PathVariable String baseSiteId,
                                                                    @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        Collection<DistContentPageMappingModel> mappings = distCmsPageService.getAllActiveContentPageMappings(baseSiteId);
        return getDataMapper().mapAsList(mappings, DistContentPageMappingWsDTO.class, fields);
    }

    @ReadOnly
    @GetMapping("/{baseSiteId}/content-page-url")
    @CacheControl(directive = PUBLIC, maxAge = LOCAL_TTL, sMaxAge = CDN_TTL)
    @ResponseBody
    @ApiBaseSiteIdParam
    public String checkContentPageUrl(@Parameter(allowReserved = true) @RequestParam String cmsPagePath) {
        Matcher matcher = CONTENT_PAGE_PATTERN.matcher(cmsPagePath);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Url path does not match cms pattern: " + cmsPagePath);
        }

        String pageLabel = matcher.group(1);

        ContentPageModel contentPageModel = null;
        try {
            contentPageModel = distCmsPageService.getPageForLabel(pageLabel);
        } catch (CMSItemNotFoundException e) {
            // ignore exception
        }

        if (contentPageModel != null) {
            String resolvedPath = contentPageUrlResolver.resolve(contentPageModel);

            if (!cmsPagePath.equals(resolvedPath)) {
                return resolvedPath;
            }
        }

        return null;
    }
}
