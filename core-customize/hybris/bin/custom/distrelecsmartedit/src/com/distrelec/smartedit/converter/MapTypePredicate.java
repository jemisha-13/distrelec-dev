package com.distrelec.smartedit.converter;

import de.hybris.platform.cms2.version.converter.attribute.data.VersionAttributeDescriptor;
import de.hybris.platform.core.model.type.MapTypeModel;
import de.hybris.platform.core.model.type.TypeModel;

import java.util.function.Predicate;

public class MapTypePredicate implements Predicate<VersionAttributeDescriptor> {

    @Override
    public boolean test(VersionAttributeDescriptor versionAttributeDescriptor) {
        TypeModel attributeType = versionAttributeDescriptor.getType();
        return attributeType.getItemtype().equals(MapTypeModel._TYPECODE);
    }

}
