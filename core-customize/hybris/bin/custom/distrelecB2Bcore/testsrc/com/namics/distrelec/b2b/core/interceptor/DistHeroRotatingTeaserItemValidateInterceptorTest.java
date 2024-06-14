/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.model.cms2.items.DistHeroRotatingTeaserItemModel;
import com.namics.distrelec.b2b.core.model.cms2.items.DistHeroRotatingTeaserModel;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Test class for <code>DistHeroRotatingTeaserValidateInterceptor</code>.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DistHeroRotatingTeaserItemValidateInterceptorTest extends ServicelayerTransactionalTest {

    @Resource
    private ModelService modelService;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Before
    public void setUp() throws Exception {
        super.createCoreData();
        super.createDefaultCatalog();

        jaloSession.getSessionContext().setLanguage(super.getOrCreateLanguage("de"));
    }

    @Test
    public void testWithoutOptions() {
        final DistHeroRotatingTeaserItemModel distHeroRotatingTeaserItem = createComponent();

        try {
            modelService.save(distHeroRotatingTeaserItem);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("Ein Eintrag muss definiert werden"));
        }
    }

    @Test
    public void testWithMultipleOptions() {
        final DistHeroRotatingTeaserItemModel distHeroRotatingTeaserItem = createComponent();
        distHeroRotatingTeaserItem.setContentTeaser(createTeaserModel());
        distHeroRotatingTeaserItem.setProduct(createProductModel());

        try {
            modelService.save(distHeroRotatingTeaserItem);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("Es darf nur ein Eintrag definiert werden"));
        }
    }

    @Test
    public void testWithProductOk() {
        final DistHeroRotatingTeaserItemModel distHeroRotatingTeaserItem = createComponent();
        distHeroRotatingTeaserItem.setProduct(createProductModel());

        try {
            modelService.save(distHeroRotatingTeaserItem);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertFalse(modelService.isNew(distHeroRotatingTeaserItem));
    }

    @Test
    public void testWithContentTeaserOk() {
        final DistHeroRotatingTeaserItemModel distHeroRotatingTeaserItem = createComponent();
        distHeroRotatingTeaserItem.setContentTeaser(createTeaserModel());

        try {
            modelService.save(distHeroRotatingTeaserItem);
        } catch (Exception e) {
            e.getMessage();
            Assert.fail();
        }
        Assert.assertFalse(modelService.isNew(distHeroRotatingTeaserItem));
    }

    private DistHeroRotatingTeaserItemModel createComponent() {
        final DistHeroRotatingTeaserItemModel component = modelService.create(DistHeroRotatingTeaserItemModel.class);
        component.setUid("testComponent_" + UUID.randomUUID().toString());
        component.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
        return component;
    }

    private DistHeroRotatingTeaserModel createTeaserModel() {
        final DistHeroRotatingTeaserModel distHeroRotatingTeaser = modelService.create(DistHeroRotatingTeaserModel.class);
        distHeroRotatingTeaser.setUid("testComponent_" + UUID.randomUUID().toString());
        distHeroRotatingTeaser.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));

        final MediaModel picture = modelService.create(MediaModel.class);
        picture.setCode("pictureCode");
        distHeroRotatingTeaser.setPicture(picture);

        final MediaModel thumbnail = modelService.create(MediaModel.class);
        thumbnail.setCode("thumbnailCode");
        distHeroRotatingTeaser.setThumbnail(thumbnail);

        final CMSLinkComponentModel cmsLink = modelService.create(CMSLinkComponentModel.class);
        cmsLink.setUid("testLink");
        cmsLink.setName("nameLink");
        distHeroRotatingTeaser.setLink(cmsLink);

        return distHeroRotatingTeaser;
    }

    private ProductModel createProductModel() {
        final ProductModel productModel = modelService.create(ProductModel.class);
        productModel.setCode("testCode");
        return productModel;
    }
}
