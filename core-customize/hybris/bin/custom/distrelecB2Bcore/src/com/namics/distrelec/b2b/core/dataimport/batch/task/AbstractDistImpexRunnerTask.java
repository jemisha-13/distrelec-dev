/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.batch.task;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.AbstractImpexRunnerTask;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.impex.jalo.imp.ValueLine;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.impex.ImpExResource;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.impl.StreamBasedImpExResource;
import de.hybris.platform.servicelayer.impex.impl.ValueLineError;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.inout.erp.impl.OnlineReconciliationService;

import java.io.*;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@code AbstractDistImpexRunnerTask}
 * <p>
 * AbstractDistImpexRunnerTask extends {@link AbstractImpexRunnerTask} to handle files that are empty.
 * </p>
 *
 * @author DAEHUSIR, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public abstract class AbstractDistImpexRunnerTask extends AbstractImpexRunnerTask {

    private static final Logger LOG = LogManager.getLogger(AbstractDistImpexRunnerTask.class);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SS");
	private static final String ERP_PRICE_FILE_PREFIX ="erp_price10scales";
	private static final String IMPEX_FILE_PREFIX ="impex_1_";
	private static final String CSV_FILE_PREFIX =".csv";
    private Map<String, List<AfterImpexImport>> afterImpexImport = new HashMap<String, List<AfterImpexImport>>();
    private Map<String, List<BeforeImpexImport>> beforeImpexImport = new HashMap<String, List<BeforeImpexImport>>();
	
	@Autowired
	private OnlineReconciliationService reconciliationService;
    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private CatalogVersionService catalogVersionService;

    @Autowired
    private UserService userService;

    @Autowired
    private MediaService mediaService;

    /*
     * (non-Javadoc)
     *
     * @see
     * de.hybris.platform.acceleratorservices.dataimport.batch.task.AbstractImpexRunnerTask#execute(de.hybris.platform.acceleratorservices.
     * dataimport.batch.BatchHeader)
     */
    @Override
    public BatchHeader execute(final BatchHeader header) throws FileNotFoundException {
        return executeInternal(header);
    }

    /**
     * @param header
     * @return the {@link BatchHeader}
     * @throws FileNotFoundException
     */
    protected BatchHeader executeInternal(final BatchHeader header) throws FileNotFoundException {
        // 1) Make some verifications
        Assert.notNull(header);
        if (CollectionUtils.isEmpty(header.getTransformedFiles())) {
            LOG.info(header.getFile() != null ? ("File '" + header.getFile() + "' has no data, so it will be skipped.")
                    : ("Current file has no data, so it will be skipped."));
            return header;
        }

        Assert.notNull(header.getEncoding());

        // 3) Lock acquired, then start file processing.
        for (final File file : header.getTransformedFiles()) {
            final long startTime = System.currentTimeMillis();
            final ImportConfig config = getImportConfig();
            // Before ImpEx import
            before(config, header, file);
            // ImpEx import
            ImportResult importResult = processFile(config, file, header.getEncoding());
            if (importResult.isFinished()) {
                // After ImpEx import
                after(config, header, file);
            } else {
                LOG.warn("Impex import is not finished: " + importResult.getCronJob().getCode());
            }

            final long endTime = System.currentTimeMillis();
            LOG.info("[impex-file-import] filename: {}, Start time: {}, End time: {}, Duration: {} ms", file.getName(), DATE_FORMAT.format(new Date(startTime)),
                    DATE_FORMAT.format(new Date(endTime)), (endTime - startTime));
        }
        return header;
    }

    /**
     * Process the ImpEx file and import it.
     *
     * @param config
     * @param file
     * @param encoding
     * @throws FileNotFoundException
     */
    protected ImportResult processFile(final ImportConfig config, final File file, final String encoding) throws FileNotFoundException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);

            final ImportConfig _config = config != null ? config : getImportConfig();
            final ImpExResource resource = new StreamBasedImpExResource(fis, encoding);
            _config.setScript(resource);
            ImportResult importResult = getImportService().importData(_config);

            if (file.getName().contains(ERP_PRICE_FILE_PREFIX)) {
            	String filename=file.getName().replace(IMPEX_FILE_PREFIX, "").replace(CSV_FILE_PREFIX, "");
                Set<String> totalSet = new HashSet<>();

                try {
                    try (BufferedReader reader = new BufferedReader(
                            new FileReader(file))) {
                    	reader.lines().forEach(line -> {
        	    	    	line=line.replaceAll("\\s","");
              		       if(!line.equals("") && line.substring(0, 1).equals(";")) {
              		    	    String[] temp = line.split(";");
              		    	   totalSet.add(temp[1]+temp[2]+temp[3]);
              		       }
        	    	    });
                    } 
                    Set<String> failedRecordsSet = getImportService().collectImportErrors(importResult)
                            .filter(ValueLineError.class::isInstance)
                            .map(ValueLineError.class::cast)
                            .map(valueLineError -> {
                                ValueLine valueLine = valueLineError.getValueLine();
                                String erpPriceConditionId = valueLine.getValueEntry(1).getCellValue();
                                String articleId = valueLine.getValueEntry(2).getCellValue();
                                String userPriceList = valueLine.getValueEntry(3).getCellValue();
                                return StringUtils.join(new Object[]{
                                        erpPriceConditionId, articleId, userPriceList
                                }, ";");
                            })
                            .collect(Collectors.toSet());

                    int processedRecords = totalSet.size()-failedRecordsSet.size();
                    reconciliationService.sendProcessedRecordCount(filename,BigInteger.valueOf(processedRecords)); 
                    LOG.info("[erp-price-file-import] filename: {}, Number of Processed records: {}, Number of unresolved records: {}", filename,processedRecords,failedRecordsSet.size());
                    
                } catch (IOException e) {
                    LOG.error("Error occured while getting the processed record count!",e);
                }
            }

            return importResult;
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    /**
     * Perform before task jobs.
     *
     * @param config
     * @param batchHeader
     * @param impexFile
     */
    protected void before(final ImportConfig config, final BatchHeader batchHeader, final File impexFile) {
        final List<BeforeImpexImport> beforeImpexImportList = getBeforeImpexImport().get(getImportType(batchHeader.getFile().getName()));
        if (CollectionUtils.isNotEmpty(beforeImpexImportList)) {
            beforeImpexImportList.forEach(beforeImpexImport -> beforeImpexImport.before(config, batchHeader, batchHeader.getFile(), impexFile));
        }
    }

    /**
     * Perform after task jobs.
     *
     * @param config
     * @param batchHeader
     * @param impexFile
     */
    protected void after(final ImportConfig config, final BatchHeader batchHeader, final File impexFile) {
        final List<AfterImpexImport> afterImpexImportList = getAfterImpexImport().get(getImportType(batchHeader.getFile().getName()));
        if (CollectionUtils.isNotEmpty(afterImpexImportList)) {
            afterImpexImportList.forEach(afterImpexImport -> afterImpexImport.after(config, batchHeader, batchHeader.getFile(), impexFile));
        }
    }

    protected String getImportType(final String fileName) {
        int index = fileName.indexOf('-');
        if (index < 0) {
            index = StringUtils.ordinalIndexOf(fileName, "_", 2);
        }
        return fileName.substring(0, index);
    }

    public Map<String, List<AfterImpexImport>> getAfterImpexImport() {
        return afterImpexImport;
    }

    public void setAfterImpexImport(Map<String, List<AfterImpexImport>> afterImpexImport) {
        this.afterImpexImport = afterImpexImport;
    }

    public Map<String, List<BeforeImpexImport>> getBeforeImpexImport() {
        return beforeImpexImport;
    }

    public void setBeforeImpexImport(final Map<String, List<BeforeImpexImport>> beforeImpexImport) {
        this.beforeImpexImport = beforeImpexImport;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public CatalogVersionService getCatalogVersionService() {
        return catalogVersionService;
    }

    public void setCatalogVersionService(final CatalogVersionService catalogVersionService) {
        this.catalogVersionService = catalogVersionService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }
}
