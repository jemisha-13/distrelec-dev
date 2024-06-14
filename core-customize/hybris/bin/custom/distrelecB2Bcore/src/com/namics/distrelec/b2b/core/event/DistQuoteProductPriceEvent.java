/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * Event object for quote product price functionality.
 */
public class DistQuoteProductPriceEvent extends AbstractCommerceUserEvent<BaseSiteModel> {

    private String company;
    private String firstName;
    private String lastName;
    private String customerEmail;
    private String phone;
    private Long quantity;
    private String comment;
    private ProductModel product;

    /**
     * Default constructor.
     */
    public DistQuoteProductPriceEvent() {
        super();
    }

    /**
     * Parameterized constructor.
     */

    public DistQuoteProductPriceEvent(final String company, final String firstName, final String lastName, final String email, final String phone,
            final Long quantity, final String comment, final ProductModel product) {
        super();
        this.company = company;
        this.firstName = firstName;
        this.lastName = lastName;
        this.customerEmail = email;
        this.phone = phone;
        this.quantity = quantity;
        this.comment = comment;
        this.product = product;
    }

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

    public ProductModel getProduct() {
        return product;
    }

    public void setProduct(final ProductModel product) {
        this.product = product;
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
}
