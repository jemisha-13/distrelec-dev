package com.namics.distrelec.b2b.core.inout.export.impl;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.namics.distrelec.b2b.core.inout.export.DistOrderExportService;
import com.namics.distrelec.b2b.core.model.inout.DistOrderExportCronJobModel;
import com.namics.distrelec.b2b.core.reevoo.util.ScpUtils;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;
import java.io.InputStream;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.CronJob.DistOrderExport.ATTRIBUTE_FTP_DIRECTORY;
import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.CronJob.DistOrderExport.ATTRIBUTE_FTP_HOST;
import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.CronJob.DistOrderExport.ATTRIBUTE_FTP_PASSWORD;
import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.CronJob.DistOrderExport.ATTRIBUTE_FTP_PORT;
import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.CronJob.DistOrderExport.ATTRIBUTE_FTP_USER;

public class DistDefaultOrderExportService implements DistOrderExportService {

    private static final Logger LOG = LogManager.getLogger(DistDefaultOrderExportService.class);

    private ConfigurationService configurationService;
    private ModelService modelService;
    private MediaService mediaService;

    private KeyGenerator mediaCodeKeyGenerator;

    @Override
    public boolean saveExternal(final InputStream data, final String exportedFileName) {
        final String sftpDirectory = getConfigurationService().getConfiguration().getString(ATTRIBUTE_FTP_DIRECTORY);
        final String sftpHost = getConfigurationService().getConfiguration().getString(ATTRIBUTE_FTP_HOST);
        final int sftpPort = getConfigurationService().getConfiguration().getInt(ATTRIBUTE_FTP_PORT, 22);
        final String sftpUser = getConfigurationService().getConfiguration().getString(ATTRIBUTE_FTP_USER);
        final String sftpPassword = getConfigurationService().getConfiguration().getString(ATTRIBUTE_FTP_PASSWORD);

        Session session = null;
        try {
            session = ScpUtils.createSession(sftpUser, sftpHost, sftpPort, sftpPassword);
            final String uploadFileName = exportedFileName + ".csv";
            ScpUtils.copyLocalToRemote(session, data, sftpDirectory, uploadFileName);
        } catch (JSchException | IOException e) {
            LOG.error("Unable to upload file", e);
            return false;
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
        return true;
    }

    @Override
    public MediaModel createMedia(final InputStream data, final DistOrderExportCronJobModel cronJob) {
        final MediaModel media = getModelService().create(MediaModel.class);
        media.setCode(getMediaCode(cronJob));
        media.setCatalogVersion(cronJob.getMediaCatalogVersion());
        cronJob.setMedia(media);
        getModelService().save(media);
        getMediaService().setStreamForMedia(media, data);
        return media;
    }

    private String getMediaCode(DistOrderExportCronJobModel cronJob) {
        return cronJob.getMediaPrefix() + "_" + getMediaCodeKeyGenerator().generate().toString();
    }

    public KeyGenerator getMediaCodeKeyGenerator() {
        return mediaCodeKeyGenerator;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public MediaService getMediaService() {
        return mediaService;
    }

    @Required
    public void setMediaService(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @Autowired
    @Qualifier("core.exportMediaCodeKeyGenerator")
    public void setMediaCodeKeyGenerator(final KeyGenerator mediaCodeKeyGenerator) {
        this.mediaCodeKeyGenerator = mediaCodeKeyGenerator;
    }
}
