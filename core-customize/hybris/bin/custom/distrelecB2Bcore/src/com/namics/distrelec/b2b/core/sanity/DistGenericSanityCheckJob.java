/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.sanity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.enums.GenericSanityCheckType;
import com.namics.distrelec.b2b.core.model.jobs.DistGenericSanityCheckCronJobModel;
import com.namics.distrelec.b2b.core.sanity.exception.TooManyResultsException;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * Performs a generic sanity check job.
 * 
 * @author pforster, Namics AG
 * @version 3.0.0
 * 
 */
public class DistGenericSanityCheckJob extends AbstractJobPerformable<DistGenericSanityCheckCronJobModel> {
    private static final Logger LOG = Logger.getLogger(DistGenericSanityCheckJob.class);

    @Override
    public PerformResult perform(final DistGenericSanityCheckCronJobModel cronJob) {
        final long startTime = System.nanoTime();
        LOG.info("Starting generic sanity check job.");

        PerformResult result = performJob(cronJob);

        final long endTime = System.nanoTime();
        LOG.info("Finished generic sanity check job in " + (int) ((endTime - startTime) / 1e9) + " seconds.");
        return result;
    }

    protected PerformResult performJob(final DistGenericSanityCheckCronJobModel cronJob) {
        if (GenericSanityCheckType.ABSOLUTSANITYCHECK.equals(cronJob.getType())) {
            return performAbsoluteSanityCheck(cronJob);
        } else if (GenericSanityCheckType.PERCENTAGESANITYCHECK.equals(cronJob.getType())) {
            return performPercentageSanityCheck(cronJob);
        } else {
            throw new IllegalStateException("The selected sanity check type [" + cronJob.getType() + "] is not supported by this job.");
        }
    }

    private PerformResult performAbsoluteSanityCheck(DistGenericSanityCheckCronJobModel cronJob) {
        double numberOfCorruptItems = getCountOfQuery(cronJob.getCheckQuery(), cronJob.getQueryLanguage().getIsocode());

        updateLastResults(cronJob, null, numberOfCorruptItems);

        if (numberOfCorruptItems > cronJob.getThreshold()) {
            LOG.info("Number of corrupt items [" + numberOfCorruptItems + "] has trespassed the threshold [" + cronJob.getThreshold() + "].");
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
        }
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    private PerformResult performPercentageSanityCheck(final DistGenericSanityCheckCronJobModel cronJob) {
        double numberOfTotalItems = getCountOfQuery(cronJob.getBaseQuery(), cronJob.getQueryLanguage().getIsocode());

        double numberOfCorruptItems = getCountOfQuery(cronJob.getCheckQuery(), cronJob.getQueryLanguage().getIsocode());

        if (new Double(numberOfTotalItems).intValue() == 0) {
            LOG.warn("The total number of items returned by the base query mustn't be zero. Otherwise it causes a division by zero. Job aborted.");
            return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
        }

        double percentageCorruptItems = 100 / numberOfTotalItems * numberOfCorruptItems;

        updateLastResults(cronJob, numberOfTotalItems, numberOfCorruptItems);

        if (percentageCorruptItems > cronJob.getThreshold()) {
            LOG.info("Percentage of corrupt items [" + percentageCorruptItems + "%] has trespassed the threshold [" + cronJob.getThreshold() + "%].");
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
        }
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    private double getCountOfQuery(final String queryString, final String queryLanguage) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
        final List<Class<?>> resultClassList = new ArrayList<Class<?>>();
        resultClassList.add(Double.class);
        query.setResultClassList(resultClassList);

        LOG.debug("Executing following query: " + query.getQuery());
        query.setLocale(new Locale(queryLanguage));
        final SearchResult<Object> searchResult = flexibleSearchService.search(query);
        List<Object> resultList = searchResult.getResult();
        if (resultList.size() != 1) {
            throw new TooManyResultsException("The query used in the sanity check job return more or less than one result. Expected one numeric result.");
        }
        return (Double) resultList.get(0);
    }

    private void updateLastResults(DistGenericSanityCheckCronJobModel cronJob, Double lastResultBaseQuery, Double lastResultCheckQuery) {
        if (lastResultBaseQuery != null) {
            cronJob.setLastResultBaseQuery(lastResultBaseQuery.intValue());
        }
        if (lastResultCheckQuery != null) {
            cronJob.setLastResultCheckQuery(lastResultCheckQuery.intValue());
        }
        modelService.save(cronJob);
    }
}
