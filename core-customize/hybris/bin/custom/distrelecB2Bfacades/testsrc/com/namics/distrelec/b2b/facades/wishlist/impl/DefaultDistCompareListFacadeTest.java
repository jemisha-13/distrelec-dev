/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.wishlist.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

import static de.hybris.platform.testframework.Assert.assertCollection;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;

@IntegrationTest
public class DefaultDistCompareListFacadeTest extends ServicelayerTransactionalTest {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(DefaultDistCompareListFacadeTest.class);

    private static final char TOKEN = ':';

    @Resource
    private UserService userService;

    @Resource
    private DefaultDistCompareListFacade distCompareListFacade;

    @Resource
    private BaseSiteService baseSiteService;

    @Resource
    private EnumerationService enumerationService;

    @Resource
    private SessionService sessionService;

    @Before
    public void setUp() throws Exception {
        super.createCoreData();
        super.createDefaultCatalog();
        importStream(DefaultDistCompareListFacadeTest.class.getResourceAsStream("/distrelecB2Bcore/test/testErpCodelist.impex"), "UTF-8", null);
        importStream(DefaultDistCompareListFacadeTest.class.getResourceAsStream("/distrelecB2Bfacades/test/testDistCompareListFacade.impex"), "UTF-8", null);
        userService.setCurrentUser(userService.getAnonymousUser());
        baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID("distrelec_CH"), false);
        sessionService.setAttribute("Europe1PriceFactory_UPG", enumerationService.getEnumerationValue(UserPriceGroup.class, "SalesOrg_UPG_7310"));
    }

    @Test
    public void getCompareListForEmptyCookieTest() {
        List<ProductData> products = distCompareListFacade.getCompareList(null);
        assertCollection(ListUtils.EMPTY_LIST, products);
        products = distCompareListFacade.getCompareList("");
        assertCollection(ListUtils.EMPTY_LIST, products);
    }

    @Test
    public void getCompareListWithWrongProductCodeTest() {
        try {
            distCompareListFacade.getCompareList("kjalsdfijklklJ");
        } catch (final UnknownIdentifierException e) {
            fail("Should not throw Exception because the cookie can be edited by the user.");
        }
    }

    @Test
    public void getCompareListForCookieTest() {
        final String[] codes = { "5849020", "9238041", "2394805" };
        final List<ProductData> products = distCompareListFacade.getCompareList(StringUtils.join(codes, TOKEN));
        assertEquals(3, products.size());
        for (final ProductData product : products) {
            assertThat(product.getCode(), Matchers.isOneOf(codes));
        }
    }

    @Test
    public void getCompareListTest() {
        userService.setCurrentUser(userService.getUserForUID("getuser"));
        final List<ProductData> products = distCompareListFacade.getCompareList(null, 100);
        assertEquals(4, products.size());
    }

    @Test
    public void addToCompareListForCookieTest() {
        String[] codes = { "5849020", "9238041" };
        final String cookieValue = distCompareListFacade.addToCompareList("2394805", StringUtils.join(codes, TOKEN));
        codes = StringUtils.split(cookieValue, TOKEN);
        assertEquals(3, codes.length);
        for (final String code : codes) {
            assertThat(code, Matchers.isOneOf("5849020", "9238041", "2394805"));
        }
    }

    @Test
    public void addToCompareListTest() {
        userService.setCurrentUser(userService.getUserForUID("adduser"));
        distCompareListFacade.addToCompareList("5849020", null);
        final List<ProductData> products = distCompareListFacade.getCompareList(null, 100);
        assertEquals(6, products.size());
    }

    @Test
    public void addCookieToCompareListTest() {
        userService.setCurrentUser(userService.getUserForUID("adduser"));
        final String[] codes = { "5849020", "2093829" };
        distCompareListFacade.addCookieProductsToCompareList(StringUtils.join(codes, TOKEN));
        final List<ProductData> products = distCompareListFacade.getCompareList(null, 100);
        assertEquals(7, products.size());
    }

    @Test
    public void removeFromCompareListForCookie() {
        String[] codes = { "5849020", "9238041" };
        final String cookieValue = distCompareListFacade.removeFromCompareList("9238041", StringUtils.join(codes, TOKEN));
        codes = StringUtils.split(cookieValue, TOKEN);
        assertEquals(1, codes.length);
        assertEquals("5849020", codes[0]);
    }

    @Test
    public void removeFromCompareList() {
        userService.setCurrentUser(userService.getUserForUID("removeuser"));
        distCompareListFacade.removeFromCompareList("5849020", null);
        final List<ProductData> products = distCompareListFacade.getCompareList(null, 100);
        assertEquals(2, products.size());
    }
}
