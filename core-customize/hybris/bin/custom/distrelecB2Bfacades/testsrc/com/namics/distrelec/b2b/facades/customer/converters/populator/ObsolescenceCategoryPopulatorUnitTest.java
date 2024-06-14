package com.namics.distrelec.b2b.facades.customer.converters.populator;

import com.namics.distrelec.b2b.core.model.ObsolescenceCategoryModel;
import com.namics.distrelec.b2b.facades.user.data.ObsolescenceCategoryData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ObsolescenceCategoryPopulatorUnitTest {

    @Mock
    private Converter<CategoryModel, CategoryData> categoryConverter;

    @InjectMocks
    ObsolescenceCategoryPopulator obsolescenceCategoryPopulator;

    @Test
    public void testPopulate() {
        // given
        ObsolescenceCategoryModel source = mock(ObsolescenceCategoryModel.class);
        ObsolescenceCategoryData target = new ObsolescenceCategoryData();
        CategoryModel categoryModel = mock(CategoryModel.class);
        CategoryData categoryData = mock(CategoryData.class);

        // when
        when(source.getCategory()).thenReturn(categoryModel);
        when(categoryConverter.convert(categoryModel)).thenReturn(categoryData);
        when(source.getObsolCategorySelected()).thenReturn(true);
        when(categoryModel.getCode()).thenReturn("category_code");

        obsolescenceCategoryPopulator.populate(source, target);

        // then
        assertThat(target.isIsObsolCategorySelected(), is(true));
        assertThat(target.getCategory(), equalTo(categoryData));
        assertThat(target.getCategoryCode(), equalTo("category_code"));
    }
}
