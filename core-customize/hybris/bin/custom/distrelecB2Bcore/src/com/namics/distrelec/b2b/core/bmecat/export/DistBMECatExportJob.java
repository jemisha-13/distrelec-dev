
package com.namics.distrelec.b2b.core.bmecat.export;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import com.namics.distrelec.b2b.core.inout.export.DistCsvTransformationService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.bmecat.export.exception.DistBMECatExportServiceException;
import com.namics.distrelec.b2b.core.bmecat.export.exception.DistBMECatExportUploadException;
import com.namics.distrelec.b2b.core.bmecat.export.model.DistBMECatExportChannelModel;
import com.namics.distrelec.b2b.core.bmecat.export.model.DistBMECatExportCronJobModel;
import com.namics.distrelec.b2b.core.bmecat.export.query.DistBMECatParameterProvider;
import com.namics.distrelec.b2b.core.bmecat.export.query.DistBMECatQueryCreator;
import com.namics.hybris.toolbox.spring.SpringUtil;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cronjob.constants.GeneratedCronJobConstants;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.enums.JobLogLevel;
import de.hybris.platform.cronjob.jalo.CronJob;
import de.hybris.platform.jalo.enumeration.EnumerationManager;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Builds a product export, saves the result to the CronJob and tries to upload it to the
 * BMECat instance.
 * 
 * 
 * @author Abhinay Jadhav, Distrelec
 * @since 10/12/2017
 */

@SuppressWarnings("deprecation")
public class DistBMECatExportJob extends AbstractJobPerformable<DistBMECatExportCronJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(DistBMECatExportJob.class);
    private CatalogVersionService catalogVersionService;
    private DistBMECatExportHelper exportHelper;
    private DistBMECatParameterProvider<DistBMECatExportChannelModel> parameterProvider;
    private DistBMECatQueryCreator distBMECatExportQueryCreator;
    private DistCsvTransformationService distCsvTransformationService;
    private ModelService modelService;

    @Override
    public PerformResult perform(final DistBMECatExportCronJobModel cronJob) {
        init(cronJob);
        CronJobResult cronJobResult = CronJobResult.FAILURE;
        cronJobResult = exportChannel(cronJob);
        return new PerformResult(cronJobResult, CronJobStatus.FINISHED);
    }

    private CronJobResult exportChannel(final DistBMECatExportCronJobModel cronJob) {
        final DistBMECatExportChannelModel channel = cronJob.getChannel();

        final Map<String, Object> parameters = getParameterProvider(cronJob).getParameters(channel);
        final boolean isDefaultLanguageDifferent = !parameters.get(DistBMECatQueryCreator.DEFAULT_LANG_ISOCODE_PARAM).equals(parameters.get(DistBMECatQueryCreator.LANG_ISOCODE_PARAM));
        final String flexibleSearchQuery = getDistBMECatExportQueryCreator().createQuery(isDefaultLanguageDifferent);
        try (InputStream exportData = getDistCsvTransformationService().transform(flexibleSearchQuery, parameters,';')) {

            getModelService().save(cronJob);
            getExportHelper().saveExportData(exportData, cronJob);
            getExportHelper().saveExternal(cronJob, false);

        } catch (final DistBMECatExportServiceException e) {
            final String msg = "Failed creating BMECat export data for channel " + channel.getChannel();
            addToLogs(msg, cronJob, e);
            return CronJobResult.FAILURE;
        } catch (final DistBMECatExportUploadException e) {
            final String msg = "Failed uploading export data for channel " + channel.getChannel() + "from media.";
            addToLogs(msg, cronJob, e);
            return CronJobResult.FAILURE;
        } catch (final Exception e) {
            final String msg = "Failed exporting BMECat data for channel " + channel.getChannel();
            addToLogs(msg, cronJob, e);
            return CronJobResult.FAILURE;
        }
        return CronJobResult.SUCCESS;
    }

    private void init(final DistBMECatExportCronJobModel cronJob) {
        final DistBMECatExportChannelModel channel = cronJob.getChannel();
        // session catalogversion & language
        getCatalogVersionService().setSessionCatalogVersion(channel.getCatalogVersion().getCatalog().getId(), channel.getCatalogVersion().getVersion());
        cronJob.setSessionLanguage(channel.getLanguage());
    }

    /**
     * Use CronJobs logging functionality, although it has deprecated methods.
     */

    @SuppressWarnings("deprecation")
    private void addToLogs(final String msg, final DistBMECatExportCronJobModel cronJob, final Exception exception) {
        final CronJob cronJobJalo = modelService.getSource(cronJob);
        final StringWriter errors = new StringWriter();
        exception.printStackTrace(new PrintWriter(errors));
        cronJobJalo.addLog(msg + " Exception: [" + errors.toString() + "]. ",
                EnumerationManager.getInstance().getEnumerationValue(GeneratedCronJobConstants.TC.JOBLOGLEVEL, JobLogLevel.ERROR.getCode()));
        LOG.error(msg, exception);
    }

    /**
     * Private method to allow overriding the parameter provider bean on the cron job.
     * 
     * @param cronJob
     *            current cronjob
     * @return the parameter provider
     */

    private DistBMECatParameterProvider getParameterProvider(final DistBMECatExportCronJobModel cronJob) {

        DistBMECatParameterProvider provider = null;
        final String parameterProviderStrategyBeanName = cronJob.getParameterProvideStrategyBean();
        if (StringUtils.isNotEmpty(parameterProviderStrategyBeanName)) { // try to load the strategy bean from cronjob
            provider = SpringUtil.getBean(parameterProviderStrategyBeanName, DistBMECatParameterProvider.class);
            LOG.debug("Use parameterProvider bean with name {} defined in the cronJob.", parameterProviderStrategyBeanName);
        }

        if (provider == null) {
            // use default
            provider = getParameterProvider();
        }

        return provider;
    }

    // BEGIN GENERATED CODE

    public CatalogVersionService getCatalogVersionService() {
        return catalogVersionService;
    }

    @Required
    public void setCatalogVersionService(final CatalogVersionService catalogVersionService) {
        this.catalogVersionService = catalogVersionService;
    }

    public DistBMECatExportHelper getExportHelper() {
        return exportHelper;
    }

    @Required
    public void setExportHelper(final DistBMECatExportHelper exportHelper) {
        this.exportHelper = exportHelper;
    }

    public DistBMECatParameterProvider<DistBMECatExportChannelModel> getParameterProvider() {
        return parameterProvider;
    }

    public void setParameterProvider(DistBMECatParameterProvider<DistBMECatExportChannelModel> parameterProvider) {
        this.parameterProvider = parameterProvider;
    }

    public DistBMECatQueryCreator getDistBMECatExportQueryCreator() {
        return distBMECatExportQueryCreator;
    }

    public void setDistBMECatExportQueryCreator(DistBMECatQueryCreator distBMECatExportQueryCreator) {
        this.distBMECatExportQueryCreator = distBMECatExportQueryCreator;
    }

    public DistCsvTransformationService getDistCsvTransformationService() {
        return distCsvTransformationService;
    }

    @Required
    public void setDistCsvTransformationService(final DistCsvTransformationService distCsvTransformationService) {
        this.distCsvTransformationService = distCsvTransformationService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Override
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    // END GENERATED CODE

}
