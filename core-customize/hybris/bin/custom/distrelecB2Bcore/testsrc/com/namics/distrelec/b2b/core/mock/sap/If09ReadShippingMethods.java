package com.namics.distrelec.b2b.core.mock.sap;

import java.util.List;

class If09ReadShippingMethods {

    private final String customerId;
    private final List<If09ReadShippingMethod> shippingMethods;

    public If09ReadShippingMethods(String customerId, List<If09ReadShippingMethod> shippingMethods) {
        this.customerId = customerId;
        this.shippingMethods = shippingMethods;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<If09ReadShippingMethod> getShippingMethods() {
        return shippingMethods;
    }
}
