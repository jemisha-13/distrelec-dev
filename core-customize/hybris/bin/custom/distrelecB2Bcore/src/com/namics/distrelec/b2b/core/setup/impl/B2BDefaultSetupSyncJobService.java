/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.core.setup.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;
import com.namics.distrelec.b2b.core.model.MultipleCatalogsSyncCronJobModel;
import com.namics.distrelec.b2b.core.setup.DistSetupSyncJobService;

import de.hybris.platform.catalog.model.SyncItemCronJobModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.commerceservices.setup.impl.DefaultSetupSyncJobService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * 
 * Specialized {@link DefaultSetupSyncJobService} performing some preconfigured (imported previously)
 * {@link MultipleCatalogsSyncCronJobModel} instance
 * 
 */
public class B2BDefaultSetupSyncJobService extends DefaultSetupSyncJobService implements DistSetupSyncJobService {

    private static final Logger LOG = Logger.getLogger(B2BDefaultSetupSyncJobService.class);

    private FlexibleSearchService flexibleSearchService;
    private CronJobService cronJobService;

    private static final String CRON_JOB_SUBFIX = "SyncCronJob";

    @Override
    public PerformResult executeCatalogSyncJob(final String catalog) {
        return executeCatalogSyncJob(catalog, false);

        // final MultipleCatalogsSyncCronJobModel cronJobModel = getCronJobIfExists(catalog);
        // getModelService().save(cronJobModel);
        // cronJobService.performCronJob(cronJobModel, true);
        // return new PerformResult(cronJobModel.getResult(), cronJobModel.getStatus());
    }

    @Override
    public PerformResult executeCatalogSyncJob(String catalogId, boolean fullSync) {
        final SyncItemJobModel catalogSyncJob = getCatalogSyncJob(catalogId);
        if (catalogSyncJob == null) {
            LOG.error("Couldn't find 'SyncItemJob' for catalog [" + catalogId + "]", null);
            return new PerformResult(CronJobResult.UNKNOWN, CronJobStatus.UNKNOWN);
        } else {
            final SyncItemCronJobModel syncJob = getLastFailedSyncCronJob(catalogSyncJob, fullSync);

            LOG.info("Created cronjob [" + syncJob.getCode() + "] to synchronize catalog [" + catalogId
                             + "] staged to online version.");
            LOG.info("Starting synchronization, this may take a while ...");

            getCronJobService().performCronJob(syncJob, true);

            LOG.info("Synchronization complete for catalog [" + catalogId + "]");

            final CronJobResult result = syncJob.getResult();
            final CronJobStatus status = syncJob.getStatus();

            return new PerformResult(result, status);
        }
    }

    protected SyncItemCronJobModel getLastFailedSyncCronJob(final SyncItemJobModel syncItemJob, boolean fullSync) {
        SyncItemCronJobModel syncCronJob = super.getLastFailedSyncCronJob(syncItemJob);

        syncCronJob.setFullSync(fullSync);
        if (fullSync) {
            syncCronJob.setForceUpdate(true);
        }
        getModelService().save(syncCronJob);

        return syncCronJob;
    }

    protected MultipleCatalogsSyncCronJobModel getCronJobIfExists(final String catalog) {
        final MultipleCatalogsSyncCronJobModel example = new MultipleCatalogsSyncCronJobModel();
        example.setCode(catalog + CRON_JOB_SUBFIX);

        final MultipleCatalogsSyncCronJobModel cronJobModel = flexibleSearchService.getModelByExample(example);

        Preconditions.checkNotNull(cronJobModel);

        return cronJobModel;
    }

    @Required
    public void setCronJobService(final CronJobService cronJobService) {
        this.cronJobService = cronJobService;
    }

    /**
     * @return the cronJobService
     */
    public CronJobService getCronJobService() {
        return cronJobService;
    }

    @Required
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
