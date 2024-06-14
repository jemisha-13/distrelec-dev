package com.namics.distrelec.b2b.facades.product.data;

import java.io.Serializable;
import java.util.List;

public class DistCategoryIndexDataList implements Serializable {

    private List<DistCategoryIndexData> categories;

    private List<DistCategoryIndexData> searchbarCategories;

    public List<DistCategoryIndexData> getCategories() {
        return categories;
    }

    public void setCategories(List<DistCategoryIndexData> categories) {
        this.categories = categories;
    }

    public List<DistCategoryIndexData> getSearchbarCategories() {
        return searchbarCategories;
    }

    public void setSearchbarCategories(List<DistCategoryIndexData> searchbarCategories) {
        this.searchbarCategories = searchbarCategories;
    }
}
