/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.mail.internal;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.search.exceptions.FlexibleSearchException;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.assertTrue;

@IntegrationTest
public class DistMOQOverStockReportDaoIntegrationTest extends ServicelayerTransactionalTest {

    @Resource(name = "DistMOQOverStockReportDao")
    private DistMOQOverStockReportDao distMOQOverStockReportDao;

    @Before
    public void before() throws ImpExException {
        importCsv("/distrelecB2Bcore/test/meshLinking/testDistManufacturers.impex", "utf-8");
        importCsv("/distrelecB2Bcore/test/meshLinking/testCategories.impex", "utf-8");
        importCsv("/distrelecB2Bcore/test/mail/testProducts.impex", "utf-8");
    }

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-13453 - Cron Job - Report on discontinued products with stock lower than MOQ
     * </p>
     * tests that the query is executed without error
     * 
     * @throws FlexibleSearchException
     *             if SQL is wrong or not compatible with used DBMS
     */
    @Test
    public void test() {
        final List<List<String>> result = distMOQOverStockReportDao.getQueryResult();
        assertTrue(result.size() > 0);
    }

}
