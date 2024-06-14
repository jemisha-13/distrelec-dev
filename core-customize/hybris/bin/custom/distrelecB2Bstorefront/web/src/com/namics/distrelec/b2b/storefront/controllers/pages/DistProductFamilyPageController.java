package com.namics.distrelec.b2b.storefront.controllers.pages;

import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.PORTRAIT_MEDIUM;
import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.PORTRAIT_MEDIUM_WEBP;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.FILTER;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;

import java.io.UnsupportedEncodingException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.cms.ProductFamilyPageModel;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.customer.DistPunchoutService;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.category.DistProductFamilyFacade;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.Namb2bacceleratorFacadesConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerData;
import com.namics.distrelec.b2b.facades.productfinder.DistProductFinderFacade;
import com.namics.distrelec.b2b.facades.productfinder.data.DistProductFinderData;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.ThirdPartyConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.CategoryDataHelper;
import com.namics.distrelec.b2b.storefront.controllers.util.ShopSettingsUtil;
import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;
import com.namics.hybris.ffsearch.breadcrumb.SearchBreadcrumbBuilder;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.util.WebUtil;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.product.converters.populator.CategoryPopulator;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;

@Controller
@RequestMapping("/**/" + DistConstants.UrlTags.PRODUCT_FAMILY)
public class DistProductFamilyPageController extends AbstractSearchPageController {

    private static final Logger LOG = LoggerFactory.getLogger(DistProductFamilyPageController.class);

    public static final String SEARCH_QUERY_PARAMETER_NAME = DistrelecfactfindersearchConstants.SEARCH_QUERY_PARAMETER_NAME;

    public static final String TRACK = "?track=true";

    private static final String CATEGORY_ONLY = "showCategoriesOnly";

    private static final Gson GSON = new Gson();

    private static final String UID_CATEGORY_PAGE = "category";

    private static final String UID_PRODUCT_LIST_PAGE = "productList";

    private static final String UID_PRODUCT_FINDER_PAGE_TEMPLATE = "ProductFinderPageTemplate";

    private static final String UID_PRODUCT_FINDER_RESULT_PAGE = "productFinderResult";

    private static final String EOL_MESSAGE = "eolMessage";

    private static final String MANUFACTURER_URL = "manufacturerUrl";

    private static final String PRODUCT_FAMILY_FILTER_PARAM = "&productFamilyCode=";

    private static final String CATEGORY_CODE_PATH_VARIABLE_PATTERN_COUNT = "/{categoryCode:.*}/cnt";

    public static final String DEFAULT_PAGE_SIZE = "0";

    public static final String DEFAULT_PAGE_NUMBER = "1";

    @Autowired
    private DistProductFamilyFacade distProductFamilyFacade;

    @Autowired
    private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    @Autowired
    @Qualifier("categoryModelUrlResolver")
    private DistUrlResolver<CategoryModel> categoryModelUrlResolver;

    @Autowired
    @Qualifier("distProductFamilyUrlResolver")
    private DistUrlResolver<CategoryModel> productFamilyUrlResolver;

    @Autowired
    private SimpleBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Autowired
    private DistPunchoutService distPunchoutService;

    @Autowired
    private DistCategoryService categoryService;

    @Autowired
    private DistCategoryFacade distCategoryFacade;

    @Autowired
    private SearchBreadcrumbBuilder searchBreadcrumbBuilder;

    @Autowired
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Autowired
    private CategoryPopulator categoryPopulator;

    @Autowired
    private ProductService productService;

    @Autowired
    private DistProductFinderFacade distProductFinderFacade;

