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
package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.event.DistSendToFriendEvent;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.customer.DistPunchoutService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Product;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.ProductInfo;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.Namb2bacceleratorFacadesConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.message.queue.data.RelatedData;
import com.namics.distrelec.b2b.facades.misc.DistShareWithFriendsFacade;
import com.namics.distrelec.b2b.facades.productfinder.DistProductFinderFacade;
import com.namics.distrelec.b2b.facades.productfinder.data.DistProductFinderData;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import com.namics.distrelec.b2b.facades.search.converter.FactFinderLazyFacetConverter;
import com.namics.distrelec.b2b.facades.seo.DistSeoFacade;
import com.namics.distrelec.b2b.facades.seo.data.MetaData;
import com.namics.distrelec.b2b.facades.util.MetaSanitizerUtil;
import com.namics.distrelec.b2b.storefront.attributes.Attributes;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.ThirdPartyConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.CategoryDataHelper;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.controllers.util.ShopSettingsUtil;
import com.namics.distrelec.b2b.storefront.forms.LoginForm;
import com.namics.distrelec.b2b.storefront.forms.SendToFriendForm;
import com.namics.distrelec.b2b.storefront.seo.DistLink;
import com.namics.distrelec.b2b.storefront.web.view.UiExperienceViewResolver;
import com.namics.hybris.ffsearch.breadcrumb.SearchBreadcrumbBuilder;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;
import com.namics.hybris.ffsearch.data.facet.CategoryDisplayData;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetData;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetSearchPageData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.facet.FilterBadgeData;
import com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.util.WebUtil;

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.product.converters.populator.CategoryPopulator;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Controller for a category page.
 */
@Controller("categoryPageController")
@RequestMapping(value = "/**/c")
public class CategoryPageController extends AbstractSearchPageController {

    protected static final Logger LOG = LogManager.getLogger(CategoryPageController.class);

    private static final String PRODUCT_LIST_PAGE = "category/productListPage";

    private static final String CATEGORY_ONLY = "showCategoriesOnly";

    private static final String PL_CATEGORY_LEVEL = "DL3_Productline";

    public static final String TRACK = "?track=true";

    private static final String DEACTIVATE_CATEGORY_LOGIC = "deactivate.category.logic";

    private static final String UID_CATEGORY_PAGE = "category";

    private static final String UID_PRODUCT_LIST_PAGE = "productList";

    private static final String UID_PRODUCT_FINDER_PAGE_TEMPLATE = "ProductFinderPageTemplate";

    private static final String UID_PRODUCT_FINDER_RESULT_PAGE = "productFinderResult";

    private static final String PAGE_PARAMETER_NAME = "page";

    public static final String DEFAULT_PAGE_SIZE = "0";

    public static final String DEFAULT_PAGE_NUMBER = "1";

    private static final String CATEGORY_CODE_PATH_VARIABLE_PATTERN_COUNT = "/cnt/{categoryCode:.*}";

    /**
     * We use this suffix pattern because of an issue with Spring 3.1 where a Uri value is incorrectly extracted if it contains on or more
     * '.' characters. Please see https://jira.springsource.org/browse/SPR-6164 for a discussion on the issue and future resolution.
     */
    private static final String CATEGORY_CODE_PATH_VARIABLE_PATTERN = "/{categoryCode:.*}";

    private static final String SUBCATEGORY_CODE_PATH_VARIABLE_PATTERN = "/{categoryCode:.*}/subCategory";

    private static final Gson GSON = new Gson();

    private static final String DIGITAL_DATA_LAYER_TERM = "digitalDataLayerTerm";

    private static final String ERROR_CMS_PAGE = "notFound";

    private static final String EOL_MESSAGE = "eolMessage";

    @Autowired
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Autowired
    private SearchBreadcrumbBuilder searchBreadcrumbBuilder;

    @Autowired
    @Qualifier("categoryModelUrlResolver")
    private DistUrlResolver<CategoryModel> categoryModelUrlResolver;

    @Autowired
    private DistCategoryFacade distCategoryFacade;

    @Autowired
    private DistCategoryService categoryService;

    @Autowired
    private DistProductFinderFacade distProductFinderFacade;

    @Autowired
    private DistShareWithFriendsFacade distShareWithFriendsFacade;

    @Autowired
    private CategoryPopulator categoryPopulator;

    @Autowired
    private DistPunchoutService distPunchoutService;

    @Autowired
    private SimpleBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Autowired
    private ProductService productService;

    @Autowired
    private DistSeoFacade distSeoFacade;

