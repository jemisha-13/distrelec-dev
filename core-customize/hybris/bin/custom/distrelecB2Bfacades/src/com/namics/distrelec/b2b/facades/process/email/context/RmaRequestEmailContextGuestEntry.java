package com.namics.distrelec.b2b.facades.process.email.context;

/**
 * @author dattembhurs
 *
 */
public class RmaRequestEmailContextGuestEntry {
    private String customerName;
    private String emailAddress;
    private String phoneNumber;
    private String returnDate;

    /**
     * Create a new instance of {@code RmaRequestEmailContextGuestEntry}
     */
    public RmaRequestEmailContextGuestEntry() {
        super();
    }

    /**
     * Create a new instance of {@code RmaRequestEmailContextGuestEntry}
     *
     * @param customerName
     * @param emailAddress
     * @param phoneNumber
     * @param returnDate
     */
    public RmaRequestEmailContextGuestEntry(final String customerName, final String emailAddress, final String phoneNumber, final String returnDate) {
        this.customerName = customerName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.returnDate = returnDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(final String customerName) {
        this.customerName = customerName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
