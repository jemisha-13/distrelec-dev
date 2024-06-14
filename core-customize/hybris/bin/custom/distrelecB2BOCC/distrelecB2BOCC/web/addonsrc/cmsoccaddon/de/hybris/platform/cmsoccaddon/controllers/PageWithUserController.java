/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsoccaddon.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.pages.PageFacade;
import de.hybris.platform.cmsoccaddon.data.CMSPageListWsDTO;
import de.hybris.platform.cmsoccaddon.data.CMSPageWsDTO;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.pages.PageAdapterUtil;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.pages.PageListWsDTOAdapter;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.pages.PageWsDTOAdapter;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.pages.PageListWsDTOAdapter.ListAdaptedPages;
import de.hybris.platform.cmsoccaddon.mapping.CMSDataMapper;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.annotation.SecurePortalUnauthenticatedAccess;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.dto.PaginationWsDTO;
import de.hybris.platform.webservicescommons.dto.SortWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller to get cms page data with a list of content slots, each of which contains a list of cms component data
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/cms")
@CacheControl(directive = CacheControlDirective.PRIVATE)
@ApiVersion("v2")
@Tag(name = "Pages")
public class PageWithUserController {
    public static final String DEFAULT_CURRENT_PAGE = "0";

    public static final String DEFAULT_PAGE_SIZE = "10";

    @Resource(name = "cmsDataMapper")
    protected CMSDataMapper dataMapper;

    @Resource(name = "cmsPageFacade")
    private PageFacade pageFacade;

    @Resource(name = "webPaginationUtils")
    private WebPaginationUtils webPaginationUtils;

    @SecurePortalUnauthenticatedAccess
    @GetMapping(value = "/pages")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Operation(summary = "Retrieves the page data and the CMS content slots.", //
            description = "Retrieves the page data and CMS content slots using the pageLabelOrId or the code parameter. "
                          + "If you don't provide a value for one of these parameters, the homepage CMS content slots are retrieved. "
                          + "\nContent pages can be filtered using pageLabelOrId while the other page types can be filtered using the specified code.", //
            operationId = "getPageWithUser")
    @ApiBaseSiteIdAndUserIdParam
    @ApiResponse(responseCode = "200", description = "CMSPageWsDTO", content = @Content(schema = @Schema(implementation = CMSPageWsDTO.class)))
    public PageAdapterUtil.PageAdaptedData getPage(
                                                   @Parameter(description = "Page type", schema = @Schema(allowableValues = { "ContentPage", "ProductPage",
                                                                                                                              "CategoryPage", "CatalogPage" })) //
                                                   @RequestParam(required = true, defaultValue = ContentPageModel._TYPECODE) final String pageType,
                                                   @Parameter(description = "Page Label or Id. If no page has a label that matches the page label exactly, try to find pages whose label starts with a section of the page label. Otherwise, try to find the page by id.\n"
                                                                            + "Note: URL encoding on the page label should be applied when the label contains special characters.", example = "/cart") //
                                                   @RequestParam(required = false) final String pageLabelOrId,
                                                   @Parameter(description = "Page code. Examples: homepage, electronics, cameras, 585.") //
                                                   @RequestParam(required = false) final String code,
                                                   @Parameter(description = "Response configuration. This is the list of fields that should be returned in the response body. Examples: BASIC, DEFAULT, FULL.", schema = @Schema(allowableValues = { "BASIC",
                                                                                                                                                                                                                                                     "DEFAULT",
                                                                                                                                                                                                                                                     "FULL" })) //
                                                   @RequestParam(defaultValue = "DEFAULT") final String fields) throws CMSItemNotFoundException {
        try {
            final AbstractPageData pageData = getPageFacade().getPageData(pageType, pageLabelOrId, code);
            final CMSPageWsDTO page = (CMSPageWsDTO) getDataMapper().map(pageData, fields);

            final PageWsDTOAdapter adapter = new PageWsDTOAdapter();
            return adapter.marshal(page);
        } catch (final ValidationException e) {
            throw new WebserviceValidationException(e.getValidationObject());
        }
    }

    @SecurePortalUnauthenticatedAccess
    @GetMapping(value = "/pages/{pageId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Operation(summary = "Retrieves the page data and CMS content slots using the page identifier.", //
            description = "Retrieves the page data and CMS content slots, each of which contains a list of CMS component data. The page identifier is required.", //
            operationId = "getPageByIdAndUser")
    @ApiBaseSiteIdAndUserIdParam
    @ApiResponse(responseCode = "200", description = "CMSPageWsDTO", content = @Content(schema = @Schema(implementation = CMSPageWsDTO.class)))
    public PageAdapterUtil.PageAdaptedData getPage( //
                                                   @Parameter(description = "Page Id", required = true, example = "cartPage") //
                                                   @PathVariable final String pageId,
                                                   @Parameter(description = "Response configuration. This is the list of fields that should be returned in the response body. Examples: BASIC, DEFAULT, FULL.", schema = @Schema(allowableValues = { "BASIC",
                                                                                                                                                                                                                                                     "DEFAULT",
                                                                                                                                                                                                                                                     "FULL" })) //
                                                   @RequestParam(defaultValue = "DEFAULT") final String fields) throws CMSItemNotFoundException {
        try {
            final AbstractPageData pageData = getPageFacade().getPageData(pageId);
            final CMSPageWsDTO page = (CMSPageWsDTO) getDataMapper().map(pageData, fields);

            final PageWsDTOAdapter adapter = new PageWsDTOAdapter();
            return adapter.marshal(page);
        } catch (final ValidationException e) {
            throw new WebserviceValidationException(e.getValidationObject());
        }
    }

