package com.namics.distrelec.b2b.core.media.storage;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import com.namics.distrelec.b2b.core.media.storage.impl.DistStoredMediaData;
import de.hybris.platform.azure.media.AzureCloudUtils;
import de.hybris.platform.azure.media.storage.WindowsAzureBlobStorageStrategy;
import de.hybris.platform.media.exceptions.ExternalStorageServiceException;
import de.hybris.platform.media.exceptions.MediaNotFoundException;
import de.hybris.platform.media.exceptions.MediaStoreException;
import de.hybris.platform.media.storage.MediaStorageConfigService.MediaFolderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class DistWindowsAzureBlobStorageStrategy extends WindowsAzureBlobStorageStrategy implements DistMediaStorageStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(DistWindowsAzureBlobStorageStrategy.class);

    private AzureBlobLocationNormalizer locationNormalizer;

    @Override
    public List<DistStoredMediaData> listFilesInFolder(MediaFolderConfig config, String folder) {
        try {
            List<DistStoredMediaData> files = new ArrayList<>();

            CloudBlobDirectory blobDirectory = getBlobDirectoryReference(config, folder);
            Iterable<ListBlobItem> blobIterable = blobDirectory.listBlobs();
            Iterator<ListBlobItem> blobIt = blobIterable.iterator();
            while (blobIt.hasNext()) {
                ListBlobItem listBlobItem = blobIt.next();
                if (listBlobItem instanceof CloudBlockBlob) {
                    CloudBlockBlob blob = (CloudBlockBlob) listBlobItem;
                    DistStoredMediaData storedMediaData;
                    if (downloadAttributes(blob, false)) {
                        String location = blob.getName();

                        storedMediaData = createStoredMediaData(location, folder, blob, true);
                    } else {
                        DistCloudBlockBlob distBlob = new DistCloudBlockBlob(blob, getLocationNormalizer());
                        boolean downloaded = downloadAttributes(distBlob, true);
                        String location = distBlob.getName();
                        String denormalizedLocation = getLocationNormalizer().denormalizeLocation(location);

                        storedMediaData = createStoredMediaData(denormalizedLocation, folder, blob, downloaded);
                    }
                    files.add(storedMediaData);
                }
            }

            return files;
        } catch (Exception e) {
            throw new MediaStoreException(e);
        }
    }

    protected DistStoredMediaData createStoredMediaData(String location, String folder, CloudBlockBlob blob, boolean downloaded) {
        String fileName = location.substring(folder.length());
        long size = blob.getProperties().getLength();
        Date lastModified = blob.getProperties().getLastModified();
        return new DistStoredMediaData(fileName, null, size, null, lastModified, downloaded);
    }

    private boolean downloadAttributes(CloudBlob blob, boolean logError) {
        try {
            blob.downloadAttributes();
            return true;
        } catch (StorageException e) {
            if (logError) {
                LOG.error("Error occurred while downloading attributes on location: {}", blob.getName());
            }
            return false;
        }
    }

    @Override
    public InputStream getAsStream(MediaFolderConfig config, String location) {
        CloudBlockBlob blob = null;
        try {
            blob = getBlockBlobReference(config, location);
            return blob.openInputStream();
        } catch (Exception e1) {
            try {
                DistCloudBlockBlob distBlob = new DistCloudBlockBlob(blob, getLocationNormalizer());
                return distBlob.openInputStream();
            } catch (Exception e2) {
                throw new MediaNotFoundException("Media not found (requested media location: " + location + ")", e2);
            }
        }
    }

    @Override
    public long getSize(MediaFolderConfig config, String location) {
        try {
            CloudBlockBlob blob = getBlockBlobReference(config, location);
            if (downloadAttributes(blob, false)) {
                return blob.getProperties().getLength();
            } else {
                DistCloudBlockBlob distBlob = new DistCloudBlockBlob(blob, getLocationNormalizer());
                distBlob.downloadAttributes();
                return distBlob.getProperties().getLength();
            }
        } catch (Exception var4) {
            throw new MediaNotFoundException("Media not found (requested media location: " + location + ")", var4);
        }
    }

    private CloudBlockBlob getBlockBlobReference(MediaFolderConfig config, String location) throws Exception {
        CloudBlobClient blobClient = getCloudBlobClient(config);
        CloudBlobContainer container = getContainerForFolder(config, blobClient);
        return container.getBlockBlobReference(location);
    }

    private AzureBlobLocationNormalizer getLocationNormalizer() {
        if (locationNormalizer == null) {
            locationNormalizer = new AzureBlobLocationNormalizerImpl();
        }
        return locationNormalizer;
    }

    private CloudBlobDirectory getBlobDirectoryReference(MediaFolderConfig config, String folder) throws Exception {
        CloudBlobClient blobClient = getCloudBlobClient(config);
        CloudBlobContainer container = getContainerForFolder(config, blobClient);
        return container.getDirectoryReference(folder);
    }

    private CloudBlobContainer getContainerForFolder(MediaFolderConfig config, CloudBlobClient blobClient) throws Exception {
        Integer numRetries = config.getParameter("createContainer.numRetries", Integer.class);
        Integer delayInSeconds = config.getParameter("createContainer.delayInSeconds", Integer.class);
        int retries = 0;

        while(true) {
            String containerName = AzureCloudUtils.computeContainerAddress(config);

            try {
                CloudBlobContainer container = blobClient.getContainerReference(containerName);
                container.createIfNotExists();
                return container;
            } catch (StorageException var9) {
                if (retries >= numRetries) {
                    throw var9;
                }

                Duration sleepTime = Duration.ofSeconds((long)delayInSeconds);
                LOG.debug("Can't create container. Reason: \"{}\". Retrying for container \"{}\" with settings - num retries: {}, try: {}, delay: {}",
                  var9.getMessage(), containerName, numRetries, retries, delayInSeconds);
                Thread.sleep(sleepTime.toMillis());
                ++retries;
            }
        }
    }

    private CloudBlobClient getCloudBlobClient(MediaFolderConfig config) throws Exception {
        String connectionString = getConnectionString(config);
        CloudStorageAccount account = CloudStorageAccount.parse(connectionString);
        return account.createCloudBlobClient();
    }

    private String getConnectionString(MediaFolderConfig config) {
        String connectionString = config.getParameter("connection");
        if (connectionString == null) {
            throw new ExternalStorageServiceException("Windows Azure specific configuration not found [key: connection was empty");
        } else {
            return connectionString;
        }
    }
}
