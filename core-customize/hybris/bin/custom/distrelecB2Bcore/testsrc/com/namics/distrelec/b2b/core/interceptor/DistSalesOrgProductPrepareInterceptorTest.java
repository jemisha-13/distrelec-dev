/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.enums.DistErpSystem;
import com.namics.distrelec.b2b.core.model.DistBrandModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Test class for <code>DistSalesOrgProductPrepareInterceptor</code>.
 * 
 * @author daehusir, Distrelec
 * 
 */
public class DistSalesOrgProductPrepareInterceptorTest extends ServicelayerTransactionalTest {

    private ProductModel product;
    private DistSalesOrgProductModel distSalesOrgProduct;
    private DistSalesOrgModel salesOrg;
    private DistSalesStatusModel statusNormal;
    private DistSalesStatusModel statusNew;
    private DistSalesStatusModel statusEol;
    private DistSalesStatusModel statusPhaseOut;

    @Resource
    private ModelService modelService;

    @Resource
    private ProductService productService;

    @Resource
    private CommonI18NService commonI18NService;

    @Before
    public void setUp() throws Exception {
        super.createCoreData();
        super.createDefaultCatalog();
        product = productService.getProductForCode("testProduct0");
        Assert.assertNotNull(product);

        final DistBrandModel brand = modelService.create(DistBrandModel.class);
        brand.setCode("DIS");
        brand.setName("Distrelec");

        final VendorModel vendor = modelService.create(VendorModel.class);
        vendor.setCode("Default");
        vendor.setName("Default Vendor");

        final WarehouseModel warehouse = modelService.create(WarehouseModel.class);
        warehouse.setCode("Default");
        warehouse.setName("Default Warehouse");
        warehouse.setVendor(vendor);

        salesOrg = modelService.create(DistSalesOrgModel.class);
        salesOrg.setCode("7310");
        salesOrg.setName("Switzerland");
        salesOrg.setBrand(brand);
        salesOrg.setErpSystem(DistErpSystem.SAP);
        salesOrg.setCountry(commonI18NService.getCountry("CH"));
        modelService.save(salesOrg);

        statusNormal = modelService.create(DistSalesStatusModel.class);
        statusNormal.setCode("Normal");
        statusNormal.setBuyableInShop(true);
        statusNormal.setVisibleInShop(true);
        statusNormal.setNewInShop(false);
        statusNormal.setEndOfLifeInShop(false);
        modelService.save(statusNormal);

        statusNew = modelService.create(DistSalesStatusModel.class);
        statusNew.setCode("New");
        statusNew.setBuyableInShop(true);
        statusNew.setVisibleInShop(true);
        statusNew.setNewInShop(true);
        statusNew.setEndOfLifeInShop(false);
        modelService.save(statusNew);

        statusEol = modelService.create(DistSalesStatusModel.class);
        statusEol.setCode("EOL");
        statusEol.setBuyableInShop(false);
        statusEol.setVisibleInShop(true);
        statusEol.setNewInShop(false);
        statusEol.setEndOfLifeInShop(true);
        modelService.save(statusEol);

        statusPhaseOut = modelService.create(DistSalesStatusModel.class);
        statusPhaseOut.setCode("PhaseOut");
        statusPhaseOut.setBuyableInShop(false);
        statusPhaseOut.setVisibleInShop(true);
        statusPhaseOut.setNewInShop(false);
        statusPhaseOut.setEndOfLifeInShop(false);
        modelService.save(statusPhaseOut);
    }