    @RequestMapping("/{code:.*}")
    public String getProductFamilyPage(@PathVariable String code, @RequestParam(value = SEARCH_QUERY_PARAMETER_NAME, required = false) final String searchQuery,
                                       @RequestParam(value = "page", defaultValue = "1") final int page,
                                       @RequestParam(value = "pageSize", defaultValue = "0") final int pageSize,
                                       @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
                                       @RequestParam(value = "sort", required = false) final String sortCode,
                                       @RequestParam(value = DistProductFinderFacade.REFINE_PAGE_PARAM, required = false) final boolean productFinderRefine,
                                       @RequestParam(value = DistProductFinderFacade.RESULT_PAGE_PARAM, required = false) final boolean productFinderResult,
                                       @RequestParam(value = "log", required = false) final String log,
                                       @RequestParam(value = "requestType", required = false) final String requestType,
                                       @RequestParam(value = "useTechnicalView", required = false, defaultValue = "false") final Boolean useTechnicalView,
                                       @RequestParam(value = "useIconView", required = false, defaultValue = "false") final Boolean useIconView,
                                       @RequestParam(value = "useListView", required = false, defaultValue = "false") final Boolean useListView,
                                       @RequestParam(value = "eolCategoryFor", required = false) final String eolCategoryFor, final Model model,
                                       final HttpServletRequest request, final HttpServletResponse response,
                                       @RequestParam(value = "useDetailView", required = false) final Boolean useDetailView) throws UnsupportedEncodingException,
                                                                                                                             CMSItemNotFoundException {
        Optional<ProductFamilyPageModel> productFamilyPage = distProductFamilyFacade.findPageForProductFamily(code);
        Optional<CategoryData> productFamilyCategory = distProductFamilyFacade.findProductFamily(code);
        if (!productFamilyPage.isPresent() || !productFamilyCategory.isPresent()) {
            LOG.info("Product Family Page redirecting to 404. Page found: {}, family found: {}, code: {}", productFamilyPage.isPresent(),
                     productFamilyCategory.isPresent(), code);
            return FORWARD_TO_404;
        }
        setFFfollowSearchParameter(Boolean.valueOf(REQUEST_TYPE_AJAX.equals(requestType)));
        setNonFilerSearchParameters(request, model);

        // This code snippet is to reduce errors like
        // "de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException: Category with code 'cat-L3D_543267&page=2'"
        String categoryCode = productFamilyCategory.get().getFamilyCategoryCode();
        String catCode = categoryCode;
        int pageNr = page;
        if (categoryCode.contains("&page=")) {
            catCode = categoryCode.substring(0, categoryCode.indexOf('&'));
            if (page <= 1) {
                try {
                    pageNr = Integer.parseInt(categoryCode.substring(categoryCode.indexOf('=') + 1));
                } catch (final NumberFormatException nfe) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(nfe.getMessage());
                    }
                }
            }
        }

        final CategoryModel category = getCategoryService().getCategoryForCode(catCode);
        if (category == null) {
            // throw new UnknownIdentifierException("Category with code " + catCode + " not found!");
            return FORWARD_TO_404;
        }

        final CategoryModel productFamily = getCategoryService().getCategoryForCode(code);
        String productFamilyUrl = getProductFamilyUrlResolver().resolve(productFamily);
        String redirection = checkRequestUrl(request, response, productFamilyUrl);
        if (StringUtils.isNotEmpty(redirection)) {
            return redirection;
        }

        getRequestContextData(request).setCategory(category);

        final StringBuilder selectedCatWtAreaCode = new StringBuilder();
        // Webtrekk category area code
        if (StringUtils.isNotBlank(category.getWtAreaCode())) {
            selectedCatWtAreaCode.append(category.getWtAreaCode());
        } else {
            // Try to get the are code of the super categories
            final Collection<CategoryModel> allSuperCategories = getCategoryService().getAllSupercategoriesForCategory(category);
            if (CollectionUtils.isNotEmpty(allSuperCategories)) {
                allSuperCategories.stream() //
                                  .filter(supCategory -> StringUtils.isNotBlank(supCategory.getWtAreaCode())) //
                                  .findFirst() //
                                  .ifPresent(supCategory -> selectedCatWtAreaCode.append(supCategory.getWtAreaCode()));
            }
        }
        model.addAttribute(ThirdPartyConstants.Webtrekk.WT_CAT_AREA_CODE, selectedCatWtAreaCode.toString());

        int totalNumberofPages = 0;
        final boolean flag = selectViewForPage(category, useTechnicalView, useIconView, useListView, useDetailView, request, response, model);
        final CategoryPageModel categoryPage = getCategoryPage(category, productFinderResult);

        final Map<String, List<String>> otherSearchParams = new HashMap<>();
        final SearchStateData searchState = createSearchStateData(request, searchQuery);
        if (searchState.getQuery() != null && StringUtils.isNotBlank(searchState.getQuery().getValue())) {
            String existingValue = searchState.getQuery().getValue();
            searchState.getQuery().setValue(existingValue.concat(PRODUCT_FAMILY_FILTER_PARAM + code));
        }
        // Remembering the Pagination preference selected by customer
        final int pageSizeValue = calculatePageSize(pageSize, request, response);
        final PageableData paging = createSearchPageableDataWithCookie(request, response, pageNr, pageSizeValue, sortCode, showMode,
                                                                       getStoreSessionFacade().isUseTechnicalView().booleanValue());

        // Do not use filters previously set on parent category
        if (isProductFinderPage(categoryPage) && !productFinderRefine) {
            final SearchQueryData queryData = new SearchQueryData();
            queryData.setValue("*");
            searchState.setQuery(queryData);
        }

        // check if its comes from suggest
        final boolean queryFromSuggest = Boolean.valueOf(request.getParameter(DistConstants.FactFinder.FF_TRACKING)).booleanValue();

        final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData = getProductSearchFacade().search(queryFromSuggest, searchState,
                                                                                                                             paging, getSearchType(searchQuery),
                                                                                                                             catCode, true, log,
                                                                                                                             otherSearchParams);
        if (searchPageData != null && searchPageData.getResults() != null) {
            for (ProductData product : searchPageData.getResults()) {
                product.setUrl(product.getUrl() + TRACK);
                try {
                    product.setSalesStatus(getProductFacade().getProductSalesStatus(product.getCode()));
                } catch (Exception ex) {
                    LOG.warn("Couldnt get Sales status for::" + product.getCode());
                }
            }
        }
        populateTabularAttributeSortingMap(model, searchPageData.getSortableAttributeMap());

        String redirectIfDoesntContainsProducts = checkIfContainsProducts(searchPageData, request, productFamilyUrl, category);
        if (StringUtils.isNotEmpty(redirectIfDoesntContainsProducts)) {
            return redirectIfDoesntContainsProducts;
        }

        // DISTRELEC-9134: SRP
        createCategoryDisplayData(model, searchPageData, distCategoryFacade);
        if (CollectionUtils.isNotEmpty(searchPageData.getResults())) {
            DistManufacturerData manufacturer = searchPageData.getResults().get(0).getDistManufacturer();
            model.addAttribute(MANUFACTURER_URL, manufacturer.getUrl());
        }
        createCategoryBreadcrumb(model, searchPageData, category, getSearchBreadcrumbBuilder());
        totalNumberofPages = searchPageData.getPagination().getNumberOfPages();
        // DISTRELEC-5176: add filterstrings
        getProductSearchFacade().addFilterstrings(searchPageData);

        // DISTRELEC-4596
        final long productCount = searchPageData.getPagination().getTotalNumberOfResults();
        final String checkDupInBetnCate = checkDupInBetnCate(category, productCount, request, response);
        if (checkDupInBetnCate != null) {
            return checkDupInBetnCate;
        }

        // DISTRELEC-6548 SEO optimization, if category has only one product, jump there directly. But should not be a facet filter
        // result
        if (!CollectionUtils.isEmpty(searchPageData.getResults()) && searchPageData.getResults().size() == 1 && !isFacetFilterRequest(request)) {
            final ProductData productData = searchPageData.getResults().get(0);
            final String productUrl = productData.getUrl();

            // don't use getRedirection() because this will reserve the existing query string but this is not needed for product
            // redirects
            if (StringUtils.isNotEmpty(productUrl)) {
                return checkRequestUrl(request, response, productUrl, true).replace("eolCategoryFor", "eolReplacementForParam");
            }
        }

        // Setting the remove filter URL
        setRemoveFilersURL(searchPageData, request);
        populateProductFinderData(model, category, categoryPage, searchPageData, productFinderRefine);

        populateEOLInformation(model, eolCategoryFor);
        storeSearchResultToModel(model, searchPageData, showMode);

        model.addAttribute(CATEGORY_ONLY, Boolean.valueOf(searchQuery == null && Boolean.FALSE.equals(categoryPage.getDefaultPage())));
        model.addAttribute("searchPageData", searchPageData);
        int lastProductNumber = searchPageData.getPagination().getCurrentPage() * searchPageData.getPagination().getPageSize();
        model.addAttribute("lastProductNumber",
                           isLastPage(searchPageData, page) ? (int) searchPageData.getPagination().getTotalNumberOfResults() : lastProductNumber);
        model.addAttribute("firstProductNumber", lastProductNumber - searchPageData.getPagination().getPageSize() + 1);
        // Populate Data Layer
        populateDataLayerForProducts(searchQuery, searchPageData, model, false);
        if (flag) {
            if (REQUEST_TYPE_AJAX.equals(requestType)) {
                // return search result as JSON
                if (searchPageData.getPagination() != null) {
                    model.addAttribute("isLastPage", Boolean.valueOf(pageNr >= totalNumberofPages));
                }
                storeCategoryPageInModel(model, categoryPage, catCode);
                storeContinueUrl(request);
                return ControllerConstants.Views.Fragments.Product.ProductsForFacetSearch;
            }
            // Setting the URLs of the previous and next pages
            final String searchURL = storeContinueUrl(request);
            updatePagination(searchURL, (FactFinderPaginationData) searchPageData.getPagination(), model, false);
        } else {
            model.addAttribute(CATEGORY_ONLY, Boolean.TRUE);
            createCategoryBreadcrumb(model, null, category, getSearchBreadcrumbBuilder());
        }

        storeCategoryPageInModel(model, categoryPage, catCode);
        storeContinueUrl(request);

        // DISTRELEC-2092: set wtPageAreaCode equal to wtCatAreaCode
        model.addAttribute(ThirdPartyConstants.Webtrekk.WT_PAGE_AREA_CODE, selectedCatWtAreaCode.toString());

        // model.addAttribute(WebConstants.BREADCRUMBS_KEY, searchBreadcrumbBuilder.getBreadcrumbs(category));

        model.addAttribute("categoryPageData", distCategoryFacade.getCategoryPageData(category));
        model.addAttribute("seoText", category.getRelevantSeoText());

        CategoryDataHelper.setCurrentCategory(request, catCode);

        if (searchQuery != null && !isProductFinderPage(categoryPage)) {
            model.addAttribute("metaRobots", "noindex, follow");
        } else {
            model.addAttribute("metaRobots", "index, follow");
        }
        // Populate the OG informations

        final CategoryData categoryData = new CategoryData();
        getCategoryPopulator().populate(category, categoryData);
        populateOpenGraphInformation(category, categoryData, model);
        model.addAttribute("categoryName", categoryData.getName());

        model.addAttribute("detailView", getStoreSessionFacade().isUseDetailView());
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.PRODUCTFAMILYPAGE);
        populateDataLayerForProducts(searchQuery, searchPageData, model, false);

        getDistDigitalDatalayerFacade().populateFFTrackingAttribute(digitalDatalayer, Boolean.FALSE);
        model.addAttribute("productFamily", productFamilyCategory.get());
        addGlobalModelAttributes(model, request);
        model.addAttribute("pageTitle", productFamilyCategory.get().getName());
        storeCmsPageInModel(model, productFamilyPage.get());
        populateMetaData(model, productFamilyCategory.get());
        List<Breadcrumb> breadCrumbs = (List<Breadcrumb>) model.asMap().get(WebConstants.BREADCRUMBS_KEY);
        breadCrumbs.addAll(getSimpleBreadcrumbBuilder().getBreadcrumbs(productFamilyCategory.get().getName(), productFamilyCategory.get().getNameEN()));
        return getViewForPage(model);
    }

    @RequestMapping(value = CATEGORY_CODE_PATH_VARIABLE_PATTERN_COUNT, method = { RequestMethod.GET,
                                                                                  RequestMethod.POST }, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getCategoryCount(@PathVariable("categoryCode") String categoryCode,
                                   @RequestParam(value = SEARCH_QUERY_PARAMETER_NAME, required = false) final String searchQuery,
                                   @RequestParam(value = PAGE_PARAMETER_NAME, defaultValue = DEFAULT_PAGE_NUMBER) final int page,
                                   @RequestParam(value = WebConstants.PAGE_SIZE, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
                                   @RequestParam(value = "show", defaultValue = PAGE_PARAMETER_NAME) final ShowMode showMode,
                                   @RequestParam(value = DistrelecfactfindersearchConstants.SORT_PARAMETER_NAME, required = false) final String sortCode,
                                   @RequestParam(value = DistProductFinderFacade.REFINE_PAGE_PARAM, required = false) final boolean productFinderRefine,
                                   @RequestParam(value = DistProductFinderFacade.RESULT_PAGE_PARAM, required = false) final boolean productFinderResult,
                                   @RequestParam(value = "log", required = false) final String log, final HttpServletRequest request,
                                   final HttpServletResponse response)
                                                                       throws UnsupportedEncodingException, CMSItemNotFoundException {
        Optional<CategoryData> productFamilyCategory = distProductFamilyFacade.findProductFamily(categoryCode);
        categoryCode = productFamilyCategory.get().getFamilyCategoryCode();
        String catCode = categoryCode;
        int pageNr = page;
        if (categoryCode.contains("&page=")) {
            catCode = categoryCode.substring(0, categoryCode.indexOf('&'));
            if (page <= 1) {
                try {
                    pageNr = Integer.parseInt(categoryCode.substring(categoryCode.indexOf('=') + 1));
                } catch (final NumberFormatException nfe) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(nfe.getMessage());
                    }
                }
            }
        }
        final CategoryModel category = getCategoryService().getCategoryForCode(catCode);

        final CategoryPageModel categoryPage = getCategoryPage(category, productFinderResult);
        final Map<String, List<String>> otherSearchParams = new HashMap<>();
        final SearchStateData searchState = createSearchStateData(request, searchQuery);

        // Remembering the Pagination preference selected by customer
        final int pageSizeValue = calculatePageSize(pageSize, request, response);
        final PageableData paging = createSearchPageableDataWithCookie(request, response, pageNr, pageSizeValue, sortCode, showMode,
                                                                       getStoreSessionFacade().isUseTechnicalView().booleanValue());

        // Do not use filters previously set on parent category
        if (isProductFinderPage(categoryPage) && !productFinderRefine) {
            final SearchQueryData queryData = new SearchQueryData();
            queryData.setValue("*");
            searchState.setQuery(queryData);
        }

        // check if its comes from suggest
        final boolean queryFromSuggest = Boolean.valueOf(request.getParameter(DistConstants.FactFinder.FF_TRACKING)).booleanValue();

        final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData = getProductSearchFacade().search(queryFromSuggest, searchState,
                                                                                                                             paging, getSearchType(searchQuery),
                                                                                                                             catCode, true, log,
                                                                                                                             otherSearchParams);
        return String.valueOf(searchPageData.getPagination().getTotalNumberOfResults());
    }

    protected void populateOpenGraphInformation(final CategoryModel categoryModel, final CategoryData categoryData, final Model model) {
        final CMSSiteModel cmsSiteModel = getCmsSiteService().getCurrentSite();
        model.addAttribute("ogSiteName", cmsSiteModel.getName(getI18nService().getCurrentLocale()));
        model.addAttribute("ogProductTitle", categoryData.getSeoMetaTitle());
        model.addAttribute("ogProductImage", getPrimaryImageUrl(categoryData, cmsSiteModel));
        model.addAttribute("ogProductDescription", categoryData.getSeoMetaDescription());
        model.addAttribute("sharePageUrl", getUrlForCMSSiteModel(categoryModel, cmsSiteModel));
    }

    protected String getUrlForCMSSiteModel(final CategoryModel categoryModel, final CMSSiteModel cmsSiteModel) {
        final String domain = getConfigurationService().getConfiguration().getString("website." + cmsSiteModel.getUid() + ".http");
        return domain + categoryModelUrlResolver.resolve(categoryModel);
    }

    protected void storeCategoryPageInModel(final Model model, final CategoryPageModel categoryPage, final String categoryCode) {
        storeCmsPageInModel(model, categoryPage, DistConstants.Webtrekk.TEASER_TRACKING_ONS);
        // add categoryCode to webtrekk page id
        model.addAttribute(ThirdPartyConstants.Webtrekk.WT_PAGE_ID, categoryPage.getUid() + "-" + categoryCode);
    }

    private void populateEOLInformation(final Model model, final String eolCategoryFor) {
        if (StringUtils.isNotBlank(eolCategoryFor)) {
            // this means that an EOL product without replacement and without stock has been redirected here
            final ProductModel originalEOLProduct = productService.getProductForCode(eolCategoryFor);
            model.addAttribute(EOL_MESSAGE, getMessageSource().getMessage("product.eol.attention.categoryFor",
                                                                          new Object[] { originalEOLProduct.getName(),
                                                                                         originalEOLProduct.getCodeErpRelevant() },
                                                                          getI18nService().getCurrentLocale()));
        }

    }

    protected String checkIfContainsProducts(final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData,
                                             final HttpServletRequest request, final String productFamilyUrl, final CategoryModel category) {
        if (searchPageData.getPagination().getTotalNumberOfResults() == 0L) {
            // check if there is active filter and if unfiltered product family page has products
            boolean hasFilters = !WebUtil.getParamsStartingWith(request, FILTER).isEmpty();
            if (hasFilters) {
                return REDIRECT_PREFIX + productFamilyUrl;
            } else {
                // redirect to super category
                String categoryUrl = getCategoryModelUrlResolver().resolve(category);
                return REDIRECT_PREFIX + categoryUrl;
            }
        }
        return null;
    }

    // DISTRELEC-4596
    protected String checkDupInBetnCate(final CategoryModel category, final long productCount, final HttpServletRequest request,
                                        final HttpServletResponse response) {
        return null;
    }

    protected void populateProductFinderData(final Model model, final CategoryModel category, final CategoryPageModel categoryPage,
                                             final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData,
                                             final boolean refineProductFinder) {

        if (isProductFinderPage(categoryPage)) {
            final DistProductFinderData productFinderData = getDistProductFinderFacade().getProductFinderData(category, searchPageData, refineProductFinder);

            if (productFinderData != null) {
                model.addAttribute("productFinderData", productFinderData);
                model.addAttribute("productFinderDataJson", GSON.toJson(productFinderData));
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Page is configured as ProductFinder-Page but no productFinderData was found for category with pk [" + category.getPk()
                              + "] and code [" + category.getCode() + "]");
                }
            }
        } else if (isProductFinderResultPage(categoryPage)) {
            final String url = getDistProductFinderFacade().getProductFinderRefineSearchUrl(searchPageData);
            model.addAttribute("productFinderRefineSearchUrl", url);
        }
    }

    private boolean selectViewForPage(final CategoryModel category, Boolean useTechnicalView, Boolean useIconView, Boolean useListView,
                                      Boolean useDetailView, final HttpServletRequest request, final HttpServletResponse response, final Model model) {

        if (useIconView.booleanValue() || useTechnicalView.booleanValue() || useListView.booleanValue()) {
            // Set use technical view
            getStoreSessionFacade().getPageViewPerCategoryMap().put(category.getCode(),
                                                                    useIconView.booleanValue() ? Namb2bacceleratorFacadesConstants.ICON_VIEW_SESSION_ATTR_KEY
                                                                                               : (useTechnicalView.booleanValue() ? Namb2bacceleratorFacadesConstants.TECHNICAL_VIEW_SESSION_ATTR_KEY
                                                                                                                                  : Namb2bacceleratorFacadesConstants.LIST_VIEW_SESSION_ATTR_KEY));
        }

        if (getStoreSessionFacade().getPageViewPerCategoryMap().containsKey(category.getCode())) {
            final String pageView = getStoreSessionFacade().getPageViewPerCategoryMap().get(category.getCode());
            if (pageView.equals(Namb2bacceleratorFacadesConstants.ICON_VIEW_SESSION_ATTR_KEY)) {
                useIconView = Boolean.valueOf(true);
                useTechnicalView = Boolean.valueOf(false);
                useListView = Boolean.valueOf(false);
            } else if (pageView.equals(Namb2bacceleratorFacadesConstants.TECHNICAL_VIEW_SESSION_ATTR_KEY)) {
                useTechnicalView = Boolean.valueOf(true);
                useIconView = Boolean.valueOf(false);
                useListView = Boolean.valueOf(false);
            } else {
                useListView = Boolean.valueOf(true);
                useIconView = Boolean.valueOf(false);
                useTechnicalView = Boolean.valueOf(false);
            }
        } else { // for a while List view is default
            useIconView = Boolean.valueOf(true);
            useTechnicalView = Boolean.valueOf(false);
            useListView = Boolean.valueOf(false);
            getStoreSessionFacade().getPageViewPerCategoryMap().put(category.getCode(), Namb2bacceleratorFacadesConstants.ICON_VIEW_SESSION_ATTR_KEY);
        }

        // Force Product list level view
        if (forceProductListLevel(category)) {
            final boolean useTechnicalViewValue = useTechnicalView.booleanValue() || !useListView.booleanValue();
            useTechnicalView = Boolean.valueOf(useTechnicalViewValue);
            useListView = Boolean.valueOf(!useTechnicalViewValue);
            useIconView = Boolean.FALSE;
            getStoreSessionFacade().getPageViewPerCategoryMap().put(category.getCode(),
                                                                    useTechnicalView.booleanValue() ? Namb2bacceleratorFacadesConstants.TECHNICAL_VIEW_SESSION_ATTR_KEY
                                                                                                    : Namb2bacceleratorFacadesConstants.LIST_VIEW_SESSION_ATTR_KEY);
            model.addAttribute("hideIconViewOption", Boolean.TRUE);
        } else {
            model.addAttribute("hideIconViewOption", Boolean.FALSE);
        }

        // Product list level default value is false
        boolean productListLevel = false;
        // If all views are set to false
        final boolean none = !useTechnicalView.booleanValue() && !useIconView.booleanValue() && !useListView.booleanValue();
        // Evaluate technical view value
        final Boolean useTechnicalViewValue = Boolean.valueOf(
                                                              none ? (productListLevel = isPimCategoryTypeProductListLevel(category))
                                                                   : (!useIconView.booleanValue() && useTechnicalView.booleanValue()));
        // Evaluate icon view value
        final Boolean useIconViewValue = Boolean.valueOf(none ? !productListLevel : useIconView.booleanValue());
        // Evaluate list view value
        final Boolean useListViewValue = Boolean
                                                .valueOf(none ? false
                                                              : (!useIconView.booleanValue() && !useTechnicalView.booleanValue()
                                                                      && useListView.booleanValue()));

        getStoreSessionFacade().setUseTechnicalView(useTechnicalViewValue);
        getStoreSessionFacade().setUseIconView(useIconViewValue);
        getStoreSessionFacade().setUseListView(useListViewValue);
        if (useDetailView != null) {
            getStoreSessionFacade().setUseDetailView(useDetailView);
        }
        // Set Cookies value
        ShopSettingsUtil.updateViews(useTechnicalViewValue, useIconViewValue, useListViewValue, useDetailView, request, response);
        return useTechnicalViewValue.booleanValue() ^ useListViewValue.booleanValue();
    }

    protected boolean isPimCategoryTypeProductListLevel(final CategoryModel category) {
        return category != null && category.getPimCategoryType() != null && !category.getPimCategoryType().getCategoryPage().booleanValue();
    }

    protected boolean forceProductListLevel(final CategoryModel category) {
        return isPimCategoryTypeProductListLevel(category) && BooleanUtils.isTrue(category.getPimCategoryType().getForceProductList());
    }

    protected boolean isProductListLevel(final CategoryModel category) {
        if (getStoreSessionFacade().isUseTechnicalView() != null && getStoreSessionFacade().isUseListView() != null
                && getStoreSessionFacade().isUseIconView() != null) {
            if (getStoreSessionFacade().isUseTechnicalView().booleanValue() || getStoreSessionFacade().isUseListView().booleanValue()) {
                return true;
            } else if (getStoreSessionFacade().isUseIconView().booleanValue()) {
                return false;
            }
        }
        return category.getPimCategoryType() != null && !category.getPimCategoryType().getCategoryPage().booleanValue();
    }

    protected DistSearchType getSearchType(final String searchQuery) {
        if (StringUtils.isBlank(searchQuery) || "*".equals(searchQuery)) {
            return DistSearchType.CATEGORY;
        }
        return DistSearchType.CATEGORY_AND_TEXT;
    }

    protected CategoryPageModel getCategoryPage(final CategoryModel category, final boolean productFinderResult) {
        CategoryPageModel categoryPage = null;
        try {
            categoryPage = getCmsPageService().getPageForCategory(category);
            if (isProductFinderPage(categoryPage) && productFinderResult) {
                categoryPage = (CategoryPageModel) getCmsPageService().getPageForId(UID_PRODUCT_FINDER_RESULT_PAGE);
            } else if (isProductListLevel(category) && categoryPage != null && UID_CATEGORY_PAGE.equals(categoryPage.getUid())) {
                categoryPage = (CategoryPageModel) getCmsPageService().getPageForId(UID_PRODUCT_LIST_PAGE);
            }
        } catch (final CMSItemNotFoundException ignore) {
            // Ignore
        }
        if (categoryPage == null) {
            categoryPage = getDefaultCategoryPage();
        }
        return categoryPage;
    }

    protected CategoryPageModel getDefaultCategoryPage() {
        try {
            return getCmsPageService().getPageForCategory(null);
        } catch (final CMSItemNotFoundException ignore) {
            // Ignore
        }
        return null;
    }

    protected boolean isProductFinderPage(final CategoryPageModel categoryPage) {
        return UID_PRODUCT_FINDER_PAGE_TEMPLATE.equals(categoryPage.getMasterTemplate().getUid());
    }

    protected boolean isProductFinderResultPage(final CategoryPageModel categoryPage) {
        return UID_PRODUCT_FINDER_RESULT_PAGE.equals(categoryPage.getUid());
    }

    private void populateMetaData(Model model, CategoryData categoryData) {
        final CMSSiteModel cmsSiteModel = getCmsSiteService().getCurrentSite();
        model.addAttribute("ogSiteName", cmsSiteModel.getName());
        model.addAttribute("ogProductTitle", categoryData.getSeoMetaTitle());
        model.addAttribute("ogProductImage", getPrimaryImageUrl(categoryData, cmsSiteModel));
        model.addAttribute("ogProductDescription", categoryData.getSeoMetaDescription());
        model.addAttribute("sharePageUrl", getUrlForCMSSiteModel(categoryData, cmsSiteModel));
    }

    private String getUrlForCMSSiteModel(CategoryData categoryData, CMSSiteModel cmsSiteModel) {
        return distSiteBaseUrlResolutionService.getWebsiteUrlForSite(cmsSiteModel, true, String.format("/product-family/%s", categoryData.getCode()));
    }

    private String getPrimaryImageUrl(final CategoryData categoryData, final CMSSiteModel cmsSiteModel) {
        if (isNotEmpty(categoryData.getFamilyImage())) {
            final Map<String, ImageData> image = categoryData.getFamilyImage();
            if (image.containsKey(PORTRAIT_MEDIUM_WEBP) || image.containsKey(PORTRAIT_MEDIUM)) {
                return distSiteBaseUrlResolutionService.getWebsiteUrlForSite(cmsSiteModel, true,
                                                                             image.getOrDefault(PORTRAIT_MEDIUM_WEBP, image.get(PORTRAIT_MEDIUM)).getUrl());
            }
        }

        return null;
    }

    public DistCategoryService getCategoryService() {
        return categoryService;
    }

    public void setCategoryService(DistCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public DistUrlResolver<CategoryModel> getCategoryModelUrlResolver() {
        return categoryModelUrlResolver;
    }

    public void setCategoryModelUrlResolver(DistUrlResolver<CategoryModel> categoryModelUrlResolver) {
        this.categoryModelUrlResolver = categoryModelUrlResolver;
    }

    public DistUrlResolver<CategoryModel> getProductFamilyUrlResolver() {
        return productFamilyUrlResolver;
    }

    public void setProductFamilyUrlResolver(DistUrlResolver<CategoryModel> productFamilyUrlResolver) {
        this.productFamilyUrlResolver = productFamilyUrlResolver;
    }

    public DistPunchoutService getDistPunchoutService() {
        return distPunchoutService;
    }

    public void setDistPunchoutService(DistPunchoutService distPunchoutService) {
        this.distPunchoutService = distPunchoutService;
    }

    public SimpleBreadcrumbBuilder getSimpleBreadcrumbBuilder() {
        return simpleBreadcrumbBuilder;
    }

    public void setSimpleBreadcrumbBuilder(SimpleBreadcrumbBuilder simpleBreadcrumbBuilder) {
        this.simpleBreadcrumbBuilder = simpleBreadcrumbBuilder;
    }

    public ProductSearchFacade<ProductData> getProductSearchFacade() {
        return productSearchFacade;
    }

    public void setProductSearchFacade(ProductSearchFacade<ProductData> productSearchFacade) {
        this.productSearchFacade = productSearchFacade;
    }

    public SearchBreadcrumbBuilder getSearchBreadcrumbBuilder() {
        return searchBreadcrumbBuilder;
    }

    public void setSearchBreadcrumbBuilder(final SearchBreadcrumbBuilder searchBreadcrumbBuilder) {
        this.searchBreadcrumbBuilder = searchBreadcrumbBuilder;
    }

    public CategoryPopulator getCategoryPopulator() {
        return categoryPopulator;
    }

    public void setCategoryPopulator(final CategoryPopulator categoryConverter) {
        this.categoryPopulator = categoryConverter;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public DistProductFinderFacade getDistProductFinderFacade() {
        return distProductFinderFacade;
    }

    public void setDistProductFinderFacade(DistProductFinderFacade distProductFinderFacade) {
        this.distProductFinderFacade = distProductFinderFacade;
    }

}
