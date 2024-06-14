/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.facades.suggestion;

import com.namics.distrelec.b2b.facades.suggestion.impl.DefaultSimpleSuggestionFacade;
import com.namics.hybris.toolbox.spring.SpringUtil;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.order.price.PriceFactory;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.user.UserService;
import junit.framework.Assert;
import org.apache.commons.lang.math.NumberUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * Integration test suite for {@link DefaultSimpleSuggestionFacade}
 */
@IntegrationTest
public class DefaultSimpleSuggestionFacadeIntegrationTest extends ServicelayerTransactionalTest {

    @Resource
    private SimpleSuggestionFacade simpleSuggestionFacade;

    @Resource
    private UserService userService;

    @Resource
    private CMSSiteService cmsSiteService;

    @Before
    public void setUp() throws Exception {
        userService.setCurrentUser(userService.getAnonymousUser());
        importCsv("/distrelecB2Bfacades/test/testSimpleSuggestionFacade.impex", "utf-8");
        JaloSession.getCurrentSession().setPriceFactory(SpringUtil.getBean("mock.priceFactory", PriceFactory.class));
        final CMSSiteModel currentCMSSite = cmsSiteService.setCurrentSiteAndCatalogVersions("distrelec_CH", false);
    }

    @After
    public void tearDown() {
        JaloSession.getCurrentSession().setPriceFactory(SpringUtil.getBean("erp.priceFactory", PriceFactory.class));
    }

    @Test
    public void testReferencesForPurchasedInCategory() {
        final UserModel user = userService.getUserForUID("dejol");
        userService.setCurrentUser(user);
        List<ProductData> result = simpleSuggestionFacade.getReferencesForPurchasedInCategory("cameras", null, false, null);
        Assert.assertEquals(4, result.size());
        result = simpleSuggestionFacade.getReferencesForPurchasedInCategory("cameras", null, false, NumberUtils.INTEGER_ONE);
        Assert.assertEquals(1, result.size());
        result = simpleSuggestionFacade.getReferencesForPurchasedInCategory("cameras", ProductReferenceTypeEnum.SIMILAR, false, null);
        Assert.assertEquals(1, result.size());
        result = simpleSuggestionFacade.getReferencesForPurchasedInCategory("cameras", ProductReferenceTypeEnum.ACCESSORIES, false, null);
        Assert.assertEquals(2, result.size());
        result = simpleSuggestionFacade.getReferencesForPurchasedInCategory("cameras", ProductReferenceTypeEnum.ACCESSORIES, true, null);
        Assert.assertEquals(1, result.size());
        final ProductData product = result.get(0);
        Assert.assertEquals("adapterDC", product.getCode());
        Assert.assertEquals("adapter", product.getName());
    }
}
