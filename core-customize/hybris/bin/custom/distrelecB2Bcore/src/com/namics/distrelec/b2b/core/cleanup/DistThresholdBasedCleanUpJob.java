/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.cleanup;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.model.jobs.DistThresholdBasedCleanUpCronJobModel;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * DistThresholdBasedCleanUpJob.
 * 
 * @author daehusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class DistThresholdBasedCleanUpJob extends AbstractJobPerformable<DistThresholdBasedCleanUpCronJobModel> {

    private static final Logger LOG = Logger.getLogger(DistThresholdBasedCleanUpJob.class);

    @Override
    public PerformResult perform(final DistThresholdBasedCleanUpCronJobModel cronJob) {
        final long startTime = System.nanoTime();
        LOG.info("Starting cleanup Job.");
        try {
            if (performJob(cronJob)) {
                final long endTime = System.nanoTime();
                LOG.info("Finished cleanup job in " + (int) ((endTime - startTime) / 1e9) + " seconds.");
                return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
            } else {
                final long endTime = System.nanoTime();
                LOG.info("Finished cleanup job with errors in " + (int) ((endTime - startTime) / 1e9) + " seconds.");
                return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
            }
        } catch (final InterruptedException e) {
            LOG.error("Could not sleep after removing item.", e);
            final long endTime = System.nanoTime();
            LOG.info("Aborted cleanup job after " + (int) ((endTime - startTime) / 1e9) + " seconds.");
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
        }
    }

    protected boolean performJob(final DistThresholdBasedCleanUpCronJobModel cronJob) throws InterruptedException {
        final Map<String, Object> params = new HashMap<String, Object>();
        final Date today = new Date();
        params.put("now", today);
        if (cronJob.getMaxDaysToKeep() != null) {
            final Date date = DateUtils.addDays(today, 0 - cronJob.getMaxDaysToKeep().intValue());
            params.put("nowMinusMaxDaysToKeep", date);
        }

        final FlexibleSearchQuery fsQuery = new FlexibleSearchQuery(cronJob.getFlexibleSearchStatement());
        fsQuery.addQueryParameters(params);
        LOG.debug("Executing following query: " + fsQuery.getQuery());

        final SearchResult<ItemModel> searchResult = flexibleSearchService.search(fsQuery);
        final List<ItemModel> items = searchResult.getResult();
        if (CollectionUtils.isNotEmpty(items)) {
            if (items.size() < cronJob.getItemCountThreshold().intValue() || cronJob.getIgnoreItemCountThreshold().booleanValue()) {
                LOG.warn("About to delete " + items.size() + " item(s).");
                for (final ItemModel item : items) {
                    modelService.remove(item);
                    Thread.sleep(cronJob.getSleepBetweenDelete().longValue());
                }
            } else {
                LOG.warn("The query has found more items (" + items.size() + ") than the item threshold (" + cronJob.getItemCountThreshold().intValue()
                        + ") allows to delete. No items deleted!");
                return false;
            }
        }

        return true;

    }
}
