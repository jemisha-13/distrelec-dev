package com.namics.hybris.ffsearch.export.sequence;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.namics.distrelec.b2b.core.constants.DistConstants.FactFinder;
import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchExecutionService;
import com.namics.distrelec.b2b.core.util.DistUtils;
import com.namics.hybris.ffsearch.export.DistFactFinderDetailQueryCreator;
import com.opencsv.CSVWriter;
import com.opencsv.ResultSetHelper;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductDetailsExporter implements Callable {

    private static final Logger LOG = LoggerFactory.getLogger(ProductDetailsExporter.class);

    private static final Pattern LOCALIZED_PATTERN = Pattern.compile("(\\w+)_l[a-z]{2}$");
    private static final Pattern ENCODE_FF_PATTERN = Pattern.compile("\\w+" + FactFinder.ENCODE_FF_SUFFIX + "(_\\w+)?$");
    private static final Pattern ENCODE_URL_PATTERN = Pattern.compile("\\w+" + FactFinder.ENCODE_URL_SUFFIX + "(_\\w+)?$");

    private final CatalogVersionService catalogVersionService;
    private final DistFlexibleSearchExecutionService flexibleSearchExecutionService;
    private final SessionService sessionService;
    private final ResultSetHelper resultSetHelper;
    private final SequentialExportContext exportContext;
    private final ProductExportBatch productExportBatch;
    private final String detailFlexSearchQuery;
    private final Map<String, Object> detailFlexSearchQueryParameters;
    private final CountDownLatch countDownLatch;
    private final CountDownLatch exceptionLatch;

    public ProductDetailsExporter(final CatalogVersionService catalogVersionService,
            final DistFlexibleSearchExecutionService flexibleSearchExecutionService, final SessionService sessionService,
            final ResultSetHelper resultSetHelper, final SequentialExportContext exportContext, final ProductExportBatch productExportBatch,
            final String detailFlexSearchQuery, final Map<String, Object> detailFlexSearchQueryParameters,
            final CountDownLatch countDownLatch, final CountDownLatch exceptionLatch) {
        this.catalogVersionService = catalogVersionService;
        this.flexibleSearchExecutionService = flexibleSearchExecutionService;
        this.sessionService = sessionService;
        this.resultSetHelper = resultSetHelper;
        this.exportContext = exportContext;
        this.productExportBatch = productExportBatch;
        this.detailFlexSearchQuery = detailFlexSearchQuery;
        this.detailFlexSearchQueryParameters = detailFlexSearchQueryParameters;
        this.countDownLatch = countDownLatch;
        this.exceptionLatch = exceptionLatch;
    }

    @Override
    public Object call() throws Exception {
        try {
            sessionService.createNewSession();
            sessionService.executeInLocalView(new SessionExecutionBody() {
                @Override
                public Object execute() {
                    if (LOG.isDebugEnabled()) {
                        String msg = String.format("export product details for %s products for site: %s",
                            productExportBatch.getProductPks().size(), productExportBatch.getCmsSiteUid());
                        LOG.debug(msg);
                    }

                    init(exportContext);
                    Map<String, Object> params = new HashMap<>(detailFlexSearchQueryParameters);
                    params.put(DistFactFinderDetailQueryCreator.PK_LIST, productExportBatch.getProductPks());

                    Collection<DbColumn> dbColumns = null;
                    Map<String, Integer> dbColumnPositions = null;
                    ResultSet detailResultSet = null;
                    try {
                        detailResultSet = flexibleSearchExecutionService.execute(detailFlexSearchQuery, params);

                        LOG.debug("Product details were fetched");

                        while (detailResultSet.next()) {
                            if (dbColumns == null) {
                                dbColumns = new ArrayList<>();
                                dbColumnPositions = new HashMap<>();
                                parseHeaders(detailResultSet, dbColumns, dbColumnPositions);

                                LOG.debug("Headers were parsed");
                            }
                            if (!exportContext.isHeaderExported()) {
                                synchronized (exportContext) {
                                    if (!exportContext.isHeaderExported()) {
                                        exportHeaders(exportContext, dbColumns);
                                        exportContext.setHeaderExported();
                                    }
                                }
                            }
                            exportProduct(exportContext, productExportBatch, detailResultSet, dbColumns, dbColumnPositions);
                        }
                        LOG.debug("Product data was exported");
                    } catch (Exception e) {
                        LOG.error("unable to export product details", e);
                        exceptionLatch.countDown();
                    } finally {
                        flexibleSearchExecutionService.closeResultSet(detailResultSet);
                    }

                    return null;
                }
            });
        } finally {
            countDownLatch.countDown();
            sessionService.closeCurrentSession();
        }

        return null;
    }

    protected void init(final SequentialExportContext exportContext) {
        Registry.setCurrentTenant(exportContext.getTenant());
        catalogVersionService.setSessionCatalogVersion(exportContext.getCatalogId(), exportContext.getCatalogVersion());
    }

    protected void parseHeaders(final ResultSet detailResultSet, final Collection<DbColumn> dbColumns,
                                final Map<String, Integer> dbColumnPositions) throws SQLException {
        List<String> resultSetColumns = Arrays.asList(resultSetHelper.getColumnNames(detailResultSet));
        Set<String> baseColumnNames = new HashSet<>();

        int position = 0;
        for (String resultSetColumn : resultSetColumns) {
            dbColumnPositions.put(resultSetColumn, position++);

            String columnName;
            int indexOf = resultSetColumn.indexOf('_');
            if (indexOf > 0) {
                columnName = resultSetColumn.substring(0, indexOf);
            } else {
                columnName = resultSetColumn;
            }

            String baseColumnName = columnName;

            // could be cms site or localized
            Matcher localizedMatcher = LOCALIZED_PATTERN.matcher(resultSetColumn);
            boolean localized = localizedMatcher.find();
            if (localized) {
                baseColumnName = localizedMatcher.group(1);
            }

            boolean encodeFF = ENCODE_FF_PATTERN.matcher(resultSetColumn).matches();
            boolean encodeUrl = ENCODE_URL_PATTERN.matcher(resultSetColumn).matches();

            if (!baseColumnNames.contains(baseColumnName)) {
                baseColumnNames.add(baseColumnName);
                DbColumn dbColumn = new DbColumn(columnName, baseColumnName, localized, encodeFF, encodeUrl);
                dbColumns.add(dbColumn);
            }
        }
    }

    protected void exportHeaders(final SequentialExportContext exportContext, final Collection<DbColumn> dbColumns) {
        String[] columnNames = dbColumns.stream()
            .map(DbColumn::getColumnName)
            .collect(Collectors.toList())
            .toArray(new String[]{});

        for (MediaEntry mediaEntry : exportContext.getMediaEntries()) {
            mediaEntry.getWriter().writeNext(columnNames);
        }
    }

    protected void exportProduct(final SequentialExportContext exportContext, final ProductExportBatch productExportBatch,
                                 final ResultSet detailResultSet, final Collection<DbColumn> dbColumns,
                                 final Map<String, Integer> dbColumnPositions) throws IOException, SQLException {
        String[] columnValues = resultSetHelper.getColumnValues(detailResultSet);

        for (Channel channel : productExportBatch.getChannels()) {
            List<String> values = new ArrayList<>(dbColumns.size());
            for (DbColumn dbColumn : dbColumns) {
                String columnName = dbColumn.getBaseColumnName();

                if (dbColumn.isLocalized()) {
                    columnName = columnName + "_l" + channel.getLanguageIsocode();
                }

                int position = dbColumnPositions.get(columnName);

                String columnValue = columnValues[position];
                if (dbColumn.isEncodeUrl()) {
                    columnValue = DistUtils.urlEncode(columnValue);
                }
                if (dbColumn.isEncodeFF()) {
                    columnValue = DistUtils.encodeFfSpecialChars(columnValue);
                }
                values.add(columnValue);
            }

            CSVWriter writer = exportContext.getMediaEntry(channel.getCode()).getWriter();
            synchronized (writer) {
                writer.writeNext(values.toArray(new String[]{}));
            }
        }
    }
}
