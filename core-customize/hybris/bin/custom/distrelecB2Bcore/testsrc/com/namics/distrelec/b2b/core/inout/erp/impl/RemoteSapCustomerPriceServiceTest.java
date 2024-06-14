/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.distrelec.webservice.sap.v1.CurrencyCode;
import com.distrelec.webservice.sap.v1.CustomerPriceArticles;
import com.distrelec.webservice.sap.v1.CustomerPriceRequest;
import com.distrelec.webservice.sap.v1.CustomerPriceResponse;
import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.namics.distrelec.b2b.core.constants.DistConstants;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductPriceData;
import de.hybris.platform.commercefacades.product.data.VolumePriceData;

/**
 * Tests the {@link SapCustomerPriceService} class with the Q SAP PI system.
 * 
 * These tests need a connection to the Q SAP PI system. You need an ssh tunnel to the ATOS Q environment for that. The tests make
 * assumptions regarding the implementation of the SAP PI system, so a failing test does not necessarily indicate, that the client side
 * implementation is wrong, but it may suggest, that the client side implementation needs to be changed.
 * 
 * @author fbersani, Distrelec AG
 * @since Distrelec 1.0
 */
@Ignore("You need an ssh tunnel to ATOS Q in order to test this locally.")
@UnitTest
public class RemoteSapCustomerPriceServiceTest {

    private static SIHybrisV1Out webServiceClient;

    private static SapCustomerPriceService sapCustomerPriceService;

    final String PRODUCT_CODE_20000000 = "20000000";
    final String PRODUCT_CODE_20000002 = "20000002";
    final String SALES_ORGANIZATION_AT = "7320";
    final String SALES_ORGANIZATION_DE = "7350";
    final String SALES_ORGANIZATION_WRONG = "1234";
    final String CUSTOMER_CODE_1000150 = "1000150";

    final long PRICE_QUANTITY_1 = 1L;
    final long PRICE_QUANTITY_10 = 10L;
    final long PRICE_QUANTITY_100 = 100L;

    @BeforeClass
    public static void setUp() {
        // init cache
        CacheManager.getInstance().addCacheIfAbsent(new Cache(DistConstants.CacheName.CUSTOMER_PRICE, 100000, false, false, 300, 0));

        final PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        final Properties properties = new Properties();
        properties.setProperty("core.sapPi.webservice.address", "http://daechs062u.dae.datwyler.biz:50000");
        properties.setProperty("core.sapPi.webservice.senderService", "WSD");
        properties.setProperty("core.sapPi.webservice.enableSchemaValidation", "true");
        configurer.setProperties(properties);
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();
        context.addBeanFactoryPostProcessor(configurer);
        context.setConfigLocation("../resources/distrelecB2Bcore-SAP-PI-webservice-spring.xml");
        context.refresh();
        webServiceClient = (SIHybrisV1Out) context.getBean("core.sapPiWebserviceClientIF08");
        sapCustomerPriceService = new SapCustomerPriceService();
        sapCustomerPriceService.setWebServiceClient(webServiceClient);
    }

    /*
     * This test assumes that there is at least one item in stock for every requested combination of product code and warehouse in the SAP
     * PI system.
     */
    @Ignore
    @Test
    public void testSapCustomerPriceServiceWithSingleProduct() {
        // init
        final CustomerPriceRequest request = new CustomerPriceRequest();

        request.setSalesOrganization(SALES_ORGANIZATION_DE);

        request.setCustomerId(CUSTOMER_CODE_1000150);
        request.setCurrencyCode(CurrencyCode.EUR);

        final List<CustomerPriceArticles> articles = new ArrayList<CustomerPriceArticles>();
        final CustomerPriceArticles article1 = new CustomerPriceArticles();
        article1.setArticleNumber(PRODUCT_CODE_20000000);
        article1.setQuantity(PRICE_QUANTITY_1);
        articles.add(article1);

        // final CustomerPriceArticles article2 = new CustomerPriceArticles();
        // article2.setArticleNumber(PRODUCT_CODE_20000002);
        // article2.setQuantity(PRICE_QUANTITY_1);
        // articles.add(article2);

        final CustomerPriceArticles article3 = new CustomerPriceArticles();
        article3.setArticleNumber(PRODUCT_CODE_20000000);
        article3.setQuantity(PRICE_QUANTITY_10);
        articles.add(article3);

        // final CustomerPriceArticles article4 = new CustomerPriceArticles();
        // article4.setArticleNumber(PRODUCT_CODE_20000002);
        // article4.setQuantity(PRICE_QUANTITY_10);
        // articles.add(article4);

        request.getArticles().addAll(articles);
        final Set<ProductPriceData> response = sapCustomerPriceService.getPricesForPricesList(CUSTOMER_CODE_1000150, SALES_ORGANIZATION_DE, "EUR",
                PRODUCT_CODE_20000000, null);

        // evaluation
        assertEquals("size of returned product prices is wrong", 1, response.size());
        final List<String> productCodes = new ArrayList<String>(response.stream().map(ProductPriceData :: getArticleNumber).collect(Collectors.toList()));
        assertEquals("product code is wrong", PRODUCT_CODE_20000000, productCodes.get(0));
        assertEquals("prices size is wrong", 2, response.stream().filter(sapPrice -> sapPrice.getArticleNumber().equalsIgnoreCase("PRODUCT_CODE_20000000")).findAny().get().getVolumePriceData().size());

        // final List<Long> productQuantities = new ArrayList<Long>(response.get(productCodes.get(0)).keySet());
        // assertEquals("the quantity is wrong", PRICE_QUANTITY_1, (productQuantities.get(0)).longValue());
        // assertEquals("the quantity is wrong", PRICE_QUANTITY_10, (productQuantities.get(1)).longValue());
    }

