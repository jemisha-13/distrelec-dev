package com.namics.distrelec.b2b.core.cleanup;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.Cache.SSR_CACHE_CLEANUP_SEGMENT_SIZE;
import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.Cache.SSR_CACHE_CONTAINER_NAME;
import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.Cache.SSR_CACHE_TTL_HOUR;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.microsoft.azure.storage.ResultContinuation;
import com.microsoft.azure.storage.ResultSegment;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobListingDetails;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class SsrBlobCacheCleanUpJob extends AbstractJobPerformable<CronJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(SsrBlobCacheCleanUpJob.class);

    private CloudBlobClient client;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public PerformResult perform(CronJobModel cronJobModel) {
        Configuration config = configurationService.getConfiguration();
        String ssrCacheContainerName = config.getString(SSR_CACHE_CONTAINER_NAME);
        int segmentSize = config.getInt(SSR_CACHE_CLEANUP_SEGMENT_SIZE);
        Date cutDate = calculateCutDate(config);
        long totalDeleted = 0;
        boolean isAborted;

        try {
            CloudBlobContainer containerRef = client.getContainerReference(ssrCacheContainerName);

            ResultContinuation continuationToken = null;
            ResultSegment<ListBlobItem> resultSegment;

            do {
                resultSegment = containerRef.listBlobsSegmented(null, true, EnumSet.noneOf(BlobListingDetails.class), segmentSize,
                                                                continuationToken, null, null);
                continuationToken = resultSegment.getContinuationToken();
                ArrayList<ListBlobItem> results = resultSegment.getResults();

                long deleteNum = results.stream()
                                        .map(blobItem -> (CloudBlockBlob) blobItem)
                                        .filter(blockBlob -> blockBlob.getProperties().getLastModified().before(cutDate))
                                        .filter(this::deleteBlockBlob)
                                        .count();
                totalDeleted += deleteNum;
                isAborted = clearAbortRequestedIfNeeded(cronJobModel);
            } while (resultSegment.getHasMoreResults() && !isAborted);
        } catch (Exception e) {
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
        }

        LOG.info("Deleted blobs: " + totalDeleted);
        if (!isAborted) {
            return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        } else {
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
        }
    }

    @Override
    public boolean isAbortable() {
        return true;
    }

    private static Date calculateCutDate(Configuration config) {
        int ttlHour = config.getInt(SSR_CACHE_TTL_HOUR);
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.HOUR, -ttlHour);
        Date cutDate = cal.getTime();
        return cutDate;
    }

    private boolean deleteBlockBlob(CloudBlockBlob blockBlob) {
        try {
            blockBlob.delete();
            return true;
        } catch (StorageException e) {
            LOG.warn("Unable to remove block blob: " + blockBlob.getName(), e);
            return false;
        }
    }

    public void setClient(CloudBlobClient client) {
        this.client = client;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
