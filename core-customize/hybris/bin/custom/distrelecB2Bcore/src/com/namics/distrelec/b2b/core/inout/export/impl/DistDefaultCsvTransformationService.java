/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.export.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Charsets;
import com.namics.distrelec.b2b.core.inout.export.DistCsvTransformationService;
import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchExecutionService;
import com.namics.distrelec.b2b.core.inout.export.exception.DistCsvTransformationException;
import com.opencsv.CSVWriter;
import com.opencsv.ResultSetHelper;
import com.opencsv.ResultSetHelperService;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.jalo.JaloSession;

/**
 * Default implementation of {@link DistCsvTransformationService}.
 * 
 * @author ceberle, Namics AG
 * @since Namics Distrelec 1.1
 */
public class DistDefaultCsvTransformationService implements DistCsvTransformationService {

    private static final Logger LOG = LogManager.getLogger(DistDefaultCsvTransformationService.class);

    public static final String ENCODE_URL_SUFFIX = "_encodeurl";
    public static final String ENCODE_FF_SUFFIX = "_encodeff";

    private static final int BUFFER_SIZE = 2048;
    private static final char SEPARATOR = ';';

    private DistFlexibleSearchExecutionService flexibleSearchExecutionService;

    private SessionService sessionService;

    @Override
    public InputStream transform(final String[] header, final List<String[]> arrayList) {
        try {
            final PipedInputStream input = new PipedInputStream(BUFFER_SIZE);
            final PipedOutputStream output = new PipedOutputStream(input);

            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Runnable exporter = new ArrayCsvTransformer(header, arrayList, output);
            executor.submit(exporter);
            executor.shutdown();
            return input;
        } catch (final IOException e) {
            throw new DistCsvTransformationException("Could not create piped stream", e);
        }
    }

    @Override
    public InputStream transform(final String flexibleSearchQuery, final Map<String, Object> flexibleSearchParameters,char seperator) {
        final ResultSet resultSet = getFlexibleSearchExecutionService().execute(flexibleSearchQuery, flexibleSearchParameters);

        if (LOG.isDebugEnabled()) {
            final ResultSetHelper resultSetHelper = new ResultSetHelperService();
            List<String> resultSetColumns;
            try {
                resultSetColumns = Arrays.asList(resultSetHelper.getColumnNames(resultSet));
                LOG.debug(Arrays.toString(resultSetColumns.toArray()));
            } catch (final SQLException e) {
                LOG.error("error logging column names", e);
            }
        }

        return transform(resultSet,seperator);
    }

    @Override
    public InputStream transform(final ResultSet resultSet,char seperator) {
        try {
            final PipedInputStream input = new PipedInputStream(BUFFER_SIZE);
            final PipedOutputStream output = new PipedOutputStream(input);

            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Runnable exporter = new CsvTransformer(Registry.getCurrentTenant(), resultSet, output,seperator);
            executor.submit(exporter);
            executor.shutdown();
            return input;
        } catch (final IOException e) {
            throw new DistCsvTransformationException("Could not create piped stream", e);
        }
    }

    protected class ArrayCsvTransformer implements Runnable {

        private final List<String[]> arrayList;
        private final String[] header;
        private final PipedOutputStream output;

        public ArrayCsvTransformer(final String[] header, final List<String[]> arrayList, final PipedOutputStream output) {
            this.arrayList = arrayList;
            this.output = output;
            this.header = header;
        }

        @Override
        public void run() {
            Registry.activateMasterTenant();
            JaloSession.getCurrentSession().activate();

            CSVWriter writer = null;
            try {
                writer = new CSVWriter(new OutputStreamWriter(output, Charsets.UTF_8.name()), SEPARATOR, CSVWriter.DEFAULT_ESCAPE_CHARACTER);

                writer.writeNext(header);

                writer.writeAll(arrayList);
            } catch (final UnsupportedEncodingException e) {
                LOG.error("Could not transform ResultSet to CSV stream", e);
                throw new DistCsvTransformationException("Could not transform ResultSet to CSV stream", e);
            } finally {
                IOUtils.closeQuietly(writer);
                IOUtils.closeQuietly(output);
            }
        }
    }

    protected class CsvTransformer implements Runnable {

        private final ResultSet resultSet;
        private final PipedOutputStream output;
        private final Tenant tenant;
        private boolean[] encodeUrl;
        private boolean[] encodeFf;
        private char seperator;

