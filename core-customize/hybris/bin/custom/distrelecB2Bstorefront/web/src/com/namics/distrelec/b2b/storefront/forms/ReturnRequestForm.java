/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;
import java.util.List;

/**
 * {@code ReturnRequestForm}
 * 
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class ReturnRequestForm {

    private String rmaCode;
    private String orderCode;
    private Date purchaseDate;
    private List<ReturnRequestRowForm> products;

    // Getters & Setters

    @JsonProperty("order-code")
    @NotBlank(message = "{lightboxreturnrequest.code.invalid}")
    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(final String orderCode) {
        this.orderCode = orderCode;
    }

    @JsonProperty("purchase-date")
    @NotNull(message = "{lightboxreturnrequest.purchaseDate.invalid}")
    @Past(message = "{lightboxreturnrequest.purchaseDate.date.invalid}")
    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(final Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    @Valid
    @NotEmpty
    @JsonProperty("products")
    public List<ReturnRequestRowForm> getProducts() {
        return products;
    }

    public void setProducts(final List<ReturnRequestRowForm> products) {
        this.products = products;
    }

    public String getRmaCode() {
        return rmaCode;
    }

    public void setRmaCode(final String rmaCode) {
        this.rmaCode = rmaCode;
    }
}
