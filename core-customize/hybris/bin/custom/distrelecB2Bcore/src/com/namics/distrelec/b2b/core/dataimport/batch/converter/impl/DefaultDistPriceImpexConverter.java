/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.batch.converter.impl;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.dataimport.batch.converter.DistPriceImpexConverter;

import de.hybris.platform.acceleratorservices.dataimport.batch.converter.impl.DefaultImpexConverter;
import de.hybris.platform.servicelayer.exceptions.SystemException;

/**
 * Default implementation of {@link DistPriceImpexConverter}.
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 3.0
 */
public class DefaultDistPriceImpexConverter extends DefaultImpexConverter implements DistPriceImpexConverter {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistPriceImpexConverter.class);

    private static final char PLUS_CHAR = '+';
    private static final char ERP_SEQUENCE_CHAR = 'E';
    private static final char SEQUENCE_CHAR = 'S';
    private static final int COLUMN_LENGTH = 25;
    private static final String EMPTY_STRING = "";
    private static final char BRACKET_END = '}';
    private static final char BRACKET_START = '{';
    static final Pattern PRICE_PATTERN = Pattern.compile("^\\s*((0){0,11}(\\.0)?\\s*)?$");

    private String impexRow;
    private String insertUpdateHeader;
    private String removeHeader;
    private String removeImpexRow;
    private String version;

    @Override
    public String convertInsertUpdate(final Map<Integer, String> row, final Long sequenceId, final String erpSequenceId) {
        Matcher matcher = null;
        final StringBuffer resultBuilder = new StringBuffer();
        String result = EMPTY_STRING;
        if (!MapUtils.isEmpty(row)) {
            final StringBuilder builder = new StringBuilder();
            int copyIdx = 0;
            int idx = impexRow.indexOf(BRACKET_START);
            while (idx > -1) {
                final int endIdx = impexRow.indexOf(BRACKET_END, idx);
                processRow(row, sequenceId, erpSequenceId, builder, copyIdx, idx, endIdx);
                copyIdx = endIdx + 1;
                idx = impexRow.indexOf(BRACKET_START, endIdx);
            }
            if (copyIdx < impexRow.length()) {
                builder.append(impexRow.substring(copyIdx));
            }
            result = builder.toString();
        }
        final String preResult = escapeQuotes(result);
        final String[] rowsToValidate = preResult.split("\n");
        for (final String rowToValidate : rowsToValidate) {
            final String[] columns = rowToValidate.split(";");
            if (columns.length == COLUMN_LENGTH) {
                String price = columns[7];
                if (matcher != null) {
                    matcher.reset(price);
                } else {
                    matcher = PRICE_PATTERN.matcher(price);
                }
                if (!matcher.matches()) {
                    if (!rowToValidate.startsWith("\t\t\t\t")) {
                        resultBuilder.append("\t\t\t\t");
                    }

                    resultBuilder.append(StringUtils.join(columns, ';'));
                    resultBuilder.append(System.getProperty("line.separator"));
                }
            }
        }

        return resultBuilder.toString();
    }

    @Override
    public String convertRemove(final Map<Integer, String> row, final Long sequenceId, final String erpSequenceId) {
        String result = EMPTY_STRING;
        if (!MapUtils.isEmpty(row)) {
            final StringBuilder builder = new StringBuilder();
            int copyIdx = 0;
            int idx = removeImpexRow.indexOf(BRACKET_START);
            while (idx > -1) {
                final int endIdx = removeImpexRow.indexOf(BRACKET_END, idx);
                if (endIdx < 0) {
                    throw new SystemException("Invalid row syntax [brackets not closed]: " + removeImpexRow);
                }
                builder.append(removeImpexRow.substring(copyIdx, idx));
                if (removeImpexRow.charAt(idx + 1) == SEQUENCE_CHAR) {
                    if (sequenceId != null) {
                        builder.append(sequenceId);
                    }
                } else if (removeImpexRow.charAt(idx + 1) == ERP_SEQUENCE_CHAR) {
                    if (erpSequenceId != null) {
                        builder.append(erpSequenceId);
                    }
                } else {
                    final boolean mandatory = removeImpexRow.charAt(idx + 1) == PLUS_CHAR;
                    Integer mapIdx = null;
                    try {
                        mapIdx = Integer.valueOf(removeImpexRow.substring(mandatory ? idx + 2 : idx + 1, endIdx));
                    } catch (final NumberFormatException e) {
                        throw new SystemException("Invalid row syntax [invalid column number]: " + removeImpexRow, e);
                    }
                    final String colValue = row.get(mapIdx);
                    if (mandatory && StringUtils.isBlank(colValue)) {
                        throw new IllegalArgumentException("Missing value for " + mapIdx);
                    }
                    if (colValue != null) {
                        builder.append(colValue);
                    }
                }
                copyIdx = endIdx + 1;
                idx = removeImpexRow.indexOf(BRACKET_START, endIdx);
            }
            if (copyIdx < removeImpexRow.length()) {
                builder.append(removeImpexRow.substring(copyIdx));
            }
            result = builder.toString();
        }
        return result;
    }

    @Override
    @Deprecated
    public String convert(final Map<Integer, String> row, Long sequenceId) {
        throw new IllegalStateException("Please use convertInsertUpdate(Map<Integer, String>) or convertRemove(Map<Integer, String>)!");
    }

    protected void processRow(final Map<Integer, String> row, final Long sequenceId, final String erpSequenceId,
        final StringBuilder builder, final int copyIdx, final int idx, final int endIdx) {
        if (endIdx < 0) {
            throw new SystemException("Invalid row syntax [brackets not closed]: " + impexRow);
        }
        builder.append(impexRow.substring(copyIdx, idx));
        if (impexRow.charAt(idx + 1) == SEQUENCE_CHAR) {
            if (sequenceId != null) {
                builder.append(sequenceId);
            }
        } else if (impexRow.charAt(idx + 1) == ERP_SEQUENCE_CHAR) {
            if (erpSequenceId != null) {
                builder.append(erpSequenceId);
            }
        } else {
            processValues(row, builder, idx, endIdx);
        }
    }

    protected void processValues(final Map<Integer, String> row, final StringBuilder builder, final int idx, final int endIdx)
    {
        final boolean mandatory = impexRow.charAt(idx + 1) == PLUS_CHAR;
        Integer mapIdx = null;
        try {
            mapIdx = Integer.valueOf(impexRow.substring(mandatory ? idx + 2 : idx + 1, endIdx));
        } catch (final NumberFormatException e) {
            throw new SystemException("Invalid row syntax [invalid column number]: " + impexRow, e);
        }
        final String colValue = row.get(mapIdx);
        if (mandatory && StringUtils.isBlank(colValue)) {
            throw new IllegalArgumentException("Missing value for " + mapIdx);
        }
        if (colValue != null) {
            builder.append(colValue);
        }
    }

    @Override
    public void setHeader(final String header) {
        // nothing to do
    }

    @Override
    @Required
    public void setImpexRow(final String impexRow) {
        this.impexRow = impexRow;
    }

    @Override
    public String getInsertUpdateHeader() {
        return insertUpdateHeader;
    }

    public void setInsertUpdateHeader(final String insertUpdateHeader) {
        this.insertUpdateHeader = insertUpdateHeader;
    }

    @Override
    public String getRemoveHeader() {
        return removeHeader;
    }

    public void setRemoveHeader(final String removeHeader) {
        this.removeHeader = removeHeader;
    }

    @Required
    public void setRemoveImpexRow(final String removeImpexRow) {
        Assert.hasText(removeImpexRow);
        this.removeImpexRow = removeImpexRow;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