    @RequestMapping(value = CATEGORY_CODE_PATH_VARIABLE_PATTERN, method = { RequestMethod.GET, RequestMethod.POST })
    public String category(@PathVariable("categoryCode") final String categoryCode,
                           @RequestParam(value = SEARCH_QUERY_PARAMETER_NAME, required = false) final String searchQuery,
                           @RequestParam(value = PAGE_PARAMETER_NAME, defaultValue = "1") final int page,
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
                           @RequestParam(value = "useDetailView", required = false) final Boolean useDetailView,
                           @RequestParam(value = DIGITAL_DATA_LAYER_TERM, required = false) final String digitalDataLayerTerm)
      throws UnsupportedEncodingException, CMSItemNotFoundException {
        setFFfollowSearchParameter(Boolean.valueOf(REQUEST_TYPE_AJAX.equals(requestType)));
        setNonFilerSearchParameters(request, model);

        // This code snippet is to reduce errors like
        // "de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException: Category with code 'cat-L3D_543267&page=2'"
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

        final CategoryModel successor = getCategoryService().findSuccessor(catCode);
        if (successor != null) {
            final String redirection = getRedirection(request, response, getCategoryModelUrlResolver().resolve(successor));
            if (StringUtils.isNotEmpty(redirection)) {
                return redirection;
            }
        }

        if (digitalDataLayerTerm != null) {
            model.addAttribute(DIGITAL_DATA_LAYER_TERM, digitalDataLayerTerm);
        }

        CMSSiteModel site = getCmsSiteService().getCurrentSite();
        model.addAttribute("isReevooActivatedForWebshop", site != null ? site.isReevooActivated() : false);
        final CMSSiteModel cmsSiteModel = getCmsSiteService().getCurrentSite();
        model.addAttribute("isReevooBrandVisible", cmsSiteModel.isReevooBrandVisible());
        Map<String, String> languageTrkRefMapping = cmsSiteModel.getReevootrkref();
        if (languageTrkRefMapping.keySet() != null && languageTrkRefMapping.keySet().size() > 0) {
            final String currentLang = getI18nService().getCurrentLocale().getLanguage();
            String trkRef = currentLang != null ? languageTrkRefMapping.get(currentLang)
                                                : languageTrkRefMapping.get(languageTrkRefMapping.keySet().toArray()[0]);
            model.addAttribute(DistConstants.PropKey.Reevoo.TRKREF_ID, trkRef);
        } else {
            model.addAttribute(DistConstants.PropKey.Reevoo.TRKREF_ID, null);
        }

        if (distPunchoutService.isCategoryPunchedout(catCode)) {
            // category is punched out - display 404 not found page
            final ContentPageModel errorPage = getContentPageForLabelOrId(ERROR_CMS_PAGE);
            storeCmsPageInModel(model, errorPage);
            setUpMetaDataForContentPage(model, errorPage);
            model.addAttribute(WebConstants.MODEL_KEY_ADDITIONAL_BREADCRUMB,
                               simpleBreadcrumbBuilder.getBreadcrumbs(errorPage.getTitle(), errorPage.getTitle(Locale.ENGLISH)));
            model.addAttribute("metaLoginForm", getMetaLoginForm());
            GlobalMessages.addErrorMessage(model, "system.error.page.not.found");
            // set response code to 404
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return ControllerConstants.Views.Pages.Error.ErrorNotFoundPage;
        }

        final CategoryModel category = getCategoryService().getCategoryForCode(catCode);
        if (category == null) {
            // throw new UnknownIdentifierException("Category with code " + catCode + " not found!");
            return ControllerConstants.Views.Pages.Error.ErrorNotFoundPage;
        }

        getRequestContextData(request).setCategory(category);

        if (distCategoryFacade.isCategoryEmptyForCurrentSite(category)) {
            // Redirect to the parent category.
            final String redirection = getRedirection(request, response, getCategoryModelUrlResolver().resolve(getSuperCategory(category)));
            if (StringUtils.isNotEmpty(redirection)) {
                return redirection;
            }
        }

        String categoryUrl = getCategoryModelUrlResolver().resolve(category);
        String redirection = getRedirection(request, response, categoryUrl);
        if (StringUtils.isNotEmpty(redirection)) {
            return redirection;
        }

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

        FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData;

        searchPageData = getProductSearchFacade().search(queryFromSuggest, searchState,
                                                         paging, getSearchType(searchQuery), catCode, true, log, otherSearchParams);

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

        // DISTRELEC-9134: SRP
        createCategoryDisplayData(model, searchPageData, distCategoryFacade);

        createCategoryBreadcrumb(model, searchPageData, category, getSearchBreadcrumbBuilder());
        Boolean isProductListLevel = Boolean.FALSE;
        // check if selected category is last category in the hierarchy
        if (null != searchPageData.getCategories() && null != searchPageData.getCategories().getValues()) {
            final int lastCatPosition = searchPageData.getCategories().getValues().size() <= 1 ? 0
                                                                                               : (searchPageData.getCategories().getValues().size() - 1);
            if (null != searchPageData.getCode()
                    && searchPageData.getCode().equalsIgnoreCase(searchPageData.getCategories().getValues().get(lastCatPosition).getName())) {
                isProductListLevel = Boolean.TRUE;
            }
        }
        model.addAttribute("isProductListLevel", isProductListLevel);
        // DISTRELEC-5858
        if (CollectionUtils.isEmpty(searchPageData.getResults()) && category.getLevel() != null && category.getLevel().intValue() >= 3) {
            if ("DL3_Productline".equalsIgnoreCase(category.getPimCategoryType().getCode())) {
                categoryUrl = getCategoryModelUrlResolver().resolve(category);
            } else {
                categoryUrl = getCategoryModelUrlResolver().resolve(getSuperCategory(category));
            }

            if (!isSearchRobot(request) && checkForCategoryFiltersOnly(searchPageData)) {
                categoryUrl = getCategoryModelUrlResolver().resolve(getSuperCategory(category));
            }

            redirection = getRedirection(request, response, categoryUrl);
            if (StringUtils.isNotEmpty(redirection)) {
                return redirection;
            }
        }

        totalNumberofPages = searchPageData.getPagination().getNumberOfPages();
        model.addAttribute("trackQuery", catCode);
        String isMarketingCookieEnabled = Attributes.FF_ENSIGTHEN_MARKETING_COOKIE.getValueFromCookies(request);
        model.addAttribute("isMarketingCookieEnabled", isMarketingCookieEnabled);
        // DISTRELEC-5176: add filterstrings
        getProductSearchFacade().addFilterstrings(searchPageData);
        // DISTRELEC-28212
        final StringBuilder filterApplied = new StringBuilder();
        final Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            final String name = paramNames.nextElement();
            if (!name.startsWith("filter_Category") || name.startsWith("filter_")) {
                if (filterApplied.length() > 0) {
                    filterApplied.append('&');
                }
                filterApplied.append(name).append('=').append(request.getParameter(name));
            }
        }
        model.addAttribute("filterapplied", filterApplied.toString());

