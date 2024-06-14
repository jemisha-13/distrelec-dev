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

import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Event listener for registration functionality.
 */
public class RegistrationEventListener extends AbstractEventListener<DistRegisterEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationEventListener.class);

    @Autowired
    private ConfigurationService configurationService;

    public BusinessProcessService getBusinessProcessService() {
        return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }

    @Override
    protected void onEvent(final DistRegisterEvent registerEvent) {

        boolean sendMail = configurationService.getConfiguration().getBoolean(
                "distrelec.registration.welcome.mail.enabled", true);

        LOG.debug("Welcome mail enabled: {}", sendMail);

        if (sendMail) {
            final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel = getBusinessProcessService().createProcess(
                    "customerRegistrationEmailProcess" + System.currentTimeMillis(), "customerRegistrationEmailProcess");
            storeFrontCustomerProcessModel.setSite(registerEvent.getSite());
            storeFrontCustomerProcessModel.setCustomer(registerEvent.getCustomer());
            storeFrontCustomerProcessModel.setLoginToken(registerEvent.getToken());
            storeFrontCustomerProcessModel.setRegistrationType(registerEvent.getRegistrationType());
            getModelServiceViaLookup().save(storeFrontCustomerProcessModel);
            getBusinessProcessService().startProcess(storeFrontCustomerProcessModel);
        }
    }
}
