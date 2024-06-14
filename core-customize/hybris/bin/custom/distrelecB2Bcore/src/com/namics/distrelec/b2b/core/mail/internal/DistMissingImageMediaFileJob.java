package com.namics.distrelec.b2b.core.mail.internal;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchExecutionService;
import com.namics.distrelec.b2b.core.mail.internal.data.DistMediaLocationData;
import com.namics.distrelec.b2b.core.mail.internal.data.MissingImageMediaFileData;
import com.namics.distrelec.b2b.core.media.storage.DistMediaStorageStrategy;
import com.namics.distrelec.b2b.core.media.storage.impl.DistStoredMediaData;
import com.namics.distrelec.b2b.core.model.jobs.DistMissingImageMediaFileCronJobModel;
import com.opencsv.CSVWriter;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.media.exceptions.MediaStoreException;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.media.storage.MediaStorageConfigService.MediaFolderConfig;
import de.hybris.platform.media.storage.MediaStorageRegistry;
import de.hybris.platform.media.storage.impl.StoredMediaData;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DistMissingImageMediaFileJob extends AbstractDistInternalMailJob<DistMissingImageMediaFileCronJobModel, String, String> {

    private static final Logger LOG = LogManager.getLogger(DistMissingImageMediaFileJob.class);

    private static final String[] HEADERS = { "Internal URL" };

    private static final int MAX_NUMBER_OF_ROWS = 1048575;

    private final AtomicInteger counter = new AtomicInteger();

    @Autowired
    private MediaStorageConfigService mediaStorageConfigService;

    @Autowired
    private MediaStorageRegistry mediaStorageRegistry;

    @Autowired
    private DistFlexibleSearchExecutionService flexibleSearchExecutionService;

    @Autowired
    private Converter<String, MissingImageMediaFileData> distMissingImageMediaFileConverter;

    @Override
    public PerformResult perform(DistMissingImageMediaFileCronJobModel cronJob) {
        setCronjob(cronJob);
        DistMediaLocationData data;
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        try {
            List<String> results = getInternalUrls();
            LOG.info("Found {} results for query", results.size());
            if (results.isEmpty()) {
                return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
            }
            counter.set(results.size());
            executor.scheduleAtFixedRate(new LoggingTask(Instant.now(), results.size()), 30L, 30L, TimeUnit.SECONDS);
            data = processResults(results);
        } catch (SQLException e) {
            LOG.error("Unable to get internal URL list", e);
            return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
        } catch (MediaStoreException ex) {
            LOG.error("Error occurred while listing files in folder", ex);
            return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
        } finally {
            executor.shutdown();
        }

        if (data == null) {
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
        }

        boolean blobNotFound = CollectionUtils.isNotEmpty(data.getNonExistingBlobs());
        LOG.info("Size of data to send email = {}", data.getMissingMedias().size());
        boolean result = send(createContext(data.getMissingMedias()));
        if (!result) {
            return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
        } else if (blobNotFound) {
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
        } else {
            return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        }
    }

    private List<String> getInternalUrls() throws SQLException {
        FlexibleSearchQuery flexibleSearchQuery = getQuery();
        if (flexibleSearchQuery != null && flexibleSearchQuery.getQuery() != null) {
            List<String> internalUrls = new ArrayList<>();
            ResultSet resultSet = null;
            Map<String, Object> flexSearchParams = new HashMap<>();
            try {
                resultSet = getFlexibleSearchExecutionService().execute(flexibleSearchQuery.getQuery(), flexSearchParams);
                while (resultSet.next()) {
                    String internalUrl = resultSet.getString(1);
                    internalUrls.add(internalUrl);
                }
            } finally {
                getFlexibleSearchExecutionService().closeResultSet(resultSet);
            }
            Collections.sort(internalUrls);
            return internalUrls;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    protected FlexibleSearchQuery getQuery() {
        return new FlexibleSearchQuery(getCronjob().getSqlQuery());
    }

    @Override
    protected String convert(String source) {
        return null;
    }

    private DistMediaLocationData processResults(List<String> results) {
        DistMediaLocationData mediaLocationData = new DistMediaLocationData();
        List<String> missingMedias = new ArrayList<>();
        List<String> nonExistingBlobs = new ArrayList<>();
        String lastLocation = null;
        List<String> downloadedFiles = Collections.emptyList();
        List<String> notDownloadedFiles = Collections.emptyList();

        int maxImagesToImport = getCronjob().getMaxImagesToImport() != null ? getCronjob().getMaxImagesToImport() : MAX_NUMBER_OF_ROWS;

        for (String internalUrl : results) {
            if (maxImagesToImport > 0 && missingMedias.size() >= maxImagesToImport) {
                LOG.info("Limit of {} images to import reached!", maxImagesToImport);
                break;
            }

            counter.getAndDecrement();
            if (clearAbortRequestedIfNeeded(getCronjob()))
            {
                return null;
            }

            MissingImageMediaFileData mediaFileData = new MissingImageMediaFileData();
            try {
                getDistMissingImageMediaFileConverter().convert(internalUrl, mediaFileData);
            } catch (IllegalArgumentException ex) {
                LOG.error("Error occurred while processing URL: {}", internalUrl);
                continue;
            }
            String mediaLocation = mediaFileData.getMediaLocation();

            if (!mediaLocation.equals(lastLocation)) {
                lastLocation = mediaLocation;
                MediaFolderConfig config = getPimMediaFolderConfig();
                DistMediaStorageStrategy mediaStorageStrategy = getMediaStorageStrategy(config);
                List<DistStoredMediaData> storedMedias = mediaStorageStrategy.listFilesInFolder(config, mediaLocation);

                downloadedFiles = storedMedias.stream()
                                              .filter(media -> media.isDownloaded())
                                              .map(StoredMediaData::getLocation)
                                              .collect(Collectors.toList());

                notDownloadedFiles = storedMedias.stream()
                                                 .filter(media -> !media.isDownloaded())
                                                 .map(StoredMediaData::getLocation)
                                                 .collect(Collectors.toList());
            }

            String mediaName = mediaFileData.getMediaName();
            if (notDownloadedFiles.contains(mediaName)) {
                nonExistingBlobs.add(internalUrl);
            } else if (!downloadedFiles.contains(mediaName)) {
                missingMedias.add(internalUrl);
            }
        }

        mediaLocationData.setMissingMedias(missingMedias);
        mediaLocationData.setNonExistingBlobs(nonExistingBlobs);
        return mediaLocationData;
    }

    private MediaFolderConfig getPimMediaFolderConfig() {
        return getMediaStorageConfigService().getConfigForFolder(DistConstants.AzureMediaFolder.PIMMEDIAS);
    }

    private DistMediaStorageStrategy getMediaStorageStrategy(MediaFolderConfig config) {
        return (DistMediaStorageStrategy) getMediaStorageRegistry().getStorageStrategyForFolder(config);
    }

    @Override
    public boolean isAbortable() {
        return true;
    }

    @Override
    protected DistInternalMailContext<String> createContext(List<String> data) {
        final DistInternalMailContext<String> context = new DistInternalMailContext<>(data);
        final String emailSubject = getEmailSubject().replace("{date}", getCurrentDate());
        context.setEmailSubject(emailSubject);
        return context;
    }

    private String getCurrentDate() {
        LocalDate today = LocalDate.now();
        return today.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    @Override
    protected String getOverriddenToEmailAddress() {
        return getConfigurationService().getConfiguration().getString(DistConstants.PropKey.Email.MISSING_IMAGE_MEDIA_FILE_NOTIFICATION_EMAIL);
    }

    @Override
    protected String getEmailSubject() {
        return super.getEmailSubject() != null ? super.getEmailSubject() : "Missing Image Media File Report: {date}";
    }

    @Override
    protected void addAttachment(MimeMessageHelper message, DistInternalMailContext<String> context) {
        File report = createMissingImageMediaFileReport(context.getData());
        if (report != null && report.exists()) {
            try {
                message.addAttachment(getCurrentDate() + ".csv", report);
                report.deleteOnExit();
            } catch (Exception ex) {
                LOG.error("Error while attaching missing image media file report", ex);
            }
        }
    }

    private File createMissingImageMediaFileReport(List<String> internalUrls) {
        if (CollectionUtils.isEmpty(internalUrls)) {
            LOG.info("No image media files are missing.");
            return null;
        }
        try {
            File tempFile = File.createTempFile("MissingImageMediaFiles", ".csv");
            tempFile.deleteOnExit();
            try (FileOutputStream outputStream = new FileOutputStream(tempFile);
                 OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
                 CSVWriter csvWriter = new CSVWriter(streamWriter)) {
                csvWriter.writeNext(HEADERS);
                for (String internalUrl : internalUrls) {
                    csvWriter.writeNext(new String[] { prepareContent(internalUrl) });
                    csvWriter.flush();
                }
            }
            return tempFile;
        } catch (IOException ex) {
            LOG.error("Could not create missing image media file report", ex);
            return null;
        }
    }

    private String prepareContent(String value) {
        return StringUtils.isEmpty(value) ? StringUtils.EMPTY : value;
    }

    public MediaStorageConfigService getMediaStorageConfigService() {
        return mediaStorageConfigService;
    }

    public void setMediaStorageConfigService(final MediaStorageConfigService mediaStorageConfigService) {
        this.mediaStorageConfigService = mediaStorageConfigService;
    }

    public MediaStorageRegistry getMediaStorageRegistry() {
        return mediaStorageRegistry;
    }

    public void setMediaStorageRegistry(final MediaStorageRegistry mediaStorageRegistry) {
        this.mediaStorageRegistry = mediaStorageRegistry;
    }

    public DistFlexibleSearchExecutionService getFlexibleSearchExecutionService() {
        return flexibleSearchExecutionService;
    }

    public void setFlexibleSearchExecutionService(final DistFlexibleSearchExecutionService flexibleSearchExecutionService) {
        this.flexibleSearchExecutionService = flexibleSearchExecutionService;
    }

    public Converter<String, MissingImageMediaFileData> getDistMissingImageMediaFileConverter() {
        return distMissingImageMediaFileConverter;
    }

    public void setDistMissingImageMediaFileConverter(final Converter<String, MissingImageMediaFileData> distMissingImageMediaFileConverter) {
        this.distMissingImageMediaFileConverter = distMissingImageMediaFileConverter;
    }

    private class LoggingTask extends TimerTask {
        private Instant start;
        private int totalCount;

        public LoggingTask(Instant start, int totalCount) {
            this.start = start;
            this.totalCount = totalCount;
        }

        @Override
        public void run() {
            Duration elapsedDuration = Duration.between(start, Instant.now());
            int remainingCount = counter.get();
            double processedPercentage = (double) (totalCount - remainingCount) / totalCount;
            Duration estimatedTotalDuration = Duration.of((long) ((double) elapsedDuration.toMillis() / processedPercentage), ChronoUnit.MILLIS);
            Duration remainingTime = estimatedTotalDuration.minusMillis(elapsedDuration.toMillis());

            LOG.info("Processed {}%,\telapsed: {},\tremaining: {},\testimated total: {},\tprocessed rows: {},\tremaining rows: {}",
              String.format("%.2f", processedPercentage * 100.0),
              DurationFormatUtils.formatDurationHMS(elapsedDuration.toMillis()),
              DurationFormatUtils.formatDurationHMS(remainingTime.toMillis()),
              DurationFormatUtils.formatDurationHMS(estimatedTotalDuration.toMillis()),
              totalCount - remainingCount,
              remainingCount);
        }
    }
}
