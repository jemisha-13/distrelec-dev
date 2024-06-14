package com.namics.distrelec.b2b.facades.search.converter.populator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class SearchResultEnergyEfficiencyPopulator extends AbstractSearchResultPopulator {

    private static final Logger log = LoggerFactory.getLogger(SearchResultEnergyEfficiencyPopulator.class);

    public static final String ENERGY_EFFICIENCY_KEY = "energyEfficiency";

    private static final String ENERGY_CLASS_BUILTIN_LED_KEY = "Energyclasses_built-in_LED_LOV";

    private static final String ENERGY_CLASS_FITTING_KEY = "Energyclasses_fitting_LOV";

    private static final String ENERGY_CLASS_INCLUDED_BULB_KEY = "energyclasses_included_bulb_LOV";

    private static final String CALC_ENERGY_LABEL_TOP_KEY = "calc_energylabel_top_text";

    private static final String CALC_ENERGY_LABEL_BOTTOM_KEY = "calc_energylabel_bottom_text";

    @Override
    public void populate(SearchResultValueData searchResultValueData, ProductData productData) throws ConversionException {
        String energyEfficiencyJson = (String) searchResultValueData.getValues().get(ENERGY_EFFICIENCY_KEY);

        if (!StringUtils.isEmpty(energyEfficiencyJson)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String, String> jsonData = objectMapper.readValue(energyEfficiencyJson, HashMap.class);

                productData.setEnergyClassesBuiltInLed(jsonData.get(ENERGY_CLASS_BUILTIN_LED_KEY));
                productData.setEnergyClassesFitting(jsonData.get(ENERGY_CLASS_FITTING_KEY));
                productData.setEnergyClassesIncludedBulb(jsonData.get(ENERGY_CLASS_INCLUDED_BULB_KEY));
                productData.setEnergyTopText(jsonData.get(CALC_ENERGY_LABEL_TOP_KEY));
                productData.setEnergyBottomText(jsonData.get(CALC_ENERGY_LABEL_BOTTOM_KEY));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}
