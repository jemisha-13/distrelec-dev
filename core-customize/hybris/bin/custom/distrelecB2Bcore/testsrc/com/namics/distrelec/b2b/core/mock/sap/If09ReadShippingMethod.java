package com.namics.distrelec.b2b.core.mock.sap;

import com.distrelec.webservice.sap.v1.ShippingMethodCode;

public class If09ReadShippingMethod {

    private final ShippingMethodCode shippingMethodCode;
    private final boolean isDefault;

    public If09ReadShippingMethod(ShippingMethodCode shippingMethodCode, boolean isDefault) {
        this.shippingMethodCode = shippingMethodCode;
        this.isDefault = isDefault;
    }

    public ShippingMethodCode getShippingMethodCode() {
        return shippingMethodCode;
    }

    public boolean isDefault() {
        return isDefault;
    }
}
