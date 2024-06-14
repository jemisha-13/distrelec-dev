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

import com.namics.distrelec.b2b.storefront.validation.annotations.EqualAttributes;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Form object for updating the password.
 */
@EqualAttributes(message = "{validation.checkPwd.equals}", value = { "pwd", "checkPwd" })
public class UpdatePwdForm {

    private String pwd;
    private String checkPwd;
    private String token;

    @NotNull(message = "{register.pwd.help.text}")
    @Size(min = 6, max = 255, message = "{register.pwd.help.text}")
    public String getPwd() {
        return pwd;
    }

    public void setPwd(final String pwd) {
        this.pwd = pwd;
    }

    @NotNull(message = "{register.pwd.help.text}")
    public String getCheckPwd() {
        return checkPwd;
    }

    public void setCheckPwd(final String checkPwd) {
        this.checkPwd = checkPwd;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }
}
