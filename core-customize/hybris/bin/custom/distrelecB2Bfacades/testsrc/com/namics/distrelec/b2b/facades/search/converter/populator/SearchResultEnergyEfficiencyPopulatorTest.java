package com.namics.distrelec.b2b.facades.search.converter.populator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;

@UnitTest
public class SearchResultEnergyEfficiencyPopulatorTest {

    private static final String TEST_EE_JSON = "{\"Energyclasses_built-in_LED_LOV\":\"A++\", " +
                                               "\"Energyclasses_fitting_LOV\":\"A\", " +
                                               "\"energyclasses_included_bulb_LOV\":\"B\", " +
                                               "\"calc_energylabel_top_text\":\"TOP_TEXT\", " +
                                               "\"calc_energylabel_bottom_text\":\"BOTTOM_TEXT\"}";

    private SearchResultEnergyEfficiencyPopulator populator;

    @Before
    public void setup() {
        populator = new SearchResultEnergyEfficiencyPopulator();
    }

    @Test
    public void testPopulate() {
        SearchResultValueData searchResult = mock(SearchResultValueData.class);

        Map<String, Object> testResultMap = new HashMap<>();

        when(searchResult.getValues()).thenReturn(testResultMap);

        ProductData productData = new ProductData();
        populator.populate(searchResult, productData);

        assertThat(productData.getEnergyClassesBuiltInLed())
                                                            .isNullOrEmpty();
        assertThat(productData.getEnergyClassesFitting())
                                                         .isNullOrEmpty();
        assertThat(productData.getEnergyClassesIncludedBulb())
                                                              .isNullOrEmpty();
        assertThat(productData.getEnergyTopText())
                                                  .isNullOrEmpty();
        assertThat(productData.getEnergyBottomText())
                                                     .isNullOrEmpty();

        testResultMap.put(SearchResultEnergyEfficiencyPopulator.ENERGY_EFFICIENCY_KEY, TEST_EE_JSON);
        populator.populate(searchResult, productData);

        assertThat(productData.getEnergyClassesBuiltInLed())
                                                            .isEqualTo("A++");
        assertThat(productData.getEnergyClassesFitting())
                                                         .isEqualTo("A");
        assertThat(productData.getEnergyClassesIncludedBulb())
                                                              .isEqualTo("B");
        assertThat(productData.getEnergyTopText())
                                                  .isEqualTo("TOP_TEXT");
        assertThat(productData.getEnergyBottomText())
                                                     .isEqualTo("BOTTOM_TEXT");
    }

}
