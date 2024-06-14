/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product;

import java.util.List;
import java.util.Map;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.DiscountInformation;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.PriceService;

/**
 * DistPriceService.
 * 
 * @author daehusir, Distrelec
 * 
 */
public interface DistPriceService extends PriceService {

    /**
     * Returns all available {@link PriceInformation} for the given {@link ProductModel} and the current session user.
     * 
     * @param model
     *            the product
     * @param onlinePrice
     *            a boolean flag to force the online customer price.
     * @return an empty list if no price information exists for the product and current session user.
     */
    List<PriceInformation> getPriceInformationsForProduct(final ProductModel model, final boolean onlinePrice);

    /**
     * Returns all available {@link PriceInformation} for the given {@link ProductModel} and the current session user.
     * 
     * @param model
     *            the product
     * @param onlinePrice
     *            a boolean flag to force the online customer price.
     * @param force
     * @return an empty list if no price information exists for the product and current session user.
     */
    List<PriceInformation> getPriceInformationsForProduct(final ProductModel model, final boolean onlinePrice, final boolean force);

    /**
     * Returns all available {@link PriceInformation} for the given {@link ProductModel}s and the current session user.
     * 
     * @param models
     *            the list of products
     * @param onlinePrice
     *            a boolean flag to force the online customer price.
     * @param force
     * @return an empty list if no price information exists for the product and current session user.
     */
    Map<String, List<PriceInformation>> getPriceInformationsForProducts(final List<ProductModel> models, final boolean onlinePrice, final boolean force);

    /**
     * Returns all available list {@link PriceInformation} for the given {@link ProductModel} and the current session user.
     * 
     * @param model
     *            the product
     * @return an empty list if no price information exists for the product and current session user.
     */
    List<PriceInformation> getListPriceInformationsForProduct(final ProductModel model);

    List<PriceInformation> getPriceInformationNet(ProductModel model);

    /**
     * Returns all available list {@link PriceInformation} for the given {@link ProductModel} and the current session user.
     * 
     * @param model
     *            the product
     * @param onlinePrice
     * @return an empty list if no price information exists for the product and current session user.
     */
    List<PriceInformation> getListPriceInformationsForProduct(final ProductModel model, final boolean onlinePrice);
    
	/**
     * Returns all available {@link DiscountInformation} for the given {@link ProductModel} and the current session user.
     * 
     * @param model
     *            the product
     * @return an empty list if no discount information exists for the product and current session user.
     */
    List<DiscountInformation> getDiscountInformationsForProduct(final ProductModel model);
}
