package com.namics.distrelec.occ.core.v2.helper.quality;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.poi.ss.usermodel.Font.DEFAULT_CHARSET;
import static org.apache.poi.ss.usermodel.IndexedColors.BLACK;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import de.hybris.platform.servicelayer.i18n.I18NService;

@Component
public class DistQualityAndLegalTemplateDownloadHelper extends DistQualityAndLegalBaseHelper {
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18nService;

    public XSSFWorkbook generateFileUploadTemplate() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        String sheetName = messageSource.getMessage("qualityAndLegal.template.import.sheet.name", null, "import", i18nService.getCurrentLocale());
        Sheet sheet = workbook.createSheet(isNotBlank(sheetName) ? sheetName : "import");

        // Header
        defineHeader(workbook, sheet);

        // Resize columns
        autoSizeColumns(workbook);
        return workbook;
    }

    private void defineHeader(XSSFWorkbook workbook, Sheet sheet) {
        Row row = sheet.createRow(0);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(getFontForHeader(workbook));
        cellStyle.setWrapText(false);
        cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);

        Cell cell1 = row.createCell(0);
        cell1.setCellStyle(cellStyle);
        cell1.setCellValue(messageSource.getMessage("qualityAndLegal.template.import.header.cell1.title", null, "Elfa/Distrelec Article Number",
                                                    i18nService.getCurrentLocale()));

        Cell cell2 = row.createCell(1);
        cell2.setCellStyle(cellStyle);
        cell2.setCellValue(messageSource.getMessage("qualityAndLegal.template.import.header.cell2.title", null, "Your Reference",
                                                    i18nService.getCurrentLocale()));
    }

    private XSSFFont getFontForHeader(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setColor(BLACK.index);
        font.setFontHeightInPoints((short) 11);
        font.setBold(false);
        font.setCharSet(DEFAULT_CHARSET);
        return font;
    }

}