    // @Ignore
    @Test
    public void testSapCustomerPriceServiceWithMultipleProduct() {
        // init
        final CustomerPriceRequest request = new CustomerPriceRequest();

        request.setSalesOrganization(SALES_ORGANIZATION_DE);

        request.setCustomerId(CUSTOMER_CODE_1000150);
        request.setCurrencyCode(CurrencyCode.EUR);

        final List<CustomerPriceArticles> articles = new ArrayList<CustomerPriceArticles>();
        final CustomerPriceArticles article1 = new CustomerPriceArticles();
        article1.setArticleNumber(PRODUCT_CODE_20000000);
        article1.setQuantity(PRICE_QUANTITY_1);
        articles.add(article1);

        final CustomerPriceArticles article2 = new CustomerPriceArticles();
        article2.setArticleNumber(PRODUCT_CODE_20000002);
        article2.setQuantity(PRICE_QUANTITY_1);
        articles.add(article2);

        final CustomerPriceArticles article3 = new CustomerPriceArticles();
        article3.setArticleNumber(PRODUCT_CODE_20000000);
        article3.setQuantity(PRICE_QUANTITY_10);
        articles.add(article3);

        final CustomerPriceArticles article4 = new CustomerPriceArticles();
        article4.setArticleNumber(PRODUCT_CODE_20000002);
        article4.setQuantity(PRICE_QUANTITY_10);
        articles.add(article4);

        final CustomerPriceArticles article5 = new CustomerPriceArticles();
        article5.setArticleNumber(PRODUCT_CODE_20000000);
        article5.setQuantity(PRICE_QUANTITY_100);
        articles.add(article5);

        final CustomerPriceArticles article6 = new CustomerPriceArticles();
        article6.setArticleNumber(PRODUCT_CODE_20000002);
        article6.setQuantity(PRICE_QUANTITY_100);
        articles.add(article6);

        request.getArticles().addAll(articles);
        final Set<ProductPriceData> response = sapCustomerPriceService.getPricesForPricesList(CUSTOMER_CODE_1000150, SALES_ORGANIZATION_DE, "EUR",
                PRODUCT_CODE_20000000, null);

        // evaluation
        assertEquals("size of returned product prices is wrong", 2, response.size());
        final List<String> productCodes = new ArrayList<String>(response.stream().map(ProductPriceData::getArticleNumber).collect(Collectors.toList()));
        assertEquals("product code is wrong", PRODUCT_CODE_20000000, productCodes.get(0));
        assertEquals("product code is wrong", PRODUCT_CODE_20000002, productCodes.get(1));

        assertEquals("prices size is wrong", 3, response.stream().filter(key->key.getArticleNumber().equalsIgnoreCase(PRODUCT_CODE_20000000)).count());
        assertEquals("prices size is wrong", 3, response.stream().filter(key->key.getArticleNumber().equalsIgnoreCase(PRODUCT_CODE_20000002)).count());

        // final List<Long> productQuantities1 = new ArrayList<Long>(response.get(productCodes.get(0)).keySet());
        // assertEquals("the quantity is wrong", PRICE_QUANTITY_1, (productQuantities1.get(0)).longValue());
        // assertEquals("the quantity is wrong", PRICE_QUANTITY_10, (productQuantities1.get(1)).longValue());
        // assertEquals("the quantity is wrong", PRICE_QUANTITY_100, (productQuantities1.get(2)).longValue());
        //
        // final List<Long> productQuantities2 = new ArrayList<Long>(response.get(productCodes.get(1)).keySet());
        // assertEquals("the quantity is wrong", PRICE_QUANTITY_1, (productQuantities2.get(0)).longValue());
        // assertEquals("the quantity is wrong", PRICE_QUANTITY_10, (productQuantities2.get(1)).longValue());
        // assertEquals("the quantity is wrong", PRICE_QUANTITY_100, (productQuantities2.get(2)).longValue());
    }

