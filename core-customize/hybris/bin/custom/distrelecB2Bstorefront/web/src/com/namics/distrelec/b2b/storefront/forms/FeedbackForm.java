/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Form object for send feedback.
 */
public class FeedbackForm {

    private String name;
    private String email;
    private String phone;
    private String feedback;

    @NotEmpty
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Email
    @NotEmpty
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    @NotEmpty
    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(final String feedback) {
        this.feedback = feedback;
    }
}
