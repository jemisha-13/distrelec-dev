/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ResendAccountActivationTokenForm {
    private String email;

    /**
     * @return the email
     */
    @NotNull(message = "{resendAccountActivationToken.email.invalid}")
    @Size(min = 1, max = 255, message = "{resendAccountActivationToken.email.invalid}")
    @Email(message = "{resendAccountActivationToken.email.invalid}")
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(final String email) {
        this.email = email;
    }
}
