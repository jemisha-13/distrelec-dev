package com.namics.distrelec.b2b.core.media.storage;

import com.microsoft.azure.storage.AccessCondition;
import com.microsoft.azure.storage.Constants;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.StorageUri;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudBlob;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class DistCloudBlockBlob extends CloudBlob {

    private final AzureBlobLocationNormalizer azureBlobLocationNormalizer;

    private boolean isStreamWriteSizeModified;

    public DistCloudBlockBlob(CloudBlob blob, AzureBlobLocationNormalizer azureBlobLocationNormalizer) throws Exception {
        super(blob);
        this.azureBlobLocationNormalizer = azureBlobLocationNormalizer;
        overwriteStorageUri();
    }

    protected void overwriteStorageUri() throws Exception {
        StorageUri storageUri = getStorageUri();
        URI primaryUri = storageUri.getPrimaryUri();
        String normalizedLocation = azureBlobLocationNormalizer.normalizeLocation(primaryUri.toString());
        URI normalizedUri = new URI(normalizedLocation);
        setStorageUri(new StorageUri(normalizedUri));
    }

    @Override
    public void setStreamWriteSizeInBytes(int streamWriteSizeInBytes) {
        if (streamWriteSizeInBytes <= Constants.MAX_BLOCK_SIZE && streamWriteSizeInBytes >= 16384) {
            this.streamWriteSizeInBytes = streamWriteSizeInBytes;
            this.isStreamWriteSizeModified = true;
        } else {
            throw new IllegalArgumentException("StreamWriteSizeInBytes");
        }
    }

    @Override
    public void upload(InputStream inputStream, long l) throws StorageException, IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void upload(InputStream inputStream, long l, AccessCondition accessCondition, BlobRequestOptions blobRequestOptions, OperationContext operationContext) throws StorageException, IOException {
        throw new UnsupportedOperationException();
    }
}
