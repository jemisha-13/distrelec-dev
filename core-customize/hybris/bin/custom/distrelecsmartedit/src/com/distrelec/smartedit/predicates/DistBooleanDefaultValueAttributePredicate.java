package com.distrelec.smartedit.predicates;

import java.util.function.Predicate;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

public class DistBooleanDefaultValueAttributePredicate implements Predicate<AttributeDescriptorModel> {

    @Override
    public boolean test(final AttributeDescriptorModel attributeDescriptor) {
        return attributeDescriptor.getDefaultValue() instanceof Boolean;
    }
}
