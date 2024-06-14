/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.enums.DistCarpetItemSize;
import com.namics.distrelec.b2b.core.model.cms2.items.DistCarpetContentTeaserModel;
import com.namics.distrelec.b2b.core.model.cms2.items.DistCarpetItemModel;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Test class for <code>DistHeroRotatingTeaserValidateInterceptor</code>.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DistCarpetItemValidateInterceptorTest extends ServicelayerTransactionalTest {

    @Resource
    private ModelService modelService;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Resource
    private L10NService l10nService;

    @Before
    public void setUp() throws Exception {

        super.createCoreData();
        super.createDefaultCatalog();

    }

    @Test
    // Create an item but without teaser or product
    public void testItemWithoutOption() {
        final DistCarpetItemModel carpetItemModel = createComponent();

        try {
            modelService.save(carpetItemModel);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(StringUtils.contains(e.getMessage(), l10nService.getLocalizedString("validations.distcarpet.item.nooptionselected")));
        }

    }

    @Test
    // Create an item with a teaser and a product
    public void testItemWithMultipleOptions() {
        final DistCarpetItemModel carpetItemModel = createComponent();

        final ProductModel productModel = modelService.create(ProductModel.class);
        productModel.setCode("P1");
        final DistCarpetContentTeaserModel contentTeaser = modelService.create(DistCarpetContentTeaserModel.class);
        contentTeaser.setUid("T1");
        final MediaModel smallImage = modelService.create(MediaModel.class);
        smallImage.setCode("I1");
        contentTeaser.setImageSmall(smallImage);

        carpetItemModel.setProduct(productModel);
        carpetItemModel.setContentTeaser(contentTeaser);

        try {
            modelService.save(carpetItemModel);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(StringUtils.contains(e.getMessage(), l10nService.getLocalizedString("validations.distcarpet.item.multipleoptionsselected")));
        }
    }

    @Test
    // Create an item and set the size to null
    public void testItemWithoutSize() {
        final DistCarpetItemModel carpetItemModel = createComponent();

        final ProductModel productModel = modelService.create(ProductModel.class);
        productModel.setCode("P2");
        carpetItemModel.setProduct(productModel);

        carpetItemModel.setSize(null);

        try {
            modelService.save(carpetItemModel);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("size"));
        }
    }

    @Test
    // Create an item with all informations need (product version)
    public void testItemWithProductOk() {
        final DistCarpetItemModel carpetItemModel = createComponent();
        carpetItemModel.setSize(DistCarpetItemSize.SMALL);

        final ProductModel productModel = modelService.create(ProductModel.class);
        productModel.setCode("testCode");
        carpetItemModel.setProduct(productModel);

        try {
            modelService.save(carpetItemModel);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertNotNull(carpetItemModel.getPk());
    }

    @Test
    // Create an item with all informations need (teaser version)
    public void testItemWithTeaserOk() {
        final DistCarpetItemModel carpetItemModel = createComponent();

        final DistCarpetContentTeaserModel contentTeaser = modelService.create(DistCarpetContentTeaserModel.class);
        contentTeaser.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
        contentTeaser.setUid("testUid");

        final MediaModel smallImage = modelService.create(MediaModel.class);
        smallImage.setCode("testCode");
        contentTeaser.setImageSmall(smallImage);

        final CMSLinkComponentModel cmsLink = modelService.create(CMSLinkComponentModel.class);
        cmsLink.setUid("testLink");
        cmsLink.setName("nameLink");
        contentTeaser.setLink(cmsLink);

        carpetItemModel.setContentTeaser(contentTeaser);

        modelService.save(carpetItemModel);
        Assert.assertNotNull(carpetItemModel.getPk());
    }

    private DistCarpetItemModel createComponent() {
        final DistCarpetItemModel component = modelService.create(DistCarpetItemModel.class);
        component.setUid("testComponent_" + UUID.randomUUID().toString());
        component.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
        return component;
    }
}
