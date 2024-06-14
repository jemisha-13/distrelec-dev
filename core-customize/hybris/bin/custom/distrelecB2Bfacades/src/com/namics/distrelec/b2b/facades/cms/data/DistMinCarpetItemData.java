/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.cms.data;

import de.hybris.platform.commercefacades.product.data.ProductData;

/**
 * {@code DistMinCarpetItemData}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class DistMinCarpetItemData {

    private DistCarpetContentTeaserData contentTeaser;
    private ProductData product;
    private Boolean isTeaser;

    public DistCarpetContentTeaserData getContentTeaser() {
        return contentTeaser;
    }

    public void setContentTeaser(final DistCarpetContentTeaserData contentTeaser) {
        this.contentTeaser = contentTeaser;
    }

    public ProductData getProduct() {
        return product;
    }

    public void setProduct(final ProductData product) {
        this.product = product;
    }

    public Boolean getIsTeaser() {
        return isTeaser;
    }

    public void setIsTeaser(final Boolean isTeaser) {
        this.isTeaser = isTeaser;
    }
}
