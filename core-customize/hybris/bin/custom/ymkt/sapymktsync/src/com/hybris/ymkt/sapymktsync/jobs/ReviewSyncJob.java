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

import de.hybris.platform.customerreview.model.CustomerReviewModel;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.ymkt.sapymktsync.model.SyncCronJobModel;
import com.hybris.ymkt.sapymktsync.services.ReviewSyncService;


public class ReviewSyncJob extends AbstractSyncJob<SyncCronJobModel, CustomerReviewModel, ReviewSyncService>
{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(ReviewSyncJob.class);

	private static final Optional<String> REVIEW_ITEM_SELECTOR = Optional.of( //
			"{item.product} IN ({{ SELECT {p.pk} FROM {Product as p} WHERE {p.catalogVersion}=?cronJob.catalogVersion }}) AND " //
					+ "{item.blocked} = FALSE AND " //
					+ "{item.approvalstatus} IN ({{ SELECT {c.pk} FROM {CustomerReviewApprovalType as c} WHERE {c.code}='approved' }})");

	@Override
	protected Class<CustomerReviewModel> getModelClass()
	{
		return CustomerReviewModel.class;
	}

	@Override
	protected Optional<String> getStreamConfigurationItemSelector(SyncCronJobModel cronJob)
	{
		return REVIEW_ITEM_SELECTOR;
	}

}
