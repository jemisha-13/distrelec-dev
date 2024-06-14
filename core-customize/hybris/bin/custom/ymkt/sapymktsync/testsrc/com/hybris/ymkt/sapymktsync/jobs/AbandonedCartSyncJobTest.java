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
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;
import com.hybris.ymkt.sapymktsync.model.SyncCronJobModel;
import com.hybris.ymkt.sapymktsync.services.AbandonedCartSyncService;
import com.hybris.ymkt.sapymktsync.services.TestTool;


@ManualTest
public class AbandonedCartSyncJobTest extends AbstractSyncJobTest<AbandonedCartSyncJob>
{
	@Resource(name = "ymktAbandonedCartSyncJob")
	AbandonedCartSyncJob abandonedCartSyncJob;
	
	CartModel cart;
	CartEntryModel item1;
	CartEntryModel item2;

	@Override
	AbandonedCartSyncJob getSyncJob()
	{
		return this.abandonedCartSyncJob;
	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		TestTool.fixLogger(AbandonedCartSyncJob.class);
		TestTool.fixLogger(AbandonedCartSyncService.class);
		createAbandonedCart();
	}

	@Override
	public void tearDown() throws Exception
	{
		super.tearDown();
		if (cart != null)
		{
			modelService.remove(cart);
		}
	}

	private void createAbandonedCart()
	{
		cart = modelService.create(CartModel.class);
		cart.setCode(uniqueId());
		cart.setUser(userService.getCurrentUser());
		cart.setDate(new Date());
		cart.setCurrency(currencyCAD);

		item1 = modelService.create(CartEntryModel.class);
		item1.setEntryNumber(0);
		item1.setQuantity(1L);
		item1.setProduct(product);
		item1.setUnit(unitPAL);
		item1.setTotalPrice(50.0);
		item1.setOrder(cart);

		item2 = modelService.create(CartEntryModel.class);
		item2.setEntryNumber(1);
		item2.setQuantity(2L);
		item2.setProduct(product);
		item2.setUnit(unitPAL);
		item2.setTotalPrice(150.0);
		item2.setOrder(cart);
		modelService.saveAll();
	}

	@Test
	public void test() throws Exception
	{
		// Create
		SyncCronJobModel cronJob = createCronJob();

		// Cart was just created, i.e. it hasn't been abandoned yet
		Assert.assertTrue(this.abandonedCartSyncJob.syncChangeDetectionService
				.collectChanges(this.abandonedCartSyncJob.getModelClass(), this.abandonedCartSyncJob.getConfiguration(cronJob))
				.isEmpty());

		Calendar date = Calendar.getInstance();
		date.add(Calendar.MINUTE, this.abandonedCartSyncJob.getInterval() - 1); // almost abandoned but not quite
		cronJob.setStartTime(date.getTime());
		modelService.save(cronJob);
		Assert.assertTrue(this.abandonedCartSyncJob.syncChangeDetectionService
				.collectChanges(this.abandonedCartSyncJob.getModelClass(), this.abandonedCartSyncJob.getConfiguration(cronJob))
				.isEmpty());

		date = Calendar.getInstance();
		date.add(Calendar.MINUTE, this.abandonedCartSyncJob.getInterval() + 10); // cart will be in abandoned state for remained of test
		cronJob.setStartTime(date.getTime());
		modelService.save(cronJob);
		Assert.assertFalse(this.abandonedCartSyncJob.syncChangeDetectionService
				.collectChanges(this.abandonedCartSyncJob.getModelClass(), this.abandonedCartSyncJob.getConfiguration(cronJob))
				.isEmpty());

		// Send the created cart
		Assert.assertEquals(CronJobResult.SUCCESS, this.abandonedCartSyncJob.perform(cronJob).getResult());

		// Update abandoned cart currency
		cart.setCurrency(currencyUSD);
		cart.setModifiedtime(new Date());
		modelService.save(cart);
		Assert.assertEquals(CronJobResult.SUCCESS, this.abandonedCartSyncJob.perform(cronJob).getResult());

		// Update item quantity
		item1.setQuantity(3L);
		modelService.save(item1);
		cart.setModifiedtime(new Date());
		modelService.save(cart);
		Assert.assertEquals(CronJobResult.SUCCESS, this.abandonedCartSyncJob.perform(cronJob).getResult());

		// Add a new item
		CartEntryModel item3 = modelService.create(CartEntryModel.class);
		item3.setEntryNumber(2);
		item3.setQuantity(4L);
		item3.setProduct(product);
		item3.setUnit(unitPAL);
		item3.setTotalPrice(160.0);
		item3.setOrder(cart);
		modelService.save(item3);
		cart.setModifiedtime(new Date());
		modelService.save(cart);
		Assert.assertEquals(CronJobResult.SUCCESS, this.abandonedCartSyncJob.perform(cronJob).getResult());

		// Delete an item
		modelService.remove(item2);
		cart.setModifiedtime(new Date());
		modelService.save(cart);
		Assert.assertEquals(CronJobResult.SUCCESS, this.abandonedCartSyncJob.perform(cronJob).getResult());

		// Delete abandoned cart
		modelService.remove(cart);
		Assert.assertEquals(CronJobResult.SUCCESS, this.abandonedCartSyncJob.perform(cronJob).getResult());
	}
}
