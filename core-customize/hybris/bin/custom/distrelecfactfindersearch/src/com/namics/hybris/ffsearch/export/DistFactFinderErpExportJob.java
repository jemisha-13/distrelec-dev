package com.namics.hybris.ffsearch.export;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.export.DistCsvTransformationService;
import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchParameterProvider;
import com.namics.hybris.ffsearch.exception.DistFactFinderExportServiceException;
import com.namics.hybris.ffsearch.exception.DistFactFinderExportUploadException;
import com.namics.hybris.ffsearch.export.query.DistFactFinderErpExportQueryCreator;
import com.namics.hybris.ffsearch.model.DistFactFinderExportChannelModel;
import com.namics.hybris.ffsearch.model.export.DistFactFinderExportCronJobModel;
import com.namics.hybris.ffsearch.service.FactFinderIndexManagementService;
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
import de.hybris.platform.util.Config;

public class DistFactFinderErpExportJob extends AbstractJobPerformable<DistFactFinderExportCronJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(DistFactFinderErpExportJob.class);

    private CatalogVersionService catalogVersionService;
    private DistFactFinderExportHelper exportHelper;
    private DistCsvTransformationService distCsvTransformationService;
    private FactFinderIndexManagementService factFinderIndexManagementService;
    private DistFlexibleSearchParameterProvider<DistFactFinderExportChannelModel> parameterProvider;
    private ModelService modelService;
    private DistFactFinderErpExportQueryCreator queryCreator;

    @Override
    public PerformResult perform(DistFactFinderExportCronJobModel cronJob) {
        init(cronJob);
        CronJobResult cronJobResult = CronJobResult.FAILURE;
        cronJobResult = exportPricesForCountry(cronJob);
        return new PerformResult(cronJobResult, CronJobStatus.FINISHED);
    }

    private CronJobResult exportPricesForCountry(final DistFactFinderExportCronJobModel cronJob) {
        final DistFactFinderExportChannelModel channel = cronJob.getChannel();
        InputStream exportData = null;
        try {
            cronJob.setFfImportTriggered(Boolean.FALSE);
            cronJob.setSuggestFFImportTriggered(Boolean.FALSE);
            getModelService().save(cronJob);
            final Map<String, Object> parameters = getParameterProvider(cronJob).getParameters(channel);
            // parameters.putAll(getParameterProvider(cronJob).getParameters(channel));

            exportData = getDistCsvTransformationService().transform(queryCreator.createQuery(), parameters,';');
            getExportHelper().saveExportData(exportData, cronJob);
            getExportHelper().saveExternal(cronJob, false, false);

            // triggerImportIfRequired(cronJob, channel);
        } catch (final DistFactFinderExportServiceException e) {
            final String msg = "Failed creating factfinder ERP export data for channel " + channel.getChannel();
            addToLogs(msg, cronJob, e);
            return CronJobResult.FAILURE;
        } catch (final DistFactFinderExportUploadException e) {
            final String msg = "Failed uploading ERP export data for channel " + channel.getChannel() + "from media.";
            addToLogs(msg, cronJob, e);
            return CronJobResult.FAILURE;
        } catch (final Exception e) {
            final String msg = "Failed exporting ERP factfinder data for channel " + channel.getChannel();
            addToLogs(msg, cronJob, e);
            return CronJobResult.FAILURE;
        } finally {
            IOUtils.closeQuietly(exportData);
        }
        return CronJobResult.SUCCESS;
    }

    private void init(final DistFactFinderExportCronJobModel cronJob) {
        final DistFactFinderExportChannelModel channel = cronJob.getChannel();
        // session catalogversion & language
        getCatalogVersionService().setSessionCatalogVersion(channel.getCatalogVersion().getCatalog().getId(), channel.getCatalogVersion().getVersion());
        cronJob.setSessionLanguage(channel.getLanguage());
    }

    /**
     * Use CronJobs logging functionality, although it has deprecated methods.
     */
    @SuppressWarnings("deprecation")
    private void addToLogs(final String msg, final DistFactFinderExportCronJobModel cronJob, final Exception exception) {
        final CronJob cronJobJalo = modelService.getSource(cronJob);
        final StringWriter errors = new StringWriter();
        exception.printStackTrace(new PrintWriter(errors));
        cronJobJalo.addLog(msg + " Exception: [" + errors.toString() + "]. ",
                EnumerationManager.getInstance().getEnumerationValue(GeneratedCronJobConstants.TC.JOBLOGLEVEL, JobLogLevel.ERROR.getCode()));
        LOG.error(msg, exception);
    }

    private void triggerImportIfRequired(final DistFactFinderExportCronJobModel cronJob, final DistFactFinderExportChannelModel channel) throws Exception {
        final boolean triggerImport = Config.getBoolean(DistConstants.PropKey.FactFinder.TRIGGER_IMPORT, true);
        if (triggerImport) {
            getFactFinderIndexManagementService().startImport(cronJob, channel);
        } else {
            cronJob.setFfImportTriggered(Boolean.FALSE);
            cronJob.setSuggestFFImportTriggered(Boolean.FALSE);
            getModelService().save(cronJob);
            LOG.debug("Won't trigger FactFinder import");
        }
    }

    /**
     * Private method to allow overriding the parameter provider bean on the cron job.
     * 
     * @param cronJob
     *            current cronjob
     * @return the parameter provider
     */
    private DistFlexibleSearchParameterProvider getParameterProvider(final DistFactFinderExportCronJobModel cronJob) {

        DistFlexibleSearchParameterProvider provider = null;
        final String parameterProviderStrategyBeanName = cronJob.getParameterProvideStrategyBean();
        if (StringUtils.isNotEmpty(parameterProviderStrategyBeanName)) {
            // try to load the strategy bean from cronjob
            provider = SpringUtil.getBean(parameterProviderStrategyBeanName, DistFlexibleSearchParameterProvider.class);
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

    public DistFactFinderExportHelper getExportHelper() {
        return exportHelper;
    }

    @Required
    public void setExportHelper(final DistFactFinderExportHelper exportHelper) {
        this.exportHelper = exportHelper;
    }

    public DistCsvTransformationService getDistCsvTransformationService() {
        return distCsvTransformationService;
    }

    @Required
    public void setDistCsvTransformationService(final DistCsvTransformationService distCsvTransformationService) {
        this.distCsvTransformationService = distCsvTransformationService;
    }

    public FactFinderIndexManagementService getFactFinderIndexManagementService() {
        return factFinderIndexManagementService;
    }

    @Required
    public void setFactFinderIndexManagementService(final FactFinderIndexManagementService factFinderIndexManagementService) {
        this.factFinderIndexManagementService = factFinderIndexManagementService;
    }

    public DistFlexibleSearchParameterProvider<DistFactFinderExportChannelModel> getParameterProvider() {
        return parameterProvider;
    }

    @Required
    public void setParameterProvider(final DistFlexibleSearchParameterProvider<DistFactFinderExportChannelModel> parameterProvider) {
        this.parameterProvider = parameterProvider;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public DistFactFinderErpExportQueryCreator getQueryCreator() {
        return queryCreator;
    }

    public void setQueryCreator(DistFactFinderErpExportQueryCreator queryCreator) {
        this.queryCreator = queryCreator;
    }

    // END GENERATED CODE

}
