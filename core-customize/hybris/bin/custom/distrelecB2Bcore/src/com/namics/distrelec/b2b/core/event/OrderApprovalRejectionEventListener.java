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

import de.hybris.platform.b2bacceleratorservices.event.OrderApprovalRejectionEvent;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

public class OrderApprovalRejectionEventListener extends AbstractEventListener<OrderApprovalRejectionEvent> {
    public BusinessProcessService getBusinessProcessService() {
        return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }

    @Override
    protected void onEvent(final OrderApprovalRejectionEvent orderApprovalRejectionEvent) {
        final OrderProcessModel orderProcessModel = (OrderProcessModel) getBusinessProcessService().createProcess(
                "orderApprovalRejectionEmailProcess" + System.currentTimeMillis(), "orderApprovalRejectionEmailProcess");
        final OrderModel orderModel = orderApprovalRejectionEvent.getProcess().getOrder();
        orderProcessModel.setOrder(orderModel);
        getModelServiceViaLookup().save(orderProcessModel);
        getBusinessProcessService().startProcess(orderProcessModel);
    }
}
