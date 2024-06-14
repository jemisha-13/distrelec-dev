package com.namics.distrelec.b2b.core.service.search.pagedata;

import com.distrelec.webservice.if12.v1.SortCriteriaType;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DistOnlineInvoiceHistoryPageableData extends PageableData {

    private static final String OPEN = "open";
    private static final String PAID = "paid";
    private static final String ZERO_ONE = "01";
    private static final String ZERO_TWO = "02";
    private static final String ZERO_ZERO = "00";
    private static final String COMMA = ",";
    private static final String BLANK_STRING = "";
    private List<String> invoiceNumbers;
    private String salesOrganisation;
    private String customerID;
    private List<String> contactPersonIDs;
    private Date invoiceDateFrom;
    private Date invoiceDateTo;
    private BigDecimal totalAmountFrom;
    private BigDecimal totalAmountTo;
    private String invoiceStatusType;
    private Date dueDateFrom;
    private Date dueDateTo;
    private List<String> salesOrderNumbers;
    private List<String> salesOrderReferenceNumbers;
    private List<String> invoicesContainingArticle;
    private SortCriteriaType sortCriteriaType;
    private boolean sortAscending;
    private int resultSize;
    private int resultOffset;


    public DistOnlineInvoiceHistoryPageableData(){
        this.invoiceNumbers = new ArrayList<String>();
        this.contactPersonIDs  = new ArrayList<String>();
        this.salesOrderNumbers  = new ArrayList<String>();
        this.salesOrderReferenceNumbers  = new ArrayList<String>();
        this.invoicesContainingArticle  = new ArrayList<String>();
    }

    public List<String> getInvoiceNumbers() {
        return this.invoiceNumbers;
    }

    public String getSalesOrganisation() {
        return salesOrganisation;
    }

    public void setSalesOrganisation(String salesOrganisation) {
        this.salesOrganisation = salesOrganisation;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public List<String> getContactPersonIDs() {
        return this.contactPersonIDs;
    }

    public Date getInvoiceDateFrom() {
        return invoiceDateFrom;
    }

    public void setInvoiceDateFrom(Date invoiceDateFrom) {
        this.invoiceDateFrom = invoiceDateFrom;
    }

    public Date getInvoiceDateTo() {
        return invoiceDateTo;
    }

    public void setInvoiceDateTo(Date invoiceDateTo) {
        this.invoiceDateTo = invoiceDateTo;
    }

    public BigDecimal getTotalAmountFrom() {
        return totalAmountFrom;
    }

    public void setTotalAmountFrom(BigDecimal totalAmountFrom) {
        this.totalAmountFrom = totalAmountFrom;
    }

    public BigDecimal getTotalAmountTo() {
        return totalAmountTo;
    }

    public void setTotalAmountTo(BigDecimal totalAmountTo) {
        this.totalAmountTo = totalAmountTo;
    }

    public String getInvoiceStatusType() {
        return invoiceStatusType;
    }

    public void setInvoiceStatusType(String invoiceStatusType) {
        this.invoiceStatusType = mapStatus(invoiceStatusType);
    }

    private String mapStatus(String invoiceStatusType){
        String [] status = invoiceStatusType.split(COMMA);
        String convertedStatus = BLANK_STRING;
        switch(status[0].toLowerCase()){
            case OPEN:
                convertedStatus = ZERO_ONE;
                break;
            case PAID:
                convertedStatus = ZERO_TWO;
                break;
            default:
                convertedStatus = ZERO_ZERO;
                break;

        }
        return convertedStatus;
    }

    public Date getDueDateFrom() {
        return dueDateFrom;
    }

    public void setDueDateFrom(Date dueDateFrom) {
        this.dueDateFrom = dueDateFrom;
    }

    public Date getDueDateTo() {
        return dueDateTo;
    }

    public void setDueDateTo(Date dueDateTo) {
        this.dueDateTo = dueDateTo;
    }

    public List<String> getSalesOrderNumbers() {
        return this.salesOrderNumbers;
    }

    public List<String> getSalesOrderReferenceNumbers() {
        return this.salesOrderReferenceNumbers;
    }


    public List<String> getInvoicesContainingArticle() {
        return this.invoicesContainingArticle;
    }


    public SortCriteriaType getSortCriteriaType() {
        return sortCriteriaType;
    }

    public void setSortCriteriaType(SortCriteriaType sortCriteriaType) {
        this.sortCriteriaType = sortCriteriaType;
    }

    public boolean isSortAscending() {
        return sortAscending;
    }

    public void setSortAscending(boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    public int getResultSize() {
        return resultSize;
    }

    public void setResultSize(int resultSize) {
        this.resultSize = resultSize;
    }

    public int getResultOffset() {
        return resultOffset;
    }

    public void setResultOffset(int resultOffset) {
        this.resultOffset = resultOffset;
    }
}
