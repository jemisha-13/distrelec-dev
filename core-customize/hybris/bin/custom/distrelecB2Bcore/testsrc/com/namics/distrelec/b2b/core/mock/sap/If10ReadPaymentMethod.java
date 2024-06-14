package com.namics.distrelec.b2b.core.mock.sap;

public class If10ReadPaymentMethod {
    private final String paymentMethodCode;
    private final boolean isDefault;

    public If10ReadPaymentMethod(String paymentMethodCode, boolean isDefault) {
        this.paymentMethodCode = paymentMethodCode;
        this.isDefault = isDefault;
    }

    public String getPaymentMethodCode() {
        return paymentMethodCode;
    }

    public boolean isDefault() {
        return isDefault;
    }
}
