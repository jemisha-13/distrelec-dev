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

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.ymkt.sapymktsync.model.SyncCronJobModel;
import com.hybris.ymkt.sapymktsync.services.CustomerSyncService;


public class CustomerSyncJob
		extends AbstractChildSyncJob<SyncCronJobModel, CustomerModel, AddressModel, CustomerSyncService>
{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(CustomerSyncJob.class);

	@Override
	protected Class<AddressModel> getChildClass()
	{
		return AddressModel.class;
	}

	@Override
	protected Optional<String> getChildStreamConfigurationItemSelector(SyncCronJobModel cronJob)
	{
		return Optional.of("{item.original} IS NULL AND {item.owner} IN ({{ SELECT {pk} FROM {Customer} }})");
	}

	@Override
	protected Class<CustomerModel> getModelClass()
	{
		return CustomerModel.class;
	}

	@Override
	protected Function<AddressModel, CustomerModel> getParentFunction()
	{
		final Function<AddressModel, ItemModel> f1 = AddressModel::getOwner;
		final Function<ItemModel, CustomerModel> f2 = CustomerModel.class::cast;
		return f1.andThen(f2);
	}

}
