/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.data.quote;

import java.util.Date;
import java.util.List;

/**
 * {@code QuotationRequestData}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.5
 */
public class QuotationRequestData {

    private String quotationId;
    private List<QuoteRequestItem> items;
    private String quotationStatus;
    private String quotationStatusText;
    private Date quotationRequestDate;
    private Date quotationExpiryDate;
    private String POnumber;
    private String customerName;
    private Double total;
    private String currencyCode;
    private Double quotationTotal;

    // Getters and Setters

    public String getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(final String quotationId) {
        this.quotationId = quotationId;
    }

    public String getQuotationStatus() {
        return quotationStatus;
    }

    public void setQuotationStatus(final String quotationStatus) {
        this.quotationStatus = quotationStatus;
    }

    public String getQuotationStatusText() {
        return quotationStatusText;
    }

    public void setQuotationStatusText(final String quotationStatusText) {
        this.quotationStatusText = quotationStatusText;
    }

    public Date getQuotationRequestDate() {
        return quotationRequestDate;
    }

    public void setQuotationRequestDate(final Date quotationRequestDate) {
        this.quotationRequestDate = quotationRequestDate;
    }

    public Date getQuotationExpiryDate() {
        return quotationExpiryDate;
    }

    public void setQuotationExpiryDate(final Date quotationExpiryDate) {
        this.quotationExpiryDate = quotationExpiryDate;
    }

    public String getPOnumber() {
        return POnumber;
    }

    public void setPOnumber(final String pOnumber) {
        POnumber = pOnumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(final String customerName) {
        this.customerName = customerName;
    }

    public List<QuoteRequestItem> getItems() {
        return items;
    }

    public void setItems(final List<QuoteRequestItem> items) {
        this.items = items;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(final Double total) {
        this.total = total;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Double getQuotationTotal() {
        return quotationTotal;
    }

    public void setQuotationTotal(final Double quotationTotal) {
        this.quotationTotal = quotationTotal;
    }

}
