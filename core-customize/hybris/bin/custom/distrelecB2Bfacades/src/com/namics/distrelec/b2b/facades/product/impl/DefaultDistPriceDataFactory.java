/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.impl;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.jalo.DistPriceRow;
import com.namics.distrelec.b2b.facades.product.DistPriceDataFactory;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.impl.DefaultPriceDataFactory;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.order.price.PriceInformation;

/**
 * Default implementation of {@link DistPriceDataFactory}.
 *
 * @author dsivakumaran, Namics AG
 */
public class DefaultDistPriceDataFactory extends DefaultPriceDataFactory implements DistPriceDataFactory {

    public static final String PRICEPERXUOMDESCRIPTION = "pricePerXUOMDesc";

    private final ConcurrentMap<String, NumberFormat> currencyFormats = new ConcurrentHashMap<>();

    @Override
    protected PriceData createPriceData() {
        return new PriceData();
    }

    @Override
    protected NumberFormat createCurrencyFormat(final Locale locale, final CurrencyModel currency) {
        final String key = ("EX".equals(locale.getCountry()) ? "EX" : locale.getISO3Country()) + "_" + currency.getIsocode();

        NumberFormat numberFormat = currencyFormats.get(key);
        if (numberFormat == null) {
            final NumberFormat currencyFormat = createNumberFormat(locale, currency);
            numberFormat = currencyFormats.putIfAbsent(key, currencyFormat);
            if (numberFormat == null) {
                numberFormat = currencyFormat;
            }
        }
        // don't allow multiple references
        return (NumberFormat) numberFormat.clone();
    }

    @Override
    public PriceData create(final PriceDataType priceType, final BigDecimal value, final String currencyIso) {
        Assert.notNull(priceType, "Parameter priceType cannot be null.");
        Assert.notNull(value, "Parameter value cannot be null.");
        Assert.notNull(currencyIso, "Parameter currencyIso cannot be null.");

        final PriceData priceData = createPriceData();
        priceData.setPriceType(priceType);
        priceData.setValue(value);
        priceData.setCurrencyIso(currencyIso);
        priceData.setFormattedValue(StringUtils.isEmpty(currencyIso) ? formatPercentagValue(value) : formattedPriceWithoutCurrency(value));
        return priceData;
    }

    @Override
    public PriceData create(final PriceDataType priceType, final BigDecimal value, final String currencyIso, final long minQtd) {
        Assert.notNull(priceType, "Parameter priceType cannot be null.");
        Assert.notNull(value, "Parameter value cannot be null.");

        final PriceData priceData = createPriceData();
        priceData.setPriceType(priceType);
        priceData.setMinQuantity(minQtd);
        priceData.setValue(value);
        priceData.setCurrencyIso(currencyIso);
        priceData.setFormattedValue(StringUtils.isEmpty(currencyIso) ? formatPercentagValue(value) : formatPrice(value, currencyIso));
        return priceData;
    }

