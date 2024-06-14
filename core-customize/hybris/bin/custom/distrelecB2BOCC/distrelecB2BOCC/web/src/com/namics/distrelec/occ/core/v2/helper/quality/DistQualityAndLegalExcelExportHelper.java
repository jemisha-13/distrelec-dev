package com.namics.distrelec.occ.core.v2.helper.quality;

import static com.namics.distrelec.occ.core.v2.util.DistCustomerAddressUtil.getCustomerAddress;
import static com.namics.distrelec.occ.core.v2.util.ExcelImageUtil.EXPAND_COLUMN;
import static com.namics.distrelec.occ.core.v2.util.ExcelImageUtil.addImageToSheet;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.poi.common.usermodel.HyperlinkType.URL;
import static org.apache.poi.ss.usermodel.BorderStyle.THICK;
import static org.apache.poi.ss.usermodel.BorderStyle.THIN;
import static org.apache.poi.ss.usermodel.IndexedColors.BLACK;
import static org.apache.poi.ss.util.RegionUtil.setBorderBottom;
import static org.apache.poi.ss.util.RegionUtil.setBorderLeft;
import static org.apache.poi.ss.util.RegionUtil.setBorderRight;
import static org.apache.poi.ss.util.RegionUtil.setBorderTop;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.occ.core.qualityandlegal.ws.dto.QualityAndLegalDownloadWsDTO;
import com.namics.distrelec.occ.core.v2.util.ExcelImageUtil.ImageCreationDetails;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.servicelayer.i18n.I18NService;

@Component
public class DistQualityAndLegalExcelExportHelper extends DistQualityAndLegalBaseHelper {

    private static final int SVHC_MAX_LENGTH = 380;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18nService;

    @Autowired
    @Qualifier("b2bCustomerFacade")
    private DistCustomerFacade b2bCustomerFacade;

    public XSSFWorkbook generateEnvironmentalInformationXlsReport(QualityAndLegalDownloadWsDTO qualityAndLegalDownloadWsDTO) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        String reportName = messageSource.getMessage("qualityAndLegal.excel.export.sheet.name", null, "Report", i18nService.getCurrentLocale());
        Sheet sheet = workbook.createSheet(isNotBlank(reportName) ? reportName : "Report");
        MutableInt rowId = new MutableInt(0);

        // Empty row
        defineFirstEmptyRow(sheet, rowId);

        // Header (title and logo)
        rowId.increment();
        defineHeader(workbook, sheet, rowId);

        // Customer Details
        rowId.increment();
        defineCustomerDetails(workbook, sheet, rowId,  qualityAndLegalDownloadWsDTO);

        // Article Details
        rowId.increment();
        defineArticleDetails(workbook, sheet, rowId, getProductsForReport(qualityAndLegalDownloadWsDTO.getProductCodes()));

        // Disclaimer
        rowId.increment();
        defineDisclaimerText(workbook, sheet, rowId);

        // Report date
        rowId.increment();
        defineReportDate(workbook, sheet, rowId);

        // Add thick borders around all outer cells
        addOuterThickBorders(sheet, rowId);

