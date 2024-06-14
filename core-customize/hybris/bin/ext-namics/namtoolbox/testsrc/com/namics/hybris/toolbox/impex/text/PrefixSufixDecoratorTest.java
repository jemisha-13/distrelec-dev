/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.text;

import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.impex.jalo.Importer;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.Resource;

import com.namics.hybris.toolbox.FileUtils;
import com.namics.hybris.toolbox.items.SessionUtil;

public class PrefixSufixDecoratorTest extends ServicelayerTransactionalTest {

    @Before
    public void setUp() throws Exception {
        createCoreData();
        createDefaultCatalog();
        createDefaultUsers();
        getOrCreateLanguage("de");
        getOrCreateLanguage("en");

        SessionUtil.setTestUserWithDefaultLanguage(SessionUtil.USER_ADMIN);
    }

    @Test
    public void testDecorate() throws ImpExException, FileNotFoundException, IOException, ConsistencyCheckException, JaloPriceFactoryException {
        JaloSession.getCurrentSession().getSessionContext().setLanguage(C2LManager.getInstance().getLanguageByIsoCode("en"));

        final Resource res = FileUtils.createResourceFromFilepath("/test/importexport/PrefixSufixDecoratorTest.impex", true);

        final String impexString = FileUtils.readFile(res.getInputStream());
        final InputStream impexInputStream = new ByteArrayInputStream(impexString.getBytes());

        final Importer importer = ImpExManager.getInstance().importDataLight(impexInputStream, "UTF-8", true);

        Assert.assertFalse("There were errors during import.", importer.hadError());
        Assert.assertFalse("Import was aborted.", importer.isAborted());
        Assert.assertFalse("Import had unresolved lines.", importer.hasUnresolvedLines());

        final CatalogVersion catalogVersion = CatalogManager.getInstance().getCatalog("testCatalog").getCatalogVersion("Online");
        final Product product = catalogVersion.getProduct("myTestProduct");

        Assert.assertNotNull("Product was null.", product);
        Assert.assertEquals("Price wasn't 'testprefix-12-testsufix'.", "testprefix-12-testsufix", product.getDescription());
    }

    @Test
    public void testDecorateWithPlaceholder() throws ImpExException, FileNotFoundException, IOException, ConsistencyCheckException, JaloPriceFactoryException {
        JaloSession.getCurrentSession().getSessionContext().setLanguage(C2LManager.getInstance().getLanguageByIsoCode("en"));

        final Resource res = FileUtils.createResourceFromFilepath("/test/importexport/PrefixSufixDecoratorWithPlaceholderTest.impex", true);

        final String impexString = FileUtils.readFile(res.getInputStream());
        final InputStream impexInputStream = new ByteArrayInputStream(impexString.getBytes());

        final Importer importer = ImpExManager.getInstance().importDataLight(impexInputStream, "UTF-8", true);

        Assert.assertFalse("There were errors during import.", importer.hadError());
        Assert.assertFalse("Import was aborted.", importer.isAborted());
        Assert.assertFalse("Import had unresolved lines.", importer.hasUnresolvedLines());

        final CatalogVersion catalogVersion = CatalogManager.getInstance().getCatalog("testCatalog").getCatalogVersion("Online");
        final Product product = catalogVersion.getProduct("myTestProduct");

        Assert.assertNotNull("Product was null.", product);
        Assert.assertEquals("Price wasn't 'testprefix -12- value of placeholder'.", "testprefix -12- value of placeholder", product.getDescription());
    }

    @Test
    public void testDecorateWithEmptyValues() throws ImpExException, FileNotFoundException, IOException, ConsistencyCheckException, JaloPriceFactoryException {
        JaloSession.getCurrentSession().getSessionContext().setLanguage(C2LManager.getInstance().getLanguageByIsoCode("en"));

        final Resource res = FileUtils.createResourceFromFilepath("/test/importexport/PrefixSufixDecoratorWithEmptyValues.impex", true);

        final String impexString = FileUtils.readFile(res.getInputStream());
        final InputStream impexInputStream = new ByteArrayInputStream(impexString.getBytes());

        final Importer importer = ImpExManager.getInstance().importDataLight(impexInputStream, "UTF-8", true);

        Assert.assertFalse("There were errors during import.", importer.hadError());
        Assert.assertFalse("Import was aborted.", importer.isAborted());
        Assert.assertFalse("Import had unresolved lines.", importer.hasUnresolvedLines());

        final CatalogVersion catalogVersion = CatalogManager.getInstance().getCatalog("testCatalog").getCatalogVersion("Online");
        final Product product1 = catalogVersion.getProduct("myTestProduct1");
        final Product product2 = catalogVersion.getProduct("myTestProduct2");

        Assert.assertNotNull("Product was null.", product1);
        Assert.assertNotNull("Product was null.", product2);
        Assert.assertEquals("Price wasn't 'testprefixvalue of placeholder'.", "testprefixvalue of placeholder", product1.getDescription());
        Assert.assertEquals("Price wasn't null.", null, product2.getDescription());
    }

}
