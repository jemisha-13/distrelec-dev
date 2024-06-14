/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.education.job;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.namics.distrelec.b2b.core.integration.azure.uploader.impl.AzureFileUploaderImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.education.data.DistEducationRegistrationData;
import com.namics.distrelec.b2b.core.model.DistEducationRegistrationModel;
import com.namics.distrelec.b2b.core.model.jobs.DistExportEducationRegistrationCronJobModel;
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

/**
 * {@code DistExportEducationRegistrationJob}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class DistExportEducationRegistrationJob extends AbstractJobPerformable<DistExportEducationRegistrationCronJobModel> {

    private static final Logger LOG = Logger.getLogger(DistExportEducationRegistrationJob.class);
    private static final String FILENAME = "educationRegistrations_";

    private String azureHotfolderLocalDirectoryBase;

    private String azureHotfolderRemotePath;

    private AzureFileUploaderImpl azureFileUploader;

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
    public PerformResult perform(DistExportEducationRegistrationCronJobModel arg0) {
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
        LOG.info("Finished exporting education registration Job in " + (int) ((endTime - startTime) / 1e9) + " seconds.");
        return new PerformResult(success ? CronJobResult.SUCCESS : CronJobResult.ERROR, CronJobStatus.FINISHED);
    }

    private List<DistEducationRegistrationModel> getRegistrationsToExport() {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT {").append(DistEducationRegistrationModel.PK).append("} FROM {").append(DistEducationRegistrationModel._TYPECODE)
                .append("} WHERE {").append(DistEducationRegistrationModel.EXPORTED).append("} =(?").append(DistEducationRegistrationModel.EXPORTED)
                .append(")");
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put(DistEducationRegistrationModel.EXPORTED, Boolean.FALSE);
        final SearchResult<DistEducationRegistrationModel> result = flexibleSearchService.search(query.toString(), params);
        return result.getTotalCount() > 0 ? result.getResult() : ListUtils.EMPTY_LIST;

    }

    private void exportRegistrations(final List<DistEducationRegistrationModel> educationRegistrations) throws ConversionException, IOException {
        if (CollectionUtils.isNotEmpty(educationRegistrations)) {
            final String path = FileUtils.concatDirectoryAndFilename(configurationService.getConfiguration().getString("education.export.resources"), FILENAME + System.currentTimeMillis() + ".csv");
            final String localPath = azureHotfolderLocalDirectoryBase + '/' + path;
            File file = new File(localPath);
            final OutputStream stream = new FileOutputStream(file);
            stream.write(exportConvertObjectsToCSV.convert(converToData(educationRegistrations)).getBytes());
            stream.flush();
            stream.close();
            modelService.saveAll(educationRegistrations);

            azureFileUploader.uploadAndDeleteLocalFile(file, azureHotfolderRemotePath + '/' + path);
        }
    }

    private List<Object> converToData(final List<DistEducationRegistrationModel> educationRegistrations) {
        final List<Object> datas = new ArrayList<>();
        for (final DistEducationRegistrationModel source : educationRegistrations) {
            final DistEducationRegistrationData target = new DistEducationRegistrationData();
            target.setProfileArea(source.getEducationArea());
            target.setContactFirstName(source.getContactFirstName());
            target.setContactLastName(source.getContactLastName());
            target.setContactAddress1(source.getContactAddress1());
            target.setContactAddress2(source.getContactAddress2());
            target.setContactZip(source.getContactZip());
            target.setContactPlace(source.getContactPlace());
            target.setContactCountry(source.getContactCountry());
            target.setContactEmail(source.getContactEmail());
            target.setContactPhoneNumber(source.getContactPhoneNumber());
            target.setContactMobileNumber(source.getContactMobileNumber());

            // Institution data
            target.setInstitutionName(source.getInstitutionName());
            target.setInstitutionAddress1(source.getInstitutionAddress1());
            target.setInstitutionAddress2(source.getInstitutionAddress2());
            target.setInstitutionZip(source.getInstitutionZip());
            target.setInstitutionPlace(source.getInstitutionPlace());
            target.setInstitutionCountry(source.getInstitutionCountry());
            target.setInstitutionPhoneNumber(source.getInstitutionPhoneNumber());
            target.setInstitutionEmail(source.getInstitutionEmail());
            datas.add(target);
            source.setExported(Boolean.TRUE);
        }
        return datas;
    }

    // Getters & Setters

    public ModelService getModelService() {
        return modelService;
    }

    @Override
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
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

    public void setAzureHotfolderLocalDirectoryBase(String azureHotfolderLocalDirectoryBase) {
        this.azureHotfolderLocalDirectoryBase = azureHotfolderLocalDirectoryBase;
    }

    public void setAzureHotfolderRemotePath(String azureHotfolderRemotePath) {
        this.azureHotfolderRemotePath = azureHotfolderRemotePath;
    }

    public void setAzureFileUploader(AzureFileUploaderImpl azureFileUploader) {
        this.azureFileUploader = azureFileUploader;
    }
}
