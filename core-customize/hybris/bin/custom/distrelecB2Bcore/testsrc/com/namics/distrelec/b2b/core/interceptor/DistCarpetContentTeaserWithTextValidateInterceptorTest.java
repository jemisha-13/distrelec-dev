/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.model.cms2.items.DistCarpetContentTeaserWithTextModel;

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
public class DistCarpetContentTeaserWithTextValidateInterceptorTest extends ServicelayerTransactionalTest {

    @Resource
    private ModelService modelService;

    @Resource
    private CatalogVersionService catalogVersionService;

    private static String tooLongTitle = "abcdefghijklmnopqrstuv"; // length = 22 (> 21)
    private static String goodTitle = "abcdefghijklmnopqrstu"; // length = 21
    private static String tooLongText = "abcdefghijklmnopqrstuv"; // length = 22 (> 21)
    private static String goodText = "abcdefghijklmnopqrstu"; // length = 21
    private static String tooLongSubText = "abcdefghijklmnopqrstuv"; // length = 21 (> 21)
    private static String goodSubText = "abcdefghijklmnopqrstu"; // length = 21

    @Before
    public void setUp() throws Exception {
        super.createCoreData();
        super.createDefaultCatalog();
        jaloSession.getSessionContext().setLanguage(super.getOrCreateLanguage("de"));
    }

    @Test
    // Create a teaser without Small Image
    public void testTeaserWithoutSmallImage() {
        final DistCarpetContentTeaserWithTextModel contentTeaser = createComponent();

        try {
            modelService.save(contentTeaser);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("Das kleine Bild ist zwingend")); // validations.distcarpet.teaser.nosmallimage
        }
    }

    @Test
    // Create a teaser with a small image
    public void testTeaserOk() {
        final DistCarpetContentTeaserWithTextModel contentTeaser = createComponent();

        final MediaModel smallImage = modelService.create(MediaModel.class);
        smallImage.setCode("imageCode");

        contentTeaser.setImageSmall(smallImage);

        try {
            modelService.save(contentTeaser);
        } catch (Exception e) {
            e.getMessage();
            Assert.fail();
        }
        Assert.assertNotNull(contentTeaser.getPk());
    }

    public void testWithTooLongText() {
        final DistCarpetContentTeaserWithTextModel contentTeaser = createComponent();

        final MediaModel smallImage = modelService.create(MediaModel.class);
        smallImage.setCode("imageCode");

        contentTeaser.setImageSmall(smallImage);

        contentTeaser.setText(tooLongText);

        try {
            modelService.save(contentTeaser);
            Assert.fail();
        } catch (Exception exp) {
            Assert.assertTrue(exp.getMessage().contains("validations.distherorotatingteaser.maxlengthtext"));
        }
    }

    @Test
    public void testWithGoodText() {
        final DistCarpetContentTeaserWithTextModel contentTeaser = createComponent();

        final MediaModel smallImage = modelService.create(MediaModel.class);
        smallImage.setCode("imageCode");

        contentTeaser.setImageSmall(smallImage);

        contentTeaser.setText(goodText);

        try {
            modelService.save(contentTeaser);
        } catch (Exception exp) {
            exp.getMessage();
            Assert.fail();
        }
        Assert.assertFalse(modelService.isNew(contentTeaser));
    }

    @Test
    public void testWithTooLongSubText() {
        final DistCarpetContentTeaserWithTextModel contentTeaser = createComponent();

        final MediaModel smallImage = modelService.create(MediaModel.class);
        smallImage.setCode("imageCode");

        contentTeaser.setImageSmall(smallImage);

        contentTeaser.setSubText(tooLongSubText);

        try {
            modelService.save(contentTeaser);
            Assert.fail();
        } catch (Exception exp) {
            Assert.assertTrue(exp.getMessage().contains("Der Subtext ist zu lang (max. 21 characters)"));
        }
    }

    @Test
    public void testWithGoodSubText() {
        final DistCarpetContentTeaserWithTextModel contentTeaser = createComponent();

        final MediaModel smallImage = modelService.create(MediaModel.class);
        smallImage.setCode("imageCode");

        contentTeaser.setImageSmall(smallImage);

        contentTeaser.setSubText(goodSubText);

        final CMSLinkComponentModel cmsLink = modelService.create(CMSLinkComponentModel.class);
        cmsLink.setUid("testLink");
        cmsLink.setName("nameLink");
        contentTeaser.setLink(cmsLink);

        try {
            modelService.save(contentTeaser);
        } catch (Exception exp) {
            exp.getMessage();
            Assert.fail();
        }
        Assert.assertFalse(modelService.isNew(contentTeaser));
    }

    @Test
    public void testWithTooLongTitle() {
        final DistCarpetContentTeaserWithTextModel contentTeaser = createComponent();

        final MediaModel smallImage = modelService.create(MediaModel.class);
        smallImage.setCode("imageCode");

        contentTeaser.setImageSmall(smallImage);

        contentTeaser.setTitle(tooLongTitle);

        try {
            modelService.save(contentTeaser);
            Assert.fail();
        } catch (Exception exp) {
            Assert.assertTrue(exp.getMessage().contains("Die Überschrift ist zu lang (max. 21 characters)"));
        }
    }

    @Test
    public void testWithGoodTitle() {
        final DistCarpetContentTeaserWithTextModel contentTeaser = createComponent();

        final MediaModel smallImage = modelService.create(MediaModel.class);
        smallImage.setCode("imageCode");

        contentTeaser.setImageSmall(smallImage);

        contentTeaser.setTitle(goodTitle);

        final CMSLinkComponentModel cmsLink = modelService.create(CMSLinkComponentModel.class);
        cmsLink.setUid("testLink");
        cmsLink.setName("nameLink");
        contentTeaser.setLink(cmsLink);

        try {
            modelService.save(contentTeaser);
        } catch (Exception exp) {
            exp.getMessage();
            Assert.fail();
        }
        Assert.assertFalse(modelService.isNew(contentTeaser));
    }

    private DistCarpetContentTeaserWithTextModel createComponent() {
        final DistCarpetContentTeaserWithTextModel component = modelService.create(DistCarpetContentTeaserWithTextModel.class);
        component.setUid("testComponent_" + UUID.randomUUID().toString());
        component.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
        return component;
    }

}
