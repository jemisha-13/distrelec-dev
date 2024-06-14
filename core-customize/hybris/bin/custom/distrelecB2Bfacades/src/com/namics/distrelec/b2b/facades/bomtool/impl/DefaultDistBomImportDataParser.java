package com.namics.distrelec.b2b.facades.bomtool.impl;

import com.namics.distrelec.b2b.facades.bomtool.DistBomFileParser;
import com.namics.distrelec.b2b.facades.bomtool.DistBomImportDataParser;
import com.namics.distrelec.b2b.facades.bomtool.exception.BomToolFacadeException;
import de.hybris.platform.core.model.media.MediaModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;

public class DefaultDistBomImportDataParser implements DistBomImportDataParser {

    private static int ROWS_LIMIT = 300;
    private static int DEFAULT_EXCEL_SHEET = 0;
    private static int DEFAULT_DATA_ARTICLE_NUMBER_POSITION = 1;
    private static int DEFAULT_DATA_QUANTITY_POSITION = 0;
    private static Integer DEFAULT_DATA_REFERENCE_POSITION = 2;

    @Autowired
    private DistBomFileParser distBomFileParser;

    @Override
    public List<String[]> parseFromString(String data, int bomToolRows, int quantityPosition, int referencePosition) throws BomToolFacadeException {
        final List<String[]> importRows = new ArrayList<>();
        final String[] lines = data.split("\r?\n|\r");

        if (lines.length > ROWS_LIMIT) {
            throw new BomToolFacadeException("Too many lines! " + ROWS_LIMIT + " are allowed, but got" + lines.length,
                    BomToolFacadeException.ErrorSource.TOO_MANY_LINES);
        }

        String speratorPattern;
        for (int lineCounter = 0; lineCounter < lines.length; lineCounter++) {
            final String line = lines[lineCounter];
            if (line.length() == 0) {
                continue;
            }

            if (line.contains(",")) {
                speratorPattern = "\\s*,\\s*";
            } else if (line.contains(" ") || line.contains("\t") || line.contains("\b")) {
                speratorPattern = "\\s+";
            } else {
                throw new BomToolFacadeException("Separator not found!", lineCounter,
                        BomToolFacadeException.ErrorSource.COPY_PAST_SEPARATOR_NOT_FOUND);
            }

            final String[] dataArray = line.split(speratorPattern);
            if (dataArray.length < 2) {
                throw new BomToolFacadeException("Separator not found!", lineCounter,
                        BomToolFacadeException.ErrorSource.COPY_PAST_SEPARATOR_NOT_FOUND);
            } else if (dataArray.length > 3) {
                final int length = dataArray.length;
                final StringBuffer buffer = new StringBuffer(dataArray[DEFAULT_DATA_REFERENCE_POSITION]);
                for (int i = 2; i < length; i++) {
                    buffer.append(dataArray[i]);
                }

                dataArray[DEFAULT_DATA_REFERENCE_POSITION] = buffer.toString();
            }

            if (dataArray.length > 2 && dataArray[DEFAULT_DATA_REFERENCE_POSITION] != null
                    && dataArray[DEFAULT_DATA_REFERENCE_POSITION].length() > 35) {
                throw new BomToolFacadeException("Customer reference is longer than 35 chars!", lineCounter,
                        BomToolFacadeException.ErrorSource.CUSTOMER_REFERENCE_FIELD);
            }

            importRows.add(dataArray);
        }

        if (importRows.isEmpty()) {
            throw new BomToolFacadeException("No data found!", BomToolFacadeException.ErrorSource.NO_DATA);
        }

        return importRows;
    }

    @Override
    public List<String[]> parseFromFile(final MediaModel file, int articleNumberPosition, int quantityPosition, Integer referencePosition, boolean ignoreFirstRow) throws BomToolFacadeException {
        String extension = null;
        int position = file.getCode().lastIndexOf('.');
        if (position > 0) {
            extension = file.getCode().substring(position + 1);
        }

        final List<String[]> importRows;
        if ("csv".equals(extension) || "txt".equals(extension)) {
            importRows = distBomFileParser.createArrayFromCsv(file, referencePosition);
        } else if ("xls".equals(extension)) {
            importRows = distBomFileParser.createArrayFromXLS(file, DEFAULT_EXCEL_SHEET, referencePosition);
        } else if ("xlsx".equals(extension)) {
            importRows = distBomFileParser.createArrayFromXLSX(file, DEFAULT_EXCEL_SHEET, referencePosition);
        } else {
            importRows = EMPTY_LIST;
        }

        if (importRows != null && !importRows.isEmpty()) {
            if (ignoreFirstRow) {
                importRows.remove(0);
            }

            final List<String []> filteredImportRows = importRows.stream()
                    .filter(Objects::nonNull)
                    .filter(stringArray -> {
                        for (String current : stringArray) {
                            if (StringUtils.isEmpty(current)) {
                                return false;
                            }
                        }
                        return true;
            }).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(filteredImportRows)) {
                return filteredImportRows;
            }
        }
        throw new BomToolFacadeException("No data found!", BomToolFacadeException.ErrorSource.NO_DATA);
    }

    @Override
    public List<String[]> getLinesFromFile(final MediaModel file) throws BomToolFacadeException {
        List<String[]> importRows = new ArrayList<>();
        // Is it an .csv or an xls?
        String extension = null;

        final int position = file.getCode().lastIndexOf('.');
        if (position > 0) {
            extension = file.getCode().substring(position + 1);
        }

        if ("csv".equals(extension) || "txt".equals(extension)) {
            importRows = distBomFileParser.createArrayFromCsv(file, DEFAULT_DATA_REFERENCE_POSITION);
        } else if ("xls".equals(extension)) {
            importRows = distBomFileParser.createArrayFromXLS(file, DEFAULT_EXCEL_SHEET, DEFAULT_DATA_REFERENCE_POSITION);
        } else if ("xlsx".equals(extension)) {
            importRows = distBomFileParser.createArrayFromXLSX(file, DEFAULT_EXCEL_SHEET, DEFAULT_DATA_REFERENCE_POSITION);
        }

        return importRows;
    }
}
