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

import de.hybris.platform.category.model.CategoryModel;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.ymkt.sapymktsync.model.SyncCronJobModel;
import com.hybris.ymkt.sapymktsync.services.AbstractSyncService;

public class CategorySyncJob
		extends AbstractSyncJob<SyncCronJobModel, CategoryModel, AbstractSyncService<CategoryModel>> {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(CategorySyncJob.class);

	@Override
	protected Class<CategoryModel> getModelClass() {
		return CategoryModel.class;
	}

	@Override
	protected Optional<String> getStreamConfigurationItemSelector(SyncCronJobModel cronJob) {
		return ITEM_SELECTOR_CATALOG_VERSION;
	}

}
