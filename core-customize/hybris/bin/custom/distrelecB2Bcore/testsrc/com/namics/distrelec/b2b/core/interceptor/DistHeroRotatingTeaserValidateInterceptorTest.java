/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.model.cms2.items.DistHeroRotatingTeaserModel;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Test class for <code>DistHeroRotatingTeaserValidateInterceptor</code>.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DistHeroRotatingTeaserValidateInterceptorTest extends ServicelayerTransactionalTest {

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
    public void testWithoutPicture() {
        final DistHeroRotatingTeaserModel distHeroRotatingTeaser = createComponent();
        try {
            modelService.save(distHeroRotatingTeaser);
            Assert.fail();
        } catch (Exception exp) {
            Assert.assertTrue(exp.getMessage().contains("Das Bild fehlt. Bitte ein Bild auswählen."));
        }
    }

    @Test
    public void testWithoutThumbnail() {
        final DistHeroRotatingTeaserModel distHeroRotatingTeaser = createComponent();
        final MediaModel picture = modelService.create(MediaModel.class);
        picture.setCode("pictureCode");

        distHeroRotatingTeaser.setPicture(picture);
        try {
            modelService.save(distHeroRotatingTeaser);
            Assert.fail();
        } catch (Exception exp) {
            Assert.assertTrue(exp.getMessage().contains("Das Vorschaubild fehlt. Bitte ein Vorschaubild auswählen."));
        }
    }

    private DistHeroRotatingTeaserModel createComponent() {
        final DistHeroRotatingTeaserModel component = modelService.create(DistHeroRotatingTeaserModel.class);
        component.setUid("testComponent_" + UUID.randomUUID().toString());
        component.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
        return component;
    }
}