    @Override
    public PriceData create(final PriceDataType priceType, final PriceInformation info) {

        Assert.notNull(priceType, "Parameter priceType cannot be null.");
        Assert.notNull(info, "Parameter value cannot be null.");

        final BigDecimal value = BigDecimal.valueOf(info.getPriceValue().getValue());
        final String currencyIso = info.getPriceValue().getCurrencyIso();
        final long minQtd = (Long) info.getQualifierValue(PriceRow.MINQTD);

        final PriceData priceData = createPriceData();
        priceData.setPriceType(priceType);
        priceData.setMinQuantity(minQtd);
        priceData.setValue(value);
        BigDecimal priceWithVat = null != info.getQualifierValue(DistPriceRow.PRICEWITHVAT) ? BigDecimal.valueOf((double) info.getQualifierValue(DistPriceRow.PRICEWITHVAT))
                                                                                            : ZERO;
        priceData.setPriceWithVat(priceWithVat);
        priceData.setFormattedPriceWithVat(formattedPriceWithoutCurrency(priceWithVat));
        priceData.setBasePrice(value);
        priceData.setCurrencyIso(currencyIso);
        priceData.setFormattedValue(StringUtils.isEmpty(currencyIso) ? formatPercentagValue(value) : formattedPriceWithoutCurrency(value));

        // Populate ERP priceperx if present else populate priceperx from hybris
        if (null != info.getQualifierValue(DistPriceRow.PRICEPERX)) {
            final BigDecimal pricePerX = (BigDecimal) info.getQualifierValue(DistPriceRow.PRICEPERX);
            if (null != pricePerX && pricePerX.floatValue() > 0) {
                priceData.setPricePerX(pricePerX);
                priceData.setFormattedPricePerX(formattedPriceWithoutCurrency(pricePerX));
                priceData.setPricePerXBaseQty(
                                              null != info.getQualifierValue(DistPriceRow.PRICEPERXBASEQTY) ? (Long) info.getQualifierValue(DistPriceRow.PRICEPERXBASEQTY)
                                                                                                            : 0);
                priceData.setPricePerXUOM(
                                          null != info.getQualifierValue(DistPriceRow.PRICEPERXUOM) ? (String) info.getQualifierValue(DistPriceRow.PRICEPERXUOM)
                                                                                                    : "");
                priceData.setPricePerXUOMDesc(
                                              null != info.getQualifierValue(PRICEPERXUOMDESCRIPTION) ? (String) info.getQualifierValue(PRICEPERXUOMDESCRIPTION)
                                                                                                      : "");
                priceData.setPricePerXUOMQty(
                                             null != info.getQualifierValue(DistPriceRow.PRICEPERXUOMQTY) ? (Long) info.getQualifierValue(DistPriceRow.PRICEPERXUOMQTY)
                                                                                                          : 0);
            }
        } else {
            final DistPriceRow distPriceRow = (DistPriceRow) info.getQualifierValue(DistPriceRow.PRICEROW);
            final BigDecimal pricePerX = distPriceRow.getPricePerX();
            if (null != pricePerX && pricePerX.floatValue() > 0) {
                priceData.setPricePerX(pricePerX);
                priceData.setFormattedPricePerX(formattedPriceWithoutCurrency(pricePerX));
                priceData.setPricePerXBaseQty(null != distPriceRow.getPricePerXBaseQty() ? distPriceRow.getPricePerXBaseQty() : 0);
                priceData.setPricePerXUOM(null != distPriceRow.getPricePerXUoM() ? distPriceRow.getPricePerXUoM().getCode() : "");
                priceData.setPricePerXUOMDesc(null != distPriceRow.getPricePerXUoM() ? distPriceRow.getPricePerXUoM().getName() : "");
                priceData.setPricePerXUOMQty(null != distPriceRow.getPricePerXUoMQty() ? distPriceRow.getPricePerXUoMQty() : 0);
            }

            priceData.setVatPercentage(null != distPriceRow.getVatPercentage() ? distPriceRow.getVatPercentage() : 0);
            priceData.setBasePrice(null != distPriceRow.getPrice() ? BigDecimal.valueOf(distPriceRow.getPrice()
                                                                                        / (double) distPriceRow.getUnitFactorAsPrimitive())
                                                                   : ZERO);
            priceData.setVatValue(null != distPriceRow.getVatValue() ? distPriceRow.getVatValue()
                                                                                   .divide(BigDecimal.valueOf((double) distPriceRow.getUnitFactorAsPrimitive()))
                                                                     : ZERO);
            BigDecimal distPriceWithVat = null != distPriceRow.getPriceWithVat() ? distPriceRow.getPriceWithVat()
                                                                                               .divide(BigDecimal.valueOf((double) distPriceRow.getUnitFactorAsPrimitive()))
                                                                                 : ZERO;
            priceData.setPriceWithVat(distPriceWithVat);
            priceData.setFormattedPriceWithVat(formattedPriceWithoutCurrency(distPriceWithVat));
        }
        return priceData;

    }

