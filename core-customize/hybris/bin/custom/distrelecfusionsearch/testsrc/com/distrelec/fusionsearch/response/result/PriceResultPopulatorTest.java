package com.distrelec.fusionsearch.response.result;

import static com.distrelec.fusionsearch.response.result.PriceResultPopulator.CURRENCY_PROPERTY;
import static com.distrelec.fusionsearch.response.result.PriceResultPopulator.SCALE_PRICES_GROSS_PROPERTY;
import static com.distrelec.fusionsearch.response.result.PriceResultPopulator.SCALE_PRICES_NET_PROPERTY;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.mockito.InjectMocks;

import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class PriceResultPopulatorTest extends AbstractSearchResultPopulatorTest {

    @InjectMocks
    PriceResultPopulator priceResultPopulator;

    @Test
    public void testMergePrices() {
        String currency = "CHF";
        String scalePricesGross = "{\"1\":1151.25,\"5\":1046.25}";
        String scalePricesNet = "{\"1\":921.0,\"5\":837.0}";
        String scalePrices = "CHF;Gross;1=1151.25|CHF;Gross;5=1046.25|CHF;Net;1=921.0|CHF;Net;5=837.0|";

        Map<String, Object> doc = Map.of(CURRENCY_PROPERTY, currency, SCALE_PRICES_GROSS_PROPERTY, scalePricesGross, SCALE_PRICES_NET_PROPERTY, scalePricesNet);

        priceResultPopulator.populate(doc, searchResult);

        Map<String, Object> values = searchResult.getValues();
        assertEquals(1, values.size());
        assertEquals(scalePrices, values.get(DistFactFinderExportColumns.PRICE.getValue()));
    }
}
