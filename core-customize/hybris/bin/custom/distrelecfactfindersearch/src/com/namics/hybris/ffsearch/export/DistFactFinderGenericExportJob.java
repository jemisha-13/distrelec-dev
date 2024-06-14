/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.export;

import java.io.InputStream;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.export.DistCsvTransformationService;
import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchQueryCreator;
import com.namics.hybris.ffsearch.exception.DistFactFinderExportServiceException;
import com.namics.hybris.ffsearch.exception.DistFactFinderExportUploadException;
import com.namics.hybris.ffsearch.model.export.DistFactFinderExportCronJobModel;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

/**
 * Job to export the result of a FlexibleSearch to CSV.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DistFactFinderGenericExportJob extends AbstractJobPerformable<DistFactFinderExportCronJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(DistFactFinderGenericExportJob.class);

    private ConfigurationService configurationService;

    private DistFlexibleSearchQueryCreator distFlexibleSearchQueryCreator;
    private DistCsvTransformationService distCsvTransformationService;
    private DistFactFinderExportHelper exportHelper;

    @Override
    public PerformResult perform(final DistFactFinderExportCronJobModel cronJob) {
        CronJobResult result = CronJobResult.FAILURE;
        final String query = getDistFlexibleSearchQueryCreator().createQuery();

        try {
            final InputStream exportData = getDistCsvTransformationService().transform(query, Collections.<String, Object> emptyMap(),';');
            getExportHelper().saveExportData(exportData, cronJob);
            boolean scpUpload = getConfigurationService().getConfiguration().getBoolean(DistConstants.PropKey.FactFinder.EXPORT_UPLOAD_VIA_SCP, false);
            getExportHelper().saveExternal(cronJob, false, scpUpload);

            result = CronJobResult.SUCCESS;
        } catch (final DistFactFinderExportServiceException e) {
            LOG.error("Could not export data", e);
        } catch (final DistFactFinderExportUploadException e) {
            LOG.error("Could not save file", e);
        }

        return new PerformResult(result, CronJobStatus.FINISHED);
    }

    // BEGIN GENERATED CODE

    public DistFactFinderExportHelper getExportHelper() {
        return exportHelper;
    }

    @Required
    public void setExportHelper(final DistFactFinderExportHelper exportHelper) {
        this.exportHelper = exportHelper;
    }

    public DistCsvTransformationService getDistCsvTransformationService() {
        return distCsvTransformationService;
    }

    @Required
    public void setDistCsvTransformationService(final DistCsvTransformationService distCsvTransformationService) {
        this.distCsvTransformationService = distCsvTransformationService;
    }

    public DistFlexibleSearchQueryCreator getDistFlexibleSearchQueryCreator() {
        return distFlexibleSearchQueryCreator;
    }

    @Required
    public void setDistFlexibleSearchQueryCreator(final DistFlexibleSearchQueryCreator distFlexibleSearchQueryCreator) {
        this.distFlexibleSearchQueryCreator = distFlexibleSearchQueryCreator;
    }
    
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Required
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    // END GENERATED CODE

}
