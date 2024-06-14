package com.namics.distrelec.b2b.facades.common.predicate;

import com.namics.distrelec.b2b.core.model.cms.ProductFamilyPageModel;

import java.util.function.Predicate;

public class ProductFamilyPageTypeCodePredicate implements Predicate<String> {

    @Override
    public boolean test(String pageTypeModel) {
        return ProductFamilyPageModel._TYPECODE.equals(pageTypeModel);
    }
}
