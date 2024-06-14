/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;

/**
 * Event object for send feedback functionality.
 */
public class DistFeedbackEvent extends AbstractDistrelecCustomerEvent<BaseSiteModel> {

    private String name;
    private String email;
    private String phone;
    private String feedback;
    private boolean searchFeedback;
    private String fromDisplayName;

    /**
     * Default constructor.
     */
    public DistFeedbackEvent() {
        super();
    }

    /**
     * Parameterized constructor.
     */
    public DistFeedbackEvent(final String name, final String email, final String phone, final String feedback) {
        super();

        this.name = name;
        this.email = email;
        this.phone = phone;
        this.feedback = feedback;
    }

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

    public boolean isSearchFeedback() {
        return searchFeedback;
    }

    public void setSearchFeedback(boolean searchFeedback) {
        this.searchFeedback = searchFeedback;
    }

    public String getFromDisplayName() {
        return fromDisplayName;
    }

    public void setFromDisplayName(String fromDisplayName) {
        this.fromDisplayName = fromDisplayName;
    }
}
