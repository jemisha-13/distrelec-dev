package com.namics.distrelec.b2b.core.service.eol.job;

import com.namics.distrelec.b2b.core.model.RemoveEolProductsCronJobModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.eol.service.DistEOLProductsRemovalService;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class DistEOLProductsRemovalJob extends AbstractJobPerformable<RemoveEolProductsCronJobModel> {
    
    private static final Logger LOG = Logger.getLogger(DistEOLProductsRemovalJob.class);
    
    @Autowired
    private DistEOLProductsRemovalService eolProductsRemovalService;
    
    @Override
    public PerformResult perform(RemoveEolProductsCronJobModel cronJob) {
        final long startTime = System.nanoTime();
        LOG.info("Starting eolProductsRemovalJob");
        try {
            eolProductsRemovalService.removeEOLProducts(cronJob.getMaxMonthsWithRef(),
                    cronJob.getMaxMonthsWithoutRef(),
                    cronJob.getRemoveLimit());
        } catch (Exception e) {
            LOG.error("An error ocurred while performing eol products removal job", e);
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
        }
        final long endTime = System.nanoTime();
        LOG.info("Finished eolProductsRemovalJob in " + (int) ((endTime - startTime) / 1e9) + " seconds.");
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }
}
