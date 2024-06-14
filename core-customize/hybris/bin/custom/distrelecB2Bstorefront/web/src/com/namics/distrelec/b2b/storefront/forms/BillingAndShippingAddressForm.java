package com.namics.distrelec.b2b.storefront.forms;

import javax.validation.constraints.NotBlank;

public class BillingAndShippingAddressForm {

    private String addressId;

    private boolean billingAndShippingAddress;

    @NotBlank
    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public boolean isBillingAndShippingAddress() {
        return billingAndShippingAddress;
    }

    public void setBillingAndShippingAddress(boolean billingAndShippingAddress) {
        this.billingAndShippingAddress = billingAndShippingAddress;
    }
}
