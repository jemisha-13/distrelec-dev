package com.namics.distrelec.b2b.core.cleanup;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.azure.storage.ResultContinuation;
import com.microsoft.azure.storage.ResultSegment;
import com.microsoft.azure.storage.blob.BlobProperties;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

class ResultSegmentBuilder {

    private final List<Integer> subtrackHours = new ArrayList<>();

    private ResultContinuation continuationToken;

    ResultSegmentBuilder aBlob(int subtractHour) {
        subtrackHours.add(subtractHour);
        return this;
    }

    ResultSegmentBuilder hasMoreResults(ResultContinuation continuationToken) {
        this.continuationToken = continuationToken;
        return this;
    }

    ResultSegment<ListBlobItem> build() {
        ResultSegment<ListBlobItem> resultSegment = mock(ResultSegment.class);

        List<CloudBlockBlob> blockBlobs = subtrackHours.stream()
                                                       .map(this::mockCloudBlockBlob)
                                                       .collect(Collectors.toList());

        ArrayList<ListBlobItem> blobs = new ArrayList<>(blockBlobs);
        when(resultSegment.getResults()).thenReturn(blobs);

        if (continuationToken != null) {
            when(resultSegment.getHasMoreResults()).thenReturn(true);
            when(resultSegment.getContinuationToken()).thenReturn(continuationToken);
        }

        return resultSegment;
    }

    private CloudBlockBlob mockCloudBlockBlob(int subtractHours) {
        CloudBlockBlob blockBlob = mock(CloudBlockBlob.class);
        BlobProperties props = mock(BlobProperties.class);
        when(blockBlob.getProperties()).thenReturn(props);

        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.HOUR, -subtractHours);
        Date lastModified = cal.getTime();
        when(props.getLastModified()).thenReturn(lastModified);

        return blockBlob;
    }
}
