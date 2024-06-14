package com.distrelec.smartedit.cmsfacades.navigations.service.functions;

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cmsfacades.data.NavigationEntryData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Function;

public class DefaultNavigationEntryCategoryModelConversionFunction implements Function<NavigationEntryData, CategoryModel> {

    @Autowired
    private CategoryService categoryService;

    @Override
    public CategoryModel apply(NavigationEntryData navigationEntryData) {
        try {
            return categoryService.getCategoryForCode(navigationEntryData.getItemId());
        } catch (AmbiguousIdentifierException | UnknownIdentifierException e) {
            throw new ConversionException("Invalid Media: " + navigationEntryData.getItemId(), e);
        }
    }
}
