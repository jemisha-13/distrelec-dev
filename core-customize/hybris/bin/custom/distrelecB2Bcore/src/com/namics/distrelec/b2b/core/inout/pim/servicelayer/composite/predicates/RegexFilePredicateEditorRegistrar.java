package com.namics.distrelec.b2b.core.inout.pim.servicelayer.composite.predicates;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;

import java.beans.PropertyEditorSupport;

public class RegexFilePredicateEditorRegistrar implements PropertyEditorRegistrar {

    @Override
    public void registerCustomEditors(PropertyEditorRegistry registry) {
        registry.registerCustomEditor(RegexFilePredicate.class, new RegexFilePredicateEditor());
    }

    private static class RegexFilePredicateEditor extends PropertyEditorSupport {

        @Override
        public void setAsText(String text) {
            setValue(new RegexFilePredicate(text));
        }

        @Override
        public String getAsText() {
            RegexFilePredicate predicate = (RegexFilePredicate) getValue();
            return predicate != null ? predicate.getPattern().pattern() : "";
        }
    }
}
