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
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.customerreview.CustomerReviewService;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Resource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.hybris.ymkt.sapymktsync.model.SyncCronJobModel;
import com.hybris.ymkt.sapymktsync.services.ReviewSyncService;
import com.hybris.ymkt.sapymktsync.services.TestTool;


@ManualTest
public class ReviewSyncJobTest extends AbstractSyncJobTest<ReviewSyncJob>
{
	@Resource(name = "ymktReviewSyncJob")
	ReviewSyncJob reviewSyncJob;

	@Resource
	CustomerReviewService customerReviewService;

	@Override
	protected ReviewSyncJob getSyncJob()
	{
		return this.reviewSyncJob;
	}

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		TestTool.fixLogger(ReviewSyncJob.class);
		TestTool.fixLogger(ReviewSyncService.class);
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

		CustomerReviewModel review = this.createGet(CustomerReviewModel.class, //
				() -> {
					return Optional.ofNullable(customerReviewService.getReviewsForProduct(product)).map(List::stream)
							.orElseGet(Stream::empty).filter(x -> customer.equals(x.getUser())).findAny().orElse(null);
				},
				Arrays.asList(r -> r.setProduct(product), //
						r -> r.setUser(customer), //
						r -> r.setRating(4.6), //
						r -> r.setHeadline("myHeadline"), //
						r -> r.setComment("myComment"), //
						r -> r.setBlocked(Boolean.FALSE), //
						r -> r.setApprovalStatus(CustomerReviewApprovalType.APPROVED)));

		// Create review
		Assert.assertEquals(CronJobResult.SUCCESS, this.reviewSyncJob.perform(cronJob).getResult());

		// Block review; nothing should be sent
		review.setBlocked(Boolean.TRUE);
		modelService.save(review);
		Assert.assertEquals(CronJobResult.SUCCESS, this.reviewSyncJob.perform(cronJob).getResult());

		// Unblock review, but set approval status to REJECTED; nothing should be sent
		review.setBlocked(Boolean.FALSE);
		review.setApprovalStatus(CustomerReviewApprovalType.REJECTED);
		modelService.save(review);
		Assert.assertEquals(CronJobResult.SUCCESS, this.reviewSyncJob.perform(cronJob).getResult());

		// approve the review; change the headline 
		review.setApprovalStatus(CustomerReviewApprovalType.APPROVED);
		review.setHeadline("myHeadlineUpdated");
		modelService.save(review);
		Assert.assertEquals(CronJobResult.SUCCESS, this.reviewSyncJob.perform(cronJob).getResult());

		// test the review rounding 
		review.setRating(3.4); // should be rounded down to 3 
		modelService.save(review);
		Assert.assertEquals(CronJobResult.SUCCESS, this.reviewSyncJob.perform(cronJob).getResult());

		review.setRating(3.5); // should be rounded up to 4 
		modelService.save(review);
		Assert.assertEquals(CronJobResult.SUCCESS, this.reviewSyncJob.perform(cronJob).getResult());

		// test delete; should not send anything 
		modelService.remove(review);
		Assert.assertEquals(CronJobResult.SUCCESS, this.reviewSyncJob.perform(cronJob).getResult());

		// test anonymous
	}

}
