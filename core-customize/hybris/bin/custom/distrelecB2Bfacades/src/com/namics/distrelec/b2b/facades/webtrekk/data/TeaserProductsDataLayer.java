/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.webtrekk.data;

public class TeaserProductsDataLayer {

    private String productID;
    private String Instrument;
    private int placement;

    public String getProductID() {
        return productID;
    }

    public void setProductID(final String productID) {
        this.productID = productID;
    }

    public String getInstrument() {
        return Instrument;
    }

    public void setInstrument(final String instrument) {
        Instrument = instrument;
    }

    public int getPlacement() {
        return placement;
    }

    public void setPlacement(final int placement) {
        this.placement = placement;
    }
}
