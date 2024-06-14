package com.namics.distrelec.b2b.storefront.controllers.pages;

import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.FILTER;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.ListMultimap;
import com.google.common.net.HttpHeaders;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.SearchExperience;
import com.namics.distrelec.b2b.core.event.FeedbackDataDto;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.feedback.DistFeedbackFacade;
import com.namics.distrelec.b2b.facades.misc.DistShareWithFriendsFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.search.converter.FactFinderLazyFacetConverter;
import com.namics.distrelec.b2b.facades.tracking.DistTrackingFactFinderFacade;
import com.namics.distrelec.b2b.storefront.controllers.ThirdPartyConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.forms.SendToFriendForm;
import com.namics.distrelec.b2b.storefront.forms.ZeroResultSearchFeedbackForm;
import com.namics.distrelec.b2b.storefront.util.URLUtil;
import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;
import com.namics.hybris.ffsearch.breadcrumb.SearchBreadcrumbBuilder;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.suggest.AutocompleteSuggestion;
import com.namics.hybris.ffsearch.util.WebUtil;
import com.namics.hybris.ffsearch.util.XSSFilterUtil;

import de.factfinder.webservice.ws71.FFsearch.SearchResultArticleNumberStatus;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Controller for search page.
 */
@Controller
@RequestMapping({ "/search", "/*/search" })
public class SearchPageController extends AbstractSearchPageController {

    public static final String REDIRECTED_FROM_SEARCH = "RedirectedFromSearch";

    public static final String SEARCH_CATEGORY_FILTER_PARAMETER = "filter_categoryCodePathROOT";

    public static final String SEARCH_SUBCATEGORY_FILTER_PARAMETER_PREFIX = "filter_categoryCodePathROOT/";

    public static final String TRACK = "?track=true";

    public static final String FILTER_CATEGORY_CODE_PATH_ROOT_REGEX = "&filter_categoryCodePathROOT=[\\w-]*";

    public static final String DIGITAL_DATA_LAYER_TERM = "digitalDataLayerTerm";

    public static final String DIGITAL_DATA_LAYER_TERM_EQUALS = "digitalDataLayerTerm=";

    private static final Logger LOG = Logger.getLogger(SearchPageController.class);

    private static final String SEARCH_CMS_PAGE_ID = "search";

    private static final String NO_RESULTS_CMS_PAGE_ID = "searchEmpty";

    private static final String SEARCH_FEEDBACK_SENT_PAGE_ID = "searchFeedbackSent";

    private static final String STATUS = "status";

    private static final String ZERO_RESULTS = "0";

    @Autowired
    @Qualifier("productModelUrlResolver")
    private UrlResolver<ProductModel> productModelUrlResolver;

    @Autowired
    private DistrelecProductFacade productFacade;

    @Autowired
    private DistFeedbackFacade distFeedbackFacade;

    @Autowired
    private SearchBreadcrumbBuilder searchBreadcrumbBuilder;

    @Autowired
    private DistShareWithFriendsFacade distShareWithFriendsFacade;

    @Autowired
    private DistTrackingFactFinderFacade distTrackingFactFinderFacade;

    @Autowired
    private DistCategoryFacade distCategoryFacade;

    @Autowired
    private B2BCustomerService b2bCustomerService;

    @Autowired
    private B2BUnitService b2bUnitService;

    @Autowired
    private SessionService sessionService;

    @Override
    public String setUpMetaRobotContent() {
        return getMessageSource().getMessage("meta.robots.search", new Object[] {}, getI18nService().getCurrentLocale());
    }

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
    public String textSearch( //
                             @RequestParam(value = SEARCH_QUERY_PARAMETER_NAME, defaultValue = "") final String searchQuery, //
                             @RequestParam(value = PAGE_PARAMETER_NAME, defaultValue = "1") final int page, //
                             @RequestParam(value = "pageSize", defaultValue = "0") final int pageSize, //
                             @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode, //
                             @RequestParam(value = "sort", required = false) final String sortCode, //
                             @RequestParam(value = "log", required = false) final String log,
                             @RequestParam(value = "requestType", required = false) final String requestType, //
                             @RequestParam(value = "useTechnicalView", required = false) final Boolean useTechnicalView, //
                             @RequestParam(value = "removetitle", required = false) final Boolean removeTitle, //
                             @RequestParam(value = FILTER_CURATED_PRODUCTS_PARAMETER_NAME, required = false) final String filter_CuratedProducts,
                             final HttpServletRequest request, //
                             final Model model, //
                             final RedirectAttributes redirectAttributes, //
                             final HttpServletResponse response) throws CMSItemNotFoundException {

        // check if the request is for a punched out product
        if (getProductFacade().enablePunchoutFilterLogic() && searchQuery.matches("([0-9]|-)+") // Accept also '-' in the product code
                && (getProductFacade().isProductExcluded(normalizeProductCode(searchQuery)))) { // Remove '-' from the product code if any

            SiteChannel currentChannel = getBaseStoreService().getCurrentBaseStore().getChannel();
            if (currentChannel.equals(SiteChannel.B2C)) {
                boolean availableToB2B = !getProductFacade().isProductExcludedForSiteChannel(normalizeProductCode(searchQuery), SiteChannel.B2B);
                if (availableToB2B) {
                    model.addAttribute("availableToB2B", availableToB2B);
                }
            }

            model.addAttribute(ThirdPartyConstants.Webtrekk.WT_TOTAL_SEARCH_RESULTS, "0");
            ContentPageModel contentPageForLabelOrId = getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID);
            storeCmsPageInModel(model, contentPageForLabelOrId);
            model.addAttribute("searchPhrase", searchQuery);

            setMetaRobots(model, contentPageForLabelOrId);
            model.addAttribute("searchTerm", XSSFilterUtil.filter(searchQuery));
            final String metaDescription = getMessageSource().getMessage("search.meta.description.results",
                                                                         new Object[] { XSSFilterUtil.filter(searchQuery), getSiteName() },
                                                                         getI18nService().getCurrentLocale());
            setUpMetaData(model, metaDescription);
            final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
            getDistDigitalDatalayerFacade().populatePrimaryPageCategoryAndPageType(digitalDatalayer, DigitalDatalayerConstants.PageCategory.SEARCH,
                                                                                   DigitalDatalayerConstants.PageType.SEARCHRESULTSPLPPAGE);
            addGlobalModelAttributes(model, request);
            model.addAttribute("pageType", PageType.ProductSearch);
            return getViewForPage(model);

        }

