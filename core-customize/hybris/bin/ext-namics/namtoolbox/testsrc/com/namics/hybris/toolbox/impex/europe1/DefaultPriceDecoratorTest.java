/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.europe1;

import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.europe1.jalo.Europe1PriceFactory;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.impex.jalo.Importer;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.testframework.HybrisJUnit4ClassRunner;
import de.hybris.platform.testframework.runlistener.ItemCreationListener;
import de.hybris.platform.testframework.runlistener.LogRunListener;
import de.hybris.platform.testframework.runlistener.PlatformRunListener;
import de.hybris.platform.testframework.RunListeners;
import de.hybris.platform.testframework.runlistener.TransactionRunListener;
import de.hybris.platform.testframework.Transactional;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.Resource;

import com.namics.hybris.toolbox.FileUtils;
import com.namics.hybris.toolbox.items.SessionUtil;

public class DefaultPriceDecoratorTest extends ServicelayerTransactionalTest {

    @Before
    public void setUp() throws Exception {
        createCoreData();
        createDefaultCatalog();
        createDefaultUsers();
        getOrCreateLanguage("de");
        getOrCreateLanguage("en");
        getOrCreateCurrency("CHF");

        SessionUtil.setTestUserWithDefaultLanguage(SessionUtil.USER_ADMIN);
    }

    @Test
    @Ignore("Der Test funktioniert nicht, weil USD statt CHF zurückgegeben wird.")
    public void testDecorate() throws ImpExException, FileNotFoundException, IOException, ConsistencyCheckException, JaloPriceFactoryException {
        JaloSession.getCurrentSession().getSessionContext().setLanguage(C2LManager.getInstance().getLanguageByIsoCode("en"));

        final Resource res = FileUtils.createResourceFromFilepath("/test/importexport/DefaultPriceDecoratorTest.impex", true);

        final String impexString = FileUtils.readFile(res.getInputStream());
        final InputStream impexInputStream = new ByteArrayInputStream(impexString.getBytes());

        final Importer importer = ImpExManager.getInstance().importDataLight(impexInputStream, "UTF-8", true);

        Assert.assertFalse("There were errors during import.", importer.hadError());
        Assert.assertFalse("Import was aborted.", importer.isAborted());
        Assert.assertFalse("Import had unresolved lines.", importer.hasUnresolvedLines());

        final CatalogVersion catalogVersion = CatalogManager.getInstance().getCatalog("testCatalog").getCatalogVersion("Online");
        final Product product = catalogVersion.getProduct("myTestProduct");

        Assert.assertNotNull("Product was null.", product);

        @SuppressWarnings("unchecked")
        final List<PriceInformation> prices = (product.getPriceInformations(true));
        boolean foundPrice = false;
        for (final PriceInformation priceInformation : prices) {
            if ("CHF".equals(priceInformation.getPriceValue().getCurrencyIso())) {
                Assert.assertEquals("Price wasn't '123'.", 123, (int) priceInformation.getPriceValue().getValue());
                foundPrice = true;
            }
        }
        Assert.assertTrue("Price wasn't found for currency 'CHF'.", foundPrice);
    }

    @Test
    public void testDecorateWithMultipleEntries() throws ImpExException, FileNotFoundException, IOException, ConsistencyCheckException,
            JaloPriceFactoryException {
        JaloSession.getCurrentSession().getSessionContext().setLanguage(C2LManager.getInstance().getLanguageByIsoCode("en"));

        final Resource res = FileUtils.createResourceFromFilepath("/test/importexport/DefaultPriceDecoratorTestWithMultipleEntries.impex", true);

        final String impexString = FileUtils.readFile(res.getInputStream());
        final InputStream impexInputStream = new ByteArrayInputStream(impexString.getBytes());

        final Importer importer = ImpExManager.getInstance().importDataLight(impexInputStream, "UTF-8", true);

        Assert.assertFalse("There were errors during import.", importer.hadError());
        Assert.assertFalse("Import was aborted.", importer.isAborted());
        Assert.assertFalse("Import had unresolved lines.", importer.hasUnresolvedLines());

        final CatalogVersion catalogVersion = CatalogManager.getInstance().getCatalog("testCatalog").getCatalogVersion("Online");
        final Product product = catalogVersion.getProduct("myTestProduct");

        Assert.assertNotNull("Product was null.", product);
        Assert.assertEquals("No 3 PriceRows found.", 3, Europe1PriceFactory.getInstance().getProductPriceRows(product, null).size());
    }

    @Test
    @Ignore("Der Test funktioniert nicht, weil USD statt CHF zurückgegeben wird.")
    public void testDecorateWithDateEntry() throws ImpExException, FileNotFoundException, IOException, ConsistencyCheckException, JaloPriceFactoryException {
        JaloSession.getCurrentSession().getSessionContext().setLanguage(C2LManager.getInstance().getLanguageByIsoCode("en"));

        final Resource res = FileUtils.createResourceFromFilepath("/test/importexport/DefaultPriceDecoratorTestWithDateEntries.impex", true);

        final String impexString = FileUtils.readFile(res.getInputStream());
        final InputStream impexInputStream = new ByteArrayInputStream(impexString.getBytes());

        final Importer importer = ImpExManager.getInstance().importDataLight(impexInputStream, "UTF-8", true);

        Assert.assertFalse("There were errors during import.", importer.hadError());
        Assert.assertFalse("Import was aborted.", importer.isAborted());
        Assert.assertFalse("Import had unresolved lines.", importer.hasUnresolvedLines());

        final CatalogVersion catalogVersion = CatalogManager.getInstance().getCatalog("testCatalog").getCatalogVersion("Online");
        final Product product = catalogVersion.getProduct("myTestProduct");

        Assert.assertNotNull("Product was null.", product);

        final List<PriceInformation> prices = (product.getPriceInformations(true));
        boolean foundPrice = false;
        for (final PriceInformation priceInformation : prices) {
            if ("CHF".equals(priceInformation.getPriceValue().getCurrencyIso())) {
                Assert.assertEquals("Price wasn't '123'.", 123, (int) priceInformation.getPriceValue().getValue());

                Assert.assertEquals("DateStart wasn't 'Tue Jun 01 00:00:00 CEST 2010'.", "Tue Jun 01 00:00:00 CEST 2010", ((PriceRow) Europe1PriceFactory
                        .getInstance().getProductPriceRows(product, null).iterator().next()).getDateRange().getStart().toString());
                Assert.assertEquals("DateEnd wasn't 'Wed Dec 30 23:59:59 CET 2099'.", "Wed Dec 30 23:59:59 CET 2099", ((PriceRow) Europe1PriceFactory
                        .getInstance().getProductPriceRows(product, null).iterator().next()).getDateRange().getEnd().toString());

                foundPrice = true;
            }
        }
        Assert.assertTrue("Price wasn't found for currency 'CHF'.", foundPrice);

    }

}
