/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import com.namics.distrelec.b2b.storefront.controllers.pages.AbstractSearchPageController.ShowMode;

import java.util.Date;

/**
 * {@code QuotationHistoryForm}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.5
 */
public class QuotationHistoryForm {

    private int page = 1;
    // Default value is 10
    private int pageSize = 10;
    private String quotationReference;
    private String contactId;
    private String quotationId;
    private ShowMode show;
    private String sort;
    private String sortType = "DESC";
    private String status;
    private Date fromDate;
    private Date toDate;
    private Date expiryFromDate;
    private Date expiryToDate;

    // additional filter for sap implementation
    private String currencyCode;
    private Double minTotal;
    private Double maxTotal;
    private String articleNumber;
    private String pONumber;

    /**
     * Create a new instance of {@code QuotationHistoryForm}
     */
    public QuotationHistoryForm() {
        // NOPE
    }

    /**
     * Create a new instance of {@code QuotationHistoryForm}
     *
     * @param page
     * @param pageSize
     * @param show
     * @param sortCode
     * @param fromDate
     * @param toDate
     */
    public QuotationHistoryForm(final String quotationId, final String contactId, final int page, final int pageSize, final ShowMode show,
            final String sortCode, final Date fromDate, final Date toDate) {
        this(quotationId, contactId, page, pageSize, show, sortCode, fromDate, toDate, null, null);
    }

    /**
     * Create a new instance of {@code QuotationHistoryForm}
     *
     * @param page
     * @param pageSize
     * @param show
     * @param sortCode
     * @param status
     * @param fromDate
     * @param toDate
     * @param sortType
     */
    public QuotationHistoryForm(final String quotationId, final String contactId, final int page, final int pageSize, final ShowMode show,
            final String sortCode, final Date fromDate, final Date toDate, final String status, final String sortType) {
        this.quotationId = quotationId;
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
     * Create a new instance of {@code QuotationHistoryForm}
     *
     * @param page
     * @param pageSize
     * @param show
     * @param sort
     * @param sortType
     * @param status
     * @param fromDate
     * @param toDate
     * @param currencyCode
     * @param minTotal
     * @param maxTotal
     */
    public QuotationHistoryForm(final int page, final int pageSize, final ShowMode show, final String sort, final String sortType, final String status,
            final Date fromDate, final Date toDate, final String currencyCode, final Double minTotal, final Double maxTotal) {
        super();
        this.page = page;
        this.pageSize = pageSize;
        this.show = show;
        this.sort = sort;
        this.sortType = sortType;
        this.status = status;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.currencyCode = currencyCode;
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

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
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

    public String getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(final String quotationId) {
        this.quotationId = quotationId;
    }

    public String getQuotationReference() {
        return quotationReference;
    }

    public void setQuotationReference(String quotationReference) {
        this.quotationReference = quotationReference;
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public String getPONumber() {
        return pONumber;
    }

    public void setPONumber(final String pONumber) {
        this.pONumber = pONumber;
    }

    public Date getExpiryToDate() {
        return expiryToDate;
    }

    public void setExpiryToDate(final Date expiryToDate) {
        this.expiryToDate = expiryToDate;
    }

    public Date getExpiryFromDate() {
        return expiryFromDate;
    }

    public void setExpiryFromDate(final Date expiryFromDate) {
        this.expiryFromDate = expiryFromDate;
    }
}
