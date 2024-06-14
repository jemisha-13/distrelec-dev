package com.namics.distrelec.occ.core.v2.controller;

import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.CDN_TTL;
import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.LOCAL_TTL;
import static de.hybris.platform.webservicescommons.cache.CacheControlDirective.PUBLIC;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.facades.product.DistrelecOutOfStockNotificationFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.product.data.ProductAccessibilityListData;
import com.namics.distrelec.b2b.facades.product.data.ProductAvailabilityData;
import com.namics.distrelec.b2b.facades.product.data.ProductAvailabilityListData;
import com.namics.distrelec.b2b.facades.product.data.ProductEnhancementData;
import com.namics.distrelec.b2b.facades.product.data.ProductEnhancementListData;
import com.namics.distrelec.b2b.facades.snapeda.SnapEdaFacade;
import com.namics.distrelec.occ.core.formatters.WsDateFormatter;
import com.namics.distrelec.occ.core.handler.ProductNotFoundException;
import com.namics.distrelec.occ.core.queues.data.ProductExpressUpdateElementData;
import com.namics.distrelec.occ.core.queues.data.ProductExpressUpdateElementDataList;
import com.namics.distrelec.occ.core.queues.impl.ProductExpressUpdateQueue;
import com.namics.distrelec.occ.core.readonly.ReadOnly;
import com.namics.distrelec.occ.core.stock.CommerceStockFacade;
import com.namics.distrelec.occ.core.v2.helper.ProductsHelper;
import com.namics.distrelec.occ.core.validator.PointOfServiceValidator;
import com.namics.distrelec.occ.product.ws.dto.ProductEnhancementListWsDTO;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.commercefacades.catalog.CatalogFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.commercefacades.product.data.ProductReferencesData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.storefinder.StoreFinderStockFacade;
import de.hybris.platform.commercefacades.storefinder.data.StoreFinderStockSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commercewebservicescommons.dto.basesite.ProductCarouselWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.basesite.ProductCarouselWsDTOList;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductAccessibilityListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductAvailabilityListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductReferenceListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.StockWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.queues.ProductExpressUpdateElementListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductSearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.store.StoreFinderStockSearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.StockSystemException;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "Products")
@RequestMapping(value = "/{baseSiteId}/products")
public class ProductsController extends BaseController {

    private static final String MAX_INTEGER = "2147483647";

    private static final int CATALOG_ID_POS = 0;

    private static final int CATALOG_VERSION_POS = 1;

    private static final String COMMA_SEPARATOR = ",";

    private static final Logger LOG = LoggerFactory.getLogger(ProductsController.class);

    List<ProductOption> PRODUCT_OPTIONS_WITH_CATPLUSSSUPPLIERAID = Arrays.asList(ProductOption.SUMMARY,
                                                                                 ProductOption.DESCRIPTION,
                                                                                 ProductOption.GALLERY,
                                                                                 ProductOption.PROMOTION_LABELS,
                                                                                 ProductOption.COUNTRY_OF_ORIGIN,
                                                                                 ProductOption.DIST_MANUFACTURER,
                                                                                 ProductOption.PRODUCT_INFORMATION,
                                                                                 ProductOption.BREADCRUMBS,
                                                                                 ProductOption.TECH_ATTRIBUTE,
                                                                                 ProductOption.DOWNLOADS,
                                                                                 ProductOption.URL,
                                                                                 ProductOption.METADATA,
                                                                                 ProductOption.ROBOTS,
                                                                                 ProductOption.ADDITIONAL,
                                                                                 ProductOption.BULK_PERMISSION,
                                                                                 ProductOption.CHANNEL_AVAILABILITY);

