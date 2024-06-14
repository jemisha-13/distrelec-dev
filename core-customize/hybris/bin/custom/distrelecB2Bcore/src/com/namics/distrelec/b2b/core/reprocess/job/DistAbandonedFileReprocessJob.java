package com.namics.distrelec.b2b.core.reprocess.job;

import com.microsoft.azure.storage.blob.CloudBlob;
import com.namics.distrelec.b2b.core.model.jobs.DistAbandonedFileReprocessCronJobModel;
import de.hybris.platform.cloud.azure.hotfolder.remote.session.AzureBlobSession;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DistAbandonedFileReprocessJob extends AbstractJobPerformable<DistAbandonedFileReprocessCronJobModel> {

    private static final Logger LOG = Logger.getLogger(DistAbandonedFileReprocessJob.class);

    @Autowired
    private ConfigurationService configurationService;

    private AzureBlobSession blobSession;

    private String azureHotfolderRemotePath;

    @Override
    public PerformResult perform(final DistAbandonedFileReprocessCronJobModel cronJob) {
        final long startTime = System.nanoTime();
        LOG.info("Starting reprocessing of abandoned files.");
        try {
            performJob(cronJob);
        } catch (final IOException e) {
            LOG.error("Exception occurred during Azure Blob Session operation", e);
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
        }
        final long endTime = System.nanoTime();
        LOG.info("Finished reprocessing of abandoned files in " + (int) ((endTime - startTime) / 1e9) + " seconds.");
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    private void performJob(final DistAbandonedFileReprocessCronJobModel cronJob) throws IOException {
        String abandonProcessingTgtLocation = azureHotfolderRemotePath + File.separator + getConfigurationService().getConfiguration().getString(cronJob.getDirectoryKey());
        String abandonProcessingSrcLocation = abandonProcessingTgtLocation + "/processing";
        LOG.info("Starting abandon processing feeds cleanup. Location:" + abandonProcessingSrcLocation);

        Date now = new Date();

        String[] processingFileNames = blobSession.listNames(abandonProcessingSrcLocation);
        for (final String processingFileName : processingFileNames) {
            CloudBlob cb = blobSession.get(processingFileName);
            Date lm = cb.getProperties().getLastModified();
            long hoursDiff = TimeUnit.HOURS.convert(Math.abs(now.getTime() - lm.getTime()), TimeUnit.MILLISECONDS);

            if (hoursDiff > 10) {
                blobSession.rename(processingFileName, abandonProcessingTgtLocation + cb.getName().substring(cb.getName().lastIndexOf("/")));
                LOG.info("Abandon feed:" + processingFileName + " moved back to queue.");
            }
        }
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setBlobSession(AzureBlobSession blobSession) {
        this.blobSession = blobSession;
    }

    public void setAzureHotfolderRemotePath(String azureHotfolderRemotePath) {
        this.azureHotfolderRemotePath = azureHotfolderRemotePath;
    }
}
