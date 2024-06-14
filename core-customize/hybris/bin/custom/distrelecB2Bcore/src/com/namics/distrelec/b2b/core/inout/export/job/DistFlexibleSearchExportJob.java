/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.export.job;

import com.namics.distrelec.b2b.core.inout.export.*;
import com.namics.distrelec.b2b.core.inout.export.exception.DistFlexibleSearchExecutionException;
import com.namics.distrelec.b2b.core.inout.export.job.exception.DistFlexibleSearchExportJobException;
import com.namics.distrelec.b2b.core.model.inout.DistFlexibleSearchExportCronJobModel;
import com.namics.distrelec.b2b.core.model.inout.DistFlexibleSearchParameterModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.media.MediaService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ParseException;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Job which executes a FlexibleSearch statement and exports the ResultSet to a media.
 *
 * @author ceberle, Namics AG
 * @since Namics Distrelec 1.1
 */
public class DistFlexibleSearchExportJob extends AbstractJobPerformable<DistFlexibleSearchExportCronJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(DistFlexibleSearchExportJob.class);

    private static final String CSV_FILE_SUFFIX = ".csv";
    private static final String ZIP_FILE_SUFFIX = ".zip";

    private DistFlexibleSearchQueryCreator distFlexibleSearchQueryCreator;
    private DistFlexibleSearchParameterProvider<DistFlexibleSearchExportCronJobModel> distFlexibleSearchParameterProvider;

    private DistCsvTransformationService distCsvTransformationService;
    private KeyGenerator mediaCodeKeyGenerator;
    private MediaService mediaService;
    private DistZipService distZipService;
    private DistExportParameterExpressionResolver exportParameterExpressionResolver;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public PerformResult perform(final DistFlexibleSearchExportCronJobModel cronJob) {
        InputStream data = null;
        try {
            final String flexibleSearchQuery = getFlexibleSearchQuery(cronJob);
            final Map<String, Object> flexibleSearchParameters = getFlexibleSearchParameters(cronJob);

            data = getDistCsvTransformationService().transform(flexibleSearchQuery, flexibleSearchParameters,';');

            createMedia(data, cronJob);

            copyExternalIfRequired(cronJob);

            return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        } catch (final DistFlexibleSearchExportJobException e) {
            LOG.error("Export job finished with error", e);
        } catch (final DistFlexibleSearchExecutionException e) {
            LOG.error("Export job finished with error", e);
        } finally {
            IOUtils.closeQuietly(data);
        }

        return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
    }

    protected String getFlexibleSearchQuery(final DistFlexibleSearchExportCronJobModel cronJob) {
        if (StringUtils.isNotBlank(cronJob.getFlexibleSearchQuery())) {
            return cronJob.getFlexibleSearchQuery();
        } else {
            if (getDistFlexibleSearchQueryCreator() != null) {
                return getDistFlexibleSearchQueryCreator().createQuery();
            } else {
                throw new DistFlexibleSearchExportJobException(
                        "Neither a FlexibleSeach-String is set nor a FlexibleSearchCreator Bean is configured (CronJob [" + cronJob.getCode() + "])");
            }
        }
    }

    private Map<String, Object> getFlexibleSearchParameters(final DistFlexibleSearchExportCronJobModel cronJob) {
        final Map<String, Object> parameters = new HashMap<String, Object>();

        if (CollectionUtils.isNotEmpty(cronJob.getFlexibleSearchParameters())) {
            for (final DistFlexibleSearchParameterModel parameter : cronJob.getFlexibleSearchParameters()) {
                if (!parameter.isDynamic()) {
                    parameters.put(parameter.getName(), Long.valueOf(parameter.getValue().getPk().getLongValue()));
                } else {
                    try {
                        Object value = exportParameterExpressionResolver.resolve(parameter.getExpression());
                        if (value instanceof ItemModel) {
                            ItemModel item = (ItemModel) value;
                            parameters.put(parameter.getName(), Long.valueOf(item.getPk().getLongValue()));
                        } else {
                            parameters.put(parameter.getName(), value);
                        }
                    } catch (ParseException | EvaluationException e) {
                        throw new DistFlexibleSearchExecutionException("Error occured while resolving parameter " + parameter.getName(), e);
                    }
                }
            }
        }

        if (getDistFlexibleSearchParameterProvider() != null) {
            final Map<String, Object> providedParameters = getDistFlexibleSearchParameterProvider().getParameters(cronJob);
            if (MapUtils.isNotEmpty(providedParameters)) {
                parameters.putAll(providedParameters);
            }
        }

        return parameters;
    }

    protected void createMedia(final InputStream data, final DistFlexibleSearchExportCronJobModel cronJob) {
        final MediaModel media = modelService.create(MediaModel.class);
        media.setCode(getMediaCode(cronJob));
        media.setCatalogVersion(cronJob.getMediaCatalogVersion());
        modelService.save(media);

        if (Boolean.TRUE.equals(cronJob.getZipOutput())) {
            final String zipArchiveEntryName = cronJob.getMediaPrefix() + CSV_FILE_SUFFIX;
            mediaService.setDataForMedia(media, getDistZipService().zip(data, zipArchiveEntryName));

        } else {
            mediaService.setStreamForMedia(media, data);
        }

        cronJob.setMedia(media);
        modelService.save(cronJob);
    }

    protected String getMediaCode(final DistFlexibleSearchExportCronJobModel cronJob) {
        final StringBuilder code = new StringBuilder();
        code.append(cronJob.getMediaPrefix());
        code.append("_");
        code.append(getMediaCodeKeyGenerator().generate().toString());
        return code.toString();
    }

    private void copyExternalIfRequired(final DistFlexibleSearchExportCronJobModel cronJob) {
        String outputPath = calculateOutputPath(cronJob);
        if (StringUtils.isNotBlank(outputPath)) {
            if (cronJob.getMedia() == null) {
                throw new DistFlexibleSearchExportJobException("No attached media found");
            }

            try {
                final StringBuilder fileName = new StringBuilder(cronJob.getMediaPrefix());
                if (Boolean.TRUE.equals(cronJob.getZipOutput())) {
                    fileName.append(ZIP_FILE_SUFFIX);
                } else {
                    fileName.append(CSV_FILE_SUFFIX);
                }
                final File file = new File(outputPath, fileName.toString());
                FileUtils.copyInputStreamToFile(getMediaService().getStreamFromMedia(cronJob.getMedia()), file);
            } catch (final IOException e) {
                throw new DistFlexibleSearchExportJobException("Could not write data to external directory (CronJob [" + cronJob.getCode() + "])", e);
            }
        }
    }

    private String calculateOutputPath(final DistFlexibleSearchExportCronJobModel cronJob) {
        PropertyPlaceholderHelper placeholderHelper = new PropertyPlaceholderHelper("${", "}");
        return placeholderHelper.replacePlaceholders(cronJob.getOutputPath(), (placeholderKey) -> {
            Configuration config = configurationService.getConfiguration();
            return config.getString(placeholderKey);
        });
    }

    // BEGIN GENERATED CODE

    public DistFlexibleSearchQueryCreator getDistFlexibleSearchQueryCreator() {
        return distFlexibleSearchQueryCreator;
    }

    public void setDistFlexibleSearchQueryCreator(final DistFlexibleSearchQueryCreator distFlexibleSearchQueryCreator) {
        this.distFlexibleSearchQueryCreator = distFlexibleSearchQueryCreator;
    }

    public DistFlexibleSearchParameterProvider<DistFlexibleSearchExportCronJobModel> getDistFlexibleSearchParameterProvider() {
        return distFlexibleSearchParameterProvider;
    }

    public void setDistFlexibleSearchParameterProvider(
            final DistFlexibleSearchParameterProvider<DistFlexibleSearchExportCronJobModel> distFlexibleSearchParameterProvider) {
        this.distFlexibleSearchParameterProvider = distFlexibleSearchParameterProvider;
    }

    public DistCsvTransformationService getDistCsvTransformationService() {
        return distCsvTransformationService;
    }

    @Autowired
    public void setDistCsvTransformationService(final DistCsvTransformationService distCsvTransformationService) {
        this.distCsvTransformationService = distCsvTransformationService;
    }

    public KeyGenerator getMediaCodeKeyGenerator() {
        return mediaCodeKeyGenerator;
    }

    @Autowired
    @Qualifier("core.exportMediaCodeKeyGenerator")
    public void setMediaCodeKeyGenerator(final KeyGenerator mediaCodeKeyGenerator) {
        this.mediaCodeKeyGenerator = mediaCodeKeyGenerator;
    }

    public MediaService getMediaService() {
        return mediaService;
    }

    @Autowired
    public void setMediaService(final MediaService mediaService) {
        this.mediaService = mediaService;
    }

    public DistZipService getDistZipService() {
        return distZipService;
    }

    @Autowired
    public void setDistZipService(final DistZipService distZipService) {
        this.distZipService = distZipService;
    }

    public DistExportParameterExpressionResolver getExportParameterExpressionResolver() {
        return exportParameterExpressionResolver;
    }

    @Autowired
    public void setExportParameterExpressionResolver(DistExportParameterExpressionResolver exportParameterExpressionResolver) {
        this.exportParameterExpressionResolver = exportParameterExpressionResolver;
    }

    // END GENERATED CODE

}
