/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class AddressChangeForm {
    private String customerNumber;
    private String comment;
    private OfflineAddressForm oldAddress;
    private OfflineAddressForm newAddress;

    @NotBlank(message = "{address.change.customerNumber.invalid}")
    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(final String customerNumber) {
        this.customerNumber = customerNumber;
    }

    @NotNull(message = "{address.change.oldAddress.invalid}")
    @Valid
    public OfflineAddressForm getOldAddress() {
        return oldAddress;
    }

    public void setOldAddress(final OfflineAddressForm oldAddress) {
        this.oldAddress = oldAddress;
    }

    @NotNull(message = "{address.change.newAddress.invalid}")
    @Valid
    public OfflineAddressForm getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(final OfflineAddressForm newAddress) {
        this.newAddress = newAddress;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

}
