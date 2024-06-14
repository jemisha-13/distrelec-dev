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

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import java.util.Date;
import javax.annotation.Resource;
import org.junit.Test;
import com.hybris.ymkt.sapymktsync.model.SyncCronJobModel;
import com.hybris.ymkt.sapymktsync.services.OrderSyncService;
import com.hybris.ymkt.sapymktsync.services.TestTool;


@ManualTest
public class OrderSyncJobTest extends AbstractSyncJobTest<OrderSyncJob>
{
	@Resource(name = "ymktOrderSyncJob")
	OrderSyncJob orderSyncJob;

	OrderEntryModel item1;
	OrderEntryModel item2;
	OrderModel order;

	@Override
	OrderSyncJob getSyncJob()
	{
		return this.orderSyncJob;
	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		TestTool.fixLogger(OrderSyncJob.class);
		TestTool.fixLogger(OrderSyncService.class);
		createOrder();
	}

	@Override
	public void tearDown() throws Exception
	{
		super.tearDown();
		if (order != null)
		{
			modelService.remove(order);
		}
	}

	void createOrder()
	{
		order = modelService.create(OrderModel.class);
		order.setCode(uniqueId());
		order.setUser(userService.getCurrentUser());
		order.setDate(new Date());
		order.setCurrency(currencyCAD);

		item1 = modelService.create(OrderEntryModel.class);
		item1.setEntryNumber(0);
		item1.setQuantity(1L);
		item1.setProduct(product);
		item1.setUnit(unitPAL);
		item1.setTotalPrice(50.0);
		item1.setOrder(order);

		item2 = modelService.create(OrderEntryModel.class);
		item2.setEntryNumber(1);
		item2.setQuantity(2L);
		item2.setProduct(product);
		item2.setUnit(unitPAL);
		item2.setTotalPrice(150.0);
		item2.setOrder(order);
		modelService.saveAll();
	}

	@Test
	public void test() throws Exception
	{
		// Create
		SyncCronJobModel cronJob = createCronJob();
		this.perform(cronJob);

		// Cancel order
		order.setStatus(OrderStatus.CANCELLED);
		order.setModifiedtime(new Date());
		modelService.save(order);
		this.perform(cronJob);

		// Update order currency
		order.setCurrency(currencyUSD);
		order.setModifiedtime(new Date());
		modelService.save(order);
		this.perform(cronJob);

		// Update item quantity
		item1.setQuantity(3L);
		modelService.save(item1);
		order.setModifiedtime(new Date());
		modelService.save(order);
		this.perform(cronJob);

		// Add a new item
		OrderEntryModel item3 = modelService.create(OrderEntryModel.class);
		item3.setEntryNumber(2);
		item3.setQuantity(4L);
		item3.setProduct(product);
		item3.setUnit(unitPAL);
		item3.setTotalPrice(160.0);
		item3.setOrder(order);
		modelService.save(item3);
		order.setModifiedtime(new Date());
		modelService.save(order);
		this.perform(cronJob);

		// Delete an item
		modelService.remove(item2);
		order.setModifiedtime(new Date());
		modelService.save(order);
		this.perform(cronJob);

		// Delete order
		modelService.remove(order);
		this.perform(cronJob);
	}
}
