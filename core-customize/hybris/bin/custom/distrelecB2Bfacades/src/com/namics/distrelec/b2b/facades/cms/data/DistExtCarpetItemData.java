/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.cms.data;

import de.hybris.platform.commercefacades.product.data.PriceData;

/**
 * {@code DistExtCarpetItemData}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class DistExtCarpetItemData extends DistMinCarpetItemData {

    private Boolean showOriginalPrice;

    private String youTubeID;

    // Discounted price
    private PriceData price;

    // Original Price
    private PriceData originalPrice;

    private String discount;

    public Boolean getShowOriginalPrice() {
        return showOriginalPrice;
    }

    public void setShowOriginalPrice(final Boolean showOriginalPrice) {
        this.showOriginalPrice = showOriginalPrice;
    }

    public String getYouTubeID() {
        return youTubeID;
    }

    public void setYouTubeID(final String youTubeID) {
        this.youTubeID = youTubeID;
    }

    public PriceData getPrice() {
        return price;
    }

    public void setPrice(final PriceData price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(final String discount) {
        this.discount = discount;
    }

    public PriceData getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(final PriceData originalPrice) {
        this.originalPrice = originalPrice;
    }
}
