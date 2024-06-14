/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.mail.internal;

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.exceptions.FlexibleSearchException;

@IntegrationTest
public class DistProductsPerWebshopDaoIntegrationTest extends ServicelayerTransactionalTest {

    private static final Logger LOG = LogManager.getLogger(DistProductsPerWebshopDaoIntegrationTest.class);

    @Resource
    private DistProductsPerWebshopDao distProductsPerWebshopDao;

    @Resource
    private FlexibleSearchService flexibleSearchService;

    @Before
    public void before() throws ImpExException {
        importCsv("/distrelecB2Bcore/test/meshLinking/testDistManufacturers.impex", "utf-8");
        importCsv("/distrelecB2Bcore/test/meshLinking/testCategories.impex", "utf-8");
        importCsv("/distrelecB2Bcore/test/mail/testProducts.impex", "utf-8");
    }

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-13885 - Update SKU volume report sent to Product Team
     * </p>
     * tests that the query is executed without error
     * 
     * @throws FlexibleSearchException
     *             if SQL is wrong or not compatible with used DBMS
     */
    @Ignore
    @Test
    public void test() {
        final List<List<String>> result = distProductsPerWebshopDao.getQueryResult();
        assertTrue(result.size() > 0);
    }

}
