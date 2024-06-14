/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product;

import java.math.BigDecimal;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.jalo.order.price.PriceInformation;

public interface DistPriceDataFactory extends PriceDataFactory {

    /**
     * Creates a PriceData object with a formatted currency string based on the price type and currency ISO code.
     *
     * @param priceType
     *            The price type
     * @param value
     *            The price amount
     * @param currencyIso
     *            The currency ISO code
     * @param minQtd
     * @return the price data
     */
    PriceData create(final PriceDataType priceType, final BigDecimal value, final String currencyIso, final long minQtd);

    /**
     * Formats the given price as defined in the currency model.
     *
     * @param price
     *            the price
     * @param currencyIso
     *            iso code of the currency
     * @return a formatted price (e. g. 12.60)
     */
    String formatPriceValue(final BigDecimal price, final String currencyIso);

    /**
     * Creates a PriceData object with a formatted currency string based on the price type and currency ISO code. It also includes the
     * original and the discounted price if a discount was set.
     *
     * @param priceType
     *            The price type
     * @param value
     *            The price amount incl. discounts
     * @param originalValue
     *            The price amount excl. discount
     * @param currencyIso
     *            The currency ISO code
     *
     *
     * @return the price data
     */
    PriceData create(final PriceDataType priceType, final BigDecimal value, final BigDecimal originalValue, final String currencyIso);

    /**
     * Creates a PriceData object with a formatted value.
     *
     * @param value
     *            the price
     * @return the price data
     */
    PriceData create(final BigDecimal value);

    /**
     * Creates a PriceData object with a formatted value, formatted with a given maximum fraction digits
     *
     * @param value
     *            the price
     * @param maxFractionDigits
     *            explicitly maximum fraction digits
     * @return the price data
     */
    PriceData create(BigDecimal value, int maxFractionDigits);

    /**
     *
     * @param priceType
     * @param info
     * @return the price data
     */
    PriceData create(final PriceDataType priceType, final PriceInformation info);

    PriceData createWithoutCurrency(PriceDataType priceType, BigDecimal value, String currencyIso);

}
