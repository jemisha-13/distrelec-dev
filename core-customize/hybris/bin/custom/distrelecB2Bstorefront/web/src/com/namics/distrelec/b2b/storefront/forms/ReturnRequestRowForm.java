/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * {@code ReturnRequest3Form}
 * 
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class ReturnRequestRowForm {

    private int quantity;
    private String reason;
    private String note;
    private String serialNumbers;
    private String productCode;
    private String packaging;
    private boolean replacement;

    // Getters & Setters
    @Min(value = 1)
    @JsonProperty("quantity")
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(final int quantity) {
        this.quantity = quantity;
    }

    @JsonProperty("reason")
    @NotBlank(message = "{lightboxreturnrequest.returnReason.invalid}")
    public String getReason() {
        return reason;
    }

    public void setReason(final String reason) {
        this.reason = reason;
    }

    @JsonProperty("note")
    public String getNote() {
        return note;
    }

    public void setNote(final String note) {
        this.note = note;
    }

    @JsonProperty("serial-numbers")
    public String getSerialNumbers() {
        return serialNumbers;
    }

    public void setSerialNumbers(final String serialNumbers) {
        this.serialNumbers = serialNumbers;
    }

    @JsonProperty("product-code")
    @NotBlank(message = "{lightboxreturnrequest.serialNumber.invalid}")
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(final String productCode) {
        this.productCode = productCode;
    }

    @JsonProperty("packaging")
    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(final String packaging) {
        this.packaging = packaging;
    }

    @JsonProperty("replacement")
    public boolean isReplacement() {
        return replacement;
    }

    public void setReplacement(final boolean replacement) {
        this.replacement = replacement;
    }

}
