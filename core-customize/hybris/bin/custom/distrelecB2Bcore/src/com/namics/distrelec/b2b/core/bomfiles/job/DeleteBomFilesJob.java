package com.namics.distrelec.b2b.core.bomfiles.job;

import java.util.List;
import java.util.Map;

import com.namics.distrelec.b2b.core.bomtool.BomToolService;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.jobs.DeleteBomFilesCronJobModel;
import com.namics.distrelec.b2b.core.model.jobs.DistObsolescenceCronJobModel;
import com.namics.distrelec.b2b.core.obsolescence.service.DistObsolescenceService;
import com.namics.distrelec.b2b.core.service.stock.job.StockNotificationEmailJob;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class DeleteBomFilesJob extends AbstractJobPerformable<DeleteBomFilesCronJobModel>{
    private static final Logger LOG = LogManager.getLogger(DeleteBomFilesJob.class);

	@Autowired
	private BomToolService bomToolService;

	@Override
	public PerformResult perform(DeleteBomFilesCronJobModel cronJob) {
		CronJobResult cronJobResult = CronJobResult.SUCCESS;
	    try{
	    	bomToolService.notifyUsersAboutBomFiles();
	    	bomToolService.deleteUnusedBomFiles();
		}catch (Exception e) {
			LOG.error("Either Bom Notification Email was not sent or the unused files were not deleted " + e.getMessage());
			cronJobResult = CronJobResult.ERROR;
			return new PerformResult(cronJobResult, CronJobStatus.FINISHED);
		}

	    LOG.info("CronJob " + cronJob.getCode()+" finished successfully!");
        return new PerformResult(cronJobResult, CronJobStatus.FINISHED);
	}

}
