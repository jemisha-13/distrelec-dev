/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.nps.jobs;

import static com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Email.NPS_EMAIL_CC;
import static com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Email.NPS_EMAIL_TO;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.microsoft.azure.storage.blob.CloudBlob;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.inout.export.DistZipService;
import com.namics.distrelec.b2b.core.integration.azure.uploader.impl.AzureFileUploaderImpl;
import com.namics.distrelec.b2b.core.mail.internal.DistInternalMailContext;
import com.namics.distrelec.b2b.core.model.jobs.NetPromoterDataTransferCronJobModel;

import de.hybris.platform.cloud.azure.hotfolder.remote.session.AzureBlobSession;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class NetPromoterDataTransferJob extends AbstractJobPerformable<NetPromoterDataTransferCronJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(NetPromoterDataTransferJob.class);

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private DistZipService distZipService;

    private AzureBlobSession blobSession;

    private String azureHotfolderLocalDirectoryBase;

    private String azureHotfolderRemotePath;

    private AzureFileUploaderImpl azureFileUploader;

    @Override
    public PerformResult perform(NetPromoterDataTransferCronJobModel npsCronJob) {
        final Configuration configuration = getConfigurationService().getConfiguration();
        File file = getLatestFilefromDir(configuration.getString(DistConstants.PropKey.NetPromoterScore.EXPORT_DIRECTORY_PROPERTY));
        boolean result = send(createContext(), file, npsCronJob);
        file.delete();
        return new PerformResult(result ? CronJobResult.SUCCESS : CronJobResult.ERROR, CronJobStatus.FINISHED);
    }

    // Setup the email content data in email context
    protected DistInternalMailContext createContext() {
        final DistInternalMailContext context = new DistInternalMailContext();
        final Configuration configuration = getConfigurationService().getConfiguration();
        final String emailSubject = configuration.getString("nps.mail.subject");

        context.setEmailSubject(emailSubject);
        return context;
    }

    // Send an email, with successful orders csv
    protected boolean send(final DistInternalMailContext context, final File fileData, final NetPromoterDataTransferCronJobModel npsCronJob) {
        final MimeMessagePreparator preparator = mimeMessage -> {
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            final Configuration configuration = getConfigurationService().getConfiguration();

            message.setSubject(context.getEmailSubject());
            message.setText(configuration.getString("nps.from.mail.text"));
            message.setFrom(configuration.getString("nps.from.mail.id"));

            // config value overrides value stored in db - for lower environments
            String toMailAddresses = configuration.containsKey(NPS_EMAIL_TO) ? configuration.getString(NPS_EMAIL_TO) : npsCronJob.getToMailAddress();
            if (!StringUtils.isEmpty(toMailAddresses)) {
                message.setTo(toMailAddresses.split(DistConstants.Punctuation.COMMA));
            }

            // config value overrides value stored in db - for lower environments
            String toCCMailAddresses = configuration.containsKey(NPS_EMAIL_CC) ? configuration.getString(NPS_EMAIL_CC) : npsCronJob.getCcMailAddress();
            if (!StringUtils.isEmpty(toCCMailAddresses)) {
                message.setCc(toCCMailAddresses.split(DistConstants.Punctuation.COMMA));
            }

            zipDataAndAddAsAttachment(fileData, message);
            message.setValidateAddresses(true);
        };
        try {
            mailSender.send(preparator);
            LOG.info("NPS Files Email Sent At : {}", new Date());
            return true;
        } catch (final MailException e) {
            LOG.error("{} {} NPS Files Email NOT sent! Please check logs", ErrorLogCode.EMAIL_ERROR, ErrorSource.HYBRIS, e);
            return false;
        }
    }

    private void zipDataAndAddAsAttachment(File fileData, MimeMessageHelper message) throws IOException, MessagingException {
        final byte[] exportData = distZipService.zip(new ByteArrayInputStream(FileUtils.readFileToByteArray(fileData)), fileData.getName());
        String fileName = removeFileType(fileData) + DistConstants.Punctuation.DOT + DistConstants.Export.FORMAT_ZIP;
        message.addAttachment(fileName, new ByteArrayDataSource(exportData, "application/zip"));
    }

    private String removeFileType(File fileData) {
        return fileData.getName().substring(0, fileData.getName().lastIndexOf(DistConstants.Punctuation.DOT));
    }

    private File getLatestFilefromDir(String locationPrefix) {

        String[] blobFileNames = null;
        List<CloudBlob> cloudBlobs = new ArrayList<>();
        File lastModifiedFile = new File(azureHotfolderLocalDirectoryBase + File.separator + locationPrefix);

        try {
            blobFileNames = blobSession.listNames(azureHotfolderRemotePath + File.separator + locationPrefix);

            for (String blobFileName : blobFileNames) {
                cloudBlobs.add(blobSession.get(blobFileName));
            }

            cloudBlobs.sort(Comparator.comparing(o -> o.getProperties().getCreatedTime()));
            Collections.reverse(cloudBlobs);

            if (cloudBlobs.size() > 0) {
                azureFileUploader.download(cloudBlobs.get(0).getName(), azureHotfolderLocalDirectoryBase + File.separator + locationPrefix);
                lastModifiedFile = new File(azureHotfolderLocalDirectoryBase + File.separator + locationPrefix
                                            + StringUtils.substringAfterLast(cloudBlobs.get(0).getName(), File.separator));
                LOG.info("Downloading file '{}' to hot-folder container finished", lastModifiedFile.getName());
            } else {
                LOG.error("No exported files at directory {}", azureHotfolderRemotePath + File.separator + locationPrefix);
            }

        } catch (IOException e) {
            LOG.error("Error occured during downloading an File " + lastModifiedFile.getName(), e);
        }

        return lastModifiedFile;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setBlobSession(AzureBlobSession blobSession) {
        this.blobSession = blobSession;
    }

    public void setAzureHotfolderLocalDirectoryBase(String azureHotfolderLocalDirectoryBase) {
        this.azureHotfolderLocalDirectoryBase = azureHotfolderLocalDirectoryBase;
    }

    public void setAzureHotfolderRemotePath(String azureHotfolderRemotePath) {
        this.azureHotfolderRemotePath = azureHotfolderRemotePath;
    }

    public void setAzureFileUploader(AzureFileUploaderImpl azureFileUploader) {
        this.azureFileUploader = azureFileUploader;
    }
}
