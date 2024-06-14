/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.search.pagedata;

import java.util.Date;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;

/**
 * POJO representation of search query pagination .
 * 
 * @author <a href="mailto:nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec AG
 * @since Namics Extensions 1.0
 */
public class DistInvoiceHistoryPageableData extends PageableData {

    private String orderNumber;
    private String invoiceNumber;
    private String contactId;
    private Date fromDate;
    private Date toDate;
    private Double minTotal;
    private Double maxTotal;
    private String status;
    private String sortType = "DESC";

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(final String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(final String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(final String contactId) {
        this.contactId = contactId;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(final Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(final Date toDate) {
        this.toDate = toDate;
    }

    public Double getMinTotal() {
        return minTotal;
    }

    public void setMinTotal(final Double minTotal) {
        this.minTotal = minTotal;
    }

    public Double getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(final Double maxTotal) {
        this.maxTotal = maxTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(final String sortType) {
        if ("ASC".equalsIgnoreCase(sortType) || "DESC".equalsIgnoreCase(sortType)) {
            this.sortType = sortType;
        }
    }
}
