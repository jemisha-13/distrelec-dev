/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.model.DistMaterialTypeModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Test class for <code>ProductShowUsedLabelValidateInterceptor</code>.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class ProductShowUsedLabelValidateInterceptorTest extends ServicelayerTransactionalTest {

    private ProductModel product;
    private DistMaterialTypeModel zocc;
    private DistMaterialTypeModel zcon;

    @Resource
    private ModelService modelService;

    @Resource
    private ProductService productService;

    @Resource
    private DistrelecCodelistService distCodelistService;

    @Before
    public void setUp() throws Exception {
        super.createCoreData();
        super.createDefaultCatalog();
        importCsv("/distrelecB2Bcore/test/testErpCodelist.impex", "utf-8");

        product = productService.getProductForCode("testProduct0");
        Assert.assertNotNull(product);

        zocc = distCodelistService.getDistrelecMaterialType("ZOCC");
        Assert.assertNotNull(zocc);

        zcon = distCodelistService.getDistrelecMaterialType("ZCON");
        Assert.assertNotNull(zcon);
    }

    @Test
    public void testMaterialTypeIsZOCC() {
        product.setMaterialType(zocc);
        modelService.save(product);

        Assert.assertTrue(product.isShowUsedLabel());
    }

    @Test
    public void testMaterialTypeIsNotZOCC() {
        product.setMaterialType(zcon);
        modelService.save(product);

        Assert.assertFalse(product.isShowUsedLabel());
    }

    @Test
    public void testMaterialTypeIsNull() {
        product.setMaterialType(null);
        modelService.save(product);

        Assert.assertFalse(product.isShowUsedLabel());
    }
}
