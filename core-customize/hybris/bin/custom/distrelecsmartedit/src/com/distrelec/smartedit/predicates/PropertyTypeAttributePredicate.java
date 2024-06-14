package com.distrelec.smartedit.predicates;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.servicelayer.type.TypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Predicate that will check if attribute is of defined item type as specified in *-items.xml
 */
public class PropertyTypeAttributePredicate implements Predicate<AttributeDescriptorModel> {

    private String type;

    private static final Logger LOG = LoggerFactory.getLogger(PropertyTypeAttributePredicate.class);

    @Autowired
    private TypeService typeService;

    @Override
    public boolean test(final AttributeDescriptorModel attributeDescriptor) {
        TypeModel attributeType = attributeDescriptor.getAttributeType();
        String attributeCode = attributeType.getCode();
        return attributeCode.equals(type);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyTypeAttributePredicate that = (PropertyTypeAttributePredicate) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
