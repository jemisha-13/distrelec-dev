/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.cms;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.cms2.components.DistProductFFCampaignCarouselComponentModel;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import com.namics.distrelec.b2b.facades.webtrekk.DistWebtrekkFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@code DistProductFFCampaignCarouselComponentController}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
@Controller("DistProductFFCampaignCarouselComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistProductFFCampaignCarouselComponent)
public class DistProductFFCampaignCarouselComponentController extends AbstractDistCMSComponentController<DistProductFFCampaignCarouselComponentModel> {

    protected static final int SEARCH_RESULT_LIMIT = 20; // Limit to 20 matching results
    protected static final int SEARCH_RESULT_MIN = 5; // Min 5 matching results
    protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.PROMOTION_LABELS,
            ProductOption.DIST_MANUFACTURER);
    private static final String AUTOPLAY = "autoplay";

    @Autowired
    private DistrelecProductFacade productFacade;

    @Autowired
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Autowired
    @Qualifier("webtrekkFacade")
    private DistWebtrekkFacade distWebtrekkFacade;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistProductFFCampaignCarouselComponentModel component) {
        final List<ProductData> products = collectSearchProducts(model, component);
        model.addAttribute("search", Boolean.TRUE);
        model.addAttribute(AUTOPLAY, Boolean.valueOf(component.getAutoplayTimeout() != null));
        model.addAttribute("productCarouselData", products);
    }

    protected List<ProductData> collectSearchProducts(final Model model, final DistProductFFCampaignCarouselComponentModel component) {
        List<ProductData> products = null;
        final String searchQuery = component.getSearchQuery();
        final Integer maxSearchResults = component.getMaxSearchResults();

        if (StringUtils.isNotBlank(searchQuery)) {
            final SearchStateData searchState = new SearchStateData();
            final SearchQueryData searchQueryData = new SearchQueryData();
            searchQueryData.setValue(searchQuery);
            searchState.setQuery(searchQueryData);

            final PageableData pageableData = new PageableData();
            if (maxSearchResults == null || maxSearchResults.equals(Integer.valueOf(0))) {
                pageableData.setPageSize(SEARCH_RESULT_LIMIT);
            } else {
                pageableData.setPageSize(maxSearchResults.intValue());
            }

            products = productSearchFacade.search(searchState, pageableData).getResults();
        }

        // Test if we have enough products
        if (CollectionUtils.isNotEmpty(products) && products.size() >= SEARCH_RESULT_MIN) {
            distWebtrekkFacade.addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_FAF_ONSITE);
        }

        return products != null ? products : Collections.EMPTY_LIST;
    }

    public DistrelecProductFacade getProductFacade() {
        return productFacade;
    }

    public void setProductFacade(final DistrelecProductFacade productFacade) {
        this.productFacade = productFacade;
    }
}
