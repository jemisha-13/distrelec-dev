package com.namics.distrelec.b2b.core.mock.sap;

public class If08UpdateContact {
    private String customerId;

    private boolean successful;

    public If08UpdateContact(String customerId, boolean successful) {
        this.customerId = customerId;
        this.successful = successful;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
