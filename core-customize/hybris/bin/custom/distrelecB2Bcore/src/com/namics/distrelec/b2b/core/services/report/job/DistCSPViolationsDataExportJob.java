package com.namics.distrelec.b2b.core.services.report.job;

import static org.apache.commons.lang.StringUtils.EMPTY;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.integration.azure.uploader.impl.AzureFileUploaderImpl;
import com.namics.distrelec.b2b.core.model.jobs.CSPViolationsDataExportCronJobModel;
import com.namics.distrelec.b2b.core.model.security.report.CSPViolationsModel;
import com.namics.distrelec.b2b.core.service.newsletter.jobs.AbstractCSVExporterJob;
import com.namics.distrelec.b2b.core.service.report.DistCSPReportService;
import com.opencsv.CSVWriter;

public class DistCSPViolationsDataExportJob extends AbstractCSVExporterJob<CSPViolationsDataExportCronJobModel, CSPViolationsModel>  {

    private static final Logger LOG = LoggerFactory.getLogger(DistCSPViolationsDataExportJob.class);
    
    private static final String EXPORT_FILENAME_PREFIX = "csp-violations-export-";
    
    private static final String[] HEADERS = {"Code", "Document URL", "Blocked URL", "Violated Directive", "Created Date", "Data"};

    private static final DateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String HORIZONTAL_SPACE_REGEX = "(^\\h*) | (\\h*$)";
    private static final String BLANK_STRING = " ";
    private static final String LINE_SEPARATOR = "line.separator";
    

    private String azureHotfolderLocalDirectoryBase;

    private String azureHotfolderRemotePath;

    private AzureFileUploaderImpl azureFileUploader;

    @Autowired
    private DistCSPReportService distCSPReportService;



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
        return DistConstants.PropKey.ContentSecurityPolicy.EXPORT_DIRECTORY_PROPERTY;
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
    protected void exportItems(List<CSPViolationsModel> items, final CSPViolationsDataExportCronJobModel cronJob) throws IOException {
       //Ignore values with -1;
        final List<CSPViolationsModel> filteredItems = items.stream()
                .filter(item -> !item.getData().isEmpty())
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

        
        getModelService().removeAll(filteredItems);

        azureFileUploader.uploadAndDeleteLocalFile(new File(azureHotfolderLocalDirectoryBase + File.separator + fileName), azureHotfolderRemotePath + File.separator + exportPath);
        
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.newsletter.jobs.AbstractCSVExporterJob#afterSuccess(de.hybris.platform.cronjob.model.
     * CronJobModel)
     */
    @Override
    public void onSuccess(final CSPViolationsDataExportCronJobModel cronJob) {
        cronJob.setLastExportDate(new Date());
        super.onSuccess(cronJob);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.newsletter.jobs.AbstractCSVExporterJob#exportItem(de.hybris.platform.core.model.ItemModel)
     */
    @Override
    protected String[] exportItem(final CSPViolationsModel csp) {
        csp.setExported(Boolean.TRUE);
        return new String[] { csp.getCode(), //
                              csp.getDocumentUri() != null ? csp.getDocumentUri() : EMPTY, //
                               csp.getBlockedUri() != null ? csp.getBlockedUri() : EMPTY, //
                               csp.getViolatedDirective(), //
                               INPUT_DATE_FORMAT.format(csp.getCreationtime()), //
                               csp.getData() != null ? filterOutWhitespace(csp.getData()) : EMPTY};
    
    }

    /**
     *  Filter out whitespace 
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
    
   
    @Override
    protected List<CSPViolationsModel> findItemsToExport() {
        return getDistCSPReportService().findExported(false);
    }

    // Getters & Setters

   

    public void setAzureHotfolderLocalDirectoryBase(String azureHotfolderLocalDirectoryBase) {
        this.azureHotfolderLocalDirectoryBase = azureHotfolderLocalDirectoryBase;
    }

    public DistCSPReportService getDistCSPReportService() {
        return distCSPReportService;
    }

    public void setDistCSPReportService(DistCSPReportService distCSPReportService) {
        this.distCSPReportService = distCSPReportService;
    }

    public void setAzureFileUploader(AzureFileUploaderImpl azureFileUploader) {
        this.azureFileUploader = azureFileUploader;
    }

    public void setAzureHotfolderRemotePath(String azureHotfolderRemotePath) {
        this.azureHotfolderRemotePath = azureHotfolderRemotePath;
    }
}
