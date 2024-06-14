/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.url.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

/**
 * Test class for extends {@link DefaultDistProductModelUrlResolver}.
 * 
 * @author pbueschi, Namics AG
 */
public class DefaultDistProductModelUrlResolverTest extends ServicelayerTransactionalTest {

    @Resource
    private ModelService modelService;

    @Resource
    private ProductService productService;

    @Resource
    private UrlResolver<ProductModel> productModelUrlResolver;

    @Resource
    private BaseSiteService baseSiteService;

    @Before
    public void setUp() throws Exception {
        super.createCoreData();
        importCsv("/distrelecB2Bcore/test/testErpCodelist.impex", "utf-8");
        importCsv("/distrelecB2Bcore/test/testProductUrlResolver.impex", "utf-8");

        final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID("distrelec_CH");
        baseSiteService.setCurrentBaseSite(baseSite, false);
    }

    @Test
    public void testEOLwithValidReplacement() {
        final ProductModel product = productService.getProductForCode("8500027_sample");
        product.setReplacementProduct(productService.getProductForCode("8500035_sample"));
        modelService.save(product);
        final String url = productModelUrlResolver.resolve(product);
        Assert.assertTrue(StringUtils.contains(url, "8500027_sample"));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testEOLwithoutValidReplacement() {
        final ProductModel product = productService.getProductForCode("8500027_sample");
        product.setReplacementProduct(productService.getProductForCode("8500035_sample"));
        product.setReplacementUntilDate(new Date(113, 0, 1));
        modelService.save(product);
        final String url = productModelUrlResolver.resolve(product);
        Assert.assertTrue(StringUtils.contains(url, "8500027_sample"));
    }

    @Test
    public void testEOL() {
        final ProductModel product = productService.getProductForCode("8500043_sample");
        final String url = productModelUrlResolver.resolve(product);
        Assert.assertTrue(StringUtils.contains(url, "8500043_sample"));
    }
}
