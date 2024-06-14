package com.namics.distrelec.b2b.core.obsolescence.job;

import java.util.List;
import java.util.Map;

import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
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

public class DistObsolescenceJob extends AbstractJobPerformable<DistObsolescenceCronJobModel>{
    private static final Logger LOG = LogManager.getLogger(DistObsolescenceJob.class);

	@Autowired
	private DistObsolescenceService distObsolescenceService;
	
	@Override
	public PerformResult perform(DistObsolescenceCronJobModel arg0) {
		// YTODO Auto-generated method stub
	    boolean success = true;		
				
		Map<String, List<AbstractOrderEntryModel>> phasedOutOrderEntries = distObsolescenceService.getPhasedOutOrderEntries();
		
		if(phasedOutOrderEntries!=null && !phasedOutOrderEntries.isEmpty() ) {
			try {	
				distObsolescenceService.sendEmail(phasedOutOrderEntries);
			}catch (Exception e) {
                LOG.error("Obsolescence Notification Email was not sent: " + e.getMessage());
			}
		}
        return new PerformResult(success ? CronJobResult.SUCCESS : CronJobResult.ERROR, CronJobStatus.FINISHED);
	}

}
