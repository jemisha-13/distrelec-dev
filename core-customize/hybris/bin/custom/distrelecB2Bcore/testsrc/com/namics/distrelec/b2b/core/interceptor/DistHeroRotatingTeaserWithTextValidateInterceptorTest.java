/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.model.cms2.items.DistHeroRotatingTeaserWithTextModel;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Test class for <code>DistHeroRotatingTeaserValidateInterceptor</code>.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DistHeroRotatingTeaserWithTextValidateInterceptorTest extends ServicelayerTransactionalTest {

    private static String tooLongText = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvw"; // length = 49 (> 48)
    private static String goodText = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuv"; // length = 48
    private static String tooLongSubText = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdef"; // length = 58 (> 57)
    private static String goodSubText = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcde"; // length = 57

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
        final DistHeroRotatingTeaserWithTextModel distHeroRotatingTeaser = createComponent();
        try {
            modelService.save(distHeroRotatingTeaser);
            Assert.fail();
        } catch (Exception exp) {
            Assert.assertTrue(exp.getMessage().contains("Das Bild fehlt. Bitte ein Bild auswählen."));
        }
    }

    @Test
    public void testWithoutThumbnail() {
        final DistHeroRotatingTeaserWithTextModel distHeroRotatingTeaser = createComponent();
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

    @Test
    public void testWithTooLongText() {
        final DistHeroRotatingTeaserWithTextModel distHeroRotatingTeaser = createComponent();

        final MediaModel picture = modelService.create(MediaModel.class);
        picture.setCode("pictureCode");
        distHeroRotatingTeaser.setPicture(picture);

        final MediaModel thumbnail = modelService.create(MediaModel.class);
        thumbnail.setCode("thumbnailCode");
        distHeroRotatingTeaser.setThumbnail(thumbnail);

        distHeroRotatingTeaser.setText(tooLongText);

        try {
            modelService.save(distHeroRotatingTeaser);
            Assert.fail();
        } catch (Exception exp) {
            Assert.assertTrue(exp.getMessage().contains("Der Text ist zu lang (max. 48 Buchstaben)"));
        }
    }

    @Test
    public void testWithGoodText() {
        final DistHeroRotatingTeaserWithTextModel distHeroRotatingTeaser = createComponent();

        final MediaModel picture = modelService.create(MediaModel.class);
        picture.setCode("pictureCode");
        distHeroRotatingTeaser.setPicture(picture);

        final MediaModel thumbnail = modelService.create(MediaModel.class);
        thumbnail.setCode("thumbnailCode");
        distHeroRotatingTeaser.setThumbnail(thumbnail);

        distHeroRotatingTeaser.setText(goodText);

        try {
            modelService.save(distHeroRotatingTeaser);
        } catch (Exception exp) {
            exp.getMessage();
            Assert.fail();
        }
        Assert.assertFalse(modelService.isNew(distHeroRotatingTeaser));
    }

    @Test
    public void testWithTooLongSubText() {
        final DistHeroRotatingTeaserWithTextModel distHeroRotatingTeaser = createComponent();

        final MediaModel picture = modelService.create(MediaModel.class);
        picture.setCode("pictureCode");
        distHeroRotatingTeaser.setPicture(picture);

        final MediaModel thumbnail = modelService.create(MediaModel.class);
        thumbnail.setCode("thumbnailCode");
        distHeroRotatingTeaser.setThumbnail(thumbnail);

        distHeroRotatingTeaser.setSubText(tooLongSubText);

        try {
            modelService.save(distHeroRotatingTeaser);
            Assert.fail();
        } catch (Exception exp) {
            Assert.assertTrue(exp.getMessage().contains("Der Text ist zu lang (max. 57 Buchstaben)"));
        }
    }

    @Test
    public void testWithGoodSubText() {
        final DistHeroRotatingTeaserWithTextModel distHeroRotatingTeaser = createComponent();

        final MediaModel picture = modelService.create(MediaModel.class);
        picture.setCode("pictureCode");
        distHeroRotatingTeaser.setPicture(picture);

        final MediaModel thumbnail = modelService.create(MediaModel.class);
        thumbnail.setCode("thumbnailCode");
        distHeroRotatingTeaser.setThumbnail(thumbnail);

        distHeroRotatingTeaser.setSubText(goodSubText);

        final CMSLinkComponentModel cmsLink = modelService.create(CMSLinkComponentModel.class);
        cmsLink.setUid("testLink");
        cmsLink.setName("nameLink");
        distHeroRotatingTeaser.setLink(cmsLink);

        try {
            modelService.save(distHeroRotatingTeaser);
        } catch (Exception exp) {
            exp.getMessage();
            Assert.fail();
        }
        Assert.assertFalse(modelService.isNew(distHeroRotatingTeaser));
    }

    private DistHeroRotatingTeaserWithTextModel createComponent() {
        final DistHeroRotatingTeaserWithTextModel component = modelService.create(DistHeroRotatingTeaserWithTextModel.class);
        component.setUid("testComponent_" + UUID.randomUUID().toString());
        component.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
        return component;
    }
}