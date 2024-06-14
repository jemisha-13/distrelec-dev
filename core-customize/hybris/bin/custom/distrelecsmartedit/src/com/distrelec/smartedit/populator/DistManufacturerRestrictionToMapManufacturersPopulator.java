package com.distrelec.smartedit.populator;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.restrictions.DistManufacturerRestrictionModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;
import java.util.stream.Collectors;

public class DistManufacturerRestrictionToMapManufacturersPopulator implements Populator<DistManufacturerRestrictionModel, Map<String, Object>> {

    @Override
    public void populate(DistManufacturerRestrictionModel distManufacturerRestrictionModel, Map<String, Object> stringObjectMap) throws ConversionException {
        stringObjectMap.put("manufacturers", distManufacturerRestrictionModel.getManufacturers().stream()
                .map(DistManufacturerModel::getCode)
                .collect(Collectors.toList()));
    }
}
