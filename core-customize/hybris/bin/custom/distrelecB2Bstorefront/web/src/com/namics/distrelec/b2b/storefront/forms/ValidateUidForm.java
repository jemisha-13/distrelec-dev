package com.namics.distrelec.b2b.storefront.forms;

import javax.validation.constraints.NotBlank;

public class ValidateUidForm {

    private String uid;

    @NotBlank
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
