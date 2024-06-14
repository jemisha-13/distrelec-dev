/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.export;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.namics.distrelec.b2b.core.media.storage.DistMediaStorageStrategy;
import com.namics.hybris.ffsearch.model.export.DistFactFinderSequentialExportCronJobModel;
import com.namics.hybris.ffsearch.util.DistFactFinderUtils;
import de.hybris.platform.catalog.CatalogService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.namics.distrelec.b2b.core.constants.DistConstants.AzureMediaFolder;
import com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.FactFinder;
import com.namics.distrelec.b2b.core.inout.export.DistZipService;
import com.namics.distrelec.b2b.core.inout.export.job.exception.DistFlexibleSearchExportJobException;
import com.namics.hybris.exports.model.export.DistExportCronJobModel;
import com.namics.hybris.ffsearch.exception.DistFactFinderExportServiceException;
import com.namics.hybris.ffsearch.exception.DistFactFinderExportUploadException;
import com.namics.hybris.ffsearch.model.DistFactFinderExportChannelModel;
import com.namics.hybris.ffsearch.model.export.DistFactFinderExportCronJobModel;
import com.namics.hybris.util.ScpUtils;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.media.storage.MediaStorageConfigService.MediaFolderConfig;
import de.hybris.platform.media.storage.MediaStorageRegistry;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.media.MediaIOException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Helper methods for the FactFinder Export.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class DistFactFinderExportHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DistFactFinderExportHelper.class);

    private static final String MEDIA_PREFIX_ERP = "erpExport";
    private static final String MEDIA_PREFIX_PRODUCT_INFO = "productInfoExport";

    private static final String CSV_FILE_SUFFIX = ".csv";
    private static final String ZIP_FILE_SUFFIX = ".zip";

    private MediaService mediaService;
    private ModelService modelService;
    private DistZipService distZipService;
    private ConfigurationService configurationService;
    private CatalogService catalogService;
    private MediaStorageConfigService mediaStorageConfigService;
    private MediaStorageRegistry mediaStorageRegistry;

    public MediaModel createExportMedia(final DistExportCronJobModel cronJob, final DistFactFinderExportChannelModel channel) {
        // create media
        final MediaModel exportMedia = getModelService().create(MediaModel.class);
        exportMedia.setCode(getMediaCode(cronJob, channel));
        exportMedia.setMime("application/zip");
        if (channel != null) {
            exportMedia.setCatalogVersion(channel.getCatalogVersion());
        } else {
            exportMedia.setCatalogVersion(catalogService.getCatalogVersion("distrelecProductCatalog", "Online"));
        }
        getModelService().save(exportMedia);

        return exportMedia;
    }

    public void saveExportData(final InputStream exportDataStream, final DistExportCronJobModel cronJob) throws DistFactFinderExportServiceException {
        try {
            DistFactFinderExportChannelModel channel = null;
            if (cronJob instanceof DistFactFinderExportCronJobModel) {
                channel = ((DistFactFinderExportCronJobModel) cronJob).getChannel();
            }
            final String exportName = getExportName(cronJob, channel);

            // create media
            final MediaModel exportMedia = createExportMedia(cronJob, channel);
            getModelService().save(exportMedia);

            // Zip output
            final String exportFileName = exportName + CSV_FILE_SUFFIX;
            final byte[] exportData = getDistZipService().zip(exportDataStream, exportFileName);

            // save the zipped output, consider using pipes when zipfile should get to big
            getMediaService().setDataForMedia(exportMedia, exportData);

            cronJob.setMedia(exportMedia);
            getModelService().save(cronJob);

        } catch (final DistFlexibleSearchExportJobException e) {
            String message = "";
            if (cronJob instanceof DistFactFinderExportCronJobModel) {
                message = "Could not zip output for channel [" + ((DistFactFinderExportCronJobModel) cronJob).getChannel().getChannel() + "].";
            }
            LOG.error(message, e);
            throw new DistFactFinderExportServiceException(message, e);
        } catch (final MediaIOException e) {
            String message = "";
            if (cronJob instanceof DistFactFinderExportCronJobModel) {
                message = "Failed writing export data for channel [" + ((DistFactFinderExportCronJobModel) cronJob).getChannel().getChannel() + "] to media.";
            }
            LOG.error(message, e);
            throw new DistFactFinderExportServiceException(message, e);
        } catch (final ModelSavingException e) {
            String message = "";
            if (cronJob instanceof DistFactFinderExportCronJobModel) {
                message = "Failed saving export data for channel [" + ((DistFactFinderExportCronJobModel) cronJob).getChannel().getChannel() + "] to media.";
            }
            LOG.error(message, e);
            throw new DistFactFinderExportServiceException(message, e);
        }
    }

    /**
     * Name of export, e.g. cronJob.mediaPrefix + "distrelec_D_7310_ch_de"
     */
    public String getExportName(final DistExportCronJobModel cronJob, final DistFactFinderExportChannelModel channel) {
        final StringBuilder name = new StringBuilder();

        if (cronJob.getMediaPrefix().startsWith(MEDIA_PREFIX_ERP) || cronJob.getMediaPrefix().startsWith(MEDIA_PREFIX_PRODUCT_INFO)) {
            return cronJob.getMediaPrefix();
        }

        if (StringUtils.isNotBlank(cronJob.getMediaPrefix())) {
            name.append(cronJob.getMediaPrefix());
        }
        if (channel != null) {
            name.append(channel.getChannel());
        }
        return name.toString();
    }

    /**
     * Media code, e.g. cronJob.mediaPrefix + "distrelec_D_7310_ch_de_20131022-111121"
     */
    private String getMediaCode(final DistExportCronJobModel cronJob, final DistFactFinderExportChannelModel channel) {
        final String exportName = getExportName(cronJob, channel);

        final StringBuilder mediaCode = new StringBuilder();
        mediaCode.append(exportName);
        mediaCode.append('_');
        mediaCode.append(FastDateFormat.getInstance("yyyyMMdd-HHmmss").format(new Date()));

        return mediaCode.toString();
    }

    public void saveExternal(final DistExportCronJobModel cronJob, boolean useCronJobsExportDirecotry, boolean scpUpload) throws DistFactFinderExportUploadException {
        Map<DistFactFinderExportChannelModel, MediaModel> channelMediaMap = getChannelMediaModelMap(cronJob);

        if (channelMediaMap != null) {
            for (Entry<DistFactFinderExportChannelModel, MediaModel> channelMediaEntry : channelMediaMap.entrySet()) {
                DistFactFinderExportChannelModel channelModel = channelMediaEntry.getKey();
                MediaModel mediaModel = channelMediaEntry.getValue();
                LOG.debug(String.format("export %s channel", channelModel.getCode()));
                saveExternalForChannel(cronJob, useCronJobsExportDirecotry, scpUpload, channelModel, mediaModel);
            }
        } else {
            saveExternalForChannel(cronJob, useCronJobsExportDirecotry, scpUpload, null, cronJob.getMedia());
        }
    }

    private void saveExternalForChannel(DistExportCronJobModel cronJob, boolean useCronJobsExportDirecotry,
            boolean scpUpload, DistFactFinderExportChannelModel channelModel, MediaModel mediaModel) throws DistFactFinderExportUploadException {
        InputStream input = null;
        try {
            input = mediaService.getStreamFromMedia(mediaModel);
            final String uploadFileName = getExportName(cronJob, channelModel) + ZIP_FILE_SUFFIX;
            String uploadDirectoryName = getUploadDirectoryName(cronJob, useCronJobsExportDirecotry);
            uploadToAzureExportsStorage(mediaModel, input, uploadFileName, uploadDirectoryName);
            if (scpUpload) {
                // upload to scp
                IOUtils.closeQuietly(input);
                input = mediaService.getStreamFromMedia(mediaModel);
                uploadToScp(input, uploadFileName);
            }
        } catch (final IOException | JSchException e) {
            String message;
            if (channelModel != null) {
                message = "Failed to save export for channel [" + channelModel + "] because an exception occured when writing external file.";
            } else {
                message = "Failed to save export because an exception occured when writing external file.";
            }
            LOG.error(message, e);
            throw new DistFactFinderExportUploadException(message, e);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    private Map<DistFactFinderExportChannelModel, MediaModel> getChannelMediaModelMap(DistExportCronJobModel cronJob) {
        Map<DistFactFinderExportChannelModel, MediaModel> channelMediaMap = null;
        if (cronJob instanceof DistFactFinderExportCronJobModel) {
            channelMediaMap = new HashMap<>();
            DistFactFinderExportCronJobModel factFinderExportCronJobModel = (DistFactFinderExportCronJobModel) cronJob;
            channelMediaMap.put(factFinderExportCronJobModel.getChannel(), factFinderExportCronJobModel.getMedia());
        } else if (cronJob instanceof DistFactFinderSequentialExportCronJobModel) {
            channelMediaMap = new HashMap<>();
            DistFactFinderSequentialExportCronJobModel sequentialExportCronJobModel = (DistFactFinderSequentialExportCronJobModel) cronJob;
            Collection<DistFactFinderExportChannelModel> channels = DistFactFinderUtils.getActiveChannelsForCMSSite(sequentialExportCronJobModel.getCmsSite());
            Map<String, DistFactFinderExportChannelModel> indexedChannels = new HashMap<>();
            for (DistFactFinderExportChannelModel channelModel : channels) {
                String exportName = getExportName(sequentialExportCronJobModel, channelModel);
                indexedChannels.put(exportName, channelModel);
            }
            for (MediaModel mediaModel : sequentialExportCronJobModel.getMedias()) {
                String mediaCode = mediaModel.getCode();
                String exportName = mediaCode.substring(0, mediaCode.lastIndexOf("_"));
                DistFactFinderExportChannelModel channelModel = indexedChannels.remove(exportName);
                channelMediaMap.put(channelModel, mediaModel);
            }
            if (!indexedChannels.isEmpty()) {
                throw new IllegalStateException("there're not exported medias for channels");
            }
        }
        return channelMediaMap;
    }

    protected String getUploadDirectoryName(final DistExportCronJobModel cronJob, boolean useCronJobsExportDirectory) {
        if (useCronJobsExportDirectory) {
            // configuration entry may override cronjob export directory
            String configKey = String.format("%s%s%s", FactFinder.EXPORT_DIRECTORY_PREFIX, cronJob.getCode(), FactFinder.EXPORT_DIRECTORY_SUFFIX);
            String configuredExportDirectory = getConfigurationService().getConfiguration().getString(configKey);
            if (StringUtils.isNotBlank(configuredExportDirectory)) {
                return configuredExportDirectory;
            } else {
                return cronJob.getExportDirectory();
            }
        } else {
            return getConfigurationService().getConfiguration().getString(FactFinder.EXPORT_UPLOAD_DIRECTORY, StringUtils.EMPTY);
        }
    }

    protected void uploadToAzureExportsStorage(MediaModel mediaModel, InputStream input, String uploadFileName,
            String uploadDirectoryName) {
        Long size = mediaModel.getSize();

        Map<String, Object> metaData = new HashMap<>();
        metaData.put("size", size);
        metaData.put("fileName", uploadDirectoryName + uploadFileName);

        MediaFolderConfig config = getExportsMediaFolderConfig();
        DistMediaStorageStrategy mediaStorageStrategy = getMediaStorageStrategy(config);
        mediaStorageStrategy.store(config, uploadFileName, metaData, input);
    }

    protected void uploadToScp(InputStream from, String fileName) throws JSchException, IOException {
        uploadToScp(from, fileName, null);
    }

    @Deprecated
    protected void uploadToScp(InputStream from, String fileName, File file) throws JSchException, IOException {
        final Configuration config = getConfigurationService().getConfiguration();
        final String user = config.getString(FactFinder.EXPORT_UPLOAD_SCP_USER);
        final String host = config.getString(FactFinder.EXPORT_UPLOAD_SCP_HOST);
        final int port = config.getInt(FactFinder.EXPORT_UPLOAD_SCP_PORT, 22);
        final String to = config.getString(FactFinder.EXPORT_UPLOAD_SCP_FOLDER);
        final String privateKey = config.getString(FactFinder.EXPORT_UPLOAD_SCP_PRIVATE_KEY);
        final String keyPassword = config.getString(FactFinder.EXPORT_UPLOAD_SCP_KEYPASSWORD);

        LOG.info("Upload {} to {}@{}:{}", fileName, user, host, to);

        Session session = null;
        try {
            session = ScpUtils.createSession(user, host, port, "ffrsa", privateKey, keyPassword);
            ScpUtils.copyLocalToRemote(session, from, to, fileName);
        } finally {
            if (session != null) {
                session.disconnect();
            }
            //this.azureFileUploader.uploadAndDeleteLocalFile(file, cloudHotfolderFactFinderUploadDirectory);
        }
    }

    protected MediaFolderConfig getExportsMediaFolderConfig() {
        MediaFolderConfig config = getMediaStorageConfigService().getConfigForFolder(AzureMediaFolder.EXPORTS);
        return config;
    }

    protected DistMediaStorageStrategy getMediaStorageStrategy(MediaFolderConfig config) {
        DistMediaStorageStrategy mediaStorageStrategy =
                (DistMediaStorageStrategy) getMediaStorageRegistry().getStorageStrategyForFolder(config);
        return mediaStorageStrategy;
    }

    // BEGIN GENERATED CODE

    protected MediaService getMediaService() {
        return mediaService;
    }

    @Required
    public void setMediaService(final MediaService mediaService) {
        this.mediaService = mediaService;
    }

    protected ModelService getModelService() {
        return modelService;
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public DistZipService getDistZipService() {
        return distZipService;
    }

    @Required
    public void setDistZipService(final DistZipService distZipService) {
        this.distZipService = distZipService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Required
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public CatalogService getCatalogService() {
        return catalogService;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public MediaStorageConfigService getMediaStorageConfigService() {
        return mediaStorageConfigService;
    }

    public void setMediaStorageConfigService(MediaStorageConfigService mediaStorageConfigService) {
        this.mediaStorageConfigService = mediaStorageConfigService;
    }

    public MediaStorageRegistry getMediaStorageRegistry() {
        return mediaStorageRegistry;
    }

    public void setMediaStorageRegistry(MediaStorageRegistry mediaStorageRegistry) {
        this.mediaStorageRegistry = mediaStorageRegistry;
    }
}
