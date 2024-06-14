/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsoccaddon.controllers;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.items.ComponentItemFacade;
import de.hybris.platform.cmsoccaddon.data.ComponentListWsDTO;
import de.hybris.platform.cmsoccaddon.data.ComponentWsDTO;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.components.ComponentListWsDTOAdapter;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.components.ComponentWsDTOAdapter;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.components.ComponentAdapterUtil.ComponentAdaptedData;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.components.ComponentListWsDTOAdapter.ListAdaptedComponents;
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
 * Default Controller for CMS Component. This controller is used for all CMS components with user id path that don\"t have a specific
 * controller to handle them.
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/cms")
@CacheControl(directive = CacheControlDirective.PRIVATE)
@ApiVersion("v2")
@Tag(name = "Components")
public class ComponentWithUserController {
    public static final String DEFAULT_CURRENT_PAGE = "0";

    public static final String DEFAULT_PAGE_SIZE = "10";

    @Resource(name = "componentItemFacade")
    private ComponentItemFacade componentItemFacade;

    @Resource(name = "cmsDataMapper")
    private CMSDataMapper dataMapper;

    @Resource(name = "webPaginationUtils")
    private WebPaginationUtils webPaginationUtils;

    @SecurePortalUnauthenticatedAccess
    @GetMapping(value = "/components/{componentId}")
    @ResponseBody
    @Operation(summary = "Retrieves the component data.", description = "Retrieves the CMS component data.", operationId = "getComponentByIdAndUser")
    @ApiBaseSiteIdAndUserIdParam
    @ApiResponse(responseCode = "200", description = "ComponentWsDTO", content = @Content(schema = @Schema(implementation = ComponentWsDTO.class)))
    public ComponentAdaptedData getComponentByIdAndUser(
                                                        @Parameter(description = "Component identifier", required = true, example = "PageTitleComponent") @PathVariable final String componentId,
                                                        @Parameter(description = "Catalog code", example = "electronics") @RequestParam(required = false) final String catalogCode,
                                                        @Parameter(description = "Product code", example = "553637") @RequestParam(required = false) final String productCode,
                                                        @Parameter(description = "Category code", example = "576") @RequestParam(required = false) final String categoryCode,
                                                        @Parameter(description = "Response configuration. This is the list of fields that should be returned in the response body. Examples: BASIC, DEFAULT, FULL.", schema = @Schema(allowableValues = { "BASIC",
                                                                                                                                                                                                                                                          "DEFAULT",
                                                                                                                                                                                                                                                          "FULL" })) @RequestParam(defaultValue = "DEFAULT") final String fields)
                                                                                                                                                                                                                                                                                                                                  throws CMSItemNotFoundException {
        try {
            final AbstractCMSComponentData componentData = getComponentItemFacade().getComponentById(componentId, categoryCode,
                                                                                                     productCode, catalogCode);
            final ComponentWsDTO data = (ComponentWsDTO) getDataMapper().map(componentData, fields);

            final ComponentWsDTOAdapter adapter = new ComponentWsDTOAdapter();
            return adapter.marshal(data);
        } catch (final ValidationException e) {
            throw new WebserviceValidationException(e.getValidationObject());
        }
    }

    @SecurePortalUnauthenticatedAccess
    @GetMapping(value = "/components")
    @ResponseBody
    @Operation(summary = "Retrieves the component data.", description = "Retrieves the CMS components using the specified identifiers. "
                                                                        + "If you don't provide any component identifiers, all of the components will be retrieved. "
                                                                        + "\nThe components list is filtered by the specified catalog, product, or category restrictions."
                                                                        + "The result is sorted according to the sort parameter.", operationId = "getComponentsByIdsAndUser")
    @ApiBaseSiteIdAndUserIdParam
    @ApiResponse(responseCode = "200", description = "ComponentIDListWsDTO", content = @Content(schema = @Schema(implementation = ComponentListWsDTO.class)))
    public ListAdaptedComponents findComponentsByIdsAndUser( //
                                                            @Parameter(description = "List of Component identifiers", example = "[\n"
                                                                                                                                + "\t\t\"FacebookLink\",\n"
                                                                                                                                + "\t\t\"ContactUsLink\",\n"
                                                                                                                                + "\t]") @RequestParam(required = false) final List<String> componentIds,
                                                            @Parameter(description = "Catalog code", example = "electronics") @RequestParam(required = false) final String catalogCode,
                                                            @Parameter(description = "Product code", example = "553637") @RequestParam(required = false) final String productCode,
                                                            @Parameter(description = "Category code", example = "576") @RequestParam(required = false) final String categoryCode,
                                                            @Parameter(description = "Response configuration. This is the list of fields that should be returned in the response body. Examples: BASIC, DEFAULT, FULL.", schema = @Schema(allowableValues = { "BASIC",
                                                                                                                                                                                                                                                              "DEFAULT",
                                                                                                                                                                                                                                                              "FULL" })) @RequestParam(defaultValue = "DEFAULT") final String fields,
                                                            @Parameter(description = "Current result page. Default value is 0.") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
                                                            @Parameter(description = "Number of results returned per page. Default value: 10.") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
                                                            @Parameter(description = "Sorting method applied to the return results.", example = "uid:asc") @RequestParam(required = false) final String sort) {
        final SearchPageData<AbstractCMSComponentData> searchPageDataInput = getSearchPageData(currentPage, pageSize, sort);
        return getComponentsByIds(componentIds, catalogCode, productCode, categoryCode, fields, searchPageDataInput);
    }

