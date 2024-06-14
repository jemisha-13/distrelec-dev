/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters.populator;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;

/**
 * {@code DistProductOnlinePricePopulator}
 * <p>
 * Additionally populates the online list price of the product.
 * </p>
 *
 * @param <SOURCE>
 *            extends ProductModel
 * @param <TARGET>
 *            extends ProductData
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.0
 */
public class DistProductOnlinePricePopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends DistProductPricePopulator<SOURCE, TARGET> {

    @Override
    protected PriceInformation getPriceInformation(final ProductModel productModel) {
        return getCommercePriceService().getWebPriceForProduct(productModel, true);
    }
}