    @SecurePortalUnauthenticatedAccess
    @GetMapping(value = "/sitepages", params = { "currentPage" })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Operation(summary = "Retrieves the page data.", operationId = "getAllPagesWithUser")
    @ApiBaseSiteIdAndUserIdParam
    @ApiResponse(responseCode = "200", description = "CMSPageListWsDTO", content = @Content(schema = @Schema(implementation = CMSPageListWsDTO.class)))
    public ListAdaptedPages getAllPages( //
                                        @Parameter(description = "Page type.", schema = @Schema(allowableValues = { "ContentPage", "ProductPage",
                                                                                                                    "CategoryPage", "CatalogPage" })) //
                                        @RequestParam(required = false) final String pageType, //
                                        @Parameter(description = "Current result page. Default value is 0.") //
                                        @RequestParam(required = true, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
                                        @Parameter(description = "Number of results returned per page. Default value: 10.") //
                                        @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
                                        @Parameter(description = "Sorting method applied to the return results.", example = "uid:asc") //
                                        @RequestParam(required = false) final String sort,
                                        @Parameter(description = "Response configuration. This is the list of fields that should be returned in the response body. Examples: BASIC, DEFAULT, FULL.", schema = @Schema(allowableValues = { "BASIC",
                                                                                                                                                                                                                                          "DEFAULT",
                                                                                                                                                                                                                                          "FULL" })) //
                                        @RequestParam(defaultValue = "DEFAULT") final String fields) {
        try {
            // Creates a SearchPageData object contains requested pagination and sorting information
            final SearchPageData<AbstractCMSComponentData> searchPageData = getWebPaginationUtils().buildSearchPageData(sort,
                                                                                                                        currentPage, pageSize, true);
            final SearchPageData<AbstractPageData> searchResult = getPageFacade().findAllPageDataForType(pageType, searchPageData);
            return formatSearchPageDataResult(fields, searchResult);
        } catch (final ValidationException e) {
            throw new WebserviceValidationException(e.getValidationObject());
        }
    }

    /**
     * Transforms the {@code SearchPageData} containing the list of page data into a {@code ListAdaptedPages} object.
     *
     * @param fields
     *            - the response configuration determining which fields to include in the response
     * @param searchResult
     *            - the configuration containing the search results, pagination and sorting information
     * @return a list of page of type {@link ListAdaptedPages}
     */
    protected ListAdaptedPages formatSearchPageDataResult(final String fields, final SearchPageData<AbstractPageData> searchResult) {
        // Map the results into a CMSPageListWsDTO which is an intermediate WsDTO
        final CMSPageListWsDTO pageListWsDTO = new CMSPageListWsDTO();
        final List<CMSPageWsDTO> pageList = searchResult.getResults().stream()
                                                        .map(pageData -> (CMSPageWsDTO) getDataMapper().map(pageData, fields)) //
                                                        .collect(Collectors.toList());
        pageListWsDTO.setPage(pageList);

        // Marshal the CMSPageListWsDTO into ListAdaptedPages
        final PageListWsDTOAdapter adapter = new PageListWsDTOAdapter();
        final ListAdaptedPages listAdaptedPages = adapter.marshal(pageListWsDTO);

        // Convert pagination and sorting data into ListAdaptedPages' WsDTO
        final PaginationWsDTO paginationWsDTO = getWebPaginationUtils().buildPaginationWsDto(searchResult.getPagination());
        final List<SortWsDTO> sortWsDTOList = getWebPaginationUtils().buildSortWsDto(searchResult.getSorts());
        listAdaptedPages.setPagination(paginationWsDTO);
        listAdaptedPages.setSorts(sortWsDTOList);

        return listAdaptedPages;
    }

    protected PageFacade getPageFacade() {
        return pageFacade;
    }

    public void setPageFacade(final PageFacade pageFacade) {
        this.pageFacade = pageFacade;
    }

    protected CMSDataMapper getDataMapper() {
        return dataMapper;
    }

    public void setDataMapper(final CMSDataMapper dataMapper) {
        this.dataMapper = dataMapper;
    }

    public WebPaginationUtils getWebPaginationUtils() {
        return webPaginationUtils;
    }

    public void setWebPaginationUtils(final WebPaginationUtils webPaginationUtils) {
        this.webPaginationUtils = webPaginationUtils;
    }
}
