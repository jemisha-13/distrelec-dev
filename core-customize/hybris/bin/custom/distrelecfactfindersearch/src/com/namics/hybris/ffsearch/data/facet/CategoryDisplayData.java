/*
 * Copyright 2000-2016 Distrelec Group AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.facet;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;

import java.util.List;
import java.util.Map;

/**
 * {@code CategoryDisplayData}
 *
 *
 * @author nshandilya, Distrelec
 * @since Distrelec 5.14
 */
public class CategoryDisplayData<STATE> extends FacetValueData<STATE> {

    private Map<String, ImageData> image;
    private List<CategoryDisplayData<STATE>> subcategoryDisplayDataList;
    private String url;
    private String description;
    private int level;
    private String promotionText;

    public Map<String, ImageData> getImage() {
        return image;
    }

    public void setImage(final Map<String, ImageData> image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public String getPromotionText() {
        return promotionText;
    }

    public void setPromotionText(final String promotionText) {
        this.promotionText = promotionText;
    }

    public List<CategoryDisplayData<STATE>> getSubcategoryDisplayDataList() {
        return subcategoryDisplayDataList;
    }

    public void setSubcategoryDisplayDataList(final List<CategoryDisplayData<STATE>> subcategoryDisplayDataList) {
        this.subcategoryDisplayDataList = subcategoryDisplayDataList;
    }

    @Override
    public String toString() {
        // return level + "_" + getCode() + "_" + getCount() + "_" + getName();
        return getCode() + "[" + getCount() + "]";
    }

    @Override
    public int hashCode() {
        // return (int) (level + getCode().hashCode() + getCount() + getName().hashCode());
        return getCode().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof CategoryDisplayData) {
            final CategoryDisplayData other = (CategoryDisplayData) obj;
            // return (level == other.getLevel() && getCode().equals(other.getCode()) && getCount() == other.getCount() &&
            // getName().equals(other.getName()));
            return getCode().equals(other.getCode());
        }
        return false;
    }

}
