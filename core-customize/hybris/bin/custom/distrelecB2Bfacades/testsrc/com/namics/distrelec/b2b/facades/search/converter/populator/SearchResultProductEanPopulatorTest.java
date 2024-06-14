package com.namics.distrelec.b2b.facades.search.converter.populator;

import static org.assertj.core.api.Java6Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;

@UnitTest
public class SearchResultProductEanPopulatorTest {

    private static final String TEST_EAN = "1234567890";

    private SearchResultProductEanPopulator populator;

    @Before
    public void setup() {
        populator = new SearchResultProductEanPopulator();
    }

    @Test
    public void testPopulate() {
        SearchResultValueData searchResultValueData = new SearchResultValueData();
        Map<String, Object> valuesMap = new HashMap<>(1);
        valuesMap.put("EAN", TEST_EAN);

        ProductData productData = new ProductData();
        searchResultValueData.setValues(valuesMap);
        populator.populate(searchResultValueData, productData);

        assertThat(productData.getEan()).isEqualTo(TEST_EAN);
    }

}
