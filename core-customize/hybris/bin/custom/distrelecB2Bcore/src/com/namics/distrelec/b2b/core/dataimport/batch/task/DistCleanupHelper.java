/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.batch.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.CleanupHelper;
import de.hybris.platform.acceleratorservices.dataimport.batch.util.BatchDirectoryUtils;

import de.hybris.platform.cloud.azure.hotfolder.remote.session.AzureBlobSessionFactory;
import de.hybris.platform.cloud.azure.hotfolder.remote.session.AzureBlobSession;

/**
 * Cleanup for the pim/impex import process. Deletes the transformed file (if required) and moves the processed file to the archive
 * directory.
 */
public class DistCleanupHelper extends CleanupHelper {

    private static final Logger LOG = LogManager.getLogger(DistCleanupHelper.class);

    private boolean cleanupTransformedFiles;
    private Boolean finalArchive;
    private String destinationFolder;
    private String searchFileNamePattern;
    private String renameFileNamePattern;
    private String cleanupRemoteFolder;
    private static final String ARCHIVE_DIRECTORY = "archive";
    private static final String ERROR_DIRECTORY = "error";
    private static final String PROCESSING_DIRECTORY = "processing";
    private static final String DATE_SEPARATOR = "_";
    private AzureBlobSessionFactory cleanupAzureBlobSessionFactory;

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.acceleratorservices.dataimport.batch.task.CleanupHelper#cleanup(de.hybris.platform.acceleratorservices.dataimport.
     * batch.BatchHeader, boolean)
     */
    @Override
    public void cleanup(final BatchHeader header, final boolean error) {
        if (error) {
            LOG.error("Error occured for file [" + header + "]");
        }
        if (isFinalArchive().booleanValue()) {
            archiveFile(header, error);
        } else {
            moveFile(header, error);
        }
    }

    private void moveFile(final BatchHeader header, final boolean error) {
        if (header != null) {
            if (isCleanupTransformedFiles()) {
                super.cleanupTransformedFiles(header);
            }
            moveSourceFile(header, error);
        }
    }

    /**
     * Removes the transformed file
     * 
     * @param header
     * @param error
     */
    private void moveSourceFile(final BatchHeader header, final boolean error) {
        if (header.getFile() != null) {
            final File movedFile = getDestFile(header.getFile(), new File(getDestinationFolder()), error);
            try {
                moveFileWithTemporaryFile(header.getFile(), movedFile);
            } catch (IOException e) {
                LOG.warn("Could not move " + header.getFile() + " to " + movedFile, e);
            }
        }
    }

    private void moveFileWithTemporaryFile(final File sourceFile, final File movedFile) throws IOException {
        File tempFile = new File(movedFile.getPath() + "temp_" + movedFile.getName() + "_temp");

        FileUtils.moveFile(sourceFile, tempFile);
        FileUtils.moveFile(tempFile, movedFile);
    }

    @Override
    public void cleanupFile(File file) {
        if (!file.delete())
        {
            LOG.warn("Could not delete " + file);
        }else{
            LOG.info("Deleted file {}", file.getName());
        }
    }

    private void archiveFile(final BatchHeader header, final boolean error) {
        if (header != null) {
            if (isCleanupTransformedFiles()) {
                super.cleanupTransformedFiles(header);
            }
            distCleanupSourceFile(header, error);
        }
    }

    protected void distCleanupSourceFile(final BatchHeader header, final boolean error)
    {
        if (header.getFile() != null)
        {
            AzureBlobSession blobSession = (AzureBlobSession) cleanupAzureBlobSessionFactory.getSession();

            try (FileInputStream fis = new FileInputStream(header.getFile()))
            {
                final String path = distGetDestLocation(cleanupRemoteFolder, error, header.getFile().getName());
                LOG.info("Archiving File to Blob Storage remote location:" + path);
                blobSession.write(fis, path);
                final String processingCleanupPath = distGetProcessingCleanupLocation(cleanupRemoteFolder, header.getFile().getName());
                LOG.info("Cleaning processing location. Removing: " + processingCleanupPath);
                blobSession.remove(processingCleanupPath);
                } catch (IOException e) {
                    LOG.error(e.getMessage());
                } finally {
                if (!FileUtils.deleteQuietly(header.getFile())) {
                    LOG.warn("Failed to remove file: {}", header.getFile());
                }
            }
        }
    }

    protected String distGetDestLocation(final String cleanupRemoteFolder, final boolean error, final String filename)
    {
        final StringBuilder builder = new StringBuilder();
        builder.append (error ? cleanupRemoteFolder + File.separator + ERROR_DIRECTORY : cleanupRemoteFolder + File.separator + ARCHIVE_DIRECTORY);
        builder.append(File.separator);
        builder.append(filename);

        if (!StringUtils.isBlank(getTimeStampFormat()))
        {
            final SimpleDateFormat sdf = new SimpleDateFormat(getTimeStampFormat(), Locale.getDefault());
            builder.append(DATE_SEPARATOR);
            builder.append(sdf.format(new Date()));
        }
        return builder.toString();
    }

    protected String distGetProcessingCleanupLocation(final String cleanupRemoteFolder, final String filename)
    {
        final StringBuilder builder = new StringBuilder();
        builder.append (cleanupRemoteFolder + File.separator + PROCESSING_DIRECTORY);
        builder.append(File.separator);
        builder.append(filename);
        return builder.toString();
    }

    /**
     * Returns the destination location of the file
     * 
     * @param source
     * @param error
     *            flag indicating if there was an error
     * @return the destination file
     */
    protected File getDestFile(final File source, final File destFolder, final boolean error) {
        String targetFileName = source.getName().replaceAll(getSearchFileNamePattern(), getRenameFileNamePattern());
        return new File(error ? BatchDirectoryUtils.getRelativeErrorDirectory(source) : destFolder.getAbsolutePath(), targetFileName);
    }

    public boolean isCleanupTransformedFiles() {
        return cleanupTransformedFiles;
    }

    @Required
    public void setCleanupTransformedFiles(final boolean cleanupTransformedFiles) {
        this.cleanupTransformedFiles = cleanupTransformedFiles;
    }

    public Boolean isFinalArchive() {
        return finalArchive == null ? Boolean.TRUE : finalArchive;
    }

    public void setFinalArchive(final Boolean finalArchive) {
        this.finalArchive = finalArchive == null ? Boolean.TRUE : finalArchive;
    }

    public String getRenameFileNamePattern() {
        return renameFileNamePattern;
    }

    public void setRenameFileNamePattern(final String renameFileNamePattern) {
        this.renameFileNamePattern = renameFileNamePattern;
    }

    public String getDestinationFolder() {
        return destinationFolder;
    }

    public void setDestinationFolder(final String destinationFolder) {
        this.destinationFolder = destinationFolder;
    }

    public String getSearchFileNamePattern() {
        return searchFileNamePattern;
    }

    public void setSearchFileNamePattern(final String searchFileNamePattern) {
        this.searchFileNamePattern = searchFileNamePattern;
    }

    public String getCleanupRemoteFolder() {
        return cleanupRemoteFolder;
    }

    public void setCleanupRemoteFolder(final String cleanupRemoteFolder) {
        this.cleanupRemoteFolder = cleanupRemoteFolder;
    }

    public AzureBlobSessionFactory getCleanupAzureBlobSessionFactory() {
        return cleanupAzureBlobSessionFactory;
    }

    public void setCleanupAzureBlobSessionFactory(final AzureBlobSessionFactory cleanupAzureBlobSessionFactory) {
        this.cleanupAzureBlobSessionFactory = cleanupAzureBlobSessionFactory;
    }
}