        // DISTRELEC-4596
        final long productCount = searchPageData.getPagination().getTotalNumberOfResults();
        final String checkDupInBetnCate = checkDupInBetnCate(category, productCount, request, response);
        if (checkDupInBetnCate != null) {
            return checkDupInBetnCate;
        }

        // DISTRELEC-5960 manage empty categories only for search bot
        if (isSearchRobot(request)) {
            if (!categoryService.isRoot(category) && CollectionUtils.isEmpty(searchPageData.getResults())) {
                categoryUrl = getCategoryModelUrlResolver().resolve(getSuperCategory(category));
                redirection = getRedirection(request, response, categoryUrl);
                if (StringUtils.isNotEmpty(redirection)) {
                    return redirection;
                }
            }
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
        // Populate Data Layer
        populateDataLayerForProducts(searchQuery, searchPageData, model, false);
        if (flag) {
            if (searchPageData != null) {
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
                updatePageTitle(category, searchPageData.getFilters(), model);
                int lastProductNumber = searchPageData.getPagination().getCurrentPage() * searchPageData.getPagination().getPageSize();
                model.addAttribute("lastProductNumber",
                                   isLastPage(searchPageData, page) ? (int) searchPageData.getPagination().getTotalNumberOfResults() : lastProductNumber);
                model.addAttribute("firstProductNumber", lastProductNumber - searchPageData.getPagination().getPageSize() + 1);
            }
        } else {
            model.addAttribute(CATEGORY_ONLY, Boolean.TRUE);
            updatePageTitle(category, Collections.<FilterBadgeData<SearchStateData>> emptyList(), model);
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
        }

        final String metaDescription = getMessageSource()
                                                         .getMessage("meta.description.category", new String[] { category.getName() }, "",
                                                                     getI18nService().getCurrentLocale())
                                                         .replace("\"", "&quot;");

        // Populate the OG informations

        final CategoryData categoryData = new CategoryData();
        getCategoryPopulator().populate(category, categoryData);
        populateOpenGraphInformation(category, categoryData, model);
        model.addAttribute("categoryName", categoryData.getName());
        model.addAttribute("categoryCode", categoryData.getCode());

        // populate meta data

        setUpMetaData(model, null != categoryData ? categoryData.getSeoMetaDescription() : metaDescription);

        model.addAttribute("cachingTimeCategoryLink", getDistCachingFacade().getCachingTimeCategoryLink());
        model.addAttribute("detailView", getStoreSessionFacade().isUseDetailView());
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer,
                                                         flag ? DigitalDatalayerConstants.PageType.CATEGORYPLPPAGE
                                                              : (null != category.getLevel()
                                                                      && category.getLevel() > 1) ? DigitalDatalayerConstants.PageType.SUBCATEGORYPAGE
                                                                                                  : DigitalDatalayerConstants.PageType.CATEGORYPAGE);

