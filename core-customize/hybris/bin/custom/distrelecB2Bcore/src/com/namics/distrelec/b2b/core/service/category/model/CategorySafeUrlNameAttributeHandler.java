package com.namics.distrelec.b2b.core.service.category.model;

import java.util.Locale;

import com.namics.distrelec.b2b.core.model.AbstractDynamicLocalizedAttributeHandler;
import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.category.model.CategoryModel;

public class CategorySafeUrlNameAttributeHandler extends AbstractDynamicLocalizedAttributeHandler<String, CategoryModel> {

    @Override
    public String get(CategoryModel category) {
        String rawName = category.getName();
        return DistUtils.urlSafe(rawName);
    }

    @Override
    public String get(CategoryModel model, Locale loc) {
        String rawName = model.getName(loc);
        return DistUtils.urlSafe(rawName);
    }
}
