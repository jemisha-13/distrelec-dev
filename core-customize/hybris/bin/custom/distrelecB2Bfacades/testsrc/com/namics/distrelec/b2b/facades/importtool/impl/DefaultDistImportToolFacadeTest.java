/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.importtool.impl;

import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.facades.importtool.DistImportToolFacade;
import com.namics.distrelec.b2b.facades.importtool.data.ImportToolMatchingData;
import com.namics.distrelec.b2b.facades.importtool.exception.ImportToolException;
import com.namics.distrelec.b2b.facades.importtool.exception.ImportToolException.ErrorSource;
import com.namics.hybris.toolbox.spring.SpringUtil;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.order.price.PriceFactory;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Test class for {@link DefaultDistImportToolFacade}.
 * 
 * @author DAEZAMOFINL, Distrelec
 * @since Distrelec 1.0
 */
@IntegrationTest
public class DefaultDistImportToolFacadeTest extends ServicelayerTransactionalTest {

    private final String path = this.getClass().getResource("/distrelecB2Bfacades/test/").getPath();

    @Resource
    private UserService userService;

    @Resource
    private DistImportToolFacade distImportToolFacade;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Resource
    private DistSalesOrgService distSalesOrgService;

    @Resource
    private CMSSiteService cmsSiteService;

    @Resource
    private BaseStoreService baseStoreService;

    @Before
    // Set the default data
    public void setUp() throws Exception {
        userService.setCurrentUser(userService.getAnonymousUser());
        importCsv("/distrelecB2Bfacades/test/testImportTool.impex", "utf-8");
        catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");
        JaloSession.getCurrentSession().setPriceFactory(SpringUtil.getBean("mock.priceFactory", PriceFactory.class));

        CMSSiteModel cmsSite = cmsSiteService.getSiteForURL(new URL("http://distrelec.ch"));
        cmsSiteService.setCurrentSite(cmsSite);
    }

    @After
    public void tearDown() {
        JaloSession.getCurrentSession().setPriceFactory(SpringUtil.getBean("erp.priceFactory", PriceFactory.class));
    }

