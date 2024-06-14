package com.namics.distrelec.b2b.facades.legal.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.namics.distrelec.b2b.facades.legal.DistQualityAndLegalInvalidFileUploadException;
import com.namics.distrelec.b2b.facades.legal.DistQualityAndLegalParser;

import static com.namics.distrelec.b2b.facades.legal.DistQualityAndLegalInvalidFileUploadException.Reason.FILE_CONTENT_BLANK;
import static com.namics.distrelec.b2b.facades.legal.DistQualityAndLegalInvalidFileUploadException.Reason.FILE_EXTENSION_UNSUPPORTED;
import static com.namics.distrelec.b2b.facades.legal.DistQualityAndLegalInvalidFileUploadException.Reason.FILE_LIST_LIMIT_EXCEEDED;
import static org.apache.commons.collections4.CollectionUtils.*;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import static org.apache.poi.ss.usermodel.CellType.BLANK;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;

public class DefaultDistQualityAndLegalParser implements DistQualityAndLegalParser {

    private static final String DOT = ".";

    private static final String CSV = "csv";

    private static final String TXT = "txt";

    private static final String XLS = "xls";

    private static final String XLSX = "xlsx";

    private static final int LIST_SIZE_LIMIT = 200;

    public List<String> getProductCodesFromFile(String filename) throws DistQualityAndLegalInvalidFileUploadException, IOException {
        List<String> productCodes;

        final String extension = StringUtils.substringAfterLast(filename, DOT).toLowerCase();

        if (CSV.equals(extension) || TXT.equals(extension)) {
            productCodes = createListFromCsv(filename);
        } else if (XLS.equals(extension) || XLSX.equals(extension)) {
            productCodes = createListFromWorkbook(filename, extension);
        } else {
            throw new DistQualityAndLegalInvalidFileUploadException(FILE_EXTENSION_UNSUPPORTED);
        }

        if (productCodes.size() > LIST_SIZE_LIMIT) {
            throw new DistQualityAndLegalInvalidFileUploadException(FILE_LIST_LIMIT_EXCEEDED);
        }

        return productCodes;
    }

    private List<String> createListFromCsv(final String fileName) throws IOException, DistQualityAndLegalInvalidFileUploadException {
        final List<String> productCodes = new ArrayList<>();

        BufferedReader bufferedReader = getBufferedReaderForCsv(fileName);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (isNotBlank(line)) {
                productCodes.add(line);
            }
        }

        return productCodes;
    }

    private BufferedReader getBufferedReaderForCsv(String fileName) throws IOException, DistQualityAndLegalInvalidFileUploadException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        if (bufferedReader.readLine() == null) {
            bufferedReader.close();
            throw new DistQualityAndLegalInvalidFileUploadException(FILE_CONTENT_BLANK);
        } else {
            bufferedReader = new BufferedReader(new FileReader(fileName));
        }

        return bufferedReader;
    }

    private List<String> createListFromWorkbook(final String fileName, String extension) throws DistQualityAndLegalInvalidFileUploadException, IOException {
        final List<String> productCodes = new ArrayList<>();
        Workbook workbook;
        if (extension.equals(XLS)) {
            workbook = new HSSFWorkbook(new FileInputStream(fileName));
        } else {
            workbook = new XSSFWorkbook(new FileInputStream(fileName));
        }
        final Sheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
        // Check if the file is "empty" (first row in column A is blank)
        if (isExcelFileEmpty(sheet)) {
            throw new DistQualityAndLegalInvalidFileUploadException(FILE_CONTENT_BLANK);
        }

        for (Row row : sheet) {
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                final Cell cell = cellIterator.next();
                if (cell != null && cell.getColumnIndex() == 0) { // skip null cells and cells with id 1 or more (read only cell with ID 0)
                    String value;
                    if (cell.getCellType() == NUMERIC) {
                        value = String.valueOf((long) cell.getNumericCellValue());
                    } else {
                        value = cell.getStringCellValue();
                    }

                    if (isNotBlank(value)) {
                        if (row.getRowNum() == 0) {
                            if (isNumeric(value)) {
                                // Check if the first row is numeric, otherwise, if the template is used and the first row is the header, first row won't be valid
                                productCodes.add(value);
                            }
                        } else {
                            productCodes.add(value);
                        }
                    }
                }
            }
        }

        if (isEmpty(productCodes)) {
            // If there is only one row, and that row is not a numeric value (for example, a blank template was uploaded), the file is considered blank
            throw new DistQualityAndLegalInvalidFileUploadException(FILE_CONTENT_BLANK);
        }

        return productCodes;
    }

    private boolean isExcelFileEmpty(Sheet sheet) {
        if (sheet == null) {
            return true; // no sheet - no rows - no columns - no values --> empty file
        }

        Row firstRow = sheet.getRow(0);
        if (firstRow == null) {
            return true; // no first row - no values --> empty file
        }

        Iterator<Cell> firstRowCellIterator = firstRow.cellIterator();
        if (firstRowCellIterator == null || isFalse(firstRowCellIterator.hasNext())) {
            return true; // no columns - no values --> empty file
        }

        Cell firstCell = firstRow.getCell(0);
        return firstCell == null || firstCell.getCellType() == BLANK; // no values (in first cell) --> empty file
    }

}
