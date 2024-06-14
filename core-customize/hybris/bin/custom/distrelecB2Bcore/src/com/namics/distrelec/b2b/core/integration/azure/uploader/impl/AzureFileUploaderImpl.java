package com.namics.distrelec.b2b.core.integration.azure.uploader.impl;

import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.namics.distrelec.b2b.core.integration.azure.uploader.AzureFileUploader;
import de.hybris.platform.cloud.azure.hotfolder.remote.session.AzureBlobSession;
import de.hybris.platform.cloud.azure.hotfolder.remote.session.AzureBlobSessionFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.io.*;
import java.nio.file.Paths;

public class AzureFileUploaderImpl implements AzureFileUploader {

    private static final Logger LOG = LoggerFactory.getLogger(AzureFileUploaderImpl.class);

    @Autowired
    private AzureBlobSession blobSession;

    private String locationPrefix;

    public AzureFileUploaderImpl(AzureBlobSessionFactory sessionFactory) {
        Assert.notNull(sessionFactory, "`sessionFactory` is mandatory");
        blobSession = (AzureBlobSession) sessionFactory.getSession();
    }

    public void uploadAndDeleteLocalFile(File file, String path) {
        upload(file, path, true);
    }

    protected void upload(File file, String path, boolean deleteLocalFile) {
        final String objectName = file.getName();

        LOG.debug("Processing remote file: {}", objectName);

        try (BufferedInputStream bis = new BufferedInputStream(FileUtils.openInputStream(file))) {

            path = locationPrefix + (path.endsWith("/") ? path.substring(0, path.length() - 1) : path) + "/" + objectName;

            blobSession.write(bis, path);

            LOG.info("Uploading file '{}' to hot-folder container finished", objectName);

            if (deleteLocalFile) {
                FileUtils.deleteQuietly(file);
            }
        } catch (Exception e) {
            LOG.error("Failed to store '" + objectName + "' in blob container", e);
        }
    }

    public void uploadToContainerAndDeleteLocalFile(File file, String path, String containerName) {
        uploadToContainer(file, path, containerName, true);
    }

    public void uploadToContainer(File file, String targetPath, String containerName, boolean deleteUploadedFile) {
        LOG.info("Uploading file '{}' to hot-folder container {}", file.getName(), containerName);

        try {
            CloudBlobContainer blobContainer = blobSession.getClientInstance().getContainerReference(containerName);
            CloudBlockBlob blob = blobContainer.getBlockBlobReference(locationPrefix + targetPath + "/" + file.getName());

            blob.uploadFromFile(file.getPath());

            LOG.info("Uploading file '{}' to hot-folder container finished", file.getName());

            if (deleteUploadedFile) {
                file.delete();
            }
        } catch (Exception e) {
            LOG.error("Failed to upload '" + file.getName() + "' from blob container", e);
        }
    }

    public void downloadAllAndKeepOriginals(String cloudPath, String localPath) {
        downloadAll(cloudPath, localPath, false);
    }

    public void downloadAllAndDeleteInCloud(String cloudPath, String localPath) {
        downloadAll(cloudPath, localPath, true);
    }

    protected void downloadAll(String cloudPath, String localPath, boolean deleteDownloadedFile) {
        LOG.debug("Getting remote files: {} from int blob container", cloudPath);
        try {

            String pathWithPrefix = locationPrefix + cloudPath;
            String[] files = blobSession.listNames(pathWithPrefix);

            for (String file : files) {
                download(file, localPath, deleteDownloadedFile);
            }
        } catch (Exception e) {
            LOG.error("Failed to get blobs in blob container path " + cloudPath, e);
        }
    }


    public void download(String cloudFileLocation, String localPath) {
        download(cloudFileLocation, localPath, false);
    }

    /**
     * Assume path is just a filename for now
     *
     * @param cloudFileLocation
     */
    public void download(String cloudFileLocation, String localPath, boolean deleteDownloadedFile) {
        LOG.debug("Getting remote file: {} from int blob container ", cloudFileLocation);

        try {

            CloudBlobContainer blobContainer = blobSession.getClientInstance().getContainerReference("hybris");
            CloudBlockBlob blob = blobContainer.getBlockBlobReference(locationPrefix + cloudFileLocation);
            File downloadedFile = new File(localPath + "/" + Paths.get(cloudFileLocation).getFileName());

            downloadedFile.getParentFile().mkdirs();

            blob.downloadToFile(downloadedFile.getPath());

            LOG.info("Downloading file '{}' to hot-folder container finished", downloadedFile.getName());

            if (deleteDownloadedFile) {
                blob.deleteIfExists();
            }
        } catch (Exception e) {
            LOG.error("Failed to download '" + cloudFileLocation + "' from blob container", e);
        }
    }

    public InputStream readRaw(String path) throws IOException {
        return blobSession.readRaw(path);
    }

    public boolean exists(String path) {
        path = locationPrefix + path;
        try {
            return blobSession.exists(path);
        } catch (Exception e) {
            LOG.error("Failed to check if file exists");
        }
        return false;
    }

    public void rename(String source, String destination) throws IOException {
        blobSession.rename(source, destination);
    }

    @Required
    public void setLocationPrefix(String locationPrefix) {
        this.locationPrefix = StringUtils.trimToEmpty(locationPrefix).toLowerCase().replaceAll("[/. !?]", "");
    }
}
