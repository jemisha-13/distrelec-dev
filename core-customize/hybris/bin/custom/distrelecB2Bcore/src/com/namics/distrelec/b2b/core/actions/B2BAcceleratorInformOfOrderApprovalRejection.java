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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.util.DistLogUtils;

import de.hybris.platform.b2b.process.approval.actions.InformOfOrderRejection;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.task.RetryLaterException;

/**
 * Checks for order in process.
 */
@Component("informOfOrderApprovalRejection")
public class B2BAcceleratorInformOfOrderApprovalRejection extends InformOfOrderRejection {
    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LogManager.getLogger(InformOfOrderRejection.class);

    @Override
    public void executeAction(final B2BApprovalProcessModel process) throws RetryLaterException {
        try {
            final OrderModel order = process.getOrder();
            Assert.notNull(order, String.format("Order of BusinessProcess %s should have be set for accelerator", process));
			LOG.debug("Process for accelerator: {} in step {} order: {} user: {} ", process.getCode(), getClass(),
					order.getUnit(),order.getUser().getUid());
        } catch (final IllegalArgumentException e) {
            DistLogUtils.logError(LOG, "Process for accelerator: {}, {}", e, process.getCode(), e.getMessage());
        }
    }
}
