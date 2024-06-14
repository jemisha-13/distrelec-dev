/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.mail.internal.data;

/**
 * {@code PaypalOrder}
 * 
 *
 * @since Distrelec 5.10
 */
public class PaypalOrder {

    private String code;
    private String userId;
    private String shopId;
    private String paymentType;
    private String modifiedTime;

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(final String shopId) {
        this.shopId = shopId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(final String paymentType) {
        this.paymentType = paymentType;
    }

    public String getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(final String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
