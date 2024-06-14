/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.ymkt.sapymktsync.jobs;

import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.ymkt.sapymktsync.model.SyncCronJobModel;
import com.hybris.ymkt.sapymktsync.services.OrderSyncService;


public class OrderSyncJob extends AbstractChildSyncJob<SyncCronJobModel, OrderModel, OrderEntryModel, OrderSyncService>
{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(OrderSyncJob.class);

	@Override
	protected Class<OrderEntryModel> getChildClass()
	{
		return OrderEntryModel.class;
	}

	@Override
	protected Class<OrderModel> getModelClass()
	{
		return OrderModel.class;
	}

	@Override
	protected Function<OrderEntryModel, OrderModel> getParentFunction()
	{
		return OrderEntryModel::getOrder;
	}
}