        final String STAR_COMMA = "*,";
        final String cleansedSearchQuery = searchQuery.startsWith(STAR_COMMA) ? searchQuery.substring(STAR_COMMA.length()) : searchQuery;
        // Remembering the Pagination preference selected by customer
        final int pageSizeValue = calculatePageSize(pageSize, request, response);

        SearchExperience searchExperience = getProductSearchFacade().getSearchExperienceFromCurrentBaseStore();
        String returnResponse = getAndPostSearchResultHandler(cleansedSearchQuery, page, pageSizeValue, showMode, sortCode, log, requestType, useTechnicalView,
                                                              removeTitle, filter_CuratedProducts, request, model, redirectAttributes, response,
                                                              searchExperience);

        returnResponse = stripCategoryFilterOnCampaignRedirect(request, redirectAttributes, returnResponse);

        model.addAttribute("pageType", PageType.ProductSearch);

        return returnResponse;
    }

    private String stripCategoryFilterOnCampaignRedirect(HttpServletRequest request, RedirectAttributes redirectAttributes, String returnResponse) {
        boolean hasSubcategoryFilter = Collections.list(request.getParameterNames()).stream()
                                                  .anyMatch(name -> name.startsWith(SEARCH_SUBCATEGORY_FILTER_PARAMETER_PREFIX));

        if (!hasSubcategoryFilter) {
            if (returnResponse.startsWith("redirect:") && !StringUtils.isEmpty(request.getParameter(SEARCH_CATEGORY_FILTER_PARAMETER))) {
                returnResponse = returnResponse.replaceFirst(FILTER_CATEGORY_CODE_PATH_ROOT_REGEX, "");
                redirectAttributes.addAttribute(REDIRECTED_FROM_SEARCH, true);
            }
        }
        return returnResponse;
    }

    private String getAndPostSearchResultHandler(String searchQuery, final int page, final int pageSize, final ShowMode showMode, final String sortCode,
                                                 final String log, final String requestType, final Boolean useTechnicalView, final Boolean removeTitle,
                                                 final String filter_CuratedProducts, final HttpServletRequest request, final Model model,
                                                 final RedirectAttributes redirectAttributes, final HttpServletResponse response,
                                                 SearchExperience searchExperience) throws CMSItemNotFoundException {
        if (REQUEST_TYPE_AJAX.equals(requestType)) {
            setFFfollowSearchParameter(Boolean.TRUE);
        } else {
            setFFfollowSearchParameter(Boolean.FALSE);
        }

        String newFilteredCategoryCode = request.getParameter(FILTER_CATEGORY_CODE_PATH_ROOT);

        if (!request.getParameterMap().containsKey(FILTER_CATEGORY_CODE_PATH_ROOT)) {
            sessionService.setAttribute(FILTER_CATEGORY_CODE_PATH_ROOT, null);
        } else {
            sessionService.setAttribute(FILTER_CATEGORY_CODE_PATH_ROOT, newFilteredCategoryCode);
        }

        setNonFilerSearchParameters(request, model);
        // check if its comes from suggest
        boolean queryFromSuggest = false;
        if (request.getParameter(DistConstants.FactFinder.FF_TRACKING) != null) {
            queryFromSuggest = Boolean.valueOf(request.getParameter(DistConstants.FactFinder.FF_TRACKING)).booleanValue();
        }

        // DISTRELEC-5016 Replace " by inch in search box
        final int inchCounter = StringUtils.countMatches(searchQuery, "\"");
        if (inchCounter == 1) {
            searchQuery = searchQuery.replaceFirst("\\s?\"", " inch");
        }

        switchTechnicalViewIfNeeded(useTechnicalView, model, request, response);
        final Boolean removeTitleValue = Boolean.valueOf(removeTitle != null && removeTitle.booleanValue());
        if (StringUtils.isNotBlank(searchQuery)) {
            // searchQuery.concat("&").concat("lang=").concat(request.getLocale().getLanguage());
            final SearchStateData searchState = createSearchStateData(request, XSSFilterUtil.filterForSearch(searchQuery));

            final PageableData pageableData = createSearchPageableDataWithCookie(request, response, page, pageSize, sortCode, showMode,
                                                                                 BooleanUtils.isTrue(useTechnicalView));

            Map<String, List<String>> otherSearchParams = getOtherSearchParams(request);

            FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData;

            searchPageData = getProductSearchFacade().search(queryFromSuggest, searchState,
                                                             pageableData, DistSearchType.TEXT, null, true, log, otherSearchParams);

            if (searchPageData != null && searchPageData.getResults() != null) {
                for (ProductData product : searchPageData.getResults()) {
                    product.setUrl(product.getUrl() + TRACK);
                }
            }

            List<ProductData> punchedOutProducts = applyPunchoutFilterLogic(searchPageData);
            if (CollectionUtils.isNotEmpty(punchedOutProducts)) {
                if (searchPageData != null && searchPageData.getPagination() != null) {
                    searchPageData.getPagination()
                                  .setTotalNumberOfResults(searchPageData.getPagination().getTotalNumberOfResults() - punchedOutProducts.size());
                    if (searchPageData.getPagination().getTotalNumberOfResults() > 0) {
                        GlobalMessages.addErrorMessage(model, "search.product.error.punchout",
                                                       new String[] { getJoinedPunchedOutProductCodes(punchedOutProducts) }, DistConstants.Punctuation.PIPE);
                    }
                }
            }

            if (searchPageData != null) {
                populateTabularAttributeSortingMap(model, searchPageData.getSortableAttributeMap());
            }

            // DISTRELEC-9134: SRP
            createCategoryDisplayData(model, searchPageData, distCategoryFacade);

            // DISTRELEC-5176: add filter strings
            getProductSearchFacade().addFilterstrings(searchPageData);

            if (searchPageData != null && StringUtils.isNotBlank(searchPageData.getKeywordRedirectUrl())) {
                try {
                    redirectAttributes.addFlashAttribute(DIGITAL_DATA_LAYER_TERM, searchQuery);
                    final URI redirectURI = new URI(searchPageData.getKeywordRedirectUrl());
                    return addFasterizeCacheControlParameter(REDIRECT_PREFIX
                                                             + (redirectURI.getScheme() != null ? (redirectURI.getScheme() + "://" + redirectURI.getHost() + ":"
                                                                                                   + redirectURI.getPort())
                                                                                                : "")
                                                             + redirectURI.getPath() + URLUtil.mergeQueryStrings(redirectURI.getRawQuery(),
                                                                                                                 DIGITAL_DATA_LAYER_TERM_EQUALS + searchQuery));
                } catch (final URISyntaxException e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Could not decode Redirect URI, failing back to redirect without merging query strings.", e);
                    }
                    return addFasterizeCacheControlParameter(REDIRECT_PREFIX + searchPageData.getKeywordRedirectUrl());
                }
            }

            if (searchPageData != null && searchPageData.getPagination() != null && searchPageData.getPagination().getTotalNumberOfResults() == 1) {
                if (searchPageData.getResultArticleNumberStatus() == SearchResultArticleNumberStatus.RESULTS_FOUND && searchPageData.isMpnMatch()) {
                    redirectAttributes.addFlashAttribute(DistConstants.FactFinder.FF_MPN_ALTERNATIVE_MATCH_KEY, Boolean.TRUE);
                }
                if ((CollectionUtils.isEmpty(searchPageData.getFilters()) &&
                        CollectionUtils.isEmpty(searchPageData.getAdvisorCampaigns()) &&
                        CollectionUtils.isEmpty(searchPageData.getFeedbackCampaigns())) ||
                        searchPageData.getResultArticleNumberStatus() == SearchResultArticleNumberStatus.RESULTS_FOUND) {
                    return addFasterizeCacheControlParameter(REDIRECT_PREFIX + searchPageData.getResults().get(0).getUrl());
                }
            }

            // Setting the remove filters URL
            setRemoveFilersURL(searchPageData, request);

            if (REQUEST_TYPE_AJAX.equals(requestType)) {
                // return search result as JSON
                model.addAttribute("searchPageData", searchPageData);
                if (searchPageData != null && searchPageData.getPagination() != null) {
                    model.addAttribute("isLastPage", isLastPage(searchPageData, page));
                }
                storeCmsPageInModel(model, getContentPageForLabelOrId(SEARCH_CMS_PAGE_ID));
                return getAjaxView();
            }

            final List<Breadcrumb> breadcrumbList;
            if (searchPageData == null) {
                model.addAttribute(ThirdPartyConstants.Webtrekk.WT_TOTAL_SEARCH_RESULTS, "0");
                storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
            } else if (searchPageData.getKeywordRedirectUrl() != null) {
                // if the search engine returns a redirect, just
                return addFasterizeCacheControlParameter(REDIRECT_PREFIX + searchPageData.getKeywordRedirectUrl());
            } else if (searchPageData.getPagination() != null && searchPageData.getPagination().getTotalNumberOfResults() == 0) {
                if (CollectionUtils.isNotEmpty(punchedOutProducts)) {
                    GlobalMessages.addErrorMessage(model, "search.product.no.result.error.punchout");
                }
                model.addAttribute(ThirdPartyConstants.Webtrekk.WT_TOTAL_SEARCH_RESULTS, "0");
                model.addAttribute("searchPageData", searchPageData);
                final ContentPageModel noResultPage = getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID);
                storeCmsPageInModel(model, noResultPage);
                updatePageTitle(searchQuery, model);
                model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                                   removeTitleValue.booleanValue() ? Collections.EMPTY_LIST
                                                                   : getSearchBreadcrumbBuilder().getSearchBreadcrumbs(noResultPage.getTitle(),
                                                                                                                       "/search?" + SEARCH_QUERY_PARAMETER_NAME
                                                                                                                                                + "="
                                                                                                                                                + searchState.getQuery()
                                                                                                                                                             .getValue(),
                                                                                                                       searchQuery));

            } else {
                if (searchPageData.getKeywordRedirectUrl() != null) {
                    // if the search engine returns a redirect, just
                    return addFasterizeCacheControlParameter(REDIRECT_PREFIX + searchPageData.getKeywordRedirectUrl());
                } else {
                    if (searchPageData.getPagination() != null && searchPageData.getPagination().getTotalNumberOfResults() == 0) {
                        model.addAttribute(ThirdPartyConstants.Webtrekk.WT_TOTAL_SEARCH_RESULTS, "0");
                        model.addAttribute("searchPageData", searchPageData);
                        final ContentPageModel noResultPage = getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID);
                        storeCmsPageInModel(model, noResultPage);
                        updatePageTitle(StringUtils.isNotBlank(filter_CuratedProducts) ? stripCuratedProductsPrefix(filter_CuratedProducts) : searchQuery,
                                        model);
                        breadcrumbList = createSearchBreadcrumb(searchQuery, model, removeTitleValue, searchState, searchPageData, noResultPage,
                                                                searchBreadcrumbBuilder, filter_CuratedProducts);
                    } else {
                        model.addAttribute(ThirdPartyConstants.Webtrekk.WT_TOTAL_SEARCH_RESULTS,
                                           String.valueOf(searchPageData.getPagination().getTotalNumberOfResults()));
                        // Setting the URLs of the previous and next pages
                        final String searchURL = storeContinueUrl(request);
                        updatePagination(searchURL, (FactFinderPaginationData) searchPageData.getPagination(), model);
                        model.addAttribute("searchPageData", searchPageData);
                        int lastProductNumber = searchPageData.getPagination().getCurrentPage() * searchPageData.getPagination().getPageSize();
                        model.addAttribute("lastProductNumber",
                                           isLastPage(searchPageData, page) ? (int) searchPageData.getPagination().getTotalNumberOfResults()
                                                                            : lastProductNumber);
                        model.addAttribute("firstProductNumber", lastProductNumber - searchPageData.getPagination().getPageSize() + 1);
                        model.addAttribute("isShowAllAllowed", isShowAllAllowed(searchPageData));
                        // Populate Data Layer
                        populateDataLayerForProducts(searchQuery, searchPageData, model, false);

                        final ContentPageModel searchPage = getContentPageForLabelOrId(SEARCH_CMS_PAGE_ID);
                        storeCmsPageInModel(model, searchPage, DistConstants.Webtrekk.TEASER_TRACKING_ONS);
                        updatePageTitle(StringUtils.isNotBlank(filter_CuratedProducts) ? stripCuratedProductsPrefix(filter_CuratedProducts) : searchQuery,
                                        model);
                        model.addAttribute("sharePageUrl",
                                           getConfigurationService().getConfiguration()
                                                                    .getString("website." + getCmsSiteService().getCurrentSite().getUid() + ".http")
                                                           + getSessionService().getAttribute(WebConstants.CONTINUE_URL));

                        breadcrumbList = createSearchBreadcrumb(searchQuery, model, removeTitleValue, searchState, searchPageData, searchPage,
                                                                getSearchBreadcrumbBuilder(), filter_CuratedProducts);

                        if (searchPageData
                                          .getResultArticleNumberStatus() == SearchResultArticleNumberStatus.RESULTS_FOUND
                                && searchPageData.isMpnMatch()) {
                            model.addAttribute("isMPNMatch", Boolean.TRUE);
                            GlobalMessages.addConfMessage(model, DistConstants.FactFinder.FF_MPN_MESSAGE_KEY);
                        }
                    }
                    model.addAttribute(WebConstants.BREADCRUMBS_KEY, breadcrumbList);
                }
            }
        } else {
            storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
        }

        // check if the online price calculation flag is up for B2B Customers
        final B2BCustomerModel b2bCustomer = (B2BCustomerModel) b2bCustomerService.getCurrentB2BCustomer();
        boolean onlinePriceCalculation = false;
        if (b2bCustomer != null) {
            final B2BUnitModel parent = (B2BUnitModel) b2bUnitService.getParent(b2bCustomer);
            onlinePriceCalculation = parent.getOnlinePriceCalculation();
        }
        model.addAttribute("onlinePriceCalculation", onlinePriceCalculation);
        model.addAttribute("searchPhrase", searchQuery);
        model.addAttribute("removetitle", removeTitleValue);
        model.addAttribute("pageType", PageType.ProductSearch);
        model.addAttribute("searchTerm", XSSFilterUtil.filter(searchQuery));
        addGlobalModelAttributes(model, request);
        final String metaDescription = getMessageSource().getMessage("search.meta.description.results",
                                                                     new Object[] { XSSFilterUtil.filter(searchQuery), getSiteName() },
                                                                     getI18nService().getCurrentLocale());
        setUpMetaData(model, metaDescription);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        populateDDLEventName(model, digitalDatalayer);
        getDistDigitalDatalayerFacade().populatePrimaryPageCategoryAndPageType(digitalDatalayer, DigitalDatalayerConstants.PageCategory.SEARCH,
                                                                               DigitalDatalayerConstants.PageType.SEARCHRESULTSPLPPAGE);
        getDistDigitalDatalayerFacade().populateFFTrackingAttribute(digitalDatalayer, Boolean.TRUE);
        return getViewForPage(model);
    }

    private void populateDDLEventName(Model model, DigitalDatalayer digitalDatalayer) {
        DigitalDatalayer.EventName eventName = DigitalDatalayer.EventName.NIL_RESULT_SEARCH;
        if (model.containsAttribute(ThirdPartyConstants.Webtrekk.WT_TOTAL_SEARCH_RESULTS)) {
            String searchCount = (String) model.asMap().get(ThirdPartyConstants.Webtrekk.WT_TOTAL_SEARCH_RESULTS);
            if (StringUtils.isNotBlank(searchCount) && searchCount != "0") {
                eventName = DigitalDatalayer.EventName.SUGGESTED_SEARCH;
            }
        }
        digitalDatalayer.setEventName(eventName);
    }

    private List<String> getPunchedOutProductCodes(List<ProductData> punchedOutProducts) {
        return punchedOutProducts.stream()
                                 .map(ProductData::getCode)
                                 .collect(Collectors.toList());
    }

    private String getJoinedPunchedOutProductCodes(List<ProductData> punchedOutProducts) {
        return punchedOutProducts
                                 .stream()
                                 .map(ProductData::getCode)
                                 .collect(Collectors.joining(", "));
    }

    private Map<String, List<String>> getOtherSearchParams(HttpServletRequest request) {
        Map<String, List<String>> otherSearchParams = new HashMap<>();
        if (StringUtils.isNotBlank(request.getParameter(SEARCH_CATEGORY_FILTER_PARAMETER))) {
            otherSearchParams.put("categorySelected", List.of("true"));
        } else {
            otherSearchParams.put("categorySelected", List.of("false"));
        }
        return otherSearchParams;
    }

    // DISTRELEC-11169 remove excluded products. Will have an impact on
    // performance for very large result sets. Needs to load tested for
    // compliance with benchmarks.
    private List<ProductData> applyPunchoutFilterLogic(final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData) {
        List<ProductData> punchedOutProducts = new ArrayList<>();
        if (searchPageData != null && searchPageData.getResults() != null && getProductFacade().enablePunchoutFilterLogic()) {
            final List<ProductData> resultsList = searchPageData.getResults();
            final ListIterator<ProductData> iterator = resultsList.listIterator();
            while (iterator.hasNext()) {
                ProductData productData = iterator.next();
                final String productCode = productData.getCode();
                if (getProductFacade().isProductExcluded(productCode)) {
                    // DISTRELEC-11019 - In case product is visible only to business customers dont remove it from the list
                    SiteChannel channel = getBaseStoreService().getCurrentBaseStore().getChannel();
                    if (SiteChannel.B2B.equals(channel) && BooleanUtils.isFalse(productData.getAvailableToB2B())) {
                        iterator.remove();
                        punchedOutProducts.add(productData);
                    }
                }
            }
        }
        return punchedOutProducts;
    }

    @RequestMapping(value = "/cnt", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public String textSearchCount( //
                                  @RequestParam(value = SEARCH_QUERY_PARAMETER_NAME, defaultValue = "") String searchQuery, //
                                  @RequestParam(value = PAGE_PARAMETER_NAME, defaultValue = "1") final int page, //
                                  @RequestParam(value = "pageSize", defaultValue = "0") final int pageSize, //
                                  @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode, //
                                  @RequestParam(value = "sort", required = false) final String sortCode, //
                                  @RequestParam(value = "log", required = false) final String log,
                                  @RequestParam(value = "requestType", required = false) final String requestType, //
                                  @RequestParam(value = "useTechnicalView", required = false) final Boolean useTechnicalView, //
                                  @RequestParam(value = "removetitle", required = false) final Boolean removeTitle, //
                                  @RequestParam(value = FILTER_CURATED_PRODUCTS_PARAMETER_NAME, required = false) final String filter_CuratedProducts, //
                                  final HttpServletRequest request, //
                                  final Model model, //
                                  final RedirectAttributes redirectAttributes, //
                                  final HttpServletResponse response) throws CMSItemNotFoundException {
        final String STAR_COMMA = "*,";
        searchQuery = searchQuery.startsWith(STAR_COMMA) ? searchQuery.substring(STAR_COMMA.length()) : searchQuery;

        setFFfollowSearchParameter(REQUEST_TYPE_AJAX.equals(requestType) ? Boolean.TRUE : Boolean.FALSE);

        String newFilteredCategoryCode = request.getParameter(FILTER_CATEGORY_CODE_PATH_ROOT);
        sessionService.setAttribute(FILTER_CATEGORY_CODE_PATH_ROOT,
                                    (!request.getParameterMap().containsKey(FILTER_CATEGORY_CODE_PATH_ROOT) ? null : newFilteredCategoryCode));

        setNonFilerSearchParameters(request, model);
        // check if its comes from suggest
        boolean queryFromSuggest = false;
        if (request.getParameter(DistConstants.FactFinder.FF_TRACKING) != null) {
            queryFromSuggest = Boolean.valueOf(request.getParameter(DistConstants.FactFinder.FF_TRACKING)).booleanValue();
        }

        // DISTRELEC-5016 Replace " by inch in search box
        final int inchCounter = StringUtils.countMatches(searchQuery, "\"");
        if (inchCounter == 1) {
            searchQuery = searchQuery.replaceFirst("\\s?\"", " inch");
        }

        switchTechnicalViewIfNeeded(useTechnicalView, model, request, response);
        final Boolean removeTitleValue = Boolean.valueOf(removeTitle != null && removeTitle.booleanValue());
        if (StringUtils.isNotBlank(searchQuery)) {
            // searchQuery.concat("&").concat("lang=").concat(request.getLocale().getLanguage());
            final SearchStateData searchState = createSearchStateData(request, XSSFilterUtil.filterForSearch(searchQuery));

            final PageableData pageableData = createSearchPageableDataWithCookie(request, response, page, pageSize, sortCode, showMode,
                                                                                 BooleanUtils.isTrue(useTechnicalView));

            Map<String, List<String>> otherSearchParams = getOtherSearchParams(request);

            FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData = getProductSearchFacade().search(queryFromSuggest, searchState,
                                                                                                                           pageableData, DistSearchType.TEXT,
                                                                                                                           null, true, log, otherSearchParams);
            return String.valueOf(searchPageData.getPagination().getTotalNumberOfResults());
        }

        return ZERO_RESULTS;

    }

    @GetMapping(value = "showmore", params = SEARCH_QUERY_PARAMETER_NAME, produces = "application/json")
    public final String textSearchShowMore(@RequestParam(SEARCH_QUERY_PARAMETER_NAME) final String searchQuery,
                                           @RequestParam(value = PAGE_PARAMETER_NAME, defaultValue = "0") final int page,
                                           @RequestParam(value = "pageSize", defaultValue = "0") final int pageSize,
                                           @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
                                           @RequestParam(value = "sort", required = false) final String sortCode,
                                           @RequestParam(value = "log", required = false) final String log, final HttpServletRequest request, final Model model,
                                           final HttpServletResponse response) throws CMSItemNotFoundException {

        final SearchStateData searchState = createSearchStateData(request, XSSFilterUtil.filterForSearch(searchQuery));
        // The page size minimum value should be 1
        // check if the page size exceeds DEFAULT_SEARCH_MAX_SIZE, if it does,
        // default to DEFAULT_SEARCH_MAX_SIZE
        // Limit call to availability, truncate list to limit.availability.to
        final int pageSizeValue = calculatePageSize(pageSize);

        final PageableData pageableData = createPageableDataWithCookie(request, response, page, pageSizeValue, sortCode, showMode);
        final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData = getProductSearchFacade().search(false, searchState, pageableData,
                                                                                                                             DistSearchType.TEXT, null, false,
                                                                                                                             log, MapUtils.EMPTY_MAP);

        // if (searchPageData != null) {
        // populateTabularAttributeSortingMap(model,
        // searchPageData.getSortableAttributeMap());
        // }

        getProductSearchFacade().addFilterstrings(searchPageData);

        // Setting the URLs of the previous and next pages
        final String searchURL = storeContinueUrl(request);
        updatePagination(searchURL, (FactFinderPaginationData) searchPageData.getPagination(), model);
        // Old DataLayer implementation
        populateDataLayerForProducts(searchQuery, searchPageData, model, false);

        model.addAttribute("searchPageData", searchPageData);
        model.addAttribute("products", searchPageData.getResults());
        model.addAttribute("isLastPage", isLastPage(searchPageData, page));
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePrimaryPageCategoryAndPageType(digitalDatalayer, DigitalDatalayerConstants.PageCategory.SEARCH,
                                                                               DigitalDatalayerConstants.PageType.SEARCHRESULTSPLPPAGE);
        return getShowMoreView();
    }

    @GetMapping(value = FactFinderLazyFacetConverter.ADDITIONAL_FACET_PATH, produces = "application/json")
    public String loadAdditionalFacet(@RequestParam("q") final String searchQuery,
                                      @RequestParam(value = FactFinderLazyFacetConverter.ADDITIONAL_FACET_PARAM_NAME, required = true) final String additionalFacet,
                                      final HttpServletRequest request, final Model model) {

        final SearchStateData searchState = createSearchStateData(request, searchQuery);
        final PageableData paging = createPageableData(0, 0, null, null);

        final FactFinderFacetData<SearchStateData> additionalFacetData = getProductSearchFacade().loadAdditionalFacet(searchState, paging, additionalFacet,
                                                                                                                      null, null);
        model.addAttribute("facet", additionalFacetData);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePrimaryPageCategoryAndPageType(digitalDatalayer, DigitalDatalayerConstants.PageCategory.SEARCH,
                                                                               DigitalDatalayerConstants.PageType.SEARCHRESULTSPLPPAGE);
        return getAdditionalFacetView();
    }

    @PostMapping(value = "/feedback")
    public String sendFeedback(final Model model, @Valid final ZeroResultSearchFeedbackForm feedback, final BindingResult bindingResult,
                               final HttpServletRequest request) throws CMSItemNotFoundException {

        try {
            final boolean isCaptchaValid = getCaptchaUtil().validateReCaptcha(request);
            if (!isCaptchaValid || bindingResult.hasErrors()) {
                storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
                model.addAttribute("sentFeedback", feedback);
                GlobalMessages.addErrorMessage(model, "toolsitem.share.message.error");
                model.addAttribute(STATUS, Boolean.FALSE);
                model.addAttribute("captchaError", Boolean.valueOf(!isCaptchaValid));
            } else {
                final FeedbackDataDto feedbackData = new FeedbackDataDto(feedback.getEmail(), feedback.getManufacturer(),
                                                                         feedback.getManufacturerTypeOtherName(),
                                                                         feedback.getManufacturerType(),
                                                                         feedback.getProductName(),
                                                                         feedback.getTellUsMore(),
                                                                         feedback.getSearchTerm());
                getDistFeedbackFacade().submitFeedbackData(feedbackData);
                final ContentPageModel page = getContentPageForLabelOrId(SEARCH_FEEDBACK_SENT_PAGE_ID);
                storeCmsPageInModel(model, page);
                model.addAttribute(STATUS, Boolean.TRUE);
            }
        } catch (final Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Some error has occurred while writing feedback form.", e);
            }
            storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
            model.addAttribute("sentFeedback", feedback);
            GlobalMessages.addErrorMessage(model, "toolsitem.share.message.error");
            model.addAttribute(STATUS, Boolean.FALSE);
        }
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePrimaryPageCategoryAndPageType(digitalDatalayer, DigitalDatalayerConstants.PageCategory.SEARCH,
                                                                               DigitalDatalayerConstants.PageType.FEEDBACKPAGE);
        addGlobalModelAttributes(model, request);
        return getViewForPage(model);
    }

    @ResponseBody
    @GetMapping(value = "/autocomplete")
    public AutocompleteSuggestion getAutocompleteSuggestions(@RequestParam("term") final String term,
                                                             @RequestParam(value = "categoryRestrictions", required = false) final String categoryRestrictions) {
        return getProductSearchFacade().getAutocompleteSuggestions(term, getProductSearchFacade().createCategoryRestrictionsArray(categoryRestrictions));
    }

    @ResponseBody
    @RequestMapping(value = "/autoApplyFilter", method = { RequestMethod.GET, RequestMethod.POST }, produces = "text/plain")
    public String updateAutoApplyFilter(@RequestParam(value = "autoApply", required = true) final Boolean autoApply, final HttpServletRequest request,
                                        final Model model, final HttpServletResponse response) {
        switchAutoApplyFilterIfNeeded(autoApply, model, request, response);
        return "OK";
    }

    @ResponseBody
    @GetMapping(value = "/autocompleteSecure")
    public AutocompleteSuggestion getAutocompleteSuggestionsSecure(@RequestParam("term") final String term,
                                                                   @RequestParam(value = "categoryRestrictions", required = false) final String categoryRestrictions) {
        return getAutocompleteSuggestions(term, categoryRestrictions);
    }

    @PostMapping(value = "/slider")
    public String changeSliderValues(final HttpServletRequest request) {
        final StringBuilder urlParams = new StringBuilder('?');
        final ListMultimap<String, String> filterParams = WebUtil.getParamsStartingWith(request, FILTER);
        final Iterator<Entry<String, String>> filterParamsIter = IteratorUtils.getIterator(filterParams.entries());
        while (filterParamsIter.hasNext()) {
            final Entry<String, String> param = filterParamsIter.next();
            urlParams.append(FILTER).append(param.getKey()).append('=').append(param.getValue());
            if (filterParamsIter.hasNext()) {
                urlParams.append('&');
            }
        }
        return getReturnRedirectUrl(request) + urlParams;
    }

    @PostMapping(value = "/sendToFriend")
    @ResponseBody
    public Map<String, String> sendSearchResultsToFriendInJSON(@Valid final SendToFriendForm form, final BindingResult bindingResults,
                                                               final HttpServletRequest request) {
        final Map<String, String> result = new HashMap<String, String>();
        if (!getCaptchaUtil().validateReCaptcha(request)) {
            result.put("errorCode", "form.captcha.error");
            return result;
        }

        if (bindingResults.hasErrors()) {
            result.put("errorCode", "unknown");
            return result;
        }

        getDistShareWithFriendsFacade().shareSearchResultsWithFriends(getSendToFriendEvent(form), request.getHeader(HttpHeaders.REFERER));

        result.put("errorCode", StringUtils.EMPTY);
        return result;
    }

    protected void updatePageTitle(final String searchText, final Model model) {
        storeContentPageTitleInModel(model, getPageTitleResolver()
                                                                  .resolveContentPageTitle(getMessageSource().getMessage("search.meta.title",
                                                                                                                         new Object[] { searchText },
                                                                                                                         getI18nService().getCurrentLocale())));
    }

    private void updatePagination(final String searchURL, final FactFinderPaginationData<?> paginationData, final Model model) {
        final String baseSearchURL = searchURL.replaceAll("&?page(Size)?=[0-9]+", StringUtils.EMPTY).replace("?&", "?");
        model.addAttribute("baseSearchURL", baseSearchURL);
        final String searchURLPrefix = baseSearchURL + "&pageSize=" + paginationData.getPageSize() + "&page=";

        paginationData.setFirstUrl(searchURLPrefix + 1);
        paginationData.setPrevUrl(searchURLPrefix + paginationData.getPrevPageNr());
        paginationData.setNextUrl(searchURLPrefix + paginationData.getNextPageNr());
        paginationData.setLastUrl(searchURLPrefix + paginationData.getNumberOfPages());
    }

    public UrlResolver<ProductModel> getProductModelUrlResolver() {
        return productModelUrlResolver;
    }

    public void setProductModelUrlResolver(final UrlResolver<ProductModel> productModelUrlResolver) {
        this.productModelUrlResolver = productModelUrlResolver;
    }

    public DistFeedbackFacade getDistFeedbackFacade() {
        return distFeedbackFacade;
    }

    public void setDistFeedbackFacade(final DistFeedbackFacade distFeedbackFacade) {
        this.distFeedbackFacade = distFeedbackFacade;
    }

    public SearchBreadcrumbBuilder getSearchBreadcrumbBuilder() {
        return searchBreadcrumbBuilder;
    }

    public void setSearchBreadcrumbBuilder(final SearchBreadcrumbBuilder searchBreadcrumbBuilder) {
        this.searchBreadcrumbBuilder = searchBreadcrumbBuilder;
    }

    public DistShareWithFriendsFacade getDistShareWithFriendsFacade() {
        return distShareWithFriendsFacade;
    }

    public void setDistShareWithFriendsFacade(final DistShareWithFriendsFacade distShareWithFriendsFacade) {
        this.distShareWithFriendsFacade = distShareWithFriendsFacade;
    }

    public DistTrackingFactFinderFacade getDistTrackingFactFinderFacade() {
        return distTrackingFactFinderFacade;
    }

    public void setDistTrackingFactFinderFacade(final DistTrackingFactFinderFacade distTrackingFactFinderFacade) {
        this.distTrackingFactFinderFacade = distTrackingFactFinderFacade;
    }

    @Override
    public DistCategoryFacade getDistCategoryFacade() {
        return distCategoryFacade;
    }

    @Override
    public void setDistCategoryFacade(final DistCategoryFacade distCategoryFacade) {
        this.distCategoryFacade = distCategoryFacade;
    }

    @Override
    public DistrelecProductFacade getProductFacade() {
        return productFacade;
    }

    @Override
    public void setProductFacade(final DistrelecProductFacade productFacade) {
        this.productFacade = productFacade;
    }

}