        getDistDigitalDatalayerFacade().populateFFTrackingAttribute(digitalDatalayer, Boolean.FALSE);
        populateDigitalDataReview(digitalDatalayer);
        if (REQUEST_TYPE_AJAX.equals(requestType)) {
            // return the empty productSearch-JSON for ajax calls (e.g. removing a category)

            return ControllerConstants.Views.Fragments.Product.ProductsForFacetSearch;
        } else {
            addGlobalModelAttributes(model, request);
            model.addAttribute("pageType", PageType.Category);
            if (!getStoreSessionFacade().isUseIconView()) {
                model.addAttribute("categoryPaginationLinksTag", setPaginationHrefLang(request, pageNr < 1 ? 1 : pageNr, totalNumberofPages));
            }
            final List<DistLink> headerLinkLang = new ArrayList<>();
            setupAlternateHreflangLinks(request, headerLinkLang, category, getCategoryModelUrlResolver());
            model.addAttribute("headerLinksLangTags", headerLinkLang);
            model.addAttribute("footerLinksLangTags", headerLinkLang);
            return getViewPage(categoryPage);
        }

    }

    public RelatedData getCategoryRelatedData(String categoryCode) {
        CategoryModel category = getCategoryService().getCategoryForCode(categoryCode);
        if (category.getPimCategoryType() != null) {
            return getDistRelatedDataFacade().findCategoryRelatedData(category.getCode());
        }
        return null;
    }

    private void populateDigitalDataReview(DigitalDatalayer digitalDatalayer) {
        List<Product> products = digitalDatalayer.getProduct();
        for (Product product : products) {
            ProductInfo info = product.getProductInfo();
            info.setReviews(DistConstants.Punctuation.EMPTY);
            info.setReviewScore(DistConstants.Punctuation.EMPTY);
        }
    }

    @RequestMapping(value = CATEGORY_CODE_PATH_VARIABLE_PATTERN_COUNT, method = { RequestMethod.GET,
                                                                                  RequestMethod.POST }, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getCategoryCount(@PathVariable("categoryCode") final String categoryCode,
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

    private boolean populateMetaData(final CategoryModel category, final Model model) {
        final MetaData metaData = distSeoFacade.getMetaDataForCategory(category);
        if (metaData == null) {
            return false;
        }
        setUpMetaData(model, metaData);
        return true;
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

    private boolean checkForCategoryFiltersOnly(final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData) {
        if (searchPageData == null || searchPageData.getFilters() == null) {
            return false;
        }

        final boolean categoryFilter = searchPageData.getFilters().stream().anyMatch(filter -> filter.isCategoryFilter());
        return categoryFilter && searchPageData.getFilters().size() <= 3;
    }

    private String getRedirection(final HttpServletRequest request, final HttpServletResponse response, final String categoryUrl)
                                                                                                                                  throws UnsupportedEncodingException {

        // DISTRELEC-4558 always use the permanent redirection (HTTP code 301)
        final String redirection = checkRequestUrl(request, response, categoryUrl, true);
        if (StringUtils.isNotEmpty(redirection)) {
            return redirection;
        } else if ("1".equals(request.getParameter("page"))) {
            if (request.getParameterMap().size() > 1) {
                final StringBuilder sb = new StringBuilder(UiExperienceViewResolver.REDIRECT_PERMANENT_URL_PREFIX);
                sb.append(request.getRequestURI()).append('?');
                final Enumeration<String> paramNames = request.getParameterNames();
                String p_name = null;
                while (paramNames.hasMoreElements()) {
                    p_name = paramNames.nextElement();
                    if (!"page".equals(p_name)) {
                        final String p_values[] = request.getParameterValues(p_name);
                        for (int i = 0; i < p_values.length; i++) {
                            sb.append('&').append(WebUtil.urlEncode(p_name)).append('=').append(WebUtil.urlEncode(p_values[i]));
                        }
                    }
                }

                return sb.toString().replace("?&", "?");
            }

            return UiExperienceViewResolver.REDIRECT_PERMANENT_URL_PREFIX + request.getRequestURI();
        }

        return StringUtils.EMPTY;
    }

    // DISTRELEC-4596
    protected String checkDupInBetnCate(final CategoryModel category, final long productCount, final HttpServletRequest request,
                                        final HttpServletResponse response) {
        return null;

        /*
         * DISTRELEC-10953
         * THIS METHOD HAS BEEN NOP'D OUT, SEE DISTRELEC-10953 FOR RATIONALE IT CAUSES AN INFINITE REDIRECT LOOP ON SOME CATEGORIES
         */

        /*
         * if (category.getLevel() != null && 5 <= category.getLevel().intValue()) { final CategoryModel superCategory =
         * getSuperCategory(category); if (superCategory.getName().equalsIgnoreCase(category.getName()) &&
         * superCategory.getLevel().equals(Integer.valueOf(category.getLevel().intValue() - 1))) { final boolean queryFromSuggest = false;
         * final Map<String, List<String>> otherSearchParams = new HashMap<String, List<String>>(); final PageableData paging =
         * createPageableData(1, 0, null, ShowMode.Page); final SearchStateData state = new SearchStateData(); state.setQuery("*"); final
         * FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData = getProductSearchFacade().search(queryFromSuggest,
         * state, paging, getSearchType(null), superCategory.getCode(), true, null, otherSearchParams); if (searchPageData != null) { final
         * long sProductCount = searchPageData.getPagination().getTotalNumberOfResults(); if (productCount == sProductCount) { return
         * REDIRECT_PERMANENT_PREFIX + getCategoryModelUrlResolver().resolve(superCategory); } } } } return null;
         */
    }

    @Override
    protected void addQueryString(final HttpServletRequest request, final UriComponentsBuilder uriComponentsBuilder) {
        if (StringUtils.isNotBlank(request.getQueryString())) {
            final String queryString = request.getQueryString();
            uriComponentsBuilder.query("1".equals(request.getParameter("page")) ? queryString.replaceFirst("&?page=1", "") : queryString);
        }
    }

    private LoginForm getMetaLoginForm() {
        return new LoginForm();
    }

    @RequestMapping(value = CATEGORY_CODE_PATH_VARIABLE_PATTERN + "/showmore", method = RequestMethod.GET, produces = "application/json")
    public String categoryShowMore(@PathVariable("categoryCode") final String categoryCode,
                                   @RequestParam(value = SEARCH_QUERY_PARAMETER_NAME, required = false) final String searchQuery,
                                   @RequestParam(value = PAGE_PARAMETER_NAME, defaultValue = "0") final int page,
                                   @RequestParam(value = "pageSize", defaultValue = "0") final int pageSize,
                                   @RequestParam(value = "sort", required = false) final String sortCode,
                                   @RequestParam(value = "log", required = false) final String log, final HttpServletRequest request,
                                   final HttpServletResponse response, final Model model) {
        // final CategoryModel category = getCommerceCategoryService().getCategoryForCode(categoryCode);

        final SearchStateData state = createSearchStateData(request, searchQuery);
        final int pageSizeValue = calculatePageSize(pageSize, request, response);
        final PageableData paging = createPageableDataWithCookie(request, response, page, pageSizeValue, sortCode, null);
        final Map<String, List<String>> otherSearchParams = new HashMap<>();

        final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData = getProductSearchFacade().search(false, state, paging,
                                                                                                                             getSearchType(searchQuery),
                                                                                                                             categoryCode, false, log,
                                                                                                                             otherSearchParams);

        // DISTRELEC-5176: add filterstrings
        getProductSearchFacade().addFilterstrings(searchPageData);

        // Setting the URLs of the previous and next pages
        final String searchURL = storeContinueUrl(request);
        updatePagination(searchURL, (FactFinderPaginationData) searchPageData.getPagination(), model, false);
        model.addAttribute("searchPageData", searchPageData);
        // Populate Data Layer
        populateDataLayerForProducts(searchQuery, searchPageData, model, true);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.CATEGORYPLPPAGE);
        model.addAttribute("products", searchPageData.getResults());
        model.addAttribute("isLastPage", Boolean.valueOf(page >= searchPageData.getPagination().getNumberOfPages()));
        return ControllerConstants.Views.Fragments.Product.ShowMoreProducts;
    }

    @RequestMapping(value = CATEGORY_CODE_PATH_VARIABLE_PATTERN
                            + FactFinderLazyFacetConverter.ADDITIONAL_FACET_PATH, method = RequestMethod.GET, produces = "application/json")
    public final String loadAdditionalFacet(@PathVariable("categoryCode") final String categoryCode,
                                            @RequestParam(SEARCH_QUERY_PARAMETER_NAME) final String searchQuery,
                                            @RequestParam(value = FactFinderLazyFacetConverter.ADDITIONAL_FACET_PARAM_NAME, required = true) final String additionalFacet,
                                            final HttpServletRequest request, final Model model) {

        final SearchStateData searchState = createSearchStateData(request, searchQuery);
        final PageableData paging = createPageableData(0, 0, null, null);

        final FactFinderFacetData<SearchStateData> additionalFacetData = getProductSearchFacade().loadAdditionalFacet(searchState, paging, additionalFacet,
                                                                                                                      isBlank(searchQuery) ? DistSearchType.CATEGORY
                                                                                                                                           : DistSearchType.CATEGORY_AND_TEXT,
                                                                                                                      categoryCode);

        model.addAttribute("facet", additionalFacetData);
        return ControllerConstants.Views.Fragments.Product.AdditionalFacetForFacetSearch;
    }

    @RequestMapping(value = CATEGORY_CODE_PATH_VARIABLE_PATTERN + "/sendToFriend", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> sendSearchResultsToFriendInJSON(@Valid final SendToFriendForm form, final BindingResult bindingResults,
                                                               final HttpServletRequest request) {
        final Map<String, String> result = new HashMap<>();
        if (!getCaptchaUtil().validateReCaptcha(request)) {
            result.put("errorCode", "captcha");
            return result;
        }

        if (bindingResults.hasErrors()) {
            result.put("errorCode", "unknown");
            return result;
        }
        final DistSendToFriendEvent sendToFriendEvent = getSendToFriendEvent(form);
        getDistShareWithFriendsFacade().shareSearchResultsWithFriends(sendToFriendEvent, request.getHeader(HttpHeaders.REFERER));

        result.put("errorCode", StringUtils.EMPTY);
        return result;
    }

    @ExceptionHandler(UnknownIdentifierException.class)
    public String handleUnknownIdentifierException(final UnknownIdentifierException exception, final HttpServletRequest request) {
        request.setAttribute("exception", exception);
        final Object uuid = java.util.UUID.randomUUID();
        final String uuidString = uuid.toString();
        if (ERROR_PAGE_LOG.isDebugEnabled()) {
            ERROR_PAGE_LOG
                          .debug("a technical error occured [uuid: " + uuidString + "], IP Address: " + request.getRemoteAddr() + ". "
                                 + exception.getMessage());
        }
        request.setAttribute("uuid", uuidString);
        return FORWARD_PREFIX + "/" + "notFound";
    }

    protected DistSearchType getSearchType(final String searchQuery) {
        if (isBlank(searchQuery) || "*".equals(searchQuery)) {
            return DistSearchType.CATEGORY;
        }
        return DistSearchType.CATEGORY_AND_TEXT;
    }

    @Override
    protected void setRemoveFilersURL(final FactFinderFacetSearchPageData<?, ?> searchPageData, final HttpServletRequest request) {
        if (searchPageData == null) {
            return;
        }
        try {
            // Get the request URI
            final String requestURI = request.getRequestURI();
            // Get the category UID
            final String categoryUID = requestURI.substring(requestURI.lastIndexOf('/') + 1);
            final CategoryModel category = getCategoryService().getCategoryForCode(categoryUID);
            final CategoryModel l3d_cat = getL3DSuperCategory(category);
            if (l3d_cat != null) {
                final boolean isFiltered = isFiltered(request);
                final StringBuilder requestURL = new StringBuilder(getCategoryModelUrlResolver().resolve(isFiltered ? category : l3d_cat)).append("?page=1");
                final Enumeration<String> paramNames = request.getParameterNames();
                while (paramNames.hasMoreElements()) {
                    final String name = paramNames.nextElement();
                    if ((isFiltered && name.startsWith("filter_Category")) || (!name.startsWith("filter_") && !"page".equals(name))) {
                        requestURL.append('&').append(name).append('=').append(request.getParameter(name));
                    }
                }
                if (isFiltered) {
                    requestURL.append("&f=1");
                }
                searchPageData.setRemoveFiltersURL(Encode.forHtmlAttribute(requestURL.toString()));
            }
        } catch (final Exception exp) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("An error occurs while setting the remove filter URL", exp);
            }
        }
    }

    private boolean isFiltered(final HttpServletRequest request) {
        final Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            final String name = paramNames.nextElement();
            if (name.startsWith("filter_") && !"filter_Buyable".equals(name) && !name.startsWith("filter_Category")) {
                return true;
            }
        }
        return false;
    }

    private CategoryModel getL3DSuperCategory(final CategoryModel category) {
        if (category.getLevel().intValue() <= 3) {
            return category;
        }
        for (final CategoryModel superCat : category.getSupercategories()) {
            if (superCat.getCode().startsWith("class-") || superCat.getLevel() == null) {
                continue;
            }
            if (superCat.getLevel().intValue() == 3) {
                return superCat;
            } else if (superCat.getLevel().intValue() > 3) {
                final CategoryModel temp = getL3DSuperCategory(superCat);
                if (temp != null) {
                    return temp;
                }
            }
        }

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

    protected <QUERY> void updatePageTitle(final CategoryModel category, final List<FilterBadgeData<SearchStateData>> list, final Model model) {
        storeContentPageTitleInModel(model, getPageTitleResolver().resolveCategoryPageTitle(category, list));
    }

    protected void populateOpenGraphInformation(final CategoryModel categoryModel, final CategoryData categoryData, final Model model) {
        final CMSSiteModel cmsSiteModel = getCmsSiteService().getCurrentSite();
        model.addAttribute("ogSiteName", cmsSiteModel.getName(getI18nService().getCurrentLocale()));
        model.addAttribute("ogProductTitle", MetaSanitizerUtil.sanitize(categoryData.getSeoMetaTitle()));
        model.addAttribute("ogProductImage", getPrimaryImageUrl(categoryData, cmsSiteModel));
        model.addAttribute("ogProductDescription", MetaSanitizerUtil.sanitize(categoryData.getSeoMetaDescription()));
        model.addAttribute("sharePageUrl", getUrlForCMSSiteModel(categoryModel, cmsSiteModel));
    }

    protected String getPrimaryImageUrl(final CategoryData categoryData, final CMSSiteModel cmsSiteModel) {
        final CategoryData distCategoryData = (CategoryData) categoryData;
        if (distCategoryData.getImage() != null && !distCategoryData.getImages().isEmpty()) {
            return getPortraitMediumUrl(distCategoryData.getImages(), cmsSiteModel);
        }
        return null;
    }

    protected String getUrlForCMSSiteModel(final CategoryModel categoryModel, final CMSSiteModel cmsSiteModel) {
        final String domain = getConfigurationService().getConfiguration().getString("website." + cmsSiteModel.getUid() + ".http");
        return domain + categoryModelUrlResolver.resolve(categoryModel);
    }

    protected String getViewPage(final CategoryPageModel categoryPage) {
        if (categoryPage != null) {
            final String targetPage = getViewForPage(categoryPage);
            if (targetPage != null && !targetPage.isEmpty()) {
                return targetPage;
            }
        }
        return PAGE_ROOT + PRODUCT_LIST_PAGE;
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

    protected boolean isCategoryDefaultIconView(final CategoryModel category) {
        return category.getPimCategoryType() != null && category.getPimCategoryType().getCategoryPage().booleanValue();
    }

    protected boolean isProductFinderPage(final CategoryPageModel categoryPage) {
        return UID_PRODUCT_FINDER_PAGE_TEMPLATE.equals(categoryPage.getMasterTemplate().getUid());
    }

    protected boolean isProductFinderResultPage(final CategoryPageModel categoryPage) {
        return UID_PRODUCT_FINDER_RESULT_PAGE.equals(categoryPage.getUid());
    }

    protected void storeCategoryPageInModel(final Model model, final CategoryPageModel categoryPage, final String categoryCode) {
        storeCmsPageInModel(model, categoryPage, DistConstants.Webtrekk.TEASER_TRACKING_ONS);
        // add categoryCode to webtrekk page id
        model.addAttribute(ThirdPartyConstants.Webtrekk.WT_PAGE_ID, categoryPage.getUid() + "-" + categoryCode);
    }

    private CategoryModel getSuperCategory(final CategoryModel category) {
        for (final CategoryModel superCategory : category.getSupercategories()) {
            if (superCategory != null && !(superCategory instanceof ClassificationClassModel)) {
                if (!categoryService.isRoot(superCategory) && superCategory.getLevel().equals(Integer.valueOf(category.getLevel().intValue() - 1))) {
                    return superCategory;
                }
            }
        }

        return category;
    }

    @Override
    protected List<DistLink> setupCanonicalHreflang(final HttpServletRequest request, final List<DistLink> result) {
        final String urlPath = request.getRequestURL().toString();
        final int page = getPageNumber(request);
        final UriBuilder uriBuilder = UriBuilder.fromPath(urlPath);
        if (page >= 2) {
            uriBuilder.queryParam(PAGE_PARAMETER_NAME, page);
        }
        final String canonicalPath = uriBuilder.build().toString();
        result.add(setupHeaderLink(LINK_CANONICAL_RELATIONSHIP, canonicalPath));
        return result;
    }

    protected int getPageNumber(final HttpServletRequest request) {
        final String pageAsString = request.getParameter(PAGE_PARAMETER_NAME);
        int page;
        try {
            page = Math.max(1, Integer.parseInt(pageAsString));
        } catch (final NumberFormatException e) {
            page = 1;
        }
        return page;
    }

    @ResponseBody
    @RequestMapping(value = SUBCATEGORY_CODE_PATH_VARIABLE_PATTERN, method = { RequestMethod.GET, RequestMethod.POST })
    public Collection<CategoryDisplayData<SearchStateData>> subCategories(@PathVariable("categoryCode") final String categoryCode,
                                                                          @RequestParam(value = SEARCH_QUERY_PARAMETER_NAME, required = false) final String searchQuery,
                                                                          @RequestParam(value = PAGE_PARAMETER_NAME, defaultValue = "1") final int page,
                                                                          @RequestParam(value = "pageSize", defaultValue = "0") final int pageSize,
                                                                          @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
                                                                          @RequestParam(value = "sort", required = false) final String sortCode,
                                                                          @RequestParam(value = DistProductFinderFacade.REFINE_PAGE_PARAM, required = false) final boolean productFinderRefine,
                                                                          @RequestParam(value = DistProductFinderFacade.RESULT_PAGE_PARAM, required = false) final boolean productFinderResult,
                                                                          @RequestParam(value = "log", required = false) final String log,
                                                                          @RequestParam(value = "requestType", required = false) final String eolCategoryFor,
                                                                          final Model model, final HttpServletRequest request,
                                                                          final HttpServletResponse response) {

        String catCode = categoryCode;
        int pageNr = page;
        final CategoryModel category = getCategoryService().getCategoryForCode(catCode);
        final CategoryPageModel categoryPage = getCategoryPage(category, productFinderResult);

        final Map<String, List<String>> otherSearchParams = new HashMap<>();
        final SearchStateData searchState = createSearchStateData(request, searchQuery);
        final int pageSizeValue = calculatePageSize(pageSize, request, response);
        final PageableData paging = createSearchPageableDataWithCookie(request, response, pageNr, pageSizeValue, sortCode, showMode,
                                                                       getStoreSessionFacade().isUseTechnicalView().booleanValue());

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
        Collection<CategoryDisplayData<SearchStateData>> categoryDisplayDataList = null;
        if (searchPageData != null) {
            categoryDisplayDataList = createCategoryDisplayData1(model, searchPageData, distCategoryFacade);
        }
        distCategoryFacade.getSubCategoryPageData(category, categoryDisplayDataList);

        return categoryDisplayDataList;

    }

    public Collection<CategoryDisplayData<SearchStateData>> createCategoryDisplayData1(final Model model,
                                                                                       final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData,
                                                                                       final DistCategoryFacade distCategoryFacade) {
        if (searchPageData != null && searchPageData.getCategories() != null && searchPageData.getCategories().getValues() != null
                && searchPageData.getCategories().getValues().size() > 0) {
            final Collection<CategoryDisplayData<SearchStateData>> createCategoryDisplayData = distCategoryFacade
                                                                                                                 .createCategoryDisplayData(searchPageData.getCategories()
                                                                                                                                                          .getValues());
            // When debugging is needed
            if (LOG.isDebugEnabled() && createCategoryDisplayData != null) {
                LOG.debug("categoryDisplayDataList = " + createCategoryDisplayData);
                LOG.debug("categoryDisplayDataList Size= " + createCategoryDisplayData.size());
            }
            return createCategoryDisplayData;

        }
        return null;
    }

    public ProductSearchFacade<ProductData> getProductSearchFacade() {
        return productSearchFacade;
    }

    public void setProductSearchFacade(final ProductSearchFacade<ProductData> productSearchFacade) {
        this.productSearchFacade = productSearchFacade;
    }

    public SearchBreadcrumbBuilder getSearchBreadcrumbBuilder() {
        return searchBreadcrumbBuilder;
    }

    public void setSearchBreadcrumbBuilder(final SearchBreadcrumbBuilder searchBreadcrumbBuilder) {
        this.searchBreadcrumbBuilder = searchBreadcrumbBuilder;
    }

    public DistUrlResolver<CategoryModel> getCategoryModelUrlResolver() {
        return categoryModelUrlResolver;
    }

    public void setCategoryModelUrlResolver(final DistUrlResolver<CategoryModel> categoryModelUrlResolver) {
        this.categoryModelUrlResolver = categoryModelUrlResolver;
    }

    @Override
    public DistCategoryFacade getDistCategoryFacade() {
        return distCategoryFacade;
    }

    @Override
    public void setDistCategoryFacade(final DistCategoryFacade distCategoryFacade) {
        this.distCategoryFacade = distCategoryFacade;
    }

    public DistCategoryService getCategoryService() {
        return categoryService;
    }

    public void setCategoryService(final DistCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public DistProductFinderFacade getDistProductFinderFacade() {
        return distProductFinderFacade;
    }

    public void setDistProductFinderFacade(final DistProductFinderFacade distProductFinderFacade) {
        this.distProductFinderFacade = distProductFinderFacade;
    }

    public DistShareWithFriendsFacade getDistShareWithFriendsFacade() {
        return distShareWithFriendsFacade;
    }

    public void setDistShareWithFriendsFacade(final DistShareWithFriendsFacade distShareWithFriendsFacade) {
        this.distShareWithFriendsFacade = distShareWithFriendsFacade;
    }

    public CategoryPopulator getCategoryPopulator() {
        return categoryPopulator;
    }

    public void setCategoryPopulator(final CategoryPopulator categoryConverter) {
        this.categoryPopulator = categoryConverter;
    }
}
