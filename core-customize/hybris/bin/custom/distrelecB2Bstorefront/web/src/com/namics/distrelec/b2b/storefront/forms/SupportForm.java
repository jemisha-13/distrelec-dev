/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import com.namics.distrelec.b2b.storefront.validation.annotations.AtLeastOneNotBlank;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Form object for send feedback.
 */
@AtLeastOneNotBlank(message = "{support.phoneOrEmail.required}", value = { "email", "phone" })
public class SupportForm {
    private String title;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String comment;
    private String contactBy = "email";

    @NotEmpty
    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    @NotEmpty
    public String getLastname() {
        return lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    @Email
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

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    @NotEmpty
    public String getContactBy() {
        return contactBy;
    }

    public void setContactBy(final String contactBy) {
        this.contactBy = contactBy;
    }
}
