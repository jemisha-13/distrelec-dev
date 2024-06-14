/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.core.model.user.UserModel;

/**
 * Deactivate User Event, implementation of {@link AbstractCommerceUserEvent}
 */
public class DeactivateUserEvent extends AbstractCommerceUserEvent<BaseSiteModel> {
    private UserModel user;
    private UserModel groupAdmin;

    /**
     * Default constructor
     */
    public DeactivateUserEvent() {
        super();
    }

    /**
     * Parameterized Constructor
     * 
     * @param user
     */
    public DeactivateUserEvent(final UserModel user, final UserModel groupAdmin) {
        super();
        this.setUser(user);
        this.setGroupAdmin(groupAdmin);
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(final UserModel user) {
        this.user = user;
    }

    public UserModel getGroupAdmin() {
        return groupAdmin;
    }

    public void setGroupAdmin(final UserModel groupAdmin) {
        this.groupAdmin = groupAdmin;
    }

}