    List<ProductOption> PRODUCT_OPTIONS_WITHOUT_CATPLUSSSUPPLIERAID = Arrays.asList(ProductOption.SUMMARY,
                                                                                    ProductOption.DESCRIPTION,
                                                                                    ProductOption.GALLERY,
                                                                                    ProductOption.CATEGORIES,
                                                                                    ProductOption.REVIEW,
                                                                                    ProductOption.PROMOTIONS,
                                                                                    ProductOption.VARIANT_FULL,
                                                                                    ProductOption.PROMOTION_LABELS,
                                                                                    ProductOption.COUNTRY_OF_ORIGIN,
                                                                                    ProductOption.DIST_MANUFACTURER,
                                                                                    ProductOption.VIDEOS,
                                                                                    ProductOption.AUDIOS,
                                                                                    ProductOption.IMAGE360,
                                                                                    ProductOption.BREADCRUMBS,
                                                                                    ProductOption.TECH_ATTRIBUTE,
                                                                                    ProductOption.DOWNLOADS,
                                                                                    ProductOption.URL,
                                                                                    ProductOption.METADATA,
                                                                                    ProductOption.ROBOTS,
                                                                                    ProductOption.CLASSIFICATION_AND_PRODUCT_INFORMATION,
                                                                                    ProductOption.ADDITIONAL,
                                                                                    ProductOption.BULK_PERMISSION,
                                                                                    ProductOption.CHANNEL_AVAILABILITY);

    @Resource(name = "storeFinderStockFacade")
    private StoreFinderStockFacade storeFinderStockFacade;

    @Resource(name = "distrelecProductFacade")
    private DistrelecProductFacade productFacade;

    @Resource(name = "wsDateFormatter")
    private WsDateFormatter wsDateFormatter;

    @Resource(name = "commerceStockFacade")
    private CommerceStockFacade commerceStockFacade;

    @Resource(name = "pointOfServiceValidator")
    private PointOfServiceValidator pointOfServiceValidator;

    @Resource(name = "productExpressUpdateQueue")
    private ProductExpressUpdateQueue productExpressUpdateQueue;

    @Resource(name = "catalogFacade")
    private CatalogFacade catalogFacade;

    @Resource(name = "productsHelper")
    private ProductsHelper productsHelper;

    @Autowired
    private DistrelecOutOfStockNotificationFacade outOfStockFacade;

    @Autowired
    private DistProductService productService;

    @Autowired
    private SnapEdaFacade snapEdaFacade;

