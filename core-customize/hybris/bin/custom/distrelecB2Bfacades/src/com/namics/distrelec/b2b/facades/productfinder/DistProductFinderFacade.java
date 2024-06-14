/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder;

import com.namics.distrelec.b2b.facades.productfinder.data.DistProductFinderData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;

/**
 * Product finder facade.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public interface DistProductFinderFacade {

    String REFINE_PAGE_PARAM = "refine";
    String RESULT_PAGE_PARAM = "result";

    DistProductFinderData getProductFinderData(CategoryModel category, FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData,
            boolean refine);

    void updateProductFinderData(DistProductFinderData productFinderData);

    String getProductFinderRefineSearchUrl(FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData);

}
