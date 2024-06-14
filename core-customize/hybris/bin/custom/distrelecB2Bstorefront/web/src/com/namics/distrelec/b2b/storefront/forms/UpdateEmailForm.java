/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.storefront.forms;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Form object for updating email
 */
public class UpdateEmailForm {

    private String email;
    private String password;

    @NotNull(message = "{profile.pwd.invalid}")
    @Size(min = 6, max = 255, message = "{profile.pwd.invalid}")
    @NotBlank(message = "{profile.pwd.invalid}")
    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

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
