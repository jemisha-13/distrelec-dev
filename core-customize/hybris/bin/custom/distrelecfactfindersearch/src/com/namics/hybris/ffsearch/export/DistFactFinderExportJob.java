/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.export;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.util.Map;

import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchExecutionService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.export.DistCsvTransformationService;
import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchParameterProvider;
import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchQueryCreator;
import com.namics.hybris.ffsearch.exception.DistFactFinderExportServiceException;
import com.namics.hybris.ffsearch.exception.DistFactFinderExportUploadException;
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

/**
 * Builds a product export using {@link DistCsvTransformationService}, saves the result to the CronJob and tries to upload it to the
 * FactFinder instance.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
@SuppressWarnings("deprecation")
public class DistFactFinderExportJob extends AbstractJobPerformable<DistFactFinderExportCronJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(DistFactFinderExportJob.class);

    private ConfigurationService configurationService;
    private CatalogVersionService catalogVersionService;
    private DistFactFinderExportHelper exportHelper;
    private DistCsvTransformationService distCsvTransformationService;
    private FactFinderIndexManagementService factFinderIndexManagementService;
    private DistFlexibleSearchParameterProvider<DistFactFinderExportChannelModel> parameterProvider;
    private DistFlexibleSearchQueryCreator distFlexibleSearchQueryCreator;
    private ModelService modelService;
    private DistFlexibleSearchExecutionService flexibleSearchExecutionService;

    @Override
    public PerformResult perform(final DistFactFinderExportCronJobModel cronJob) {
        init(cronJob);
        CronJobResult cronJobResult = CronJobResult.FAILURE;
        cronJobResult = exportChannel(cronJob);
        return new PerformResult(cronJobResult, CronJobStatus.FINISHED);
    }

    private CronJobResult exportChannel(final DistFactFinderExportCronJobModel cronJob) {
        final DistFactFinderExportChannelModel channel = cronJob.getChannel();
        InputStream exportData = null;
        try {
            cronJob.setFfImportTriggered(Boolean.FALSE);
            cronJob.setSuggestFFImportTriggered(Boolean.FALSE);
            getModelService().save(cronJob);
            final Map<String, Object> parameters = getParameterProvider(cronJob).getParameters(channel);
            final String flexibleSearchQuery = getQueryCreator(cronJob).createQuery();

            LOG.debug("exportChannel: {} with query:{}, parameters:{}", channel.getCode(), flexibleSearchQuery, parameters);

            boolean createExecutionPlan = getConfigurationService().getConfiguration()
                .getBoolean(DistConstants.PropKey.FactFinder.CREATE_EXECUTION_PLAN, false);
            if (createExecutionPlan) {
                ResultSet resultSet = null;
                try {
                    resultSet = getFlexibleSearchExecutionService().executionPlan(flexibleSearchQuery, parameters);

                    while (resultSet.next()) {
                        LOG.info("Explain plan: " + resultSet.getString(1));
                    }
                } finally {
                    getFlexibleSearchExecutionService().closeResultSet(resultSet);
                }
            } else {
                exportData = getDistCsvTransformationService().transform(flexibleSearchQuery, parameters,';');
                getExportHelper().saveExportData(exportData, cronJob);

                boolean scpUpload = getConfigurationService().getConfiguration().getBoolean(DistConstants.PropKey.FactFinder.EXPORT_UPLOAD_VIA_SCP, false);
                getExportHelper().saveExternal(cronJob, false, scpUpload);

                triggerImportIfRequired(cronJob, channel);
            }

            LOG.info("exportChannel {} done", channel.getCode());
        } catch (final DistFactFinderExportServiceException e) {
            final String msg = "Failed creating factfinder export data for channel " + channel.getChannel();
            addToLogs(msg, cronJob, e);
            return CronJobResult.FAILURE;
        } catch (final DistFactFinderExportUploadException e) {
            final String msg = "Failed uploading export data for channel " + channel.getChannel() + "from media.";
            addToLogs(msg, cronJob, e);
            return CronJobResult.FAILURE;
        } catch (final Exception e) {
            final String msg = "Failed exporting factfinder data for channel " + channel.getChannel();
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
        String errorMsg = msg + " Exception: [" + errors.toString() + "]";
        cronJobJalo.addLog(errorMsg,
                EnumerationManager.getInstance().getEnumerationValue(GeneratedCronJobConstants.TC.JOBLOGLEVEL, JobLogLevel.ERROR.getCode()));
        LOG.error(errorMsg, exception);
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
     * Private method to allow overriding the query creator bean on the cron job.
     * 
     * @param cronJob
     *            current cronjob
     * @return the query creator
     */
    private DistFlexibleSearchQueryCreator getQueryCreator(final DistFactFinderExportCronJobModel cronJob) {

        DistFlexibleSearchQueryCreator queryCreator = null;
        final String queryCreatorStrategyBeanName = cronJob.getQueryCreatorStrategyBean();
        if (StringUtils.isNotEmpty(queryCreatorStrategyBeanName)) {
            // try to load the strategy bean from cronjob
            queryCreator = SpringUtil.getBean(queryCreatorStrategyBeanName, DistFlexibleSearchQueryCreator.class);
            LOG.debug("Use queryCreator bean with name {} defined in the cronJob.", queryCreatorStrategyBeanName);
        }

        if (queryCreator == null) {
            // use default
            queryCreator = getDistFlexibleSearchQueryCreator();
        }
        return queryCreator;
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

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Required
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
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

    public DistFlexibleSearchQueryCreator getDistFlexibleSearchQueryCreator() {
        return distFlexibleSearchQueryCreator;
    }

    @Required
    public void setDistFlexibleSearchQueryCreator(final DistFlexibleSearchQueryCreator distFlexibleSearchQueryCreator) {
        this.distFlexibleSearchQueryCreator = distFlexibleSearchQueryCreator;
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Override
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public DistFlexibleSearchExecutionService getFlexibleSearchExecutionService() {
        return flexibleSearchExecutionService;
    }

    @Required
    public void setFlexibleSearchExecutionService(final DistFlexibleSearchExecutionService flexibleSearchExecutionService) {
        this.flexibleSearchExecutionService = flexibleSearchExecutionService;
    }

    // END GENERATED CODE

}
