/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.infocenter.seminar.registration.job;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarRegistrationData;
import com.namics.distrelec.b2b.core.model.DistSeminarRegistrationModel;
import com.namics.distrelec.b2b.core.model.jobs.DistExportSeminarRegistrationCronJobModel;
import com.namics.hybris.toolbox.FileUtils;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

public class DistExportSeminarRegistrationJob extends AbstractJobPerformable<DistExportSeminarRegistrationCronJobModel> {

    private static final Logger LOG = Logger.getLogger(DistExportSeminarRegistrationJob.class);
    private static final String FILENAME = "seminarRegistrations_";

    @Autowired
    private ModelService modelService;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    @Qualifier("defaultExportConvertObjectsToCSV")
    private Converter<List<Object>, String> exportConvertObjectsToCSV;

    @Override
    public PerformResult perform(final DistExportSeminarRegistrationCronJobModel cronjob) {
        final long startTime = System.nanoTime();
        boolean success = false;
        try {
            exportRegistrations(getRegistrationsToExport());
            success = true;
        } catch (FileNotFoundException e) {
            LOG.error("Could not write csv-file. Check if the configured path is correct.", e);
        } catch (ConversionException e) {
            LOG.error("Could not convert given registration to a csv-String", e);
        } catch (IOException e) {
            LOG.error("Could not write to file.", e);
        }

        final long endTime = System.nanoTime();
        LOG.info("Finished exporting seminar registration Job in " + (int) ((endTime - startTime) / 1e9) + " seconds.");
        return new PerformResult(success ? CronJobResult.SUCCESS : CronJobResult.ERROR, CronJobStatus.FINISHED);
    }

    private List<DistSeminarRegistrationModel> getRegistrationsToExport() {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT {").append(DistSeminarRegistrationModel.PK).append("} FROM {").append(DistSeminarRegistrationModel._TYPECODE).append("} WHERE {")
                .append(DistSeminarRegistrationModel.EXPORTED).append("} =(?").append(DistSeminarRegistrationModel.EXPORTED).append(")");
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put(DistSeminarRegistrationModel.EXPORTED, Boolean.FALSE);
        final SearchResult<DistSeminarRegistrationModel> result = flexibleSearchService.search(query.toString(), params);
        return result.getTotalCount() > 0 ? result.getResult() : ListUtils.EMPTY_LIST;

    }

    private void exportRegistrations(final List<DistSeminarRegistrationModel> seminarRegistrations) throws ConversionException, IOException {
        if (CollectionUtils.isNotEmpty(seminarRegistrations)) {
            final String path = configurationService.getConfiguration().getString("infoCenter.seminar.registration.export.path");
            final OutputStream stream = new FileOutputStream(FileUtils.concatDirectoryAndFilename(path, FILENAME + System.currentTimeMillis() + ".csv"));
            stream.write(exportConvertObjectsToCSV.convert(converToData(seminarRegistrations)).getBytes());
            stream.flush();
            stream.close();
            modelService.saveAll(seminarRegistrations);
        }
    }

    private List<Object> converToData(final List<DistSeminarRegistrationModel> seminarRegistrations) {
        final List<Object> datas = new ArrayList<>();
        for (final DistSeminarRegistrationModel registration : seminarRegistrations) {
            final DistSeminarRegistrationData registrationData = new DistSeminarRegistrationData();
            registrationData.setTopic(registration.getTopic());
            registrationData.setDate(registration.getSeminarDate());
            registrationData.setCompanyName(registration.getCompanyName());
            registrationData.setCustomerNumber(registration.getCustomerNumber());
            registrationData.setSalutation(registration.getSalutation());
            registrationData.setLastName(registration.getLastName());
            registrationData.setFirstName(registration.getFirstName());
            registrationData.setDepartment(registration.getDepartment());
            registrationData.setStreet(registration.getStreet());
            registrationData.setNumber(registration.getNumber());
            registrationData.setPobox(registration.getPobox());
            registrationData.setZip(registration.getZip());
            registrationData.setPlace(registration.getPlace());
            registrationData.setCountry(registration.getCountry());
            registrationData.setDirectPhone(registration.getDirectPhone());
            registrationData.seteMail(registration.getEMail());
            registrationData.setFax(registration.getFax());
            registrationData.setComment(registration.getComment());
            datas.add(registrationData);

            registration.setExported(Boolean.TRUE);
        }
        return datas;
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Override
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public Converter<List<Object>, String> getExportConvertObjectsToCSV() {
        return exportConvertObjectsToCSV;
    }

    public void setExportConvertObjectsToCSV(final Converter<List<Object>, String> exportConvertObjectsToCSV) {
        this.exportConvertObjectsToCSV = exportConvertObjectsToCSV;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    @Override
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
