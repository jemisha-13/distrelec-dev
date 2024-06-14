package com.namics.distrelec.b2b.core.mock.sap;

import com.distrelec.webservice.sap.v1.CurrencyCode;

public class If08ReadCustomer {
    private String customerId;

    private String salesOrganization;

    private String customerType;

    private CurrencyCode currency;

    private String priceList;

    private boolean active;

    public If08ReadCustomer(String customerId, String salesOrganization, String customerType, CurrencyCode currency, String priceList, boolean active) {
        this.customerId = customerId;
        this.salesOrganization = salesOrganization;
        this.customerType = customerType;
        this.currency = currency;
        this.priceList = priceList;
        this.active = active;
    }

    public String getSalesOrganization() {
        return salesOrganization;
    }

    public void setSalesOrganization(String salesOrganization) {
        this.salesOrganization = salesOrganization;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyCode currency) {
        this.currency = currency;
    }

    public String getPriceList() {
        return priceList;
    }

    public void setPriceList(String priceList) {
        this.priceList = priceList;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
