/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.rma;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.inout.erp.impl.SapRMAService;
import com.namics.distrelec.b2b.core.model.jobs.SapReturnRequestExportCronJobModel;
import com.namics.distrelec.b2b.core.model.rma.SapReturnRequestModel;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Exports SAP return request in to CSV file.
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class SapReturnRequestExportJob extends AbstractJobPerformable<SapReturnRequestExportCronJobModel> {

    private final static Logger LOG = Logger.getLogger(SapReturnRequestExportJob.class.getName());

    private String exportFileNamePrefix = "sap-rma-export-";

    private ModelService modelService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private I18NService i18nService;

    @Autowired
    private SapRMAService sapRMAService;

    @Autowired
    private Converter<SapReturnRequestModel, ReturnRequestData> sapReturnRequestConverter;

    @Override
    public PerformResult perform(final SapReturnRequestExportCronJobModel cronJob) {
        final Date startTime = new Date();
        LOG.info("Starting Return requests export CronJob at " + startTime);
        FileWriter writer = null;
        try {
            final Date lastExportDate = cronJob.getLastExportDate();
            final List<Object> rmaRequests = getSapRMAService().getReturnRequestsFromDate(lastExportDate);
            if (!rmaRequests.isEmpty()) {
                final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", getI18nService().getCurrentLocale());
                final String exportPath = getConfigurationService().getConfiguration().getString("erp.rma.export.directory");

                final File exportDir = new File(exportPath);
                if (!(exportDir.exists() || exportDir.mkdir())) {
                    throw new IOException("Unable to create the RMA export folder [Path: " + exportPath + "]");
                }

                LOG.info("Export RMA requests, last export : " + (lastExportDate == null ? "No export has been done yet" : lastExportDate));
                final String fileName = exportPath.endsWith(File.separator) ? exportPath + getExportFileNamePrefix() + dateFormat.format(new Date()) + ".csv"
                        : exportPath + File.separator + getExportFileNamePrefix() + dateFormat.format(new Date()) + ".csv";
                LOG.info("Export RMA requests destination file : " + fileName);
                writer = new FileWriter(fileName);

                for (final Object obj : rmaRequests) {
                    writer.write(getSapReturnRequestConverter().convert((SapReturnRequestModel) obj).toCSV(null));
                    writer.flush();
                }
            }
            // If everything is OK, then set the last export date to the current date
            cronJob.setLastExportDate(startTime);
            getModelService().save(cronJob);

            return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        } catch (final Exception e) {
            LOG.error("Can not export return request!", e);
            return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
        } finally {
            LOG.info("End of " + cronJob.getCode() + " at " + new Date());
            if (writer != null) {
                try {
                    writer.close();
                } catch (final IOException e) {
                    LOG.error("Can not close writer.", e);
                }
            }
        }
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Override
    @Required
    public void setModelService(final ModelService modelService) {
        super.setModelService(modelService);
        this.modelService = modelService;
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

    public String getExportFileNamePrefix() {
        return exportFileNamePrefix;
    }

    public void setExportFileNamePrefix(final String exportFileNamePrefix) {
        this.exportFileNamePrefix = exportFileNamePrefix;
    }

    public SapRMAService getSapRMAService() {
        return sapRMAService;
    }

    public void setSapRMAService(SapRMAService sapRMAService) {
        this.sapRMAService = sapRMAService;
    }

    public Converter<SapReturnRequestModel, ReturnRequestData> getSapReturnRequestConverter() {
        return sapReturnRequestConverter;
    }

    public void setSapReturnRequestConverter(Converter<SapReturnRequestModel, ReturnRequestData> sapReturnRequestConverter) {
        this.sapReturnRequestConverter = sapReturnRequestConverter;
    }

}
