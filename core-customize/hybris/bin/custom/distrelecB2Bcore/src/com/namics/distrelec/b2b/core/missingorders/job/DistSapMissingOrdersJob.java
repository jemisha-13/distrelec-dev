package com.namics.distrelec.b2b.core.missingorders.job;

import com.namics.distrelec.b2b.core.missingorders.service.CreateMissingOrdersResult;
import com.namics.distrelec.b2b.core.missingorders.service.DistMissingOrdersService;
import com.namics.distrelec.b2b.core.missingorders.service.MissingOrdersMatchResult;
import com.namics.distrelec.b2b.core.model.jobs.DistSapMissingOrdersCronJobModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class DistSapMissingOrdersJob extends AbstractJobPerformable<DistSapMissingOrdersCronJobModel> {
    private static final Logger LOG = LogManager.getLogger(DistSapMissingOrdersJob.class);

    @Autowired
    private DistMissingOrdersService distMissingOrdersService;

    @Override
    public PerformResult perform(DistSapMissingOrdersCronJobModel cronJob) {
        try {
            MissingOrdersMatchResult matchResult = distMissingOrdersService.matchMissingOrders(cronJob.getNumberOfDays(), cronJob.isFetchOrdersByDays(), cronJob.getOrdersFromDate(), cronJob.getOrdersToDate());
            CreateMissingOrdersResult createResult = distMissingOrdersService.createSapOrders(matchResult.getOrdersMissingInErp());

            if (distMissingOrdersService.sendReportEmail(matchResult.getOrdersFoundInErp(),
                    createResult.getSuccessfullyCreatedOrders(),
                    createResult.getFailedOrders())) {
                return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
            } else {
                return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
            }
        } catch (Exception e) {
            LOG.error("Unable to create missing orders in SAP: " + e.getMessage());
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
        }
    }

}
