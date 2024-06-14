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

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

/**
 * Form for forgotten password.
 */
public class ForgottenPwdForm {

    private String email;

    @Size(min = 1, max = 175, message = "{forgottenPwd.email.invalid}")
    @Email(message = "{forgottenPwd.email.invalid}")
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }
}
