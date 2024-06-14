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

import com.namics.hybris.toolbox.spring.SpringUtil;

import de.hybris.platform.order.events.SubmitOrderEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Event listener for submit order functionality.
 */
public class SubmitOrderEventListener extends AbstractEventListener<SubmitOrderEvent> {

    @Override
    protected void onEvent(final SubmitOrderEvent event) {
        // Do nothing. The process is handled by the b2b approval process
    }

    public BusinessProcessService getBusinessProcessService() {
        return SpringUtil.getBean("businessProcessService", BusinessProcessService.class);
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }
}