    @Override
    public PriceData createWithoutCurrency(PriceDataType priceType, BigDecimal value, String currencyIso) {
        Assert.notNull(priceType, "Parameter priceType cannot be null.");
        Assert.notNull(value, "Parameter value cannot be null.");
        Assert.notNull(currencyIso, "Parameter currencyIso cannot be null.");

        final PriceData priceData = createPriceData();
        priceData.setPriceType(priceType);
        priceData.setValue(value);
        priceData.setCurrencyIso(currencyIso);
        priceData.setFormattedValue(StringUtils.isEmpty(currencyIso) ? formatPercentagValue(value) : formattedPriceWithoutCurrency(value));
        return priceData;
    }

    @Override
    public PriceData create(PriceDataType priceType, BigDecimal value, CurrencyModel currency) {
        Assert.notNull(priceType, "Parameter priceType cannot be null.");
        Assert.notNull(value, "Parameter value cannot be null.");
        Assert.notNull(currency, "Parameter currency cannot be null.");

        final PriceData priceData = createPriceData();

        priceData.setPriceType(priceType);
        priceData.setValue(value);
        priceData.setCurrencyIso(currency.getIsocode());
        priceData.setFormattedValue(formattedPriceWithoutCurrency(value));

        return priceData;
    }

    @Override
    public PriceData create(final PriceDataType priceType, final BigDecimal value, final BigDecimal originalValue, final String currencyIso) {
        Assert.notNull(priceType, "Parameter priceType cannot be null.");
        Assert.notNull(value, "Parameter value cannot be null.");
        Assert.notNull(originalValue, "Parameter originalValue cannot be null.");
        Assert.notNull(currencyIso, "Parameter currencyIso cannot be null.");

        final PriceData priceData = createPriceData();

        priceData.setPriceType(priceType);
        priceData.setValue(value);
        priceData.setOriginalValue(originalValue);
        priceData.setCurrencyIso(currencyIso);
        priceData.setFormattedValue(formattedPriceWithoutCurrency(value));
        priceData.setFormattedOriginalValue(formatPrice(originalValue, currencyIso));

        final BigDecimal discountValue = calculatePriceDifferenceValue(originalValue, value);
        priceData.setDiscountValue(discountValue);
        priceData.setFormattedDiscountValue(formatPrice(discountValue, currencyIso));

        return priceData;
    }

    @Override
    public PriceData create(final BigDecimal value) {
        Assert.notNull(value, "Parameter value cannot be null.");

        final PriceData priceData = createPriceData();

        priceData.setPriceType(PriceDataType.BUY);
        priceData.setValue(value);
        priceData.setFormattedValue(formattedPriceWithoutCurrency(value));
        return priceData;
    }

    @Override
    public PriceData create(final BigDecimal value, final int maxFractionDigits) {
        Assert.notNull(value, "Parameter value cannot be null.");

        final PriceData priceData = createPriceData();

        priceData.setPriceType(PriceDataType.BUY);
        priceData.setValue(value);
        priceData.setFormattedValue(formattedPriceWithoutCurrency(value, maxFractionDigits));
        return priceData;
    }

    @Override
    public String formatPriceValue(final BigDecimal price, final String currencyIso) {
        final DecimalFormat decimalFormat = new DecimalFormat();
        adjustDigits(decimalFormat, price, getCommonI18NService().getCurrency(currencyIso));
        return decimalFormat.format(price);
    }

    protected String formatPercentagValue(final BigDecimal price) {
        return price.doubleValue() + " %";
    }

    protected String formatPrice(final BigDecimal value, final String currencyIso) {
        final CurrencyModel requestedCurrency = getCommonI18NService().getCurrency(currencyIso);

        final NumberFormat currencyFormat = createCurrencyFormat(getLocale(), value, requestedCurrency);
        return currencyFormat.format(value);
    }

