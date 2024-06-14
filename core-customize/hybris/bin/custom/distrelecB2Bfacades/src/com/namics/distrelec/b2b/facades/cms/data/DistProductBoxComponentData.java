/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.cms.data;

import java.util.List;

/**
 * {@code DistProductBoxComponentData}
 *
 * @author datneerajs, Distrelec Group AG
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec Group AG
 * @since Distrelec 5.6
 */
public class DistProductBoxComponentData {

    private List<DistCarpetItemData> items;
    private boolean horizontal;
    private boolean rotating;
    private boolean showLogo;
    private boolean checkout;
    private boolean heroProduct;
    private String title;

    public List<DistCarpetItemData> getItems() {
        return items;
    }

    public void setItems(final List<DistCarpetItemData> items) {
        this.items = items;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public void setHorizontal(final boolean horizontal) {
        this.horizontal = horizontal;
    }

    public boolean isRotating() {
        return rotating;
    }

    public void setRotating(final boolean rotating) {
        this.rotating = rotating;
    }

    public boolean isShowLogo() {
        return showLogo;
    }

    public void setShowLogo(final boolean showLogo) {
        this.showLogo = showLogo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public boolean isCheckout() {
        return checkout;
    }

    public void setCheckout(final boolean checkout) {
        this.checkout = checkout;
    }

    public boolean isHeroProduct() {
        return heroProduct;
    }

    public void setHeroProduct(final boolean heroProduct) {
        this.heroProduct = heroProduct;
    }
}
