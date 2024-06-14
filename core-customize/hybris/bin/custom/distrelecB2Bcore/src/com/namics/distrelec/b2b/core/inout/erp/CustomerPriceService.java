/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hybris.platform.commercefacades.product.data.ProductPriceData;
import de.hybris.platform.jalo.order.price.PriceInformation;

/**
 * Customer Price Service
 * 
 * @author francesco, Distrelec AG
 * @since Distrelec Extensions 1.0
 * 
 */
public interface CustomerPriceService {

    public Set<ProductPriceData> getPricesForPricesList(final String userId, final String salesOrg, final String currency, final String productCode,
            final List<PriceInformation> pricesList);

    /**
     * TODO write documentation
     * 
     * @param userId
     * @param salesOrg
     * @param currency
     * @param productCode
     * @param minQuantity
     * @return
     */
    public Set<ProductPriceData> getOnlinePriceList(final String userId, final String salesOrg, final String currency, final String productCode,
            final Long minQuantity);

    /**
     * TODO write documentation
     * 
     * @param userId
     * @param salesOrg
     * @param currency
     * @param productCodesAndMinQuantities
     * @return
     */
    public Set<ProductPriceData> getOnlinePriceList(final String userId, final String salesOrg, final String currency,
            final Map<String, Long> productCodesAndMinQuantities);

}
