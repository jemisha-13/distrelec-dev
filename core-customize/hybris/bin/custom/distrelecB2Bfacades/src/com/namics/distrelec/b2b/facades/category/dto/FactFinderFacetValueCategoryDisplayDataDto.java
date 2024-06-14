/*
 *
 */
package com.namics.distrelec.b2b.facades.category.dto;

import com.namics.hybris.ffsearch.data.facet.FactFinderFacetValueData;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.search.data.SearchStateData;

/**
 * {@code FactFinderFacetValueCategoryDisplayDataDto}
 *
 *
 * @author nshandilya, Distrelec
 * @since Distrelec 5.14
 */
public class FactFinderFacetValueCategoryDisplayDataDto {

    private CategoryModel categoryModel;
    private FactFinderFacetValueData<SearchStateData> factFinderFacetValueData;

    public FactFinderFacetValueCategoryDisplayDataDto(final CategoryModel categoryModel,
            final FactFinderFacetValueData<SearchStateData> factFinderFacetValueData) {
        super();
        this.categoryModel = categoryModel;
        this.factFinderFacetValueData = factFinderFacetValueData;
    }

    public CategoryModel getCategoryModel() {
        return categoryModel;
    }

    public void setCategoryModel(final CategoryModel categoryModel) {
        this.categoryModel = categoryModel;
    }

    public FactFinderFacetValueData<SearchStateData> getFactFinderFacetValueData() {
        return factFinderFacetValueData;
    }

    public void setFactFinderFacetValueData(final FactFinderFacetValueData<SearchStateData> factFinderFacetValueData) {
        this.factFinderFacetValueData = factFinderFacetValueData;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("FactFinderFacetValueCategoryDisplayDataDto [");
        if (categoryModel != null) {
            builder.append("categoryModel=");
            builder.append(categoryModel.getCode());
            builder.append(", ");
        }
        if (factFinderFacetValueData != null) {
            builder.append("factFinderFacetValueData=");
            builder.append(factFinderFacetValueData);
        }
        builder.append("]");
        return builder.toString();
    } 

}
