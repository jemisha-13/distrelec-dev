/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.event;

import java.util.Date;
import java.util.List;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * {@code DistNewProductsNewsLetterEvent}
 * 
 *
 * @since Distrelec 5.10
 */
public class DistNewProductsNewsLetterEvent extends AbstractCommerceUserEvent<BaseSiteModel> {

    private List<ProductModel> products;
    private Date fromdate;
    private Date todate;

    /**
     * Create a new instance of {@code DistNewProductsNewsLetterEvent}
     */
    public DistNewProductsNewsLetterEvent() {
        super();
    }

    /**
     * Create a new instance of {@code DistNewProductsNewsLetterEvent}
     * 
     * @param products
     * @param fromdate
     * @param todate
     */
    public DistNewProductsNewsLetterEvent(final List<ProductModel> products, final Date fromdate, final Date todate) {
        this.products = products;
        this.fromdate = fromdate;
        this.todate = todate;
    }

    public List<ProductModel> getProducts() {
        return products;
    }

    public void setProducts(final List<ProductModel> products) {
        this.products = products;
    }

    public Date getFromdate() {
        return fromdate;
    }

    public void setFromdate(final Date fromdate) {
        this.fromdate = fromdate;
    }

    public Date getTodate() {
        return todate;
    }

    public void setTodate(final Date todate) {
        this.todate = todate;
    }
}
