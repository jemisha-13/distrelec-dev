/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.pages.DistManufacturerPageModel;
import com.namics.distrelec.b2b.core.service.evaluator.impl.DistManufacturerRestrictionEvaluator;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.manufacturer.DistManufacturerFacade;
import com.namics.distrelec.b2b.facades.manufacturer.converters.DistManufacturerConverter;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerData;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistMiniManufacturerData;
import com.namics.distrelec.b2b.facades.message.queue.data.RelatedData;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import com.namics.distrelec.b2b.facades.search.converter.FactFinderLazyFacetConverter;
import com.namics.distrelec.b2b.facades.seo.data.MetaData;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.ManufacturerBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.seo.DistLink;
import com.namics.distrelec.b2b.storefront.support.PageTitleResolver;
import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;
import com.namics.hybris.ffsearch.breadcrumb.SearchBreadcrumbBuilder;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * Controller for manufacturer stores page.
 */
@Controller
public class ManufacturerStoresPageController extends AbstractStoresPageController {

    protected static final Logger LOG = LogManager.getLogger(ManufacturerStoresPageController.class);

    private static final String MANUFACTURER_STORES_CMS_PAGE = "manufacturerStoresPage";

    private static final String DIGITAL_DATA_LAYER_TERM = "digitalDataLayerTerm";

    @Autowired
    private DistManufacturerService distManufacturerService;

    @Autowired
    private DistManufacturerFacade distManufacturerFacade;

    @Autowired
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Autowired
    private ManufacturerBreadcrumbBuilder manufacturerBreadcrumbBuilder;

    @Autowired
    private DistManufacturerConverter distManufacturerConverter;

    @Autowired
    private PageTitleResolver pageTitleResolver;

    @Autowired
    @Qualifier("distManufacturerModelUrlResolver")
    private DistUrlResolver<DistManufacturerModel> distManufacturerModelUrlResolver;

    @Autowired
    @Qualifier("contentPageUrlResolver")
    private DistUrlResolver<ContentPageModel> contentPageUrlResolver;

    @Autowired
    private DistCategoryFacade distCategoryFacade;

    @Autowired
    private SearchBreadcrumbBuilder searchBreadcrumbBuilder;

    @GetMapping(value = "/manufacturers", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<DistMiniManufacturerData> getManufacturers() {
        List<DistMiniManufacturerData> manufacturers = new ArrayList<>();
        Map<String, List<DistMiniManufacturerData>> groupedManufacturers = distManufacturerFacade.getManufactures();
        groupedManufacturers.forEach((group, manufacturerList) -> manufacturers.addAll(manufacturerList));
        return manufacturers;
    }

    @GetMapping(value = "/**/manufacturer")
    public String manufacturerStores(final Model model, final HttpServletRequest request, final HttpServletResponse response)
                                                                                                                              throws CMSItemNotFoundException,
                                                                                                                              UnsupportedEncodingException {
        addGlobalModelAttributes(model, request);
        model.addAttribute("manufacturers", distManufacturerFacade.getManufactures());
        // /{language}/{page-title}/cms/{page-label}
        final ContentPageModel contentPage = getContentPageForLabelOrId(getStoreCmsPageName());
        final String resolvedURL = getContentPageUrlResolver().resolve(contentPage);
        final String redirection = checkRequestUrl(request, response, resolvedURL);
        if (StringUtils.isNotBlank(redirection)) {
            return redirection;
        }

        storeCmsPageInModel(model, contentPage);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, getBreadcrumbs(contentPage.getTitle(), resolvedURL));
        return getStoreView();
    }

