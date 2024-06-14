
package com.namics.distrelec.b2b.core.bmecat.export;

import com.namics.distrelec.b2b.core.bmecat.export.exception.DistBMECatExportServiceException;
import com.namics.distrelec.b2b.core.bmecat.export.exception.DistBMECatExportUploadException;
import com.namics.distrelec.b2b.core.bmecat.export.model.DistBMECatExportChannelModel;
import com.namics.distrelec.b2b.core.bmecat.export.model.DistBMECatExportCronJobModel;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.export.DistZipService;
import com.namics.distrelec.b2b.core.inout.export.job.exception.DistFlexibleSearchExportJobException;
import com.namics.distrelec.b2b.core.media.storage.DistMediaStorageStrategy;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.media.storage.MediaStorageRegistry;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.media.MediaIOException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * @author Abhinay Jadhav, Datwyler IT
 * @since 10-Dec-2017
 * 
 */
public class DistBMECatExportHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DistBMECatExportHelper.class);

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

    public void saveExportData(final InputStream exportDataStream, final DistBMECatExportCronJobModel cronJob) throws DistBMECatExportServiceException {
        try {
            final String exportName = getExportName(cronJob);
            DistBMECatExportChannelModel channel = null;
            if (cronJob != null) {
                channel = cronJob.getChannel();
            }

            // create media
            final MediaModel exportMedia = getModelService().create(MediaModel.class);
            exportMedia.setCode(getMediaCode(cronJob));
            if (channel != null) {
                exportMedia.setCatalogVersion(channel.getCatalogVersion());
            } else {
                exportMedia.setCatalogVersion(catalogService.getCatalogVersion("distrelecProductCatalog", "Online"));
            }
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
            if (cronJob != null && cronJob.getChannel() != null) {
                message = "Could not zip output for channel [" + cronJob.getChannel().getChannel() + "].";
            } else {
                message = "Could not zip output. ";
            }
            LOG.error(message, e);
            throw new DistBMECatExportServiceException(message, e);
        } catch (final MediaIOException e) {
            String message = "";
            if (cronJob != null && cronJob.getChannel() != null) {
                message = "Failed writing export data for channel [" + cronJob.getChannel().getChannel() + "] to media.";
            } else {
                message = "Failed writing export BMECat data for channel ";
            }
            LOG.error(message, e);
            throw new DistBMECatExportServiceException(message, e);
        } catch (final ModelSavingException e) {
            String message = "";
            if (cronJob != null && cronJob.getChannel() != null) {
                message = "Failed saving export data for channel [" + cronJob.getChannel().getChannel() + "] to media.";
            } else {
                message = "Failed saving BMECat export data ";
            }
            LOG.error(message, e);
            throw new DistBMECatExportServiceException(message, e);
        }
    }

    /**
     * Name of export, e.g. cronJob.mediaPrefix + "distrelec_D_7310_ch_de"
     */
    public String getExportName(final DistBMECatExportCronJobModel cronJob) {
        final StringBuilder name = new StringBuilder();

        if (cronJob.getMediaPrefix().startsWith(MEDIA_PREFIX_ERP) || cronJob.getMediaPrefix().startsWith(MEDIA_PREFIX_PRODUCT_INFO)) {
            return cronJob.getMediaPrefix();
        }

        if (StringUtils.isNotBlank(cronJob.getMediaPrefix())) {
            name.append(cronJob.getMediaPrefix());
        }
        name.append(cronJob.getChannel().getChannel());
        return name.toString();
    }

    /**
     * Media code, e.g. cronJob.mediaPrefix + "distrelec_D_7310_ch_de_20131022-111121"
     */
    private String getMediaCode(final DistBMECatExportCronJobModel cronJob) {
        final String exportName = getExportName(cronJob);

        final StringBuilder mediaCode = new StringBuilder();
        mediaCode.append(exportName);
        mediaCode.append('_');
        mediaCode.append(FastDateFormat.getInstance("yyyyMMdd-HHmmss").format(new Date()));

        return mediaCode.toString();
    }

    public void saveExternal(final DistBMECatExportCronJobModel cronJob, boolean useCronJobsExportDirecotry) throws DistBMECatExportUploadException {
        InputStream input = null;
        try {
            input = mediaService.getStreamFromMedia(cronJob.getMedia());
            final String uploadFileName = getExportName(cronJob) + ZIP_FILE_SUFFIX;
            String uploadDirectoryName = getConfigurationService().getConfiguration().getString(DistConstants.PropKey.FactFinder.EXPORT_UPLOAD_DIRECTORY,
                    StringUtils.EMPTY);
            if (useCronJobsExportDirecotry) {
                uploadDirectoryName = cronJob.getExportDirectory();
            }

            Long size = cronJob.getMedia().getSize();

            Map<String, Object> metaData = new HashMap<>();
            metaData.put("size", size);
            metaData.put("fileName", uploadDirectoryName + uploadFileName);

            MediaStorageConfigService.MediaFolderConfig config = getExportsMediaFolderConfig();
            DistMediaStorageStrategy mediaStorageStrategy = getMediaStorageStrategy(config);
            mediaStorageStrategy.store(config, uploadFileName, metaData, input);
        } catch (final Exception e) {
            String message = "";

            message = "Failed to save export for channel [" + cronJob.getChannel() + "] because an exception occured when writing external file.";

            LOG.error(message, e);
            throw new DistBMECatExportUploadException(message, e);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    protected MediaStorageConfigService.MediaFolderConfig getExportsMediaFolderConfig() {
        MediaStorageConfigService.MediaFolderConfig config = getMediaStorageConfigService().getConfigForFolder(DistConstants.AzureMediaFolder.EXPORTS);
        return config;
    }

    protected DistMediaStorageStrategy getMediaStorageStrategy(MediaStorageConfigService.MediaFolderConfig config) {
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
