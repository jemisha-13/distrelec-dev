/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.inout.erp.impl;

import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.namics.distrelec.b2b.core.mock.sap.AbstractSapWebServiceTest;
import com.namics.distrelec.b2b.core.mock.sap.SIHybrisV1OutMockBuilder;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.constants.Europe1Constants;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.namics.distrelec.b2b.core.mock.sap.SIHybrisV1OutMockBuilder.customerPrice;

/**
 * {@code SAPPriceFactoryTest}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.8
 */
public class SAPPriceFactoryTest extends AbstractSapWebServiceTest {

    @Resource(name = "sap.priceFactory")
    private SAPPriceFactory sapPriceFactory;

    @Resource
    private CMSSiteService cmsSiteService;

    @Resource
    private ProductService productService;

    @Resource
    private ModelService modelService;

    @Resource
    private SessionService sessionService;

    @Resource
    private ConfigurationService configurationService;

    private String anonymousCustomerId;
    private ProductModel product0;
    private ProductModel product1;
    private ProductModel product2;
    private ProductModel product3;

    private JaloSession jaloSession;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        importCsv("/distrelecB2Bcore/test/priceFactory/content-catalog.impex", "utf-8");
        importCsv("/distrelecB2Bcore/test/priceFactory/store.impex", "utf-8");
        importCsv("/distrelecB2Bcore/test/priceFactory/priceFactoryTest.impex", "utf-8");
        importCsv("/distrelecB2Bcore/test/priceFactory/site.impex", "utf-8");

        anonymousCustomerId = configurationService.getConfiguration().getString(SAPPriceFactory.DUMMY_CUSTOMER_ID_PROP, null);
        product0 = productService.getProductForCode("11029907");
        product1 = productService.getProductForCode("30033729");
        product2 = productService.getProductForCode("14022002");
        product3 = productService.getProductForCode("11029908");


        jaloSession = JaloSession.getCurrentSession();
        jaloSession.setUser(UserManager.getInstance().getAnonymousCustomer());
        jaloSession.getSessionContext().setCurrency(C2LManager.getInstance().getCurrencyByIsoCode("CHF"));

