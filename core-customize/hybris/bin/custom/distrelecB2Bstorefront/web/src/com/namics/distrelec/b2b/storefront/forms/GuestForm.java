package com.namics.distrelec.b2b.storefront.forms;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Form for inputting a guest users email.
 */
public class GuestForm {

    private String email;

    @NotBlank
    @Size(min = 1, max = 175)
    @Email
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }
}