    /**
     * Finds cms components by the specified IDs. When none is provided, this will retrieve all components. The
     * components list will be filtered by the given catalog, product or category restrictions, as well as by the
     * pagination information. The result will be sorted in the specified order.
     *
     * @param componentIds
     *            - the list of component uid's
     * @param catalogCode
     *            - the catalog code determining the restriction to apply
     * @param productCode
     *            - the product code determining the restriction to apply
     * @param categoryCode
     *            - the category code determining the restriction to apply
     * @param fields
     *            - the response configuration determining which fields to include in the response
     * @param searchPageDataInput
     *            - the search page information determining the page index
     * @return a list of cms component data
     */
    protected ListAdaptedComponents getComponentsByIds(final List<String> componentIds, final String catalogCode,
                                                       final String productCode, final String categoryCode, final String fields,
                                                       SearchPageData<AbstractCMSComponentData> searchPageDataInput) {
        try {
            // Get search result in a SearchPageData object contains search results with applied pagination and sorting information
            final SearchPageData<AbstractCMSComponentData> searchPageDataResult = getComponentItemFacade()
                                                                                                          .getComponentsByIds(componentIds, categoryCode,
                                                                                                                              productCode, catalogCode,
                                                                                                                              searchPageDataInput);

            return formatSearchPageDataResult(fields, searchPageDataResult);
        } catch (final ValidationException e) {
            throw new WebserviceValidationException(e.getValidationObject());
        }
    }

    /**
     * Finds SearchPageData cms components.
     *
     * @param currentPage
     *            - the pagination information determining the page index
     * @param pageSize
     *            - the pagination information determining the page size
     * @param sort
     *            - the sorting information determining which field to sort on and the ordering direction (ASC or DESC)
     * @return a list of search page data
     */
    protected SearchPageData<AbstractCMSComponentData> getSearchPageData(final int currentPage, final int pageSize, final String sort) {
        try {
            // Creates a SearchPageData object contains requested pagination and sorting information
            return getWebPaginationUtils().buildSearchPageData(sort,
                                                               currentPage, pageSize, true);
        } catch (final ValidationException e) {
            throw new WebserviceValidationException(e.getValidationObject());
        }
    }

    /**
     * Transforms the {@code SearchPageData} containing the list of cms component data into a
     * {@code ListAdaptedComponents} object.
     *
     * @param fields
     *            - the response configuration determining which fields to include in the response
     * @param searchPageDataResult
     *            - the configuration containing the pagination and sorting information
     * @return a list of components of type {@link ListAdaptedComponents}
     */
    protected ListAdaptedComponents formatSearchPageDataResult(final String fields,
                                                               final SearchPageData<AbstractCMSComponentData> searchPageDataResult) {
        // Map the results into a ComponentListWsDTO which is an intermediate WsDTO
        final ComponentListWsDTO componentListWsDTO = new ComponentListWsDTO();
        final List<ComponentWsDTO> componentWsDTOList = getDataMapper().mapAsList(searchPageDataResult.getResults(),
                                                                                  ComponentWsDTO.class, fields);
        componentListWsDTO.setComponent(componentWsDTOList);

        // Marshal the ComponentListWsDTO into ListAdaptedComponents
        final ComponentListWsDTOAdapter adapter = new ComponentListWsDTOAdapter();
        final ListAdaptedComponents listAdaptedComponent = adapter.marshal(componentListWsDTO);

        // Convert pagination and sorting data into ListAdaptedComponents' WsDTO
        final PaginationWsDTO paginationWsDTO = getWebPaginationUtils().buildPaginationWsDto(searchPageDataResult.getPagination());
        final List<SortWsDTO> sortWsDTOList = getWebPaginationUtils().buildSortWsDto(searchPageDataResult.getSorts());
        listAdaptedComponent.setPagination(paginationWsDTO);
        listAdaptedComponent.setSorts(sortWsDTOList);

        return listAdaptedComponent;
    }

    public ComponentItemFacade getComponentItemFacade() {
        return componentItemFacade;
    }

    public void setComponentItemFacade(final ComponentItemFacade componentItemFacade) {
        this.componentItemFacade = componentItemFacade;
    }

    public CMSDataMapper getDataMapper() {
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
