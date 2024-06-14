/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.namics.distrelec.b2b.storefront.validation.annotations.Phonenumber;

public class B2BBillingAddressForm extends AbstractB2BAddressForm {

    private String phoneNumber;

    private String mobileNumber;

    private String faxNumber;

    @Phonenumber(message = "{form.phone.error.for.country}")
    @Size(max = 30, message = "{Size.phoneNumber}")
    @NotBlank(message = "{form.phone.error.for.country}")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Phonenumber(message = "{form.phone.error.for.country}")
    @Size(max = 30, message = "{Size.mobileNumber}")
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(final String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Phonenumber(message = "{form.phone.error.for.country}")
    @Size(max = 30, message = "{Size.faxNumber}")
    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(final String faxNumber) {
        this.faxNumber = faxNumber;
    }
}
