/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.nps.jobs;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.integration.azure.uploader.impl.AzureFileUploaderImpl;
import com.namics.distrelec.b2b.core.model.feedback.DistNetPromoterScoreModel;
import com.namics.distrelec.b2b.core.model.jobs.NetPromoterScoreExportCronJobModel;
import com.namics.distrelec.b2b.core.service.newsletter.jobs.AbstractCSVExporterJob;
import com.namics.distrelec.b2b.core.service.nps.DistNetPromoterScoreService;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.apache.commons.lang.StringUtils.EMPTY;

/**
 * {@code NetPromoterScoreExportJob}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class NetPromoterScoreExportJob extends AbstractCSVExporterJob<NetPromoterScoreExportCronJobModel, DistNetPromoterScoreModel> {

    private static final Logger LOG = LoggerFactory.getLogger(NetPromoterScoreExportJob.class);
    
    private static final String EXPORT_FILENAME_PREFIX = "nps-export-";
    
    private static final String[] HEADERS = { "Code", "ReasonCode", "SubReasonCode", "Type", "Sales Org.", "Domain", "Email", "First Name", "Last Name",
            "Company Name", "ERP Customer ID", "ERP Contact ID", "Order Number", "Delivery Date", "Input Date", "Value", "Free Text" };

    private static final DateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String HORIZONTAL_SPACE_REGEX = "(^\\h*) | (\\h*$)";
    private static final String BLANK_STRING = " ";
    private static final String LINE_SEPARATOR = "line.separator";
    private static final int BLANK_SCORE = -1;

    private String azureHotfolderLocalDirectoryBase;

    private String azureHotfolderRemotePath;

    private AzureFileUploaderImpl azureFileUploader;

    @Autowired
    private DistNetPromoterScoreService distNetPromoterScoreService;


    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.newsletter.jobs.AbstractCSVExporterJob#getExportFileNamePrefix()
     */
    @Override
    protected String getExportFileNamePrefix() {
        return EXPORT_FILENAME_PREFIX;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.newsletter.jobs.AbstractCSVExporterJob#getExportDirectoryProperty()
     */
    @Override
    protected String getExportDirectoryProperty() {
        return DistConstants.PropKey.NetPromoterScore.EXPORT_DIRECTORY_PROPERTY;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.newsletter.jobs.AbstractCSVExporterJob#getHeaders()
     */
    @Override
    protected String[] getHeaders() {
        return HEADERS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.newsletter.jobs.AbstractCSVExporterJob#exportItems(java.util.List,
     * de.hybris.platform.cronjob.model.CronJobModel)
     */
    @Override
    protected void exportItems(List<DistNetPromoterScoreModel> items, final NetPromoterScoreExportCronJobModel cronJob) throws IOException {
       //Ignore values with -1;
        final List<DistNetPromoterScoreModel> filteredItems = items.stream()
                .filter(item -> BLANK_SCORE != item.getValue())
                .collect(Collectors.toList());


        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        final String exportPath = getConfigurationService().getConfiguration().getString(getExportDirectoryProperty());
        final File exportDir = new File(azureHotfolderLocalDirectoryBase + File.separator + exportPath);
        if (!(exportDir.exists() || exportDir.mkdirs())) {
            throw new IOException("Unable to create the export folder [Path: " + exportPath + "]");
        }
        final String fileName = exportPath.endsWith(File.separator) ? exportPath + getExportFileNamePrefix() + dateFormat.format(new Date()) + ".csv"
                : exportPath + File.separator + getExportFileNamePrefix() + dateFormat.format(new Date()) + ".csv";
        LOG.info("Export destination file : " + fileName);
        final CSVWriter csvWriter = new CSVWriter(new FileWriter(azureHotfolderLocalDirectoryBase + File.separator + fileName), ';');

        // Write the header line
        csvWriter.writeNext(getHeaders());
        filteredItems.forEach(item -> {
            try {
                csvWriter.writeNext(exportItem(item));
                csvWriter.flush();
            } catch (final IOException e) {
                LOG.error("ERROR occur during the export of the item " + item.toString(), e);
            }
        });
        csvWriter.close();

        getModelService().saveAll(filteredItems);

        azureFileUploader.uploadAndDeleteLocalFile(new File(azureHotfolderLocalDirectoryBase + File.separator + fileName), azureHotfolderRemotePath + File.separator + exportPath);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.newsletter.jobs.AbstractCSVExporterJob#afterSuccess(de.hybris.platform.cronjob.model.
     * CronJobModel)
     */
    @Override
    public void onSuccess(final NetPromoterScoreExportCronJobModel cronJob) {
        cronJob.setLastExportDate(new Date());
        super.onSuccess(cronJob);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.newsletter.jobs.AbstractCSVExporterJob#exportItem(de.hybris.platform.core.model.ItemModel)
     */
    @Override
    protected String[] exportItem(final DistNetPromoterScoreModel nps) {
        nps.setExported(Boolean.TRUE);
        return new String[] { nps.getCode(), //
                nps.getReason() != null ? nps.getReason().getCode() : EMPTY, //
                nps.getSubReason() != null ? nps.getSubReason().getCode() : EMPTY, //
                nps.getType() != null ? nps.getType().getCode() : EMPTY, //
                nps.getSalesOrg(), //
                nps.getDomain(), //
                nps.getEmail(), //
                nps.getFirstname(), //
                nps.getLastname(), //
                nps.getCompanyName(), //
                nps.getErpCustomerID(), //
                nps.getErpContactID(), //
                nps.getOrderNumber(), //
                nps.getDeliveryDate() != null ? INPUT_DATE_FORMAT.format(nps.getDeliveryDate()) : EMPTY, //
                INPUT_DATE_FORMAT.format(nps.getCreationtime()), //
                String.valueOf(nps.getValue()), //
                filterOutWhitespace(nps.getText()) };
    }

    /**
     *  Filter out whitespace in a NPS File
     * @param unfiltered text containing potential whitespace characters.
     * @return A String containing no whitespace characters.
     */
    private String filterOutWhitespace(String unfiltered){
        if(null == unfiltered || unfiltered.isEmpty()){
            return EMPTY;
        } else {
            unfiltered = unfiltered.replaceAll(System.getProperty(LINE_SEPARATOR), BLANK_STRING);
            return unfiltered.replaceAll(HORIZONTAL_SPACE_REGEX, EMPTY);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.newsletter.jobs.AbstractCSVExporterJob#findItemsToExport()
     */
    @Override
    protected List<DistNetPromoterScoreModel> findItemsToExport() {
        return getDistNetPromoterScoreService().findExported(false);
    }

    // Getters & Setters

    public DistNetPromoterScoreService getDistNetPromoterScoreService() {
        return distNetPromoterScoreService;
    }

    public void setDistNetPromoterScoreService(final DistNetPromoterScoreService distNetPromoterScoreService) {
        this.distNetPromoterScoreService = distNetPromoterScoreService;
    }

    public void setAzureHotfolderLocalDirectoryBase(String azureHotfolderLocalDirectoryBase) {
        this.azureHotfolderLocalDirectoryBase = azureHotfolderLocalDirectoryBase;
    }

    public void setAzureFileUploader(AzureFileUploaderImpl azureFileUploader) {
        this.azureFileUploader = azureFileUploader;
    }

    public void setAzureHotfolderRemotePath(String azureHotfolderRemotePath) {
        this.azureHotfolderRemotePath = azureHotfolderRemotePath;
    }
}
