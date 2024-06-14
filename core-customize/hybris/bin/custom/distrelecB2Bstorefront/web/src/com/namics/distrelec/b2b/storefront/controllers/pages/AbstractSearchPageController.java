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

import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.PORTRAIT_MEDIUM;
import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.PORTRAIT_MEDIUM_WEBP;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.FILTER;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import com.distrelec.webservice.if12.v1.SortCriteriaType;
import com.namics.distrelec.b2b.core.enums.SearchExperience;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistInvoiceHistoryPageableData;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOnlineInvoiceHistoryPageableData;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistPaginationData;
import com.namics.distrelec.b2b.core.service.search.pagedata.QuotationHistoryPageableData;
import com.namics.distrelec.b2b.core.service.search.pagedata.SearchPageableData;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.message.queue.cassandra.DistRelatedDataFacade;
import com.namics.distrelec.b2b.facades.storesession.data.ShoppingSettingsCookieData;
import com.namics.distrelec.b2b.storefront.attributes.Attributes;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.ShopSettingsUtil;
import com.namics.distrelec.b2b.storefront.forms.InvoiceHistoryForm;
import com.namics.distrelec.b2b.storefront.forms.OrderHistoryForm;
import com.namics.distrelec.b2b.storefront.forms.QuotationHistoryForm;
import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;
import com.namics.hybris.ffsearch.breadcrumb.SearchBreadcrumbBuilder;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;
import com.namics.hybris.ffsearch.data.facet.CategoryDisplayData;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetSearchPageData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.util.WebUtil;
import com.namics.hybris.ffsearch.util.XSSFilterUtil;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

/**
 * Controller for search pages.
 */
public abstract class AbstractSearchPageController extends AbstractPageController {

    public static final String TRACK_QUERY_PARAMETER_NAME = DistrelecfactfindersearchConstants.TRACK_QUERY_PARAMETER_NAME;

    public static final String SEARCH_QUERY_PARAMETER_NAME = DistrelecfactfindersearchConstants.SEARCH_QUERY_PARAMETER_NAME;

    public static final String PRODUCT_INFORMATION_QUERY_PARAMETER_NAME = DistrelecfactfindersearchConstants.PRODUCT_INFORMATION_QUERY_PARAMETER_NAME;

    public static final String PAGE_PARAMETER_NAME = DistrelecfactfindersearchConstants.PAGE_PARAMETER_NAME;

    public static final String POS_PARAMETER_NAME = DistrelecfactfindersearchConstants.POS_PARAMETER_NAME;

    public static final String ORIG_POS_PARAMETER_NAME = DistrelecfactfindersearchConstants.ORIG_POS_PARAMETER_NAME;

    public static final String ORIG_PAGE_SIZE_PARAMETER_NAME = DistrelecfactfindersearchConstants.ORIG_PAGE_SIZE_PARAMETER_NAME;

    public static final String FILTER_CURATED_PRODUCTS_PARAMETER_NAME = "filter_CuratedProducts";

    protected static final String REQUEST_TYPE_AJAX = "ajax";

    protected static final String FF_FOLLOW_SEARCH_PARAMETER_ACTIVE = "ff.followsearchparameter.active";

    private static final Logger LOG = LogManager.getLogger(AbstractSearchPageController.class);

    private static final String BLANK_STRING = "";

    private static final String DEFAULT_MIN_PAGE_SIZE = "default.minimum.page.size";

    private static final String OPTED_PAGE_SIZE = "optedPageSize";

    @Autowired
    private DistRelatedDataFacade distRelatedDataFacade;

    protected void setFFfollowSearchParameter(final Boolean value) {
        getSessionService().setAttribute(FF_FOLLOW_SEARCH_PARAMETER_ACTIVE, value);
    }

    @Deprecated // Use constructors
    protected DistOrderHistoryPageableData createPageableData(final int pageNumber, final int pageSize, final String sortCode, final ShowMode showMode) {
        return createPageableData(pageNumber, pageSize, sortCode, showMode, null);
    }

    protected PageableData createPageableDataWithCookie(final HttpServletRequest request, final HttpServletResponse response, final int pageNumber,
                                                        int pageSize, final String sortCode, final ShowMode showMode) {
        final String shopSettingsString = Attributes.SHOP_SETTINGS.getValueFromCookies(request);
        // when pageSize is zero that means use doesn't have select no of items per page on FE
        // in this case we read value of this parameter from shopSettings cookies
        if (shopSettingsString != null & pageSize == 0) {
            final ShoppingSettingsCookieData cookieData = ShopSettingsUtil.readShopSettingsCookie(shopSettingsString);
            if (cookieData != null && cookieData.getItemsPerPage() != null) {
                pageSize = Integer.parseInt(cookieData.getItemsPerPage());
            }
        } else {
            // Update shopSettings cookies for selected pageSize
            ShopSettingsUtil.updateItemsPerPageShopSettingsCookie(request, response, String.valueOf(pageSize));
        }

        return createPageableData(pageNumber, pageSize, sortCode, showMode);
    }

