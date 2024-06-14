/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import com.namics.distrelec.b2b.storefront.controllers.pages.AbstractSearchPageController.ShowMode;

import java.util.Date;

/**
 * Form Object for the order history
 * 
 * @author nbenothman Distrelec
 * @since Distrelec 1.0
 * 
 */
public class InvoiceHistoryFormIF12 {

    private int page;
    // Default value is 10
    private int pageSize = 10;
    private String invoiceNumber;
    private String orderNumber;
    private String contactId;
    private ShowMode show;
    private String sort;
    private String sortType = "DESC";
    private String status;
    private Date fromDate;
    private Date toDate;
    private Double minTotal;
    private Double maxTotal;

    /**
     * Create a new instance of {@code OrderHistoryForm}
     */
    public InvoiceHistoryFormIF12() {
        // NOPE
    }

    /**
     * Create a new instance of {@code InvoiceHistoryForm}
     * 
     * @param page
     * @param pageSize
     * @param invoiceNumber
     * @param orderNumber
     * @param contractId
     * @param show
     * @param sort
     * @param sortType
     * @param status
     * @param fromDate
     * @param toDate
     * @param minTotal
     * @param maxTotal
     */
    public InvoiceHistoryFormIF12(final int page, final int pageSize, final String invoiceNumber, final String orderNumber, final String contractId,
            final ShowMode show, final String sort, final String sortType, final String status, final Date fromDate, final Date toDate, final Double minTotal,
            final Double maxTotal) {
        super();
        this.page = page;
        this.pageSize = pageSize;
        this.invoiceNumber = invoiceNumber;
        this.orderNumber = orderNumber;
        this.contactId = contractId;
        this.show = show;
        this.sort = sort;
        this.sortType = sortType;
        this.status = status;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.minTotal = minTotal;
        this.maxTotal = maxTotal;
    }

    public int getPage() {
        return page;
    }

    public void setPage(final int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(final String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public ShowMode getShow() {
        return show;
    }

    public void setShow(final ShowMode show) {
        this.show = show;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(final String sortCode) {
        this.sort = sortCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
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

    public String getSortType() {
        return sortType;
    }

    public void setSortType(final String sortType) {
        if ("asc".equalsIgnoreCase(sortType) || "desc".equalsIgnoreCase(sortType)) {
            this.sortType = sortType.toUpperCase();
        }
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
}
