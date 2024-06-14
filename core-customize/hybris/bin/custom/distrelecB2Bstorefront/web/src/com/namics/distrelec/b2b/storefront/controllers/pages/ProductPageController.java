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
import java.math.BigDecimal;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.net.HttpHeaders;
import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.event.DistCatalogPlusPriceRequestEvent;
import com.namics.distrelec.b2b.core.event.DistQuoteProductPriceEvent;
import com.namics.distrelec.b2b.core.event.DistSendToFriendEvent;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.*;
import com.namics.distrelec.b2b.facades.compliance.DistComplianceFacade;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.hazardous.DistHazardousFacade;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerData;
import com.namics.distrelec.b2b.facades.message.queue.data.RelatedData;
import com.namics.distrelec.b2b.facades.misc.DistShareWithFriendsFacade;
import com.namics.distrelec.b2b.facades.order.cart.DistCartFacade;
import com.namics.distrelec.b2b.facades.product.DistProductPriceQuotationFacade;
import com.namics.distrelec.b2b.facades.product.data.DistDownloadSectionData;
import com.namics.distrelec.b2b.facades.reco.DistRecommendationFacade;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import com.namics.distrelec.b2b.facades.seo.data.MetaData;
import com.namics.distrelec.b2b.facades.snapeda.SnapEdaFacade;
import com.namics.distrelec.b2b.facades.storesession.data.ShoppingSettingsCookieData;
import com.namics.distrelec.b2b.facades.util.MetaSanitizerUtil;
import com.namics.distrelec.b2b.storefront.attributes.Attributes;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.CatalogPlusBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.ProductBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.ThirdPartyConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.controllers.util.ProductDataHelper;
import com.namics.distrelec.b2b.storefront.controllers.util.ShopSettingsUtil;
import com.namics.distrelec.b2b.storefront.etag.ETagCalculationService;
import com.namics.distrelec.b2b.storefront.etag.ETagHeaderService;
import com.namics.distrelec.b2b.storefront.forms.QuoteProductPriceForm;
import com.namics.distrelec.b2b.storefront.forms.SendToFriendForm;
import com.namics.distrelec.b2b.storefront.security.exceptions.CatalogPlusItemAccessException;
import com.namics.distrelec.b2b.storefront.security.exceptions.PunchoutException;
import com.namics.distrelec.b2b.storefront.seo.DistLink;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;
import com.namics.hybris.ffsearch.data.campaign.FactFinderFeedbackTextData;
import com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.hybris.platform.b2b.enums.B2BPermissionTypeEnum;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.product.CommerceProductService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * Controller for product details page.
 */