    // @Test
    // Search 4 articles, 3 are in base and one is not (wrongProduct-id)
    public void testSearchProductsFromData() {
        // Quantity, Article Number, Item Reference
        final String data = "1,product id 001,Item Reference1 \r\n" + "2,product id 002,Item Reference1 \r\n" + "3, product id 003,Item Reference1 \r\n"
                + "3,wrong product id,Item Reference1\r\n";

        Map<String, Object> importResult = null;
        try {
            importResult = distImportToolFacade.searchProductsFromData(data);
        } catch (final ImportToolException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(importResult);

        final List<ImportToolMatchingData> matchingProducts = (List<ImportToolMatchingData>) importResult.get("matchingProducts");
        orderList(matchingProducts);

        Assert.assertEquals(3, matchingProducts.size());
        Assert.assertEquals("product id 001", matchingProducts.get(0).getProductCode());
        Assert.assertEquals("product id 002", matchingProducts.get(1).getProductCode());
        Assert.assertEquals("product id 003", matchingProducts.get(2).getProductCode());

        final List<String[]> notMatchingProducts = (List<String[]>) importResult.get("notMatchingProducts");
        Assert.assertEquals(1, notMatchingProducts.size());
        Assert.assertEquals("wrong product id", notMatchingProducts.get(0)[1]);
    }

    @Test
    // Search 3 times the same articles (quantity = 1 + 2 + 3 = 6)
    public void testSearchProductsFromDataWithMerge() {
        // Quantity, Article Number, Item Reference
        final String data = "1, product id 001, Item Reference1 \r\n" + "2, product id 001, Item Reference1 \r\n" + "3, product id 001, Item Reference1 \r\n";

        Map<String, Object> importResult = null;
        try {
            importResult = distImportToolFacade.searchProductsFromData(data);
        } catch (final ImportToolException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(importResult);

        final List<ImportToolMatchingData> matchingProducts = (List<ImportToolMatchingData>) importResult.get("matchingProducts");
        orderList(matchingProducts);

        Assert.assertEquals(1, matchingProducts.size());
        Assert.assertEquals("product id 001", matchingProducts.get(0).getProductCode());
        Assert.assertEquals(6, matchingProducts.get(0).getQuantity());

        final List<String[]> notMatchingProducts = (List<String[]>) importResult.get("notMatchingProducts");
        Assert.assertTrue(notMatchingProducts.isEmpty());
    }

    @Test
    // Search 2 times the same articles (quantity = 1 + quantityError = 1 + 1 = 2)
    public void testSearchProductsFromDataWithMergeWithWrongQuantity() {
        // Quantity, Article Number, Item Reference
        final String data = "1, product id 001, Item Reference1 \r\n" + "quantityError, product id 001, Item Reference1 \r\n";

        Map<String, Object> importResult = null;
        try {
            importResult = distImportToolFacade.searchProductsFromData(data);
        } catch (final ImportToolException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(importResult);

        final List<ImportToolMatchingData> matchingProducts = (List<ImportToolMatchingData>) importResult.get("matchingProducts");
        orderList(matchingProducts);

        Assert.assertEquals(1, matchingProducts.size());
        Assert.assertEquals("product id 001", matchingProducts.get(0).getProductCode());
        Assert.assertEquals(2, matchingProducts.get(0).getQuantity());

        final List<String[]> notMatchingProducts = (List<String[]>) importResult.get("notMatchingProducts");
        Assert.assertTrue(notMatchingProducts.isEmpty());
    }

    @Test
    // The datas are not good formated. There is no separator ("\t" or ";")
    public void testSearchProductsFromDataWithoutSeparator() {
        // Quantity, Article Number, Item Reference
        final String data = "1--productid001--ItemReference1 \r\n" + "2--productid002--ItemReference1 \r\n" + "3--productid003,ItemReference1 \r\n";
        try {
            distImportToolFacade.searchProductsFromData(data);
            // Error
            Assert.fail();
        } catch (final ImportToolException e) {
            Assert.assertEquals(ErrorSource.COPY_PAST_SEPARATOR_NOT_FOUND, e.getErrorSource());
        }
    }

    @Test
    // No datas to search
    public void testSearchProductsFromDataContentEmpty() {
        // Quantity, Article Number, Item Reference
        final String data = "";
        try {
            distImportToolFacade.searchProductsFromData(data);
            Assert.fail();
        } catch (final ImportToolException e) {
            Assert.assertEquals(ErrorSource.NO_DATA, e.getErrorSource());
        }
    }

    @Test
    // More than 100 lines, 0 not matching. Only the 100 first correct lines are used
    public void testSearchProductsFromDataMoreMaxLines() {
        // Quantity, Article Number, Item Reference
        final String data = "1, product id 001, Item Reference 001\r\n" + "2, product id 002, Item Reference 002\r\n"
                + "3, product id 003, Item Reference 003\r\n" + "4, product id 004, Item Reference 004\r\n" + "5, product id 005, Item Reference 005\r\n"
                + "6, product id 006, Item Reference 006\r\n" + "7, product id 007, Item Reference 007\r\n" + "8, product id 008, Item Reference 008\r\n"
                + "9, product id 009, Item Reference 009\r\n" + "10, product id 010, Item Reference 010\r\n" + "11, product id 011, Item Reference 011\r\n"
                + "12, product id 012, Item Reference 012\r\n" + "13, product id 013, Item Reference 013\r\n" + "14, product id 014, Item Reference 014\r\n"
                + "15, product id 015, Item Reference 015\r\n" + "16, product id 016, Item Reference 016\r\n" + "17, product id 017, Item Reference 017\r\n"
                + "18, product id 018, Item Reference 018\r\n" + "19, product id 019, Item Reference 019\r\n" + "20, product id 020, Item Reference 020\r\n"
                + "21, product id 021, Item Reference 021\r\n" + "22, product id 022, Item Reference 022\r\n" + "23, product id 023, Item Reference 023\r\n"
                + "24, product id 024, Item Reference 024\r\n" + "25, product id 025, Item Reference 025\r\n" + "26, product id 026, Item Reference 026\r\n"
                + "27, product id 027, Item Reference 027\r\n" + "28, product id 028, Item Reference 028\r\n" + "29, product id 029, Item Reference 029\r\n"
                + "30, product id 030, Item Reference 030\r\n" + "31, product id 031, Item Reference 031\r\n" + "32, product id 032, Item Reference 032\r\n"
                + "33, product id 033, Item Reference 033\r\n" + "34, product id 034, Item Reference 034\r\n" + "35, product id 035, Item Reference 035\r\n"
                + "36, product id 036, Item Reference 036\r\n" + "37, product id 037, Item Reference 037\r\n" + "38, product id 038, Item Reference 038\r\n"
                + "39, product id 039, Item Reference 039\r\n" + "40, product id 040, Item Reference 040\r\n" + "41, product id 041, Item Reference 041\r\n"
                + "42, product id 042, Item Reference 042\r\n" + "43, product id 043, Item Reference 043\r\n" + "44, product id 044, Item Reference 044\r\n"
                + "45, product id 045, Item Reference 045\r\n" + "46, product id 046, Item Reference 046\r\n" + "47, product id 047, Item Reference 047\r\n"
                + "48, product id 048, Item Reference 048\r\n" + "49, product id 049, Item Reference 049\r\n" + "50, product id 050, Item Reference 050\r\n"
                + "51, product id 051, Item Reference 051\r\n" + "52, product id 052, Item Reference 052\r\n" + "53, product id 053, Item Reference 053\r\n"
                + "54, product id 054, Item Reference 054\r\n" + "55, product id 055, Item Reference 055\r\n" + "56, product id 056, Item Reference 056\r\n"
                + "57, product id 057, Item Reference 057\r\n" + "58, product id 058, Item Reference 058\r\n" + "59, product id 059, Item Reference 059\r\n"
                + "60, product id 060, Item Reference 060\r\n" + "61, product id 061, Item Reference 061\r\n" + "62, product id 062, Item Reference 062\r\n"
                + "63, product id 063, Item Reference 063\r\n" + "64, product id 064, Item Reference 064\r\n" + "65, product id 065, Item Reference 065\r\n"
                + "66, product id 066, Item Reference 066\r\n" + "67, product id 067, Item Reference 067\r\n" + "68, product id 068, Item Reference 068\r\n"
                + "69, product id 069, Item Reference 069\r\n" + "70, product id 070, Item Reference 070\r\n" + "71, product id 071, Item Reference 071\r\n"
                + "72, product id 072, Item Reference 072\r\n" + "73, product id 073, Item Reference 073\r\n" + "74, product id 074, Item Reference 074\r\n"
                + "75, product id 075, Item Reference 075\r\n" + "76, product id 076, Item Reference 076\r\n" + "77, product id 077, Item Reference 077\r\n"
                + "78, product id 078, Item Reference 078\r\n" + "79, product id 079, Item Reference 079\r\n" + "80, product id 080, Item Reference 080\r\n"
                + "81, product id 081, Item Reference 081\r\n" + "82, product id 082, Item Reference 082\r\n" + "83, product id 083, Item Reference 083\r\n"
                + "84, product id 084, Item Reference 084\r\n" + "85, product id 085, Item Reference 085\r\n" + "86, product id 086, Item Reference 086\r\n"
                + "87, product id 087, Item Reference 087\r\n" + "88, product id 088, Item Reference 088\r\n" + "89, product id 089, Item Reference 089\r\n"
                + "90, product id 090, Item Reference 090\r\n" + "91, product id 091, Item Reference 091\r\n" + "92, product id 092, Item Reference 092\r\n"
                + "93, product id 093, Item Reference 093\r\n" + "94, product id 094, Item Reference 094\r\n" + "95, product id 095, Item Reference 095\r\n"
                + "96, product id 096, Item Reference 096\r\n" + "97, product id 097, Item Reference 097\r\n" + "98, product id 098, Item Reference 098\r\n"
                + "99, product id 099, Item Reference 099\r\n" + "100, product id 100, Item Reference 100\r\n";

        Map<String, Object> importResult = null;
        try {
            importResult = distImportToolFacade.searchProductsFromData(data);
        } catch (final ImportToolException e) {
            Assert.fail(e.getMessage());
        }
        final List<ImportToolMatchingData> matchingProducts = (List<ImportToolMatchingData>) importResult.get("matchingProducts");
        Assert.assertEquals(100, matchingProducts.size());

        final List<String[]> notMatchingProducts = (List<String[]>) importResult.get("notMatchingProducts");
        Assert.assertTrue(notMatchingProducts.isEmpty());

    }

    @Test
    // More than 100 lines, 1 items to merge. Only the 101 first correct lines are used to set 100 products
    public void testSearchProductsFromDataMoreMaxLinesWithMerge() {
        // Quantity, Article Number, Item Reference
        final String data = "1, product id 001, Item Reference 001\r\n" + "2, product id 002, Item Reference 002\r\n"
                + "3, product id 003, Item Reference 003\r\n" + "4, product id 004, Item Reference 004\r\n" + "5, product id 005, Item Reference 005\r\n"
                + "6, product id 006, Item Reference 006\r\n" + "7, product id 007, Item Reference 007\r\n" + "8, product id 008, Item Reference 008\r\n"
                + "9, product id 009, Item Reference 009\r\n" + "10, product id 010, Item Reference 010\r\n" + "11, product id 011, Item Reference 011\r\n"
                + "12, product id 012, Item Reference 012\r\n" + "13, product id 013, Item Reference 013\r\n" + "14, product id 014, Item Reference 014\r\n"
                + "15, product id 015, Item Reference 015\r\n" + "16, product id 016, Item Reference 016\r\n" + "17, product id 017, Item Reference 017\r\n"
                + "18, product id 018, Item Reference 018\r\n" + "19, product id 019, Item Reference 019\r\n" + "20, product id 020, Item Reference 020\r\n"
                + "21, product id 021, Item Reference 021\r\n" + "22, product id 022, Item Reference 022\r\n" + "23, product id 023, Item Reference 023\r\n"
                + "24, product id 024, Item Reference 024\r\n" + "25, product id 025, Item Reference 025\r\n" + "26, product id 026, Item Reference 026\r\n"
                + "27, product id 027, Item Reference 027\r\n" + "28, product id 028, Item Reference 028\r\n" + "29, product id 029, Item Reference 029\r\n"
                + "30, product id 030, Item Reference 030\r\n" + "31, product id 031, Item Reference 031\r\n" + "32, product id 032, Item Reference 032\r\n"
                + "33, product id 033, Item Reference 033\r\n" + "34, product id 034, Item Reference 034\r\n" + "35, product id 035, Item Reference 035\r\n"
                + "36, product id 036, Item Reference 036\r\n" + "37, product id 037, Item Reference 037\r\n" + "38, product id 038, Item Reference 038\r\n"
                + "39, product id 039, Item Reference 039\r\n" + "40, product id 040, Item Reference 040\r\n" + "41, product id 041, Item Reference 041\r\n"
                + "42, product id 042, Item Reference 042\r\n" + "43, product id 043, Item Reference 043\r\n" + "44, product id 044, Item Reference 044\r\n"
                + "45, product id 045, Item Reference 045\r\n" + "46, product id 046, Item Reference 046\r\n" + "47, product id 047, Item Reference 047\r\n"
                + "48, product id 048, Item Reference 048\r\n" + "49, product id 049, Item Reference 049\r\n" + "50, product id 050, Item Reference 050\r\n"
                + "51, product id 051, Item Reference 051\r\n" + "52, product id 052, Item Reference 052\r\n" + "53, product id 053, Item Reference 053\r\n"
                + "54, product id 054, Item Reference 054\r\n" + "55, product id 055, Item Reference 055\r\n" + "56, product id 056, Item Reference 056\r\n"
                + "57, product id 057, Item Reference 057\r\n" + "58, product id 058, Item Reference 058\r\n" + "59, product id 059, Item Reference 059\r\n"
                + "60, product id 060, Item Reference 060\r\n" + "61, product id 061, Item Reference 061\r\n" + "62, product id 062, Item Reference 062\r\n"
                + "63, product id 063, Item Reference 063\r\n" + "64, product id 064, Item Reference 064\r\n" + "65, product id 065, Item Reference 065\r\n"
                + "66, product id 066, Item Reference 066\r\n" + "67, product id 067, Item Reference 067\r\n" + "68, product id 068, Item Reference 068\r\n"
                + "69, product id 069, Item Reference 069\r\n" + "70, product id 070, Item Reference 070\r\n" + "71, product id 071, Item Reference 071\r\n"
                + "72, product id 072, Item Reference 072\r\n" + "73, product id 073, Item Reference 073\r\n" + "74, product id 074, Item Reference 074\r\n"
                + "75, product id 075, Item Reference 075\r\n" + "76, product id 076, Item Reference 076\r\n" + "77, product id 077, Item Reference 077\r\n"
                + "78, product id 078, Item Reference 078\r\n" + "79, product id 079, Item Reference 079\r\n" + "80, product id 080, Item Reference 080\r\n"
                + "81, product id 081, Item Reference 081\r\n" + "82, product id 082, Item Reference 082\r\n" + "83, product id 083, Item Reference 083\r\n"
                + "84, product id 084, Item Reference 084\r\n" + "85, product id 085, Item Reference 085\r\n" + "86, product id 086, Item Reference 086\r\n"
                + "87, product id 087, Item Reference 087\r\n" + "88, product id 088, Item Reference 088\r\n" + "89, product id 089, Item Reference 089\r\n"
                + "90, product id 090, Item Reference 090\r\n" + "91, product id 091, Item Reference 091\r\n" + "92, product id 092, Item Reference 092\r\n"
                + "93, product id 093, Item Reference 093\r\n" + "94, product id 094, Item Reference 094\r\n" + "95, product id 095, Item Reference 095\r\n"
                + "96, product id 096, Item Reference 096\r\n" + "97, product id 097, Item Reference 097\r\n" + "98, product id 098, Item Reference 098\r\n"
                + "99, product id 099, Item Reference 099\r\n" + "001, product id 100, Item Reference 100\r\n";

        Map<String, Object> importResult = null;
        try {
            importResult = distImportToolFacade.searchProductsFromData(data);
        } catch (final ImportToolException e) {
            Assert.fail(e.getMessage());
        }

        final List<ImportToolMatchingData> matchingProducts = (List<ImportToolMatchingData>) importResult.get("matchingProducts");
        orderList(matchingProducts);

        Assert.assertEquals(100, matchingProducts.size());
        Assert.assertEquals("product id 001", matchingProducts.get(0).getProductCode());
        Assert.assertEquals(1, matchingProducts.get(0).getQuantity());

        final List<String[]> notMatchingProducts = (List<String[]>) importResult.get("notMatchingProducts");
        Assert.assertEquals(0, notMatchingProducts.size());
    }

    @Test
    // More than 100 lines, 2 not matching. Only the 100 first lines are used (98 lines matching + 2 lines not matching)
    public void testSearchProductsFromDataMoreMaxLinesWithNotMatchingProducts() {
        // Quantity, Article Number, Item Reference
        final String data = "1, product id 001x, Item Reference 001\r\n" + "2, product id 002x, Item Reference 002\r\n"
                + "3, product id 003, Item Reference 003\r\n" + "4, product id 004, Item Reference 004\r\n" + "5, product id 005, Item Reference 005\r\n"
                + "6, product id 006, Item Reference 006\r\n" + "7, product id 007, Item Reference 007\r\n" + "8, product id 008, Item Reference 008\r\n"
                + "9, product id 009, Item Reference 009\r\n" + "10, product id 010, Item Reference 010\r\n" + "11, product id 011, Item Reference 011\r\n"
                + "12, product id 012, Item Reference 012\r\n" + "13, product id 013, Item Reference 013\r\n" + "14, product id 014, Item Reference 014\r\n"
                + "15, product id 015, Item Reference 015\r\n" + "16, product id 016, Item Reference 016\r\n" + "17, product id 017, Item Reference 017\r\n"
                + "18, product id 018, Item Reference 018\r\n" + "19, product id 019, Item Reference 019\r\n" + "20, product id 020, Item Reference 020\r\n"
                + "21, product id 021, Item Reference 021\r\n" + "22, product id 022, Item Reference 022\r\n" + "23, product id 023, Item Reference 023\r\n"
                + "24, product id 024, Item Reference 024\r\n" + "25, product id 025, Item Reference 025\r\n" + "26, product id 026, Item Reference 026\r\n"
                + "27, product id 027, Item Reference 027\r\n" + "28, product id 028, Item Reference 028\r\n" + "29, product id 029, Item Reference 029\r\n"
                + "30, product id 030, Item Reference 030\r\n" + "31, product id 031, Item Reference 031\r\n" + "32, product id 032, Item Reference 032\r\n"
                + "33, product id 033, Item Reference 033\r\n" + "34, product id 034, Item Reference 034\r\n" + "35, product id 035, Item Reference 035\r\n"
                + "36, product id 036, Item Reference 036\r\n" + "37, product id 037, Item Reference 037\r\n" + "38, product id 038, Item Reference 038\r\n"
                + "39, product id 039, Item Reference 039\r\n" + "40, product id 040, Item Reference 040\r\n" + "41, product id 041, Item Reference 041\r\n"
                + "42, product id 042, Item Reference 042\r\n" + "43, product id 043, Item Reference 043\r\n" + "44, product id 044, Item Reference 044\r\n"
                + "45, product id 045, Item Reference 045\r\n" + "46, product id 046, Item Reference 046\r\n" + "47, product id 047, Item Reference 047\r\n"
                + "48, product id 048, Item Reference 048\r\n" + "49, product id 049, Item Reference 049\r\n" + "50, product id 050, Item Reference 050\r\n"
                + "51, product id 051, Item Reference 051\r\n" + "52, product id 052, Item Reference 052\r\n" + "53, product id 053, Item Reference 053\r\n"
                + "54, product id 054, Item Reference 054\r\n" + "55, product id 055, Item Reference 055\r\n" + "56, product id 056, Item Reference 056\r\n"
                + "57, product id 057, Item Reference 057\r\n" + "58, product id 058, Item Reference 058\r\n" + "59, product id 059, Item Reference 059\r\n"
                + "60, product id 060, Item Reference 060\r\n" + "61, product id 061, Item Reference 061\r\n" + "62, product id 062, Item Reference 062\r\n"
                + "63, product id 063, Item Reference 063\r\n" + "64, product id 064, Item Reference 064\r\n" + "65, product id 065, Item Reference 065\r\n"
                + "66, product id 066, Item Reference 066\r\n" + "67, product id 067, Item Reference 067\r\n" + "68, product id 068, Item Reference 068\r\n"
                + "69, product id 069, Item Reference 069\r\n" + "70, product id 070, Item Reference 070\r\n" + "71, product id 071, Item Reference 071\r\n"
                + "72, product id 072, Item Reference 072\r\n" + "73, product id 073, Item Reference 073\r\n" + "74, product id 074, Item Reference 074\r\n"
                + "75, product id 075, Item Reference 075\r\n" + "76, product id 076, Item Reference 076\r\n" + "77, product id 077, Item Reference 077\r\n"
                + "78, product id 078, Item Reference 078\r\n" + "79, product id 079, Item Reference 079\r\n" + "80, product id 080, Item Reference 080\r\n"
                + "81, product id 081, Item Reference 081\r\n" + "82, product id 082, Item Reference 082\r\n" + "83, product id 083, Item Reference 083\r\n"
                + "84, product id 084, Item Reference 084\r\n" + "85, product id 085, Item Reference 085\r\n" + "86, product id 086, Item Reference 086\r\n"
                + "87, product id 087, Item Reference 087\r\n" + "88, product id 088, Item Reference 088\r\n" + "89, product id 089, Item Reference 089\r\n"
                + "90, product id 090, Item Reference 090\r\n" + "91, product id 091, Item Reference 091\r\n" + "92, product id 092, Item Reference 092\r\n"
                + "93, product id 093, Item Reference 093\r\n" + "94, product id 094, Item Reference 094\r\n" + "95, product id 095, Item Reference 095\r\n"
                + "96, product id 096, Item Reference 096\r\n" + "97, product id 097, Item Reference 097\r\n" + "98, product id 098, Item Reference 098\r\n"
                + "99, product id 099, Item Reference 099\r\n" + "100, product id 100, Item Reference 100\r\n";

        Map<String, Object> importResult = null;
        try {
            importResult = distImportToolFacade.searchProductsFromData(data);
        } catch (final ImportToolException e) {
            Assert.fail(e.getMessage());
        }
        final List<ImportToolMatchingData> matchingProducts = (List<ImportToolMatchingData>) importResult.get("matchingProducts");
        Assert.assertEquals(98, matchingProducts.size());

        final List<String[]> notMatchingProducts = (List<String[]>) importResult.get("notMatchingProducts");
        Assert.assertEquals(2, notMatchingProducts.size());
    }

    @Test
    // Search 1 article with a wrong quantity value. The value is replace to 1
    public void testSearchProductsFromDataQuantityError() {
        // Quantity, Article Number, Item Reference
        final String data = "quantityError, product id 001, Item Reference1 \r\n";

        Map<String, Object> importResult = null;
        try {
            importResult = distImportToolFacade.searchProductsFromData(data);
        } catch (final ImportToolException e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertNotNull(importResult);

        final List<ImportToolMatchingData> matchingProducts = (List<ImportToolMatchingData>) importResult.get("matchingProducts");
        Assert.assertEquals(1, matchingProducts.size());
        Assert.assertEquals(1, matchingProducts.get(0).getQuantity());

        final List<String[]> notMatchingProducts = (List<String[]>) importResult.get("notMatchingProducts");
        Assert.assertTrue(notMatchingProducts.isEmpty());
    }

    @Test
    // Search 1 article with a wrong quantity value. The value is replace to 1
    public void testSearchProductsFromDataReferenceError() {
        // Quantity, Article Number, Item Reference
        final String data = "quantityError, product id 001, A reference that is much much much to long\r\n";

        try {
            distImportToolFacade.searchProductsFromData(data);
            Assert.fail("Should not get to this point. The reference is longer than 35 chars.");
        } catch (final ImportToolException e) {
            Assert.assertEquals(ErrorSource.CUSTOMER_REFERENCE_FIELD, e.getErrorSource());
        }
    }

    @Test
    // Search 4 articles, 3 are in base, one is not (wrongProduct-id), and one is unavailable because it is not assigned to sales organization (unavailable product id 001)
    public void testSearchProductsFromCsv() {
        // Quantity, Article Number, Item Reference
        final String orderFileName = path + "testImportToolFromCsv.csv";

        Map<String, Object> importResult = null;
        try {
            importResult = distImportToolFacade.searchProductsFromFile(orderFileName, 1, 0, 2, true);
        } catch (final ImportToolException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(importResult);

        final List<ImportToolMatchingData> matchingProducts = (List<ImportToolMatchingData>) importResult.get("matchingProducts");
        orderList(matchingProducts);

        Assert.assertEquals(3, matchingProducts.size());
        Assert.assertEquals("product id 001", matchingProducts.get(0).getProductCode());
        Assert.assertEquals("product id 002", matchingProducts.get(1).getProductCode());
        Assert.assertEquals("product id 003", matchingProducts.get(2).getProductCode());

        final List<String[]> notMatchingProducts = (List<String[]>) importResult.get("notMatchingProducts");
        Assert.assertEquals(1, notMatchingProducts.size());
        Assert.assertEquals("wrong product id", notMatchingProducts.get(0)[1]);

        final List<ImportToolMatchingData> unavailableProducts = (List<ImportToolMatchingData>) importResult.get("unavailableProducts");
        Assert.assertEquals(1, unavailableProducts.size());
        Assert.assertEquals("unavailable product id 001", unavailableProducts.get(0).getProductCode());
    }

    @Test
    // The datas are not good formated. There is no separator ("\t" or ";")
    public void testSearchProductsFromCsvWithoutSeparator() {
        // Quantity, Article Number, Item Reference
        final String orderFileName = path + "testImportToolFromCsvWithoutSeparator.csv";

        try {
            distImportToolFacade.searchProductsFromFile(orderFileName, 1, 0, 2, false);
            Assert.fail();
        } catch (final ImportToolException e) {
            Assert.assertEquals(ErrorSource.FIELD_SEPARATOR_NOT_FOUND, e.getErrorSource());
        }
    }

    @Test
    // No datas to search
    public void testSearchProductsFromCsvContentEmpty() {
        // Quantity, Article Number, Item Reference
        final String orderFileName = path + "testImportToolFromCsvContentEmpty.csv";

        try {
            distImportToolFacade.searchProductsFromFile(orderFileName, 1, 0, 2, true);
            Assert.fail();
        } catch (final ImportToolException e) {
            Assert.assertEquals(ErrorSource.NO_DATA, e.getErrorSource());
        }
    }

    @Test
    // Search 1 article but set the quantity with a wrong value. The value must be set to one
    public void testSearchProductsFromCsvQuantityError() {
        // Quantity, Article Number, Item Reference
        final String orderFileName = path + "testImportToolFromCsvQuantityError.csv";

        Map<String, Object> importResult = null;
        try {
            importResult = distImportToolFacade.searchProductsFromFile(orderFileName, 1, 0, 2, true);
        } catch (final ImportToolException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(importResult);

        final List<ImportToolMatchingData> matchingProducts = (List<ImportToolMatchingData>) importResult.get("matchingProducts");
        Assert.assertEquals(1, matchingProducts.size());
        Assert.assertEquals(1, matchingProducts.get(0).getQuantity());

        final List<String[]> notMatchingProducts = (List<String[]>) importResult.get("notMatchingProducts");
        Assert.assertTrue(notMatchingProducts.isEmpty());
    }

    @Test
    // Search 1 article but set the quantity with a wrong value. The value must be set to one
    public void testSearchProductsFromCsvWithMerge() {
        // Quantity, Article Number, Item Reference
        final String orderFileName = path + "testImportToolFromCsvWithMerge.csv";

        Map<String, Object> importResult = null;
        try {
            importResult = distImportToolFacade.searchProductsFromFile(orderFileName, 1, 0, 2, true);
        } catch (final ImportToolException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(importResult);

        final List<ImportToolMatchingData> matchingProducts = (List<ImportToolMatchingData>) importResult.get("matchingProducts");
        Assert.assertEquals(1, matchingProducts.size());
        Assert.assertEquals(3, matchingProducts.get(0).getQuantity());

        final List<String[]> notMatchingProducts = (List<String[]>) importResult.get("notMatchingProducts");
        Assert.assertTrue(notMatchingProducts.isEmpty());
    }

    @Test
    // Search 1 article but set the quantity with a wrong value. The value must be set to one
    public void testSearchProductsFromCsvWithMergeWithQuantityError() {
        // Quantity, Article Number, Item Reference
        final String orderFileName = path + "testImportToolFromCsvWithMergeWithQuantityError.csv";

        Map<String, Object> importResult = null;
        try {
            importResult = distImportToolFacade.searchProductsFromFile(orderFileName, 1, 0, 2, true);
        } catch (final ImportToolException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(importResult);

        final List<ImportToolMatchingData> matchingProducts = (List<ImportToolMatchingData>) importResult.get("matchingProducts");
        Assert.assertEquals(1, matchingProducts.size());
        Assert.assertEquals(2, matchingProducts.get(0).getQuantity());

        final List<String[]> notMatchingProducts = (List<String[]>) importResult.get("notMatchingProducts");
        Assert.assertTrue(notMatchingProducts.isEmpty());
    }

    @Test
    // Search 1 article but set the quantity with a wrong quantity value. The value is replace to "1"
    public void testSearchProductsFromCsvMoreMaxLines() {
        // Quantity, Article Number, Item Reference
        final String orderFileName = path + "testImportToolFromCsvMoreMaxLines.csv";

        try {
            distImportToolFacade.searchProductsFromFile(orderFileName, 1, 0, 2, true);
            Assert.fail("Should not get to this point. The file is longer than 300 lines.");
        } catch (final ImportToolException e) {
            Assert.assertEquals(ErrorSource.TOO_MANY_LINES, e.getErrorSource());
        }
    }

    @Test
    // Search 4 articles, 3 are in base and one is not (wrongProduct-id)
    public void testSearchProductsFromXls() {
        // Quantity, Article Number, Item Reference
        final String orderFileName = path + "testImportToolFromXls.xls";

        Map<String, Object> importResult = null;
        try {
            importResult = distImportToolFacade.searchProductsFromFile(orderFileName, 1, 0, 2, false);
        } catch (final ImportToolException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(importResult);

        final List<ImportToolMatchingData> matchingProducts = (List<ImportToolMatchingData>) importResult.get("matchingProducts");
        orderList(matchingProducts);

        Assert.assertEquals(2, matchingProducts.size());
        Assert.assertEquals("product id 001", matchingProducts.get(0).getProductCode());
        Assert.assertEquals("product id 002", matchingProducts.get(1).getProductCode());

        final List<String[]> notMatchingProducts = (List<String[]>) importResult.get("notMatchingProducts");
        Assert.assertEquals(1, notMatchingProducts.size());
        Assert.assertEquals("wrong product id", notMatchingProducts.get(0)[1]);
    }

    @Test
    // No datas to search
    public void testSearchProductsFromXlsEmpty() {
        // Quantity, Article Number, Item Reference
        final String orderFileName = path + "testImportToolFromXlsEmpty.xls";

        try {
            distImportToolFacade.searchProductsFromFile(orderFileName, 1, 0, 2, false);
            Assert.fail();
        } catch (final ImportToolException e) {
            Assert.assertEquals(ErrorSource.FILE_EMPTY, e.getErrorSource());
        }
    }

    @Test
    // No datas to search
    public void testSearchProductsFromXlsContentEmpty() {
        // Quantity, Article Number, Item Reference
        final String orderFileName = path + "testImportToolFromXlsContentEmpty.xls";

        try {
            distImportToolFacade.searchProductsFromFile(orderFileName, 1, 0, 2, false);
            Assert.fail();
        } catch (final ImportToolException e) {
            Assert.assertEquals(ErrorSource.FILE_EMPTY, e.getErrorSource());
        }
    }

    @Test
    // Search 1 article but set the quantity with a wrong quantity value. The value is replace to "1"
    public void testSearchProductsFromXLSQuantityError() {
        // Quantity, Article Number, Item Reference
        final String orderFileName = path + "testImportToolFromXlsQuantityError.xls";

        Map<String, Object> importResult = null;
        try {
            importResult = distImportToolFacade.searchProductsFromFile(orderFileName, 1, 0, 2, false);
        } catch (final ImportToolException e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertNotNull(importResult);

        final List<ImportToolMatchingData> matchingProducts = (List<ImportToolMatchingData>) importResult.get("matchingProducts");
        Assert.assertEquals(1, matchingProducts.size());
        Assert.assertEquals(1, matchingProducts.get(0).getQuantity());

        final List<String[]> notMatchingProducts = (List<String[]>) importResult.get("notMatchingProducts");
        Assert.assertTrue(notMatchingProducts.isEmpty());
    }

    @Test
    // More than 100 lines, only the 100 first lines are used
    public void testSearchProductsFromXlsMoreMaxLines() {
        // Quantity, Article Number, Item Reference
        final String orderFileName = path + "testImportToolFromXlsMoreMaxLines.xls";

        try {
            distImportToolFacade.searchProductsFromFile(orderFileName, 0, 3, 4, false);
            Assert.fail("Should not get to this point. The file is longer than 300 lines.");
        } catch (final ImportToolException e) {
            Assert.assertEquals(ErrorSource.TOO_MANY_LINES, e.getErrorSource());
        }
    }

    @Test
    // Merge of 2 lines
    public void testSearchProductsFromXlsWithMerge() {
        // Quantity, Article Number, Item Reference
        final String orderFileName = path + "testImportToolFromXlsWithMerge.xls";

        Map<String, Object> importResult = null;
        try {
            importResult = distImportToolFacade.searchProductsFromFile(orderFileName, 1, 0, 2, false);
        } catch (final ImportToolException e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertNotNull(importResult);

        final List<ImportToolMatchingData> matchingProducts = (List<ImportToolMatchingData>) importResult.get("matchingProducts");
        Assert.assertEquals(1, matchingProducts.size());
        Assert.assertEquals(5, matchingProducts.get(0).getQuantity());

        final List<String[]> notMatchingProducts = (List<String[]>) importResult.get("notMatchingProducts");
        Assert.assertTrue(notMatchingProducts.isEmpty());
    }

    @Test
    // More than 100 lines, 1 line is merged with a wrong quantity value (replace to 1)
    public void testSearchProductsFromXlsWithMergeWithQuantityError() {
        // Quantity, Article Number, Item Reference
        final String orderFileName = path + "testImportToolFromXlsWithMergeWithQuantityError.xls";

        Map<String, Object> importResult = null;
        try {
            importResult = distImportToolFacade.searchProductsFromFile(orderFileName, 1, 0, 2, false);
        } catch (final ImportToolException e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertNotNull(importResult);

        final List<ImportToolMatchingData> matchingProducts = (List<ImportToolMatchingData>) importResult.get("matchingProducts");
        Assert.assertEquals(1, matchingProducts.size());
        Assert.assertEquals(3, matchingProducts.get(0).getQuantity());

        final List<String[]> notMatchingProducts = (List<String[]>) importResult.get("notMatchingProducts");
        Assert.assertTrue(notMatchingProducts.isEmpty());
    }

    private void orderList(final List<ImportToolMatchingData> list) {
        Collections.sort(list, new Comparator<ImportToolMatchingData>() {
            @Override
            public int compare(final ImportToolMatchingData importToolMatchingData1, final ImportToolMatchingData importToolMatchingData2) {
                return importToolMatchingData1.getProductCode().compareTo(importToolMatchingData2.getProductCode());
            }
        });
    }
}
