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
import de.hybris.platform.core.model.user.AddressModel;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.ymkt.sapymktsync.model.SyncCronJobModel;
import com.hybris.ymkt.sapymktsync.services.CustomerSyncService;
import com.hybris.ymkt.sapymktsync.services.TestTool;


@ManualTest
public class CustomerSyncJobTest extends AbstractSyncJobTest<CustomerSyncJob>
{
	private static final Logger LOG = LoggerFactory.getLogger(CustomerSyncJobTest.class);

	@Resource(name = "ymktCustomerSyncJob")
	CustomerSyncJob customerSyncJob;

	@Override
	protected CustomerSyncJob getSyncJob()
	{
		return this.customerSyncJob;
	}

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		TestTool.fixLogger(CustomerSyncJob.class);
		TestTool.fixLogger(CustomerSyncService.class);
	}

	@After
	public void tearDown() throws Exception
	{
		super.tearDown();
	}

	@Test
	public void test() throws Exception
	{
		SyncCronJobModel cronJob = this.createCronJob();

		AddressModel address = this.createGet(AddressModel.class, //
				a -> a.setOwner(customer), //
				a -> a.setTown("Jungle"));

		LOG.info("Create customer");
		this.perform(cronJob);

		LOG.info("Update customer");
		customer.setName("Apollo Credulous");
		modelService.save(customer);
		this.perform(cronJob);

		LOG.info("Update address");
		address.setTown("Jungle Fever");
		modelService.save(address);
		this.perform(cronJob);

		LOG.info("Add default payment address (takes priority over all other addresses, so city should be Default Jungle)");
		AddressModel defaultPaymentAddress = this.createGet(AddressModel.class, //
				a -> a.setOwner(customer), //
				a -> a.setTown("Default Jungle"), //
				a -> customer.setDefaultPaymentAddress(a));
		this.perform(cronJob);

		LOG.info("Delete default payment address (city should fall back to Jungle Fever)");
		modelService.remove(defaultPaymentAddress);
		modelService.save(customer);
		this.perform(cronJob);

		LOG.info("Delete last remaining address (city should be empty)");
		modelService.remove(address);
		modelService.save(customer);
		this.perform(cronJob);

		LOG.info("Delete customer");
		modelService.remove(customer);
		this.perform(cronJob);
	}
}
