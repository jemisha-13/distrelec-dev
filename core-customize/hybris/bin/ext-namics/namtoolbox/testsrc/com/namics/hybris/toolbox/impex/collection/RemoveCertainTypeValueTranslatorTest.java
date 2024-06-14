/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.collection;

import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.category.jalo.Category;
import de.hybris.platform.category.jalo.CategoryManager;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.impex.jalo.Importer;
import de.hybris.platform.jalo.ConsistencyCheckException;
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
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.Resource;

import com.namics.hybris.toolbox.FileUtils;
import com.namics.hybris.toolbox.items.SessionUtil;

/**
 * Test class for {@link RemoveCertainTypeValueTranslator}.
 * 
 * @author jweiss, namics ag
 * @since MGB MEL 1.0
 * 
 */
@SuppressWarnings("all")
public class RemoveCertainTypeValueTranslatorTest extends ServicelayerTransactionalTest {

    /**
     * Setup before test case.
     */
    @Before
    public void setUp() throws Exception {
        createCoreData();
        createDefaultCatalog();
        createDefaultUsers();
        getOrCreateLanguage("de");

        SessionUtil.setTestUserWithDefaultLanguage(SessionUtil.USER_ADMIN);
    }

    /**
     * Test import.
     * 
     * @throws ImpExException
     *             Thrown exception during import.
     * @throws IOException
     *             Thrown exception during import.
     * @throws ConsistencyCheckException
     *             Thrown exception during import.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testPerformImport() throws ImpExException, IOException, ConsistencyCheckException {
        final Resource res = FileUtils.createResourceFromFilepath("/test/importexport/RemoveCertainTypeValueTranslator.impex", true);

        final String impexString = FileUtils.readFile(res.getInputStream());
        final InputStream impexInputStream = new ByteArrayInputStream(impexString.getBytes());

        final Importer importer = ImpExManager.getInstance().importDataLight(impexInputStream, "UTF-8", true);

        Assert.assertFalse("There were errors during import.", importer.hadError());
        Assert.assertFalse("Import was aborted.", importer.isAborted());
        Assert.assertFalse("Import had unresolved lines.", importer.hasUnresolvedLines());

        final CatalogVersion catalogVersion = CatalogManager.getInstance().getCatalog("testCatalog").getCatalogVersion("Online");
        final Product product = catalogVersion.getProduct("myTestProduct");

        final Category newCreatedCategory1 = CategoryManager.getInstance().getCategoryByCode("myTestCategory1");
        final Category newCreatedCategory2 = CategoryManager.getInstance().getCategoryByCode("myTestCategory2");
        final Category newCreatedClassificationClass1 = CategoryManager.getInstance().getCategoryByCode("myTestClassificationClass1");
        final Category newCreatedClassificationClass2 = CategoryManager.getInstance().getCategoryByCode("myTestClassificationClass2");

        Assert.assertTrue("The category '" + newCreatedClassificationClass1 + "', that should stay in the collection, was removed.", CategoryManager
                .getInstance().getSupercategories(product).contains(newCreatedClassificationClass1));
        Assert.assertTrue("The category '" + newCreatedClassificationClass2 + "', that should stay in the collection, was removed.", CategoryManager
                .getInstance().getSupercategories(product).contains(newCreatedClassificationClass2));
        Assert.assertTrue("The category '" + newCreatedCategory1 + "', that should stay in the collection, was removed.", CategoryManager.getInstance()
                .getSupercategories(product).contains(newCreatedCategory1));
        Assert.assertFalse("The category '" + newCreatedCategory2 + "', that should be removed in the collection, is still in the collection.", CategoryManager
                .getInstance().getSupercategories(product).contains(newCreatedCategory2));

    }

}
