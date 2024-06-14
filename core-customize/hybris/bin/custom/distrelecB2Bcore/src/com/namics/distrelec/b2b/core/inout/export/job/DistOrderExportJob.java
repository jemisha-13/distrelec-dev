package com.namics.distrelec.b2b.core.inout.export.job;

import com.namics.distrelec.b2b.core.inout.export.DistCsvTransformationService;
import com.namics.distrelec.b2b.core.inout.export.DistExportParameterExpressionResolver;
import com.namics.distrelec.b2b.core.inout.export.DistOrderExportService;
import com.namics.distrelec.b2b.core.model.inout.DistFlexibleSearchParameterModel;
import com.namics.distrelec.b2b.core.model.inout.DistOrderExportCronJobModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.media.MediaService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

public class DistOrderExportJob extends AbstractJobPerformable<DistOrderExportCronJobModel> {

    private final Logger LOG = LoggerFactory.getLogger(DistOrderExportJob.class);

    private DistCsvTransformationService distCsvTransformationService;
    private DistExportParameterExpressionResolver exportParameterExpressionResolver;
    private DistOrderExportService distOrderExportService;
    private MediaService mediaService;

    @Override
    public PerformResult perform(final DistOrderExportCronJobModel cronJob) {
        return new PerformResult(performJob(cronJob) ? CronJobResult.SUCCESS : CronJobResult.FAILURE, CronJobStatus.FINISHED);
    }

    private boolean performJob(final DistOrderExportCronJobModel cronJob){
        final String query = getQuery(cronJob);

        if(StringUtils.isNotEmpty(query)) {
            final Map<String, Object> parameters = getParameters(cronJob);
            MediaModel media = null;
            try (final InputStream data = getDistCsvTransformationService().transform(query, parameters, ';')) {
                media = getDistOrderExportService().createMedia(data, cronJob);
            } catch (IOException e) {
                LOG.error("Unable to create media", e);
                return false;
            }

            try (final InputStream data = getMediaService().getStreamFromMedia(media)) {
                return getDistOrderExportService().saveExternal(data, media.getCode());
            } catch (IOException e) {
                LOG.error("Unable to upload media", e);
            }
        }
        return false;
    }

    private String getQuery(DistOrderExportCronJobModel cronJobModel) {
        return StringUtils.isNotBlank(cronJobModel.getFlexibleSearchQuery()) ? cronJobModel.getFlexibleSearchQuery(): null;
    }

    private Map<String, Object> getParameters(DistOrderExportCronJobModel cronJobModel) {
        return cronJobModel.getFlexibleSearchParameters()
                .stream()
                .filter(DistFlexibleSearchParameterModel::isDynamic)
                .collect(Collectors.toMap(DistFlexibleSearchParameterModel::getName, p -> exportParameterExpressionResolver.resolve(p.getExpression())));
    }

    public DistCsvTransformationService getDistCsvTransformationService() {
        return distCsvTransformationService;
    }

    @Required
    public void setDistCsvTransformationService(DistCsvTransformationService distCsvTransformationService) {
        this.distCsvTransformationService = distCsvTransformationService;
    }

    public DistExportParameterExpressionResolver getExportParameterExpressionResolver() {
        return exportParameterExpressionResolver;
    }

    @Required
    public void setExportParameterExpressionResolver(DistExportParameterExpressionResolver exportParameterExpressionResolver) {
        this.exportParameterExpressionResolver = exportParameterExpressionResolver;
    }

    public DistOrderExportService getDistOrderExportService() {
        return distOrderExportService;
    }

    @Required
    public void setDistOrderExportService(DistOrderExportService distOrderExportService) {
        this.distOrderExportService = distOrderExportService;
    }

    public MediaService getMediaService() {
        return mediaService;
    }

    @Required
    public void setMediaService(MediaService mediaService) {
        this.mediaService = mediaService;
    }
}
