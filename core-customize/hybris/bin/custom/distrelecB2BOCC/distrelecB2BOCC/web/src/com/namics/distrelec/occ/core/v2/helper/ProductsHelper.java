package com.namics.distrelec.occ.core.v2.helper;

import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.PRODUCT_FAMILY_CODE;
import static com.namics.hybris.ffsearch.util.WebUtil.urlEncode;
import static de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper.BASIC_LEVEL;
import static de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper.DEFAULT_LEVEL;
import static de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper.FULL_LEVEL;
import static java.lang.Boolean.TRUE;
import static java.lang.Math.toIntExact;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.namics.distrelec.b2b.core.service.order.exceptions.ProductNotBuyableException;
import com.namics.distrelec.b2b.facades.wishlist.DistShoppingListFacade;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistData;
import de.hybris.platform.order.exceptions.CalculationException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.SearchExperience;
import com.namics.distrelec.b2b.core.eprocurement.service.ariba.DistAribaService;
import com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.search.pagedata.SearchPageableData;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.category.DistProductFamilyFacade;
import com.namics.distrelec.b2b.facades.compliance.DistComplianceFacade;
import com.namics.distrelec.b2b.facades.hazardous.DistHazardousFacade;
import com.namics.distrelec.b2b.facades.product.DistProductPriceQuotationFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.product.data.DistCompareFeatureData;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.facades.storesession.data.SalesOrgData;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistEntryData;
import com.namics.distrelec.occ.cms.ws.dto.ShoppingListPunchedOutProductEntryWsDTO;
import com.namics.distrelec.occ.cms.ws.dto.ShoppingListWsDTO;
import com.namics.distrelec.occ.core.util.ws.SearchQueryCodec;
import com.namics.distrelec.occ.core.v2.helper.search.RedirectRuleToSearchPageDataConverter;
import com.namics.distrelec.occ.core.v2.helper.search.SearchRedirectRule;
import com.namics.distrelec.occ.core.v2.helper.search.SearchRedirectService;
import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;
import com.namics.hybris.ffsearch.breadcrumb.SearchBreadcrumbBuilder;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;
import com.namics.hybris.ffsearch.util.WebUtil;
import com.namics.hybris.ffsearch.util.XSSFilterUtil;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ClassificationData;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commercewebservicescommons.dto.product.FeatureWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.HazardWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductCategorySearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductSearchPageWsDTO;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.store.services.BaseStoreService;

