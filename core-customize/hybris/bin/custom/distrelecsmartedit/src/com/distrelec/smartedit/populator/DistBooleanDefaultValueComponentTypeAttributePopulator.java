package com.distrelec.smartedit.populator;

import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DistBooleanDefaultValueComponentTypeAttributePopulator implements Populator<AttributeDescriptorModel, ComponentTypeAttributeData> {

    @Override
    public void populate(final AttributeDescriptorModel source, final ComponentTypeAttributeData target) throws ConversionException {
        if (source.getDefaultValue() instanceof Boolean) {
            target.setDefaultValue((Boolean) source.getDefaultValue());
        }
    }
}
