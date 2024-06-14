/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Form object for updating the newsletter subscription.
 *
 * @author daehusir, Namics AG
 */
public class UpdateNewsletterForm {

    private boolean marketingConsent;
    private boolean npsConsent;
    private boolean subscribePhoneMarketing;
    private String division;
    private String role;

    public boolean isMarketingConsent() {
        return marketingConsent;
    }

    public void setMarketingConsent(final boolean marketingConsent) {
        this.marketingConsent = marketingConsent;
    }

    public boolean isNpsConsent() {
        return npsConsent;
    }

    public void setNpsConsent(boolean npsConsent) {
        this.npsConsent = npsConsent;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(final String division) {
        this.division = division;
    }

    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public boolean isSubscribePhoneMarketing() {
        return subscribePhoneMarketing;
    }

    public void setSubscribePhoneMarketing(final boolean subscribePhoneMarketing) {
        this.subscribePhoneMarketing = subscribePhoneMarketing;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
