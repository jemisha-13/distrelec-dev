package com.namics.distrelec.b2b.facades.product.converters.populator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.facades.product.data.EnergyEfficencyData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DistEnergyEfficencyPopulator<SOURCE extends ProductModel, TARGET extends EnergyEfficencyData> implements Populator<SOURCE, TARGET> {

    private static final Logger LOG = LoggerFactory.getLogger(DistEnergyEfficencyPopulator.class);

    private static final String ENERGY_EFFICIENCY_KEY = "Energyclasses_LOV";
    private static final String ENERGY_POWER = "Leistung_W";

    @Override
    public void populate(final SOURCE source, final TARGET target) throws ConversionException {
        populateFeatures(target, source);
        if (source.getManufacturer() != null) {
            target.setManufacturer(source.getManufacturer().getName());
        }
        target.setType(source.getTypeName());
    }

    private void populateFeatures(final EnergyEfficencyData target, final ProductModel product) {
        if (product.getEnergyEffiencyLabels() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String, String> eelData = objectMapper.readValue(product.getEnergyEffiencyLabels(), HashMap.class);
                target.setEfficency(eelData.get(ENERGY_EFFICIENCY_KEY));
                String power = eelData.get(ENERGY_POWER);
                if (power != null) {
                    target.setPower(power + " kWh / 1000 h");
                }
            } catch (IOException ex) {
                LOG.error(ex.getMessage());
            }
        }
    }
}
