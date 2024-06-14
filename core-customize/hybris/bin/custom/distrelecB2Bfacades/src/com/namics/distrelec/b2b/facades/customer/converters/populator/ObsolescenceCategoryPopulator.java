package com.namics.distrelec.b2b.facades.customer.converters.populator;

import com.namics.distrelec.b2b.core.model.ObsolescenceCategoryModel;
import com.namics.distrelec.b2b.facades.user.data.ObsolescenceCategoryData;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ObsolescenceCategoryPopulator implements Populator<ObsolescenceCategoryModel, ObsolescenceCategoryData> {

	@Autowired
	@Qualifier("categoryConverter")
	private Converter<CategoryModel, CategoryData> categoryConverter;

	@Override
	public void populate(final ObsolescenceCategoryModel source, final ObsolescenceCategoryData target) {
		target.setIsObsolCategorySelected(source.getObsolCategorySelected());
		final CategoryModel category = source.getCategory();
		target.setCategory(categoryConverter.convert(category));
		target.setCategoryCode(category.getCode());
	}
}
