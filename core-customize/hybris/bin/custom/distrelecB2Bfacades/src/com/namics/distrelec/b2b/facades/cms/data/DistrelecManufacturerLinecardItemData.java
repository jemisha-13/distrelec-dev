/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.cms.data;

import de.hybris.platform.commercefacades.product.data.ImageData;

/**
 * {@code DistrelecManufacturerLinecardItemData}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.6
 */
public class DistrelecManufacturerLinecardItemData {

    private String title;

    private String url;

    private final ImageData icon;

    /**
     * Create a new instance of {@code DistrelecManufacturerLinecardItemData}
     */
    public DistrelecManufacturerLinecardItemData() {
        this.icon = new ImageData();
    }

    /**
     * Create a new instance of {@code DistrelecManufacturerLinecardItemData}
     * 
     * @param title
     * @param url
     * @param icon
     */
    public DistrelecManufacturerLinecardItemData(final String title, final String url, final ImageData icon) {
        this.title = title;
        this.url = url;
        this.icon = icon != null ? icon : new ImageData();
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

    public ImageData getIcon() {
        return icon;
    }
}
