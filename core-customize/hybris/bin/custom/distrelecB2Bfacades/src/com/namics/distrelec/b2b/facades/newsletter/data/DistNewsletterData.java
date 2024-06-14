/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.newsletter.data;

/**
 * Data object for a newsletter.
 *
 * @author pnueesch, Namics AG
 * @since Distrelec 1.0
 *
 */
public class DistNewsletterData {

    private String email;
    private String titleCode;
    private String firstName;
    private String lastName;
    private String division;
    private String role;
    private Boolean activeSubscribtion;
    private Boolean segmentedTopic;

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getTitleCode() {
        return titleCode;
    }

    public void setTitleCode(final String titleCode) {
        this.titleCode = titleCode;
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

    public Boolean getActiveSubscribtion() {
        return activeSubscribtion;
    }

    public void setActiveSubscribtion(final Boolean activeSubscribtion) {
        this.activeSubscribtion = activeSubscribtion;
    }

    public Boolean getSegmentedTopic() {
        return segmentedTopic;
    }

    public void setSegmentedTopic(final Boolean segmentedTopic) {
        this.segmentedTopic = segmentedTopic;
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
}
