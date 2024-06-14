/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.reco;

import java.util.List;

import com.namics.hybris.ffsearch.data.campaign.FactFinderFeedbackTextData;

import de.hybris.platform.commercefacades.product.data.ProductData;

/**
 * Recommendation facade interface. Used to retrieve "recommended" and "also-bought" products of type {@link ProductData}.
 * 
 * @param <ITEM>
 *            Product data object.
 * @param <TEXT>
 *            Teedback text data object.
 */
public interface DistRecommendationFacade<ITEM extends ProductData, TEXT extends FactFinderFeedbackTextData> {

    /**
     * Manually boosted (= recommended) products as defined in the FactFinder instance.
     * 
     * @param productCode
     *            product code to get recommendations for.
     * @return A collection of recommended products of type {@link ProductData}
     */
    List<ITEM> getCartRecommendedProducts(List<String> productCodes);

    /**
     * Manually boosted (= recommended) products as defined in the FactFinder instance.
     * 
     * @param productCode
     *            product code to get recommendations for.
     * @return A collection of recommended products of type {@link ProductData}
     */
    List<ITEM> getRecommendedProducts(String productCode);

    /**
     * Feedback texts as defined in the FactFinder instance.
     * 
     * @param productCode
     *            product code to feedback for.
     * @return A collection of recommended products of type {@link ProductData}
     */
    List<TEXT> getFeedbackTexts(String productCode);

    /**
     * "Also-bought" products as defined by the gathered tracking data in the FactFinder instance for the given product codes.
     * 
     * @param productCode
     *            product code to also-bought products for.
     * @return A collection of recommended products of type {@link ProductData}
     */
    List<ITEM> getAlsoBoughtProducts(String productCode);
}
