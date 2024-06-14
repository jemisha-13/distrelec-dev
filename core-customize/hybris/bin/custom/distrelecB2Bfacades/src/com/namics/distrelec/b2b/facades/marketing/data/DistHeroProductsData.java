/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.marketing.data;

import java.util.List;

import de.hybris.platform.commercefacades.product.data.ProductData;

/**
 * {@code DistHeroProductsData}
 * 
 * 
 * @author <a href="wilhelm-patrick.spalinger@distrelec.com">Wilhelm Spalinger</a>, Distrelec
 * @since Distrelec 4.13
 */
public class DistHeroProductsData {

    List<ProductData> products;

    public DistHeroProductsData() {

    }

    public DistHeroProductsData(final List<ProductData> products) {
        this.products = products;
    }

    public List<ProductData> getProducts() {
        return products;
    }

    public void setProducts(final List<ProductData> products) {
        this.products = products;
    }

}
