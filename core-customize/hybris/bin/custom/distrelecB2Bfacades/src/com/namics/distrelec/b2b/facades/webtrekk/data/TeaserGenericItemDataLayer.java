/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.webtrekk.data;

public class TeaserGenericItemDataLayer {

    private String href;
    private String imgSrc;
    private int placement;

    public String getHref() {
        return href;
    }

    public void setHref(final String href) {
        this.href = href;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(final String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public int getPlacement() {
        return placement;
    }

    public void setPlacement(final int placement) {
        this.placement = placement;
    }
}
