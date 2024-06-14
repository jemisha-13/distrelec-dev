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

import com.namics.distrelec.b2b.core.constants.DistConstants;

import de.hybris.platform.b2b.event.OrderApprovedEvent;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;

/**
 * Listener for order approval.
 * 
 * @author daehusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class OrderApprovedEventListener extends AbstractOrderEventListener<OrderApprovedEvent> {

    @Override
    protected void onEvent(final OrderApprovedEvent event) {
        final OrderModel order = event.getOrder();
        final UserModel user = (UserModel) event.getApprover();
        setSessionLocaleForOrder(order);

        String comment = "";
        for (final B2BPermissionResultModel permissionResult : order.getPermissionResults()) {
            if (permissionResult.getApprover().equals(user)) {
                final String currentNote = permissionResult.getNote(getI18NServiceViaLookup().getCurrentLocale());
                if (currentNote != null) {
                    comment = currentNote;
                    break;
                }
            }
        }
        createOrderHistoryEntry(user, order, OrderStatus.APPROVED, comment);

        // fire the default replenishment process
        final OrderProcessModel businessProcessModel = (OrderProcessModel) getBusinessProcessService().createProcess(
                "distplaceorder" + event.getOrder().getCode() + +System.currentTimeMillis(), DistConstants.Processes.DIST_PLACE_ORDER);
        businessProcessModel.setOrder(event.getOrder());
        getBusinessProcessService().startProcess(businessProcessModel);
    }

    public BusinessProcessService getBusinessProcessService() {
        return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
    }
}
