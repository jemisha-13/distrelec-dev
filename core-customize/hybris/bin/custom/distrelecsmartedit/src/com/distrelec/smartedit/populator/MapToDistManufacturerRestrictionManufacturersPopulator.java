package com.distrelec.smartedit.populator;

import com.namics.distrelec.b2b.core.model.restrictions.DistManufacturerRestrictionModel;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapToDistManufacturerRestrictionManufacturersPopulator implements Populator<Map<String, Object>, DistManufacturerRestrictionModel> {

    private static final Logger LOG = LoggerFactory.getLogger(MapToDistManufacturerRestrictionManufacturersPopulator.class);

    private static final String MANUFACTURERS_FIELD = "manufacturers";

    @Autowired
    DistManufacturerService manufacturerService;

    @Override
    public void populate(Map<String, Object> stringObjectMap, DistManufacturerRestrictionModel manufacturerRestrictionModel) throws ConversionException {
        List<String> manufacturerCodes = (List<String>) stringObjectMap.get(MANUFACTURERS_FIELD);

        if(manufacturerCodes != null){
            manufacturerRestrictionModel.setManufacturers(manufacturerCodes.stream()
                    .map(manufacturerService::getManufacturerByCode)
                    .collect(Collectors.toList()));
        }

    }
}
