package com.namics.distrelec.b2b.facades.order.invoice.data;

import com.distrelec.webservice.if12.v1.SearchResponse;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class DistOnlineInvoiceSearchData {

    private SearchResponse.Invoices invoices;
    private String customerId;
    private BigInteger resultTotalSize;
    private String invoiceDocumentURL;
    private List<DistB2BInvoiceHistoryOrderData> relatedOrders;
    private String invoiceStatus;

    private String invoiceNumber;
    private Date invoiceDate;
    private Date invoiceDueDate;
    private Double invoiceTotal;
    private String currency;

    private Double invoiceTaxes;
    private Double invoiceTotalWithoutTaxes;
    private String invoiceType;

    public SearchResponse.Invoices getInvoices() {
        return invoices;
    }

    public void setInvoices(SearchResponse.Invoices invoices) {
        this.invoices = invoices;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public BigInteger getResultTotalSize() {
        return resultTotalSize;
    }

    public void setResultTotalSize(BigInteger resultTotalSize) {
        this.resultTotalSize = resultTotalSize;
    }

    public String getInvoiceDocumentURL() {
        return invoiceDocumentURL;
    }

    public void setInvoiceDocumentURL(String invoiceDocumentURL) {
        this.invoiceDocumentURL = invoiceDocumentURL;
    }

    public List<DistB2BInvoiceHistoryOrderData> getRelatedOrders() {
        return relatedOrders;
    }

    public void setRelatedOrders(List<DistB2BInvoiceHistoryOrderData> relatedOrders) {
        this.relatedOrders = relatedOrders;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Date getInvoiceDueDate() {
        return invoiceDueDate;
    }

    public void setInvoiceDueDate(Date invoiceDueDate) {
        this.invoiceDueDate = invoiceDueDate;
    }

    public Double getInvoiceTotal() {
        return invoiceTotal;
    }

    public void setInvoiceTotal(Double invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getInvoiceTaxes() {
        return invoiceTaxes;
    }

    public void setInvoiceTaxes(Double invoiceTaxes) {
        this.invoiceTaxes = invoiceTaxes;
    }

    public Double getInvoiceTotalWithoutTaxes() {
        return invoiceTotalWithoutTaxes;
    }

    public void setInvoiceTotalWithoutTaxes(Double invoiceTotalWithoutTaxes) {
        this.invoiceTotalWithoutTaxes = invoiceTotalWithoutTaxes;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }
}