        public CsvTransformer(final Tenant tenant, final ResultSet resultSet, final PipedOutputStream output,char seperator) {
            this.resultSet = resultSet;
            this.output = output;
            this.tenant = tenant;
            this.seperator=seperator;
        }

        @Override
        public void run() {
            Registry.setCurrentTenant(tenant);
            JaloSession.getCurrentSession().activate();

            CSVWriter writer = null;
            long count = 0;
            try {
                writer = new CSVWriter(new OutputStreamWriter(output, Charsets.UTF_8.name()), seperator, CSVWriter.DEFAULT_ESCAPE_CHARACTER);

                final ResultSetHelper resultSetHelper = new ResultSetHelperService();
                String[] columnNames = parseColumnNames(resultSetHelper);
                writer.writeNext(columnNames);

                while (resultSet.next()) {
                    count++;
                    String[] columnValues = encodeColumnValues(resultSetHelper);
                    writer.writeNext(columnValues);
                }
                LOG.info("Exported {} rows", count);
            } catch (final UnsupportedEncodingException e) {
                LOG.error("Could not transform ResultSet to CSV stream", e);
                throw new DistCsvTransformationException("Could not transform ResultSet to CSV stream", e);
            } catch (final SQLException e) {
                LOG.error("Could not transform ResultSet to CSV stream", e);
                throw new DistCsvTransformationException("Could not transform ResultSet to CSV stream", e);
            } catch (final IOException e) {
                LOG.error("Could not transform ResultSet to CSV stream", e);
                throw new DistCsvTransformationException("Could not transform ResultSet to CSV stream", e);
            } finally {
                getFlexibleSearchExecutionService().closeResultSet(resultSet);
                IOUtils.closeQuietly(writer);
                IOUtils.closeQuietly(output);
                // See https://wiki.hybris.com/display/hybrisALF/Creating+and+Initializing+Threads
                JaloSession.deactivate();
                Registry.unsetCurrentTenant();
            }
        }

        /**
         * Goes through the column names list, and checks if a column name ends with encoding suffixes. If it does, then
         * marks it should be encoded in boolean arrays.
         */
        private String[] parseColumnNames(ResultSetHelper resultSetHelper) throws SQLException {
            String[] columnNames = resultSetHelper.getColumnNames(resultSet);
            String[] parsedColumnNames = new String[columnNames.length];

            encodeUrl = new boolean[columnNames.length];
            encodeFf = new boolean[columnNames.length];

            for (int i = 0 ; i < columnNames.length ; i++) {
                doEncode(columnNames, ENCODE_URL_SUFFIX, i, encodeUrl, parsedColumnNames);
                doEncode(columnNames, ENCODE_FF_SUFFIX, i, encodeFf, parsedColumnNames);
            }
            return parsedColumnNames;
        }

        private void doEncode(String[] columns, String suffix, int i, boolean[] encode, String[] finalColumns) {
            String column = columns[i];
            boolean doEncode = column.endsWith(suffix);
            encode[i] = doEncode;

            String toEscapeColumn = finalColumns[i] != null ? finalColumns[i] : column;

            if (doEncode) {
                finalColumns[i] = toEscapeColumn.substring(0, toEscapeColumn.length() - suffix.length());
            } else {
                finalColumns[i] = toEscapeColumn;
            }
        }

        /**
         * Encodes column values if they are marked for encoding.
         */
        private String[] encodeColumnValues(ResultSetHelper resultSetHelper) throws IOException, SQLException {
            String[] columnValues = resultSetHelper.getColumnValues(resultSet);
            String[] encodedValues = new String[columnValues.length];

            for (int i = 0 ; i < columnValues.length ; i++) {
                String columnValue = columnValues[i];

                if (encodeUrl[i]) {
                    columnValue = DistUtils.urlEncode(columnValue);
                }
                if (encodeFf[i]) {
                    columnValue = DistUtils.encodeFfSpecialChars(columnValue);
                }

                encodedValues[i] = columnValue;
            }

            return encodedValues;
        }
    }

    // BEGIN GENERATED CODE

    public DistFlexibleSearchExecutionService getFlexibleSearchExecutionService() {
        return flexibleSearchExecutionService;
    }

    @Autowired
    public void setFlexibleSearchExecutionService(final DistFlexibleSearchExecutionService flexibleSearchExecutionService) {
        this.flexibleSearchExecutionService = flexibleSearchExecutionService;
    }

    // END GENERATED CODE
}
