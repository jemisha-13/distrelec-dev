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
public class DistOrderHistoryPageableData extends PageableData {

    private String orderNumber;
    private String contactId;
    private Date fromDate;
    private Date toDate;
    private String sortType = "DESC";

    // additional filter for sap implementation
    private String filterCurrencyCode;
    private String invoiceNumber;
    private Double minTotal;
    private Double maxTotal;
    private String status;
    private String reference;
    private String productNumber;
    private String filterContactId;

    public DistOrderHistoryPageableData() {
        super();
    }

    public DistOrderHistoryPageableData(final int pageNumber, final int pageSize, final String sortCode, final String sortType) {
        super();
        this.setCurrentPage(pageNumber);
        this.setPageSize(pageSize);
        this.setSort(sortCode);
        this.setSortType(sortType);
    }

    public DistOrderHistoryPageableData(final String orderNumber) {
        super();
        this.orderNumber = orderNumber;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(final String orderNumber) {
        this.orderNumber = orderNumber;
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

    public String getSortType() {
        return sortType;
    }

    public void setSortType(final String sortType) {
        if ("ASC".equalsIgnoreCase(sortType) || "DESC".equalsIgnoreCase(sortType)) {
            this.sortType = sortType;
        }
    }

    public String getFilterCurrencyCode() {
        return filterCurrencyCode;
    }

    public void setFilterCurrencyCode(final String filterCurrencyCode) {
        this.filterCurrencyCode = filterCurrencyCode;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(final String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
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

    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(final String productNumber) {
        this.productNumber = productNumber;
    }

    public String getFilterContactId() {
        return filterContactId;
    }

    public void setFilterContactId(String filterContactId) {
        this.filterContactId = filterContactId;
    }
}
