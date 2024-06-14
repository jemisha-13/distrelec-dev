/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.cms.data;

import de.hybris.platform.commercefacades.product.data.ImageData;

/**
 * {@code DistPaymentMethodComponentData}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.0
 */
public class DistPaymentMethodComponentData {

    private ImageData icon;
    private String url;
    private String title;

    /**
     * Create a new instance of {@code DistPaymentMethodComponentData}
     */
    public DistPaymentMethodComponentData() {
        super();
    }

    /**
     * Create a new instance of {@code DistPaymentMethodComponentData}
     *
     * @param icon
     * @param url
     * @param title
     */
    public DistPaymentMethodComponentData(final ImageData icon, final String url, final String title) {
        this.icon = icon;
        this.url = url;
        this.title = title;
    }

    public ImageData getIcon() {
        return icon;
    }

    public void setIcon(final ImageData icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
