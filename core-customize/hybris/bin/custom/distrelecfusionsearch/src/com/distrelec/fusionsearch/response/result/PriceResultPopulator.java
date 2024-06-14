package com.distrelec.fusionsearch.response.result;

import static com.namics.distrelec.b2b.facades.search.converter.populator.SearchResultPricePopulator.DELIMITER;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.GROSS;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.NET;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

class PriceResultPopulator implements Populator<Map<String, Object>, SearchResultValueData> {

    static final String CURRENCY_PROPERTY = "currency";

    static final String ITEM_MIN = "itemMin";

    static final String SCALE_PRICES_GROSS_PROPERTY = "scalePricesGross";

    static final String SCALE_PRICES_NET_PROPERTY = "scalePricesNet";

    static final String STANDARD_PRICE_GROSS = "standardPriceGross";

    static final String STANDARD_PRICE_NET = "standardPriceNet";

    private final DecimalFormat ITEMS_MIN_DF = new DecimalFormat("0");

    @Override
    public void populate(Map<String, Object> doc, SearchResultValueData searchResult) throws ConversionException {
        String currency = (String) doc.get(CURRENCY_PROPERTY);
        String scalePricesGross = (String) doc.get(SCALE_PRICES_GROSS_PROPERTY);
        String scalePricesNet = (String) doc.get(SCALE_PRICES_NET_PROPERTY);

        Map<String, Double> scalePricesGrossMap = parseJson(scalePricesGross, Map.class);
        Map<String, Double> scalePricesNetMap = parseJson(scalePricesNet, Map.class);

        StringBuilder scalePricesStringBuilder = new StringBuilder();

        appendPrices(scalePricesGrossMap, GROSS, currency, scalePricesStringBuilder);
        appendPrices(scalePricesNetMap, NET, currency, scalePricesStringBuilder);

        String scalePrices = scalePricesStringBuilder.toString();
        searchResult.getValues().put(DistFactFinderExportColumns.PRICE.getValue(), scalePrices);

        populateStandardPrices(doc, searchResult, currency);
    }

    private void populateStandardPrices(Map<String, Object> doc, SearchResultValueData searchResult, String currency) {
        Double standardPriceGross = (Double) doc.get(STANDARD_PRICE_GROSS);
        Double standardPriceNet = (Double) doc.get(STANDARD_PRICE_NET);
        if (standardPriceGross != null && standardPriceNet != null) {
            Double itemsMin = (Double) doc.get(ITEM_MIN);
            String itemsMinString = ITEMS_MIN_DF.format(itemsMin);

            StringBuilder standardPriceStringBuilder = new StringBuilder();
            appendPrices(Map.of(itemsMinString, standardPriceGross), GROSS, currency, standardPriceStringBuilder);
            appendPrices(Map.of(itemsMinString, standardPriceNet), NET, currency, standardPriceStringBuilder);

            String standardPrice = standardPriceStringBuilder.toString();
            searchResult.getValues().put(DistFactFinderExportColumns.STANDARD_PRICE.getValue(), standardPrice);
        }
    }

    private void appendPrices(Map<String, Double> scaleToPriceMap, String grossNet, String currency, StringBuilder pricesStringBuilder) {
        for (Entry<String, Double> scaleToPrice : scaleToPriceMap.entrySet()) {
            String scale = scaleToPrice.getKey();
            Double price = scaleToPrice.getValue();
            pricesStringBuilder.append(currency).append(";").append(grossNet).append(";").append(scale).append("=")
                               .append(price)
                               .append(DELIMITER);
        }
    }

    private <T> T parseJson(String content, Class<T> valueType) throws ConversionException {
        try {
            return new ObjectMapper().readValue(content, valueType);
        } catch (JsonProcessingException e) {
            throw new ConversionException("Unable to parse " + content, e);
        }
    }
}
