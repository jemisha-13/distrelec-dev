package com.namics.distrelec.b2b.core.service.pricerow.job;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.RemoveOrphanedPriceRowsCronJobModel;
import com.namics.distrelec.b2b.core.service.pricerow.service.OrphanedPriceRowsRemovalService;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class OrphanedPriceRowsRemovalJob extends AbstractJobPerformable<RemoveOrphanedPriceRowsCronJobModel> {

    private static final Logger LOG = Logger.getLogger(OrphanedPriceRowsRemovalJob.class);

    private static final int DEFAULT_LIMIT = 5000;

    @Autowired
    private OrphanedPriceRowsRemovalService orphanedPriceRowsRemovalService;

    @Override
    public PerformResult perform(RemoveOrphanedPriceRowsCronJobModel cronJob) {
        final long startTime = System.nanoTime();
        LOG.info("OrphanedPriceRowsRemovalJob started");

        try {
            Integer limit = cronJob.getLimit();
            if (limit == null || limit <= 0) {
                limit = DEFAULT_LIMIT;
            }

            orphanedPriceRowsRemovalService.removeOrphanedPriceRows(limit);
        } catch (Exception e) {
            LOG.error("An error ocurred while performing orphaned price rows removal job", e);
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
        }

        final long endTime = System.nanoTime();
        LOG.info("OrphanedPriceRowsRemovalJob finished in " + (int) ((endTime - startTime) / 1e9) + " seconds.");
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

}
