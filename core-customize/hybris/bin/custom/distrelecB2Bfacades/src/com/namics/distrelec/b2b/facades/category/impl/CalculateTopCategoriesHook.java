package com.namics.distrelec.b2b.facades.category.impl;

import java.util.List;

import com.namics.distrelec.b2b.facades.product.data.DistCategoryIndexData;

interface CalculateTopCategoriesHook {

    List<DistCategoryIndexData> calculateTopCategoryData(TopCategoriesCacheKey cacheKey);
}
