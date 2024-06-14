/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Form object for send to friend.
 */
public class SendToFriendForm {

    private String name;
    private String email;
    private String receivername;
    private String email2;
    private String message;

    @NotNull
    @Size(max = 128, message = "{sendToFriend.name.maxLength}")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @NotNull
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @NotNull
    @Size(max = 128, message = "{sendToFriend.name.maxLength}")
    public String getReceivername() {
        return receivername;
    }

    public void setReceivername(final String receivername) {
        this.receivername = receivername;
    }

    @NotNull
    public String getEmail2() {
        return email2;
    }

    public void setEmail2(final String email2) {
        this.email2 = email2;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
