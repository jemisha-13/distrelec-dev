/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

/**
 * {@code DistRmaRequestEventEntry}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class DistRmaRequestEventEntry {

    private String amount;
    private String comment;
    private String returnReason;
    private String returnPackaging;
    private String serialNumbers;
    private String productNumber;
    private String productName;
    private String replacement;

    /**
     * Create a new instance of {@code DistRmaRequestEventEntry}
     */
    public DistRmaRequestEventEntry() {
        super();
    }

    /**
     * Create a new instance of {@code DistRmaRequestEventEntry}
     * 
     * @param amount
     * @param returnReason
     * @param returnPackaging
     * @param comment
     * @param serialNumbers
     * @param productNumber
     * @param productName
     */
    public DistRmaRequestEventEntry(final String amount, final String returnReason, final String returnPackaging, final String comment,
            final String serialNumbers, final String productNumber, final String productName) {
        this.amount = amount;
        this.returnReason = returnReason;
        this.returnPackaging = returnPackaging;
        this.comment = comment;
        this.serialNumbers = serialNumbers;
        this.productNumber = productNumber;
        this.productName = productName;
    }

    // Getters & Setters

    public String getAmount() {
        return amount;
    }

    public void setAmount(final String amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(final String returnReason) {
        this.returnReason = returnReason;
    }

    public String getReturnPackaging() {
        return returnPackaging;
    }

    public void setReturnPackaging(final String returnPackaging) {
        this.returnPackaging = returnPackaging;
    }

    public String getSerialNumbers() {
        return serialNumbers;
    }

    public void setSerialNumbers(final String serialNumbers) {
        this.serialNumbers = serialNumbers;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(final String productNumber) {
        this.productNumber = productNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(final String productName) {
        this.productName = productName;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(final String replacement) {
        this.replacement = replacement;
    }
}
