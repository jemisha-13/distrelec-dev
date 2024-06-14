package com.namics.distrelec.b2b.facades.common.predicate;

import com.namics.distrelec.b2b.core.model.pages.DistManufacturerPageModel;

import java.util.function.Predicate;

public class ManufacturerPageTypeCodePredicate implements Predicate<String> {

    @Override
    public boolean test(String pageTypeModel) {
        return DistManufacturerPageModel._TYPECODE.equals(pageTypeModel);
    }
}
