package com.namics.distrelec.b2b.core.mock.sap;

import java.util.List;

class If10ReadPaymentMethods {

    private final String customerId;
    private final List<If10ReadPaymentMethod> paymentMethods;

    public If10ReadPaymentMethods(String customerId, List<If10ReadPaymentMethod> paymentMethods) {
        this.customerId = customerId;
        this.paymentMethods = paymentMethods;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<If10ReadPaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }
}
