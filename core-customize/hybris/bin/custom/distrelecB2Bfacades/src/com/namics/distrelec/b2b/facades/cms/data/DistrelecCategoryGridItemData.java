/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.cms.data;

import de.hybris.platform.commercefacades.product.data.ImageData;

/**
 * {@code DistrelecCategoryGridItemData}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.5
 */
public class DistrelecCategoryGridItemData {

    private String title;

    private String url;

    private ImageData thumbnail = new ImageData();

    private String categoryCode;

    /**
     * Create a new instance of {@code DistrelecCategoryGridItemData}
     */
    public DistrelecCategoryGridItemData() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public ImageData getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(final ImageData thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(final String categoryCode) {
        this.categoryCode = categoryCode;
    }
}
