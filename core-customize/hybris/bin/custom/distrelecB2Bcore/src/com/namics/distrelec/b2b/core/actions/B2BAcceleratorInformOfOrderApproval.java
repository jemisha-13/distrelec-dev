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

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.util.DistLogUtils;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.process.approval.actions.InformOfOrderApproval;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.task.RetryLaterException;

/**
 * Sends an order approval email.
 */
@Component("informOfOrderApproval")
public class B2BAcceleratorInformOfOrderApproval extends InformOfOrderApproval {
	/**
	 * The Constant LOG.
	 */
	private static final Logger LOG = LogManager.getLogger(InformOfOrderApproval.class);

	@Override
	public void executeAction(final B2BApprovalProcessModel process) throws RetryLaterException {
		OrderModel order = null;
		try {
			order = process.getOrder();
			Assert.notNull(order,
					String.format("Order of BusinessProcess %s should have be set for accelerator", process));
			final B2BCustomerModel user = (B2BCustomerModel) order.getUser();

			LOG.debug("Process for accelerator: {} in step {} order: {} user: {} ", process.getCode(), getClass(),
					order.getUnit(), user.getUid());

		} catch (final Exception e) {
            DistLogUtils.logError(LOG, "{} {} B2B Process Error for accelerator: {} for order: {}, {}", e, ErrorLogCode.ORDER_RELATED_ERROR, ErrorSource.HYBRIS,
                    process.getCode(), order.getUnit(), e.getMessage());
			this.handleError(order, e);
		}
	}

	@Override
	protected void handleError(final OrderModel order, final Exception e) {
		if (order != null) {
			this.setOrderStatus(order, OrderStatus.B2B_PROCESSING_ERROR);
		}
	}
}
