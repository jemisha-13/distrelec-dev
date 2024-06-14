/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.price;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.jalo.DistPriceRow;
import com.namics.distrelec.b2b.core.service.product.DistCommercePriceService;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.constants.Europe1Constants;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.daos.CountryDao;
import de.hybris.platform.servicelayer.session.SessionService;

public class DefaultDistCommercePriceServiceIntTest extends ServicelayerTest {

    @Resource
    private DistCommercePriceService commercePriceService;

    @Resource
    private ProductService productService;

    @Resource
    private DistrelecCMSSiteService cmsSiteService;

    @Resource
    private SessionService sessionService;

    @Resource
    private CountryDao countryDao;

    @SuppressWarnings("deprecation")
    @Before
    public void setup() throws ImpExException {
        importCsv("/distrelecB2Bcore/test/testCommercePriceService.impex", "utf-8");
        final CMSSiteModel currentSite = cmsSiteService.getSiteForCountry(countryDao.findCountriesByCode("DE").get(0));
        cmsSiteService.setCurrentSite(currentSite);

        jaloSession = JaloSession.getCurrentSession();
        jaloSession.setUser(UserManager.getInstance().getAnonymousCustomer());
        jaloSession.getSessionContext().setCurrency(C2LManager.getInstance().getCurrencyByIsoCode("EUR"));

        sessionService.setAttribute(Europe1Constants.PARAMS.UPG, cmsSiteService.getCurrentSite().getUserPriceGroup());
        sessionService.setAttribute(Europe1Constants.PARAMS.UTG, cmsSiteService.getCurrentSite().getUserTaxGroup());
    }