        // Resize columns
        autoSizeColumns(workbook);
        return workbook;
    }

    private CellStyle getDefaultCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setTopBorderColor(BLACK.index);
        cellStyle.setBottomBorderColor(BLACK.index);
        cellStyle.setLeftBorderColor(BLACK.index);
        cellStyle.setRightBorderColor(BLACK.index);

        return cellStyle;
    }

    private void defineFirstEmptyRow(Sheet sheet, MutableInt rowId) {
        // The actual first row and the first column (with index 0) are empty (as per the template in https://jira.distrelec.com/browse/DISTRELEC-23740)
        Row emptyFirstRow = sheet.createRow(rowId.intValue());
        emptyFirstRow.setHeightInPoints((float) 16.00);
    }

    private void defineHeader(XSSFWorkbook workbook, Sheet sheet, MutableInt rowId) throws IOException {
        Row row = sheet.createRow(rowId.intValue());
        row.setHeightInPoints((float) 63.00);
        CellStyle cellStyle = getDefaultCellStyle(workbook);
        cellStyle.setFont(getFontForHeader(workbook));
        cellStyle.setWrapText(true);
        setAlignmentForCellStyle(cellStyle, VerticalAlignment.BOTTOM, HorizontalAlignment.CENTER);

        Cell cell = row.createCell(1);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.header.title",
                                                   null,
                                                   "Distrelec Article Environmental Compliance Report \n" +
                                                         "RoHS 2011/65/EU (RoHS 2015/863/EU) | REACH 1907/2006/EC |\"SCIP\" EU Waste Framework Directive (EU)2018/851",
                                                   i18nService.getCurrentLocale()));

        int excelRow = rowId.intValue() + 1; // because rowId starts the count from 0, but Excel counts rows from 1
        CellRangeAddress region = CellRangeAddress.valueOf("B" + excelRow + ":L" + excelRow);
        sheet.addMergedRegion(region);
        addBottomBordersForRegion(sheet, THICK, region);

        addDistrelecLogo(sheet);
    }

    private void defineCustomerDetails(XSSFWorkbook workbook, Sheet sheet, MutableInt rowId, QualityAndLegalDownloadWsDTO qualityAndLegalDownloadWsDTO) {
        CustomerData currentCustomer = b2bCustomerFacade.getCurrentCustomer();

        // Subtitle
        Row row = sheet.createRow(rowId.intValue());
        row.setHeightInPoints(31);
        CellStyle cellStyleForSubtitle = getDefaultCellStyle(workbook);
        cellStyleForSubtitle.setFont(getFontForSubtitles(workbook));
        setAlignmentForCellStyle(cellStyleForSubtitle, VerticalAlignment.BOTTOM, HorizontalAlignment.CENTER);
        addBottomBordersForCellStyle(cellStyleForSubtitle);

        Cell cell = row.createCell(1);
        cell.setCellStyle(cellStyleForSubtitle);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.customerDetails.title",
                                                   null,
                                                   "Customer Details",
                                                   i18nService.getCurrentLocale()));

        int excelRow = rowId.intValue() + 1; // because rowId starts the count from 0, but Excel counts rows from 1
        CellRangeAddress region = CellRangeAddress.valueOf("B" + excelRow + ":L" + excelRow);
        sheet.addMergedRegion(region);
        addBottomBordersForRegion(sheet, THIN, region);

        // Customer name and customer number
        rowId.increment();
        int startingMergeRow = rowId.intValue(); // this is needed to know from which row to which row we need to merge
        row = sheet.createRow(rowId.intValue());
        row.setHeightInPoints(37);
        CellStyle cellStyleForData = getDefaultCellStyle(workbook);
        cellStyleForData.setFont(getFontForDataColumns(workbook));
        setAlignmentForCellStyle(cellStyleForData, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);
        addAllBordersForCellStyle(cellStyleForData);
        CellStyle cellStyleForDataBold = getDefaultCellStyle(workbook);
        cellStyleForDataBold.setFont(getFontForDataColumnsBold(workbook));
        setAlignmentForCellStyle(cellStyleForDataBold, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);
        addAllBordersForCellStyle(cellStyleForDataBold);

        cell = row.createCell(1);
        cell.setCellStyle(cellStyleForDataBold);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.customerDetails.customerName",
                                                   null,
                                                   "Customer Name:",
                                                   i18nService.getCurrentLocale()));

        cell = row.createCell(2);
        cell.setCellStyle(cellStyleForData);
        cell.setCellValue(getCustomerName(currentCustomer, qualityAndLegalDownloadWsDTO.getCustomerName()));
        excelRow = rowId.intValue() + 1; // because rowId starts the count from 0, but Excel counts rows from 1
        sheet.addMergedRegion(CellRangeAddress.valueOf("C" + excelRow + ":D" + excelRow));

        cell = row.createCell(4);
        cell.setCellStyle(cellStyleForDataBold);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.customerDetails.customerNumber",
                                                   null,
                                                   "Customer Number:",
                                                   i18nService.getCurrentLocale()));

        cell = row.createCell(5);
        cell.setCellStyle(cellStyleForData);
        cell.setCellValue(currentCustomer.getUnit().getErpCustomerId());

        // Customer address and customer e-mail address
        rowId.increment();
        row = sheet.createRow(rowId.intValue());
        row.setHeightInPoints(47);

        cell = row.createCell(1);
        cell.setCellStyle(cellStyleForDataBold);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.customerDetails.customerAddress",
                                                   null,
                                                   "Customer Address:",
                                                   i18nService.getCurrentLocale()));

        cell = row.createCell(2);
        cell.setCellStyle(cellStyleForData);
        cell.setCellValue(getCustomerAddress(currentCustomer));
        excelRow = rowId.intValue() + 1; // because rowId starts the count from 0, but Excel counts rows from 1
        region = CellRangeAddress.valueOf("C" + excelRow + ":D" + excelRow);
        sheet.addMergedRegion(region);
        addTopBordersForRegion(sheet, region);
        addBottomBordersForRegion(sheet, THIN, region);

        cell = row.createCell(4);
        cell.setCellStyle(cellStyleForDataBold);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.customerDetails.customerEmailAddress",
                                                   null,
                                                   "Customer E-Mail Address:",
                                                   i18nService.getCurrentLocale()));

        cell = row.createCell(5);
        cell.setCellStyle(cellStyleForData);
        cell.setCellValue(currentCustomer.getEmail());

        // Big merged area
        int excelRowFrom = startingMergeRow + 1; // because rowId starts the count from 0, but Excel counts rows from 1
        int excelRowTo = rowId.intValue() + 1; // because rowId starts the count from 0, but Excel counts rows from 1
        region = CellRangeAddress.valueOf("G" + excelRowFrom + ":L" + excelRowTo);
        sheet.addMergedRegion(region);
        addTopBordersForRegion(sheet, region);

        // Thick black border at the end of Customer details (and above Article details)
        region = CellRangeAddress.valueOf("B" + excelRowFrom + ":L" + excelRowTo);
        addBottomBordersForRegion(sheet, THICK, region);
    }

    private void defineArticleDetails(XSSFWorkbook workbook, Sheet sheet, MutableInt rowId, List<ProductData> products) {
        // Subtitle
        Row row = sheet.createRow(rowId.intValue());
        row.setHeightInPoints(33);
        CellStyle cellStyleForSubtitle = getDefaultCellStyle(workbook);
        cellStyleForSubtitle.setFont(getFontForSubtitles(workbook));
        setAlignmentForCellStyle(cellStyleForSubtitle, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);

        Cell cell = row.createCell(1);
        cell.setCellStyle(cellStyleForSubtitle);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.title",
                                                   null,
                                                   "Article Details",
                                                   i18nService.getCurrentLocale()));

        int excelRow = rowId.intValue() + 1; // because rowId starts the count from 0, but Excel counts rows from 1
        CellRangeAddress region = CellRangeAddress.valueOf("B" + excelRow + ":L" + excelRow);
        sheet.addMergedRegion(region);
        addBottomBordersForRegion(sheet, THIN, region);

        // Data column names (header for article details)
        rowId.increment();
        row = sheet.createRow(rowId.intValue());
        row.setHeightInPoints(32);
        CellStyle cellStyleForDataBold = getDefaultCellStyle(workbook);
        cellStyleForDataBold.setFont(getFontForDataColumnsBold(workbook));
        setAlignmentForCellStyle(cellStyleForDataBold, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        addBottomBordersForCellStyle(cellStyleForDataBold);

        cell = row.createCell(1);
        cell.setCellStyle(cellStyleForDataBold);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.distrelecArticleNumber",
                                                   null,
                                                   "Distrelec Article Number",
                                                   i18nService.getCurrentLocale()));

        cell = row.createCell(2);
        cell.setCellStyle(cellStyleForDataBold);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.manufacturerPartNumber",
                                                   null,
                                                   "Manufacturers Part Number",
                                                   i18nService.getCurrentLocale()));

        cell = row.createCell(3);
        cell.setCellStyle(cellStyleForDataBold);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.manufacturerName",
                                                   null,
                                                   "Manufacturer Name",
                                                   i18nService.getCurrentLocale()));

        cell = row.createCell(4);
        cell.setCellStyle(cellStyleForDataBold);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.description",
                                                   null,
                                                   "Description",
                                                   i18nService.getCurrentLocale()));

        cell = row.createCell(5);
        cell.setCellStyle(cellStyleForDataBold);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.euRohsDirective",
                                                   null,
                                                   "EU RoHS Directive",
                                                   i18nService.getCurrentLocale()));

        cell = row.createCell(6);
        cell.setCellStyle(cellStyleForDataBold);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.rohsExemptions",
                                                   null,
                                                   "RoHS Exemptions",
                                                   i18nService.getCurrentLocale()));

        cell = row.createCell(7);
        cell.setCellStyle(cellStyleForDataBold);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.reachRawDate",
                                                   null,
                                                   "REACH Review Date",
                                                   i18nService.getCurrentLocale()));

        cell = row.createCell(8);
        cell.setCellStyle(cellStyleForDataBold);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.svhcAboveTreshold",
                                                   null,
                                                   "SVHC Above Threshold",
                                                   i18nService.getCurrentLocale()));

        cell = row.createCell(9);
        cell.setCellStyle(cellStyleForDataBold);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.svhcSubstance",
                                                   null,
                                                   "SVHC Substance (CAS-Nr.)",
                                                   i18nService.getCurrentLocale()));

        cell = row.createCell(10);
        cell.setCellStyle(cellStyleForDataBold);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.distrelecScipNumber",
                                                   null,
                                                   "Distrelec SCIP Number",
                                                   i18nService.getCurrentLocale()));

        cell = row.createCell(11);
        cell.setCellStyle(cellStyleForDataBold);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.manufacturersComplianceUrl",
                                                   null,
                                                   "Manufacturers Compliance URL",
                                                   i18nService.getCurrentLocale()));

        // Data columns (products list)
        for (ProductData productData : products) {
            rowId.increment();
            defineOneArticleDetailDataRow(workbook, sheet, rowId, productData);
        }

        // Thick black border at the end of Article details (and above the disclaimer)
        excelRow = rowId.intValue() + 1; // because rowId starts the count from 0, but Excel counts rows from 1
        region = CellRangeAddress.valueOf("B" + excelRow + ":L" + excelRow);
        addBottomBordersForRegion(sheet, THICK, region);
    }

    private void defineDisclaimerText(XSSFWorkbook workbook, Sheet sheet, MutableInt rowId) {
        Row row = sheet.createRow(rowId.intValue());
        row.setHeightInPoints(47);
        CellStyle cellStyleForData = getDefaultCellStyle(workbook);
        cellStyleForData.setFont(getFontForDataColumns(workbook));
        cellStyleForData.setWrapText(true);
        setAlignmentForCellStyle(cellStyleForData, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);

        Cell cell = row.createCell(1);
        cell.setCellStyle(cellStyleForData);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.disclaimer.text",
                                                   null,
                                                   "The above information we provide to you is based solely on the information presented to us, under good faith that our suppliers act within their legal requirements. \n"
                                                         +
                                                         "Distrelec is therefore in no way responsible for any damages or penalties suffered by you as a result of using or relying upon such information.\n"
                                                         +
                                                         "This document was electronically created and is valid without official signature.",
                                                   i18nService.getCurrentLocale()));

        int excelRow = rowId.intValue() + 1; // because rowId starts the count from 0, but Excel counts rows from 1
        sheet.addMergedRegion(CellRangeAddress.valueOf("B" + excelRow + ":L" + excelRow));
    }

    private void defineReportDate(XSSFWorkbook workbook, Sheet sheet, MutableInt rowId) {
        int startingMergeRow = rowId.intValue();
        Row row = sheet.createRow(rowId.intValue());
        row.setHeightInPoints(15);
        CellStyle cellStyleForDataBold = getDefaultCellStyle(workbook);
        cellStyleForDataBold.setFont(getFontForDataColumnsBold(workbook));
        setAlignmentForCellStyle(cellStyleForDataBold, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);
        CellStyle cellStyleForDataForDates = getDefaultCellStyle(workbook);
        cellStyleForDataForDates.setFont(getFontForDataColumns(workbook));
        cellStyleForDataForDates.setDataFormat(getDataFormatForDates(workbook));
        setAlignmentForCellStyle(cellStyleForDataForDates, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);

        Cell cell = row.createCell(1);
        cell.setCellStyle(cellStyleForDataBold);
        cell.setCellValue(messageSource.getMessage("qualityAndLegal.excel.export.reportDate.title",
                                                   null,
                                                   "Report Date:",
                                                   i18nService.getCurrentLocale()));

        cell = row.createCell(2);
        cell.setCellStyle(cellStyleForDataForDates);
        cell.setCellValue(Calendar.getInstance().getTime());

        int excelRowFrom = startingMergeRow + 1; // because rowId starts the count from 0, but Excel counts rows from 1
        int excelRowTo = rowId.intValue() + 2; // merge through two rows, hence the + 2
        sheet.addMergedRegion(CellRangeAddress.valueOf("B" + excelRowFrom + ":B" + excelRowTo));
        sheet.addMergedRegion(CellRangeAddress.valueOf("C" + excelRowFrom + ":L" + excelRowTo));
        rowId.add(2);
    }

    private void defineOneArticleDetailDataRow(XSSFWorkbook workbook, Sheet sheet, MutableInt rowId, ProductData productData) {
        // Data column names (header for article details)
        Row row = sheet.createRow(rowId.intValue()); // row 6 is the subtitle, row index 'i' starts the count from '1', so the next row is 6 + 1 = 7, and so on
        row.setHeightInPoints(16);
        CellStyle cellStyleForData = getDefaultCellStyle(workbook);
        cellStyleForData.setFont(getFontForDataColumns(workbook));
        cellStyleForData.setWrapText(true);
        setAlignmentForCellStyle(cellStyleForData, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        addBottomBordersForCellStyle(cellStyleForData);
        CellStyle cellStyleForDataForDates = getDefaultCellStyle(workbook);
        cellStyleForDataForDates.setFont(getFontForDataColumns(workbook));
        cellStyleForDataForDates.setDataFormat(getDataFormatForDates(workbook));
        setAlignmentForCellStyle(cellStyleForDataForDates, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        addBottomBordersForCellStyle(cellStyleForDataForDates);

        Cell cell = row.createCell(1);
        cell.setCellStyle(cellStyleForData);
        cell.setCellValue(productData.getCode());

        cell = row.createCell(2);
        cell.setCellStyle(cellStyleForData);
        cell.setCellValue(productData.getTypeName());

        cell = row.createCell(3);
        cell.setCellStyle(cellStyleForData);
        String manufacturerName = EMPTY;
        if (productData.getDistManufacturer() != null && productData.getDistManufacturer().getName() != null) {
            manufacturerName = productData.getDistManufacturer().getName();
        }
        cell.setCellValue(manufacturerName);

        cell = row.createCell(4);
        cell.setCellStyle(cellStyleForData);
        cell.setCellValue(constructProductDescription(productData));

        cell = row.createCell(5);
        cell.setCellStyle(cellStyleForData);
        cell.setCellValue(productData.getRohs());

        cell = row.createCell(6);
        cell.setCellStyle(cellStyleForData);
        cell.setCellValue(productData.getRohsExemptionsFormatted());

        cell = row.createCell(7);
        cell.setCellStyle(cellStyleForDataForDates);
        cell.setCellValue(productData.getSvhcReviewDate());

        cell = row.createCell(8);
        cell.setCellStyle(cellStyleForData);
        cell.setCellValue(getHasSvhcString(productData));

        cell = row.createCell(9);
        cell.setCellStyle(cellStyleForData);
        String svhc = productData.getSvhc();
        cell.setCellValue(svhc);
        if (isNotBlank(svhc) && svhc.length() > SVHC_MAX_LENGTH) {
            row.setHeight((short) -1);
        }

        cell = row.createCell(10);
        cell.setCellStyle(cellStyleForData);
        cell.setCellValue(productData.getScip());

        cell = row.createCell(11);
        cell.setCellStyle(cellStyleForData);
        if (isNotBlank(productData.getSvhcURL())) {
            cell.setCellValue(productData.getSvhcURL());
            Hyperlink link = getHyperlink(workbook, productData.getSvhcURL());
            cell.setHyperlink(link);
        }
    }

    private String getHasSvhcString(ProductData productData) {
        if (productData == null || productData.getHasSvhc() == null) {
            return "N/A";
        } else {
            return productData.getHasSvhc() ? "Yes" : "No";
        }
    }

    private void addDistrelecLogo(Sheet sheet) throws IOException {
        java.net.URL imageUrl = this.getClass().getClassLoader().getResource("../../_ui/all/media/distrelec_logo_ql_xlsx_export.png");
        if (imageUrl == null) {
            return;
        }

        ImageCreationDetails imageCreationDetails = new ImageCreationDetails();
        imageCreationDetails.setColNumber(9);
        imageCreationDetails.setRowNumber(1);
        imageCreationDetails.setSheet(sheet);
        imageCreationDetails.setImageFile(imageUrl);
        imageCreationDetails.setReqImageWidthMM(30);
        imageCreationDetails.setReqImageHeightMM(8.5);
        imageCreationDetails.setResizeBehaviour(EXPAND_COLUMN);
        imageCreationDetails.setDx1(1000);
        imageCreationDetails.setDy1(10);
        imageCreationDetails.setDx2(5000);
        imageCreationDetails.setDy2(10);

        addImageToSheet(imageCreationDetails);
    }

    private void addOuterThickBorders(Sheet sheet, MutableInt rowId) {
        CellRangeAddress region = CellRangeAddress.valueOf("B2:L" + rowId.intValue());
        addAllBordersForRegion(sheet, region);
    }

    private void addBottomBordersForRegion(Sheet sheet, BorderStyle borderStyle, CellRangeAddress region) {
        setBorderBottom(borderStyle, region, sheet);
    }

    private void addTopBordersForRegion(Sheet sheet, CellRangeAddress region) {
        setBorderTop(THIN, region, sheet);
    }

    private void addAllBordersForRegion(Sheet sheet, CellRangeAddress region) {
        setBorderTop(THICK, region, sheet);
        setBorderBottom(THICK, region, sheet);
        setBorderLeft(THICK, region, sheet);
        setBorderRight(THICK, region, sheet);
    }

    private void setAlignmentForCellStyle(CellStyle cellStyle, VerticalAlignment verticalAlignment, HorizontalAlignment horizontalAlignment) {
        cellStyle.setVerticalAlignment(verticalAlignment);
        cellStyle.setAlignment(horizontalAlignment);
    }

    private void addBottomBordersForCellStyle(CellStyle cellStyle) {
        cellStyle.setBorderBottom(BorderStyle.THIN);
    }

    private void addAllBordersForCellStyle(CellStyle cellStyle) {
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
    }

    private XSSFFont getDefaultFont(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setFontName("Calibri");
        font.setColor(BLACK.index);
        return font;
    }

    private XSSFFont getFontForDataColumns(XSSFWorkbook workbook) {
        XSSFFont font = getDefaultFont(workbook);
        font.setFontHeightInPoints((short) 11);
        return font;
    }

    private XSSFFont getFontForDataColumnsBold(XSSFWorkbook workbook) {
        XSSFFont font = getFontForDataColumns(workbook);
        font.setBold(true);
        return font;
    }

    private XSSFFont getFontForHeader(XSSFWorkbook workbook) {
        XSSFFont font = getDefaultFont(workbook);
        font.setFontHeightInPoints((short) 22);
        font.setBold(true);
        return font;
    }

    private XSSFFont getFontForSubtitles(XSSFWorkbook workbook) {
        XSSFFont font = getDefaultFont(workbook);
        font.setFontHeightInPoints((short) 18);
        font.setBold(true);
        return font;
    }

    private short getDataFormatForDates(XSSFWorkbook workbook) {
        return workbook.getCreationHelper().createDataFormat().getFormat("MM-dd-yyyy");
    }

    private XSSFHyperlink getHyperlink(XSSFWorkbook workbook, String url) {
        XSSFHyperlink link = workbook.getCreationHelper().createHyperlink(URL);
        link.setAddress(url);
        return link;
    }

    private String constructProductDescription(final ProductData productData) {
        final String productName = isNotBlank(productData.getName()) ? productData.getName() : EMPTY;

        final String mpn = productData.getTypeName();
        final String manufacturerName = productData.getDistManufacturer() != null ? productData.getDistManufacturer().getName() : EMPTY;
        final StringBuilder result = new StringBuilder();
        if (isNotBlank(mpn) && !containsIgnoreCase(productName, mpn)) {
            result.append(mpn).append(" - ");
        }
        result.append(productName);
        if (isNotBlank(manufacturerName) && !containsIgnoreCase(productName, manufacturerName)) {
            result.append(", ").append(manufacturerName);
        }
        return result.toString();
    }

}
