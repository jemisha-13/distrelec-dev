/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import java.util.List;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * {@code DistCatalogPlusPriceRequestEvent}
 * 
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class DistCatalogPlusPriceRequestEvent extends AbstractCommerceUserEvent<BaseSiteModel> {

    private String company;
    private String firstName;
    private String lastName;
    private String customerEmail;
    private String phone;
    private Long quantity;
    private String comment;
    private List<ProductModel> products;

    /**
     * Default constructor.
     */
    public DistCatalogPlusPriceRequestEvent() {
        super();
    }

    /**
     * Parameterized constructor.
     */
    public DistCatalogPlusPriceRequestEvent(final String company, final String firstName, final String lastName, final String email, final String phone,
            final Long quantity, final String comment, final List<ProductModel> products) {
        this.company = company;
        this.firstName = firstName;
        this.lastName = lastName;
        this.customerEmail = email;
        this.phone = phone;
        this.quantity = quantity;
        this.comment = comment;
        this.products = products;
    }

    /* Getters and Setters */

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(final Long quantity) {
        this.quantity = quantity;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public List<ProductModel> getProducts() {
        return products;
    }

    public void setProduct(final List<ProductModel> products) {
        this.products = products;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setProducts(List<ProductModel> products) {
        this.products = products;
    }

}
