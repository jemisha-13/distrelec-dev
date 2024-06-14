/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.data.quote;

import de.hybris.platform.commercefacades.product.data.ProductData;

/**
 * {@code QuoteRequestItem}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.0
 */
public class QuoteRequestItem {

    private ProductData product;
    private int quantity;
    private Double price;
    private String currencyIso;

    /**
     * Create a new instance of {@code QuoteRequestItem}
     */
    public QuoteRequestItem() {
    }

    public ProductData getProduct() {
        return product;
    }

    public void setProduct(final ProductData product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(final int quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(final Double price) {
        this.price = price;
    }

    public String getCurrencyIso() {
        return currencyIso;
    }

    public void setCurrencyIso(final String currencyIso) {
        this.currencyIso = currencyIso;
    }
}
