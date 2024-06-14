/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Product;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.ManufacturerBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.support.PageTitleResolver;
import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;
import com.namics.hybris.ffsearch.breadcrumb.SearchBreadcrumbBuilder;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.util.XSSFilterUtil;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

/**
 * Controller for new product store.
 *
 * @author pforster, Namics AG
 * @since Distrelec 3.0
 */
@Controller
public abstract class AbstractPromotionStorePageController extends AbstractStoresPageController {

    @Autowired
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Autowired
    private ManufacturerBreadcrumbBuilder manufacturerBreadcrumbBuilder;

    @Autowired
    private PageTitleResolver pageTitleResolver;

    @Autowired
    private DistCategoryFacade distCategoryFacade;

    @Autowired
    private SearchBreadcrumbBuilder searchBreadcrumbBuilder;

    protected String getStoreDetails(final String searchQuery, final int page, final int pageSize, final ShowMode showMode, final String sortCode,
                                     final String requestType, final Boolean useTechnicalView, final Model model, final HttpServletRequest request,
                                     final HttpServletResponse response)
                                                                         throws CMSItemNotFoundException {
        switchTechnicalViewIfNeeded(useTechnicalView, model, request, response);
        String modifiedSearchQuery = "*";
        final String hideCategories = request.getParameter("hide_categories");
        model.addAttribute("hide_categories", Boolean.valueOf("true".equals(hideCategories)));

        final ContentPageModel searchPage = getContentPageForLabelOrId(getStoreCmsPageName());

        // model.addAttribute(WebConstants.BREADCRUMBS_KEY, getBreadcrumbs(searchPage.getTitle(), searchPage.getLabel()));

        final SearchStateData state = createSearchStateData(request, modifiedSearchQuery);
        final PageableData paging = createPageableDataWithCookie(request, response, page, pageSize, sortCode, showMode);

        final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData = getProducts(false, null, state, paging, true);
        // DISTRELEC-9134: SRP
        createCategoryDisplayData(model, searchPageData, distCategoryFacade);
        createNewStorePageBreadcrumb(model, searchPageData, searchPage, searchBreadcrumbBuilder);
        productSearchFacade.addFilterstrings(searchPageData);
        storeSearchResultToModel(model, searchPageData, showMode);
        storeCmsPageInModel(model, searchPage);
        storeContentPageTitleInModel(model, getPageTitleResolver().resolveContentPageTitle(searchPage.getTitle()));
        setMetaRobots(model, searchPage);
        // Setting the URLs of the previous and next pages
        final String searchURL = storeContinueUrl(request);
        updatePagination(searchURL, (FactFinderPaginationData) searchPageData.getPagination(), model, false);
        int lastProductNumber = searchPageData.getPagination().getCurrentPage() * searchPageData.getPagination().getPageSize();
        model.addAttribute("lastProductNumber",
                           isLastPage(searchPageData, page) ? (int) searchPageData.getPagination().getTotalNumberOfResults() : lastProductNumber);
        model.addAttribute("firstProductNumber", lastProductNumber - searchPageData.getPagination().getPageSize() + 1);

        // Setting the remove filter URL
        setRemoveFilersURL(searchPageData, request);
        if (isDatalayerEnabled()) {
            DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);

            getProductsDataLayer(digitalDatalayer, searchPageData.getResults());
            populateDTMSearchData(digitalDatalayer, searchQuery, searchPageData);
            model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        }
        model.addAttribute("searchPageData", searchPageData);

        if (REQUEST_TYPE_AJAX.equals(requestType)) {
            // return search result as JSON
            if (searchPageData.getPagination() != null) {
                model.addAttribute("isLastPage", isLastPage(searchPageData, page));
            }
            return getAjaxView();
        }

        // set meta keywords
        storeMetaKeywordsInModel(model, searchQuery);