    @Test
    public void testBuyableInShopStatusChange() throws Exception {
        distSalesOrgProduct = modelService.create(DistSalesOrgProductModel.class);
        distSalesOrgProduct.setSalesStatus(statusNormal);
        distSalesOrgProduct.setSalesOrg(salesOrg);
        distSalesOrgProduct.setProduct(product);
        modelService.save(distSalesOrgProduct);

        modelService.refresh(distSalesOrgProduct);
        Assert.assertNull(distSalesOrgProduct.getEndOfLifeDate());
        Assert.assertNull(distSalesOrgProduct.getShowNewLabelFromDate());
        Assert.assertNull(distSalesOrgProduct.getShowNewLabelUntilDate());

        distSalesOrgProduct.setSalesStatus(statusEol);
        modelService.save(distSalesOrgProduct);

        modelService.refresh(distSalesOrgProduct);
        Assert.assertNotNull(distSalesOrgProduct.getEndOfLifeDate());
        Assert.assertNull(distSalesOrgProduct.getShowNewLabelFromDate());
        Assert.assertNull(distSalesOrgProduct.getShowNewLabelUntilDate());

        distSalesOrgProduct.setSalesStatus(statusNormal);
        modelService.save(distSalesOrgProduct);

        modelService.refresh(distSalesOrgProduct);
        Assert.assertNull(distSalesOrgProduct.getEndOfLifeDate());
        Assert.assertNull(distSalesOrgProduct.getShowNewLabelFromDate());
        Assert.assertNull(distSalesOrgProduct.getShowNewLabelUntilDate());
    }

    @Test
    public void testNewInShopStatusChange() throws Exception {
        distSalesOrgProduct = modelService.create(DistSalesOrgProductModel.class);
        distSalesOrgProduct.setSalesStatus(statusNew);
        distSalesOrgProduct.setSalesOrg(salesOrg);
        distSalesOrgProduct.setProduct(product);
        modelService.save(distSalesOrgProduct);

        modelService.refresh(distSalesOrgProduct);
        Assert.assertNull(distSalesOrgProduct.getEndOfLifeDate());
        Assert.assertNull(distSalesOrgProduct.getShowNewLabelFromDate());
        Assert.assertNull(distSalesOrgProduct.getShowNewLabelUntilDate());

        distSalesOrgProduct.setSalesStatus(statusNormal);
        modelService.save(distSalesOrgProduct);

        modelService.refresh(distSalesOrgProduct);
        Assert.assertNull(distSalesOrgProduct.getShowNewLabelFromDate());
        Assert.assertNull(distSalesOrgProduct.getShowNewLabelUntilDate());

        distSalesOrgProduct.setSalesStatus(statusNew);
        modelService.save(distSalesOrgProduct);

        modelService.refresh(distSalesOrgProduct);
        Assert.assertNull(distSalesOrgProduct.getShowNewLabelFromDate());
        Assert.assertNull(distSalesOrgProduct.getShowNewLabelUntilDate());
    }

    @Test
    public void testPhaseOutInShopStatusChange() throws Exception {
        distSalesOrgProduct = modelService.create(DistSalesOrgProductModel.class);
        distSalesOrgProduct.setSalesStatus(statusNormal);
        distSalesOrgProduct.setSalesOrg(salesOrg);
        distSalesOrgProduct.setProduct(product);
        modelService.save(distSalesOrgProduct);

        modelService.refresh(distSalesOrgProduct);
        Assert.assertNull(distSalesOrgProduct.getEndOfLifeDate());
        Assert.assertNull(distSalesOrgProduct.getShowNewLabelFromDate());
        Assert.assertNull(distSalesOrgProduct.getShowNewLabelUntilDate());

        distSalesOrgProduct.setSalesStatus(statusPhaseOut);
        modelService.save(distSalesOrgProduct);

        modelService.refresh(distSalesOrgProduct);
        Assert.assertNull(distSalesOrgProduct.getEndOfLifeDate());
        Assert.assertNull(distSalesOrgProduct.getShowNewLabelFromDate());
        Assert.assertNull(distSalesOrgProduct.getShowNewLabelUntilDate());

        distSalesOrgProduct.setSalesStatus(statusNormal);
        modelService.save(distSalesOrgProduct);

        modelService.refresh(distSalesOrgProduct);
        Assert.assertNull(distSalesOrgProduct.getShowNewLabelFromDate());
        Assert.assertNull(distSalesOrgProduct.getShowNewLabelUntilDate());
    }
}
