package com.namics.distrelec.b2b.core.reconciliation.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ObjectArrays;
import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchExecutionService;
import com.namics.distrelec.b2b.core.inout.export.exception.DistCsvTransformationException;
import com.namics.distrelec.b2b.core.reconciliation.dao.DistPriceReconciliationDao;
import com.namics.distrelec.b2b.core.reconciliation.service.DistPriceReconciliationService;
import com.namics.distrelec.b2b.core.util.DistUtils;
import com.opencsv.CSVWriter;
import com.opencsv.ResultSetHelper;
import com.opencsv.ResultSetHelperService;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.jalo.JaloSession;

public class DefaultDistPriceReconciliationService implements DistPriceReconciliationService {

    private static final Logger LOG = LogManager.getLogger(DefaultDistPriceReconciliationService.class);

    public static final String ENCODE_URL_SUFFIX = "_encodeurl";

    public static final String ENCODE_FF_SUFFIX = "_encodeff";

    private static final char SEPARATOR = ',';

    private static final int BUFFER_SIZE = 2048;

    private static int FILE_HEADER_SIZE = 0;

    @Autowired
    private DistPriceReconciliationDao distPriceReconciliationDao;

    @Autowired
    private DistFlexibleSearchExecutionService flexibleSearchExecutionService;

    @Override
    public ResultSet getAllPriceRows(String salesOrg) {
        return distPriceReconciliationDao.fetchAllPriceRows(salesOrg);
    }

    @Override
    public InputStream transform(final ResultSet resultSet) {
        try {
            final PipedInputStream input = new PipedInputStream(BUFFER_SIZE);
            final PipedOutputStream output = new PipedOutputStream(input);

            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Runnable exporter = new OPCRCsvTransformer(Registry.getCurrentTenant(), resultSet, output);
            executor.submit(exporter);
            executor.shutdown();
            return input;
        } catch (final IOException e) {
            throw new DistCsvTransformationException("Could not create piped stream", e);
        }
    }

    protected class OPCRCsvTransformer implements Runnable {

        private final ResultSet resultSet;

        private final PipedOutputStream output;

        private final Tenant tenant;

        private boolean[] encodeUrl;

        private boolean[] encodeFf;

        public OPCRCsvTransformer(final Tenant tenant, final ResultSet resultSet, final PipedOutputStream output) {
            this.resultSet = resultSet;
            this.output = output;
            this.tenant = tenant;
        }

        @Override
        public void run() {
            Registry.setCurrentTenant(tenant);
            JaloSession.getCurrentSession().activate();

            CSVWriter writer = null;
            long count = 0;
            try {
                writer = new CSVWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8.name()), SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);

                final ResultSetHelper resultSetHelper = new ResultSetHelperService();
                String[] columnNames = parseColumnNames(resultSetHelper);
                writer.writeNext(columnNames);

                while (resultSet.next()) {
                    count++;
                    String[] columnValues = encodeColumnValues(resultSetHelper);
                    writer.writeNext(columnValues);
                }
                LOG.debug("Exported {} rows", count);
            } catch (final SQLException | IOException e) {
                LOG.error("Could not transform ResultSet to CSV stream", e);
                throw new DistCsvTransformationException("Could not transform ResultSet to CSV stream", e);
            } finally {
                flexibleSearchExecutionService.closeResultSet(resultSet);
                IOUtils.closeQuietly(writer);
                IOUtils.closeQuietly(output);
                JaloSession.deactivate();
                Registry.unsetCurrentTenant();
            }
        }

        /**
         * Goes through the column names list, and checks if a column name ends with encoding suffixes. If it does, then
         * marks it should be encoded in boolean arrays.
         */
        private String[] parseColumnNames(ResultSetHelper resultSetHelper) throws SQLException {

            String[] firstArray = resultSetHelper.getColumnNames(resultSet);
            firstArray[7] = "Scale1,Price1,PricePerX1,Scale2,Price2,PricePerX2,Scale3,Price3,PricePerX3,Scale4,Price4,PricePerX4,Scale5,Price5,PricePerX5,Scale6,Price6,PricePerX6,Scale7,Price7,PricePerX7,Scale8,Price8,PricePerX8,Scale9,Price9,PricePerX9,Scale10,Price10,PricePerX10";
            String[] secondArray = firstArray[7].split(",");
            String[] columnNames = Arrays.copyOf(firstArray, 7);
            String[] combine = ObjectArrays.concat(columnNames, secondArray, String.class);
            String[] parsedColumnNames = new String[combine.length];

            encodeUrl = new boolean[combine.length];
            encodeFf = new boolean[combine.length];

            for (int i = 0; i < combine.length; i++) {
                doEncode(combine, ENCODE_URL_SUFFIX, i, encodeUrl, parsedColumnNames);
                doEncode(combine, ENCODE_FF_SUFFIX, i, encodeFf, parsedColumnNames);
            }
            FILE_HEADER_SIZE = parsedColumnNames.length;
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
            String[] firstArray = resultSetHelper.getColumnValues(resultSet);
            String[] secondArray = firstArray[7].split(",");
            secondArray = Arrays.stream(secondArray)
                                .filter(s -> (s != null && s.length() > 0))
                                .toArray(String[]::new);
            String[] columnValues = Arrays.copyOf(firstArray, 7);
            for (int i = 0; i < secondArray.length; i++) {
                if (i % 3 != 0) {
                    double d = Double.parseDouble(secondArray[i]);
                    secondArray[i] = String.format("%.2f", d);
                }
            }
            String[] combine = (String[]) ArrayUtils.addAll(columnValues, secondArray);
            String[] encodedValues = new String[combine.length];
            for (int i = 0; i < combine.length; i++) {
                String columnValue = combine[i];
                if (columnValue.contains("SalesOrg_UPG")) {
                    String[] bits = columnValue.split("_");
                    columnValue = bits[bits.length - 1];
                }
                if (encodeUrl[i]) {
                    columnValue = DistUtils.urlEncode(columnValue);
                }
                if (encodeFf[i]) {
                    columnValue = DistUtils.encodeFfSpecialChars(columnValue);
                }

                encodedValues[i] = columnValue;
            }
            String[] finalArray = new String[FILE_HEADER_SIZE - encodedValues.length];
            if (encodedValues.length < FILE_HEADER_SIZE) {
                for (int s = 0; s < finalArray.length; s++) {
                    finalArray[s] = "0";
                }

            }

            return (String[]) ArrayUtils.addAll(encodedValues, finalArray);
        }
    }

}
