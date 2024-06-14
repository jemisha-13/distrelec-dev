/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.cleanup;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.namics.distrelec.b2b.core.model.availability.DistErpAvailabilityInfoModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.model.jobs.DistGenericCleanUpCronJobModel;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;


public class DistGenericCleanUpJob extends AbstractJobPerformable<DistGenericCleanUpCronJobModel> {
    private static final Logger LOG = Logger.getLogger(DistGenericCleanUpJob.class);

    @Override
    public PerformResult perform(final DistGenericCleanUpCronJobModel cronJob) {
        final long startTime = System.nanoTime();
        LOG.info("Starting cleanup Job.");
        try {
            performJob(cronJob);
        } catch (final InterruptedException e) {
            LOG.error("Could not sleep after removing item.", e);
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
        }
        final long endTime = System.nanoTime();
        LOG.info("Finished cleanup Job in " + (int) ((endTime - startTime) / 1e9) + " seconds.");
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    protected void performJob(final DistGenericCleanUpCronJobModel cronJob) throws InterruptedException {
        final Map<String, Object> params = new HashMap<String, Object>();
        final Date today = new Date();
        params.put("now", today);
        if (cronJob.getMaxDaysToKeep() != null) {
            final Date date = DateUtils.addDays(today, 0 - cronJob.getMaxDaysToKeep().intValue());
            params.put("nowMinusMaxDaysToKeep", date);
        }

        final FlexibleSearchQuery fsQuery = new FlexibleSearchQuery(cronJob.getFlexibleSearchStatement());
        if (cronJob.getMaxItemsToDelete() != null) {
            fsQuery.setCount(cronJob.getMaxItemsToDelete().intValue());
        }
        fsQuery.addQueryParameters(params);
        LOG.debug("Executing following query: " + fsQuery.getQuery());

        final SearchResult<ItemModel> searchResult = flexibleSearchService.search(fsQuery);
        final List<ItemModel> items = searchResult.getResult();
        if (isNotEmpty(items)) {
            LOG.info("About to delete " + items.size() + " item(s).");
            for (final ItemModel item : items) {
                if (item instanceof OrderModel) {
                    OrderModel order = (OrderModel) item;
                    processOrderEntries(order.getEntries());
                }
                modelService.remove(item);
                Thread.sleep(cronJob.getSleepBetweenDelete().longValue());
            }
        }
    }

    private void processOrderEntries(List<AbstractOrderEntryModel> orderEntries) {
        if (isNotEmpty(orderEntries)) {
            for (AbstractOrderEntryModel orderEntry : orderEntries) {
                removeErpAvailabilityInfo(orderEntry.getErpAvailabilityInfos());
            }
        }
    }

    private void removeErpAvailabilityInfo(List<DistErpAvailabilityInfoModel> erpAvailabilityInfos) {
        if (isNotEmpty(erpAvailabilityInfos)) {
            modelService.removeAll(erpAvailabilityInfos);
        }
    }

}
