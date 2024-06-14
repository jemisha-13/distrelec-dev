package com.namics.distrelec.b2b.storefront.forms;

import com.namics.distrelec.b2b.storefront.validation.annotations.Phonenumber;

public class ContactForm {

    private String phoneNumber;
    private String mobileNumber;
    
    @Phonenumber(message = "{logindata.changeName.phoneNumber.invalid}")
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    @Phonenumber(message = "{logindata.changeName.phoneNumber.invalid}")
    public String getMobileNumber() {
        return mobileNumber;
    }
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
    
    
}
