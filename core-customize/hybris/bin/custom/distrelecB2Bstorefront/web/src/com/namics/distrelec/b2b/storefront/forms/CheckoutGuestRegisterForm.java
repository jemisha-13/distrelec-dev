package com.namics.distrelec.b2b.storefront.forms;

import com.namics.distrelec.b2b.storefront.validation.annotations.EqualAttributes;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@EqualAttributes(message = "{validation.checkPwd.equals}", value = {"password", "repeatPassword"})
public class CheckoutGuestRegisterForm {

    String password;
    String repeatPassword;
    String orderCode;
    String email;

    @NotBlank(message = "{register.pwd.help.text}")
    @Size(min = 6, max = 255, message = "{register.pwd.help.text}")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @NotBlank(message = "{register.pwd.help.text}")
    @Size(min = 6, max = 255, message = "{register.pwd.help.text}")
    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    @NotBlank
    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    @NotBlank
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
