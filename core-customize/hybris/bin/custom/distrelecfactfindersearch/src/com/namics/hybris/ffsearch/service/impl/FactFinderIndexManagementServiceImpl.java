/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.namics.hybris.ffsearch.model.export.DistAbstractFactFinderExportCronJobModel;
import de.factfinder.webservice.ws71.FFimport.String2ArrayOfStringMap;
import de.factfinder.webservice.ws71.FFimport.String2ArrayOfStringMap.Entry;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.namics.hybris.ffsearch.model.DistFactFinderExportChannelModel;
import com.namics.hybris.ffsearch.model.export.DistFactFinderExportCronJobModel;
import com.namics.hybris.ffsearch.service.FactFinderIndexManagementService;

import de.factfinder.webservice.ws71.FFimport.AuthenticationToken;
import de.factfinder.webservice.ws71.FFimport.ImportPortType;
import de.hybris.platform.cronjob.constants.GeneratedCronJobConstants;
import de.hybris.platform.cronjob.enums.JobLogLevel;
import de.hybris.platform.cronjob.jalo.CronJob;
import de.hybris.platform.jalo.enumeration.EnumerationManager;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Default implementation of {@link FactFinderIndexManagementService}.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class FactFinderIndexManagementServiceImpl implements FactFinderIndexManagementService {

    static final String ERRORS_SECTION = "ERRORS";

    /**
     * Returned error message by factfinder start import web services.
     */
    static final String ANOTHER_IMPORT_IS_ALREADY_RUNNING_ERROR_MSG = "Another import is already running.";

    static final Long SLEEP_BETWEEN_CALLS = 30_000L;

    private static final Logger LOG = LoggerFactory.getLogger(FactFinderIndexManagementServiceImpl.class);

    private ImportPortType indexManagementWebserviceClient;
    private FactFinderAuthentication authentication;
    private ModelService modelService;

    @Override
    public synchronized void startImport(final DistAbstractFactFinderExportCronJobModel cronJob, final DistFactFinderExportChannelModel channel) throws Exception {
        final AuthenticationToken auth = getAuthentication().getIndexManagementAuth();

        boolean repeatImportCall = true;
        while (repeatImportCall) {
            repeatImportCall = startImport(cronJob, channel, auth);
            if (repeatImportCall) {
                sleep();
            }
        }

        boolean repeatSuggestImportCall = true;
        while (repeatSuggestImportCall) {
            repeatSuggestImportCall = startSuggestImport(cronJob, channel, auth);
            if (repeatSuggestImportCall) {
                sleep();
            }
        }

        modelService.save(cronJob);
    }

    /**
     * Calls start import web service, and returns true if we should call it once more if it is already running.
     */
    private boolean startImport(final DistAbstractFactFinderExportCronJobModel cronJob, final DistFactFinderExportChannelModel channel,
            final AuthenticationToken auth) throws Exception {
        try {
            LOG.debug("Trigger search index import for channel [{}]", channel.getChannel());
            String2ArrayOfStringMap result = getIndexManagementWebserviceClient().startImport(channel.getChannel(), auth);
            verifyResult(result);
            cronJob.setFfImportTriggered(Boolean.TRUE);
        } catch (final Exception e) {
            if (StringUtils.contains(e.getMessage(), ANOTHER_IMPORT_IS_ALREADY_RUNNING_ERROR_MSG)) {
                // repeat call
                return true;
            }
            cronJob.setFfImportTriggered(Boolean.FALSE);
            throw e;
        }
        return false;
    }

    /**
     * Calls start suggest import web service, and returns true if we should call it once more if it is already running.
     */
    private boolean startSuggestImport(final DistAbstractFactFinderExportCronJobModel cronJob, final DistFactFinderExportChannelModel channel,
            final AuthenticationToken auth) throws Exception {
        try {
            LOG.debug("Trigger suggest import for channel [{}]", channel.getChannel());
            String2ArrayOfStringMap result = getIndexManagementWebserviceClient().startSuggestImport(channel.getChannel(), auth);
            verifyResult(result);
            cronJob.setSuggestFFImportTriggered(Boolean.TRUE);
        } catch (final Exception e) {
            if (StringUtils.contains(e.getMessage(), ANOTHER_IMPORT_IS_ALREADY_RUNNING_ERROR_MSG)) {
                // repeat call
                return true;
            }
            cronJob.setSuggestFFImportTriggered(Boolean.FALSE);
            throw e;
        }
        return false;
    }

    private void verifyResult(String2ArrayOfStringMap result) throws Exception {
        for (Entry entry : result.getEntry()) {
            if (ERRORS_SECTION.equals(entry.getKey()) && !entry.getValue().getString().isEmpty()) {
                String errors = String.join(System.lineSeparator(), entry.getValue().getString());
                throw new Exception(errors);
            }
        }
    }

    /**
     * Use CronJobs logging functionality, although it has deprecated methods.
     */
    @SuppressWarnings("deprecation")
    private void addToLogs(final String msg, final DistFactFinderExportCronJobModel cronJob, final Exception exception) {
        final CronJob cronJobJalo = modelService.getSource(cronJob);
        final StringWriter errors = new StringWriter();
        errors.append(exception.getMessage()).append(System.lineSeparator());
        exception.printStackTrace(new PrintWriter(errors));
        cronJobJalo.addLog(msg + " Exception: [" + errors.toString() + "]. ",
                EnumerationManager.getInstance().getEnumerationValue(GeneratedCronJobConstants.TC.JOBLOGLEVEL, JobLogLevel.ERROR.getCode()));
        LOG.error(msg, exception);
    }

    private void sleep() {
        try {
            Thread.sleep(SLEEP_BETWEEN_CALLS);
        } catch (InterruptedException ie) {
            LOG.warn("Exception during sleep between calls", ie);
        }
    }

    public ImportPortType getIndexManagementWebserviceClient() {
        return indexManagementWebserviceClient;
    }

    @Required
    public void setIndexManagementWebserviceClient(final ImportPortType indexManagementWebserviceClient) {
        this.indexManagementWebserviceClient = indexManagementWebserviceClient;
    }

    public FactFinderAuthentication getAuthentication() {
        return authentication;
    }

    @Required
    public void setAuthentication(final FactFinderAuthentication authentication) {
        this.authentication = authentication;
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

}
