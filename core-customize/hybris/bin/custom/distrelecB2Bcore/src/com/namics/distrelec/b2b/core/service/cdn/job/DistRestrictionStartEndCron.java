package com.namics.distrelec.b2b.core.service.cdn.job;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.cdn.DistrelecCDNService;
import com.namics.distrelec.b2b.core.restriction.service.DistRestrictionService;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class DistRestrictionStartEndCron extends AbstractJobPerformable<CronJobModel> {

	private static final Logger LOG = Logger.getLogger(DistRestrictionStartEndCron.class);

	private static final int LATEST_CRON_HISTORY = 2;

	@Autowired
	private DistRestrictionService distRestrictionService;
	
	@Autowired
	private DistrelecCDNService distrelecCDNService;

	@Override
	public PerformResult perform(CronJobModel cronJobModel) {
		final long startTime = System.nanoTime();
		LOG.info("Starting DistrelecRestrictionCDNCron");
		try {
			Date startDate = getStartDate(cronJobModel);
			if (startDate != null) {
				Set<String> websiteUid = distRestrictionService.getCDNSitesForClearingCache(startDate);
				Iterator<String> websiteIterator = websiteUid.iterator();
				while (websiteIterator.hasNext()) {
					distrelecCDNService.registerCDNCacheClearRequest(websiteIterator.next());
				}
			}

		} catch (Exception e) {
			LOG.error("An error ocurred while running CDN Cache Clearing Job", e);
			return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
		}
		final long endTime = System.nanoTime();
		LOG.info("Finished DistrelecRestrictionCDNCron in " + (int) ((endTime - startTime) / 1e9) + " seconds.");
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	private Date getStartDate(CronJobModel cronJobModel) {
		return cronJobModel.getCronJobHistoryEntries().size() >= LATEST_CRON_HISTORY
				? cronJobModel.getCronJobHistoryEntries()
						.get(cronJobModel.getCronJobHistoryEntries().size() - LATEST_CRON_HISTORY).getEndTime()
				: null;
	}

}
