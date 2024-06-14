package com.namics.hybris.ffsearch.populator.suggestion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.core.suggestion.SuggestionEnergyEfficencyData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimConstants.EFFICENCY_FEATURE;
import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimConstants.POWER_FEATURE;

public class DistFactFinderEnergyEfficiencyPopulator implements Populator<String, SuggestionEnergyEfficencyData> {

    private static final Logger LOG = LoggerFactory.getLogger(DistFactFinderEnergyEfficiencyPopulator.class);

    @Override
    public void populate(String energyEfficiencyJson, SuggestionEnergyEfficencyData energyEfficiencyData) throws ConversionException {
        if (!StringUtils.isEmpty(energyEfficiencyJson)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String,String> jsonData = objectMapper.readValue(energyEfficiencyJson, HashMap.class);
                energyEfficiencyData.setEfficency(jsonData.get(EFFICENCY_FEATURE));
                String power = jsonData.get(POWER_FEATURE);
                if (power != null) {
                    energyEfficiencyData.setPower(power + " kWh / 1000 h");
                }
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
    }
}
