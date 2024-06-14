package com.distrelec.smartedit.populator;

import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.annotation.Order;

import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

@Order()
public class DistOptionalComponentTypeAttributePopulator implements Populator<AttributeDescriptorModel, ComponentTypeAttributeData> {

    private Set<String> enclosingTypes;

    @Override
    public void populate(final AttributeDescriptorModel source, final ComponentTypeAttributeData target) throws ConversionException {
        if (source.getEnclosingType() != null) {
            String typeCode = source.getEnclosingType().getCode();
            if (isAttributeOptional(typeCode)) {
                target.setRequired(false);
            }
        }
    }

    private boolean isAttributeOptional(final String typeCode) {
        return StringUtils.isNotBlank(typeCode) && CollectionUtils.isNotEmpty(getEnclosingTypes()) && getEnclosingTypes().contains(typeCode);
    }

    protected Set<String> getEnclosingTypes() {
        return enclosingTypes;
    }

    @Required
    public void setEnclosingTypes(final Set<String> enclosingTypes) {
        this.enclosingTypes = enclosingTypes;
    }
}
