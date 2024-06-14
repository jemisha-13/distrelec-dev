/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.CSVUtils;
import de.hybris.platform.util.CSVWriter;

/**
 * MelectronicsCSVWriter will escape all fields.
 * 
 * @author rhusi, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class ExtendedCSVWriter extends CSVWriter {

    /**
     * Constructor.
     * 
     * @param writer
     *            The writer
     * 
     * @throws UnsupportedEncodingException
     *             Throws exception if the given encoding is not supported
     * @throws FileNotFoundException
     *             Throws exception if the given file does not exist
     */
    public ExtendedCSVWriter(final Writer writer) throws UnsupportedEncodingException, FileNotFoundException {
        super(writer);
    }

    /**
     * Constructor.
     * 
     * @param file
     *            The file to use for writing
     * @param encoding
     *            The encoding
     * @throws UnsupportedEncodingException
     *             Throws exception if the given encoding is not supported
     * @throws FileNotFoundException
     *             Throws exception if the given file does not exist
     */
    public ExtendedCSVWriter(final File file, final String encoding) throws UnsupportedEncodingException, FileNotFoundException {
        super(file, encoding);
    }

    /**
     * Constructor.
     * 
     * @param file
     *            The file to use for writing
     * @param encoding
     *            The encoding
     * @param append
     *            Should the line be appended or not
     * @throws UnsupportedEncodingException
     *             Throws exception if the given encoding is not supported
     * @throws FileNotFoundException
     *             Throws exception if the given file does not exist
     */
    public ExtendedCSVWriter(final File file, final String encoding, final boolean append) throws UnsupportedEncodingException, FileNotFoundException {
        super(file, encoding, append);
    }

    /**
     * Constructor.
     * 
     * @param outputstream
     *            The outputstream
     * @param encoding
     *            The encoding
     * @throws UnsupportedEncodingException
     *             Throws exception if the given encoding is not supported
     * @throws FileNotFoundException
     *             Throws exception if the given file does not exist
     */
    public ExtendedCSVWriter(final OutputStream outputstream, final String encoding) throws UnsupportedEncodingException, FileNotFoundException {
        super(outputstream, encoding);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String createCSVLine(final Map fields) {
        final BitSet cells = new BitSet();
        int max = 0;
        for (final Iterator<Map.Entry<Integer, String>> iter = fields.entrySet().iterator(); iter.hasNext();) {
            final Map.Entry<Integer, String> entry = iter.next();
            final int idx = entry.getKey().intValue();
            if (idx < 0) {
                throw new IllegalArgumentException("cell index < 0 (got " + idx + "=>" + entry.getValue() + ")");
            }
            cells.set(idx);
            max = (max < idx) ? idx : max;
        }
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i <= max; ++i) {
            if (i > 0) {
                buffer.append(getFieldseparator());
            }
            if (!(cells.get(i))) {
                continue;
            }
            final String str = (String) fields.get(Integer.valueOf(i));
            if (str == null) {
                buffer.append("\"\"");
            }
            buffer.append(createCSVField(str));
        }

        return buffer.toString();
    }

    private String createCSVField(final String fielddata) {
        if ((fielddata == null) || (fielddata.length() == 0)) {
            return "";
        }

        final Set<String> specials = new HashSet<String>(Arrays.asList(CSVConstants.LINE_SEPARATORS));
        specials.add(Character.toString(getTextseparator()));
        specials.add(Character.toString(getFieldseparator()));
        specials.add(getLinebreak());

        final StringBuilder buffer = new StringBuilder(fielddata);
        CSVUtils.escapeString(buffer, new String[] { Character.toString(getTextseparator()) }, specials.toArray(new String[specials.size()]), true);
        buffer.insert(0, getTextseparator());
        buffer.append(getTextseparator());
        return buffer.toString();
    }

}
