/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import javax.validation.constraints.NotNull;

/**
 * Form object for quote product price.
 */
public class QuoteProductPriceForm {

    private Long modalQuotationQuantity;
    private String modalQuotationMessage;

    // Only for OCI/Ariba. For the rest they will be empty.
    private String company;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;

    @NotNull
    public Long getModalQuotationQuantity() {
        return modalQuotationQuantity;
    }

    public void setModalQuotationQuantity(final Long modalQuotationQuantity) {
        this.modalQuotationQuantity = modalQuotationQuantity;
    }

    public String getModalQuotationMessage() {
        return modalQuotationMessage;
    }

    public void setModalQuotationMessage(final String modalQuotationMessage) {
        this.modalQuotationMessage = modalQuotationMessage;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(final String company) {
        this.company = company;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

}
