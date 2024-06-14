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
package com.namics.distrelec.b2b.core.actions;

import com.namics.distrelec.b2b.core.constants.DistConstants;

import de.hybris.platform.b2b.process.approval.actions.AbstractProceduralB2BOrderApproveAction;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.task.RetryLaterException;

/**
 * StartFulfilmentProcessAction for the Distrelec place order process.
 * 
 * @author dsivakumaran, Namics AG
 * @since Distrelec 1.0
 * 
 */
public class StartFulfilmentProcessAction extends AbstractProceduralB2BOrderApproveAction {

    @Override
    public void executeAction(final B2BApprovalProcessModel process) throws RetryLaterException {
        // start the default fulfillment process defined in the fulfillment extension
        final OrderProcessModel businessProcessModel = (OrderProcessModel) getBusinessProcessService().createProcess(
                "distplaceorder" + process.getOrder().getCode() + +System.currentTimeMillis(), DistConstants.Processes.DIST_PLACE_ORDER);
        businessProcessModel.setOrder(process.getOrder());
        getBusinessProcessService().startProcess(businessProcessModel);
    }

    public BusinessProcessService getBusinessProcessService() {
        throw new IllegalStateException("Please inject BusinessProcessService via a look-up method");
    }
}
