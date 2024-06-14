package com.namics.distrelec.b2b.core.event;

import java.util.Date;

/**
 * {@code DistRmaRequestEventGuestEntry}
 * <p>
 * This holds the details of Guets User for Return Claim Online
 * </p>
 * 
 * @author <a href="sudarshan.tembhurnikar@datwyler.com">Sudarshan Tembhurnikar</a>, Distrelec
 * @since Distrelec 7.0
 */
public class DistRmaRequestEventGuestEntry {
    private String customerName;
    private String emailAddress;
    private String phoneNumber;
    
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