@Controller
@RequestMapping(value = ProductPageController.PRODUCT_PAGE_REQUEST_MAPPING)
public class ProductPageController extends AbstractSearchPageController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductPageController.class);

    public static final String ACCESSORIES_PATH = "/accessories";

    public static final String CONSISTENT_WITH_PATH = "/consistent-with";

    private static final String PRODUCT_TITLE_SEPARATOR_AFTER_MPN = " - ";

    private static final String PRODUCT_TITLE_SEPARATOR_BEFORE_MANUFACTURER = ", ";

    private static final String FILTER_URL = "filterURL";

    private static final String EOL_MESSAGE = "eolMessage";

    public static final String PRODUCT_ATTRIBUTE_NAME = "product";

    private static final String PROPOSITION_CMS_POSITION = "Proposition";

    private static final String PROPOSITION_ANALYTICS_PREFIX = "hero18 propositional-";

    private static final String REPLACEMENT_CATEGORY_LINK = "replacementProductLink";

    public static final String PUNCHOUT_ERROR_MESSAGE = "Current customer has no permission to buy or see this product, because the product is not buyable for the current sales organisation or a punchout applies.";

    public static final String PRODUCT_PAGE_REQUEST_MAPPING = "/**/p";

    public static final String ENERGY_EFFICENCY_PATH_VARIABLE_PATTERN = "/energyefficencydata/{productCode:.*}";

    public static final String MULTIPLE_PRODUCT_ALTERNATIVE_URL = "/getAlterntaives/{productCode:.*}";

    private static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.DIST_MANUFACTURER,
                                                                             ProductOption.PROMOTION_LABELS);

    /**
     * We use this suffix pattern because of an issue with Spring 3.1 where a Uri value is incorrectly extracted if it contains on or more
     * '.' characters. Please see https://jira.springsource.org/browse/SPR-6164 for a discussion on the issue and future resolution.
     */
    public static final String PRODUCT_CODE_PATH_VARIABLE_PATTERN = "/{productCode:.*}";

    public static final String CATALOG_PLUS_PRODUCT_CODE_PATH_VARIABLE_PATTERN = "/serviceplus" + PRODUCT_CODE_PATH_VARIABLE_PATTERN;

    private static final int COOKIE_LISTSIZE_DEFAULT_VALUE = 10;

    private static final String DIGITAL_DATA_LAYER_TERM = "digitalDataLayerTerm";

    @Autowired
    @Qualifier("productModelUrlResolver")
    private DistUrlResolver<ProductModel> productModelUrlResolver;

    @Autowired
    @Qualifier("catalogPlusProductModelUrlResolver")
    private DistUrlResolver<ProductModel> catalogPlusProductModelUrlResolver;

    @Autowired
    @Qualifier("categoryModelUrlResolver")
    private DistUrlResolver<CategoryModel> categoryModelUrlResolver;

    @Autowired
    private CommerceProductService commerceProductService;

    @Autowired
    private DistCategoryService distCategoryService;

    @Autowired
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Autowired
    private DistShareWithFriendsFacade distShareWithFriendsFacade;

    @Autowired
    private DistProductPriceQuotationFacade distProductPriceQuotationFacade;

    @Autowired
    private ProductBreadcrumbBuilder productBreadcrumbBuilder;

    @Autowired
    private DistHazardousFacade hazardousFacade;

    @Autowired
    @Qualifier("distRecommendationFacade")
    private DistRecommendationFacade<ProductData, FactFinderFeedbackTextData> recommendationFacade;

    @Autowired
    private CatalogPlusBreadcrumbBuilder catalogPlusBreadcrumbBuilder;

    @Autowired
    @Qualifier("b2bCartFacade")
    private DistCartFacade cartFacade;

    @Autowired
    private CMSComponentService cmsComponentService;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private DistComplianceFacade distComplianceFacade;

    @Autowired
    private ETagCalculationService eTagCalculationService;

    @Autowired
    private ETagHeaderService eTagHeaderService;

    @Autowired
    private SnapEdaFacade snapEdaFacade;

    public String getWtCurrency() {
        return getStoreSessionFacade().getCurrentCurrency().getIsocode();
    }

    @GetMapping(value = { PRODUCT_CODE_PATH_VARIABLE_PATTERN, CATALOG_PLUS_PRODUCT_CODE_PATH_VARIABLE_PATTERN })
    public String productDetail(
                                @PathVariable("productCode") final String productCode,
                                @RequestParam(value = SEARCH_QUERY_PARAMETER_NAME, required = false) final String searchQuery,
                                @RequestParam(value = TRACK_QUERY_PARAMETER_NAME, required = false) final String trackQuery,
                                @RequestParam(value = PAGE_PARAMETER_NAME, defaultValue = "0", required = false) final int page,
                                @RequestParam(value = "sort", required = false) final String sortCode,
                                @RequestParam(value = FILTER_URL, required = false) final String filterURL,
                                @RequestParam(value = "eolReplacementForParam", required = false) final String eolReplacementForParam,
                                @RequestParam(value = "eolReplacedByParam", required = false) final String eolReplacedByParam,
                                @RequestParam(value = "manufacturerCode", required = false) final String manufacturerCode,
                                @RequestParam(value = "outletSearch", required = false) final Boolean outletSearchFlag,
                                @RequestParam(value = "newSearch", required = false) final Boolean newSearchFlag,
                                @RequestParam(value = "pdpbanner", required = false) final String pdpbannerUid,
                                @RequestParam(value = POS_PARAMETER_NAME, required = false) final String pos,
                                @RequestParam(value = ORIG_POS_PARAMETER_NAME, required = false) final String origPos,
                                @RequestParam(value = ORIG_PAGE_SIZE_PARAMETER_NAME, required = false) final String origPageSize,
                                @RequestParam(value = "filterapplied", required = false) final String filterapplied,
                                @RequestParam(required = false) String digitalDataLayerTerm,
                                final Model model, final HttpServletRequest request, final ServletWebRequest servletWebRequest,
                                RedirectAttributes redirectAttributes, final HttpServletResponse response)
                                                                                                           throws CMSItemNotFoundException,
                                                                                                           UnsupportedEncodingException {

        if (userFacade.isAnonymousUser()) {
            eTagHeaderService.setCacheControlHeader(response);
            String eTag = eTagCalculationService.calculateProductPageETag(productCode);
            if (servletWebRequest.checkNotModified(eTag)) {
                return null;
            }
        }

        if (getProductFacade().isProductExcluded(productCode) && getProductFacade().enablePunchoutFilterLogic()) {
            GlobalMessages.addRedirectErrorMessage(redirectAttributes, "article.not.found.error", productCode);

            SiteChannel currentChannel = getBaseStoreService().getCurrentBaseStore().getChannel();
            if (currentChannel.equals(SiteChannel.B2C) && isNotAvailableForB2CUser(productCode)) {
                GlobalMessages.addRedirectErrorMessage(redirectAttributes, "article.business.only.error");
            }
            return REDIRECT_PREFIX + ROOT;
        }

        if (digitalDataLayerTerm != null) {
            model.addAttribute(DIGITAL_DATA_LAYER_TERM, digitalDataLayerTerm);
        }

        model.addAttribute("businessOnlyProduct", isNotAvailableForB2CUser(productCode));
        model.addAttribute("hazardStatementData", hazardousFacade.getAllDistHazardStatement());
        model.addAttribute("SuppHazardInfoData", hazardousFacade.getAllDistSupplementalHazardInfo());
        model.addAttribute("pos", pos);
        model.addAttribute("origPos", origPos);
        model.addAttribute("origPageSize", origPageSize);
        model.addAttribute("trackQuery", trackQuery);
        model.addAttribute("filterapplied", filterapplied);

        // DISTRELEC-6526 and DISTRELEC-6987 Redirection of EOL Products
        // There are two cases of EOL product handling
        // - EOL product with stock: in this case we do not directly redirect to
        // the replacement, but we show a popin to show the info.
        // - EOL product without stock: in this case we directly redirect to the
        // replacement showing the info about redirection.

        ProductModel productModel = getProductService().getProductForCode(productCode);

        // DISTRELEC-9328: Deactivate Service+ for all shops (all countries)
        if (productModel == null || StringUtils.isNotBlank(productModel.getCatPlusSupplierAID())) {
            throw new UnknownIdentifierException(String.format("Product with code '%s' not found!", productCode));
        }

        getSessionService().setAttribute("currentProduct", productModel);
        getSessionService().setAttribute("manufacture", productModel.getManufacturer());

        if (!StringUtils.isEmpty(pdpbannerUid)) {
            try {
                AbstractCMSComponentModel cmsComponent = cmsComponentService.getAbstractCMSComponent(pdpbannerUid);
                model.addAttribute("propositionalComponent", cmsComponent);
            } catch (CMSItemNotFoundException e) {
                LOG.warn("PDP banner component with uid {} not found", pdpbannerUid);
            }
        }

        final DistSalesStatusModel productSalesStatusModel = getProductFacade().getProductSalesStatusModel(productCode);

        // A product is valid only when we have salesStatus
        if (productSalesStatusModel == null) {
            LOG.error("No Sales status found for Product with code '{}'!", productCode);
            return forwardResponseToNotFoundPage();
        }

        if (productSalesStatusModel.isEndOfLifeInShop() && isNoCrawlCategory(productModel)) {
            model.addAttribute("metaRobots", "noindex,follow");
        }

        // If sales status is there but it is of type which is suppose to be not
        // visible
        if (!productSalesStatusModel.isVisibleInShop()) {
            LOG.error("Product with code '{}' have SaleStatus '{}'. Which means this product is not visible in webshop !", productCode,
                      productSalesStatusModel.getCode());
            return forwardResponseToNotFoundPage();
        }

        // check if its comes from suggest
        boolean queryFromSuggest = false;
        if (request.getParameter(DistConstants.FactFinder.FF_TRACKING) != null) {
            queryFromSuggest = Boolean.valueOf(request.getParameter(DistConstants.FactFinder.FF_TRACKING)).booleanValue();
        }

        final String productSalesStatusCode = productSalesStatusModel.getCode();

        // initialize EOL variables to display messages
        final String eolReplacedBy = "";
        final String eolReplacementFor = eolReplacementForParam;
        final boolean eolShowProduct = false;

        // DISTRELEC-7395 Checking redirection before doing any job.
        final String productURL = getProductUrlResolver(productModel).resolve(productModel);
        final String redirection = checkRequestUrl(request, response, productURL, true);

        if (StringUtils.isNotEmpty(redirection)) {
            return redirection;
        }

        boolean phaseoutproductflag = false;
        final List<String> notforsale = getErpStatusUtil().getErpSalesStatusFromConfiguration(DistConfigConstants.ErpSalesStatus.ATTRIBUTE_NO_PRODUCT_FOR_SALE_GENERIC);

        // Populate replacement (alternative) for all products (not only for EOL)
        populateMessageForPhaseoutProduct(model, productModel);

        // DISTRELEC-4347: EOL and other sales status dependent PDP messaging &
        // successor products
        if (notforsale.contains(productSalesStatusCode)) {
            phaseoutproductflag = true;
        }

        if (getCartFacade().hasSessionCart()) {
            model.addAttribute("cartData", getCartFacade().getSessionCart());
        }

        populateEOLReplacementInformation(model, eolReplacementFor, eolReplacedBy, eolShowProduct, productModel);

        final boolean prevnextbtn = getConfigurationService().getConfiguration().getBoolean("distrelec.productdetailed.page.prevnext.button.enabled", false);
        FactFinderPaginationData pagination = null;
        String productCodeToResolve = productCode;
        if (page > 0 && prevnextbtn) {
            final StringBuilder searchQueryBuilder = new StringBuilder(searchQuery == null ? "" : searchQuery);
            final SearchStateData searchState = createSearchStateData(request, searchQueryBuilder.toString());
            final PageableData pageableData = createPageableData(page, 1, sortCode, null);

            // DISTRELEC-5900 put this search query in internal FF logging.
            if (!StringUtils.contains(searchQuery, DistrelecfactfindersearchConstants.LOG_INTERNAL_PARAM)) {
                final SearchQueryData searchQueryData = new SearchQueryData();
                searchQueryData.setValue(searchState.getQuery().getValue() + "&" + DistrelecfactfindersearchConstants.LOG_INTERNAL_PARAM);
                searchState.setQuery(searchQueryData);
            }

            if (BooleanUtils.isTrue(outletSearchFlag)) {
                // Outlet search
                pagination = getProductSearchFacade().getPagination(queryFromSuggest, searchState, pageableData, DistSearchType.OUTLET, null);
            } else if (BooleanUtils.isTrue(newSearchFlag)) {
                // New Search
                pagination = getProductSearchFacade().getPagination(queryFromSuggest, searchState, pageableData, DistSearchType.NEW, null);
            } else if (StringUtils.isNotEmpty(manufacturerCode)) {
                // Manufacturer search
                pagination = getProductSearchFacade().getPagination(queryFromSuggest, searchState, pageableData, DistSearchType.MANUFACTURER, manufacturerCode);
            } else {
                // Text search
                pagination = getProductSearchFacade().getPagination(queryFromSuggest, searchState, pageableData, DistSearchType.TEXT, productCode);
            }

            // Update product code. When clicking the "next" button on a product
            // detail page the product code in the URL stays the same. The
            // new product code is returned by the paging request.
            if (pagination != null && StringUtils.isNotBlank(pagination.getCurrentProductCode())) {
                productCodeToResolve = pagination.getCurrentProductCode();
            }
        }

        productModel = getProductService().getProductForCode(productCodeToResolve);

        model.addAttribute("cachingTimeCategoryLink", getDistCachingFacade().getCachingTimeCategoryLink());

        String seoMetaTitle = StringUtils.EMPTY;
        String seoMetaDescription = StringUtils.EMPTY;
        CMSSiteModel cmsSiteModel = cmsSiteService.getCurrentSite();
        final String cmsSiteName = cmsSiteModel.getName(getI18nService().getCurrentLocale());
        model.addAttribute("ogSiteName", cmsSiteName);
        if (StringUtils.isNotEmpty(productModel.getSeoMetaTitle())) {
            seoMetaTitle = productModel.getSeoMetaTitle().replace("${siteName}", cmsSiteName);
            model.addAttribute("ogProductTitle", MetaSanitizerUtil.sanitize(seoMetaTitle));
        }

        if (StringUtils.isNotEmpty(productModel.getSeoMetaDescription())) {
            seoMetaDescription = productModel.getSeoMetaDescription().replace("${siteName}", cmsSiteName);
            model.addAttribute("ogProductDescription", MetaSanitizerUtil.sanitize(seoMetaDescription));
        }

        final String metaDescription = getMessageSource().getMessage("meta.description.product", new Object[] { productModel.getName() }, "",
                                                                     getI18nService().getCurrentLocale());

        setUpMetaData(model, StringUtils.isNoneEmpty(seoMetaDescription) ? seoMetaDescription : metaDescription);

        // populate meta data
        populateMetaData(productModel, model);

        storeContentPageTitleInModel(model,
                                     StringUtils.isNoneEmpty(seoMetaTitle) ? seoMetaTitle : getPageTitleResolver().resolveProductPageTitle(productModel));
        populateProductDetailForDisplay(productModel, pagination, model, request, filterURL, phaseoutproductflag, productSalesStatusModel);

        populateSimilarProductBaseUrl(productModel, model);

        // DISTRELEC-7395 Adding global attributes only if we don't redirect
        addGlobalModelAttributes(model, request);

        model.addAttribute("pageType", PageType.Product);
        // Add Data layer attributes
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.PDPPAGE);
        if (Boolean.TRUE.equals(model.asMap().get(DistConstants.FactFinder.FF_MPN_ALTERNATIVE_MATCH_KEY))) {
            GlobalMessages.addConfMessage(model, DistConstants.FactFinder.FF_MPN_MESSAGE_KEY);
        }

        // Add header and footer Links
        final List<DistLink> headerLinkLang = new ArrayList<>();
        setupAlternateHreflangLinks(request, headerLinkLang, productModel, getProductUrlResolver(productModel));
        model.addAttribute("headerLinksLangTags", headerLinkLang);
        model.addAttribute("footerLinksLangTags", headerLinkLang);

        populateSearchEvent(request, model);
        // DISTRELEC-4684
        if (request.getRequestURI().contains("/serviceplus") && !productSalesStatusModel.isEndOfLifeInShop()) {
            model.addAttribute("metaRobots", "noindex,follow,noodp,noydir");
        }

        String isMarketingCookieEnabled = Attributes.FF_ENSIGTHEN_MARKETING_COOKIE.getValueFromCookies(request);
        model.addAttribute("isMarketingCookieEnabled", isMarketingCookieEnabled);

        addToLastViewedCookie(request, response, productCodeToResolve);
        setFreeShippingValue(model);

        getDistDigitalDatalayerFacade().populateBannersFromPosition(digitalDatalayer, model, PROPOSITION_CMS_POSITION, PROPOSITION_ANALYTICS_PREFIX);
        getDistDigitalDatalayerFacade().populateFFTrackingAttribute(digitalDatalayer, Boolean.FALSE);
        populateDigitalDataReview(digitalDatalayer);
        return getViewForPage(model);
    }

    private boolean isNotAvailableForB2CUser(@PathVariable("productCode") String productCode) {
        return getProductFacade().isProductExcludedForSiteChannel(productCode, SiteChannel.B2C);
    }

    public RelatedData getProductRelatedData(ProductData product) {
        return getDistRelatedDataFacade().findProductRelatedData(product.getCode());
    }

    private void populateDigitalDataReview(DigitalDatalayer digitalDatalayer) {
        List<Product> products = digitalDatalayer.getProduct();
        for (Product product : products) {
            ProductInfo info = product.getProductInfo();
            info.setReviews(DistConstants.Punctuation.EMPTY);
            info.setReviewScore(DistConstants.Punctuation.EMPTY);
        }
    }

    private boolean isNoCrawlCategory(ProductModel product) {
        final List<String> productCategoryInfo = new ArrayList<>();
        if (product.getPrimarySuperCategory() != null) {
            productCategoryInfo.add(product.getPrimarySuperCategory().getCode());
        }
        for (final CategoryModel category : product.getSupercategories()) {
            fillCategories(category, productCategoryInfo);
        }
        Configuration configuration = getConfigurationService().getConfiguration();
        final String noCrawlCategoryCodes = configuration.getString("specialwebshop.category.nocrawl");
        List<String> noCrawlCategoryList = Arrays.asList(noCrawlCategoryCodes.split(","));
        for (String productCategory : productCategoryInfo) {
            if (noCrawlCategoryList != null && noCrawlCategoryList.contains(productCategory)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    private void fillCategories(final CategoryModel category, final List<String> productCategoryInfo) {
        for (final CategoryModel superCategory : category.getSupercategories()) {
            if ((superCategory != null && !(superCategory instanceof ClassificationClassModel)) && //
                    superCategory.getLevel().equals(Integer.valueOf(category.getLevel().intValue() - 1)) && //
                    superCategory.getLevel().intValue() > 0) {
                productCategoryInfo.add(superCategory.getCode());
                fillCategories(superCategory, productCategoryInfo);
            }
        }
    }

    protected DigitalDatalayer populateSearchEvent(HttpServletRequest request, Model model) {

        DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        Page page = digitalDatalayer.getPage();
        if (null == page) {
            page = new Page();
        }
        PageInfo pageInfo = page.getPageInfo();
        if (null == pageInfo) {
            pageInfo = new PageInfo();
        }

        if (model.getAttribute(DIGITAL_DATA_LAYER_TERM) != null) {
            pageInfo.setSearchTerm((String) model.getAttribute(DIGITAL_DATA_LAYER_TERM));
        }

        Search searchData = pageInfo.getSearch();
        if (null == searchData) {
            searchData = new Search();
        }
        String referUrl = request.getHeader(REFERER);
        if (StringUtils.isNotEmpty(referUrl) && referUrl.contains("/search")) {
            searchData.setPdp(true);
            model.addAttribute("pdpSearchDatalayer", true);
        }
        pageInfo.setSearch(searchData);
        page.setPageInfo(pageInfo);
        digitalDatalayer.setPage(page);
        return digitalDatalayer;
    }

    private String forwardResponseToNotFoundPage() {
        return FORWARD_PREFIX + "/notFound";
        // return "redirect:/" + getCmsPageService().getHomepage().getName();
    }

    private boolean populateMetaData(final ProductModel productModel, final Model model) {
        final MetaData metaData = getDistSeoFacade().getMetaDataForProduct(productModel);
        if (metaData == null) {
            return false;
        }
        setUpMetaData(model, metaData);
        return true;
    }

    @GetMapping(value = ENERGY_EFFICENCY_PATH_VARIABLE_PATTERN + "/get", produces = "application/json")
    public String productEnergyEfficency(@PathVariable("productCode") final String productCode, final Model model) {
        populateEnergyEfficencyData(model, productCode);
        return ControllerConstants.Views.Fragments.Product.ProductEnergyEfficency;
    }

    @GetMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/downloads", produces = "application/json")
    public String productDownloads(@PathVariable("productCode") final String productCode, final Model model) {
        final ProductData product = getProductWithOptions(productCode, Arrays.asList(ProductOption.DOWNLOADS));
        final List<DistDownloadSectionData> productlist = product != null ? product.getDownloads() : Collections.<DistDownloadSectionData> emptyList();
        model.addAttribute("productDownloads", productlist);

        return ControllerConstants.Views.Fragments.Product.ProductDownloads;
    }

    /**
     * Load similar products for the product specified by its code.
     *
     * @param productCode
     *            the product code
     * @param offset
     *            the position from which similar products will be loaded
     * @param size
     *            the maximum number of similar products to be loaded
     * @return the similar products page
     */
    @GetMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/similars", produces = "application/json")
    public String getSimilarProducts(@PathVariable("productCode") final String productCode,
                                     @RequestParam(value = "offset", required = true, defaultValue = "0") final int offset,
                                     @RequestParam(value = "size", required = false, defaultValue = "10") final int size, final Model model) {
        try {
            if (getProductFacade().isProductBuyable(productCode)) {
                model.addAttribute("products", getProductFacade().getSimilarProducts(productCode, offset, size));
                model.addAttribute("offset", Integer.valueOf(offset < 0 ? 0 : offset));
                model.addAttribute("maxSize", Integer.valueOf(size));
            }
        } catch (final Exception exp) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No product found for code: {}", productCode);
            }
        }

        return ControllerConstants.Views.Fragments.Product.SimilarProducts;
    }

    /**
     * Load product accessories for the product specified by its code.
     *
     * @param productCode
     *            the product code
     * @param offset
     *            the position from which similar products will be loaded
     * @param size
     *            the maximum number of accessories products to be loaded
     * @return the similar products page
     */
    @GetMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + ACCESSORIES_PATH, produces = "application/json")
    public String getProductAccessories(@PathVariable("productCode") final String productCode,
                                        @RequestParam(value = "offset", required = true, defaultValue = "0") final int offset,
                                        @RequestParam(value = "size", required = false, defaultValue = "10") final int size, final Model model) {
        return getProductReferences(productCode, Arrays.asList(ProductReferenceTypeEnum.DIST_ACCESSORY), offset, size, model);
    }

    /**
     * Load product "Consistent With" for the product specified by its code.
     *
     * @param productCode
     *            the product code
     * @param offset
     *            the position from which similar products will be loaded
     * @param size
     *            the maximum number of similar products to be loaded
     * @return the similar products page
     */
    @GetMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + CONSISTENT_WITH_PATH, produces = "application/json")
    public String getProductConsistentWith( //
                                           @PathVariable("productCode") final String productCode, //
                                           @RequestParam(value = "offset", required = true, defaultValue = "0") final int offset, //
                                           @RequestParam(value = "size", required = false, defaultValue = "10") final int size, //
                                           final Model model) {
        return getProductReferences(productCode, Arrays.asList(ProductReferenceTypeEnum.DIST_CONSISTENT_WITH), offset, size, model);
    }

    protected String getProductReferences(final String productCode, final List<ProductReferenceTypeEnum> referenceTypes, final int offset, final int size,
                                          final Model model) {
        try {
            model.addAttribute("products", getProductFacade().getProductsReferences(normalizeProductCode(productCode), referenceTypes, offset, size));
            model.addAttribute("offset", Math.max(offset, 0));
            model.addAttribute("maxSize", size);
        } catch (final Exception exp) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No product found for code: {}", productCode);
            }
        }
        return ControllerConstants.Views.Fragments.Product.ProductAccessories;
    }

    @GetMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/zoomImages")
    public String showZoomImages(@PathVariable("productCode") final String productCode,
                                 @RequestParam(value = "galleryPosition", required = false) final String galleryPosition, final Model model) {
        if (!getProductFacade().isProductBuyable(productCode)) {
            throw new PunchoutException(PUNCHOUT_ERROR_MESSAGE);
        }

        final ProductData productData = getProductFacade().getProductForCodeAndOptions(productCode, Collections.singleton(ProductOption.GALLERY));
        final List<Map<String, ImageData>> images = getGalleryImages(productData);
        model.addAttribute("galleryImages", images);
        model.addAttribute(PRODUCT_ATTRIBUTE_NAME, productData);
        if (galleryPosition != null) {
            try {
                model.addAttribute("zoomImageUrl", images.get(Integer.parseInt(galleryPosition)).get("zoom").getUrl());
            } catch (final IndexOutOfBoundsException ignore) {
                model.addAttribute("zoomImageUrl", "");
            } catch (final NumberFormatException ignore) {
                model.addAttribute("zoomImageUrl", "");
            }
        }
        return ControllerConstants.Views.Fragments.Product.ZoomImagesPopup;
    }

    @GetMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/quickView")
    public String showQuickView(@PathVariable("productCode") final String productCode, final Model model, final HttpServletRequest request) {
        if (!getProductFacade().isProductBuyable(productCode)) {
            throw new PunchoutException(PUNCHOUT_ERROR_MESSAGE);
        }
        final ProductData productData = getProductFacade().getProductForCodeAndOptions(productCode,
                                                                                       Arrays.asList(ProductOption.BASIC, ProductOption.PRICE,
                                                                                                     ProductOption.SUMMARY, ProductOption.DESCRIPTION,
                                                                                                     ProductOption.CATEGORIES,
                                                                                                     ProductOption.PROMOTIONS, ProductOption.STOCK,
                                                                                                     ProductOption.REVIEW, ProductOption.VOLUME_PRICES));
        populateProductData(productData, model, request, null);
        return ControllerConstants.Views.Fragments.Product.QuickViewPopup;
    }

    @PostMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/sendToFriend")
    public @ResponseBody Map<String, String> sendProductToFriendInJSON(@PathVariable("productCode") final String productCode,
                                                                       @Valid final SendToFriendForm form, final BindingResult bindingResults,
                                                                       final HttpServletRequest request) {
        if (!getProductFacade().isProductBuyable(productCode)) {
            throw new PunchoutException(PUNCHOUT_ERROR_MESSAGE);
        }
        final Map<String, String> result = new HashMap<>();
        if (!getCaptchaUtil().validateReCaptcha(request)) {
            result.put("errorCode", "captcha");
            return result;
        }

        if (bindingResults.hasErrors()) {
            result.put("errorCode", "unknown");
            return result;
        }

        final ProductModel productModel = getProductService().getProductForCode(productCode);
        final DistSendToFriendEvent sendToFriendEvent = getSendToFriendEvent(form);
        getDistShareWithFriendsFacade().shareProductWithFriends(sendToFriendEvent, productModel, request.getHeader(HttpHeaders.REFERER));

        result.put("errorCode", StringUtils.EMPTY);
        return result;
    }

    @PostMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/quoteProductPrice")
    public ResponseEntity quoteProductPrice(@PathVariable("productCode") final String productCode, @Valid final QuoteProductPriceForm form,
                                            final BindingResult bindingResults) {
        if (!getProductFacade().isProductBuyable(productCode)) {
            throw new PunchoutException(PUNCHOUT_ERROR_MESSAGE);
        }
        if (bindingResults.hasErrors()) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        final ProductModel productModel = getProductService().getProductForCode(productCode);
        DistQuoteProductPriceEvent quoteProductPrieEvent = null;
        if (getDistOciService().isOciCustomer() || getDistAribaService().isAribaCustomer()) {
            quoteProductPrieEvent = new DistQuoteProductPriceEvent(form.getCompany(), form.getFirstName(), form.getLastName(), form.getEmail(), form.getPhone(),
                                                                   form.getModalQuotationQuantity(), form.getModalQuotationMessage(), productModel);
        } else { // DISTRELEC-8963
            final CustomerData customer = getB2bCustomerFacade().getCurrentCustomer();
            final String companyName = customer.getUnit() != null ? customer.getUnit().getName() : customer.getCompanyName();
            final String firstName = customer.getContactAddress() != null ? customer.getContactAddress().getFirstName() : customer.getFirstName();
            final String lastName = customer.getContactAddress() != null ? customer.getContactAddress().getLastName() : customer.getLastName();
            final String email = customer.getContactAddress() != null ? customer.getContactAddress().getEmail() : customer.getEmail();
            final String phone = customer.getContactAddress() != null ? customer.getContactAddress().getPhone() : "";

            quoteProductPrieEvent = new DistQuoteProductPriceEvent(companyName, firstName, lastName, email, phone, form.getModalQuotationQuantity(),
                                                                   form.getModalQuotationMessage(), productModel);
        }

        getDistProductPriceQuotationFacade().sendProductPriceQuotation(quoteProductPrieEvent);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = CATALOG_PLUS_PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/quoteProductPrice")
    public ResponseEntity catalogPlusRequestProductPrice(@PathVariable("productCode") final String productCode, @Valid final QuoteProductPriceForm form,
                                                         final BindingResult bindingResults) {
        if (bindingResults.hasErrors()) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        final ProductModel productModel = getProductService().getProductForCode(productCode);

        if (productModel.getCatPlusSupplierAID() != null) {
            // CatalogPlus items are not available for guests and B2C customers
            if (getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            }
        }

        final DistCatalogPlusPriceRequestEvent catplusProductPriceRequestEvent = new DistCatalogPlusPriceRequestEvent(form.getCompany(), form.getFirstName(),
                                                                                                                      form.getLastName(), form.getEmail(),
                                                                                                                      form.getPhone(),
                                                                                                                      form.getModalQuotationQuantity(),
                                                                                                                      form.getModalQuotationMessage(),
                                                                                                                      Arrays.asList(productModel));
        getDistProductPriceQuotationFacade().sendCatalogPlusProductPriceRequest(catplusProductPriceRequestEvent);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = MULTIPLE_PRODUCT_ALTERNATIVE_URL, produces = "application/json")
    public String getMPNProductAlternatives(@PathVariable("productCode") final String productCode,
                                            @RequestParam(value = "offset", required = true, defaultValue = "0") final int offset,
                                            @RequestParam(value = "size", required = false, defaultValue = "10") final int size, final Model model) {
        model.addAttribute("products", getProductFacade().getMultipleProductAlternatives(productCode, offset, size));
        return ControllerConstants.Views.Fragments.Product.AlternateProducts;
    }

    @ExceptionHandler({ UnknownIdentifierException.class, CatalogPlusItemAccessException.class, PunchoutException.class })
    public String handleUnknownIdentifierException(final SystemException exception, final HttpServletRequest request) {
        final String uuidString = java.util.UUID.randomUUID().toString();
        if (ERROR_PAGE_LOG.isDebugEnabled()) {
            ERROR_PAGE_LOG.debug("a technical error occured [uuid: " + uuidString + "], IP Address: " + request.getRemoteAddr() + ". " + exception.getMessage(),
                                 exception);
        }
        request.setAttribute("uuid", uuidString);
        request.setAttribute("exception", exception);

        if (exception instanceof CatalogPlusItemAccessException) {
            return FORWARD_PREFIX + "/catplusAccessDenied";
        }

        return FORWARD_PREFIX + "/notFound";
    }

    protected void populateProductDetailForDisplay(final ProductModel productModel, final FactFinderPaginationData pagination, final Model model,
                                                   final HttpServletRequest request, final String filterURL, final boolean phaseoutprodflag,
                                                   final DistSalesStatusModel productSalesStatusModel)
                                                                                                       throws CMSItemNotFoundException {
        final ProductData productData;
        final String productsalesorgstatus = productSalesStatusModel.getCode();

        getRequestContextData(request).setProduct(productModel);

        if (productModel.getCatPlusSupplierAID() != null) {
            productData = getProductFacade().getProductForCodeAndOptions(productModel,
                                                                         Arrays.asList(ProductOption.BASIC, ProductOption.PRICE,
                                                                                       ProductOption.SUMMARY, ProductOption.DESCRIPTION,
                                                                                       ProductOption.GALLERY,
                                                                                       ProductOption.VOLUME_PRICES,
                                                                                       ProductOption.PROMOTION_LABELS,
                                                                                       ProductOption.COUNTRY_OF_ORIGIN,
                                                                                       ProductOption.DIST_MANUFACTURER,
                                                                                       ProductOption.PRODUCT_INFORMATION));
        } else {
            productData = getProductFacade().getProductForCodeAndOptions(productModel,
                                                                         Arrays.asList(ProductOption.BASIC,
                                                                                       ProductOption.ONLINE_PRICE /* ProductOption.PRICE */,
                                                                                       ProductOption.SUMMARY, ProductOption.DESCRIPTION,
                                                                                       ProductOption.GALLERY, ProductOption.CATEGORIES,
                                                                                       ProductOption.REVIEW, ProductOption.PROMOTIONS,
                                                                                       /* ProductOption.CLASSIFICATION, */ProductOption.VARIANT_FULL,
                                                                                       ProductOption.STOCK, ProductOption.VOLUME_PRICES,
                                                                                       ProductOption.PROMOTION_LABELS,
                                                                                       ProductOption.COUNTRY_OF_ORIGIN,
                                                                                       ProductOption.DIST_MANUFACTURER,
                                                                                       ProductOption.AUDIOS,
                                                                                       ProductOption.VIDEOS,
                                                                                       /* ProductOption.PRODUCT_INFORMATION, */ProductOption.IMAGE360,
                                                                                       ProductOption.CLASSIFICATION_AND_PRODUCT_INFORMATION));
        }
        model.addAttribute("isROHSValidForCountry", distComplianceFacade.isROHSAllowedForProduct(productData));
        model.addAttribute("isROHSComplaint", distComplianceFacade.isROHSCompliant(productData));
        model.addAttribute("isROHSConform", distComplianceFacade.isROHSConform(productData));
        model.addAttribute("isRNDProduct", distComplianceFacade.isRNDProduct(productData));
        model.addAttribute("isProductBatteryCompliant", distComplianceFacade.isProductBatteryCompliant(productData));

        CMSSiteModel site = cmsSiteService.getCurrentSite();
        model.addAttribute("isReevooActivatedForWebshop", site != null ? site.isReevooActivated() : false);
        if (site != null) {
            model.addAttribute("isReevooBrandVisible", site.isReevooBrandVisible());
            Map<String, String> languageTrkRefMapping = site.getReevootrkref();
            if (languageTrkRefMapping.keySet() != null && languageTrkRefMapping.keySet().size() > 0) {
                final String currentLang = getI18nService().getCurrentLocale().getLanguage();
                String trkRef = currentLang != null ? languageTrkRefMapping.get(currentLang)
                                                    : languageTrkRefMapping.get(languageTrkRefMapping.keySet().toArray()[0]);
                model.addAttribute(DistConstants.PropKey.Reevoo.TRKREF_ID, trkRef);
            } else {
                model.addAttribute(DistConstants.PropKey.Reevoo.TRKREF_ID, null);
            }
        }
        addPagination(productData, pagination);
        storeCmsPageInModel(model, getPageForProduct(productModel));
        setWebtrekkProperties(model, productModel);
        populateProductData(productData, model, request, productsalesorgstatus);
        populateProductTitle(productData, model);
        populateOpenGraphInformation(productModel, productData, model);
        populateWarningMessageforDangerousGoodsExportShop(productModel, model);
        addFlagForPhaseOutProduct(productData, phaseoutprodflag);

        // Add details for out Of Stock product notification display
        outOfStockNotification(model, productData);

        // Populate DTM Elements
        if (null != productData && isDatalayerEnabled()) {
            final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
            try {
                final List<Product> dtmProducts = new ArrayList<>();
                dtmProducts.add(populateProductDTMObjects(productData));
                digitalDatalayer.setProduct(dtmProducts);
                digitalDatalayer.setEventName(DigitalDatalayer.EventName.PRODUCT_DETAIL);
                model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
            } catch (final Exception ex) {
                LOG.error("Exception while populating DTM product attributes in search page" + ex);
            }
        }
        // DISTRELEC-6690
        model.addAttribute("hasDownloads", Boolean.valueOf(((DistProductService) getProductService()).hasDownloads(productModel.getCode())));
        model.addAttribute("hasAccessories", Boolean.valueOf(((DistProductService) getProductService()).hasAccessories(productModel.getCode())));

        String customFilter = request.getHeader(HttpHeaders.REFERER);
        // Testing if the referer comes from the same host.
        if (customFilter != null && !StringUtils.equals(StringUtils.split(customFilter, "/")[1], request.getHeader(HttpHeaders.HOST))) {
            customFilter = null;
        }
        if (filterURL != null) {
            customFilter = new String(Base64.decodeBase64(filterURL));
        }
        if (customFilter != null && StringUtils.contains(customFilter, "?q=")) {
            model.addAttribute(FILTER_URL, Base64.encodeBase64URLSafeString(customFilter.getBytes()));
        }

        if (StringUtils.isNotEmpty(productModel.getCatPlusSupplierAID())) {
            model.addAttribute(WebConstants.BREADCRUMBS_KEY, getCatalogPlusBreadcrumbBuilder().getBreadcrumbs(productModel));
        } else {
            model.addAttribute(WebConstants.BREADCRUMBS_KEY, getProductBreadcrumbBuilder().getBreadcrumbs(productModel, customFilter, request));
        }

        if (!getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
            boolean isBuyable = productSalesStatusModel.isBuyableInShop();
            model.addAttribute("allowBulk", isBuyable && hasB2BQuotationRequestPermission());
        }
    }

    private boolean hasB2BQuotationRequestPermission() {
        final CustomerData customer = getB2bCustomerFacade().getCurrentCustomer();
        List<B2BPermissionData> permissions = customer.getPermissions();
        return CollectionUtils.isNotEmpty(permissions) && permissions.stream(). //
                                                                     filter(permission -> permission.isActive() && permission.getB2BPermissionTypeData() != null
                                                                             && B2BPermissionTypeEnum.DISTB2BREQUESTQUOTATIONPERMISSION.getCode()
                                                                                                                                       .equals(permission.getB2BPermissionTypeData()
                                                                                                                                                         .getCode())) //
                                                                     .findAny()
                                                                     .isPresent();
    }

    private void populateWarningMessageforDangerousGoodsExportShop(final ProductModel productModel, final Model model) {
        if (isCurrentShopElfa() && getProductFacade().isDangerousProduct(productModel.getCode())) {
            model.addAttribute("isDangerousGoods", Boolean.TRUE);
            model.addAttribute("dangerousgoods.warning.message", "dangerousgoods.warning.message");
        }
    }

    protected void populateOpenGraphInformation(final ProductModel productModel, final ProductData productData, final Model model) {
        final CMSSiteModel cmsSiteModel = getCmsSiteService().getCurrentSite();

        model.addAttribute("ogProductImage", getPrimaryImageUrl(productData, cmsSiteModel));

        final String sharePageURL = getUrlForCMSSiteModel(productModel, cmsSiteModel);
        model.addAttribute("sharePageUrl", sharePageURL);
        model.addAttribute("seoCanonicalLink", getProductUrlResolver(productModel).resolve(productModel));
    }

    private String getUrlForCMSSiteModel(final ProductModel productModel, final CMSSiteModel cmsSiteModel) {
        final String domain = getConfigurationService().getConfiguration().getString("website." + cmsSiteModel.getUid() + ".http");
        final String url = domain + getProductUrlResolver(productModel).resolve(productModel);
        return url;

    }

    protected void populateProductData(final ProductData productData, final Model model, final HttpServletRequest request, final String productsalesorgstatus) {
        ProductDataHelper.setCurrentProduct(request, productData.getCode());
        model.addAttribute("galleryImages", getGalleryImages(productData));
        model.addAttribute(PRODUCT_ATTRIBUTE_NAME, productData);

        model.addAttribute(ThirdPartyConstants.Webtrekk.WT_PRODUCT_COST, productData.getPrice() == null ? "0.0" : productData.getPrice().getValue());
        model.addAttribute(ThirdPartyConstants.Webtrekk.WT_PRODUCT_STATUS, "view");
        if (MapUtils.isNotEmpty(productData.getProductReferencesMap())) {
            // add teaser tracking for referenced products (is always -ons- and
            // must not be declared for every product)
            getDistWebtrekkFacade().addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_ONS);
        }
        final DistManufacturerData manufacturer = productData.getDistManufacturer();
        model.addAttribute(ThirdPartyConstants.Webtrekk.WT_PRODUCT_CATEGORY,
                           getDistWebtrekkFacade().encodeToUTF8(manufacturer != null ? manufacturer.getName() : ""));
        model.addAttribute(ThirdPartyConstants.Webtrekk.WT_PRODUCT,
                           getDistWebtrekkFacade().encodeToUTF8(productData.getCodeErpRelevant() + " / " + productData.getName()));
        productData.setSalesStatus(productsalesorgstatus);
    }

    protected void populateProductTitle(final ProductData productData, final Model model) {
        final String productTitle;
        final String productName = productData.getName();
        final String mpn = productData.getTypeName();
        final String manufacturerName = productData.getDistManufacturer() != null ? productData.getDistManufacturer().getName() : StringUtils.EMPTY;
        productTitle = populateProductTitle(productName, mpn, manufacturerName);
        model.addAttribute("productTitle", productTitle);
    }

    protected String populateProductTitle(final String productName, final String mpn, final String manufacturerName) {
        final StringBuilder result = new StringBuilder();
        if (StringUtils.isNotBlank(mpn) && !StringUtils.containsIgnoreCase(productName, mpn)) {
            result.append(mpn).append(PRODUCT_TITLE_SEPARATOR_AFTER_MPN);
        }
        result.append(productName);
        if (StringUtils.isNotBlank(manufacturerName) && !StringUtils.containsIgnoreCase(productName, manufacturerName)) {
            result.append(PRODUCT_TITLE_SEPARATOR_BEFORE_MANUFACTURER).append(manufacturerName);
        }
        return result.toString();
    }

    protected void addPagination(final ProductData productData, final FactFinderPaginationData pagination) {
        if (pagination != null) {
            final boolean prevnextbtn = getConfigurationService().getConfiguration().getBoolean("distrelec.productdetailed.page.prevnext.button.enabled",
                                                                                                false);
            if (prevnextbtn) {
                productData.setNextUrl(pagination.getNextUrl());
                productData.setPrevUrl(pagination.getPrevUrl());
            }
        }
    }

    protected AbstractPageModel getPageForProduct(final ProductModel product) throws CMSItemNotFoundException {
        return getCmsPageService().getPageForProduct(product);
    }

    protected String getPrimaryImageUrl(final ProductData productData, final CMSSiteModel cmsSiteModel) {
        if (CollectionUtils.isNotEmpty(productData.getProductImages())) {
            return getPortraitMediumUrl(productData.getProductImages().iterator().next(), cmsSiteModel);
        }
        return null;
    }

    protected List<Map<String, ImageData>> getGalleryImages(final ProductData productData) {
        final List<Map<String, ImageData>> galleryImages = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(productData.getImages())) {
            final List<ImageData> images = new ArrayList<>();
            for (final ImageData image : productData.getImages()) {
                if (ImageDataType.GALLERY.equals(image.getImageType())) {
                    images.add(image);
                }
            }
            Collections.sort(images, Comparator.comparing(ImageData::getGalleryIndex));

            if (CollectionUtils.isNotEmpty(images)) {
                int currentIndex = images.get(0).getGalleryIndex();
                Map<String, ImageData> formats = new HashMap<>();
                for (final ImageData image : images) {
                    if (currentIndex != image.getGalleryIndex()) {
                        galleryImages.add(formats);
                        formats = new HashMap<>();
                        currentIndex = image.getGalleryIndex();
                    }
                    formats.put(image.getFormat(), image);
                }
                if (!formats.isEmpty()) {
                    galleryImages.add(formats);
                }
            }
        }
        return galleryImages;
    }

    protected void addToLastViewedCookie(final HttpServletRequest request, final HttpServletResponse response, final String productCode) {
        String lastViewedProducts = Attributes.LAST_VIEWED.getValueFromCookies(request);
        final String shopSettingsString = Attributes.SHOP_SETTINGS.getValueFromCookies(request);

        final ShoppingSettingsCookieData cookieData = ShopSettingsUtil.readShopSettingsCookie(shopSettingsString);
        String isPrefEnabled = Attributes.FF_ENSIGTHEN_PREFERENCES_COOKIE.getValueFromCookies(request);
        final String[] consentCheckCountries = getConfigurationService().getConfiguration().getString("skip.cookie.consent.check.country")
                                                                        .split(",");
        List<String> baseSiteList = (null != consentCheckCountries && consentCheckCountries.length > 0) ? Arrays.asList(consentCheckCountries)
                                                                                                        : Collections.EMPTY_LIST;
        if (null != cookieData && StringUtils.isNotEmpty(cookieData.getCountry()) && CollectionUtils.isNotEmpty(baseSiteList)
                && baseSiteList.contains(cookieData.getCountry())) {
            isPrefEnabled = "1";
        }
        if (StringUtils.isNotEmpty(isPrefEnabled) && isPrefEnabled.equalsIgnoreCase("1")) {
            final int maxCookieSize = getConfigurationService().getConfiguration().getInt(DistConstants.PropKey.Cookie.LAST_VIEWED_LIST_SIZE,
                                                                                          COOKIE_LISTSIZE_DEFAULT_VALUE);
            if (lastViewedProducts == null) {
                lastViewedProducts = productCode;
            } else {
                String[] productCodes = StringUtils.split(lastViewedProducts, ":");
                if (ArrayUtils.contains(productCodes, productCode)) {
                    // remove before insert at first position again
                    productCodes = (String[]) ArrayUtils.removeElement(productCodes, productCode);
                }
                productCodes = (String[]) ArrayUtils.add(productCodes, 0, productCode);
                if (maxCookieSize > 0 && productCodes.length > maxCookieSize) {
                    productCodes = (String[]) ArrayUtils.subarray(productCodes, 0, maxCookieSize);
                }
                lastViewedProducts = StringUtils.join(productCodes, ":");
            }

            Attributes.LAST_VIEWED.setValue(request, response, lastViewedProducts);
        } else {
            Attributes.LAST_VIEWED.removeValue(response);
        }
    }

    protected void populateEOLReplacementInformation(final Model model, final String eolReplacementFor, final String eolReplacedBy,
                                                     final boolean eolShowProduct, final ProductModel sourceProductModel) {

        ProductModel productReplacement = null;
        if (StringUtils.isNotBlank(eolReplacementFor)) {
            // the original eol product is specified in the replacementFor
            // paramenter
            final ProductModel originalEOLProduct = getProductService().getProductForCode(eolReplacementFor);
            // the current product is the replacement
            productReplacement = sourceProductModel;

            if ((productReplacement != null) && shouldDisplayWarningMessage(getProductFacade().getProductSalesStatus(eolReplacementFor))) {
                populateMessageForProductReplacementFor(model, originalEOLProduct, productReplacement);
            }
        } else if (StringUtils.isNotBlank(eolReplacedBy)) {
            productReplacement = getProductService().getProductForCode(eolReplacedBy);
            if ((productReplacement != null) && shouldDisplayWarningMessage(getProductFacade().getProductSalesStatus(sourceProductModel.getCode()))) {
                populateMessageForProductReplacedBy(model, sourceProductModel, productReplacement);
            }
        } else if (eolShowProduct) {
            final CategoryModel parentCategory = CollectionUtils.isNotEmpty(sourceProductModel.getSupercategories())
                                                                                                                     ? sourceProductModel.getSupercategories()
                                                                                                                                         .iterator().next()
                                                                                                                     : null;
            populateMessageForDisplayProductEOL(model, sourceProductModel, parentCategory);
        }
    }

    /**
     * Return the product URL resolver for the specified product
     *
     * @param productModel
     * @return the product URL resolver.
     */
    public DistUrlResolver<ProductModel> getProductUrlResolver(final ProductModel productModel) {
        final DistUrlResolver<ProductModel> resolver = productModel.getCatPlusSupplierAID() == null ? getProductModelUrlResolver()
                                                                                                    : getCatalogPlusProductModelUrlResolver();
        return resolver;
    }

    private void populateMessageForDisplayProductEOL(final Model model, final ProductModel productModel, final CategoryModel parentCategoryModel) {
        if (parentCategoryModel == null) {
            model.addAttribute(EOL_MESSAGE, getMessageSource().getMessage("product.eol.attention.displayEolWithoutCategory", //
                                                                          new Object[] { productModel.getName(), productModel.getCodeErpRelevant() }, "",
                                                                          getI18nService().getCurrentLocale()));
        } else {
            model.addAttribute(EOL_MESSAGE, getMessageSource().getMessage("product.eol.attention.displayEolWithCategory", new Object[] { productModel.getName(), //
                                                                                                                                         productModel.getCodeErpRelevant(), //
                                                                                                                                         getCategoryModelUrlResolver().resolve(parentCategoryModel), //
                                                                                                                                         parentCategoryModel.getName() },
                                                                          "", getI18nService().getCurrentLocale()));

        }
    }

    private boolean shouldDisplayWarningMessage(final String productSalesStatus) {
        return !productSalesStatus.equals("30"); // DISTRELEC-6995
    }

    private void populateMessageForProductReplacementFor(final Model model, final ProductModel source, final ProductModel replacedProduct) {
        model.addAttribute(EOL_MESSAGE, getMessageSource().getMessage("product.eol.attention.replacementFor",
                                                                      new Object[] { source.getName(), source.getCodeErpRelevant(),
                                                                                     replacedProduct.getCodeErpRelevant() },
                                                                      "", getI18nService().getCurrentLocale()));
    }

    private void populateMessageForProductReplacedBy(final Model model, final ProductModel sourceProductModel, final ProductModel replacedByProduct) {
        model.addAttribute(EOL_MESSAGE,
                           getMessageSource().getMessage(
                                                         "product.eol.attention.replacedBy",
                                                         new Object[] { sourceProductModel.getName(), sourceProductModel.getCodeErpRelevant(),
                                                                        replacedByProduct.getName(), getProductModelUrlResolver().resolve(replacedByProduct) },
                                                         "", getI18nService().getCurrentLocale()));
    }

    protected void populateSimilarProductBaseUrl(final ProductModel productModel, final Model model) {
        final CategoryModel similarProductBaseCategory = getSimilarProductBaseCategory(productModel);

        String url = null;
        if (similarProductBaseCategory != null) {
            url = getCategoryModelUrlResolver().resolve(similarProductBaseCategory);
            url = url + "?useTechnicalView=true";
        } else {
            url = "/search?q=*";
        }

        model.addAttribute("similarProductBaseUrl", url);
    }

    protected CategoryModel getSimilarProductBaseCategory(final ProductModel productModel) {
        Collection<CategoryModel> superCategories = getCommerceProductService().getSuperCategoriesExceptClassificationClassesForProduct(productModel);
        while (CollectionUtils.isNotEmpty(superCategories)) {
            final CategoryModel superCategory = superCategories.iterator().next();

            if (superCategory == null) {
                return null;
            } else {
                if (superCategory.getLevel() != null && superCategory.getLevel().intValue() == 2) {
                    return superCategory;
                }
                // if (superCategory.getLevel() == null ||
                // SIMILAR_PRODUCT_BASE_CATEGORY_LEVEL.compareTo(superCategory.getLevel())
                // >= 0) {
                // return superCategory;
                // }
            }

            superCategories = getDistCategoryService().getProductSuperCategories(superCategory);
        }

        return null;
    }

    private ProductData getProductWithOptions(final String productCode, final List<? extends ProductOption> productOption) {
        return getProductFacade().getProductForOptions(productCode, productOption);
    }

    private void setWebtrekkProperties(final Model model, final ProductModel productModel) throws CMSItemNotFoundException {
        // add productCode to webtrekk page id
        model.addAttribute(ThirdPartyConstants.Webtrekk.WT_PAGE_ID, getPageForProduct(productModel).getUid() + "-" + productModel.getCode());

        // get category Webtrekk category area code
        String catWtAreaCode = "";

        final Iterator<CategoryModel> categories = getCommerceProductService().getSuperCategoriesExceptClassificationClassesForProduct(productModel).iterator();
        while (categories.hasNext()) {
            final CategoryModel category = categories.next();
            if (!StringUtils.isEmpty(category.getWtAreaCode())) {
                catWtAreaCode = category.getWtAreaCode();
                break;
            }
            final Iterator<CategoryModel> supercategories = getDistCategoryService().getAllSupercategoriesForCategory(category).iterator();
            while (supercategories.hasNext()) {
                final CategoryModel superCategory = supercategories.next();
                if (!StringUtils.isBlank(superCategory.getWtAreaCode())) {
                    catWtAreaCode = superCategory.getWtAreaCode();
                    break;
                }
            }
        }

        model.addAttribute(ThirdPartyConstants.Webtrekk.WT_CAT_AREA_CODE, catWtAreaCode);
    }

    private void populateMessageForPhaseoutProduct(final Model model, final ProductModel sourceProductModel) {
        if (hasValidReplacement(sourceProductModel)) {
            final ProductModel replacementProduct = sourceProductModel.getReplacementProduct();
            final ProductData productDataBasicPopulated = getProductFacade().getProductForCodeAndOptions(replacementProduct, PRODUCT_OPTIONS);
            if (getProductFacade().getRelevantSalesUnit(productDataBasicPopulated.getCode()) != null) {
                productDataBasicPopulated.setSalesUnit(getProductFacade().getRelevantSalesUnit(replacementProduct.getCode()));
            }

            productDataBasicPopulated.setUrl(getProductUrlResolver(replacementProduct).resolve(replacementProduct));
            model.addAttribute(REPLACEMENT_PRODUCT_LIST, Arrays.asList(productDataBasicPopulated));
        } else if (CollectionUtils.isNotEmpty(sourceProductModel.getSupercategories())) {
            model.addAttribute(REPLACEMENT_CATEGORY_LINK, getCategoryModelUrlResolver().resolve(sourceProductModel.getSupercategories().iterator().next()));
        }
    }

    protected void addFlagForPhaseOutProduct(final ProductData productData, final boolean phaseoutprodflag) {
        productData.setBuyablePhaseoutProduct(phaseoutprodflag);
    }

    private void setFreeShippingValue(final Model model) {
        final double minFreeFreight = getConfigurationService().getConfiguration()
                                                               .getDouble("cart.free.freight.min.value." + getCmsSiteService().getCurrentSite().getUid(), 0.0);
        model.addAttribute("freeShippingValue", cartFacade.getFreeShippingValue(BigDecimal.valueOf(minFreeFreight), getWtCurrency()));
    }

    private void outOfStockNotification(final Model model, final ProductData productData) {

        // Add Stock notification For, to display the out of stock notification
        boolean expiredProduct = Boolean.FALSE;
        if (getProductFacade().isEndOfLife(productData.getCode())) {
            expiredProduct = Boolean.TRUE;
        }
        model.addAttribute("expiredProduct", expiredProduct);
    }

    public DistUrlResolver<ProductModel> getProductModelUrlResolver() {
        return productModelUrlResolver;
    }

    public void setProductModelUrlResolver(final DistUrlResolver<ProductModel> productModelUrlResolver) {
        this.productModelUrlResolver = productModelUrlResolver;
    }

    public DistUrlResolver<ProductModel> getCatalogPlusProductModelUrlResolver() {
        return catalogPlusProductModelUrlResolver;
    }

    public void setCatalogPlusProductModelUrlResolver(final DistUrlResolver<ProductModel> catalogPlusProductModelUrlResolver) {
        this.catalogPlusProductModelUrlResolver = catalogPlusProductModelUrlResolver;
    }

    public DistUrlResolver<CategoryModel> getCategoryModelUrlResolver() {
        return categoryModelUrlResolver;
    }

    public void setCategoryModelUrlResolver(final DistUrlResolver<CategoryModel> categoryModelUrlResolver) {
        this.categoryModelUrlResolver = categoryModelUrlResolver;
    }

    public CommerceProductService getCommerceProductService() {
        return commerceProductService;
    }

    public void setCommerceProductService(final CommerceProductService commerceProductService) {
        this.commerceProductService = commerceProductService;
    }

    public DistCategoryService getDistCategoryService() {
        return distCategoryService;
    }

    public void setDistCategoryService(final DistCategoryService distCategoryService) {
        this.distCategoryService = distCategoryService;
    }

    public ProductSearchFacade<ProductData> getProductSearchFacade() {
        return productSearchFacade;
    }

    public void setProductSearchFacade(final ProductSearchFacade<ProductData> productSearchFacade) {
        this.productSearchFacade = productSearchFacade;
    }

    public DistShareWithFriendsFacade getDistShareWithFriendsFacade() {
        return distShareWithFriendsFacade;
    }

    public void setDistShareWithFriendsFacade(final DistShareWithFriendsFacade distShareWithFriendsFacade) {
        this.distShareWithFriendsFacade = distShareWithFriendsFacade;
    }

    @Override
    public DistProductPriceQuotationFacade getDistProductPriceQuotationFacade() {
        return distProductPriceQuotationFacade;
    }

    @Override
    public void setDistProductPriceQuotationFacade(final DistProductPriceQuotationFacade distProductPriceQuotationFacade) {
        this.distProductPriceQuotationFacade = distProductPriceQuotationFacade;
    }

    public ProductBreadcrumbBuilder getProductBreadcrumbBuilder() {
        return productBreadcrumbBuilder;
    }

    public void setProductBreadcrumbBuilder(final ProductBreadcrumbBuilder productBreadcrumbBuilder) {
        this.productBreadcrumbBuilder = productBreadcrumbBuilder;
    }

    public CatalogPlusBreadcrumbBuilder getCatalogPlusBreadcrumbBuilder() {
        return catalogPlusBreadcrumbBuilder;
    }

    public void setCatalogPlusBreadcrumbBuilder(final CatalogPlusBreadcrumbBuilder catalogPlusBreadcrumbBuilder) {
        this.catalogPlusBreadcrumbBuilder = catalogPlusBreadcrumbBuilder;
    }

    public DistRecommendationFacade<ProductData, FactFinderFeedbackTextData> getRecommendationFacade() {
        return recommendationFacade;
    }

    public void setRecommendationFacade(final DistRecommendationFacade<ProductData, FactFinderFeedbackTextData> recommendationFacade) {
        this.recommendationFacade = recommendationFacade;
    }

    public DistCartFacade getCartFacade() {
        return cartFacade;
    }

    public void setCartFacade(final DistCartFacade cartFacade) {
        this.cartFacade = cartFacade;
    }
}
