package com.namics.distrelec.b2b.core.timestampupdate.job;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.jobs.DistExportFirstAppearanceDateToPIMCronJobModel;
import com.namics.distrelec.b2b.core.timestampupdate.ImportException;
import com.namics.distrelec.b2b.core.timestampupdate.ImportException.ErrorSource;
import com.namics.distrelec.b2b.core.timestampupdate.automatedtransfer.FTPWriteHandler;
import com.namics.distrelec.b2b.core.timestampupdate.service.DistTimeStampService;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class DistExportFirstAppearanceDateToPIMJob extends AbstractJobPerformable<DistExportFirstAppearanceDateToPIMCronJobModel> {

    private static final Logger LOG = Logger.getLogger(DistExportFirstAppearanceDateToPIMJob.class);

    private DistTimeStampService distTimeStampService;
    private FTPWriteHandler ftpWriteHandler;

    @Autowired
    private ConfigurationService configurationService;

    private static final String EXPORT_PIM_FTP_FOLDER_PATH = "pim.ftp.export.directory";
    private static final String PIM_FTP_HOST = "pim.ftp.host";
    private static final String PIM_FTP_USER = "pim.ftp.user";
    private static final String PIM_FTP_PASSWORD = "pim.ftp.password";

    // TODO complete implementation

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.CronJobModel)
     */

    @Override
    public PerformResult perform(final DistExportFirstAppearanceDateToPIMCronJobModel cronJob) {
        final long startTime = System.nanoTime();
        LOG.info("Starting DistExportFirstAppearanceDateToPIMJob at " + new Date());
        boolean success = true;

        try {
            performJob(cronJob.getExportFilePath());

        } catch (final Exception exp) {
            LOG.error(exp.getMessage(), exp);
            System.out.println("Exception");
        }
        LOG.info("Finished DistExportFirstAppearanceDateToPIM job in " + (int) ((System.nanoTime() - startTime) / 1e9) + " seconds.");
        return new PerformResult(success ? CronJobResult.SUCCESS : CronJobResult.ERROR, CronJobStatus.FINISHED);
    }

    protected void performJob(String exportfilepath) throws InterruptedException {
        LOG.info("Starting Fetching Data from database at " + new Date());

        String extension = null;
        boolean success = false;
        final int position = exportfilepath.lastIndexOf('.');

        final String ftpDir = configurationService.getConfiguration().getString(EXPORT_PIM_FTP_FOLDER_PATH);
        final String ftphost = configurationService.getConfiguration().getString(PIM_FTP_HOST);
        final String ftpuser = configurationService.getConfiguration().getString(PIM_FTP_USER);
        final String ftppassword = configurationService.getConfiguration().getString(PIM_FTP_PASSWORD);

        LOG.info("FTP IP: " + ftphost + " FTP User: " + ftpuser);
        try {
            List<List<String>> pimExportData = getDistTimeStampService().searchProductsForExport();
            if (!pimExportData.isEmpty() && !exportfilepath.equals("")) {
                if (position > 0) {
                    extension = exportfilepath.substring(position + 1);
                }
                if ("xls".equals(extension) || "xlsx".equals(extension)) {
                    success = createWorkbook(pimExportData, exportfilepath);
                }
                if ("csv".equals(extension)) {
                    success = createCSV(pimExportData, exportfilepath);
                }
                if (success) {
                    ftpWriteHandler.transferFileOnFTPServer(exportfilepath, ftpDir, ftphost, ftpuser, ftppassword);
                }
            }
        } catch (final Exception exp) {
            if (exp instanceof NullPointerException || exp instanceof IllegalStateException) {
                LOG.error("An error occurs while writing the data in file :" + exp);
            }
            LOG.error("An error occurs to exporting the file to pim: " + exp.toString());
        }
    }

    protected boolean createWorkbook(final List<List<String>> pimExportData, final String excelFilename) throws ImportException, IOException {
        try {
            // return writeToExcel(pimExportData, excelFilename);
            return writeToExcel(pimExportData, excelFilename);
        } catch (final IOException e) {
            throw new ImportException(e, ErrorSource.IO_EXCEPTION);
        }
    }

    protected boolean createCSV(final List<List<String>> pimExportData, final String excelFilename) throws ImportException, IOException {
        try {
            // return writeToExcel(pimExportData, excelFilename);
            return writeCsv(pimExportData, excelFilename);
        } catch (final IOException e) {
            throw new ImportException(e, ErrorSource.IO_EXCEPTION);
        }
    }

    public boolean writeCsv(final List<List<String>> writedatainfo, final String csvFilePath) throws IOException {
        final String COMMA_DELIMITER = ";";
        final String NEW_LINE_SEPARATOR = "\n";

        // CSV file header
        final String FILE_HEADER = "<ID>;wmAuto35PublishedInWeb";

        FileWriter fileWriter = null;

        try {

            fileWriter = new FileWriter(csvFilePath);

            // Write the CSV file header
            fileWriter.append(FILE_HEADER.toString());

            // Add a new line separator after the header
            fileWriter.append(NEW_LINE_SEPARATOR);

            Iterator<String> iterator = null;
            LOG.info("DistExportFirstAppearanceDateToPIMJob: No. of records from Database: " + writedatainfo.size());
            for (final List<String> writerow : writedatainfo) {

                iterator = writerow.iterator();
                // PIM ID
                fileWriter.append(String.valueOf(iterator.next()));
                fileWriter.append(COMMA_DELIMITER);

                // First Appreance Date
                fileWriter.append(String.valueOf(iterator.next()));
                fileWriter.append(NEW_LINE_SEPARATOR);
            }

            LOG.info("CSV file was created successfully !!!");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            LOG.error("IOException : Error while flushing/closing fileWriter !!!" + e);
        } catch (Exception ee) {
            LOG.error("Error while flushing/closing fileWriter !!!" + ee);
        }
        return true;
    }

    public boolean writeToExcel(final List<List<String>> writedatainfo, final String excelFilePath) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("TimeStamp Information");
        createHeaderRow(sheet);
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);
        CreationHelper createHelper = workbook.getCreationHelper();
        short dateFormat = createHelper.createDataFormat().getFormat("yyyy-MM-DD HH:mm:ss");
        cellStyle.setDataFormat(dateFormat);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        int rowCount = 1;
        LOG.info("DistExportFirstAppearanceDateToPIMJob: No. of records need to add in Excel: " + writedatainfo.size());
        Iterator<String> iterator = null;
        for (final List<String> aBook : writedatainfo) {
            iterator = aBook.iterator();
            Row row = sheet.createRow(rowCount);

            // Write PIM ID
            Cell cell = row.createCell(0);
            cell.setCellValue(iterator.next());

            // Write First Appearance Date
            cell = row.createCell(1);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(iterator.next());
            rowCount++;
        }
        LOG.info("DistExportFirstAppearanceDateToPIMJob: No. of records rows in Excel: " + rowCount);
        try {
            FileOutputStream outputStream = new FileOutputStream(excelFilePath);
            {
                workbook.write(outputStream);
            }
        } catch (Exception ee) {

        }
        return true;
    }

    protected void createHeaderRow(Sheet sheet) {

        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);
        Row row = sheet.createRow(0);
        Cell cellTitle = row.createCell(0);

        cellTitle.setCellStyle(cellStyle);
        cellTitle.setCellValue("<ID>");

        Cell cellAuthor = row.createCell(1);
        cellAuthor.setCellStyle(cellStyle);
        cellAuthor.setCellValue("wmAuto35PublishedInWeb");
    }

    // Getters and Setters.

    public DistTimeStampService getDistTimeStampService() {
        return distTimeStampService;
    }

    public void setDistTimeStampService(DistTimeStampService distTimeStampService) {
        this.distTimeStampService = distTimeStampService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public FTPWriteHandler getFtpWriteHandler() {
        return ftpWriteHandler;
    }

    public void setFtpWriteHandler(FTPWriteHandler ftpWriteHandler) {
        this.ftpWriteHandler = ftpWriteHandler;
    }

}