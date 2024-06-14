/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UpdateLoginForm {

    private String login;
    private String password;

    @NotBlank(message = "{profile.login.invalid}")
    public String getLogin() {
        return login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    @NotNull(message = "{profile.pwd.invalid}")
    @Size(min = 6, max = 255, message = "{profile.pwd.invalid}")
    @NotBlank(message = "{profile.pwd.invalid}")
    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

}
