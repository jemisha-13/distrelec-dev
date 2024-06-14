/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.cms.data;

import de.hybris.platform.commercefacades.product.data.ImageData;

/**
 * {@code DistFooterComponentItemData}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class DistFooterComponentItemData {

    private String text;
    private ImageData icon;

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public ImageData getIcon() {
        return icon;
    }

    public void setIcon(final ImageData icon) {
        this.icon = icon;
    }
}
