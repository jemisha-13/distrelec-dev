package com.namics.distrelec.b2b.core.mock.sap;

import com.distrelec.webservice.sap.v1.ContactStatus;
import com.distrelec.webservice.sap.v1.CustomerStatus;

public class If08FindContact {
    private String customerId;

    private CustomerStatus customerStatus;

    private ContactStatus contactStatus;

    private String contactId;

    public If08FindContact(String customerId, CustomerStatus customerStatus, ContactStatus contactStatus, String contactId) {
        this.customerId = customerId;
        this.customerStatus = customerStatus;
        this.contactStatus = contactStatus;
        this.contactId = contactId;
    }

    public CustomerStatus getCustomerStatus() {
        return customerStatus;
    }

    public void setCustomerStatus(CustomerStatus customerStatus) {
        this.customerStatus = customerStatus;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public ContactStatus getContactStatus() {
        return contactStatus;
    }

    public void setContactStatus(ContactStatus contactStatus) {
        this.contactStatus = contactStatus;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }
}
