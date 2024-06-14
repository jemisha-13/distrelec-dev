package com.namics.distrelec.b2b.core.dataimport.batch.converter.impl;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.dataimport.batch.converter.BeforeConverting;
import com.namics.distrelec.b2b.core.dataimport.batch.converter.BeforeEachConverting;
import com.namics.distrelec.b2b.core.dataimport.batch.converter.DistImpexConverter;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.converter.impl.DefaultImpexConverter;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.util.CSVReader;

/**
 * This replace the standard DefaultImpexConverter used in accelerator.<br>
 * Enhanced features:<br>
 * - Manage CSV escape for string. If the source csv contains escaped string the double-quote escape is preserved.
 * 
 * @author daebersanif, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class DefaultDistImpexConverter extends DefaultImpexConverter implements DistImpexConverter {
    private static final char PLUS_CHAR = '+';
    private static final char ERP_SEQUENCE_CHAR = 'E';
    private static final char SEQUENCE_CHAR = 'S';
    private static final String EMPTY_STRING = "";
    private static final char BRACKET_END = '}';
    private static final char BRACKET_START = '{';

    private String impexRow;
    private BeforeConverting beforeConverting;
    private BeforeEachConverting beforeEachConverting;

    @Override
    public String convert(final Map<Integer, String> row, final Long sequenceId, final String erpSequenceId) {
        String result = EMPTY_STRING;
        if (!MapUtils.isEmpty(row)) {
            final StringBuilder builder = new StringBuilder();
            int copyIdx = 0;
            int idx = getImpexRow().indexOf(BRACKET_START);
            while (idx > -1) {
                final int endIdx = getImpexRow().indexOf(BRACKET_END, idx);
                if (endIdx < 0) {
                    throw new SystemException("Invalid row syntax [brackets not closed]: " + getImpexRow());
                }
                builder.append(getImpexRow().substring(copyIdx, idx));
                if (getImpexRow().charAt(idx + 1) == SEQUENCE_CHAR) {
                    if (sequenceId != null) {
                        builder.append(sequenceId);
                    }
                } else if (getImpexRow().charAt(idx + 1) == ERP_SEQUENCE_CHAR) {
                    if (erpSequenceId != null) {
                        builder.append(erpSequenceId);
                    }
                } else {
                    final boolean mandatory = getImpexRow().charAt(idx + 1) == PLUS_CHAR;
                    Integer mapIdx = null;
                    try {
                        mapIdx = Integer.valueOf(getImpexRow().substring(mandatory ? idx + 2 : idx + 1, endIdx));
                    } catch (final NumberFormatException e) {
                        throw new SystemException("Invalid row syntax [invalid column number]: " + getImpexRow(), e);
                    }
                    final String colValue = row.get(mapIdx);
                    if (mandatory && StringUtils.isBlank(colValue)) {
                        throw new IllegalArgumentException("Missing value for " + mapIdx);
                    }
                    if (colValue != null) {
                        // taking care of the double-quotes
                        String escapedColValue = StringEscapeUtils.escapeCsv(colValue);
                        // remove first unneeded double-quote
                        if (escapedColValue.startsWith("\"")) {
                            escapedColValue = escapedColValue.substring(1, escapedColValue.length());
                        }
                        // remove last unneeded double-quote
                        if (escapedColValue.endsWith("\"")) {
                            escapedColValue = escapedColValue.substring(0, escapedColValue.length() - 1);
                        }
                        builder.append(escapedColValue);
                    }
                }
                copyIdx = endIdx + 1;
                idx = getImpexRow().indexOf(BRACKET_START, endIdx);
            }
            if (copyIdx < getImpexRow().length()) {
                builder.append(getImpexRow().substring(copyIdx));
            }
            result = builder.toString();
        }
        return result;
    }

    @Override
    public void before(final BatchHeader header, final CSVReader csvReader) {
        if (getBeforeConverting() != null) {
            getBeforeConverting().before(header, csvReader);
        }
    }

    @Override
    public void beforeEach(Map<Integer, String> row) {
        if (getBeforeEachConverting() != null) {
            getBeforeEachConverting().beforeEach(row);
        }
    }

    public String getImpexRow() {
        return impexRow;
    }

    @Override
    @Required
    public void setImpexRow(final String impexRow) {
        Assert.hasText(impexRow);
        this.impexRow = impexRow;
    }

    public BeforeConverting getBeforeConverting() {
        return beforeConverting;
    }

    public void setBeforeConverting(final BeforeConverting beforeConverting) {
        this.beforeConverting = beforeConverting;
    }

    public BeforeEachConverting getBeforeEachConverting() {
        return beforeEachConverting;
    }

    public void setBeforeEachConverting(final BeforeEachConverting beforeEachConverting) {
        this.beforeEachConverting = beforeEachConverting;
    }
}
