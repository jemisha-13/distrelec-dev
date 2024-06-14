/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.enums.DistCarpetItemSize;
import com.namics.distrelec.b2b.core.model.cms2.components.DistCarpetComponentModel;
import com.namics.distrelec.b2b.core.model.cms2.items.DistCarpetContentTeaserModel;
import com.namics.distrelec.b2b.core.model.cms2.items.DistCarpetItemModel;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Test class for <code>DistHeroRotatingTeaserValidateInterceptor</code>.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DistCarpetComponentValidateInterceptorTest extends ServicelayerTransactionalTest {

    @Resource
    private ModelService modelService;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Resource
    private ProductService productService;

    private List<ProductModel> products;

    @Before
    public void setUp() throws Exception {
        super.createCoreData();
        super.createDefaultCatalog();

        products = new ArrayList<ProductModel>();
        for (int i = 0; i <= 4; i++) {
            products.add(productService.getProductForCode("testProduct" + i));
        }
    }

    @Test
    public void testWithoutOption() {
        // TODO
        // final DistCarpetComponentModel carpetModel = createCarpetComponentModel();
        // try {
        // modelService.save(carpetModel);
        // Assert.fail();
        // } catch (Exception e) {
        // Assert.assertTrue(e.getMessage().contains("validations.distcarpet.nooptionselected"));
        // }

    }

    @Test
    public void testWithMultipleOptions() {
        // TODO
        // final DistCarpetComponentModel carpetModel = createCarpetComponentModel();

        // Set the query and items
        // final DistCarpetItemModel itemModel = createCarpetItemModel();

        // final DistCarpetContentTeaserModel contentTeaserModel = modelService.create(DistCarpetContentTeaserModel.class);
        // contentTeaserModel.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
        // contentTeaserModel.setUid("testUid");

        // final MediaModel smallImage = modelService.create(MediaModel.class);
        // smallImage.setCode("testCode");
        // contentTeaserModel.setImageSmall(smallImage);

        // itemModel.setContentTeaser(contentTeaserModel);

        // final List<DistCarpetItemModel> column1 = new ArrayList<DistCarpetItemModel>();
        // column1.add(itemModel);

        // carpetModel.setCarpetColumn1Items(column1);

        // query
        // carpetModel.setSearchQuery("USB");

        // try {
        // modelService.save(carpetModel);
        // Assert.fail();
        // } catch (Exception e) {
        // Assert.assertTrue(e.getMessage().contains("validations.distcarpet.multipleoptionsselected"));
        // }
    }

    @Test
    public void testWrongColumnLength() {
        // TODO
        // final DistCarpetComponentModel carpetModel = createCarpetComponentModel();

        // Set items
        // final DistCarpetItemModel itemModel1 = createCarpetItemModel(DistCarpetItemSize.SMALL);
        // itemModel1.setProduct(productService.getProductForCode("testProduct1"));

        // final List<DistCarpetItemModel> column1 = new ArrayList<DistCarpetItemModel>();
        // column1.add(itemModel1);
        // carpetModel.setCarpetColumn1Items(column1);

        // final DistCarpetItemModel itemModel2 = createCarpetItemModel(DistCarpetItemSize.SMALL);
        // itemModel2.setProduct(productService.getProductForCode("testProduct2"));

        // final List<DistCarpetItemModel> column2 = new ArrayList<DistCarpetItemModel>();
        // column2.add(itemModel2);
        // carpetModel.setCarpetColumn2Items(column2);

        // final DistCarpetItemModel itemModel3 = createCarpetItemModel(DistCarpetItemSize.LARGE);
        // itemModel3.setProduct(productService.getProductForCode("testProduct3"));

        // final List<DistCarpetItemModel> column3 = new ArrayList<DistCarpetItemModel>();
        // column3.add(itemModel3);
        // carpetModel.setCarpetColumn3Items(column3);

        // try {
        // modelService.save(carpetModel);
        // Assert.fail();
        // } catch (Exception e) {
        // Assert.assertTrue(e.getMessage().contains("validations.distcarpet.columnslengthnotequal"));
        // }
    }

    @Test
    public void testDuplicateItems() {
        final DistCarpetComponentModel carpetModel = createCarpetComponentModel();

        // Set items
        final DistCarpetItemModel itemModel = createCarpetItemModel();

        final DistCarpetContentTeaserModel contentTeaserModel = modelService.create(DistCarpetContentTeaserModel.class);
        contentTeaserModel.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
        contentTeaserModel.setUid("testUid");

        final MediaModel smallImage = modelService.create(MediaModel.class);
        smallImage.setCode("testCode");
        contentTeaserModel.setImageSmall(smallImage);

        itemModel.setContentTeaser(contentTeaserModel);

        final List<DistCarpetItemModel> column1 = new ArrayList<DistCarpetItemModel>();
        column1.add(itemModel);
        carpetModel.setCarpetColumn1Items(column1);

        final List<DistCarpetItemModel> column2 = new ArrayList<DistCarpetItemModel>();
        column2.add(itemModel);
        carpetModel.setCarpetColumn2Items(column2);

        final List<DistCarpetItemModel> column3 = new ArrayList<DistCarpetItemModel>();
        column3.add(itemModel);
        carpetModel.setCarpetColumn3Items(column3);

        try {
            modelService.save(carpetModel);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("validations.distcarpet.duplicateitem"));
        }
    }

    @Test
    public void testDuplicateProducts() {
        final DistCarpetComponentModel carpetModel = createCarpetComponentModel();

        // Set items
        final DistCarpetItemModel itemModel1 = createCarpetItemModel();
        itemModel1.setProduct(productService.getProductForCode("testProduct1"));

        final List<DistCarpetItemModel> column1 = new ArrayList<DistCarpetItemModel>();
        column1.add(itemModel1);
        carpetModel.setCarpetColumn1Items(column1);

        final DistCarpetItemModel itemModel2 = createCarpetItemModel();
        itemModel2.setProduct(productService.getProductForCode("testProduct1"));

        final List<DistCarpetItemModel> column2 = new ArrayList<DistCarpetItemModel>();
        column2.add(itemModel2);
        carpetModel.setCarpetColumn2Items(column2);

        final DistCarpetItemModel itemModel3 = createCarpetItemModel();
        itemModel3.setProduct(productService.getProductForCode("testProduct1"));

        final List<DistCarpetItemModel> column3 = new ArrayList<DistCarpetItemModel>();
        column3.add(itemModel3);
        carpetModel.setCarpetColumn3Items(column3);

        try {
            modelService.save(carpetModel);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("validations.distcarpet.duplicateproduct"));
        }
    }

    @Test
    public void testQuerryMinResult() {
        final DistCarpetComponentModel carpetModel = createCarpetComponentModel();
        carpetModel.setSearchQuery("wrongQuery");
        carpetModel.setMaxSearchResults(Integer.valueOf(2));

        try {
            modelService.save(carpetModel);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("validations.distcarpet.lessminsearchresults"));
        }
    }

    @Test
    public void testItemsOk() {
        final DistCarpetComponentModel carpetModel = createCarpetComponentModel();

        // Set items
        final DistCarpetItemModel itemModel1 = createCarpetItemModel();
        itemModel1.setProduct(productService.getProductForCode("testProduct1"));

        final List<DistCarpetItemModel> column1 = new ArrayList<DistCarpetItemModel>();
        column1.add(itemModel1);
        carpetModel.setCarpetColumn1Items(column1);

        final DistCarpetItemModel itemModel2 = createCarpetItemModel();
        itemModel2.setProduct(productService.getProductForCode("testProduct2"));

        final List<DistCarpetItemModel> column2 = new ArrayList<DistCarpetItemModel>();
        column2.add(itemModel2);
        carpetModel.setCarpetColumn2Items(column2);

        final DistCarpetItemModel itemModel3 = createCarpetItemModel();
        itemModel3.setProduct(productService.getProductForCode("testProduct3"));

        final List<DistCarpetItemModel> column3 = new ArrayList<DistCarpetItemModel>();
        column3.add(itemModel3);
        carpetModel.setCarpetColumn3Items(column3);

        try {
            modelService.save(carpetModel);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertNotNull(carpetModel.getPk());
    }

    @Test
    public void testQueryOk() {
        final DistCarpetComponentModel carpetModel = createCarpetComponentModel();

        // query
        carpetModel.setSearchQuery("USB");

        try {
            modelService.save(carpetModel);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertNotNull(carpetModel.getPk());
    }

    private DistCarpetComponentModel createCarpetComponentModel() {
        final DistCarpetComponentModel component = modelService.create(DistCarpetComponentModel.class);
        component.setUid("testComponent_" + UUID.randomUUID().toString());
        component.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
        return component;
    }

    private DistCarpetItemModel createCarpetItemModel() {
        final DistCarpetItemModel itemModel = modelService.create(DistCarpetItemModel.class);
        itemModel.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
        itemModel.setUid("testItem_" + UUID.randomUUID().toString());
        return itemModel;
    }

    private DistCarpetItemModel createCarpetItemModel(final DistCarpetItemSize size) {
        final DistCarpetItemModel itemModel = createCarpetItemModel();
        itemModel.setSize(size);
        return itemModel;
    }
}
