/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.rma;



/**
 * {@code ReturnRequestEntryData}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class ReturnRequestEntryData {

    private String productName;
    private String productNumber;
    // unpacked
    private String packaging;
    private boolean replacement;
    private int quantity;
    private String reason;
    private String note;
    private String serialNumbers;

    /**
     * Create a new instance of {@code ReturnRequestEntryData}
     */
    public ReturnRequestEntryData() {
        super();
    }

    /**
     * Create a new instance of {@code ReturnRequestEntryData}
     * 
     * @param productName
     * @param productNumber
     * @param packaging
     * @param replacement
     * @param quantity
     * @param reason
     * @param note
     * @param serialNumbers
     */
    public ReturnRequestEntryData(final String productName, final String productNumber, final String packaging, final boolean replacement, final int quantity,
            final String reason, final String note, final String serialNumbers) {
        this.productName = productName;
        this.productNumber = productNumber;
        this.packaging = packaging;
        this.replacement = replacement;
        this.quantity = quantity;
        this.reason = reason;
        this.note = note;
        this.serialNumbers = serialNumbers;
    }

    // Getters & Setters

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public boolean isReplacement() {
        return replacement;
    }

    public void setReplacement(boolean replacement) {
        this.replacement = replacement;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSerialNumbers() {
        return serialNumbers;
    }

    public void setSerialNumbers(String serialNumbers) {
        this.serialNumbers = serialNumbers;
    }

}
