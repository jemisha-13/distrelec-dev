package com.namics.distrelec.b2b.core.mock.sap;

import com.distrelec.webservice.sap.v1.ShippingMethodCode;

class If09UpdateDefaultShippingMethod {

    private final String customerId;
    private final ShippingMethodCode shippingMethodCode;
    private final boolean successful;

    public If09UpdateDefaultShippingMethod(String customerId, ShippingMethodCode shippingMethodCode, boolean successful) {
        this.customerId = customerId;
        this.shippingMethodCode = shippingMethodCode;
        this.successful = successful;
    }

    public String getCustomerId() {
        return customerId;
    }

    public ShippingMethodCode getShippingMethodCode() {
        return shippingMethodCode;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
