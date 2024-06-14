/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * Form POJO for checkout review.
 *
 * @author ceberle, Namics AG
 * @since Distrelec 1.1
 */
public class CheckoutReviewForm {
    // default for both are false
    private boolean agree;
    // No better name because Distrelec is not quite sure yet what this field is supposed to be.
    private boolean agree1;
    private boolean marketingConsent;
    private boolean completeDelivery;

    // this field is used when and existing open order needs to be closed now.
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    @Nullable
    private Date openOrderCloseDate;

    public boolean isAgree() {
        return agree;
    }

    public void setAgree(final boolean agree) {
        this.agree = agree;
    }

    public boolean isMarketingConsent() {
        return marketingConsent;
    }

    public void setMarketingConsent(final boolean marketingConsent) {
        this.marketingConsent = marketingConsent;
    }

    public boolean isAgree1() {
        return agree1;
    }

    public void setAgree1(final boolean agree1) {
        this.agree1 = agree1;
    }

    public Date getOpenOrderCloseDate() {
        return openOrderCloseDate;
    }

    public void setOpenOrderCloseDate(final Date openOrderCloseDate) {
        this.openOrderCloseDate = openOrderCloseDate;
    }

    public boolean isCompleteDelivery() {
        return completeDelivery;
    }

    public void setCompleteDelivery(final boolean completeDelivery) {
        this.completeDelivery = completeDelivery;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
