/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.data;

import org.apache.commons.lang.builder.ToStringBuilder;

public class FindContactResponseData {
    private boolean customerFound;
    private String erpCustomerId;
    private boolean contactFound;
    private boolean contactUnique;
    private String erpContactId;

    public boolean isCustomerFound() {
        return customerFound;
    }

    public void setCustomerFound(final boolean customerFound) {
        this.customerFound = customerFound;
    }

    public String getErpCustomerId() {
        return erpCustomerId;
    }

    public void setErpCustomerId(final String erpCustomerId) {
        this.erpCustomerId = erpCustomerId;
    }

    public boolean isContactFound() {
        return contactFound;
    }

    public void setContactFound(final boolean contactFound) {
        this.contactFound = contactFound;
    }

    public boolean isContactUnique() {
        return contactUnique;
    }

    public void setContactUnique(final boolean contactUnique) {
        this.contactUnique = contactUnique;
    }

    public String getErpContactId() {
        return erpContactId;
    }

    public void setErpContactId(final String erpContactId) {
        this.erpContactId = erpContactId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
