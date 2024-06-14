package com.namics.distrelec.b2b.facades.product.converters.populator;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.namics.distrelec.b2b.core.service.category.DistCategoryService;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.converters.populator.ProductCategoriesPopulator;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DistProductCategoriesPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends ProductCategoriesPopulator<SOURCE, TARGET> {

    private DistCategoryService distCategoryService;

    @Override
    public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException {
        super.populate(productModel, productData);
        if (Objects.nonNull(getBreadcrumbCategories(productModel))) {
            productData.setBreadcrumbs(getBreadcrumbCategories(productModel));
        }

    }

    private List<CategoryData> getBreadcrumbCategories(final ProductModel product) {
        if (product != null) {
            return getCategoryConverter().convertAll(getSuperCategoriesForBaseProduct(product));
        }

        return Collections.emptyList();
    }

    private List<CategoryModel> getSuperCategoriesForBaseProduct(final ProductModel baseProduct) {
        if (Objects.nonNull(baseProduct)) {
            if (Objects.nonNull(baseProduct.getPrimarySuperCategory())) {
                List<CategoryModel> categories = getDistCategoryService().getBreadcrumbCategoriesInReverseOrderForCategory(baseProduct.getPrimarySuperCategory());
                return categories.stream()
                                 .filter(c -> c.getLevel() != null && c.getLevel() != 0)
                                 .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    public DistCategoryService getDistCategoryService() {
        return distCategoryService;
    }

    public void setDistCategoryService(final DistCategoryService distCategoryService) {
        this.distCategoryService = distCategoryService;
    }
}
