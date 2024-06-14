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
package com.namics.distrelec.b2b.core.service.customer.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.user.CustomerModel;

/**
 */
public class DefaultDistCustomerEmailResolutionService implements CustomerEmailResolutionService {
    private static final Logger LOG = Logger.getLogger(DefaultDistCustomerEmailResolutionService.class);

    private CustomerEmailResolutionService defaultCustomerEmailResolutionService;

    protected CustomerEmailResolutionService getDefaultCustomerEmailResolutionService() {
        return defaultCustomerEmailResolutionService;
    }

    @Required
    public void setDefaultCustomerEmailResolutionService(final CustomerEmailResolutionService defaultCustomerEmailResolutionService) {
        this.defaultCustomerEmailResolutionService = defaultCustomerEmailResolutionService;
    }

    @Override
    public String getEmailForCustomer(final CustomerModel customerModel) {
        validateParameterNotNullStandardMessage("customerModel", customerModel);

        if (customerModel instanceof B2BCustomerModel) {
            return ((B2BCustomerModel) customerModel).getEmail();
        }

        return getDefaultCustomerEmailResolutionService().getEmailForCustomer(customerModel);
    }
}