        return getViewForPage(searchPage);
    }

    protected String getStoreDetailsCount(final String searchQuery, final int page, final int pageSize, final ShowMode showMode, final String sortCode,
                                          final String requestType, final Boolean useTechnicalView, final Model model, final HttpServletRequest request,
                                          final HttpServletResponse response)
                                                                              throws CMSItemNotFoundException {
        switchTechnicalViewIfNeeded(useTechnicalView, model, request, response);

        final String hideCategories = request.getParameter("hide_categories");
        model.addAttribute("hide_categories", Boolean.valueOf("true".equals(hideCategories)));

        final ContentPageModel searchPage = getContentPageForLabelOrId(getStoreCmsPageName());

        // model.addAttribute(WebConstants.BREADCRUMBS_KEY, getBreadcrumbs(searchPage.getTitle(), searchPage.getLabel()));

        final SearchStateData state = createSearchStateData(request, searchQuery);
        final PageableData paging = createPageableDataWithCookie(request, response, page, pageSize, sortCode, showMode);

        final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData = getProducts(false, null, state, paging, true);
        return String.valueOf(searchPageData.getPagination().getTotalNumberOfResults());
    }

    protected String getShowMoreProducts(final String searchQuery, final int page, final int pageSize, final ShowMode showMode, final String sortCode,
                                         final Model model, final HttpServletRequest request, final HttpServletResponse response) {
        final SearchStateData state = createSearchStateData(request, searchQuery);
        final int calculatedPageSize = calculatePageSize(pageSize, request, response);
        final PageableData paging = createPageableDataWithCookie(request, response, page, calculatedPageSize, sortCode, showMode);
        final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData = getProducts(false, null, state, paging, false);
        // DISTRELEC-9134: SRP
        createCategoryDisplayData(model, searchPageData, distCategoryFacade);
        productSearchFacade.addFilterstrings(searchPageData);
        // Setting the URLs of the previous and next pages
        final String searchURL = storeContinueUrl(request);
        updatePagination(searchURL, (FactFinderPaginationData) searchPageData.getPagination(), model, false);
        int lastProductNumber = searchPageData.getPagination().getCurrentPage() * searchPageData.getPagination().getPageSize();
        model.addAttribute("lastProductNumber",
                           isLastPage(searchPageData, page) ? (int) searchPageData.getPagination().getTotalNumberOfResults() : lastProductNumber);
        model.addAttribute("firstProductNumber", lastProductNumber - searchPageData.getPagination().getPageSize() + 1);

        // Setting the remove filter URL
        setRemoveFilersURL(searchPageData, request);
        if (isDatalayerEnabled()) {
            DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
            getProductsDataLayer(digitalDatalayer, searchPageData.getResults());
            populateDTMSearchData(digitalDatalayer, searchQuery, searchPageData);
            model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        }
        model.addAttribute("searchPageData", searchPageData);
        model.addAttribute("isLastPage", isLastPage(searchPageData, calculatedPageSize));
        model.addAttribute("products", searchPageData.getResults());
        return getShowMoreView();
    }

    protected String getAdditionalFacet(final String searchQuery, final String additionalFacet, final HttpServletRequest request, final Model model) {
        final SearchStateData searchState = createSearchStateData(request, searchQuery);
        final PageableData paging = createPageableData(0, 0, null, null);

        final FactFinderFacetData<SearchStateData> additionalFacetData = productSearchFacade.loadAdditionalFacet(searchState, paging, additionalFacet,
                                                                                                                 null, null);

        model.addAttribute("facet", additionalFacetData);

        return getAdditionalFacetView();
    }

    @Override
    protected abstract String getStoreCmsPageName();

    protected abstract DistSearchType getSearchType();

    // BEGIN Implementation of abstract methods

    @Override
    protected List<Breadcrumb> getBreadcrumbs(final String pageTitle, final String uri) {
        return manufacturerBreadcrumbBuilder.getBreadcrumbs(pageTitle, uri);
    }

    @Override
    protected FactFinderProductSearchPageData<SearchStateData, ProductData> getProducts(final boolean tracking, final String searchString,
                                                                                        final SearchStateData searchState, final PageableData pageableData,
                                                                                        final boolean generateASN) {
        final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData = productSearchFacade.search(tracking, searchState,
                                                                                                                        pageableData, getSearchType(), null,
                                                                                                                        generateASN, FF_LOG_NAME,
                                                                                                                        MapUtils.EMPTY_MAP);
        // DISTRELEC-5176: add filterstrings
        productSearchFacade.addFilterstrings(searchPageData);

        return searchPageData;
    }

    @Override
    protected void storeMetaKeywordsInModel(final Model model, final Object item) {

        if (item instanceof String) {
            final String searchQuery = (String) item;
            final String metaDescription = getMessageSource().getMessage("search.meta.description.results",
                                                                         new Object[] { XSSFilterUtil.filter(searchQuery), getSiteName() },
                                                                         getI18nService().getCurrentLocale());
            setUpMetaData(model, metaDescription);
        }

    }

    @Override
    public PageTitleResolver getPageTitleResolver() {
        return pageTitleResolver;
    }

    @Override
    public void setPageTitleResolver(final PageTitleResolver pageTitleResolver) {
        this.pageTitleResolver = pageTitleResolver;
    }

    private void getProductsDataLayer(final DigitalDatalayer digitalDatalayer, final List<ProductData> results) {
        // DTM Products
        List<Product> products = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(results)) {
            for (final ProductData productData : results) {
                products.add(populateProductDTMObjects(productData));
            }
            if (products.size() > 0) {
                digitalDatalayer.setProduct(products);
            }
        }
    }
    // END GENERATED CODE
}
