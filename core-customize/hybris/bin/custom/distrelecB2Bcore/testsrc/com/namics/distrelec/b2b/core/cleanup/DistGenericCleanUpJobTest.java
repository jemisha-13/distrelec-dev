/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.cleanup;

import javax.annotation.Resource;

import org.apache.commons.jexl.junit.Asserter;
import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.model.DistPunchOutFilterModel;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

public class DistGenericCleanUpJobTest extends ServicelayerTest {

    @Resource
    private CronJobService cronJobService;

    @Resource
    private FlexibleSearchService flexibleSearchService;

    @Before
    public void setUp() throws Exception {
        super.createCoreData();
        importStream(DistGenericCleanUpJobTest.class.getResourceAsStream("/distrelecB2Bcore/test/testErpCodelist.impex"), "UTF-8", null);
        importStream(DistGenericCleanUpJobTest.class.getResourceAsStream("/distrelecB2Bcore/test/cleanUpJob/testDistCleanUpJob.impex"), "UTF-8", null);
    }

    @Test
    public void testRemovePunchOutFilters() throws InterruptedException {

        final CronJobModel cronJob = cronJobService.getCronJob("distCUPunchoutOutFilterCleanUpTestCronJob");
        final String query = getQuery(DistPunchOutFilterModel._TYPECODE);

        SearchResult<DistPunchOutFilterModel> result = flexibleSearchService.search(query);
        Asserter.assertEquals(4, result.getCount());

        cronJobService.performCronJob(cronJob, true);

        result = flexibleSearchService.search(query);
        Asserter.assertEquals(1, result.getCount());
    }

    @Test
    public void testRemoveB2BUnit() throws InterruptedException {
        final CronJobModel cronJob = cronJobService.getCronJob("distB2BUnitCleanUpTestCronJob");
        final String query = getQuery(B2BUnitModel._TYPECODE);

        SearchResult<DistPunchOutFilterModel> result = flexibleSearchService.search(query);
        Asserter.assertEquals(2, result.getCount());

        cronJobService.performCronJob(cronJob, true);

        result = flexibleSearchService.search(query);
        Asserter.assertEquals(1, result.getCount());
    }

    protected String getQuery(final String type) {
        return "SELECT {pk} FROM {" + type + "}";
    }

}
