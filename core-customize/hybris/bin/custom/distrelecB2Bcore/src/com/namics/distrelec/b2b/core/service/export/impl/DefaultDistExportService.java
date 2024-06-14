/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.export.impl;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.export.DistZipService;
import com.namics.distrelec.b2b.core.reevoo.util.ScpUtils;
import com.namics.distrelec.b2b.core.service.export.DistExportService;
import com.namics.distrelec.b2b.core.service.export.data.AbstractDistExportData;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.media.MediaIOException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Default implementation of {@link DistExportService}
 *
 * @author pbueschi, Namics AG
 */
public class DefaultDistExportService implements DistExportService {

    private static final Logger LOG = LogManager.getLogger(DefaultDistExportService.class);

    public static final String DATE_PATTERN = "yyyyMMdd-HHmmss";

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private CatalogVersionService catalogVersionService;

    @Autowired
    private DistZipService distZipService;

    @Autowired
    private Converter<List<? extends AbstractDistExportData>, String> exportConvertObjectsToCSV;

    @Autowired
    private ModelService modelService;

    @Autowired
    private MediaService mediaService;

    @Override
    public File getDownloadExportFile(final List<? extends AbstractDistExportData> exportData, final String exportFormat, final String exportFileNamePrefix) {
        File downloadFile = null;

        final String filePath = System.getProperty("HYBRIS_TEMP_DIR", System.getProperty("java.io.tmpdir"));
        if (exportFormat.equals(DistConstants.Export.FORMAT_CSV)) {
            final String csvFileNameSuffix = DistConstants.Punctuation.DOT + DistConstants.Export.FORMAT_CSV;
            downloadFile = createCSVFile(exportData, getFullFileName(filePath, exportFileNamePrefix, csvFileNameSuffix));
        }
        if (exportFormat.equals(DistConstants.Export.FORMAT_XLS)) {
            final String xlsFileNameSuffix = DistConstants.Punctuation.DOT + DistConstants.Export.FORMAT_XLS;
            downloadFile = createXLSFile(exportData, getFullFileName(filePath, exportFileNamePrefix, xlsFileNameSuffix));
        }
        return downloadFile;
    }