    // This method is used by only in case of post method as requested by FE
    protected void setNonFilerSearchParameters(final HttpServletRequest request, final Model model) {
        final StringBuilder nonFilterParameters = new StringBuilder();

        for (final String key : request.getParameterMap().keySet()) {
            if (!key.startsWith("filter_")) {
                String query = Encode.forHtmlAttribute(key + "=" + request.getParameter(key));
                if (nonFilterParameters.length() == 0)
                    nonFilterParameters.append(query);
                else
                    nonFilterParameters.append("&" + query);
            }

        }
        model.addAttribute("nonFilterParameters", nonFilterParameters);
    }

    // DISTRELEC-9134: SRP
    protected void createCategoryDisplayData(final Model model, final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData,
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
            createCategoryDisplayData.stream().forEach(categoryDisplayData -> {
                if (model.getAttribute("digitalDataLayerTerm") != null) {
                    String newUrl = updateCategoryDisplayDataUrl((String) model.getAttribute("digitalDataLayerTerm"), categoryDisplayData);
                    categoryDisplayData.setUrl(newUrl);
                }
            });
            model.addAttribute("categoryDisplayDataList", createCategoryDisplayData);
        }
    }

    private String updateCategoryDisplayDataUrl(String digitalDataLayerTerm, CategoryDisplayData<SearchStateData> categoryDisplayData) {
        String oldUrl = categoryDisplayData.getUrl();
        return oldUrl.concat("&digitalDataLayerTerm=" + digitalDataLayerTerm);
    }

    // DISTRELEC-11325: Implement functionality to have breadcrumb when user search for keyword
    protected List<Breadcrumb> createSearchBreadcrumb(final String searchQuery, final Model model, final Boolean removeTitleValue,
                                                      final SearchStateData searchState,
                                                      final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData,
                                                      final ContentPageModel cmsPage, final SearchBreadcrumbBuilder searchBreadcrumbBuilder,
                                                      final String filter_CuratedProducts) {
        if (null != searchPageData.getCategories() && null != searchPageData.getCategories().getValues()
                && searchPageData.getCategories().getHasSelectedElements() == Boolean.TRUE) {
            final List<Breadcrumb> searchBreadcrumbs = searchBreadcrumbBuilder.getSearchBreadcrumbs(searchPageData);

            // Replace * with the CuratedProducts value & make sure that curatedProducts parameter is not removed from URL
            final Breadcrumb curatedProductBreadcrumb = searchBreadcrumbs.get(0);
            if (null != curatedProductBreadcrumb && "*".equals(searchQuery) && StringUtils.isNotBlank(filter_CuratedProducts)) {
                curatedProductBreadcrumb
                                        .setName(StringUtils.replace(curatedProductBreadcrumb.getName(), "*",
                                                                     stripCuratedProductsPrefix(filter_CuratedProducts)));
                final String url = curatedProductBreadcrumb.getUrl();
                String encodedValue;
                encodedValue = URLEncoder.encode(filter_CuratedProducts, Charsets.UTF_8);
                curatedProductBreadcrumb.setUrl(url + "&" + FILTER_CURATED_PRODUCTS_PARAMETER_NAME + "=" + encodedValue);
            }
            return searchBreadcrumbs;
        } else {
            final List<Breadcrumb> breadCrumbList;
            if (BooleanUtils.isTrue(removeTitleValue)) {
                breadCrumbList = Collections.emptyList();
            } else {
                final String breadCrumbValue;
                final String breadCrumbValueForURL;
                final String additionalParam;
                if (StringUtils.isNotBlank(filter_CuratedProducts)) {
                    breadCrumbValue = stripCuratedProductsPrefix(filter_CuratedProducts);
                    breadCrumbValueForURL = searchQuery;
                    additionalParam = "&" + FILTER_CURATED_PRODUCTS_PARAMETER_NAME + "=" + filter_CuratedProducts;
                } else {
                    breadCrumbValue = searchQuery;
                    breadCrumbValueForURL = searchState.getQuery().getValue();
                    additionalParam = StringUtils.EMPTY;
                }
                breadCrumbList = searchBreadcrumbBuilder.getSearchBreadcrumbs(cmsPage.getTitle(),
                                                                              "/search?" + SEARCH_QUERY_PARAMETER_NAME + "=" + breadCrumbValueForURL
                                                                                                  + additionalParam,
                                                                              breadCrumbValue);
            }
            return breadCrumbList;
        }
    }

    /**
     * Removes the ordinal prefix from Curated Products filter value. (e.g. 1_Robotics becomes Robotics)
     *
     * @param filter_CuratedProducts
     * @return
     */
    protected String stripCuratedProductsPrefix(final String filter_CuratedProducts) {
        return StringUtils.substringAfter(filter_CuratedProducts, "_");
    }

    // DISTRELEC-11325: Implement functionality to have ManufacturerBreadcrumbs
    protected void createManufacturerBreadcrumb(final Model model, final String value, final String parentUrl, final String url, final String name,
                                                final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData,
                                                final SearchBreadcrumbBuilder searchBreadcrumbBuilder) {
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, searchBreadcrumbBuilder.getManufacturerBreadcrumbs(value, parentUrl, url, name, searchPageData));
    }

    // DISTRELEC-11325: Implement functionality to have CategodyBreadcrumbs
    protected void createCategoryBreadcrumb(final Model model, final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData,
                                            final CategoryModel category, final SearchBreadcrumbBuilder searchBreadcrumbBuilder) {
        if (searchPageData != null && null != searchPageData.getCategories() && null != searchPageData.getCategories().getValues()
                && searchPageData.getCategories().getHasSelectedElements() == Boolean.TRUE) {
            model.addAttribute(WebConstants.BREADCRUMBS_KEY, searchBreadcrumbBuilder.getCategoryBreadcrumbs(searchPageData));
        } else {
            model.addAttribute(WebConstants.BREADCRUMBS_KEY, searchBreadcrumbBuilder.getBreadcrumbs(category));
        }
    }

    // DISTRELEC-11325: Implement functionality to have new & clearance StroePageBreadcrumbs
    protected void createNewStorePageBreadcrumb(final Model model, final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData,
                                                final ContentPageModel searchPage, final SearchBreadcrumbBuilder searchBreadcrumbBuilder) {
        if (null != searchPageData.getCategories() && null != searchPageData.getCategories().getValues()
                && searchPageData.getCategories().getHasSelectedElements() == Boolean.TRUE) {
            model.addAttribute(WebConstants.BREADCRUMBS_KEY, searchBreadcrumbBuilder.getStoreBreadCrum(searchPageData, searchPage.getTitle(),
                                                                                                       searchPage.getTitle(Locale.ENGLISH),
                                                                                                       searchPage.getLabel()));
        } else {
            model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                               searchBreadcrumbBuilder.getGeneralBreadcrumbs(searchPage.getTitle(), searchPage.getTitle(Locale.ENGLISH),
                                                                             searchPage.getLabel()));

        }
    }

    /**
     * Populated Data Layer data
     *
     * @param searchQuery
     * @param searchPageData
     * @param model
     * @param oldOnly
     */
    protected void populateDataLayerForProducts(final String searchQuery, final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData,
                                                final Model model, final boolean oldOnly) {
        if (CollectionUtils.isNotEmpty(searchPageData.getResults())) {
            if (!oldOnly && isDatalayerEnabled()) { // Populate DTM Elements
                final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
                try {
                    populateDTMSearchData(digitalDatalayer,
                                          model.getAttribute("digitalDataLayerTerm") != null ? (String) model.getAttribute("digitalDataLayerTerm")
                                                                                             : searchQuery,
                                          searchPageData);
                    digitalDatalayer.setProduct(getProductsDTMDataLayer(searchPageData.getResults()));
                    model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
                } catch (final Exception ex) {
                    LOG.error("Exception while populating DTM product attributes in search page", ex);
                }
            }
        }
    }

    /**
     * Calculate the allowed page size.
     *
     * @param pageSize
     *            the submitted page size.
     * @return the new value of the page size.
     */
    protected int calculatePageSize(final int pageSize) {
        final Configuration configuration = getConfigurationService().getConfiguration();
        final int defaultPageSize = Integer.parseInt(configuration.getString(DEFAULT_MIN_PAGE_SIZE));
        final int MAX_PAGE_SIZE = Math.max(DEFAULT_SEARCH_MAX_SIZE,
                                           getConfigurationService().getConfiguration().getInt("limit.availability.to", DEFAULT_SEARCH_MAX_SIZE));
        return Math.min((pageSize > 0 ? pageSize : defaultPageSize), MAX_PAGE_SIZE);
    }

    protected int calculatePageSize(final int pageSize, final HttpServletRequest request, final HttpServletResponse response) {
        final int calculatedPageSize = calculatePageSize(pageSize);
        final Configuration configuration = getConfigurationService().getConfiguration();
        final int defaultPageSize = Integer.parseInt(configuration.getString(DEFAULT_MIN_PAGE_SIZE));
        if (pageSize == 0) {
            return defaultPageSize;
        } else if (pageSize > 0) {
            ShopSettingsUtil.updateItemsPerPageShopSettingsCookie(request, response, String.valueOf(calculatedPageSize));
            return calculatedPageSize;
        } else {
            ShoppingSettingsCookieData shoppSettingsCookieData = getShopSettingsCookie(request);
            if (shoppSettingsCookieData != null && shoppSettingsCookieData.getItemsPerPage() != null) {
                return Integer.parseInt(shoppSettingsCookieData.getItemsPerPage());
            } else {
                ShopSettingsUtil.updateItemsPerPageShopSettingsCookie(request, response, String.valueOf(defaultPageSize));
            }
        }
        return defaultPageSize;
    }

    /**
     * Create a new instance of {@code PageableData} and set fields from the specified data
     *
     * @param pageNumber
     *            the page number
     * @param pageSize
     *            the page size
     * @param sortCode
     *            the sort code
     * @param showMode
     *            the show mode
     * @param sortType
     * @return a new instance of {@code DistOrderHistoryPageableData}
     */
    protected DistOrderHistoryPageableData createPageableData(final int pageNumber, final int pageSize, final String sortCode, final ShowMode showMode,
                                                              final String sortType) {

        final int actualPageSize = ShowMode.All == showMode ? DEFAULT_SEARCH_MAX_SIZE : pageSize;

        return new DistOrderHistoryPageableData(pageNumber, actualPageSize, sortCode, sortType);
    }

    protected PageableData createSearchPageableDataWithCookie(final HttpServletRequest request, final HttpServletResponse response, final int pageNumber,
                                                              int pageSize, final String sortCode, final ShowMode showMode, final boolean technicalView) {
        final String shopSettingsString = Attributes.SHOP_SETTINGS.getValueFromCookies(request);
        // when pageSize is zero that means use doesn't have select no of items per page on FE
        // in this case we read value of this parameter from shopSettings cookies
        if (shopSettingsString != null & pageSize == 0) {
            final ShoppingSettingsCookieData cookieData = ShopSettingsUtil.readShopSettingsCookie(shopSettingsString);
            if (cookieData != null && cookieData.getItemsPerPage() != null) {
                pageSize = Integer.parseInt(cookieData.getItemsPerPage());
            }
        } else {
            // Update shopSettings cookies for selected pageSize
            ShopSettingsUtil.updateItemsPerPageShopSettingsCookie(request, response, String.valueOf(pageSize));
        }

        return createSearchPageableData(pageNumber, pageSize, sortCode, showMode, null, technicalView);
    }

    protected PageableData createSearchPageableData(final int pageNumber, final int pageSize, final String sortCode, final ShowMode showMode,
                                                    final String sortType, final boolean technicalView) {
        final SearchPageableData pageableData = new SearchPageableData();
        pageableData.setCurrentPage(pageNumber);
        pageableData.setSort(sortCode);
        pageableData.setSortType(sortType);
        pageableData.setTechnicalView(technicalView);
        if (ShowMode.All == showMode) {
            pageableData.setPageSize(DEFAULT_SEARCH_MAX_SIZE);
        } else {
            pageableData.setPageSize(pageSize);
        }
        return pageableData;
    }

    /**
     * Create a new instance of {@code PageableData} and set fields from the specified data
     *
     * @param pageNumber
     *            the page number
     * @param orderNumber
     *            the order number
     * @param pageSize
     *            the page size
     * @param sortCode
     *            the sort code
     * @param showMode
     *            the show mode
     * @param fromDate
     *            the date from which the history starts
     * @param toDate
     *            the date to which the history ends
     * @return a new instance of {@code PageableData}
     */
    protected DistOrderHistoryPageableData createPageableData(final String orderNumber, final String contactId, final int pageNumber, final int pageSize,
                                                              final String sortCode,
                                                              final ShowMode showMode, final String fromDate, final String toDate) {
        return createPageableData(new OrderHistoryForm(orderNumber, contactId, pageNumber, pageSize, showMode, sortCode, fromDate, toDate));
    }

    /**
     * Create a new instance of {@code PageableData} and set fields from the {@code OrderHistoryForm} This is used for search open orders
     *
     * @param orderHistoryForm
     * @return a new instance of {@code PageableData}
     */
    protected DistOrderHistoryPageableData createOpenOrderPageableData(final OrderHistoryForm orderHistoryForm) {
        final DistOrderHistoryPageableData pageableData = new DistOrderHistoryPageableData();
        pageableData.setCurrentPage(orderHistoryForm.getPage());

        if (ShowMode.All == orderHistoryForm.getShow()) {
            pageableData.setPageSize(DEFAULT_SEARCH_MAX_SIZE);
        } else {
            pageableData.setPageSize(orderHistoryForm.getPageSize());
        }

        // Status
        pageableData.setStatus("ALL");

        return pageableData;
    }

    /**
     * Create a new instance of {@code PageableData} and set fields from the {@code OrderHistoryForm}
     *
     * @param orderHistoryForm
     * @return a new instance of {@code PageableData}
     */
    protected DistOrderHistoryPageableData createPageableData(final OrderHistoryForm orderHistoryForm) {
        final DistOrderHistoryPageableData pageableData = new DistOrderHistoryPageableData();
        pageableData.setCurrentPage(orderHistoryForm.getPage());
        pageableData.setSort(orderHistoryForm.getSort());

        if (ShowMode.All == orderHistoryForm.getShow()) {
            pageableData.setPageSize(DEFAULT_SEARCH_MAX_SIZE);
        } else {
            pageableData.setPageSize(orderHistoryForm.getPageSize());
        }

        pageableData.setOrderNumber(orderHistoryForm.getOrderNumber() != null ? orderHistoryForm.getOrderNumber().toUpperCase().replace('*', '%') : null);
        if (StringUtils.isEmpty(orderHistoryForm.getContactId())) {
            orderHistoryForm.setContactId(getUser().getContactId());
        }

        if (StringUtils.isNotEmpty(orderHistoryForm.getFilterContactId())) {
            pageableData.setFilterContactId(orderHistoryForm.getFilterContactId());
        }

        if (StringUtils.isNotEmpty(orderHistoryForm.getFromDate())) {
            try {
                pageableData.setFromDate(parseLocalSpecificDate(orderHistoryForm.getFromDate()));
            } catch (final ParseException e) {
                LOG.warn("Exception occurred during date parsing", e);
            }
        }

        if (StringUtils.isNotEmpty(orderHistoryForm.getToDate())) {
            try {
                pageableData.setToDate(parseLocalSpecificDate(orderHistoryForm.getToDate()));
            } catch (final ParseException e) {
                LOG.warn("Exception occurred during date parsing", e);
            }
        }

        if (isCompanyOrderAdminUser()) {
            pageableData.setContactId(orderHistoryForm.getContactId());
        } else {
            pageableData.setContactId(getUser().getContactId());
        }

        pageableData.setSortType(orderHistoryForm.getSortType());
        // additional filter for sap implementation
        pageableData.setFilterCurrencyCode(orderHistoryForm.getCurrencyCode());
        pageableData.setInvoiceNumber(orderHistoryForm.getInvoiceNumber());
        pageableData.setMaxTotal(orderHistoryForm.getMaxTotal());
        pageableData.setMinTotal(orderHistoryForm.getMinTotal());
        pageableData.setStatus(orderHistoryForm.getStatus());
        pageableData.setReference(orderHistoryForm.getReference());
        pageableData.setProductNumber(orderHistoryForm.getProductNumber());
        return pageableData;
    }

    protected boolean isCompanyOrderAdminUser() {
        final B2BCustomerModel currentUser = (B2BCustomerModel) getUserService().getCurrentUser();
        return (getCurrentSalesOrg().isAdminManagingSubUsers() && getUser().isAdminUser()) || getCurrentSalesOrg().isOrderVisibleToAll()
                || currentUser.isShowAllOrderhistory();
    }

    /**
     * Create a new instance of {@code PageableData} and set fields from the {@code OrderHistoryForm}
     *
     * @param quotationHistoryForm
     * @return a new instance of {@code PageableData}
     */
    protected QuotationHistoryPageableData createPageableData(final QuotationHistoryForm quotationHistoryForm) {
        final QuotationHistoryPageableData pageableData = new QuotationHistoryPageableData();
        pageableData.setCurrentPage(quotationHistoryForm.getPage());
        pageableData.setSort(quotationHistoryForm.getSort());
        pageableData.setPageSize(ShowMode.All == quotationHistoryForm.getShow() ? DEFAULT_SEARCH_MAX_SIZE : quotationHistoryForm.getPageSize());
        pageableData.setQuotationId(quotationHistoryForm.getQuotationId());
        // DISTRELEC-11421 by default we set what we receive from the FE, this will be checked before sending the request to the ERP.
        pageableData.setContactId(quotationHistoryForm.getContactId());
        pageableData.setFilterArticleNumber(normalizeProductCode(quotationHistoryForm.getArticleNumber()));
        pageableData.setQuotationReference(quotationHistoryForm.getQuotationReference());
        pageableData.setFromDate(quotationHistoryForm.getFromDate());
        pageableData.setToDate(quotationHistoryForm.getToDate());
        pageableData.setFilterExpiryFromDate(quotationHistoryForm.getExpiryFromDate());
        pageableData.setFilterExpiryToDate(quotationHistoryForm.getExpiryToDate());
        pageableData.setSortType(quotationHistoryForm.getSortType());
        // additional filter for sap implementation
        pageableData
                    .setFilterCurrencyCode(quotationHistoryForm.getCurrencyCode() == null ? getCmsSiteService().getCurrentSite().getDefaultCurrency()
                                                                                                               .getIsocode()
                                                                                          : quotationHistoryForm.getCurrencyCode());
        pageableData.setMaxTotal(quotationHistoryForm.getMaxTotal());
        pageableData.setMinTotal(quotationHistoryForm.getMinTotal());
        pageableData.setStatus(quotationHistoryForm.getStatus());

        return pageableData;
    }

    /**
     * Create a new instance of {@code PageableData} and set fields from the {@code InvoiceHistoryForm}
     *
     * @param invoiceHistoryForm
     * @return a new instance of {@code PageableData}
     */
    protected DistInvoiceHistoryPageableData createPageableData(final InvoiceHistoryForm invoiceHistoryForm) {
        final DistInvoiceHistoryPageableData pageableData = new DistInvoiceHistoryPageableData();
        pageableData.setCurrentPage(invoiceHistoryForm.getPage());
        pageableData.setSort(invoiceHistoryForm.getSort());

        if (ShowMode.All == invoiceHistoryForm.getShow()) {
            pageableData.setPageSize(DEFAULT_SEARCH_MAX_SIZE);
        } else {
            pageableData.setPageSize(invoiceHistoryForm.getPageSize());
        }

        pageableData.setOrderNumber(invoiceHistoryForm.getOrderNumber());
        pageableData.setInvoiceNumber(invoiceHistoryForm.getInvoiceNumber());
        if (!getCurrentSalesOrg().isInvoiceVisibleToAll()) {
            pageableData.setContactId(getUser().getContactId());
        } else {
            pageableData.setContactId(invoiceHistoryForm.getContactId());
        }
        pageableData.setFromDate(invoiceHistoryForm.getFromDate());
        pageableData.setToDate(invoiceHistoryForm.getToDate());
        pageableData.setMinTotal(invoiceHistoryForm.getMinTotal().doubleValue());
        pageableData.setMaxTotal(invoiceHistoryForm.getMaxTotal().doubleValue());
        pageableData.setStatus(invoiceHistoryForm.getStatus());
        pageableData.setSortType(invoiceHistoryForm.getSortType());

        return pageableData;
    }

    /**
     * Create a new instance of {@code PageableData} and set fields from the {@code InvoiceHistoryForm}
     *
     * @param invoiceHistoryForm
     * @return a new instance of {@code PageableData}
     */
    protected DistOnlineInvoiceHistoryPageableData createPageableDataIF12(final InvoiceHistoryForm invoiceHistoryForm) {
        final DistOnlineInvoiceHistoryPageableData pagableData = new DistOnlineInvoiceHistoryPageableData();

        final int pageSize = (ShowMode.All == invoiceHistoryForm.getShow()) ? DEFAULT_SEARCH_MAX_SIZE : invoiceHistoryForm.getPageSize();
        pagableData.setPageSize(pageSize);

        final int offset = (invoiceHistoryForm.getPage() * pageSize);
        pagableData.setResultOffset(offset);
        pagableData.setSalesOrganisation(getCurrentSalesOrg().getCode());
        pagableData.setCustomerID(getUser().getUnit().getErpCustomerId());
        pagableData.setSort(invoiceHistoryForm.getSort());
        final boolean isAscendingSort = invoiceHistoryForm.getSortType()
                                                          .equalsIgnoreCase("asc");
        pagableData.setSortAscending(isAscendingSort);
        pagableData.getSalesOrderNumbers().add(invoiceHistoryForm.getOrderNumber());
        pagableData.getInvoiceNumbers().add(invoiceHistoryForm.getInvoiceNumber());

        final String salesOrderReferenceNumber = (null == invoiceHistoryForm.getOrdernf()) ? BLANK_STRING : invoiceHistoryForm.getOrdernf();
        pagableData.getSalesOrderReferenceNumbers().add(salesOrderReferenceNumber);
        pagableData.getInvoicesContainingArticle().add(invoiceHistoryForm.getArticleNumber());

        final String invoiceStatusType = (null == invoiceHistoryForm.getStatus()) ? "00" : invoiceHistoryForm.getStatus();
        pagableData.setInvoiceStatusType(invoiceStatusType);

        String contactId = null;
        if (!StringUtils.isEmpty(invoiceHistoryForm.getContactId())) {
            contactId = invoiceHistoryForm.getContactId();
        } else {
            if (!isCompanyInvoiceAdminUser()) {
                contactId = getUser().getContactId();
            }
        }

        pagableData.getContactPersonIDs().add(contactId);
        pagableData.setDueDateFrom(invoiceHistoryForm.getFromDueDate());
        pagableData.setDueDateTo(invoiceHistoryForm.getToDueDate());

        pagableData.setCurrentPage(invoiceHistoryForm.getPage());
        pagableData.setSortCriteriaType(getSortType(invoiceHistoryForm.getSort()));
        pagableData.setInvoiceDateFrom(invoiceHistoryForm.getFromDate());
        pagableData.setInvoiceDateTo(invoiceHistoryForm.getToDate());

        pagableData.setTotalAmountFrom(invoiceHistoryForm.getMinTotal());
        pagableData.setTotalAmountTo(invoiceHistoryForm.getMaxTotal());

        return pagableData;
    }

    protected boolean isCompanyInvoiceAdminUser() {
        final B2BCustomerModel currentUser = (B2BCustomerModel) getUserService().getCurrentUser();
        return (getCurrentSalesOrg().isAdminManagingSubUsers() && getUser().isAdminUser())
                || getCurrentSalesOrg().isInvoiceVisibleToAll()
                || currentUser.isShowAllOrderhistory();
    }

    private SortCriteriaType getSortType(final String sortTypeOnForm) {
        SortCriteriaType selectedSort = null;
        switch (sortTypeOnForm.toLowerCase()) {
            case "bydate":
                selectedSort = SortCriteriaType.INVOICE_DATE;
                break;
            case "bystatus":
                selectedSort = SortCriteriaType.INVOICE_STATUS;
                break;
            case "bytotalprice":
                selectedSort = SortCriteriaType.INVOICE_TOTAL;
                break;
            default:
                selectedSort = SortCriteriaType.INVOICE_DATE;
                break;
        }
        return selectedSort;
    }

    protected SearchStateData createSearchStateData(final HttpServletRequest request, final String searchQuery) {
        final SearchStateData searchState = new SearchStateData();
        // maybe one could do something more generic with {@link ServletRequestParameterPropertyValues} ?
        final String filterParams = WebUtil.getQueryParamsStartingWith(request, searchQuery, FILTER);
        final SearchQueryData queryData = new SearchQueryData();
        queryData.setValue(filterParams);
        searchState.setQuery(queryData);
        SearchExperience searchExperience = getProductSearchFacade().getSearchExperienceFromCurrentBaseStore();
        searchState.setSearchExperience(searchExperience);
        return searchState;
    }

    protected boolean isShowAllAllowed(final SearchPageData<?> searchPageData) {
        return searchPageData.getPagination().getNumberOfPages() > 1 && searchPageData.getPagination().getTotalNumberOfResults() < DEFAULT_SEARCH_MAX_SIZE;
    }

    protected void storeSearchResultToModel(final Model model, final SearchPageData<?> searchPageData, final ShowMode showMode) {
        if (!(searchPageData instanceof FactFinderProductSearchPageData)) {
            model.addAttribute("ffsearchPageData", new FactFinderProductSearchPageData<SearchStateData, ProductData>());
        }
        model.addAttribute("searchPageData", searchPageData);
        model.addAttribute("isShowAllAllowed", calculateShowAll(searchPageData, showMode));
        model.addAttribute("isShowPageAllowed", calculateShowPaged(searchPageData, showMode));
    }

    protected void setRemoveFilersURL(final FactFinderFacetSearchPageData<?, ?> searchPageData, final HttpServletRequest request) {
        if (searchPageData == null) {
            return;
        }
        final boolean isFiltered = isFiltered(request);
        final StringBuilder requestURL = new StringBuilder(request.getRequestURI()).append("?page=1");
        final Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            final String name = paramNames.nextElement();
            if ((isFiltered && name.startsWith("filter_Category")) || (!name.startsWith("filter_") && !"page".equals(name))) {
                requestURL.append('&').append(XSSFilterUtil.filterUsingRules(name)).append('=').append(request.getParameter(name));
            }
        }
        if (isFiltered) {
            requestURL.append("&f=2");
        }
        searchPageData.setRemoveFiltersURL(Encode.forHtmlAttribute(requestURL.toString()));
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

    protected String getPortraitMediumUrl(Map<String, ImageData> imageMap, CMSSiteModel cmsSiteModel) {
        if (imageMap.containsKey(PORTRAIT_MEDIUM_WEBP) || imageMap.containsKey(PORTRAIT_MEDIUM)) {
            return imageMap.getOrDefault(PORTRAIT_MEDIUM_WEBP, imageMap.get(PORTRAIT_MEDIUM)).getUrl();
        }
        return null;
    }

    /**
     * Populate the pagination data with the correct values and populate the model.
     *
     * @param searchURL
     *            the search query URL
     * @param paginationData
     *            the pagination data
     * @param model
     *            the model to populate
     */
    protected void updatePagination(final String searchURL, final DistPaginationData paginationData, final Model model, final boolean zeroBased) {
        String baseSearchURL = searchURL.replaceAll("&?page(Size)?=[0-9]+", StringUtils.EMPTY).replace("?&", "?");
        if (baseSearchURL.indexOf('?') < 0) {
            baseSearchURL += '?';
        }
        baseSearchURL = Encode.forHtmlAttribute(baseSearchURL);

        final StringBuilder searchURLPrefixBuilder = new StringBuilder(baseSearchURL).append(baseSearchURL.charAt(baseSearchURL.length() - 1) == '?' ? "" : "&")
                                                                                     .append("pageSize=").append(paginationData.getPageSize());
        // Putting the baseSearchURL in the model before appending the "page" parameter.
        model.addAttribute("baseSearchURL", searchURLPrefixBuilder.toString());
        final String searchURLPrefix = searchURLPrefixBuilder.append("&page=").toString();

        final int x = zeroBased ? 0 : 1;
        final int y = zeroBased ? 1 : 0;
        if (paginationData.getCurrentPage() <= x) {
            paginationData.setCurrentPage(x);
            paginationData.setPrevPageNr(paginationData.getCurrentPage());
            paginationData.setNextPageNr(paginationData.getCurrentPage() + 1);
        } else if (paginationData.getCurrentPage() >= paginationData.getNumberOfPages() - y) {
            paginationData.setCurrentPage(paginationData.getNumberOfPages() - y);
            paginationData.setNextPageNr(paginationData.getCurrentPage());
            paginationData.setPrevPageNr(paginationData.getCurrentPage() - 1);
        } else {
            paginationData.setPrevPageNr(paginationData.getCurrentPage() - 1);
            paginationData.setNextPageNr(paginationData.getCurrentPage() + 1);
        }

        // Setting the first page URL
        paginationData.setFirstUrl(searchURLPrefix + x);
        // Setting the last page URL
        paginationData.setLastUrl(searchURLPrefix + (paginationData.getNumberOfPages() - y));
        // Setting the previous page URL
        paginationData.setPrevUrl(searchURLPrefix + paginationData.getPrevPageNr());
        // Setting the next page URL
        paginationData.setNextUrl(searchURLPrefix + paginationData.getNextPageNr());
    }

    protected FactFinderProductSearchPageData<SearchStateData, ProductData> createEmptySearchPageData() {
        FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData;
        searchPageData = new FactFinderProductSearchPageData<>();
        final FactFinderPaginationData pagination = new FactFinderPaginationData();
        pagination.setNumberOfPages(0);
        pagination.setCurrentPage(0);
        pagination.setTotalNumberOfResults(0);
        searchPageData.setPagination(pagination);
        return searchPageData;
    }

    protected Boolean calculateShowAll(final SearchPageData<?> searchPageData, final ShowMode showMode) {
        return Boolean
                      .valueOf((showMode != ShowMode.All
                              && searchPageData.getPagination().getTotalNumberOfResults() > searchPageData.getPagination().getPageSize())
                              && isShowAllAllowed(searchPageData));
    }

    protected Boolean calculateShowPaged(final SearchPageData<?> searchPageData, final ShowMode showMode) {
        return Boolean.valueOf(showMode == ShowMode.All
                && (searchPageData.getPagination().getNumberOfPages() > 1 || searchPageData.getPagination().getPageSize() == DEFAULT_SEARCH_MAX_SIZE));
    }

    protected void switchTechnicalViewIfNeeded(final Boolean useTechnicalView, final Model model, final HttpServletRequest request,
                                               final HttpServletResponse response) {
        // set cookie and session;
        getStoreSessionFacade().setUseTechnicalView(BooleanUtils.isTrue(useTechnicalView));
        getStoreSessionFacade().setUseListView(
                                               useTechnicalView == null || !useTechnicalView.booleanValue());
        addSearchModelAttributes(model, request);
    }

    @SuppressWarnings("unused")
    protected void switchAutoApplyFilterIfNeeded(final Boolean autoApplyFilter, final Model model, final HttpServletRequest request,
                                                 final HttpServletResponse response) {
        if (autoApplyFilter != null) {
            // set cookie and session;
            ShopSettingsUtil.updateAutoApplyFilter(autoApplyFilter, request, response);
            getStoreSessionFacade().setAutoApplyFilter(autoApplyFilter);
        }
    }

    protected Date parseLocalSpecificDate(final String date) throws ParseException {
        return new SimpleDateFormat(getDataFormatForCurrentCmsSite()).parse(date);
    }

    protected Boolean isLastPage(final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData, final int page) {
        return Boolean.valueOf(page >= searchPageData.getPagination().getNumberOfPages());
    }

    protected String getAjaxView() {

        return ControllerConstants.Views.Fragments.Product.ProductsForFacetSearch;
    }

    protected String getShowMoreView() {
        return ControllerConstants.Views.Fragments.Product.ShowMoreProducts;
    }

    protected String getAdditionalFacetView() {
        return ControllerConstants.Views.Fragments.Product.AdditionalFacetForFacetSearch;
    }

    public DistRelatedDataFacade getDistRelatedDataFacade() {
        return distRelatedDataFacade;
    }

    public void setDistRelatedDataFacade(final DistRelatedDataFacade distRelatedDataFacade) {
        this.distRelatedDataFacade = distRelatedDataFacade;
    }

    public enum ShowMode {
        Page,
        All,
        page
    }
}
