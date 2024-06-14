package com.namics.distrelec.b2b.facades.category;

import java.util.Optional;

import com.namics.distrelec.b2b.core.model.cms.ProductFamilyPageModel;

import de.hybris.platform.commercefacades.product.data.CategoryData;

public interface DistProductFamilyFacade {

    Optional<ProductFamilyPageModel> findPageForProductFamily(String code);

    Optional<CategoryData> findProductFamily(String code);

    Optional<CategoryData> getProductFamilySuperCategory(String productFamilyCode);
}
