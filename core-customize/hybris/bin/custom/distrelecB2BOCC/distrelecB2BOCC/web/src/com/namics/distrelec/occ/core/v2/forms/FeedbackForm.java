package com.namics.distrelec.occ.core.v2.forms;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class FeedbackForm {

    @NotEmpty
    private String name;

    @Email
    @NotEmpty
    private String email;

    private String phone;

    @NotEmpty
    private String feedback;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

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

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(final String feedback) {
        this.feedback = feedback;
    }
}
