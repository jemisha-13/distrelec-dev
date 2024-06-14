/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import com.namics.distrelec.b2b.storefront.controllers.pages.AbstractSearchPageController.ShowMode;

/**
 * Form Object for the order history
 *
 * @author nbenothman Distrelec
 * @since Distrelec 1.0
 *
 */
public class OrderHistoryForm {

    private int page;
    // Default value is 10
    private int pageSize = 10;
    private String orderNumber;
    private String contactId;
    private ShowMode show;
    private String sort;
    private String sortType = "DESC";
    private String status;
    private String fromDate;
    private String toDate;

    // additional filter for sap implementation
    private String currencyCode;
    private String invoiceNumber;
    private Double minTotal;
    private Double maxTotal;
    private String reference;
    private String productNumber;
    private String filterContactId;

    /**
     * Create a new instance of {@code OrderHistoryForm}
     */
    public OrderHistoryForm() {
        // NOPE
    }

    /**
     * Create a new instance of {@code OrderHistoryForm}
     *
     * @param orderNumber
     * @param page
     * @param pageSize
     * @param show
     * @param sortCode
     * @param fromDate
     * @param toDate
     */
    public OrderHistoryForm(final String orderNumber, final String contactId, final int page, final int pageSize, final ShowMode show, final String sortCode,
            final String fromDate, final String toDate) {
        this(orderNumber, contactId, page, pageSize, show, sortCode, fromDate, toDate, null, null);
    }

    /**
     * Create a new instance of {@code OrderHistoryForm}
     *
     * @param orderNumber
     * @param page
     * @param pageSize
     * @param show
     * @param sortCode
     * @param status
     * @param fromDate
     * @param toDate
     * @param sortType
     */
    public OrderHistoryForm(final String orderNumber, final String contactId, final int page, final int pageSize, final ShowMode show, final String sortCode,
            final String fromDate, final String toDate, final String status, final String sortType) {
        this.orderNumber = orderNumber;
        this.contactId = contactId;
        this.page = page;
        this.pageSize = pageSize;
        this.show = show;
        this.sort = sortCode;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.status = status;
        if ("ASC".equalsIgnoreCase(sortType) || "DESC".equalsIgnoreCase(sortType)) {
            this.sortType = sortType.toUpperCase();
        }
    }

    /**
     * Create a new instance of {@code OrderHistoryForm}
     *
     * @param page
     * @param pageSize
     * @param orderNumber
     * @param show
     * @param sort
     * @param sortType
     * @param status
     * @param fromDate
     * @param toDate
     * @param currencyCode
     * @param invoiceNumber
     * @param minTotal
     * @param maxTotal
     */
    public OrderHistoryForm(final int page, final int pageSize, final String orderNumber, final ShowMode show, final String sort, final String sortType,
            final String status, final String fromDate, final String toDate, final String currencyCode, final String invoiceNumber, final Double minTotal,
            final Double maxTotal) {
        super();
        this.page = page;
        this.pageSize = pageSize;
        this.orderNumber = orderNumber;
        this.show = show;
        this.sort = sort;
        this.sortType = sortType;
        this.status = status;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.currencyCode = currencyCode;
        this.invoiceNumber = invoiceNumber;
        this.minTotal = minTotal;
        this.maxTotal = maxTotal;
    }

    /**
     * Create a new instance of {@code OrderHistoryForm}
     *
     * @param page
     * @param pageSize
     * @param orderNumber
     * @param show
     * @param sort
     * @param sortType
     * @param status
     * @param fromDate
     * @param toDate
     * @param currencyCode
     * @param invoiceNumber
     * @param minTotal
     * @param maxTotal
     * @param reference
     */

    public OrderHistoryForm(final int page, final int pageSize, final String orderNumber, final ShowMode show, final String sort, final String sortType,
            final String status, final String fromDate, final String toDate, final String currencyCode, final String invoiceNumber, final Double minTotal,
            final Double maxTotal, final String reference) {
        super();
        this.page = page;
        this.pageSize = pageSize;
        this.orderNumber = orderNumber;
        this.show = show;
        this.sort = sort;
        this.sortType = sortType;
        this.status = status;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.currencyCode = currencyCode;
        this.invoiceNumber = invoiceNumber;
        this.minTotal = minTotal;
        this.maxTotal = maxTotal;
        this.reference = reference;
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

    public ShowMode getShow() {
        return show;
    }

    public void setShow(final ShowMode show) {
        this.show = show;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(final String sort) {
        this.sort = sort;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(final String sortType) {
        if ("ASC".equalsIgnoreCase(sortType) || "DESC".equalsIgnoreCase(sortType)) {
            this.sortType = sortType.toUpperCase();
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
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

    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(final String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(final String toDate) {
        this.toDate = toDate;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getFilterContactId() {
        return filterContactId;
    }

    public void setFilterContactId(String filterContactId) {
        this.filterContactId = filterContactId;
    }
}
