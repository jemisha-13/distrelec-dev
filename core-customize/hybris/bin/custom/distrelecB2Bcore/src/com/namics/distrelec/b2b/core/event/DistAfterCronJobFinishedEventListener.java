package com.namics.distrelec.b2b.core.event;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.namics.distrelec.b2b.core.cdn.DistrelecCDNService;
import com.namics.distrelec.b2b.core.radware.exception.DistRadwareAPIException;

import de.hybris.platform.core.Registry;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.event.events.AfterCronJobFinishedEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

public class DistAfterCronJobFinishedEventListener extends AbstractEventListener<AfterCronJobFinishedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(DistAfterCronJobFinishedEventListener.class);

    private static final String CATALOG_VERSION_JOB_TYPE = "CatalogVersionSyncCronJob";

    private static final int START_INDEX = 5;

    private static final int END_INDEX = 17;

    private static final String SYNC_JOB_PATTERN = "sync ";

    private static final String EMPTY_STRING = "";

    @Autowired
    private DistrelecCDNService distrelecCDNService;

    @Autowired
    private ConfigurationService configurationService;

    @Resource
    private CronJobService cronJobService;

    public BusinessProcessService getBusinessProcessService() {
        return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }

    @Override
    protected void onEvent(final AfterCronJobFinishedEvent cronJobFinishEvent) {
        try {
            if (cronJobFinishEvent.getCronJobType().equalsIgnoreCase(CATALOG_VERSION_JOB_TYPE)) {
                String synchJob = cronJobFinishEvent.getCronJob();
                String websiteUid = getWebsiteUid(synchJob);
                if (!websiteUid.isEmpty() && getWebsiteSpecificRadwareApp(websiteUid) != null
                        && !getWebsiteSpecificRadwareApp(websiteUid).isEmpty()) {
                    distrelecCDNService.registerCDNCacheClearRequest(websiteUid);
                }
            }
        } catch (JsonProcessingException e) {
            LOG.error("Error in Clearing Radware CDN Cache");
        } catch (DistRadwareAPIException e) {
            LOG.error("Error in Clearing Radware CDN Cache");
        }

    }

    private String getWebsiteUid(String jobName) {
        CronJobModel cronJob = cronJobService.getCronJob(jobName);
        return cronJob.getJob().getCode().contains(SYNC_JOB_PATTERN) ? cronJob.getJob().getCode().substring(START_INDEX, END_INDEX) : EMPTY_STRING;
    }

    private String getWebsiteSpecificRadwareApp(String websiteUid) {
        return configurationService.getConfiguration().getString("radware.api." + websiteUid + ".app.code");
    }

}
