/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.export;

import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchExecutionService;
import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchParameterProvider;
import com.namics.distrelec.b2b.core.inout.export.exception.DistFlexibleSearchExecutionException;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;
import com.namics.hybris.ffsearch.export.query.DistFactFinderProductExportQueryCreator3;
import com.namics.hybris.ffsearch.model.DistFactFinderExportChannelModel;
import com.namics.hybris.ffsearch.model.export.DistFactFinderExportCronJobModel;
import com.namics.hybris.ffsearch.service.FactFinderChannelService;
import com.opencsv.ResultSetHelper;
import com.opencsv.ResultSetHelperService;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.exceptions.FlexibleSearchException;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@IntegrationTest
public class DistFactFinderProductExportQueryCreator3IntegrationTest extends ServicelayerTransactionalTest {

    private static final String CH_GER_CHANNEL = "distrelec_7310_ch_de";

    private static final Logger LOG = LogManager.getLogger(DistFactFinderProductExportQueryCreator3IntegrationTest.class);

    @Resource(name = "distff.distFactFinderProductExportQueryCreator3")
    DistFactFinderProductExportQueryCreator3 distFactFinderProductExportQueryCreator3;

    @Resource(name = "distff.productExportParameterProvider")
    DistFlexibleSearchParameterProvider distFlexibleSearchParameterProvider;

    @Resource(name = "distff.channelService")
    FactFinderChannelService factFinderChannelService;

    @Resource(name = "core.distDefaultFlexibleSearchExecutionService")
    DistFlexibleSearchExecutionService distFlexibleSearchExecutionService;

    @Resource
    SessionService sessionService;

    @Resource(name = "distff.factFinderExportJob")
    DistFactFinderExportJob distFactFinderExportJob;

    @Resource
    CronJobService cronJobService;

    @Resource
    CatalogVersionService catalogVersionService;

    @Before
    public void setupSession() throws ImpExException {
        assertNotNull(getSessionService().getCurrentSession());
        importCsv("/distrelecfactfindersearch/test/testFFProductExportQuery.impex", "utf-8");
    }

    /**
     * Checks that the attributes {@link DistFactFinderExportCronJobModel} runs without errors
     */
    @Ignore
    @Test
    public void testCronjob() {
        final DistFactFinderExportCronJobModel cronJob = (DistFactFinderExportCronJobModel) getCronJobService()
                .getCronJob("distrelec_7310_ch_de_export");

        final DistFactFinderExportChannelModel channel = cronJob.getChannel();
        assertNotNull(channel);
        assertNotNull(channel.getCatalogVersion().getCatalog().getId());
        assertNotNull(channel.getCatalogVersion().getVersion());
        assertNotNull(channel.getLanguage());

        final PerformResult result = getDistFactFinderExportJob().perform(cronJob);
        assertEquals(CronJobResult.SUCCESS, result.getResult());
    }

    /**
     * <p>
     * test that the query is executed without error and header columns are like expected
     * </p>
     * 
     * @throws DistFlexibleSearchExecutionException
     *             if SQL is wrong or not compatible with used DBMS
     * 
     * @throws SQLException
     *             if there is an error in accessing the ResultSet
     */
    @Ignore
    @Test
    public void testResult_FactFinderExport() throws SQLException {
        final String query = getDistFactFinderProductExportQueryCreator3().createQuery();
        final DistFactFinderExportChannelModel gerChChannel = getFactFinderChannelService().getChannelForCode(CH_GER_CHANNEL);
        final Map<String, Object> parameters = getDistFlexibleSearchParameterProvider()
                .getParameters(gerChChannel);

        // Check that query works
        ResultSet resultSet;
        try {
            resultSet = getDistFlexibleSearchExecutionService().execute(query, parameters);
        } catch (final FlexibleSearchException | DistFlexibleSearchExecutionException e) {
            LOG.error(new ParameterizedMessage("Exception executing query: [{}] with parameters: {}", query, parameters.toString()), e);
            fail(e.getMessage());
            return;
        }

        // Check header columns are the same as expected
        final List<String> expectedColumns = Arrays.asList(DistFactFinderExportColumns.values()).stream().filter(c -> c.isExported()).map(c -> c.getValue())
                .collect(Collectors.toList());
        final ResultSetHelper resultSetHelper = new ResultSetHelperService();
        final List<String> resultSetColumns = Arrays.asList(resultSetHelper.getColumnNames(resultSet));
        LOG.info(Arrays.toString(resultSetColumns.toArray()));
        assertEquals(expectedColumns, resultSetColumns);

        // TODO Import a product which has these properties
        // resultSet.next();
        // assertEquals("cps1|cps2", resultSet.getString(CURATED_PRODUCT_SELECTION_POSITION));
        // assertEquals("ct1|ct2", resultSet.getString(CAMPAIGN_TAG_POSITION));
    }

    public DistFactFinderProductExportQueryCreator3 getDistFactFinderProductExportQueryCreator3() {
        return distFactFinderProductExportQueryCreator3;
    }

    public void setDistFactFinderProductExportQueryCreator3(final DistFactFinderProductExportQueryCreator3 distFactFinderProductExportQueryCreator3) {
        this.distFactFinderProductExportQueryCreator3 = distFactFinderProductExportQueryCreator3;
    }

    public DistFlexibleSearchParameterProvider getDistFlexibleSearchParameterProvider() {
        return distFlexibleSearchParameterProvider;
    }

    public void setDistFlexibleSearchParameterProvider(final DistFlexibleSearchParameterProvider distFlexibleSearchParameterProvider) {
        this.distFlexibleSearchParameterProvider = distFlexibleSearchParameterProvider;
    }

    public FactFinderChannelService getFactFinderChannelService() {
        return factFinderChannelService;
    }

    public void setFactFinderChannelService(final FactFinderChannelService factFinderChannelService) {
        this.factFinderChannelService = factFinderChannelService;
    }

    public DistFlexibleSearchExecutionService getDistFlexibleSearchExecutionService() {
        return distFlexibleSearchExecutionService;
    }

    public void setDistFlexibleSearchExecutionService(final DistFlexibleSearchExecutionService distFlexibleSearchExecutionService) {
        this.distFlexibleSearchExecutionService = distFlexibleSearchExecutionService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public DistFactFinderExportJob getDistFactFinderExportJob() {
        return distFactFinderExportJob;
    }

    public void setDistFactFinderExportJob(final DistFactFinderExportJob distFactFinderExportJob) {
        this.distFactFinderExportJob = distFactFinderExportJob;
    }

    public CronJobService getCronJobService() {
        return cronJobService;
    }

    public void setCronJobService(final CronJobService cronJobService) {
        this.cronJobService = cronJobService;
    }

    public CatalogVersionService getCatalogVersionService() {
        return catalogVersionService;
    }

    public void setCatalogVersionService(final CatalogVersionService catalogVersionService) {
        this.catalogVersionService = catalogVersionService;
    }

}
