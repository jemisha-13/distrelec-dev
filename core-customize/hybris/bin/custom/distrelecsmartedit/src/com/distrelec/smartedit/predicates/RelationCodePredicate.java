package com.distrelec.smartedit.predicates;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.RelationDescriptorModel;

import java.util.Objects;
import java.util.function.Predicate;

public class RelationCodePredicate implements Predicate<AttributeDescriptorModel> {

    private String code;

    @Override
    public boolean test(AttributeDescriptorModel attributeDescriptor) {
        if(attributeDescriptor instanceof RelationDescriptorModel){
            RelationDescriptorModel relationDescriptor = (RelationDescriptorModel) attributeDescriptor;
            return relationDescriptor.getRelationType().getCode().equals(code);
        }

        return false;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationCodePredicate that = (RelationCodePredicate) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
