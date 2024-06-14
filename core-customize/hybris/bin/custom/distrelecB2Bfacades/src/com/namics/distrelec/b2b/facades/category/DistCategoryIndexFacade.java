package com.namics.distrelec.b2b.facades.category;

import com.namics.distrelec.b2b.facades.product.data.DistCategoryIndexData;

import java.util.List;

public interface DistCategoryIndexFacade {

	List<DistCategoryIndexData> getCategoryIndexData();

	List<DistCategoryIndexData> getTopCategoryIndexData();

	List<DistCategoryIndexData> getTopCategoryDataForOCC();
}
