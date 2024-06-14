/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.rma;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * {@code ReturnRequestData}
 * 
 * @author <a href="abhinay.jadhav@datwyler.com">Abhinay Jadhav</a>, Datwyler
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0,7.3
 */
public class ReturnRequestData {

    private static final String I_SEP = "\";";
    private static final String CRLF = System.getProperty("line.separator");

    //
    private String requestId;
    private Date requestDate;
    private String orderCode;
    private Date purchaseDate;
    private List<ReturnRequestEntryData> entries;

    // Additional data for later usage.
    // XXX these data are used only to convert Return Request model to a ReturnRequestData
    private String salesOrgCode;
    private String customerId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String company;


    /**
     * Converts this RMA data object to a CSV format
     * 
     * @param dateFormatPattern
     *            the date format to be used
     * @return CSV representation of this RMA data object
     */
    public String toCSV(final String dateFormatPattern) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatPattern != null ? dateFormatPattern : "dd.MM.yyyy");

        final StringBuilder builder = new StringBuilder();
        builder.append('"').append(this.requestId != null ? this.requestId : "").append(I_SEP);
        builder.append('"').append(this.requestDate != null ? dateFormat.format(requestDate) : "").append(I_SEP);
        builder.append('"').append(this.salesOrgCode != null ? this.salesOrgCode : "").append(I_SEP);
        builder.append('"').append(this.customerId != null ? this.customerId : "").append(I_SEP);
        builder.append('"').append(this.lastName != null ? this.lastName : "").append(I_SEP);
        builder.append('"').append(this.firstName != null ? this.firstName : "").append(I_SEP);
        builder.append('"').append(this.phoneNumber != null ? this.phoneNumber : "").append(I_SEP);
        builder.append('"').append(this.email != null ? this.email : "").append(I_SEP);
        builder.append('"').append(this.company != null ? this.company : "").append(I_SEP);
        if (this.entries != null && !this.entries.isEmpty()) {
            for (final ReturnRequestEntryData entry : this.entries) {
                builder.append(CRLF).append("\"\";");
                builder.append('"').append(entry.getProductNumber() != null ? entry.getProductNumber() : "").append(I_SEP);
                builder.append('"').append(entry.getQuantity()).append(I_SEP);
                builder.append('"').append(entry.getProductName() != null ? entry.getProductName() : "").append(I_SEP);
                builder.append('"').append(entry.getReason() != null ? entry.getReason() : "").append(I_SEP);
                builder.append('"').append(entry.getPackaging() != null ? entry.getPackaging() : "").append(I_SEP);
                builder.append('"').append(this.orderCode != null ? this.orderCode : "").append(I_SEP);
                builder.append('"').append(entry.getSerialNumbers() != null ? entry.getSerialNumbers() : "").append(I_SEP);
                builder.append('"').append(this.purchaseDate != null ? dateFormat.format(purchaseDate) : "").append(I_SEP);
                builder.append('"').append(entry.getNote() != null ? entry.getNote() : "").append('"');
            }
        }
        return builder.append(CRLF).toString();
    }

    // Getters and Setters

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(final String orderCode) {
        this.orderCode = orderCode;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(final Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final String customerId) {
        this.customerId = customerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(final String company) {
        this.company = company;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(final Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getSalesOrgCode() {
        return salesOrgCode;
    }


    public void setSalesOrgCode(String salesOrgCode) {
        this.salesOrgCode = salesOrgCode;
    }

    public List<ReturnRequestEntryData> getEntries() {
        return entries;
    }

    public void setEntries(List<ReturnRequestEntryData> entries) {
        this.entries = entries;
    }
}
