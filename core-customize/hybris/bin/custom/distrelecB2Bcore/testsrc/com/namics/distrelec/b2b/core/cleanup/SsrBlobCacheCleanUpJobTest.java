package com.namics.distrelec.b2b.core.cleanup;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.Cache.SSR_CACHE_CLEANUP_SEGMENT_SIZE;
import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.Cache.SSR_CACHE_CONTAINER_NAME;
import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.Cache.SSR_CACHE_TTL_HOUR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.EnumSet;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.microsoft.azure.storage.ResultContinuation;
import com.microsoft.azure.storage.ResultSegment;
import com.microsoft.azure.storage.blob.BlobListingDetails;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SsrBlobCacheCleanUpJobTest {

    private final String ssrCacheContainerName = "ssrCache";

    private final int segmentSize = 10;

    private final int ttlHour = 1;

    @InjectMocks
    SsrBlobCacheCleanUpJob cleanUpJob;

    @Mock
    CloudBlobClient client;

    @Mock
    ConfigurationService configurationService;

    @Mock
    CloudBlobContainer containerRef;

    @Mock
    CronJobModel cronJobModel;

    @Mock
    ModelService modelService;

    @Before
    public void setUp() throws Exception {
        Configuration config = mock(Configuration.class);
        when(config.getString(SSR_CACHE_CONTAINER_NAME)).thenReturn(ssrCacheContainerName);
        when(config.getInt(SSR_CACHE_CLEANUP_SEGMENT_SIZE)).thenReturn(segmentSize);
        when(config.getInt(SSR_CACHE_TTL_HOUR)).thenReturn(ttlHour);
        when(configurationService.getConfiguration()).thenReturn(config);

        when(client.getContainerReference(ssrCacheContainerName)).thenReturn(containerRef);
    }

    @Test
    public void deletesOldBlob() throws Exception {
        ResultSegment<ListBlobItem> resultSegment = new ResultSegmentBuilder().aBlob(2).build();
        when(containerRef.listBlobsSegmented(null, true, EnumSet.noneOf(BlobListingDetails.class), segmentSize, null, null, null))
                                                                                                                                  .thenReturn(resultSegment);

        PerformResult performResult = cleanUpJob.perform(cronJobModel);

        assertThat(performResult.getResult()).isEqualTo(CronJobResult.SUCCESS);
        assertThat(performResult.getStatus()).isEqualTo(CronJobStatus.FINISHED);

        ListBlobItem blobItem = resultSegment.getResults()
                                             .get(0);
        verify((CloudBlockBlob) blobItem).delete();
    }

    @Test
    public void doesNotDeleteNewBlob() throws Exception {
        ResultSegment<ListBlobItem> resultSegment = new ResultSegmentBuilder().aBlob(0).build();
        when(containerRef.listBlobsSegmented(null, true, EnumSet.noneOf(BlobListingDetails.class), segmentSize, null, null, null))
                                                                                                                                  .thenReturn(resultSegment);

        PerformResult performResult = cleanUpJob.perform(cronJobModel);

        assertThat(performResult.getResult()).isEqualTo(CronJobResult.SUCCESS);
        assertThat(performResult.getStatus()).isEqualTo(CronJobStatus.FINISHED);

        ListBlobItem blobItem = resultSegment.getResults()
                                             .get(0);
        verify((CloudBlockBlob) blobItem, never()).delete();
    }

    @Test
    public void useContinuationTokenForFollowingSegment() throws Exception {
        ResultContinuation continuationToken = mock(ResultContinuation.class);

        ResultSegment<ListBlobItem> firstSegment = new ResultSegmentBuilder().hasMoreResults(continuationToken).build();
        ResultSegment<ListBlobItem> secondSegment = new ResultSegmentBuilder().aBlob(2).build();

        when(containerRef.listBlobsSegmented(null, true, EnumSet.noneOf(BlobListingDetails.class), segmentSize, null, null, null))
                                                                                                                                  .thenReturn(firstSegment);
        when(containerRef.listBlobsSegmented(null, true, EnumSet.noneOf(BlobListingDetails.class), segmentSize, continuationToken, null, null))
                                                                                                                                               .thenReturn(secondSegment);

        PerformResult performResult = cleanUpJob.perform(cronJobModel);

        assertThat(performResult.getResult()).isEqualTo(CronJobResult.SUCCESS);
        assertThat(performResult.getStatus()).isEqualTo(CronJobStatus.FINISHED);

        ListBlobItem blobItem = secondSegment.getResults()
                                             .get(0);
        verify((CloudBlockBlob) blobItem).delete();
    }

    @Test
    public void doesNotProceedIfAborted() throws Exception {
        ResultContinuation continuationToken = mock(ResultContinuation.class);

        ResultSegment<ListBlobItem> firstSegment = new ResultSegmentBuilder().aBlob(2).hasMoreResults(continuationToken).build();

        when(containerRef.listBlobsSegmented(null, true, EnumSet.noneOf(BlobListingDetails.class), segmentSize, null, null, null))
                                                                                                                                  .thenReturn(firstSegment);
        when(cronJobModel.getRequestAbort()).thenReturn(true); // aborted

        PerformResult performResult = cleanUpJob.perform(cronJobModel);

        assertThat(performResult.getResult()).isEqualTo(CronJobResult.FAILURE);
        assertThat(performResult.getStatus()).isEqualTo(CronJobStatus.ABORTED);

        ListBlobItem blobItem = firstSegment.getResults()
                                            .get(0);
        verify((CloudBlockBlob) blobItem).delete();
        verify(containerRef, atMostOnce()).listBlobsSegmented(any(), anyBoolean(), any(), any(), any(), any(), any());
    }
}