@Component
public class ProductsHelper extends AbstractHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ProductsHelper.class);

    @Resource(name = "productSearchFacade")
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Resource(name = "cwsSearchQueryCodec")
    private SearchQueryCodec<SearchQueryData> searchQueryCodec;

    @Autowired
    private List<SearchRedirectService> searchRedirectServices;

    @Autowired
    private DistComplianceFacade distComplianceFacade;

    @Resource(name = "distrelecProductFacade")
    private DistrelecProductFacade productFacade;

    @Resource(name = "storeSessionFacade")
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Autowired
    private DistOciService distOciService;

    @Autowired
    private DistAribaService distAribaService;

    @Autowired
    private DistProductPriceQuotationFacade distProductPriceQuotationFacade;

    @Autowired
    private DistHazardousFacade hazardousFacade;

    @Autowired
    private DistCategoryFacade distCategoryFacade;

    @Autowired
    private DistCategoryService distCategoryService;

    @Autowired
    private RedirectRuleToSearchPageDataConverter redirectRuleToSearchPageDataConverter;

    @Autowired
    private SearchBreadcrumbBuilder searchBreadcrumbBuilder;

    @Autowired
    private BaseStoreService baseStoreService;

    @Autowired
    private DistProductFamilyFacade distProductFamilyFacade;

    @Autowired
    private DistShoppingListFacade shoppingListFacade;

    public ProductSearchPageWsDTO searchProducts(String query, int currentPage, int pageSize, String sort, String sessionId) {
        return searchProducts(query, currentPage, pageSize, sort, "BASIC", true, null, sessionId);
    }

    public ProductSearchPageWsDTO searchProducts(String query, int currentPage, int pageSize, String sort, String fields, String searchQueryContext,
                                                 String sessionId) {
        return searchProducts(query, currentPage, pageSize, sort, fields, false, searchQueryContext, sessionId);
    }

    public ProductSearchPageWsDTO searchProducts(String query, int currentPage, int pageSize, String sort, String fields, boolean countOnly,
                                                 String searchQueryContext, String sessionId) {
        boolean queryFromSuggest = false; // check if its comes from suggest
        SearchQueryData searchQuery = searchQueryCodec.decodeQuery(XSSFilterUtil.filterForSearch(query));
        SearchRedirectRule redirectRule = getRedirectRule(searchRedirectServices, searchQuery);

        FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData = preformSearch(redirectRule,
                                                                                                     searchQuery,
                                                                                                     currentPage,
                                                                                                     pageSize,
                                                                                                     sort,
                                                                                                     queryFromSuggest,
                                                                                                     countOnly,
                                                                                                     sessionId);
        if (searchPageData == null) {
            return null;
        }
        processPunchOutProducts(searchPageData);
        populateCategoryDisplayData(searchPageData);
        populateProductFamilyCategory(searchQuery, searchPageData);
        searchPageData.setBreadcrumbs(getCategoryBreadcrumbs(searchPageData, searchQuery.getSearchType()));
        if (DistSearchType.CATEGORY.equals(searchQuery.getSearchType())) {
            return mapCategorySearchResponse(searchPageData, fields);
        }
        return getDataMapper().map(searchPageData, ProductSearchPageWsDTO.class, fields);
    }

    private void populateProductFamilyCategory(SearchQueryData searchQuery, FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData) {
        emptyIfNull(searchQuery.getFilterTerms()).stream()
                                                 .filter(term -> PRODUCT_FAMILY_CODE.equals(term.getKey()))
                                                 .findFirst()
                                                 .ifPresent(t -> setProductFamilyCategoryCode(t.getValue(), searchPageData));

    }

    private void setProductFamilyCategoryCode(String productFamilyFilterTerm, FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData) {
        searchPageData.setProductFamilyCategoryCode(distProductFamilyFacade.getProductFamilySuperCategory(productFamilyFilterTerm)
                                                                           .map(CategoryData::getCode)
                                                                           .orElse(null));
    }

    private void processPunchOutProducts(FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData) {
        List<ProductData> punchedOutProducts = applyPunchOutFilterLogic(searchPageData);
        if (isEmpty(punchedOutProducts)) {
            return;
        }
        PaginationData pagination = searchPageData.getPagination();
        if (pagination != null) {
            searchPageData.setPunchedOut(isNotEmpty(punchedOutProducts) && toIntExact(pagination.getTotalNumberOfResults()) == punchedOutProducts.size());
            if (isFalse(searchPageData.isPunchedOut())) {
                pagination.setTotalNumberOfResults(pagination.getTotalNumberOfResults() - punchedOutProducts.size());
            }
            searchPageData.setPunchedOutProducts(punchedOutProducts);
        }
    }

    private void populateCategoryDisplayData(FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData) {
        if (searchPageData.getCategories() != null && isNotEmpty(searchPageData.getCategories().getValues())) {
            searchPageData.setCategoryDisplayData(distCategoryFacade.createCategoryDisplayData(searchPageData.getCategories().getValues()));
        }
    }

    private FactFinderProductSearchPageData<SearchStateData, ProductData> preformSearch(SearchRedirectRule redirectRule,
                                                                                        SearchQueryData searchQuery,
                                                                                        int currentPage,
                                                                                        int pageSize,
                                                                                        String sort,
                                                                                        boolean queryFromSuggest,
                                                                                        boolean countOnly,
                                                                                        String sessionId) {
        if (redirectRule != null) {
            return redirectRuleToSearchPageDataConverter.convert(searchQuery, redirectRule);
        }
        SearchStateData searchState = createSearchStateData(searchQuery);
        PageableData pageableData = createPageableData(currentPage, pageSize, sort, searchQuery.isTechnicalView());
        Map<String, List<String>> otherSearchParams = getOtherSearchParams(searchQuery);
        if (countOnly) {
            return productSearchFacade.search(queryFromSuggest, searchState, pageableData, searchQuery.getSearchType(), searchQuery.getCode(), true,
                                              true, false, null, otherSearchParams, sessionId);
        }
        return productSearchFacade.search(queryFromSuggest, searchState, pageableData, searchQuery.getSearchType(), searchQuery.getCode(), true,
                                          null, otherSearchParams, sessionId);
    }

    protected PageableData createPageableData(final int currentPage, final int pageSize, final String sort, boolean useTechnicalView) {
        PageableData pageableData = super.createPageableData(currentPage, pageSize, sort);
        if (pageableData instanceof SearchPageableData) {
            ((SearchPageableData) pageableData).setTechnicalView(useTechnicalView);
        }
        return pageableData;
    }

    private SearchRedirectRule getRedirectRule(List<SearchRedirectService> searchRedirectServices, SearchQueryData searchQuery) {
        for (SearchRedirectService searchRedirectService : searchRedirectServices) {
            if (searchRedirectService.supportsSearchType(searchQuery.getSearchType())) {
                return searchRedirectService.shouldRedirect(searchQuery);
            }
        }
        return null;
    }

    private ProductSearchPageWsDTO mapCategorySearchResponse(FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData, String fields) {
        searchPageData.setProductListLevel(isSelectedCategoryLastCategoryInHierarchy(searchPageData));
        return getDataMapper().map(searchPageData, ProductCategorySearchPageWsDTO.class, fields);
    }

    private boolean isSelectedCategoryLastCategoryInHierarchy(FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData) {
        FactFinderFacetData<SearchStateData> categories = searchPageData.getCategories();
        return categories != null
                && isNotEmpty(categories.getValues())
                && isNotBlank(searchPageData.getCode())
                && searchPageData.getCode().equalsIgnoreCase(categories.getValues().get(categories.getValues().size() - 1).getName());
    }

    protected List<Breadcrumb> getCategoryBreadcrumbs(FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData, DistSearchType searchType) {
        if (searchPageData.getCategories() != null
                && searchPageData.getCategories().getValues() != null
                && TRUE.equals(searchPageData.getCategories().getHasSelectedElements())) {
            return searchBreadcrumbBuilder.getCategoryBreadcrumbs(searchPageData);
        }
        if (DistSearchType.CATEGORY.equals(searchType) && isNotBlank(searchPageData.getCode())) {
            CategoryModel category = getCategory(searchPageData.getCode());
            return searchBreadcrumbBuilder.getBreadcrumbs(category);
        }
        return emptyList();
    }

    private CategoryModel getCategory(String code) {
        try {
            return distCategoryService.getCategoryForCode(code);
        } catch (Exception e) {
            LOG.error("Category not found.", e);
        }
        return null;
    }

    protected SearchStateData createSearchStateData(SearchQueryData searchQuery) {
        SearchQueryData queryData = new SearchQueryData();
        queryData.setValue(buildSearchStateDataQuery(searchQuery));

        SearchStateData searchState = new SearchStateData();
        searchState.setQuery(queryData);
        searchState.setSearchExperience(SearchExperience.FACTFINDER); // only Fact-Finder is supported through OCC on headless
        return searchState;
    }

    private String buildSearchStateDataQuery(SearchQueryData searchQuery) {
        String freeTextSearch = searchQuery.getFreeTextSearch();
        StringBuilder query = new StringBuilder();
        query.append(isBlank(freeTextSearch) ? WebUtil.STAR : urlEncode(freeTextSearch));
        for (SearchQueryTermData termData : searchQuery.getFilterTerms()) {
            query.append(WebUtil.AMPERSAND);
            query.append(termData.getKey());
            query.append(WebUtil.EQUALS);
            query.append(termData.getValue());
        }
        return query.toString();
    }

    private Map<String, List<String>> getOtherSearchParams(SearchQueryData searchQuery) {
        return singletonMap("categorySelected", List.of(Boolean.toString(containsCategoryFilter(searchQuery))));
    }

    protected boolean containsCategoryFilter(SearchQueryData searchQuery) {
        return searchQuery.getFilterTerms().stream()
                          .map(SearchQueryTermData::getKey)
                          .anyMatch(filterKey -> filterKey.startsWith(DistrelecfactfindersearchConstants.CATEGORY_CODE_PREFIX));
    }

    public void setAdditionalAttributes(ProductData productData, ProductWsDTO productWsDTO, String fields) {
        productWsDTO.setIsROHSValidForCountry(distComplianceFacade.isROHSAllowedForProduct(productData));
        productWsDTO.setIsROHSComplaint(distComplianceFacade.isROHSCompliant(productData));
        productWsDTO.setIsROHSConform(distComplianceFacade.isROHSConform(productData));
        productWsDTO.setIsRNDProduct(distComplianceFacade.isRNDProduct(productData));
        productWsDTO.setIsProductBatteryCompliant(distComplianceFacade.isProductBatteryCompliant(productData));
        setHazards(productData, productWsDTO, fields);
    }

    public void setHazards(ProductData productData, ProductWsDTO productWsDTO, String fields) {
        fields = getCorrectScope(fields);
        productWsDTO.setHazardStatements(getHazardStatements(productData, fields));
        productWsDTO.setSupplementalHazardInfos(getSupplementalHazards(productData, fields));
    }

    /**
     * Sets the `fields` attribute to "DEFAULT" if it's not "DEFAULT", "BASIC", or "FULL".
     * The `fields` attribute is passed in the ProductsController as the scope.
     * In Angular in `PunchOutProductGuard` in `fetchProduct` we are sending the `exclusionReason` as the `fields` attribute in the controller.
     * We do this for the punchout purpose only. The `exclusionReason` is not a valid scope,
     * but it's used with the `no-cache` header to ensure that this request is not cached,
     * and the URL is not the same as when we're just getting the product data (for example for PDP).
     * Introduced in <a href="https://jira.distrelec.com/browse/HDLS-3989">HDLS-3989</a>
     * 
     * @param fields
     *            Scope passed in the controller
     * @return Correct scope
     */
    private String getCorrectScope(String fields) {
        if (!fields.equals(BASIC_LEVEL) &&
                !fields.equals(FULL_LEVEL) &&
                !fields.equals(DEFAULT_LEVEL)) {
            return DEFAULT_LEVEL;
        }

        return fields;
    }

    private List<HazardWsDTO> getHazardStatements(ProductData productData, String fields) {
        List<String> hazardStatementsCodes = productData.getHazardStatements();
        if (isEmpty(hazardStatementsCodes)) {
            return emptyList();
        }
        return hazardousFacade.getAllDistHazardStatement().stream()
                              .filter(statement -> hazardStatementsCodes.contains(statement.getCode()))
                              .map(hazardStatement -> getDataMapper().map(hazardStatement, HazardWsDTO.class, fields))
                              .collect(Collectors.toList());
    }

    private List<HazardWsDTO> getSupplementalHazards(ProductData productData, String fields) {
        List<String> supplementalHazardInfoCodes = productData.getSupplementalHazardInfos();
        if (isEmpty(supplementalHazardInfoCodes)) {
            return emptyList();
        }
        return hazardousFacade.getAllDistSupplementalHazardInfo().stream()
                              .filter(info -> supplementalHazardInfoCodes.contains(info.getCode()))
                              .map(hazardInfo -> getDataMapper().map(hazardInfo, HazardWsDTO.class, fields))
                              .collect(Collectors.toList());
    }

    public void populateAttributes(final List<ProductData> products) {
        populateAttributesForFeatureCodes(products, evaluateCommonFeatureCodes(products));
    }

    public boolean isDangerousGoods(final ProductModel productModel) {
        return isCurrentShopElfaShop() && productFacade.isDangerousProduct(productModel.getCode());
    }

    protected boolean isCurrentShopElfaShop() {
        final SalesOrgData salesOrgData = storeSessionFacade.getCurrentSalesOrg();
        return salesOrgData != null && (salesOrgData.getErpSystem().equalsIgnoreCase("SAP") && !salesOrgData.getCode().equals("7350"));
    }

    protected void populateAttributesForFeatureCodes(final List<ProductData> products, final List<DistCompareFeatureData> allFeatures) {
        final Map<String, String> possibleCommonAttributes = new HashMap<>();
        final Map<String, String> possibleOtherAttributes = new HashMap<>();

        for (final ProductData product : products) {
            final Map<String, FeatureData> commonAttributes = new HashMap<>();
            final Map<String, FeatureData> otherAttributes = new HashMap<>();

            final Map<String, FeatureData> features = getFeatures(product.getClassifications());

            for (final DistCompareFeatureData feature : allFeatures) {
                final FeatureData tempFeature = features.containsKey(feature.getCode()) ? features.get(feature.getCode()) : createEmptyFeature(feature);

                if (feature.getCounter() > 1 || products.size() == 1) {
                    commonAttributes.put(tempFeature.getCode(), tempFeature);
                    if (!possibleCommonAttributes.containsKey(tempFeature.getCode())) {
                        possibleCommonAttributes.put(tempFeature.getCode(), tempFeature.getName());
                    }
                } else if (tempFeature.getFeatureValues() != null) {
                    otherAttributes.put(tempFeature.getCode(), tempFeature);
                    if (!possibleOtherAttributes.containsKey(tempFeature.getCode())) {
                        possibleOtherAttributes.put(tempFeature.getCode(), tempFeature.getName());
                    }
                }
            }

            product.setCommonAttributes(commonAttributes);
            product.setPossibleCommonAttributes(possibleCommonAttributes);
            product.setOtherAttributes(otherAttributes);
            product.setPossibleOtherAttributes(possibleOtherAttributes);
        }
    }

    protected List<DistCompareFeatureData> evaluateCommonFeatureCodes(final List<ProductData> products) {
        final Map<String, DistCompareFeatureData> allFeatures = new HashMap<>();
        for (final ProductData product : products) {
            if (product.getEndOfLifeDate() == null && product.isBuyable()) {
                final List<DistCompareFeatureData> compareFeatures = getCompareFeatures(product.getClassifications());
                for (final DistCompareFeatureData compareFeature : compareFeatures) {
                    String code = compareFeature.getCode();
                    final int index = code.lastIndexOf(DistConstants.Punctuation.DOT);
                    if (index >= 0) {
                        code = compareFeature.getCode().substring(index + 1);
                    }

                    // Setting the optimized feature code
                    compareFeature.setCode(code);

                    if (allFeatures.containsKey(code)) {
                        allFeatures.get(code).incrementCounter();
                    } else {
                        allFeatures.put(code, compareFeature);
                    }
                }
            }
        }
        final List<DistCompareFeatureData> sortedFeatures = new ArrayList<>(allFeatures.values());
        Collections.sort(sortedFeatures);
        return sortedFeatures;
    }

    protected Map<String, FeatureData> getFeatures(final Collection<ClassificationData> classifications) {
        final Map<String, FeatureData> featureMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(classifications)) {
            for (final ClassificationData classification : classifications) {
                if (CollectionUtils.isNotEmpty(classification.getFeatures())) {
                    for (final FeatureData feature : classification.getFeatures()) {
                        String code = feature.getCode();
                        final int index = code.lastIndexOf(DistConstants.Punctuation.DOT);
                        if (index >= 0) {
                            code = feature.getCode().substring(index + 1);
                        }
                        feature.setCode(code);
                        featureMap.put(code, feature);
                    }
                }
            }
        }
        return featureMap;
    }

    protected List<DistCompareFeatureData> getCompareFeatures(final Collection<ClassificationData> classifications) {
        final List<DistCompareFeatureData> compareFeatures = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(classifications)) {
            for (final ClassificationData classification : classifications) {
                if (CollectionUtils.isNotEmpty(classification.getFeatures())) {
                    for (final FeatureData feature : classification.getFeatures()) {
                        final DistCompareFeatureData compareFeature = new DistCompareFeatureData();
                        compareFeature.setCode(feature.getCode());
                        compareFeature.setName(feature.getName());
                        compareFeature.setDescription(feature.getDescription());
                        compareFeature.setVisibility(feature.getVisibility());
                        compareFeature.setPosition(feature.getPosition());
                        compareFeatures.add(compareFeature);
                    }
                }
            }
        }
        return compareFeatures;
    }

    protected FeatureData createEmptyFeature(final DistCompareFeatureData feature) {
        final FeatureData emptyFeature = new FeatureData();
        emptyFeature.setCode(feature.getCode());
        emptyFeature.setName(feature.getName());
        emptyFeature.setDescription(feature.getDescription());
        emptyFeature.setVisibility(feature.getVisibility());
        emptyFeature.setPosition(feature.getPosition());
        emptyFeature.setFeatureValues(null);
        emptyFeature.setFeatureUnit(null);
        return emptyFeature;
    }

    public List<ProductWsDTO> getProductListForWsDTOCompare(final List<ProductData> productDataList, final String fields) {
        List<ProductWsDTO> productListWsDTO = new ArrayList<>();
        for (ProductData productData : productDataList) {
            ProductWsDTO productWsDTO = getDataMapper().map(productData, ProductWsDTO.class, fields);
            List<FeatureWsDTO> commonAttributesWsDTOList = productData.getCommonAttributes().entrySet().stream()
                                                                      .map(entry -> convertFeatureDataToWs(entry, fields)).collect(Collectors.toList());
            productWsDTO.setCommonAttrs(commonAttributesWsDTOList);
            productListWsDTO.add(productWsDTO);
        }
        return productListWsDTO;
    }

    private FeatureWsDTO convertFeatureDataToWs(Map.Entry<String, FeatureData> entry, String fields) {
        return getDataMapper().map(entry.getValue(), FeatureWsDTO.class, fields);
    }

    public List<NamicsWishlistEntryData> processPunchOutProductsInWishlist(NamicsWishlistData wishlist) {
        final String listId = wishlist.getUniqueId();
        List<NamicsWishlistEntryData> removedProducts = wishlist.getEntries()
                                                                    .stream()
                                                                    .filter(entry -> entry.getIsPunchedOut()
                                                                                     || !entry.getIsBuyable())
                                                                    .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(removedProducts)) {
            removedProducts.forEach(entry -> shoppingListFacade.removeFromWishlist(listId, entry.getProduct().getCode()));
            wishlist.getEntries().removeAll(removedProducts);
            wishlist.setCalculated(Boolean.FALSE);
            wishlist.setTotalUnitCount(wishlist.getEntries().size());
        }

        if (!wishlist.isCalculated()) {
            try {
                wishlist = shoppingListFacade.calculateShoppingList(listId);
            } catch (final ProductNotBuyableException e) {
                LOG.error("Exception occurred in checking shopping list with ID {} \n{}", listId, e.getMessage(), e);
            } catch (final CalculationException e) {
                LOG.error("Exception occurred in calculating shopping list with ID {} \n{}", listId, e.getMessage(), e);
            }
        }
        return removedProducts;
    }

    public void populatePunchOutProductCodes(List<NamicsWishlistEntryData> punchedOutProducts, ShoppingListWsDTO shoppingListWsDTO) {
        shoppingListWsDTO.setPunchOutProducts(punchedOutProducts.stream()
                                                                .map(punchedOutProduct -> {
                                                                    ShoppingListPunchedOutProductEntryWsDTO entry = new ShoppingListPunchedOutProductEntryWsDTO();
                                                                    entry.setProductCode(punchedOutProduct.getProduct().getCode());
                                                                    entry.setIsPunchedOut(punchedOutProduct.getIsPunchedOut());
                                                                    entry.setIsBuyable(punchedOutProduct.getIsBuyable());
                                                                    entry.setSalesStatus(punchedOutProduct.getSalesStatus());

                                                                    return entry;
                                                                })
                                                                .collect(Collectors.toList()));
    }

    private List<ProductData> applyPunchOutFilterLogic(FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData) {
        if (skipPunchOutLogic(searchPageData)) {
            return emptyList();
        }
        List<ProductData> punchOutProducts = searchPageData.getResults().stream()
                                                           .filter(p -> productFacade.isProductExcluded(p.getCode()) && isFalse(p.getAvailableToB2B()))
                                                           .collect(Collectors.toList());

        searchPageData.getResults().removeAll(punchOutProducts);
        return punchOutProducts;
    }

    private boolean skipPunchOutLogic(FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData) {
        SiteChannel channel = baseStoreService.getCurrentBaseStore().getChannel();
        return isFalse(SiteChannel.B2B.equals(channel))
                || isEmpty(searchPageData.getResults())
                || isFalse(productFacade.enablePunchoutFilterLogic())
                || searchPageData.getCurrentQuery().getUrl().contains("filter_productFamilyCode");
    }
}
