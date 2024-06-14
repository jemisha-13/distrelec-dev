/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.service;

import java.util.List;

/**
 * Product campaign interface. Its purpose is to retrieve campaigns for products with the given id.
 * 
 * @param <RESULTS>
 *            Results for converting to a product object data.
 * @param <FEEDBACK>
 *            Feedback text object data.
 */
public interface FactFinderCampaignService<RESULTS, FEEDBACK> {
    
    /**
     * Get the manually recommended products (a.k.a. "pushed-products" in FactFinder slang, note the difference to "recommended-products" in
     * FactFinder slang!!!) for the given product code from FactFinder.
     * 
     * @param productCode
     *            product code to get the pushed products for.
     * @return a list of results for pushed products.
     */
    RESULTS getCartPushedProducts(List<String> productCodes);

    /**
     * Get the manually recommended products (a.k.a. "pushed-products" in FactFinder slang, note the difference to "recommended-products" in
     * FactFinder slang!!!) for the given product code from FactFinder.
     * 
     * @param productCode
     *            product code to get the pushed products for.
     * @return a list of results for pushed products.
     */
    RESULTS getPushedProducts(String productCode);

    /**
     * Get the manually maintained feedback texts (e.g. banner html) for a product from FactFinder.
     * 
     * @param productCode
     *            product code to get feedback texts (e.g. banner html) for.
     * @return a list of feedback texts
     */
    FEEDBACK getFeedbackTexts(String productCode);

}
