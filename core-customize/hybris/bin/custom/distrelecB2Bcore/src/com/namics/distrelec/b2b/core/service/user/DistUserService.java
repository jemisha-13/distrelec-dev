/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.user;

import javax.servlet.http.HttpServletRequest;

import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.user.UserService;

public interface DistUserService extends UserService {

    CustomerModel getCustomerByContactId(String erpContactId);

    <T extends CustomerModel> T getCustomerByContactId(String erpContactId, Class<T> clazz);

    /**
     * Returns true if a user accesses from internal IP ranges.
     */
    boolean accessFromInternalIp(HttpServletRequest request);

    CustomerType getCurrentCustomerType();

    CustomerType getCustomerType(CustomerModel customer);

    String getInternalIps();
    
    boolean isCurrentCustomerErpSelected();
}
