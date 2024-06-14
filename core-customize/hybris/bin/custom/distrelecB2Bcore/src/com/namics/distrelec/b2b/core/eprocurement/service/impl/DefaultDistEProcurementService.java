/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementService;

import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Default implementation for <code>DistEProcurementService</code>.
 * 
 * @author pbueschi, Namics AG
 */
public class DefaultDistEProcurementService extends AbstractBusinessService implements DistEProcurementService {

    @Autowired
    private UserService userService;

    @Override
    public boolean isEProcurementCustomer() {
        final UserModel currentUser = userService.getCurrentUser();
        final UserGroupModel userGroup = userService.getUserGroupForUID(DistConstants.User.EPROCUREMENTGROUP_UID);
        if (userGroup == null) {
            return false;
        }
        return userService.isMemberOfGroup(currentUser, userGroup);
    }
}
