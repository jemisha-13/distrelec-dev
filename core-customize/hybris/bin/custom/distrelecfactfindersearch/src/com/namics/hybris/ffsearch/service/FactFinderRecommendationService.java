/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.service;

import java.util.List;

/**
 * Product recommendation interface. Its purpose is to retrieve products which were also bought or considered for a given product.
 * 
 * @param <RESULTS>
 *            Results for converting to a product object data.
 */
public interface FactFinderRecommendationService<RESULTS> {
    
    /**
     * Get products which were also bought with the given product code (a.k.a. "recommended-products" in FactFinder slang, note the
     * difference to "product-campaigns" in FactFinder slang !!!)
     * 
     * @param productCodes
     *            product code to get the also bought for.
     * @return a list of products which were also bought for the given products.
     */
    RESULTS getCartRecommendedProducts(List<String> productCodes);

    /**
     * Get products which were also bought with the given product code (a.k.a. "recommended-products" in FactFinder slang, note the
     * difference to "product-campaigns" in FactFinder slang !!!)
     * 
     * @param productCode
     *            product code to get the also bought for.
     * @return a list of products which were also bought for the given products.
     */
    RESULTS getAlsoBought(String productCode);

}
