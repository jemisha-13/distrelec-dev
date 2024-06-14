/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Form object for send feedback.
 */
public class NewsletterRegisterForm {

    private String titleCode;
    private String firstName;
    private String lastName;
    private String division;
    private String email;
    private String role;
    private String updateToken;
    private boolean marketingConsent;

    @NotNull(message = "{newsletter.title.invalid}")
    @Size(min = 1, max = 255, message = "{newsletter.title.invalid}")
    public String getTitleCode() {
        return titleCode;
    }

    public void setTitleCode(final String titleCode) {
        this.titleCode = titleCode;
    }

    @NotBlank(message = "{newsletter.firstName.invalid}")
    @Size(min = 1, max = 40, message = "{newsletter.firstName.invalid}")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    @NotBlank(message = "{newsletter.lastName.invalid}")
    @Size(min = 1, max = 40, message = "{newsletter.lastName.invalid}")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(final String division) {
        this.division = division;
    }

    @NotBlank(message = "{newsletter.email.invalid}")
    @Size(min = 1, max = 175, message = "{newsletter.email.invalid}")
    @Email(message = "{newsletter.email.invalid}")
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @NotEmpty(message = "{newsletter.topics.invalid}")
    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public String getUpdateToken() {
        return updateToken;
    }

    public void setUpdateToken(final String updateToken) {
        this.updateToken = updateToken;
    }

    public boolean isMarketingConsent() {
        return marketingConsent;
    }

    public void setMarketingConsent(boolean marketingConsent) {
        this.marketingConsent = marketingConsent;
    }

}
