/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.text;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.impex.jalo.Importer;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.namics.hybris.toolbox.FileUtils;
import com.namics.hybris.toolbox.items.SessionUtil;

public class ReplaceCertainTextDecoratorTest extends ServicelayerTransactionalTest {

    @Resource
    private CommonI18NService commonI18NService;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Resource
    private ProductService productService;

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
    public void testDecorate() throws Exception {
        commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));

        final org.springframework.core.io.Resource res = FileUtils.createResourceFromFilepath("/test/importexport/ReplaceCertainTextDecorator.impex", true);

        final String impexString = FileUtils.readFile(res.getInputStream());
        final InputStream impexInputStream = new ByteArrayInputStream(impexString.getBytes());

        final Importer importer = ImpExManager.getInstance().importDataLight(impexInputStream, "UTF-8", true);

        Assert.assertFalse("There were errors during import.", importer.hadError());
        Assert.assertFalse("Import was aborted.", importer.isAborted());
        Assert.assertFalse("Import had unresolved lines.", importer.hasUnresolvedLines());

        final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
        final ProductModel product = productService.getProductForCode(catalogVersion, "myTestProduct");

        Assert.assertNotNull("Product was null.", product);
        Assert.assertEquals("Description wasn't 'cbcbcbcb'.", "cbcbcbcb", product.getDescription());
    }

    @Test
    @Ignore("Läuft nicht mehr, weil Impex im File ReplaceCertainTextDecoratorWithWhitespaceReplace.impex nicht tut.")
    public void testDecorateWithWhitespaceReplace() throws Exception {
        commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));

        final org.springframework.core.io.Resource res = FileUtils.createResourceFromFilepath(
                "/test/importexport/ReplaceCertainTextDecoratorWithWhitespaceReplace.impex", true);

        final String impexString = FileUtils.readFile(res.getInputStream());
        final InputStream impexInputStream = new ByteArrayInputStream(impexString.getBytes());

        final Importer importer = ImpExManager.getInstance().importDataLight(impexInputStream, "UTF-8", true);

        Assert.assertFalse("There were errors during import.", importer.hadError());
        Assert.assertFalse("Import was aborted.", importer.isAborted());
        Assert.assertFalse("Import had unresolved lines.", importer.hasUnresolvedLines());

        final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
        final ProductModel product = productService.getProductForCode(catalogVersion, "myTestProduct");

        Assert.assertNotNull("Product was null.", product);
        Assert.assertEquals("Description wasn't 'Multi Line Description Multi Test Line'.", "Multi Line Description Multi Test Line", product.getDescription());
    }
}