    /**
     * this test is retriving the price for a product that has the following prices:<br>
     * <ul>
     * <li>testCondition1 type=PR00, value=100, startDate= five days ago</li><br>
     * <li>testCondition2 type=PR00, value=90, startDate= six days ago</li><br>
     * <li>testCondition3 type=PR00, value=80, startDate= seven days ago</li><br>
     * <br>
     * </ul>
     * since the testCondition1 is the most recent then is the right one.<br>
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testWebPrice_OnlyListPrice() {

        final ProductModel product = productService.getProductForCode("testProduct1");
        final PriceInformation priceInfo = commercePriceService.getWebPriceForProduct(product);
        assertNotNull("price info cannot be null", priceInfo);
        assertNotNull("price value cannot be null", priceInfo.getPriceValue());
        assertTrue("The price is retrived in the wrong way.", Double.valueOf(100D).equals(Double.valueOf(priceInfo.getPriceValue().getValue())));
    }

    /**
     * this test is retriving the price for a product that has the following prices:<br>
     * <ul>
     * <li>testCondition1 type=PR00, value=100, startDate= one day ago</li><br>
     * <li>testCondition2 type=PR00, value=90, startDate= two days ago</li><br>
     * <li>testCondition3 type=ZR00, value=80, startDate= ten days ago</li><br>
     * <br>
     * </ul>
     * since the testCondition3 is the only special price then is the right one.<br>
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testWebPrice_ListPriceAndSpecial() {

        final ProductModel product = productService.getProductForCode("testProduct2");
        final PriceInformation priceInfo = commercePriceService.getWebPriceForProduct(product);
        assertNotNull("price info cannot be null", priceInfo);
        assertNotNull("price value cannot be null", priceInfo.getPriceValue());
        assertTrue("The price is retrived in the wrong way.", Double.valueOf(80D).equals(Double.valueOf(priceInfo.getPriceValue().getValue())));
    }

    /**
     * this test is retriving the price for a product that has the following prices:<br>
     * <ul>
     * <li>testCondition1 type=PR00, value=110, startDate= one day ago</li><br>
     * <li>testCondition2 type=PR00, value=100, startDate= two day ago</li><br>
     * <li>testCondition3 type=PR00, value=90, startDate= three days ago</li><br>
     * <li>testCondition4 type=ZN00, value=80, startDate= five days ago</li><br>
     * <li>testCondition5 type=ZN00, value=60, startDate= ten days ago</li><br>
     * <br>
     * </ul>
     * since the testCondition4 is the priority price most recent is the right one.<br>
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testWebPrice_ListPriceAndPrioPrice() {

        final ProductModel product = productService.getProductForCode("testProduct3");
        final PriceInformation priceInfo = commercePriceService.getWebPriceForProduct(product);
        assertNotNull("price info cannot be null", priceInfo);
        assertNotNull("price value cannot be null", priceInfo.getPriceValue());
        assertTrue("The price is retrived in the wrong way.", Double.valueOf(80D).equals(Double.valueOf(priceInfo.getPriceValue().getValue())));
    }

    /**
     * this test is retriving the price for a product that has the following prices:<br>
     * <ul>
     * <li>testCondition1 type=PR00, value=110, startDate= one day ago</li><br>
     * <li>testCondition2 type=PR00, value=100, startDate= two day ago</li><br>
     * <li>testCondition3 type=PR00, value=90, startDate= three days ago</li><br>
     * <li>testCondition4 type=ZN00, value=80, startDate= five days ago</li><br>
     * <li>testCondition5 type=ZN00, value=60, startDate= ten days ago</li><br>
     * <br>
     * </ul>
     * since the testCondition4 is the priority price most recent is the right one.<br>
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testListWebPrice_ListPriceAndPrioPrice() {

        final ProductModel product = productService.getProductForCode("testProduct4");

        final List<PriceInformation> priceInfos = commercePriceService.getScaledPriceInformations(product);
        assertTrue("Price Lists must not be empty", CollectionUtils.isNotEmpty(priceInfos));
        assertTrue("There must be 6 price row for 3 scales", priceInfos.size() == 6);

        for (final PriceInformation priceInfo : priceInfos) {
            if (((DistPriceRow) priceInfo.getQualifierValue("pricerow")).isSpecialPrice()) {
                switch (getMinQuantity(priceInfo).intValue()) {
                case 1:
                    assertTrue("The price for scale " + getMinQuantity(priceInfo) + " is retrived in the wrong way.",
                            Double.valueOf(80D).equals(Double.valueOf(priceInfo.getPriceValue().getValue())));
                    break;
                case 5:
                    assertTrue("The price for scale " + getMinQuantity(priceInfo) + " is retrived in the wrong way.",
                            Double.valueOf(78D).equals(Double.valueOf(priceInfo.getPriceValue().getValue())));
                    break;
                case 10:
                    assertTrue("The price for scale " + getMinQuantity(priceInfo) + " is retrived in the wrong way.",
                            Double.valueOf(76D).equals(Double.valueOf(priceInfo.getPriceValue().getValue())));
                    break;
                }
            } else {
                switch (getMinQuantity(priceInfo).intValue()) {
                case 1:
                    assertTrue("The price for scale " + getMinQuantity(priceInfo) + " is retrived in the wrong way.",
                            Double.valueOf(110D).equals(Double.valueOf(priceInfo.getPriceValue().getValue())));
                    break;
                case 5:
                    assertTrue("The price for scale " + getMinQuantity(priceInfo) + " is retrived in the wrong way.",
                            Double.valueOf(108D).equals(Double.valueOf(priceInfo.getPriceValue().getValue())));
                    break;
                case 10:
                    assertTrue("The price for scale " + getMinQuantity(priceInfo) + " is retrived in the wrong way.",
                            Double.valueOf(106D).equals(Double.valueOf(priceInfo.getPriceValue().getValue())));
                    break;
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testListWebPrice_ListPriceAndSpecialPrice_complex() {

        final ProductModel product = productService.getProductForCode("testProduct5");

        final List<PriceInformation> priceInfos = commercePriceService.getScaledPriceInformations(product);
        assertTrue("Price Lists must not be empty", CollectionUtils.isNotEmpty(priceInfos));
        assertTrue("There must be 1 price row for 1 scale", priceInfos.size() == 1);

        for (final PriceInformation priceInfo : priceInfos) {
            assertTrue("The price for scale " + getMinQuantity(priceInfo) + " is retrived in the wrong way.",
                    Double.valueOf(80D).equals(Double.valueOf(priceInfo.getPriceValue().getValue())));

        }
    }

    protected Long getMinQuantity(final PriceInformation priceInfo) {
        final Map qualifiers = priceInfo.getQualifiers();
        final Object minQtdObj = qualifiers.get(PriceRow.MINQTD);
        if (minQtdObj instanceof Long) {
            return (Long) minQtdObj;
        }
        return null;
    }

}
