/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import com.namics.distrelec.b2b.storefront.validation.annotations.AtLeastOneNotBlank;
import com.namics.distrelec.b2b.storefront.validation.annotations.Phonenumber;

import javax.validation.constraints.Size;

/**
 * POJO for shipping address.
 * 
 * @author rmeier, Namics AG
 * @since Distrelec 1.0
 */
@AtLeastOneNotBlank(message = "{register.onePhone.required}", value = { "phoneNumber", "mobileNumber" })
public class B2BShippingAddressForm extends AbstractB2BAddressForm {

    // removed title/firstName/lastName and contactPhone for DISTRELEC-10501
    // Just used for Movex
    private String phoneNumber;
    private String mobileNumber;
    private String faxNumber;
    private String formId;
 
    @Phonenumber(message = "{address.phoneNumber.invalid}")
    @Size(max = 30, message = "{Size.phoneNumber}")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Phonenumber(message = "{address.mobileNumber.invalid}")
    @Size(max = 30, message = "{Size.mobileNumber}")
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(final String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Phonenumber(message = "{address.faxNumber.invalid}")
    @Size(max = 30, message = "{Size.faxNumber}")
    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(final String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }
    
    
}
