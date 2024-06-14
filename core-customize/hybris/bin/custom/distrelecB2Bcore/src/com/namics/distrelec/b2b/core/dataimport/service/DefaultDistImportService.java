/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.service;

import com.google.common.base.Preconditions;
import com.namics.distrelec.b2b.core.model.jobs.impex.DistImpExImportCronJobModel;

import de.hybris.platform.impex.model.cronjob.ImpExImportCronJobModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.servicelayer.exceptions.ModelInitializationException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.impl.DefaultImportService;
import de.hybris.platform.servicelayer.impex.impl.ImportCronJobResult;
import de.hybris.platform.util.Config;

/**
 * {@code DefaultDistImportService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.8
 */
public class DefaultDistImportService extends DefaultImportService {

    @Override
    public ImportResult importData(final ImportConfig config) {
        final ImpExImportCronJobModel cronJob = getModelService().create(DistImpExImportCronJobModel._TYPECODE);
        try {
            getModelService().initDefaults(cronJob);
        } catch (final ModelInitializationException e) {
            throw new SystemException(e);
        }

        configureCronJob(cronJob, config);
        getModelService().saveAll(cronJob.getJob(), cronJob);
        importData(cronJob, config.isSynchronous(), config.isRemoveOnSuccess());

        if (getModelService().<Item> getSource(cronJob).isAlive()) {
            return new ImportCronJobResult(cronJob);
        }

        return new ImportCronJobResult(getModelService().<Item> getSource(cronJob).isAlive() ? cronJob : null);
    }

    public boolean isDistributedImpexEnabled(final ImportConfig config) {
        Boolean enabledFromConfig = config.isDistributedImpexEnabled();
        return enabledFromConfig != null ? enabledFromConfig : Config.getBoolean("impex.import.service.distributed.enabled", false);
    }

    protected ImportResult importDataUsingStandardImpex(final ImportConfig config) {
        Preconditions.checkState(!isDistributedImpexEnabled(config), "To import using standard ImpEx distributed ImpEx flag must be disabled");
        final ImpExImportCronJobModel cronJob = getModelService().create(DistImpExImportCronJobModel._TYPECODE);

        try {
            this.getModelService().initDefaults(cronJob);
        } catch (ModelInitializationException arg3) {
            throw new SystemException(arg3);
        }

        configureCronJob(cronJob, config);
        getModelService().saveAll(cronJob.getJob(), cronJob);
        importData(cronJob, config.isSynchronous(), config.isRemoveOnSuccess());
        return new ImportCronJobResult(getModelService().<Item> getSource(cronJob).isAlive() ? cronJob : null);
    }

}
