/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.search.pagedata;

import java.util.Date;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;

/**
 * {@code QutationHistoryPageableData}
 * <p>
 * POJO representation of search query pagination.
 * </p>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.5
 */
public class QuotationHistoryPageableData extends PageableData {

    private String quotationId;
    private String quotationReference;
    private String contactId;
    private Date fromDate;
    private Date toDate;
    private String sortType = "DESC";

    // additional filter for sap implementation
    private String filterCurrencyCode;
    private String filterArticleNumber;
    private String filterTypeName;
    protected String filterPONumber;
    private Double minTotal;
    private Double maxTotal;
    private String status;
    private Date filterExpiryFromDate;
    private Date filterExpiryToDate;

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

    public String getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(final String quotationId) {
        this.quotationId = quotationId;
    }

    public String getFilterArticleNumber() {
        return filterArticleNumber;
    }

    public void setFilterArticleNumber(final String filterArticleNumber) {
        this.filterArticleNumber = filterArticleNumber;
    }

    public String getFilterTypeName() {
        return filterTypeName;
    }

    public void setFilterTypeName(final String filterTypeName) {
        this.filterTypeName = filterTypeName;
    }

    public Date getFilterExpiryFromDate() {
        return filterExpiryFromDate;
    }

    public void setFilterExpiryFromDate(final Date filterExpiryFromDate) {
        this.filterExpiryFromDate = filterExpiryFromDate;
    }

    public Date getFilterExpiryToDate() {
        return filterExpiryToDate;
    }

    public void setFilterExpiryToDate(final Date filterExpiryToDate) {
        this.filterExpiryToDate = filterExpiryToDate;
    }

    public String getQuotationReference() {
        return quotationReference;
    }

    public void setQuotationReference(final String quotationReference) {
        this.quotationReference = quotationReference;
    }

    public String getFilterPONumber() {
        return filterPONumber;
    }

    public void setFilterPONumber(final String filterPONumber) {
        this.filterPONumber = filterPONumber;
    }
}
