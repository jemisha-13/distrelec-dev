package com.distrelec.smartedit.cmsfacades.navigations.service.functions;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.function.Function;

public class DefaultNavigationEntryCategoryModelIdConversionFunction implements Function<ItemModel, String> {

    @Override
    public String apply(ItemModel itemModel) {
        if (!(CategoryModel.class.isAssignableFrom(itemModel.getClass()))) {
            throw new ConversionException("Invalid Category Component: " + itemModel);
        }
        return ((CategoryModel) itemModel).getCode();
    }
}
