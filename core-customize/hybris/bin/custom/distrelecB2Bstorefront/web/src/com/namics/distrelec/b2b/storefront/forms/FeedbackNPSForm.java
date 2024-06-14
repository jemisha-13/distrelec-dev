/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.storefront.validation.annotations.DataFormInput;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.util.Date;

/**
 * {@code FeedbackNPSForm}
 * <p>
 * Data class to hold informations from a {@link com.namics.distrelec.b2b.core.model.feedback.DistNetPromoterScoreModel}
 * </p>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class FeedbackNPSForm {

    // The NPS reason
    private String reason;
    // The NPS sub-reason
    private String subreason;
    // The NPS type
    private String type;
    // The NPS value
    private Integer value = WebConstants.DEFAULT_STARTING_NPS_SCORE;
    // The NPS feedback text
    private String feedback;
    // Order Number
    private String order;
    // The Customer Email Address
    private String email;
    // First name
    private String fname;
    // Last name
    private String namn;
    // Company Name
    private String company;
    // ERP Customer ID
    private String cnumber;
    // ERP Contact ID
    private String contactnum;
    // Delivery Date
    private Date delivery;
    // NPS Code
    private String id;

    /**
     * Create a new instance of {@code FeedbackNPSForm}
     */
    public FeedbackNPSForm() {
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(final Integer value) {
        this.value = value;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(final String feedback) {
        this.feedback = feedback;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(final String orderNumber) {
        this.order = orderNumber;
    }

    @NotBlank
    @Size(max = 175, message = "{register.email.invalid}")
    @Email(message = "{register.email.invalid}")
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(final String companyName) {
        this.company = companyName;
    }

    @DataFormInput(message = "{register.customerId.invalid}", patternKey = "register.customerId.validationPattern")
    public String getCnumber() {
        return cnumber;
    }

    public void setCnumber(final String erpCustomerID) {
        this.cnumber = erpCustomerID;
    }

    public String getContactnum() {
        return contactnum;
    }

    public void setContactnum(final String erpContactID) {
        this.contactnum = erpContactID;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(final String firstname) {
        this.fname = firstname;
    }

    public String getNamn() {
        return namn;
    }

    public void setNamn(final String lastname) {
        this.namn = lastname;
    }

    public Date getDelivery() {
        return delivery;
    }

    public void setDelivery(final Date deliveryDate) {
        this.delivery = deliveryDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(final String reason) {
        this.reason = reason;
    }

    public String getSubreason() {
        return subreason;
    }

    public void setSubreason(final String subreason) {
        this.subreason = subreason;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }
}