    private Locale getLocale() {
        Locale locale = getCommerceCommonI18NService().getLocaleForLanguage(getCommonI18NService().getCurrentLanguage());
        if (locale == null) {
            // Fallback to session locale
            locale = getI18NService().getCurrentLocale();
        }

        return locale;
    }

    protected String formattedPriceWithoutCurrency(final BigDecimal value) {
        NumberFormat numberFormat = createNumberFormatWithoutCurrency(getLocale(), value);
        BigDecimal roundedValue = value.setScale(numberFormat.getMaximumFractionDigits(), RoundingMode.HALF_UP);
        return numberFormat.format(roundedValue);
    }

    protected String formattedPriceWithoutCurrency(final BigDecimal value, final int maxFractionDigits) {
        NumberFormat numberFormat = createNumberFormatWithoutCurrency(getLocale(), maxFractionDigits);
        BigDecimal roundedValue = value.setScale(maxFractionDigits, RoundingMode.HALF_UP);
        return numberFormat.format(roundedValue);
    }

    protected NumberFormat createCurrencyFormat(final Locale locale, final BigDecimal value, final CurrencyModel currency) {
        final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        adjustDigits((DecimalFormat) currencyFormat, value, currency);
        adjustSymbol((DecimalFormat) currencyFormat, currency);
        return currencyFormat;
    }

    protected NumberFormat createNumberFormatWithoutCurrency(final Locale locale, final BigDecimal value) {
        final NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        adjustDigits((DecimalFormat) numberFormat, value);
        return numberFormat;
    }

    protected NumberFormat createNumberFormatWithoutCurrency(final Locale locale, final int maxFractionDigits) {
        final NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        final CurrencyModel currentCurrency = getCommonI18NService().getCurrentCurrency();
        final int tempDigits = currentCurrency == null ? 2 : currentCurrency.getDigits() == null ? 0 : currentCurrency.getDigits().intValue();
        final int digits = Math.max(0, tempDigits);

        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(maxFractionDigits);
        if (digits == 0) {
            ((DecimalFormat) numberFormat).setDecimalSeparatorAlwaysShown(false);
        }

        return numberFormat;
    }

    protected DecimalFormat adjustDigits(final DecimalFormat format, final BigDecimal price, final CurrencyModel currencyModel) {
        final int tempDigits = currencyModel.getDigits() == null ? 0 : currencyModel.getDigits().intValue();
        final int digits = Math.max(0, tempDigits);

        format.setMinimumFractionDigits(digits);
        if (price.doubleValue() < 1 && price.scale() > 2) {
            format.setMaximumFractionDigits(5);
        } else {
            format.setMaximumFractionDigits(digits);
        }

        if (digits == 0) {
            format.setDecimalSeparatorAlwaysShown(false);
        }

        return format;
    }

    protected DecimalFormat adjustDigits(final DecimalFormat format, final BigDecimal price) {
        final CurrencyModel currentCurrency = getCommonI18NService().getCurrentCurrency();
        final int tempDigits = currentCurrency == null ? 2 : currentCurrency.getDigits() == null ? 0 : currentCurrency.getDigits().intValue();
        final int digits = Math.max(0, tempDigits);

        format.setMinimumFractionDigits(digits);
        if (price.doubleValue() < 1 && price.scale() > 2) {
            format.setMaximumFractionDigits(5);
        } else {
            format.setMaximumFractionDigits(digits);
        }

        if (digits == 0) {
            format.setDecimalSeparatorAlwaysShown(false);
        }

        return format;
    }

    protected BigDecimal calculatePriceDifferenceValue(final BigDecimal originalPrice, final BigDecimal discountedPrice) {
        if (originalPrice != null && discountedPrice != null) {
            return originalPrice.subtract(discountedPrice);
        }

        return null;
    }

}
