/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Form object for updating a migrated profile.
 */
public class UpdateMigratedProfileForm extends UpdateProfileForm {

    private String email;

    @NotNull(message = "{profile.email.invalid}")
    @Size(min = 1, max = 175, message = "{profile.email.invalid}")
    @Email(message = "{profile.email.invalid}")
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

}
