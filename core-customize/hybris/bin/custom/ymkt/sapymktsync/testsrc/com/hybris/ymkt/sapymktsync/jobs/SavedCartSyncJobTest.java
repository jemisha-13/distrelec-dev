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
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import java.util.Date;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;
import com.hybris.ymkt.sapymktsync.model.SyncCronJobModel;
import com.hybris.ymkt.sapymktsync.services.SavedCartSyncService;
import com.hybris.ymkt.sapymktsync.services.TestTool;


@ManualTest
public class SavedCartSyncJobTest extends AbstractSyncJobTest<SavedCartSyncJob>
{
	@Resource(name = "ymktSavedCartSyncJob")
	SavedCartSyncJob savedCartSyncJob;

	CartModel cart;
	CartEntryModel item1;
	CartEntryModel item2;

	@Override
	SavedCartSyncJob getSyncJob()
	{
		return this.savedCartSyncJob;
	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		TestTool.fixLogger(SavedCartSyncJob.class);
		TestTool.fixLogger(SavedCartSyncService.class);
		createSavedCart();
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

	SyncCronJobModel createCronJob()
	{
		ServicelayerJobModel servicelayerJobModel = modelService.create(ServicelayerJobModel.class);
		servicelayerJobModel.setSpringId("ymktSavedCartSyncJob");
		servicelayerJobModel.setCode(uniqueId());
		modelService.save(servicelayerJobModel);

		SyncCronJobModel cronJob = modelService.create(SyncCronJobModel.class);
		cronJob.setActive(Boolean.TRUE);
		cronJob.setCatalogVersion(catalogVersion);
		cronJob.setCode(uniqueId());
		cronJob.setJob(servicelayerJobModel);
		cronJob.setStartTime(new Date());
		modelService.save(cronJob);
		return cronJob;
	}

	void createSavedCart()
	{
		cart = modelService.create(CartModel.class);
		cart.setSaveTime(new Date());
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
		Assert.assertEquals(CronJobResult.SUCCESS, this.savedCartSyncJob.perform(cronJob).getResult());

		// Update saved cart currency
		cart.setCurrency(currencyUSD);
		cart.setModifiedtime(new Date());
		modelService.save(cart);
		Assert.assertEquals(CronJobResult.SUCCESS, this.savedCartSyncJob.perform(cronJob).getResult());

		// Update item quantity
		item1.setQuantity(3L);
		modelService.save(item1);
		cart.setModifiedtime(new Date());
		modelService.save(cart);
		Assert.assertEquals(CronJobResult.SUCCESS, this.savedCartSyncJob.perform(cronJob).getResult());

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
		Assert.assertEquals(CronJobResult.SUCCESS, this.savedCartSyncJob.perform(cronJob).getResult());

		// Delete an item
		modelService.remove(item2);
		cart.setModifiedtime(new Date());
		modelService.save(cart);
		Assert.assertEquals(CronJobResult.SUCCESS, this.savedCartSyncJob.perform(cronJob).getResult());

		// Delete saved cart
		modelService.remove(cart);
		Assert.assertEquals(CronJobResult.SUCCESS, this.savedCartSyncJob.perform(cronJob).getResult());
	}
}