    /**
     * Create filename with configured path and filename extended by current session id.
     */
    protected String getFullFileName(final String filePath, final String fileNamePrefix, final String fileNameSuffix) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        final StringBuilder fullFileName = new StringBuilder();
        fullFileName.append(filePath);
        if (StringUtils.isNotBlank(filePath) && !StringUtils.right(filePath, 1).equals(DistConstants.Punctuation.FORWARD_SLASH)) {
            fullFileName.append(DistConstants.Punctuation.FORWARD_SLASH);
        }
        fullFileName.append(fileNamePrefix.replace(' ', '_'));
        fullFileName.append(DistConstants.Punctuation.UNDERSCORE);
        fullFileName.append(simpleDateFormat.format(new Date()));
        fullFileName.append(fileNameSuffix);
        return fullFileName.toString();
    }

    protected File createCSVFile(final List<? extends AbstractDistExportData> exportData, final String fileName) {
        final String csvContent = createCSVFileContent(exportData);
        final File outputFile = new File(fileName);
        try {
            final FileWriter fileWriter = new FileWriter(outputFile.getAbsoluteFile());
            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                bufferedWriter.write(csvContent);
            }
        } catch (final IOException e) {
            LOG.error("Could not create CSV file", e);
        }
        return outputFile;
    }

    protected File createXLSFile(final List<? extends AbstractDistExportData> exportData, final String fileName) {
        final String csvContent = createCSVFileContent(exportData);
        final HSSFWorkbook xlsWorkbook = createXLSFileContent(csvContent);
        final File outputFile = new File(fileName);
        try {
            final FileOutputStream outputStream = new FileOutputStream(outputFile);
            xlsWorkbook.write(outputStream);
            outputStream.close();
        } catch (final IOException e) {
            LOG.error("Could not create XLS file", e);
        }
        return outputFile;
    }

    protected String createCSVFileContent(final List<? extends AbstractDistExportData> exportData) {
        return exportConvertObjectsToCSV.convert(exportData);
    }

    protected HSSFWorkbook createXLSFileContent(final String csvContent) {
        final HSSFWorkbook workbook = new HSSFWorkbook();
        final HSSFSheet sheet = workbook.createSheet("Sample sheet");

        int rowNumber = 0;
        final String[] csvContentRows = StringUtils.split(csvContent, "\r\n");
        for (String csvContentRow : csvContentRows) {
            int cellNumber = 0;
            final Row row = sheet.createRow(rowNumber++);
            for (final Object obj : StringUtils.splitPreserveAllTokens(csvContentRow, ";")) {
                final Cell cell = row.createCell(cellNumber++);
                if (obj instanceof Date) {
                    cell.setCellValue((Date) obj);
                } else if (obj instanceof Boolean) {
                    cell.setCellValue((Boolean) obj);
                } else if (obj instanceof String) {
                    cell.setCellValue((String) obj);
                } else if (obj instanceof Double) {
                    cell.setCellValue((Double) obj);
                } else if (obj instanceof Calendar) {
                    cell.setCellValue((Calendar) obj);
                }
            }
        }

        return workbook;
    }

    @Override
    public void saveExportData(InputStream exportDataStream, String exportMediaName, String exportFolder, String fileType, boolean exportAsZip) {
        try {
            String exportFileName = exportMediaName + fileType;

            final byte[] data = copyData(exportDataStream);
            final byte[] zippedData = createAndSaveMedia(data, exportMediaName, exportFileName);

            if (exportAsZip) {
                String newExportFileName = exportFileName + DistConstants.Punctuation.DOT + DistConstants.Export.FORMAT_ZIP;
                saveExternal(new ByteArrayInputStream(zippedData), newExportFileName, exportFolder);
            } else {
                saveExternal(new ByteArrayInputStream(data), exportFileName, exportFolder);
            }
        } catch (final MediaIOException | IOException e) {
            LOG.error("Failed writing export data for file {} to media.", exportMediaName, e);
        }
    }

    private byte[] copyData(InputStream exportDataStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(exportDataStream, baos);
        return baos.toByteArray();
    }

    private byte[] createAndSaveMedia(byte[] bytes, String exportMediaName, String exportFileName) {
        final MediaModel exportMedia = modelService.create(MediaModel.class);
        exportMedia.setCode(exportMediaName);
        exportMedia.setCatalogVersion(catalogVersionService.getCatalogVersion(DistConstants.Catalog.DISTRELEC_PRODUCT_CATALOG_ID,
          DistConstants.CatalogVersion.ONLINE));
        modelService.save(exportMedia);
        final byte[] exportData = distZipService.zip(new ByteArrayInputStream(bytes), exportFileName);
        mediaService.setDataForMedia(exportMedia, exportData);
        return exportData;
    }

    private void saveExternal(final InputStream from, String exportFileName, String exportFolder) throws IOException {
        try {
            final Configuration config = configurationService.getConfiguration();
            final String user = config.getString(DistConstants.PropKey.Sap.EXPORT_UPLOAD_SCP_USER);
            final String host = config.getString(DistConstants.PropKey.Sap.EXPORT_UPLOAD_SCP_HOST);
            final int port = config.getInt(DistConstants.PropKey.Sap.EXPORT_UPLOAD_SCP_PORT, 2222);
            final String to = StringUtils.isNotBlank(exportFolder) ? exportFolder : config.getString(DistConstants.PropKey.Sap.EXPORT_UPLOAD_SCP_FOLDER);
            final String privateKey = config.getString(DistConstants.PropKey.Sap.EXPORT_UPLOAD_SCP_PRIVATEKEY);
            final String keyPassword = config.getString(DistConstants.PropKey.Sap.EXPORT_UPLOAD_SCP_KEYPASSWORD);
            LOG.info("Upload {} to {}@{}:{}", exportFileName, user, host, to);
            Session session = null;
            try {
                session = ScpUtils.createSession(user, host, port, privateKey, keyPassword);
                ScpUtils.copyLocalToRemote(session, from, to, exportFileName);
            } finally {
                if (session != null) {
                    session.disconnect();
                }
            }
        } catch (JSchException ex) {
            LOG.error("Cannot close FTP Client", ex);
        }
    }
}