    @GetMapping(value = "/**/manufacturer/**/{manufacturerCode:.*}")
    public String manufacturerStoreDetails(@PathVariable("manufacturerCode") final String manufacturerCode,
                                           @RequestParam(value = SEARCH_QUERY_PARAMETER_NAME, required = false) final String searchQuery,
                                           @RequestParam(value = PAGE_PARAMETER_NAME, defaultValue = "1") final int page,
                                           @RequestParam(value = "pageSize", defaultValue = "0") final int pageSize,
                                           @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
                                           @RequestParam(value = "sort", required = false) final String sortCode,
                                           @RequestParam(value = "requestType", required = false) final String requestType,
                                           @RequestParam(value = "useTechnicalView", required = false) final Boolean useTechnicalView,
                                           final Model model, final HttpServletRequest request, final HttpServletResponse response,
                                           @RequestParam(required = false) String digitalDataLayerTerm, RedirectAttributes redirectAttributes)
                                                                                                                                               throws CMSItemNotFoundException,
                                                                                                                                               UnsupportedEncodingException {

        if (distManufacturerFacade.isManufacturerExcluded(manufacturerCode) && getProductFacade().enablePunchoutFilterLogic()) {
            redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, "manufacturer.product.error.punchout");
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + ROOT);
        }

        switchTechnicalViewIfNeeded(useTechnicalView, model, request, response);

        if (digitalDataLayerTerm != null) {
            model.addAttribute(DIGITAL_DATA_LAYER_TERM, digitalDataLayerTerm);
        }

        final DistManufacturerModel manufacturerModel = distManufacturerService.getManufacturerByCode(getCleanManufacturerCode(manufacturerCode));
        final DistManufacturerData manufacturer = distManufacturerConverter.convert(manufacturerModel);
        final String manufacturerUrl = distManufacturerModelUrlResolver.resolve(manufacturerModel);
        final String redirection = checkRequestUrl(request, response, manufacturerUrl, true);
        if (StringUtils.isNotEmpty(redirection)) {
            return redirection;
        }

        // Resolve the parent URL
        final String parentURL = contentPageUrlResolver.resolve(getContentPageForLabelOrId(getStoreCmsPageName()));
        final SearchStateData state = createSearchStateData(request, searchQuery);
        final int pageSizeValue = calculatePageSize(pageSize, request, response);
        final PageableData paging = createSearchPageableDataWithCookie(request, response, page, pageSizeValue, sortCode, showMode,
                                                                       BooleanUtils.isTrue(useTechnicalView));

        // check if its comes from suggest
        boolean queryFromSuggest = false;
        if (request.getParameter(DistConstants.FactFinder.FF_TRACKING) != null) {
            queryFromSuggest = Boolean.valueOf(request.getParameter(DistConstants.FactFinder.FF_TRACKING)).booleanValue();
        }

        final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData = getSearchPageData(queryFromSuggest, manufacturerModel, state,
                                                                                                               paging);
        final List<ProductData> productDataList = searchPageData.getResults();
        if (CollectionUtils.isEmpty(productDataList)) {
            // Resolve the URL of the manufacturer store page and redirect to it.
            return checkRequestUrl(request, response, parentURL, true);
        }

        populateTabularAttributeSortingMap(model, searchPageData.getSortableAttributeMap());
        // DISTRELEC-9134: SRP
        createCategoryDisplayData(model, searchPageData, distCategoryFacade);

        // add type specific attributes
        model.addAttribute(DistManufacturerRestrictionEvaluator.MANUFACTURER, manufacturer);

        final DistManufacturerPageModel pageModel = getCmsPageService().getPageForManufacturer(manufacturerModel);
        getSessionService().setAttribute("manufacture", manufacturerModel);

        final ContentPageModel contentPage = getContentPageForLabelOrId(getStoreCmsPageName());
        createManufacturerBreadcrumb(model, contentPage.getTitle(), parentURL, manufacturerUrl, manufacturer.getName(), searchPageData,
                                     getSearchBreadcrumbBuilder());

        // final ContentPageModel searchPage = getContentPageForLabelOrId(getStoreCmsPageName());
        // creatSearchBreadcrum(searchQuery, model, Boolean.FALSE, searchState, searchPageData, searchPage,
        // getSearchBreadcrumbBuilder());
        addGlobalModelAttributes(model, request);
        final List<DistLink> headerLinkLang = new ArrayList<>();
        setupAlternateHreflangLinks(request, headerLinkLang, manufacturerModel, getDistManufacturerModelUrlResolver());
        model.addAttribute("headerLinksLangTags", headerLinkLang);
        model.addAttribute("footerLinksLangTags", headerLinkLang);

        productSearchFacade.addFilterstrings(searchPageData);
        storeSearchResultToModel(model, searchPageData, showMode);
        storeCmsPageInModel(model, pageModel);

        // Setting the URLs of the previous and next pages
        final String searchURL = storeContinueUrl(request);
        updatePagination(searchURL, (FactFinderPaginationData) searchPageData.getPagination(), model, false);
        // Setting the remove filter URL
        setRemoveFilersURL(searchPageData, request);

        final String freeTextSearch = searchPageData.getFreeTextSearch();
        if (StringUtils.containsOnly(freeTextSearch, "*")) {
            searchPageData.setFreeTextSearch(StringUtils.chop(freeTextSearch));
        }

        model.addAttribute("searchPageData", searchPageData);

        // Populate Data Layer
        populateDataLayerForProducts(searchQuery, searchPageData, model, false);

        if (REQUEST_TYPE_AJAX.equals(requestType)) {
            // return search result as JSON
            if (searchPageData.getPagination() != null) {
                model.addAttribute("isLastPage", isLastPage(searchPageData, page));
            }
            return getAjaxView();
        }
        int lastProductNumber = searchPageData.getPagination().getCurrentPage() * searchPageData.getPagination().getPageSize();
        model.addAttribute("lastProductNumber",
                           isLastPage(searchPageData, page) ? (int) searchPageData.getPagination().getTotalNumberOfResults() : lastProductNumber);
        model.addAttribute("firstProductNumber", lastProductNumber - searchPageData.getPagination().getPageSize() + 1);

        model.addAttribute("cachingTimeCategoryLink", getDistCachingFacade().getCachingTimeCategoryLink());
        String seoMetaTitle = StringUtils.EMPTY;
        String seoMetaDescription = StringUtils.EMPTY;
        CMSSiteModel cmsSiteModel = getCmsSiteService().getCurrentSite();
        final String cmsSiteName = cmsSiteModel.getName(getI18nService().getCurrentLocale());
        model.addAttribute("ogSiteName", cmsSiteName);
        if (StringUtils.isNotEmpty(manufacturerModel.getSeoMetaTitle())) {
            seoMetaTitle = manufacturerModel.getSeoMetaTitle().replace("${siteName}", cmsSiteName);
            model.addAttribute("ogProductTitle", seoMetaTitle);
        }

        if (StringUtils.isNotEmpty(manufacturerModel.getSeoMetaDescription())) {
            seoMetaDescription = manufacturerModel.getSeoMetaDescription().replace("${siteName}", cmsSiteName);
            model.addAttribute("ogProductDescription", seoMetaDescription);
        }
        storeMetaKeywordsInModel(model, manufacturerModel, seoMetaDescription);
        storeContentPageTitleInModel(model, StringUtils.isNotEmpty(seoMetaTitle) ? seoMetaTitle
                                                                                 : getPageTitleResolver().resolveManufacturerPageTitle(manufacturerModel));

        // Populate the OG informations
        populateOpenGraphInformation(getPrimaryImageUrl(manufacturer, cmsSiteModel), cmsSiteModel, model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MANUFACTURERPLPPAGE);
        model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        return getViewForPage(pageModel);
    }

    public RelatedData getManufacturerRelatedData(DistManufacturerData manufacturer) {
        RelatedData manufacturerRelatedData = getDistRelatedDataFacade().findManufacturerRelatedData(manufacturer.getCode());
        LOG.debug("manufacturerRelatedData: {}", manufacturerRelatedData);
        return manufacturerRelatedData;
    }

    @GetMapping(value = "/**/manufacturercount/**/{manufacturerCode:.*}", produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String manufacturerStoreDetailsCount(@PathVariable("manufacturerCode") final String manufacturerCode,
                                                              @RequestParam(value = SEARCH_QUERY_PARAMETER_NAME, required = false) final String searchQuery,
                                                              @RequestParam(value = PAGE_PARAMETER_NAME, defaultValue = "1") final int page,
                                                              @RequestParam(value = "pageSize", defaultValue = "0") final int pageSize,
                                                              @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
                                                              @RequestParam(value = "sort", required = false) final String sortCode,
                                                              @RequestParam(value = "requestType", required = false) final String requestType,
                                                              @RequestParam(value = "useTechnicalView", required = false) final Boolean useTechnicalView,
                                                              final Model model, final HttpServletRequest request, final HttpServletResponse response)
                                                                                                                                                       throws CMSItemNotFoundException,
                                                                                                                                                       UnsupportedEncodingException {

        switchTechnicalViewIfNeeded(useTechnicalView, model, request, response);

        final DistManufacturerModel manufacturerModel = getDistManufacturerService().getManufacturerByCode(getCleanManufacturerCode(manufacturerCode));
        final DistManufacturerData manufacturer = getDistManufacturerConverter().convert(manufacturerModel);
        final String manufacturerUrl = getDistManufacturerModelUrlResolver().resolve(manufacturerModel);
        // Resolve the parent URL
        final String parentURL = getContentPageUrlResolver().resolve(getContentPageForLabelOrId(getStoreCmsPageName()));
        final SearchStateData state = createSearchStateData(request, searchQuery);
        final int pageSizeValue = calculatePageSize(pageSize, request, response);
        final PageableData paging = createSearchPageableDataWithCookie(request, response, page, pageSizeValue, sortCode, showMode,
                                                                       BooleanUtils.isTrue(useTechnicalView));

        // check if its comes from suggest
        boolean queryFromSuggest = false;
        if (request.getParameter(DistConstants.FactFinder.FF_TRACKING) != null) {
            queryFromSuggest = Boolean.valueOf(request.getParameter(DistConstants.FactFinder.FF_TRACKING)).booleanValue();
        }

        final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData = getSearchPageData(queryFromSuggest, manufacturerModel, state,
                                                                                                               paging);
        return String.valueOf(searchPageData.getPagination().getTotalNumberOfResults());
    }

    private boolean populateMetaData(final MetaData metaData, final Model model) {
        if (metaData == null) {
            return false;
        }
        setUpMetaData(model, metaData);
        return true;
    }

    @GetMapping(value = "/**/manufacturer/**/{manufacturerCode:.*}/showmore", produces = "application/json")
    public String showMoreProductsFromManufacturers(@PathVariable("manufacturerCode") final String manufacturerCode,
                                                    @RequestParam(value = SEARCH_QUERY_PARAMETER_NAME, required = false) final String searchQuery,
                                                    @RequestParam(value = PAGE_PARAMETER_NAME) final int page,
                                                    @RequestParam(value = "pageSize", defaultValue = "0") final int pageSize,
                                                    @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
                                                    @RequestParam(value = "sort", required = false) final String sortCode,
                                                    final Model model, final HttpServletRequest request, final HttpServletResponse response) {
        final DistManufacturerModel manufacturerModel = getDistManufacturerService().getManufacturerByCode(getCleanManufacturerCode(manufacturerCode));

        final SearchStateData state = createSearchStateData(request, searchQuery);
        final int pageSizeValue = calculatePageSize(pageSize, request, response);
        final PageableData paging = createPageableDataWithCookie(request, response, page, pageSizeValue, sortCode, showMode);
        final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData = getProducts(false, manufacturerModel.getCode(), state, paging,
                                                                                                         false);
        getProductSearchFacade().addFilterstrings(searchPageData);
        // Setting the URLs of the previous and next pages
        final String searchURL = storeContinueUrl(request);
        updatePagination(searchURL, (FactFinderPaginationData) searchPageData.getPagination(), model, false);
        // Setting the remove filter URL

        // Populate Data Layer
        populateDataLayerForProducts(searchQuery, searchPageData, model, true);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MANUFACTURERPLPPAGE);
        model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        setRemoveFilersURL(searchPageData, request);
        model.addAttribute("searchPageData", searchPageData);
        model.addAttribute("isLastPage", isLastPage(searchPageData, page));
        model.addAttribute("products", searchPageData.getResults());
        return getShowMoreView();
    }

    @GetMapping(value = "/**/manufacturer/**/{manufacturerCode:.*}" + FactFinderLazyFacetConverter.ADDITIONAL_FACET_PATH, produces = "application/json")
    public final String outletLoadAdditionalFacet(@PathVariable("manufacturerCode") final String manufacturerCode,
                                                  @RequestParam("q") final String searchQuery,
                                                  @RequestParam(value = FactFinderLazyFacetConverter.ADDITIONAL_FACET_PARAM_NAME) final String additionalFacet,
                                                  final HttpServletRequest request, final Model model) {

        final SearchStateData searchState = createSearchStateData(request, searchQuery);
        final PageableData paging = createPageableData(0, 0, null, null);

        final FactFinderFacetData<SearchStateData> additionalFacetData = getProductSearchFacade().loadAdditionalFacet(searchState, paging, additionalFacet,
                                                                                                                      null,
                                                                                                                      getCleanManufacturerCode(manufacturerCode));
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MANUFACTURERPLPPAGE);
        model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        model.addAttribute("facet", additionalFacetData);
        return getAdditionalFacetView();
    }

    protected String getCleanManufacturerCode(final String manufacturerCode) {
        return StringUtils.startsWith(manufacturerCode, "man_") ? manufacturerCode : "man_" + manufacturerCode;
    }

    protected FactFinderProductSearchPageData<SearchStateData, ProductData> getSearchPageData(final boolean tracking,
                                                                                              final DistManufacturerModel manufacturerModel,
                                                                                              final SearchStateData state, final PageableData paging) {
        return getProducts(tracking, manufacturerModel.getCode(), state, paging, true);
    }

    protected String getPrimaryImageUrl(final DistManufacturerData distManufacturerData, final CMSSiteModel cmsSiteModel) {
        if (distManufacturerData.getImage() != null && !distManufacturerData.getImage().isEmpty()) {
            return getPortraitMediumUrl(distManufacturerData.getImage(), cmsSiteModel);
        }
        return null;
    }

    protected void populateOpenGraphInformation(final String primaryImageUrl, final CMSSiteModel cmsSiteModel, final Model model,
                                                final HttpServletRequest request) {
        model.addAttribute("ogSiteName", cmsSiteModel.getName(getI18nService().getCurrentLocale()));
        model.addAttribute("ogProductImage", primaryImageUrl);
        model.addAttribute("sharePageUrl", request.getRequestURL().toString());
    }

    protected String getErrorMessageKey() {
        return "manufacturerStores.manufacturer.notFound";
    }

    /**
     * Override this method to avoid problems with manufacturers with same name and code.<br>
     * For example manufacturer st
     */
    @Override
    protected String checkRequestUrl(final HttpServletRequest request, final HttpServletResponse response, final String resolvedUrl)
                                                                                                                                     throws UnsupportedEncodingException {
        try {
            final String requestURI = URIUtil.decode(request.getRequestURI(), "utf-8");
            final String decoded = URIUtil.decode(resolvedUrl, "utf-8");

            /*
             * The links that could be managed are: /manufacturer/st and /manufacturer/st/man_st With the default implementation this case
             * is not supported.
             */

            if (StringUtils.isNotEmpty(decoded) && decoded.startsWith(requestURI) && (decoded.split("/").length == requestURI.split("/").length)) {
                return null;
            } else {
                request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.MOVED_PERMANENTLY);

                final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(resolvedUrl);

                if (StringUtils.isNotBlank(request.getQueryString())) {
                    uriComponentsBuilder.query(request.getQueryString());
                }

                final RedirectView redirectView = new RedirectView(uriComponentsBuilder.build().toUriString());
                redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
                return addFasterizeCacheControlParameter(REDIRECT_PREFIX + uriComponentsBuilder.build().toUriString());
            }
        } catch (final URIException e) {
            throw new UnsupportedEncodingException(e.getMessage()); // NOPMD
        }
    }

    protected List<Breadcrumb> getBreadcrumbs(final String pageTitle, final String parentUrl, final String url, final String name) {
        return getManufacturerBreadcrumbBuilder().getBreadcrumbs(pageTitle, parentUrl, url, name);
    }

    protected String getDetailView() {
        return ControllerConstants.Views.Pages.Manufacturer.ManufacturerStoreDetailPage;
    }

    protected String getStoreView() {
        return ControllerConstants.Views.Pages.Manufacturer.ManufacturerStoresPage;
    }

    // BEGIN Implementation of abstract methods

    @Override
    protected String getStoreCmsPageName() {
        return MANUFACTURER_STORES_CMS_PAGE;
    }

    @Override
    protected List<Breadcrumb> getBreadcrumbs(final String pageTitle, final String uri) {
        return getManufacturerBreadcrumbBuilder().getBreadcrumbs(pageTitle, uri);
    }

    @Override
    protected FactFinderProductSearchPageData<SearchStateData, ProductData> getProducts(final boolean tracking, final String searchString,
                                                                                        final SearchStateData searchState, final PageableData pageableData,
                                                                                        final boolean generateASN) {

        if (StringUtils.isNotEmpty(searchString)) {
            final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData = getProductSearchFacade().search(tracking, searchState,
                                                                                                                                 pageableData,
                                                                                                                                 DistSearchType.MANUFACTURER,
                                                                                                                                 searchString, generateASN,
                                                                                                                                 FF_LOG_NAME,
                                                                                                                                 MapUtils.EMPTY_MAP);
            // DISTRELEC-5176: add filterstrings
            getProductSearchFacade().addFilterstrings(searchPageData);
            return searchPageData;
        } else {
            return null;
        }
    }

    protected void storeMetaKeywordsInModel(final Model model, final Object item, final String seoMetaDescription) {

        if (item instanceof DistManufacturerModel) {

            final DistManufacturerModel manufacturerModel = (DistManufacturerModel) item;

            final String metaDescription = getMessageSource().getMessage("meta.description.manufacturer",
                                                                         new Object[] { manufacturerModel.getName() }, "", getI18nService().getCurrentLocale());
            setUpMetaData(model, StringUtils.isNotEmpty(seoMetaDescription) ? seoMetaDescription : metaDescription);
        }

    }

    @Override
    protected void storeMetaKeywordsInModel(final Model model, final Object item) {

        if (item instanceof DistManufacturerModel) {

            final DistManufacturerModel manufacturerModel = (DistManufacturerModel) item;

            final String metaDescription = getMessageSource().getMessage("meta.description.manufacturer", new Object[] { manufacturerModel.getName() }, "",
                                                                         getI18nService().getCurrentLocale());
            setUpMetaData(model, metaDescription);
        }

    }

    @ExceptionHandler(UnknownIdentifierException.class)
    public String handleUnknownIdentifierException(final UnknownIdentifierException exception, final HttpServletRequest request) {
        request.setAttribute("exception", exception);
        final String uuidString = java.util.UUID.randomUUID().toString();
        ERROR_PAGE_LOG.debug("a technical error occured [uuid: {}], IP Address: {}. {}", uuidString, request.getRemoteAddr(), exception.getMessage());
        request.setAttribute("uuid", uuidString);
        return FORWARD_PREFIX + "/" + "notFound";
    }

    // END Implementation of abstract methods

    // BEGIN GENERATED CODE

    public DistManufacturerService getDistManufacturerService() {
        return distManufacturerService;
    }

    public void setDistManufacturerService(final DistManufacturerService distManufacturerService) {
        this.distManufacturerService = distManufacturerService;
    }

    @Override
    public DistManufacturerFacade getDistManufacturerFacade() {
        return distManufacturerFacade;
    }

    @Override
    public void setDistManufacturerFacade(final DistManufacturerFacade distManufacturerFacade) {
        this.distManufacturerFacade = distManufacturerFacade;
    }

    public ProductSearchFacade<ProductData> getProductSearchFacade() {
        return productSearchFacade;
    }

    public void setProductSearchFacade(final ProductSearchFacade<ProductData> productSearchFacade) {
        this.productSearchFacade = productSearchFacade;
    }

    public ManufacturerBreadcrumbBuilder getManufacturerBreadcrumbBuilder() {
        return manufacturerBreadcrumbBuilder;
    }

    public void setManufacturerBreadcrumbBuilder(final ManufacturerBreadcrumbBuilder manufacturerBreadcrumbBuilder) {
        this.manufacturerBreadcrumbBuilder = manufacturerBreadcrumbBuilder;
    }

    public DistManufacturerConverter getDistManufacturerConverter() {
        return distManufacturerConverter;
    }

    public void setDistManufacturerConverter(final DistManufacturerConverter distManufacturerConverter) {
        this.distManufacturerConverter = distManufacturerConverter;
    }

    @Override
    public PageTitleResolver getPageTitleResolver() {
        return pageTitleResolver;
    }

    @Override
    public void setPageTitleResolver(final PageTitleResolver pageTitleResolver) {
        this.pageTitleResolver = pageTitleResolver;
    }

    public DistUrlResolver<DistManufacturerModel> getDistManufacturerModelUrlResolver() {
        return distManufacturerModelUrlResolver;
    }

    public void setDistManufacturerModelUrlResolver(final DistUrlResolver<DistManufacturerModel> distManufacturerModelUrlResolver) {
        this.distManufacturerModelUrlResolver = distManufacturerModelUrlResolver;
    }

    public DistUrlResolver<ContentPageModel> getContentPageUrlResolver() {
        return contentPageUrlResolver;
    }

    public void setContentPageUrlResolver(final DistUrlResolver<ContentPageModel> contentPageUrlResolver) {
        this.contentPageUrlResolver = contentPageUrlResolver;
    }

    @Override
    public DistCategoryFacade getDistCategoryFacade() {
        return distCategoryFacade;
    }

    @Override
    public void setDistCategoryFacade(final DistCategoryFacade distCategoryFacade) {
        this.distCategoryFacade = distCategoryFacade;
    }

    public SearchBreadcrumbBuilder getSearchBreadcrumbBuilder() {
        return searchBreadcrumbBuilder;
    }

    public void setSearchBreadcrumbBuilder(final SearchBreadcrumbBuilder searchBreadcrumbBuilder) {
        this.searchBreadcrumbBuilder = searchBreadcrumbBuilder;
    }
    // END GENERATED CODE
}
