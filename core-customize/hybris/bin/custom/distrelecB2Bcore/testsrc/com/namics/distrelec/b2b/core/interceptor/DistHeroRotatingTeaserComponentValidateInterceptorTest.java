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

import com.namics.distrelec.b2b.core.enums.CmsComponentWidth;
import com.namics.distrelec.b2b.core.model.cms2.components.DistHeroRotatingTeaserComponentModel;
import com.namics.distrelec.b2b.core.model.cms2.items.DistHeroRotatingTeaserItemModel;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Test class for <code>DistHeroRotatingTeaserComponentValidateInterceptor</code>.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DistHeroRotatingTeaserComponentValidateInterceptorTest extends ServicelayerTransactionalTest {

    @Resource
    private ModelService modelService;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Resource
    private ProductService productService;

    @Resource
    private CategoryService categoryService;

    private List<DistHeroRotatingTeaserItemModel> heroRotatingTeaserItems;

    @Before
    public void setUp() throws Exception {
        super.createCoreData();
        super.createDefaultCatalog();

        heroRotatingTeaserItems = new ArrayList<DistHeroRotatingTeaserItemModel>();
        for (int i = 0; i <= 4; i++) {
            try {
                final DistHeroRotatingTeaserItemModel productHeroRotatingTeaserItem = new DistHeroRotatingTeaserItemModel();
                productHeroRotatingTeaserItem.setUid("testProduct" + i);
                productHeroRotatingTeaserItem.setProduct(productService.getProductForCode("testProduct" + i));
                heroRotatingTeaserItems.add(productHeroRotatingTeaserItem);

                final DistHeroRotatingTeaserItemModel categoryHeroRotatingTeaserItem = new DistHeroRotatingTeaserItemModel();
                categoryHeroRotatingTeaserItem.setUid("testCategory" + i);
                categoryHeroRotatingTeaserItem.setCategory(categoryService.getCategoryForCode("testCategory" + i));
                heroRotatingTeaserItems.add(categoryHeroRotatingTeaserItem);
            } catch (final UnknownIdentifierException uie) {
                // ignore and add next product or category
            }
        }
    }

    @Test
    public void testFullWidthEnoughItems() {
        final DistHeroRotatingTeaserComponentModel component = createComponent(CmsComponentWidth.FULLWIDTH);
        component.setHeroRotatingTeaserItems(getItemList(7));
        modelService.save(component);
        Assert.assertFalse(modelService.isNew(component));
    }

    @Test(expected = ModelSavingException.class)
    public void testFullWidthNotEnoughItems() {
        final DistHeroRotatingTeaserComponentModel component = createComponent(CmsComponentWidth.FULLWIDTH);
        component.setHeroRotatingTeaserItems(getItemList(1));
        modelService.save(component);
    }

    @Test
    public void testTwoThirdWidthEnoughItems() {
        final DistHeroRotatingTeaserComponentModel component = createComponent(CmsComponentWidth.TWOTHIRD);
        component.setHeroRotatingTeaserItems(getItemList(5));
        modelService.save(component);
        Assert.assertFalse(modelService.isNew(component));
    }

    @Test(expected = ModelSavingException.class)
    public void testTwoThirdNotEnoughItems() {
        final DistHeroRotatingTeaserComponentModel component = createComponent(CmsComponentWidth.TWOTHIRD);
        component.setHeroRotatingTeaserItems(getItemList(1));
        modelService.save(component);
    }

    private DistHeroRotatingTeaserComponentModel createComponent(final CmsComponentWidth componentWidth) {
        final DistHeroRotatingTeaserComponentModel component = new DistHeroRotatingTeaserComponentModel();
        component.setUid("testComponent_" + UUID.randomUUID().toString());
        component.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
        component.setComponentWidth(componentWidth);
        return component;
    }

    private List<DistHeroRotatingTeaserItemModel> getItemList(final int numberOfItems) {
        return heroRotatingTeaserItems.subList(0, numberOfItems);
    }
}
