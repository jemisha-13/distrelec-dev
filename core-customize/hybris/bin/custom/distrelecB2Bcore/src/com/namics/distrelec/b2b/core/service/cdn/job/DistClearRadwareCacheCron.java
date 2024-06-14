package com.namics.distrelec.b2b.core.service.cdn.job;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.cdn.DistrelecCDNService;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class DistClearRadwareCacheCron extends AbstractJobPerformable<CronJobModel> {

    private static final Logger LOG = Logger.getLogger(DistClearRadwareCacheCron.class);
    
    @Autowired
    private DistrelecCDNService distrelecCDNService;

    @Override
    public PerformResult perform(CronJobModel cronJobModel) {
        final long startTime = System.nanoTime();
        LOG.debug("Starting DistClearRadwareCacheCron");
        try {
            distrelecCDNService.clearCDNCache();

        } catch (Exception e) {
            LOG.error("An error ocurred while running CDN Cache Clearing Job", e);
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
        }
        final long endTime = System.nanoTime();
        LOG.debug("Finished DistClearRadwareCacheCron in " + (int) ((endTime - startTime) / 1e9) + " seconds.");
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }


}
