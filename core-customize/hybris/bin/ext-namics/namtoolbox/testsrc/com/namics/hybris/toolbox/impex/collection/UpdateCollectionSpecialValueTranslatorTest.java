/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.collection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;

import com.namics.hybris.toolbox.FileUtils;
import com.namics.hybris.toolbox.items.SessionUtil;

import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.category.jalo.CategoryManager;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.impex.jalo.Importer;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

/**
 * Test class for {@link UpdateCollectionSpecialValueTranslator}.
 * 
 * @author jweiss, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class UpdateCollectionSpecialValueTranslatorTest extends ServicelayerTransactionalTest {

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
    @Test
    public void testPerformImport() throws ImpExException, IOException, ConsistencyCheckException {
        final Resource res = FileUtils.createResourceFromFilepath("/test/importexport/UpdateCollectionSpecialValueTranslatorTest.impex", true);

        final String impexString = FileUtils.readFile(res.getInputStream());
        final InputStream impexInputStream = new ByteArrayInputStream(impexString.getBytes());

        final Importer importer = ImpExManager.getInstance().importDataLight(impexInputStream, "UTF-8", true);

        Assert.assertFalse("There were errors during import.", importer.hadError());
        Assert.assertFalse("Import was aborted.", importer.isAborted());
        Assert.assertFalse("Import had unresolved lines.", importer.hasUnresolvedLines());

        final CatalogVersion catalogVersion = CatalogManager.getInstance().getCatalog("testCatalog").getCatalogVersion("Online");
        final Product product = catalogVersion.getProduct("myTestProduct");

        Assert.assertNotNull("Product was null.", product);
        Assert.assertEquals("Product had not two categories.", 2, CategoryManager.getInstance().getCategoriesByProduct(product).size());
    }

}
