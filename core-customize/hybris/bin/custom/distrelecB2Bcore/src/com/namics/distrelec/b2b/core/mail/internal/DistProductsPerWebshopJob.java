package com.namics.distrelec.b2b.core.mail.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;


import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.model.jobs.DistProductsPerWebshopCronjobModel;
import com.namics.hybris.toolbox.FileUtils;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class DistProductsPerWebshopJob<T extends DistProductsPerWebshopCronjobModel> extends AbstractReportJob<T> {

    private static final String EXPORT_LOCATION_PROPERTY_NAME = "DistProductsPerWebshopJob.export.location";

    private static final Logger LOG = LogManager.getLogger(DistProductsPerWebshopJob.class);

    @Autowired
    private DistProductsPerWebshopDao distProductsPerWebshopDao;

    private static final String REPORT_FILE_NAME = "ProductsPerWebshops.xlsx";
    private static final String TITLE_IS_BUYABLE = "Buyable";
    private static final String TITLE_IS_NOT_BUYABLE = "Not Buyable";
    private static final String VOLUME = "Volume";
    private static final String DIFFERENCE = "Difference";
    private static final String RATIO = "%";
    private static final String TOTAL = "Total";

    private static final int HEADER_SIZE_IN_ROWS = 4;

    @Override
    public PerformResult perform(final T productPerWebshopCronJob) {
        boolean result = false;

        // Open file (reusing last created file)
        File file;
        try {
            file = getXlsxFile();
        } catch (IOException e1) {
            LOG.error(e1,e1);
            return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
        }

        try {
            // Read Old Values
            MultiKeyMap<MultiKey, Integer> oldValuesMultiKeyMap = readOldValues(file);

            // Get New Data
            List<List<String>> productsPerShop = getDistProductsPerWebshopDao().getQueryResult();
            Map<String, Map<String, Map<Boolean, Integer>>> newValuesNestedMap = getNewData(productsPerShop);

            // Write New Data
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = getSheet(workbook);

            Set<Pair<String, String>> shopCodesAndNames = getShopCodesAndNames(productsPerShop);
            writeHeader(sheet, shopCodesAndNames);

            int rownum = writeData(sheet, shopCodesAndNames, newValuesNestedMap, oldValuesMultiKeyMap);

            // CALCULATE TOTALS
            writeTotals(sheet, rownum, shopCodesAndNames.size());

            addStyle(sheet, newValuesNestedMap.size(), shopCodesAndNames.size());
            sheet.autoSizeColumn(0, true);

            // Write the workbook in file system
            writeFileToFilesystem(workbook, file);

            // Send Email
            result = sendEmail(productPerWebshopCronJob, file);

        } catch (InvalidFormatException | IOException e) {
            LOG.error(new ParameterizedMessage("{} for file {}", e.getMessage(), file.getAbsolutePath()), e);
        }

        return new PerformResult(result ? CronJobResult.SUCCESS : CronJobResult.ERROR, CronJobStatus.FINISHED);
    }
    
    @Override
    protected String getReportFileName() {
        return REPORT_FILE_NAME;
    }

    private void addStyle(XSSFSheet sheet, int categoriesCount, int shopsCount) {

        LOG.debug("categoriesCount: {}, shopsCount: {}", categoriesCount, shopsCount);

        for (int i = 0; i < shopsCount; i++) {
            CellRangeAddress buyableRegion = new CellRangeAddress(2, HEADER_SIZE_IN_ROWS + categoriesCount - 1, 6 * i + 1, 6 * i + 3);
            applyBorderStyle(sheet, buyableRegion, BorderStyle.THIN);

            CellRangeAddress nonBuyableRegion = new CellRangeAddress(2, HEADER_SIZE_IN_ROWS + categoriesCount - 1, 6 * i + 4, 6 * i + 6);
            applyBorderStyle(sheet, nonBuyableRegion, BorderStyle.THIN);

            CellRangeAddress shopRegion = new CellRangeAddress(0, HEADER_SIZE_IN_ROWS + categoriesCount - 1, 6 * i + 1, 6 * i + 6);
            applyBorderStyle(sheet, shopRegion, BorderStyle.THICK);
        }

        CellRangeAddress totalRegion = new CellRangeAddress(HEADER_SIZE_IN_ROWS + categoriesCount + 1, HEADER_SIZE_IN_ROWS + categoriesCount + 1, 0,
                shopsCount * 6);
        applyBorderStyle(sheet, totalRegion, BorderStyle.THICK);

        CellRangeAddress categoryNamesRegion = new CellRangeAddress(HEADER_SIZE_IN_ROWS, HEADER_SIZE_IN_ROWS + categoriesCount - 1, 0, 0);
        applyBorderStyle(sheet, categoryNamesRegion, BorderStyle.THICK);

    }

    protected Map<String, Map<String, Map<Boolean, Integer>>> getNewData(List<List<String>> productsPerShop) {
        Map<String, Map<String, Map<Boolean, Integer>>> newValuesNestedMap = new LinkedHashMap<>(); // Keep insertion Order
        productsPerShop.stream().forEachOrdered(l -> {
            Assert.notNull(l);
            String webshopName = l.get(1);
            String categoryName = l.get(2);
            boolean buyable = "1".equals(l.get(3));
            Integer count = Integer.valueOf(l.get(4));
            Map<String, Map<Boolean, Integer>> categoryMap = Optional.ofNullable(newValuesNestedMap.get(categoryName)).orElse(new LinkedHashMap<>());
            Map<Boolean, Integer> categoryShopMap = Optional.ofNullable(categoryMap.get(webshopName)).orElse(new LinkedHashMap<>());
            Assert.isTrue(!categoryShopMap.containsKey(buyable));
            categoryShopMap.put(buyable, count);
            categoryMap.put(webshopName, categoryShopMap);
            newValuesNestedMap.put(categoryName, categoryMap);
        });
        LOG.debug("newValuesNestedMap: {}", newValuesNestedMap);
        return newValuesNestedMap;
    }

    protected MultiKeyMap<MultiKey, Integer> readOldValues(File file) throws IOException, InvalidFormatException, FileNotFoundException {
        MultiKeyMap<MultiKey, Integer> oldValuesMultiKeyMap = new MultiKeyMap<>();
        if (file.exists()) {

            XSSFWorkbook workbook = (XSSFWorkbook) WorkbookFactory.create(new FileInputStream(file));
            XSSFSheet sheet = workbook.getSheetAt(0);
            List<String> shopNames = parseHeader(sheet);
            Iterator<Row> rowIterator = sheet.rowIterator();
            for (int i = 0; i < HEADER_SIZE_IN_ROWS; i++) { // Skip header
                rowIterator.next();
            }
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                Cell cell = cellIterator.next();
                String categoryName = cell.getStringCellValue();
                for (String shopName : shopNames) {
                    int countBuyable = getOldValue(cellIterator);
                    int countNonBuyable = getOldValue(cellIterator);
                    oldValuesMultiKeyMap.put(new MultiKey(categoryName, shopName, true), countBuyable);
                    oldValuesMultiKeyMap.put(new MultiKey(categoryName, shopName, false), countNonBuyable);
                }
            }
            LOG.info("oldValuesMultiKeyMap: {}", oldValuesMultiKeyMap);
            LOG.info("{} entries read from {}", oldValuesMultiKeyMap.size(), file.getAbsolutePath());
        } else {
            LOG.error("file {} not found. Comparison to previous day will not be done", file.getAbsolutePath());
        }
        return oldValuesMultiKeyMap;
    }

    protected int writeData(XSSFSheet sheet, Set<Pair<String, String>> shopCodesAndNames, Map<String, Map<String, Map<Boolean, Integer>>> newValuesNestedMap,
            MultiKeyMap<MultiKey, Integer> oldValuesMultiKeyMap) {
        int rownum = HEADER_SIZE_IN_ROWS;
        Set<Entry<String, Map<String, Map<Boolean, Integer>>>> categoryEntrySet = newValuesNestedMap.entrySet();
        
        CellStyle percentageStyle = sheet.getWorkbook().createCellStyle();
        percentageStyle.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat("0.00%"));
        
        for (Entry<String, Map<String, Map<Boolean, Integer>>> categoryEntry : categoryEntrySet) {
            String categoryName = categoryEntry.getKey();
            LOG.debug("Creating row {} for Category {}", rownum, categoryName);
            XSSFRow row = sheet.createRow(rownum);
            int cellNum = 0;
            row.createCell(cellNum++).setCellValue(categoryName);
            Map<String, Map<Boolean, Integer>> categoryShopMap = categoryEntry.getValue();
            for (Pair<String, String> shop : shopCodesAndNames) {
                String shopName = shop.getRight();
                Map<Boolean, Integer> buyableMap = categoryShopMap.get(shopName);
                // Buyable
                {
                    LOG.debug("Creating Buyable block on row {}, first cell {} for Category {} and shop {}", rownum, cellNum, categoryName, shopName);
                    Integer newVolume = (buyableMap == null || buyableMap.get(true) == null) ? Integer.valueOf(0) : buyableMap.get(true);
                    XSSFCell newVolumeCell = row.createCell(cellNum++);
                    newVolumeCell.setCellValue(newVolume); // Volume
                    Integer oldVolume = Optional.ofNullable(oldValuesMultiKeyMap.get(categoryName, shopName, true)).orElse(Integer.valueOf(0));
                    row.createCell(cellNum++).setCellValue(newVolume - oldVolume);
                    XSSFCell buyableRatioCell = row.createCell(cellNum++);
                    buyableRatioCell.setCellFormula(createRatioFormula(newVolumeCell.getColumnIndex(), newVolumeCell.getRowIndex()));
                    buyableRatioCell.setCellStyle(percentageStyle);
                }
                // Not Buyable
                {
                    LOG.debug("Creating Non-Buyable block on row {}, first cell {} for Category {} and shop {}", rownum, cellNum, categoryName, shopName);
                    Integer newVolume = (buyableMap == null || buyableMap.get(false) == null) ? Integer.valueOf(0) : buyableMap.get(false);
                    XSSFCell newVolumeCell = row.createCell(cellNum++);
                    newVolumeCell.setCellValue(newVolume); // Volume
                    Integer oldVolume = Optional.ofNullable(oldValuesMultiKeyMap.get(categoryName, shopName, false)).orElse(Integer.valueOf(0));
                    row.createCell(cellNum++).setCellValue(newVolume - oldVolume);
                    XSSFCell notBuyableRatioCell = row.createCell(cellNum++);
                    notBuyableRatioCell.setCellFormula(createRatioFormula(newVolumeCell.getColumnIndex(), newVolumeCell.getRowIndex()));
                    notBuyableRatioCell.setCellStyle(percentageStyle);
                }
            }
            rownum++;
        }
        return rownum;
    }

    protected void writeTotals(XSSFSheet sheet, int rownum, int shopsAmount) {
        rownum++; // Leave an empty line
        XSSFRow totalsRow = sheet.createRow(rownum);
        totalsRow.createCell(0).setCellValue(TOTAL);
        // 2 for buyable and not-buyable. 3 for volume, difference and ratio
        for (int colNum = 1; colNum <= (2 * 3 * shopsAmount);) {
            // Total Volume
            String totalValueformula = createTotalFormula("SUM", colNum, rownum);
            LOG.debug("totalValueformula {}", totalValueformula);
            XSSFCell totalValueCell = totalsRow.createCell(colNum);
            totalValueCell.setCellFormula(totalValueformula);
            colNum++;

            // Total Difference
            String totalDifferenceformula = createTotalFormula("SUM", colNum, rownum);
            LOG.debug("totalDifferenceformula {}", totalDifferenceformula);
            totalsRow.createCell(colNum).setCellFormula(totalDifferenceformula);
            colNum++;

            // Average Ratio
            String avgRatioformula = createRatioFormula(totalValueCell.getColumnIndex(), totalValueCell.getRowIndex());
            LOG.debug("avgRatioformula {}", avgRatioformula);
            XSSFCell avgRatioCell = totalsRow.createCell(colNum);
            avgRatioCell.setCellFormula(avgRatioformula);
            CellStyle percentageStyle = sheet.getWorkbook().createCellStyle();
            percentageStyle.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat("0.00%"));
            avgRatioCell.setCellStyle(percentageStyle);
            colNum++;

        }
    }

    protected String createTotalFormula(String operation, int colNum, int rownum) {
        return String.format("%s(%s%d:%s%d)", operation, CellReference.convertNumToColString(colNum), HEADER_SIZE_IN_ROWS + 1,
                CellReference.convertNumToColString(colNum), rownum - 1);
    }
    
    protected String createRatioFormula(int volumeColNum, int rownum) {
        return String.format("IF((%s%d-%s%d)=0, MIN(%s%d,1), %s%d/(%s%d-%s%d))", 
                CellReference.convertNumToColString(volumeColNum), rownum+1, CellReference.convertNumToColString(volumeColNum+1), rownum+1,
                CellReference.convertNumToColString(volumeColNum), rownum+1, CellReference.convertNumToColString(volumeColNum+1), rownum+1,
                CellReference.convertNumToColString(volumeColNum), rownum+1, CellReference.convertNumToColString(volumeColNum+1), rownum+1);
    }

    protected File getXlsxFile() throws IOException {
        String exportFolderString = getConfigurationService().getConfiguration().getString(EXPORT_LOCATION_PROPERTY_NAME);
        FileUtils.checkAndCreateDirectory(exportFolderString);
        String absolutFilePath = FileUtils.concatDirectoryAndFilename(exportFolderString, REPORT_FILE_NAME);
        File file = new File(absolutFilePath);
        return file;
    }

    protected List<String> parseHeader(XSSFSheet sheet) {
        List<String> shopNames = new ArrayList<>();
        XSSFRow shopsRow = sheet.getRow(0);
        int cellIndex = 1;
        while (true) {
            XSSFCell shopCell = shopsRow.getCell(cellIndex);
            if (shopCell == null) {
                break;
            }
            String shopName = shopCell.getStringCellValue();
            if (StringUtils.isEmpty(shopName)) {
                break;
            }
            shopNames.add(shopName);
            cellIndex = cellIndex + 6;
        }
        LOG.debug("parseHeader - shopNames: {}", Arrays.toString(shopNames.toArray()));
        return shopNames;
    }

    protected void writeHeader(XSSFSheet sheet, Set<Pair<String, String>> shopCodesAndNames) {
        XSSFRow shopNameRow = sheet.createRow(0);
        XSSFRow shopCodeRow = sheet.createRow(1);
        XSSFRow buyableRow = sheet.createRow(2);
        XSSFRow volDiffRatioRow = sheet.createRow(3);

        int cellIndex = 1;

        for (Pair<String, String> shopEntry : shopCodesAndNames) {
            XSSFCell shopNameCell = shopNameRow.createCell(cellIndex);
            shopNameCell.setCellValue(shopEntry.getRight());
            sheet.addMergedRegion(new CellRangeAddress(shopNameCell.getRowIndex(), shopNameCell.getRowIndex(), shopNameCell.getColumnIndex(),
                    shopNameCell.getColumnIndex() + 5));

            XSSFCell shopCodeCell = shopCodeRow.createCell(cellIndex);
            shopCodeCell.setCellValue(shopEntry.getLeft());
            sheet.addMergedRegion(new CellRangeAddress(shopCodeCell.getRowIndex(), shopCodeCell.getRowIndex(), shopCodeCell.getColumnIndex(),
                    shopCodeCell.getColumnIndex() + 5));

            XSSFCell buyableCell = buyableRow.createCell(cellIndex);
            buyableCell.setCellValue(TITLE_IS_BUYABLE);
            sheet.addMergedRegion(
                    new CellRangeAddress(buyableCell.getRowIndex(), buyableCell.getRowIndex(), buyableCell.getColumnIndex(), buyableCell.getColumnIndex() + 2));

            XSSFCell notBuyableCell = buyableRow.createCell(cellIndex + 3);
            notBuyableCell.setCellValue(TITLE_IS_NOT_BUYABLE);
            sheet.addMergedRegion(new CellRangeAddress(notBuyableCell.getRowIndex(), notBuyableCell.getRowIndex(), notBuyableCell.getColumnIndex(),
                    notBuyableCell.getColumnIndex() + 2));

            XSSFCell volumeBuyableCell = volDiffRatioRow.createCell(cellIndex);
            volumeBuyableCell.setCellValue(VOLUME);

            XSSFCell differenceBuyableCell = volDiffRatioRow.createCell(cellIndex + 1);
            differenceBuyableCell.setCellValue(DIFFERENCE);

            XSSFCell ratioBuyableCell = volDiffRatioRow.createCell(cellIndex + 2);
            ratioBuyableCell.setCellValue(RATIO);

            XSSFCell volumeNotBuyableCell = volDiffRatioRow.createCell(cellIndex + 3);
            volumeNotBuyableCell.setCellValue(VOLUME);

            XSSFCell differenceNotBuyableCell = volDiffRatioRow.createCell(cellIndex + 4);
            differenceNotBuyableCell.setCellValue(DIFFERENCE);

            XSSFCell ratioNotBuyableCell = volDiffRatioRow.createCell(cellIndex + 5);
            ratioNotBuyableCell.setCellValue(RATIO);
            cellIndex += 6;
        }
    }

    private int getOldValue(Iterator<Cell> cellIterator) {
        try {
            Integer count = Double.valueOf(cellIterator.next().getNumericCellValue()).intValue();
            cellIterator.next(); // Skip Difference
            cellIterator.next(); // Skip %
            return count;
        } catch (NoSuchElementException e) {
            LOG.warn(e.getClass().getName());
            return 0;
        }
    }

    public DistProductsPerWebshopDao getDistProductsPerWebshopDao() {
        return distProductsPerWebshopDao;
    }

    public void setDistProductsPerWebshopDao(DistProductsPerWebshopDao distProductsPerWebshopDao) {
        this.distProductsPerWebshopDao = distProductsPerWebshopDao;
    }

}
