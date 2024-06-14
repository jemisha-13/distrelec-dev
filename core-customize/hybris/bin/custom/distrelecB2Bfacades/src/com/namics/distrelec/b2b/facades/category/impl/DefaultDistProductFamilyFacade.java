package com.namics.distrelec.b2b.facades.category.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.cms.ProductFamilyPageModel;
import com.namics.distrelec.b2b.core.service.DistCmsPageService;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.facades.category.DistProductFamilyFacade;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class DefaultDistProductFamilyFacade implements DistProductFamilyFacade {

    @Autowired
    private DistCmsPageService distCmsPageService;

    @Autowired
    private DistCategoryService distCategoryService;

    @Autowired
    private CatalogVersionService catalogVersionService;

    @Autowired
    @Qualifier("categoryConverter")
    private Converter<CategoryModel, CategoryData> categoryConverter;

    @Override
    public Optional<ProductFamilyPageModel> findPageForProductFamily(String code) {
        return distCmsPageService.findProductFamilyPage(code, catalogVersionService.getSessionCatalogVersions());
    }

    @Override
    public Optional<CategoryData> findProductFamily(String code) {
        return distCategoryService.findProductFamily(code).map(categoryConverter::convert);
    }

    @Override
    public Optional<CategoryData> getProductFamilySuperCategory(String productFamilyCode) {
        Optional<CategoryModel> productFamily = distCategoryService.findProductFamily(productFamilyCode);
        if (productFamily.isEmpty()) {
            return Optional.empty();
        }
        return emptyIfNull(productFamily.get().getSupercategories()).stream()
                                                                    .findFirst()
                                                                    .map(categoryConverter::convert);
    }
}
