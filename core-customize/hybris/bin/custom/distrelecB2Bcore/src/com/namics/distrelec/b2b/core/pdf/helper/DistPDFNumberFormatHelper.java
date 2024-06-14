package com.namics.distrelec.b2b.core.pdf.helper;

import de.hybris.platform.commercefacades.product.data.PriceData;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

import static java.math.BigDecimal.*;

/**
 * This class was written to replicate the behavior of the
 * storefront tag /WEB-INF/tags/shared/terrific/format/price.tag
 * for Cart so that the PDF file can have the same number format for
 * displaying prices.
 */
public class DistPDFNumberFormatHelper {

    private static final BigDecimal ZERO = new BigDecimal("0.00");

    private NumberFormat bigFormat;

    private NumberFormat smallFormat;

    private String currencyIso;

    public DistPDFNumberFormatHelper(Locale currentLocale, String currencyIso, @Nullable String commaCountries) {
        this.currencyIso = currencyIso;
        this.bigFormat = NumberFormat.getInstance(processLocale(currentLocale, commaCountries));
        this.smallFormat = NumberFormat.getInstance(processLocale(currentLocale, commaCountries));
        this.bigFormat.setMaximumFractionDigits(2);
        this.bigFormat.setMinimumFractionDigits(2);
        this.smallFormat.setMaximumFractionDigits(6);
        this.smallFormat.setMinimumFractionDigits(2);
    }

    private String formatInternal(BigDecimal number) {
        if (number.compareTo(ONE) < 0 && number.scale() > 2) {
            return smallFormat.format(number);
        }
        return bigFormat.format(number);
    }

    public String format(BigDecimal number) {
        String result = formatInternal(number);
        if (currencyIso != null && currencyIso.equals("CHF")) {
            return result.replace(",", "\'");
        }
        return result;
    }

    public String formatPriceData(PriceData priceData) {
        String formattedValue = Optional.ofNullable(priceData)
                                        .map(PriceData::getValue)
                                        .map(this::format)
                                        .orElseGet(this::getDefaultFormattedValue);
        String currency = Optional.ofNullable(priceData)
                                  .map(PriceData::getCurrencyIso)
                                  .orElse(currencyIso);
        return String.format("%s %s", currency,formattedValue);
    }

    private String getDefaultFormattedValue() {
        return format(ZERO);
    }

    /**
     * If country belongs to the Comma using countries list,
     * defaults language of Locale to German since that forces
     * commas in numeric formatting. Based on the price formatting
     * in price.tag
     * @param locale - the original locale
     * @param commaCountries - a comma seperated list of countries
     * @return the amended Local
     */
    private Locale processLocale(Locale locale, String commaCountries) {
        if (commaCountries == null) {
            return locale;
        }
        String country = locale.getCountry();
        if (country == null) {
            return locale;
        }
        if (commaCountries.contains(country)) {
            return new Locale("de", country);
        }

        return locale;
    }

}
