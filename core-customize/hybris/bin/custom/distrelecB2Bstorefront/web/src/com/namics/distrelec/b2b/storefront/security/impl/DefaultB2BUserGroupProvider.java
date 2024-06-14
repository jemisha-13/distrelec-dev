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
package com.namics.distrelec.b2b.storefront.security.impl;

import com.namics.distrelec.b2b.storefront.security.B2BUserGroupProvider;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Set;

/**
 * Default provider for B2b User group.
 */
public class DefaultB2BUserGroupProvider implements B2BUserGroupProvider {

    private UserService userService;
    private Set<String> authorizedGroups;
    private Set<String> authorizedGroupsToCheckOut;
    private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2BCustomerService;

    @Override
    public Set<String> getAllowedUserGroup() {
        return authorizedGroups;
    }

    @Override
    public boolean isCurrentUserAuthorized() {
        return checkIfUserAuthorized(getUserService().getCurrentUser());
    }

    @Override
    public boolean isUserAuthorized(final UserModel user) {
        return checkIfUserAuthorized(user);
    }

    @Override
    public boolean isUserAuthorized(final String loginName) {
        return checkIfUserAuthorized(getUserService().getUserForUID(loginName));
    }

    @Override
    public boolean isUserEnabled(final String userId) {
        return getB2BCustomerService().getUserForUID(userId).getActive().booleanValue();
    }

    @Override
    public boolean isCurrentUserAuthorizedToCheckOut() {
        return checkIfUserAuthorizedToCheckOut(getUserService().getCurrentUser());
    }

    @Override
    public boolean isUserAuthorizedToCheckOut(final UserModel user) {
        return checkIfUserAuthorizedToCheckOut(user);
    }

    @Override
    public boolean isUserAuthorizedToCheckOut(final String loginName) {
        final UserModel user = getUserService().getUserForUID(loginName);
        return checkIfUserAuthorizedToCheckOut(user);
    }

    protected boolean checkIfUserAuthorized(final UserModel user) {
        if (user.getAllGroups() == null) {
            return false;
        }

        return user.getAllGroups().stream().map(group -> group.getUid()).anyMatch(uid -> getAuthorizedGroups().contains(uid));
    }

    protected boolean checkIfUserAuthorizedToCheckOut(final UserModel user) {
        if (user.getAllGroups() == null) {
            return false;
        }

        return user.getAllGroups().stream().map(group -> group.getUid()).anyMatch(uid -> getAuthorizedGroupsToCheckOut().contains(uid));
    }

    public Set<String> getAuthorizedGroupsToCheckOut() {
        return authorizedGroupsToCheckOut;
    }

    public void setAuthorizedGroupsToCheckOut(final Set<String> authorizedGroupsToCheckOut) {
        this.authorizedGroupsToCheckOut = authorizedGroupsToCheckOut;
    }

    protected UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    protected Set<String> getAuthorizedGroups() {
        return authorizedGroups;
    }

    public void setAuthorizedGroups(final Set<String> authorizedGroups) {
        this.authorizedGroups = authorizedGroups;
    }

    public B2BCustomerService<B2BCustomerModel, B2BUnitModel> getB2BCustomerService() {
        return b2BCustomerService;
    }

    public void setB2BCustomerService(final B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService) {
        b2BCustomerService = b2bCustomerService;
    }
}
