/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product;

import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.DiscountInformation;
import de.hybris.platform.jalo.order.price.PriceInformation;

import java.util.List;
import java.util.Map;

/**
 * Extend commerce price service for handling scaled prices.
 *
 * @author dsivakumaran, Namics AG
 */
public interface DistCommercePriceService extends CommercePriceService {

    boolean isOnlinePricingCustomer();

    /**
     * Retrieve the first price returned by ProductItem
     *
     * @param product     the product
     * @param onlinePrice a flag to force taking the online prices from the ERP.
     * @return PriceInformation
     */
    PriceInformation getWebPriceForProduct(final ProductModel product, final boolean onlinePrice);

    /**
     * Retrieve the first price returned by ProductItem
     *
     * @param product     the product
     * @param onlinePrice a flag to force taking the online prices from the ERP.
     * @param force
     * @return PriceInformation
     */
    PriceInformation getWebPriceForProduct(final ProductModel product, final boolean onlinePrice, final boolean force);

    /**
     * Retrieve the price informations for the list of products.
     *
     * @param products    the list of products
     * @param onlinePrice a flag to force taking the online prices from the ERP.
     * @param force
     * @return a list of {@code PriceInformation}
     */
    Map<String, PriceInformation> getWebPriceForProducts(final List<ProductModel> products, final boolean onlinePrice, final boolean force);

    /**
     * Retrieve all discounts by ProductItem
     *
     * @param product the product
     * @return A list of PriceInformation for discounts
     */
    List<DiscountInformation> getWebDiscountsForProduct(ProductModel product);

    /**
     * Returns all scaled prices for this product or an empty list if there is just one scale.
     *
     * @param product the current product
     * @return all scaled prices for this product or an empty list if there is just one scale
     */
    List<PriceInformation> getScaledPriceInformations(ProductModel product);

    /**
     * Returns all scaled prices for this product or an empty list if there is just one scale.
     *
     * @param product the current product
     * @return all scaled prices for this product or an empty list if there is just one scale
     */
    List<PriceInformation> getScaledPriceInformations(final ProductModel product, final boolean onlinePrice);

    /**
     * Returns the list price for this product.
     *
     * @param product the current product
     * @return the list price for this product
     */
    PriceInformation getListWebPriceForProduct(final ProductModel product);

    /**
     * Returns the list price for this product.
     *
     * @param product     the current product
     * @param onlinePrice
     * @return the list price for this product
     */
    PriceInformation getListWebPriceForProduct(final ProductModel product, final boolean onlinePrice);

    PriceInformation getWebPriceForProductAndQuantity(ProductModel product, int quantity);

    PriceInformation getOnlineWebPriceForProductAndQuantity(ProductModel product, boolean onlinePrice, int quantity);

}