    @ReadOnly
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getProducts", summary = "Get a list of products and additional data", description = "Returns a list of products and additional data, such as available facets, "
                                                                                                                  + "available sorting, and pagination options. It can also include spelling suggestions. To make spelling suggestions work, you need to make sure "
                                                                                                                  + "that \"enableSpellCheck\" on the SearchQuery is set to \"true\" (by default, it should already be set to \"true\"). You also need to have indexed "
                                                                                                                  + "properties configured to be used for spellchecking.")
    @ApiBaseSiteIdParam
    public ProductSearchPageWsDTO getProducts(
                                              @Parameter(allowReserved = true, description = "Serialized query, free text search, facets. The format of a serialized query: freeTextSearch:sort:facetKey1:facetValue1:facetKey2:facetValue2") @RequestParam(required = false) final String query,
                                              @Parameter(description = "The current result page requested.") @RequestParam(defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
                                              @Parameter(description = "The number of results returned per page.") @RequestParam(defaultValue = "0") final int pageSize,
                                              @Parameter(description = "Sorting method applied to the return results.") @RequestParam(required = false) final String sort,
                                              @Parameter(description = "The context to be used in the search query.") @RequestParam(required = false) final String searchQueryContext,
                                              @Parameter(description = "Fact Finder Session Id.") @RequestParam(required = false) final String sid,
                                              @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
                                              final HttpServletResponse response) {
        final ProductSearchPageWsDTO result = productsHelper.searchProducts(query, currentPage, getPageSizeOrDefault(pageSize),
                                                                            sort, addPaginationField(fields), searchQueryContext, sid);
        setTotalCountHeader(response, result.getPagination());
        return result;
    }

    @RequestMapping(value = "/search", method = RequestMethod.HEAD)
    @Operation(operationId = "countProducts", summary = "Get a header with total number of products.", description = "In the response header, the \"x-total-count\" indicates the total number of products satisfying a query.")
    @ApiBaseSiteIdParam
    public void countProducts(
                              @Parameter(allowReserved = true, description = "Serialized query, free text search, facets. The format of a serialized query: freeTextSearch:sort:facetKey1:facetValue1:facetKey2:facetValue2") @RequestParam(required = false) final String query,
                              @Parameter(description = "Fact Finder Session Id.") @RequestParam(required = false) final String sid,
                              final HttpServletResponse response) {
        final ProductSearchPageWsDTO result = productsHelper.searchProducts(query, 0, 1, null, sid);
        setTotalCountHeader(response, result.getPagination());
    }

    @ReadOnly
    @RequestMapping(value = "/{productCode}", method = RequestMethod.GET)
    @CacheControl(directive = PUBLIC, maxAge = LOCAL_TTL, sMaxAge = CDN_TTL)
    @ResponseBody
    @Operation(operationId = "getProduct", summary = "Get product details.", description = "Returns details of a single product according to a product code.")
    @ApiBaseSiteIdParam
    public ProductWsDTO getProduct(@Parameter(description = "Product identifier", required = true) @PathVariable final String productCode,
                                   @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws Exception {
        ProductModel productModel;
        try {
            productModel = productService.getProductForCode(productCode);
            if (DEFAULT_FIELD_SET.equalsIgnoreCase(fields)) {
                snapEdaFacade.flagProduct(productModel);
            }
        } catch (final UnknownIdentifierException ex) {
            throw new ProductNotFoundException(String.format("Product with code '%s' not found!", productCode));
        }
        if (productModel == null || isNotBlank(productModel.getCatPlusSupplierAID())) {
            throw new ProductNotFoundException(String.format("Product with code '%s' not found!", productCode));
        }

        DistSalesStatusModel salesStatus = productService.getProductSalesStatusModel(productModel);
        if (salesStatus == null) {
            // respond with product not found
            throw new ProductNotFoundException(String.format("Product with code '%s' not found!", productCode));
        }

        ProductData b2bProductData = getProductData(productModel);
        if (Objects.nonNull(b2bProductData)) {
            ProductWsDTO b2bProductWsDTO = getDataMapper().map(b2bProductData, ProductWsDTO.class, fields);
            b2bProductWsDTO.setBusinessOnlyProduct(false);
            productsHelper.setAdditionalAttributes(b2bProductData, b2bProductWsDTO, fields);
            b2bProductWsDTO.setIsDangerousGoods(productsHelper.isDangerousGoods(productModel));
            return b2bProductWsDTO;
        } else {
            throw new ProductNotFoundException(
                                               String.format("Product with code '%s' not found!", productCode));
        }
    }

    private ProductData getProductData(ProductModel productModel) {
        if (isNotBlank(productModel.getCatPlusSupplierAID())) {
            return productFacade.getProductForCodeAndOptions(productModel, PRODUCT_OPTIONS_WITH_CATPLUSSSUPPLIERAID);
        }
        return productFacade.getProductForCodeAndOptions(productModel, PRODUCT_OPTIONS_WITHOUT_CATPLUSSSUPPLIERAID);
    }

    @ReadOnly
    @RequestMapping(value = "/accessibility", method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getAccessibilityForProductCode", summary = "Get accessibility of product's.", description = "Returns product's accessibility ")
    @ApiBaseSiteIdParam
    public ProductAccessibilityListWsDTO getAccessibilityForProductCode(
                                                                        @Parameter(description = "Product identifier", required = true) @RequestParam final String productCodes,
                                                                        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {

        final List<String> accessibilityData = new ArrayList<>();
        final String[] productCodesList = productCodes.split(" ");

        for (final String product : productCodesList) {
            if (productFacade.enablePunchoutFilterLogic() && productFacade.isProductExcluded(product)) {
                continue;
            }
            final ProductModel productModel = productService.getProductForCode(product);
            if (productModel == null || isNotBlank(productModel.getCatPlusSupplierAID())) {
                continue;
            }
            final DistSalesStatusModel salesStatus = productFacade.getProductSalesStatusModel(product);
            if (salesStatus == null || !salesStatus.isVisibleInShop()) {
                continue;
            }
            // Return only products which are accessible
            accessibilityData.add(product);
        }

        final ProductAccessibilityListData productAccessibilityListData = new ProductAccessibilityListData();
        productAccessibilityListData.setProductAccessibility(accessibilityData);
        return getDataMapper().map(productAccessibilityListData, ProductAccessibilityListWsDTO.class, fields);
    }

    @ReadOnly
    @RequestMapping(value = "/availability", method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getAvailabilityForProductCode", summary = "Get availability of product's.", description = "Returns product's availabilities ")
    @ApiBaseSiteIdParam
    public ProductAvailabilityListWsDTO getAvailabilityForProductCode(
                                                                      @Parameter(description = "Product identifier", required = true) @RequestParam final List<String> productCodes,
                                                                      @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        // Limit call to availability, truncate list to limit.availability.to
        final int limitAvailabilityTo = Math.max(DEFAULT_SEARCH_MAX_SIZE,
                                                 getConfigurationService().getConfiguration().getInt("limit.availability.to", DEFAULT_SEARCH_MAX_SIZE));
        final List<ProductAvailabilityData> availabilityData = getProductAvailabilityData(productCodes, limitAvailabilityTo);
        final ProductAvailabilityListData productAvailabilityListData = new ProductAvailabilityListData();
        productAvailabilityListData.setProductAvailability(availabilityData);
        return getDataMapper().map(productAvailabilityListData, ProductAvailabilityListWsDTO.class, fields);
    }

    @RequestMapping(value = "/{productCode}/stock/{storeName}", method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getStoreProductStock", summary = "Get a product's stock level for a store", description = "Returns a product's stock level for a particular store (in other words, for a particular point of sale).")
    public StockWsDTO getStoreProductStock(@Parameter(description = "Base site identifier", required = true) @PathVariable final String baseSiteId,
                                           @Parameter(description = "Product identifier", required = true) @PathVariable final String productCode,
                                           @Parameter(description = "Store identifier", required = true) @PathVariable final String storeName,
                                           @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        validate(storeName, "storeName", pointOfServiceValidator);
        if (!commerceStockFacade.isStockSystemEnabled(baseSiteId)) {
            throw new StockSystemException("Stock system is not enabled on this site", StockSystemException.NOT_ENABLED, baseSiteId);
        }
        final StockData stockData = commerceStockFacade.getStockDataForProductAndPointOfService(productCode, storeName);
        return getDataMapper().map(stockData, StockWsDTO.class, fields);
    }

    @RequestMapping(value = "/{productCode}/stock", method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getLocationProductStock", summary = "Get a product's stock level.", description = "Returns a product's stock levels sorted by distance from the specified location, which is provided "
                                                                                                                + "using the free-text \"location\" parameter, or by using the longitude and latitude parameters. The following two sets of parameters are available: location "
                                                                                                                + "(required), currentPage (optional), pageSize (optional); or longitude (required), latitude (required), currentPage (optional), pageSize(optional).")
    @ApiBaseSiteIdParam
    public StoreFinderStockSearchPageWsDTO getLocationProductStock(
                                                                   @Parameter(description = "Product identifier", required = true) @PathVariable final String productCode,
                                                                   @Parameter(description = "Free-text location") @RequestParam(required = false) final String location,
                                                                   @Parameter(description = "Latitude location parameter.") @RequestParam(required = false) final Double latitude,
                                                                   @Parameter(description = "Longitude location parameter.") @RequestParam(required = false) final Double longitude,
                                                                   @Parameter(description = "The current result page requested.") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
                                                                   @Parameter(description = "The number of results returned per page.") @RequestParam(required = false, defaultValue = "0") final int pageSize,
                                                                   @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
                                                                   final HttpServletResponse response) {
        LOG.debug("getLocationProductStock: code={}  | location={} | latitude={} | longitude={}", sanitize(productCode),
                  sanitize(location), latitude,
                  longitude);

        final StoreFinderStockSearchPageData result = doSearchProductStockByLocation(productCode, location, latitude, longitude,
                                                                                     currentPage, getPageSizeOrDefault(pageSize));
        setTotalCountHeader(response, result.getPagination());
        return getDataMapper().map(result, StoreFinderStockSearchPageWsDTO.class, addPaginationField(fields));
    }

    @RequestMapping(value = "/{productCode}/stock", method = RequestMethod.HEAD)
    @Operation(operationId = "countProductStockByLocation", summary = "Get header with a total number of product's stock levels.", description = "In the response header, the \"x-total-count\" indicates the total number of a "
                                                                                                                                                 + "product's stock levels. The following two sets of parameters are available: location (required); or longitude (required), and latitude (required).")
    @ApiBaseSiteIdParam
    public void countProductStockByLocation(@Parameter(description = "Product identifier", required = true) @PathVariable final String productCode,
                                            @Parameter(description = "Free-text location") @RequestParam(required = false) final String location,
                                            @Parameter(description = "Latitude location parameter.") @RequestParam(required = false) final Double latitude,
                                            @Parameter(description = "Longitude location parameter.") @RequestParam(required = false) final Double longitude,
                                            final HttpServletResponse response) {
        final StoreFinderStockSearchPageData result = doSearchProductStockByLocation(productCode, location, latitude, longitude, 0,
                                                                                     1);
        setTotalCountHeader(response, result.getPagination());
    }

    protected StoreFinderStockSearchPageData doSearchProductStockByLocation(final String productCode, final String location,
                                                                            final Double latitude,
                                                                            final Double longitude, final int currentPage, final int pageSize) {
        final Set<ProductOption> opts = EnumSet.of(ProductOption.BASIC);
        final StoreFinderStockSearchPageData result;
        if (latitude != null && longitude != null) {
            result = storeFinderStockFacade.productSearch(createGeoPoint(latitude, longitude),
                                                          productFacade.getProductForCodeAndOptions(productCode, opts),
                                                          createPageableData(currentPage, pageSize, null));
        } else if (location != null) {
            result = storeFinderStockFacade.productSearch(location, productFacade.getProductForCodeAndOptions(productCode, opts),
                                                          createPageableData(currentPage, pageSize, null));
        } else {
            throw new RequestParameterException("You need to provide location or longitute and latitute parameters",
                                                RequestParameterException.MISSING,
                                                "location or longitute and latitute");
        }
        return result;
    }

    @ReadOnly
    @RequestMapping(value = "/{productCode}/references", method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getProductReferences", summary = "Get a product reference", description = "Returns references for a product with a given product code. Reference type specifies which references to return.")
    @ApiBaseSiteIdParam
    public ProductReferenceListWsDTO getProductReferences(@Parameter(description = "Product identifier", required = true) @PathVariable final String productCode,
                                                          @Parameter(description = "Maximum size of returned results.") @RequestParam(required = false, defaultValue = MAX_INTEGER) final int pageSize,
                                                          @Parameter(description = "Comma-separated list of reference types according to enum ProductReferenceTypeEnum. If not specified, all types of product references will be used.") @RequestParam(required = false) final String referenceType,
                                                          @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {

        List<ProductReferenceTypeEnum> productReferenceTypeList = StringUtils.isNotEmpty(referenceType) ? getProductReferenceTypeEnums(referenceType)
                                                                                                        : List.of(ProductReferenceTypeEnum.values());

        List<ProductReferenceData> productReferences = productFacade.getProductReferencesForCode(productCode,
                                                                                                 productReferenceTypeList,
                                                                                                 PRODUCT_OPTIONS_WITHOUT_CATPLUSSSUPPLIERAID,
                                                                                                 pageSize);
        ProductReferencesData productReferencesData = new ProductReferencesData();
        productReferencesData.setReferences(productReferences);
        return getDataMapper().map(productReferencesData, ProductReferenceListWsDTO.class, fields);
    }

    @ReadOnly
    @RequestMapping(value = "/{productCode}/alternatives", method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getProductAlternatives", summary = "Get product alternatives", description = "Returns alternatives for a product with a given product code.")
    @ApiBaseSiteIdParam
    public ProductReferenceListWsDTO getProductAlternatives(@Parameter(description = "Product identifier", required = true) @PathVariable final String productCode,
                                                            @Parameter(description = "Maximum size of returned results.") @RequestParam(required = false, defaultValue = MAX_INTEGER) final int pageSize,
                                                            @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        List<ProductData> productAlternatives = productFacade.getMultipleProductAlternatives(productCode, 0, pageSize);
        List<ProductReferenceData> productReferences = productAlternatives.stream()
                                                                          .map(this::convertToProductReferenceData)
                                                                          .collect(Collectors.toList());

        ProductReferencesData productReferencesData = new ProductReferencesData();
        productReferencesData.setReferences(productReferences);
        return getDataMapper().map(productReferencesData, ProductReferenceListWsDTO.class, fields);
    }

    private ProductReferenceData convertToProductReferenceData(ProductData productData) {
        ProductReferenceData productReference = new ProductReferenceData();
        try {
            ProductReferenceTypeEnum referenceType = ProductReferenceTypeEnum.valueOf(productData.getAlternateCategory().toUpperCase());
            productReference.setReferenceType(referenceType);
        } catch (IllegalArgumentException e) {
            LOG.warn("Unable to map alternative products {} alternate category {} to reference type enum", productData.getCode(),
                     productData.getAlternateCategory());
        }
        productReference.setTarget(productData);
        return productReference;
    }

    @RequestMapping(value = "/notifyBackInStock", method = RequestMethod.POST)
    @ResponseBody
    @Operation(operationId = "saveStockNotificationDataPDP", summary = "save Stock Notification Data on PDP", description = "returns a boolean value when product is not in stock and save notification details")
    @ApiBaseSiteIdParam
    public boolean saveStockNotificationDataPDP(@RequestParam(value = "customerEmail") final String customerEmail,
                                                @RequestParam(value = "articleNumbers") final List<String> articleNumbers) {
        if (StringUtils.isNotEmpty(customerEmail)
                && EmailValidator.getInstance().isValid(customerEmail)
                && CollectionUtils.isNotEmpty(articleNumbers)) {
            return outOfStockFacade.saveStockNotificationDetails(customerEmail, articleNumbers);
        }
        return false;
    }

    private List<ProductReferenceTypeEnum> getProductReferenceTypeEnums(String referenceType) {
        String[] referenceTypes = referenceType.split(COMMA_SEPARATOR);
        return Arrays.stream(referenceTypes)
                     .map(ProductReferenceTypeEnum::valueOf)
                     .collect(Collectors.toList());
    }

    private PageableData createPageableData(int currentPage, int pageSize, String sort) {
        PageableData pageable = new PageableData();
        pageable.setCurrentPage(currentPage);
        pageable.setPageSize(pageSize);
        pageable.setSort(sort);
        return pageable;
    }

    private GeoPoint createGeoPoint(Double latitude, Double longitude) {
        GeoPoint point = new GeoPoint();
        point.setLatitude(latitude);
        point.setLongitude(longitude);
        return point;
    }

    @Secured("ROLE_TRUSTED_CLIENT")
    @RequestMapping(value = "/expressupdate", method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getExpressUpdateProducts", summary = "Get products added to the express update feed.", description = "Returns products added to the express update feed. Returns only elements "
                                                                                                                                   + "updated after the provided timestamp. The queue is cleared using a defined cronjob.")
    @ApiBaseSiteIdParam
    public ProductExpressUpdateElementListWsDTO getExpressUpdateProducts(
                                                                         @Parameter(description = "Only items newer than the given parameter are retrieved from the queue. This parameter should be in ISO-8601 format.", required = true) @RequestParam final String timestamp,
                                                                         @Parameter(description = "Only products from this catalog are returned. Format: catalogId:catalogVersion") @RequestParam(required = false) final String catalog,
                                                                         @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final Date timestampDate;
        try {
            timestampDate = wsDateFormatter.toDate(timestamp);
        } catch (final IllegalArgumentException ex) {
            throw new RequestParameterException("Wrong time format. The only accepted format is ISO-8601.",
                                                RequestParameterException.INVALID, "timestamp", ex);
        }
        final ProductExpressUpdateElementDataList productExpressUpdateElementDataList = new ProductExpressUpdateElementDataList();
        final List<ProductExpressUpdateElementData> products = productExpressUpdateQueue.getItems(timestampDate);
        filterExpressUpdateQueue(products, validateAndSplitCatalog(catalog));
        productExpressUpdateElementDataList.setProductExpressUpdateElements(products);
        return getDataMapper().map(productExpressUpdateElementDataList, ProductExpressUpdateElementListWsDTO.class, fields);
    }

    @ReadOnly
    @RequestMapping(value = "/{productCode}/product-references", method = RequestMethod.GET)
    @CacheControl(directive = PUBLIC, maxAge = LOCAL_TTL, sMaxAge = CDN_TTL)
    @ResponseBody
    @Operation(operationId = "getProductReferences", summary = "Get References of a product", description = "Returns list of accessories according to a product code.")
    @ApiBaseSiteIdParam
    public ProductCarouselWsDTOList getProductReferences(@Parameter(description = "Product code", required = true) @PathVariable final String productCode,
                                                         @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        ProductCarouselWsDTOList productCarouselWsDTOList = new ProductCarouselWsDTOList();
        List<ProductReferenceTypeEnum> accessoriesTypes = singletonList(ProductReferenceTypeEnum.DIST_ACCESSORY);
        List<ProductReferenceTypeEnum> alternativesTypes = Arrays.asList(ProductReferenceTypeEnum.DIS_ALTERNATIVE_UPGRADE,
                                                                         ProductReferenceTypeEnum.DIS_ALTERNATIVE_DE,
                                                                         ProductReferenceTypeEnum.DIS_ALTERNATIVE_SIMILAR,
                                                                         ProductReferenceTypeEnum.DIS_ALTERNATIVE_BETTERVALUE);
        List<ProductReferenceTypeEnum> mandatoryTypes = singletonList(ProductReferenceTypeEnum.MANDATORY);
        List<ProductReferenceTypeEnum> crossellingTypes = singletonList(ProductReferenceTypeEnum.CROSSELLING);
        productCarouselWsDTOList.setAccessories(getReferencesForType(productCode, accessoriesTypes, fields));
        productCarouselWsDTOList.setAlternatives(getReferencesForType(productCode, alternativesTypes, fields));
        productCarouselWsDTOList.setMandatory(getReferencesForType(productCode, mandatoryTypes, fields));
        productCarouselWsDTOList.setCrosselling(getReferencesForType(productCode, crossellingTypes, fields));

        return productCarouselWsDTOList;
    }

    private ProductCarouselWsDTO getReferencesForType(final String productCode, final List<ProductReferenceTypeEnum> referenceTypes, final String fields) {
        final ProductCarouselWsDTO carouselData = new ProductCarouselWsDTO();
        final List<ProductData> references = productFacade.getProductsReferences(productCode, referenceTypes, 0, 10);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Fetched reference products [{}]", ReflectionToStringBuilder.toString(carouselData));
        }
        final List<ProductWsDTO> referencesList = getDataMapper().mapAsList(references, ProductWsDTO.class, fields);
        carouselData.setCarouselProducts(referencesList);
        return getDataMapper().map(carouselData, ProductCarouselWsDTO.class, fields);
    }

    protected void filterExpressUpdateQueue(final List<ProductExpressUpdateElementData> products, final List<String> catalogInfo) {
        if (catalogInfo.size() == 2 && StringUtils.isNotEmpty(catalogInfo.get(CATALOG_ID_POS))
                && StringUtils.isNotEmpty(catalogInfo.get(CATALOG_VERSION_POS))
                && CollectionUtils.isNotEmpty(products)) {
            products.removeIf(productExpressUpdateElementData -> !catalogInfo.get(CATALOG_ID_POS).equals(productExpressUpdateElementData.getCatalogId())
                    || !catalogInfo.get(CATALOG_VERSION_POS).equals(productExpressUpdateElementData.getCatalogVersion()));
        }
    }

    protected List<String> validateAndSplitCatalog(final String catalog) {
        final List<String> catalogInfo = new ArrayList<>();
        if (StringUtils.isNotEmpty(catalog)) {
            catalogInfo.addAll(Lists.newArrayList(Splitter.on(':').trimResults().omitEmptyStrings().split(catalog)));
            if (catalogInfo.size() == 2) {
                catalogFacade.getProductCatalogVersionForTheCurrentSite(catalogInfo.get(CATALOG_ID_POS),
                                                                        catalogInfo.get(CATALOG_VERSION_POS),
                                                                        Collections.emptySet());
            } else if (!catalogInfo.isEmpty()) {
                throw new RequestParameterException("Invalid format. You have to provide catalog as 'catalogId:catalogVersion'",
                                                    RequestParameterException.INVALID, "catalog");
            }
        }
        return catalogInfo;
    }

    private List<ProductAvailabilityData> getProductAvailabilityData(final List<String> productCodes,
                                                                     final int limitAvailabilityTo) {
        if (productCodes.size() > limitAvailabilityTo) {
            productCodes.subList(limitAvailabilityTo, productCodes.size()).clear();
        }
        final Map<String, Integer> requestedQuantities = new LinkedHashMap<>();
        String[] tab;
        for (final String productCodeQuantity : productCodes) {
            tab = productCodeQuantity.split(";");
            final String productCode = normalizeProductCode(tab[0]);
            final Integer productRequestedQuantity = tab.length == 2 && tab[1] != null ? Integer.valueOf(tab[1])
                                                                                       : Integer.valueOf(1);
            if (StringUtils.isNotEmpty(productCode)) {
                requestedQuantities.put(productCode, productRequestedQuantity);
            }
        }
        return productFacade.getAvailability(requestedQuantities, true);
    }

    @RequestMapping(value = "/enhance", method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getAdditionalDataForProducts", summary = "Get availability of product's.", description = "Returns product's availabilities ")
    @ApiBaseSiteIdParam
    public ProductEnhancementListWsDTO getEnhancedDataForProducts(
                                                                  @Parameter(description = "Product identifier", required = true) @RequestParam final List<String> productCodes,
                                                                  @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        // Limit call to enhancements, truncate list to limit.availability.to
        int limitSearchSizeTo = Math.max(DEFAULT_SEARCH_MAX_SIZE,
                                         getConfigurationService().getConfiguration().getInt("limit.availability.to", DEFAULT_SEARCH_MAX_SIZE));

        List<ProductEnhancementData> productEnhancements = productFacade.getEnhancementsForProducts(productCodes, limitSearchSizeTo);

        ProductEnhancementListData productEnhancementListData = new ProductEnhancementListData();
        productEnhancementListData.setProductEnhancements(productEnhancements);
        return getDataMapper().map(productEnhancementListData, ProductEnhancementListWsDTO.class, fields);
    }

}
