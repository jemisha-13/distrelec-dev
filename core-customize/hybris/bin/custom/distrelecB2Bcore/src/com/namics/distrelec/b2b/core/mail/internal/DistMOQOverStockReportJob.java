/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.mail.internal;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.google.common.collect.ImmutableList;
import com.namics.distrelec.b2b.core.model.jobs.DistInternalCronjobModel;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class DistMOQOverStockReportJob<T extends DistInternalCronjobModel> extends AbstractReportJob<T> {

    protected static final List<String> COLUMNS = ImmutableList.of("Product Code", "Minimum Quantity", "Stock");

    private static final Logger LOG = LogManager.getLogger(DistMOQOverStockReportJob.class);

    private static final int HEADER_SIZE_IN_ROWS = 3;

    private static final String REPORT_FILE_NAME = "MoqOverStock.xlsx";

    @Autowired
    private DistMOQOverStockReportDao distMOQOverStockReportDao;

    @Override
    public PerformResult perform(final T cronJob) {
        LOG.info("{} started", this.getClass().getSimpleName());
        boolean result = false;

        try {
            final List<List<String>> queryResult = getDistMOQOverStockReportDao().getQueryResult();

            final Map<String, Map<String, Pair<Integer, Integer>>> data = parseQueryResult(queryResult);

            // Create Header
            final XSSFWorkbook workbook = new XSSFWorkbook();
            final XSSFSheet sheet = getSheet(workbook);

            final Set<Pair<String, String>> shopCodesAndNames = getShopCodesAndNames(queryResult);
            writeHeader(sheet, shopCodesAndNames);

            writeData(sheet, shopCodesAndNames, data);

            addStyle(sheet, data, shopCodesAndNames);

            final File file = File.createTempFile("temp-file-name", ".xlsx");
            writeFileToFilesystem(workbook, file);

            // Send Email
            result = sendEmail(cronJob, file);
        } catch (final Exception e) {
            LOG.error(e, e);
        }

        return new PerformResult(result ? CronJobResult.SUCCESS : CronJobResult.ERROR, CronJobStatus.FINISHED);

    }

    /**
     * @param queryResult
     * @return The outer map will have the ShopName as Key. The inner Map will have Product Code as Key, MOQ:Stock as value
     */
    protected Map<String, Map<String, Pair<Integer, Integer>>> parseQueryResult(final List<List<String>> queryResult) {
        final Map<String, Map<String, Pair<Integer, Integer>>> result = new LinkedHashMap<>(); // Keep insertion Order
        queryResult.stream().forEachOrdered(l -> {
            Assert.notNull(l);
            final String webshopName = l.get(1);
            final String productCode = l.get(2);
            final Integer moq = Integer.valueOf(l.get(3));
            final Integer stock = Integer.valueOf(l.get(4));
            final Map<String, Pair<Integer, Integer>> productsMap = Optional.ofNullable(result.get(webshopName)).orElse(new LinkedHashMap<>());
            productsMap.put(productCode, Pair.of(moq, stock));
            result.put(webshopName, productsMap);
        });
        LOG.info("queryResult size: {}, result: {}", queryResult.size(), result); // TODO Remove LOG
        return result;
    }

    protected void writeHeader(final XSSFSheet sheet, final Set<Pair<String, String>> shopCodesAndNames) {
        final XSSFRow shopNameRow = sheet.createRow(0);
        final XSSFRow shopCodeRow = sheet.createRow(1);
        final XSSFRow productsRow = sheet.createRow(2);

        int cellIndex = 0;

        for (final Pair<String, String> shopEntry : shopCodesAndNames) {

            final XSSFCell shopNameCell = shopNameRow.createCell(cellIndex);
            shopNameCell.setCellValue(shopEntry.getRight());

            final XSSFCell shopCodeCell = shopCodeRow.createCell(cellIndex);
            shopCodeCell.setCellValue(shopEntry.getLeft());

            for (final String column : COLUMNS) {
                final XSSFCell cell = productsRow.createCell(cellIndex++);
                cell.setCellValue(column);
            }
        }
    }

    protected int writeData(final XSSFSheet sheet, final Set<Pair<String, String>> shopCodesAndNames,
            final Map<String, Map<String, Pair<Integer, Integer>>> data) {
        final int maxRownum = HEADER_SIZE_IN_ROWS;

        int columnIndex = 0;
        final Map<Integer, XSSFRow> rows = new HashMap<>();
        for (final Entry<String, Map<String, Pair<Integer, Integer>>> e : data.entrySet()) {
            int rownum = HEADER_SIZE_IN_ROWS;
            // final String shopName = e.getKey();
            final Map<String, Pair<Integer, Integer>> productsMap = e.getValue();
            for (final Entry<String, Pair<Integer, Integer>> p : productsMap.entrySet()) {
                final String productCode = p.getKey();
                final Integer moq = p.getValue().getLeft();
                final Integer stock = p.getValue().getRight();
                LOG.debug("productCode: {}, columnIndex: {}, rownum: {}", productCode, columnIndex, rownum);
                final XSSFRow row = getOrCreateRow(sheet, rows, rownum);
                row.createCell(columnIndex).setCellValue(productCode);
                row.createCell(columnIndex + 1).setCellValue(moq);
                row.createCell(columnIndex + 2).setCellValue(stock);
                rownum++;
            }
            sheet.autoSizeColumn(columnIndex, true);
            sheet.autoSizeColumn(columnIndex + 1, true);
            sheet.autoSizeColumn(columnIndex + 2, true);
            columnIndex += HEADER_SIZE_IN_ROWS;
        }
        return maxRownum;
    }

    private void addStyle(final XSSFSheet sheet, final Map<String, Map<String, Pair<Integer, Integer>>> data,
            final Set<Pair<String, String>> shopCodesAndNames) {

        final Iterator<Pair<String, String>> iterator = shopCodesAndNames.iterator();

        for (int i = 0; i < shopCodesAndNames.size(); i++) {
            final Pair<String, String> shopCodesAndName = iterator.next();
            final CellRangeAddress shopRegion = new CellRangeAddress( //
                    0, // firstRow
                    HEADER_SIZE_IN_ROWS + data.get(shopCodesAndName.getRight()).size() - 1, // lastRow
                    COLUMNS.size() * i, // firstCol
                    COLUMNS.size() * (i + 1) - 1); // lastCol
            LOG.debug("Applying border to: {}", shopRegion.formatAsString());
            applyBorderStyle(sheet, shopRegion, BorderStyle.THICK);
        }

    }

    protected XSSFRow getOrCreateRow(final XSSFSheet sheet, final Map<Integer, XSSFRow> rows, final int rownum) {
        XSSFRow row = rows.get(rownum);
        if (row == null) {
            row = sheet.createRow(rownum);
            rows.put(rownum, row);
        }
        return row;
    }

    @Override
    protected String getReportFileName() {
        return REPORT_FILE_NAME;
    }

    public DistMOQOverStockReportDao getDistMOQOverStockReportDao() {
        return distMOQOverStockReportDao;
    }

    public void setDistMOQOverStockReportDao(final DistMOQOverStockReportDao distMOQOverStockReportDao) {
        this.distMOQOverStockReportDao = distMOQOverStockReportDao;
    }


}
