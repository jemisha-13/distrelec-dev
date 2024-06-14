/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;

/**
 * Event object for send support functionality.
 */
public class DistSupportEvent extends AbstractDistrelecCustomerEvent<BaseSiteModel> {

    private String title;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String comment;
    private String contactBy;
    private String fromDisplayName;

    /**
     * Default constructor.
     */
    public DistSupportEvent() {
        super();
    }

    /**
     * Parameterized constructor.
     */
    public DistSupportEvent(final String title, final String firstname, final String lastname, final String email, final String phone, final String comment,
            final String contactBy) {
        super();

        this.title = title;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.comment = comment;
        this.contactBy = contactBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
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

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public String getContactBy() {
        return contactBy;
    }

    public void setContactBy(final String contactBy) {
        this.contactBy = contactBy;
    }

    public String getFromDisplayName() {
        return fromDisplayName;
    }

    public void setFromDisplayName(String fromDisplayName) {
        this.fromDisplayName = fromDisplayName;
    }
}
