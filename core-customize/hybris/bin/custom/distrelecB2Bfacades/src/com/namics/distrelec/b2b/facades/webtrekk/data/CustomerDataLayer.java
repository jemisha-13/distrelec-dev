/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.webtrekk.data;

/**
 * {@code CustomerDataLayer}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.0
 */
public class CustomerDataLayer {

    private String userid;
    private String firstName;
    private String lastName;
    private String nickName;
    private String cartid;

    /**
     * Create a new instance of {@code CustomerDataLayer}
     */
    public CustomerDataLayer() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(final String userId) {
        this.userid = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getCartid() {
        return cartid;
    }

    public void setCartid(final String cartId) {
        this.cartid = cartId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(final String nickName) {
        this.nickName = nickName;
    }
}