        cmsSiteService.setCurrentSite(cmsSiteService.getSites().iterator().next());
        sessionService.setAttribute(Europe1Constants.PARAMS.UPG, cmsSiteService.getCurrentSite().getUserPriceGroup());
        sessionService.setAttribute(Europe1Constants.PARAMS.UTG, cmsSiteService.getCurrentSite().getUserTaxGroup());
    }

    /**
     * Test method for
     * {@link com.namics.distrelec.b2b.core.inout.erp.impl.SAPPriceFactory#getProductPriceInformations(de.hybris.platform.jalo.SessionContext, de.hybris.platform.core.model.product.ProductModel, java.util.Date, boolean, boolean, boolean)}.
     */
    @Test
    public void testGetProductPriceInformations() {
        SIHybrisV1Out siHybrisV1OutMock =
                new SIHybrisV1OutMockBuilder()
                        .expectIf07CustomerPrice(anonymousCustomerId, customerPrice(product0, 1L, 20))
                        .expectIf07CustomerPrice(anonymousCustomerId, customerPrice(product1, 1L, 18))
                        .expectIf07CustomerPrice(anonymousCustomerId, customerPrice(product2, 1L, 23))
                        .build();
        setWebServiceClient(siHybrisV1OutMock);

        try {
            List<PriceInformation> prices = sapPriceFactory.getProductPriceInformations(jaloSession.getSessionContext(), product0, new Date(), true, true,
                    true);
            Assert.assertTrue(1 <= prices.size());
            Assert.assertEquals(Long.valueOf(1), prices.get(0).getQualifierValue(PriceRow.MINQTD));
            Assert.assertEquals(Double.valueOf(20), getPrice(prices.get(0)));

            prices = sapPriceFactory.getProductPriceInformations(jaloSession.getSessionContext(), product1, new Date(), true, true, true);
            Assert.assertTrue(1 <= prices.size());
            Assert.assertEquals(Long.valueOf(1), prices.get(0).getQualifierValue(PriceRow.MINQTD));
            Assert.assertEquals(Double.valueOf(18), getPrice(prices.get(0)));

            // No list price is returned because we have a special price for this product and than no list price is shown
            prices = sapPriceFactory.getProductPriceInformations(jaloSession.getSessionContext(), product2, new Date(), true, true, true);
            Assert.assertTrue(1 <= prices.size());
            Assert.assertEquals(Long.valueOf(1), prices.get(0).getQualifierValue(PriceRow.MINQTD));
            Assert.assertEquals(Double.valueOf(23), getPrice(prices.get(0)));
        } catch (final JaloPriceFactoryException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link com.namics.distrelec.b2b.core.inout.erp.impl.SAPPriceFactory#getProductListPriceInformations(de.hybris.platform.jalo.SessionContext, de.hybris.platform.core.model.product.ProductModel, java.util.Date, boolean, boolean, boolean)}.
     */
    @Test
    public void testGetProductListPriceInformations() {
        SIHybrisV1Out siHybrisV1OutMock =
                new SIHybrisV1OutMockBuilder()
                        .expectIf07CustomerPrice(anonymousCustomerId, customerPrice(product0, 1L, 19))
                        .expectIf07CustomerPrice(anonymousCustomerId, customerPrice(product1, 1L, 17))
                        .expectIf07CustomerPrice(anonymousCustomerId, customerPrice(product2, 1L, 22))
                        .build();
        setWebServiceClient(siHybrisV1OutMock);

        try {
            List<PriceInformation> prices = sapPriceFactory.getProductListPriceInformations(jaloSession.getSessionContext(), product0, new Date(), true, true,
                    true);
            Assert.assertTrue(1 <= prices.size());
            Assert.assertEquals(Long.valueOf(1), prices.get(0).getQualifierValue(PriceRow.MINQTD));
            Assert.assertEquals(Double.valueOf(19), getPrice(prices.get(0)));

            prices = sapPriceFactory.getProductListPriceInformations(jaloSession.getSessionContext(), product1, new Date(), true, true, true);
            Assert.assertTrue(1 <= prices.size());
            Assert.assertEquals(Long.valueOf(1), prices.get(0).getQualifierValue(PriceRow.MINQTD));
            Assert.assertEquals(Double.valueOf(17), getPrice(prices.get(0)));

            // No list price is returned because we have a special price for this product and than no list price is shown
            prices = sapPriceFactory.getProductListPriceInformations(jaloSession.getSessionContext(), product2, new Date(), true, true, true);
            Assert.assertTrue(1 <= prices.size());
            Assert.assertEquals(Long.valueOf(1), prices.get(0).getQualifierValue(PriceRow.MINQTD));
            Assert.assertEquals(Double.valueOf(22), getPrice(prices.get(0)));
        } catch (final JaloPriceFactoryException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link com.namics.distrelec.b2b.core.inout.erp.impl.SAPPriceFactory#getProductPriceInformations(de.hybris.platform.jalo.SessionContext, de.hybris.platform.jalo.product.Product, java.util.Date, boolean, boolean)}.
     */
    @Test
    public void testgetProductPriceInformations() {
        try {

            final Product productItem = (Product) modelService.getSource(product3);
            List<PriceInformation> prices = sapPriceFactory.getProductPriceInformations(jaloSession.getSessionContext(), productItem, new Date(), true, false);
            Assert.assertTrue(1 <= prices.size());
            Assert.assertEquals(Long.valueOf(1), prices.get(0).getQualifierValue(PriceRow.MINQTD));
            Assert.assertEquals(Double.valueOf(20), getPrice(prices.get(0)));

        } catch (final JaloPriceFactoryException e) {
            Assert.fail(e.getMessage());
        }
    }

    private Double getPrice(PriceInformation priceInformation) {
        return priceInformation.getPriceValue().getValue();
    }
}
