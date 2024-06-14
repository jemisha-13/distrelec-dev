/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.survey.job;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyAnswerModel;
import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyModel;
import com.namics.distrelec.b2b.core.model.jobs.DistOnlineSurveyExportCronJobModel;
import com.namics.distrelec.b2b.core.service.survey.service.DistOnlineSurveyService;
import com.opencsv.CSVWriter;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * {@code DistOnlineSurveyExportJob}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DistOnlineSurveyExportJob extends AbstractJobPerformable<DistOnlineSurveyExportCronJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(DistOnlineSurveyExportJob.class);
    private static final String EXPORT_FILENAME_PREFIX = "survey-export-";
    private static final String[] HEADERS = { "Sequence ID", "Language", "Date-Time", "Question", "Answer Value" };

    private static final DateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.ENGLISH);

    @Autowired
    private ModelService modelService;
    @Autowired
    private DistOnlineSurveyService onlineSurveyService;
    @Autowired
    private ConfigurationService configurationService;

    // TODO complete implementation

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.CronJobModel)
     */
    @Override
    public PerformResult perform(final DistOnlineSurveyExportCronJobModel cronJob) {
        final long startTime = System.nanoTime();
        LOG.info("Starting Online Survey export CronJob at " + new Date());
        boolean success = true;
        try {
            exportSurveys(cronJob);
            cronJob.setLastExportDate(new Date());
            getModelService().save(cronJob);
        } catch (final Exception exp) {
            LOG.error("An error occurs while exporting the surveys", exp);
            success = false;
        }
        LOG.info("Finished exporting online surveys Job in " + (int) ((System.nanoTime() - startTime) / 1e9) + " seconds.");
        return new PerformResult(success ? CronJobResult.SUCCESS : CronJobResult.ERROR, CronJobStatus.FINISHED);
    }

    /**
     * Export the list of all non exported NPSs to a CSV file.
     * 
     * @param cronJob
     * @throws IOException
     */
    private void exportSurveys(final DistOnlineSurveyExportCronJobModel cronJob) throws IOException {

        final List<DistOnlineSurveyModel> surveys = getOnlineSurveyService().findActiveOnDate(cronJob.getLastExportDate());
        if (surveys.isEmpty()) {
            LOG.info("No active survey to export!");
            return;
        }

        // Get configurations for survey export.
        final String exportPath = getConfigurationService().getConfiguration().getString("erp.survey.export.directory");
        final File exportDir = new File(exportPath);
        if (!(exportDir.exists() || exportDir.mkdirs())) {
            throw new IOException("Unable to create the Online Survey root export folder [Path: " + exportPath + "]");
        }

        final Date lastExportDate = cronJob.getLastExportDate();
        LOG.info("Export Online Survey records, last export : " + (lastExportDate == null ? "No export has been done yet" : lastExportDate));

        // Each survey is exported in a separate file.
        for (final DistOnlineSurveyModel survey : surveys) {
            try {
                exportSurvey(survey, exportPath);
            } catch (final IOException ioexp) {
                LOG.error("An unknown error occurs while exporting survey [{}, {}]", new Object[] { survey.getUid(), survey.getVersion() });
            }
        }
    }

    /**
     * Exports the survey answers
     * 
     * @param survey
     *            the source survey
     * @param exportRootDirPath
     *            the root parent export folder path.
     * @throws IOException
     */
    private void exportSurvey(final DistOnlineSurveyModel survey, final String exportRootDirPath) throws IOException {
        final List<DistOnlineSurveyAnswerModel> answers = getOnlineSurveyService().getAnswersForExport(survey);
        if (answers.isEmpty()) {
            LOG.info("No new survey answer to export for survey : [{}, {}]", new Object[] { survey.getUid(), survey.getVersion() });
            return;
        }

        // Each survey is exported in a separate folder.
        String exportDirPath = exportRootDirPath + (exportRootDirPath.endsWith(File.separator) ? "" : File.separator) + survey.getVersion() + File.separator;

        final File exportDir = new File(exportDirPath);
        if (!(exportDir.exists() || exportDir.mkdir())) {
            throw new IOException("Unable to create the Online Survey export folder [Path: " + exportDirPath + "]");
        }

        final String fileName = exportDirPath + EXPORT_FILENAME_PREFIX + survey.getVersion() + "_" + dateFormat.format(new Date()) + ".csv";

        LOG.info("Export Online Survey records destination file : " + fileName);

        final CSVWriter csvWriter = new CSVWriter(new FileWriter(fileName), ';');

        // Write the header line
        csvWriter.writeNext(HEADERS);

        for (final DistOnlineSurveyAnswerModel answer : answers) {
            csvWriter.writeNext(exportAnswer(answer));
            csvWriter.flush();
            // Set the exported flag to true
            answer.setExported(true);
        }

        csvWriter.close();
        // Save the exported answers.
        getModelService().saveAll(answers);
    }

    /**
     * Export the Online Survey Answer model to an array of strings.
     * 
     * @param answer
     * @return an array of strings containing the online survey answer informations.
     */
    private String[] exportAnswer(final DistOnlineSurveyAnswerModel answer) {
        return new String[] { String.valueOf(answer.getSequenceID()), //
                answer.getLanguage(), //
                INPUT_DATE_FORMAT.format(answer.getCreationtime()), //
                answer.getQuestion().getValue(new Locale(answer.getLanguage())), //
                answer.getValue() };
    }

    // Getters and Setters.

    public ModelService getModelService() {
        return modelService;
    }

    @Override
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public DistOnlineSurveyService getOnlineSurveyService() {
        return onlineSurveyService;
    }

    public void setOnlineSurveyService(final DistOnlineSurveyService onlineSurveyService) {
        this.onlineSurveyService = onlineSurveyService;
    }
}
