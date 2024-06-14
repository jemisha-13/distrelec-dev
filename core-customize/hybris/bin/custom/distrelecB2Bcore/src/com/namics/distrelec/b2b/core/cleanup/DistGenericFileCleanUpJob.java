/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.cleanup;

import com.namics.distrelec.b2b.core.model.jobs.DistGenericFileCleanUpCronJobModel;
import de.hybris.platform.cloud.azure.hotfolder.remote.session.AzureBlobSession;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A generic file cleanup job.
 *
 * @author dsivakumaran, Namics AG
 * @since Distrelec 1.0
 *
 */
public class DistGenericFileCleanUpJob extends AbstractJobPerformable<DistGenericFileCleanUpCronJobModel> {
    private static final Logger LOG = Logger.getLogger(DistGenericFileCleanUpJob.class);

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private I18NService i18nService;

    private AzureBlobSession blobSession;

    private String azureHotfolderRemotePath;

    @Override
    public PerformResult perform(final DistGenericFileCleanUpCronJobModel cronJob) {
        final long startTime = System.nanoTime();
        LOG.info("Starting file cleanup Job.");
        try {
            performJob(cronJob);
        } catch (final InterruptedException e) {
            LOG.error("Could not sleep after removing item.", e);
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
        } catch (final IOException e) {
            LOG.error("Exception occurred during Azure Blob Session operation", e);
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
        }
        final long endTime = System.nanoTime();
        LOG.info("Finished cleanup Job in " + (int) ((endTime - startTime) / 1e9) + " seconds.");
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    private void performJob(final DistGenericFileCleanUpCronJobModel cronJob) throws InterruptedException, IOException {
        String[] blobFileNames = null;

        String cleanupLocation = azureHotfolderRemotePath + File.separator + getConfigurationService().getConfiguration().getString(cronJob.getDirectoryKey());
        blobFileNames = blobSession.listNames(cleanupLocation);

        final Pattern pattern = Pattern.compile(cronJob.getFilePattern());

        if (blobFileNames != null && blobFileNames.length > 0) {
            int numberOfDeletedFiles = 0;
            for (final String blobFileName : blobFileNames) {
                if (cronJob.getMaxFilesToDelete() != null && numberOfDeletedFiles >= cronJob.getMaxFilesToDelete()) {
                    break;
                }
                final Matcher matcher = pattern.matcher(blobFileName);
                if (matcher.matches()) {
                    if (matcher.groupCount() <= 0 || cronJob.getMaxDaysToKeep() == null || cronJob.getDateFormat() == null) {
                        blobSession.remove(blobFileName);
                        numberOfDeletedFiles++;
                    } else {
                        final Date nowMinusMaxDaysToKeep = DateUtils.addDays(new Date(), -cronJob.getMaxDaysToKeep());
                        final DateFormat format = new SimpleDateFormat(cronJob.getDateFormat(), getI18nService().getCurrentLocale());
                        Date fileDate = null;
                        for (int c = 0; c < matcher.groupCount(); c++) {
                            final String group = matcher.group(c + 1);
                            try {
                                fileDate = format.parse(group);
                                break;
                            } catch (ParseException e) {
                                LOG.debug("Could not parse given text into a date. Check your configured regex pattern or the configured date format.", e);
                            }
                        }
                        if (fileDate != null && fileDate.before(nowMinusMaxDaysToKeep)) {
                            blobSession.remove(blobFileName);
                            numberOfDeletedFiles++;
                        }
                    }
                    Thread.sleep(cronJob.getSleepBetweenDelete());
                }
            }
            LOG.info("Number of deleted files: " + numberOfDeletedFiles);
        }
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public I18NService getI18nService() {
        return i18nService;
    }

    public void setI18nService(final I18NService i18nService) {
        this.i18nService = i18nService;
    }

    public void setBlobSession(AzureBlobSession blobSession) {
        this.blobSession = blobSession;
    }

    public void setAzureHotfolderRemotePath(String azureHotfolderRemotePath) {
        this.azureHotfolderRemotePath = azureHotfolderRemotePath;
    }
}
