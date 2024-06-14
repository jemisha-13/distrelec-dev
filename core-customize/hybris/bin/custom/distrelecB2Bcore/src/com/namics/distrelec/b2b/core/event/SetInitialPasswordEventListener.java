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

import com.namics.distrelec.b2b.core.model.process.SetInitialPasswordProcessModel;

import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Event listener for forgotten password functionality.
 */
public class SetInitialPasswordEventListener extends AbstractEventListener<SetInitialPwdEvent> {
    public BusinessProcessService getBusinessProcessService() {
        return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }

    @Override
    protected void onEvent(final SetInitialPwdEvent setInitialPwdEvent) {
        SetInitialPasswordProcessModel setInitialPasswordProcessModel = getBusinessProcessService().createProcess(generateProcessName(),
                                                                                                                  "setInitialPasswordEmailProcess");
        setInitialPasswordProcessModel.setSite(setInitialPwdEvent.getSite());
        setInitialPasswordProcessModel.setCustomer(setInitialPwdEvent.getCustomer());
        setInitialPasswordProcessModel.setToken(setInitialPwdEvent.getToken());
        setInitialPasswordProcessModel.setStorefrontRequest(setInitialPwdEvent.isStorefrontRequest());
        setInitialPasswordProcessModel.setSkipEmailSending(setInitialPwdEvent.isSkipEmailSending());
        getModelServiceViaLookup().save(setInitialPasswordProcessModel);
        getBusinessProcessService().startProcess(setInitialPasswordProcessModel);
    }

    private String generateProcessName() {
        return "setInitialPassword" + System.currentTimeMillis();
    }
}
