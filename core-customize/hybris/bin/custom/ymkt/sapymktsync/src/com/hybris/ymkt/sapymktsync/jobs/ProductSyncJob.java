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

import de.hybris.platform.core.model.product.ProductModel;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.ymkt.sapymktsync.model.SyncCronJobModel;
import com.hybris.ymkt.sapymktsync.services.AbstractSyncService;


public class ProductSyncJob extends AbstractSyncJob<SyncCronJobModel, ProductModel, AbstractSyncService<ProductModel>>
{

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(ProductSyncJob.class);

	@Override
	protected Class<ProductModel> getModelClass()
	{
		return ProductModel.class;
	}

	@Override
	protected Optional<String> getStreamConfigurationItemSelector(SyncCronJobModel cronJob)
	{
		return ITEM_SELECTOR_CATALOG_VERSION;
	}

}