    @Ignore
    @Test
    public void testSapCustomerPriceServiceWithWrongSalesOrganization() {

        // init
        final CustomerPriceRequest request = new CustomerPriceRequest();

        request.setSalesOrganization(SALES_ORGANIZATION_WRONG);

        request.setCustomerId(CUSTOMER_CODE_1000150);
        request.setCurrencyCode(CurrencyCode.EUR);

        final List<CustomerPriceArticles> articles = new ArrayList<CustomerPriceArticles>();
        final CustomerPriceArticles article1 = new CustomerPriceArticles();
        article1.setArticleNumber(PRODUCT_CODE_20000000);
        article1.setQuantity(PRICE_QUANTITY_1);
        articles.add(article1);

        // final CustomerPriceArticles article2 = new CustomerPriceArticles();
        // article2.setArticleNumber(PRODUCT_CODE_20000002);
        // article2.setQuantity(PRICE_QUANTITY_1);
        // articles.add(article2);

        final CustomerPriceArticles article3 = new CustomerPriceArticles();
        article3.setArticleNumber(PRODUCT_CODE_20000000);
        article3.setQuantity(PRICE_QUANTITY_10);
        articles.add(article3);

        // final CustomerPriceArticles article4 = new CustomerPriceArticles();
        // article4.setArticleNumber(PRODUCT_CODE_20000002);
        // article4.setQuantity(PRICE_QUANTITY_10);
        // articles.add(article4);

        request.getArticles().addAll(articles);

        try {
            final CustomerPriceResponse response = webServiceClient.if07CustomerPrice(request);
            assertNull("the response is wrong", response);
            fail("Expected an exception"); // this failure line is critical!

        } catch (P1FaultMessage ex) {
            assertEquals("Wrong Fault Info raised", "Sales organization " + SALES_ORGANIZATION_WRONG + " does not exist", ex.getFaultInfo().getFaultText());
        }

    }

    @Ignore
    @Test
    public void testSapCustomerPriceServiceWithWrongCustomer() {

        // init
        final CustomerPriceRequest request = new CustomerPriceRequest();

        request.setSalesOrganization(SALES_ORGANIZATION_AT);

        request.setCustomerId(CUSTOMER_CODE_1000150);
        request.setCurrencyCode(CurrencyCode.EUR);

        final List<CustomerPriceArticles> articles = new ArrayList<CustomerPriceArticles>();
        final CustomerPriceArticles article1 = new CustomerPriceArticles();
        article1.setArticleNumber(PRODUCT_CODE_20000000);
        article1.setQuantity(PRICE_QUANTITY_1);
        articles.add(article1);

        // final CustomerPriceArticles article2 = new CustomerPriceArticles();
        // article2.setArticleNumber(PRODUCT_CODE_20000002);
        // article2.setQuantity(PRICE_QUANTITY_1);
        // articles.add(article2);

        final CustomerPriceArticles article3 = new CustomerPriceArticles();
        article3.setArticleNumber(PRODUCT_CODE_20000000);
        article3.setQuantity(PRICE_QUANTITY_10);
        articles.add(article3);

        // final CustomerPriceArticles article4 = new CustomerPriceArticles();
        // article4.setArticleNumber(PRODUCT_CODE_20000002);
        // article4.setQuantity(PRICE_QUANTITY_10);
        // articles.add(article4);

        request.getArticles().addAll(articles);

        try {
            final CustomerPriceResponse response = webServiceClient.if07CustomerPrice(request);
            assertNull("the response is wrong", response);
            fail("Expected an exception"); // this failure line is critical!

        } catch (P1FaultMessage ex) {
            assertEquals("Wrong Fault Info raised", "Ship-to party " + CUSTOMER_CODE_1000150 + " is not assigned to a sold-to party", ex.getFaultInfo()
                    .getFaultText());
        }

    }

}
