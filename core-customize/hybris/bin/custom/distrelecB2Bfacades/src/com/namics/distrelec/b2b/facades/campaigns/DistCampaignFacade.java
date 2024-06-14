/*
 * Copyright 2000-2015 DISTRELEC. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.campaigns;

import java.util.List;

import de.hybris.platform.commercefacades.product.data.ProductData;

/**
 * Campaign facade interface. Used to retrieve campaign products of type {@link ProductData}.
 * 
 * @param <ITEM>
 *            Product data object.
 */
public interface DistCampaignFacade<ITEM extends ProductData> {

    /**
     * Campaign products as defined in the FactFinder instance for <code>productCode</code>.
     * 
     * @param productCode
     *            product code to which campaign has been assigned.
     * @return A collection of campaign products of type {@link ProductData}
     */
    List<ITEM> getCartCampaignProducts(List<String> productCode);

    /**
     * Campaign products as defined in the FactFinder instance for <code>productCode</code>.
     * 
     * @param productCode
     *            product code to which campaign has been assigned.
     * @return A collection of campaign products of type {@link ProductData}
     */
    List<ITEM> getCampaignProducts(String productCode);
}
