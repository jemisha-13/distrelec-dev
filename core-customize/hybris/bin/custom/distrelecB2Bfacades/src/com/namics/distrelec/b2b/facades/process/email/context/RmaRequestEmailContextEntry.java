/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.process.email.context;

/**
 * {@code RmaRequestEmailContextEntry}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class RmaRequestEmailContextEntry {

    private String amount;
    private String returnReason;
    private String returnPackaging;
    private String comment;
    private String replacement;
    private String serialNumbers;
    private String productNumber;
    private String productName;

    /**
     * Create a new instance of {@code RmaRequestEmailContextEntry}
     */
    public RmaRequestEmailContextEntry() {
        super();
    }

    /**
     * Create a new instance of {@code RmaRequestEmailContextEntry}
     *
     * @param amount
     * @param returnReason
     * @param returnPackaging
     * @param comment
     * @param serialNumbers
     * @param productNumber
     * @param productName
     */
    public RmaRequestEmailContextEntry(final String amount, final String returnReason, final String returnPackaging, final String comment,
            final String serialNumbers, final String productNumber, final String productName, final String replacement) {
        this.amount = amount;
        this.returnReason = returnReason;
        this.returnPackaging = returnPackaging;
        this.comment = comment;
        this.serialNumbers = serialNumbers;
        this.productNumber = productNumber;
        this.productName = productName;
        this.replacement = replacement;
    }

    // Getters & Setters

    public String getAmount() {
        return amount;
    }

    public void setAmount(final String amount) {
        this.amount = amount;
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

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
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
