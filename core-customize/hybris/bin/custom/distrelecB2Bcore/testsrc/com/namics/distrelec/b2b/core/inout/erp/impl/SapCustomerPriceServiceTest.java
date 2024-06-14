/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.distrelec.webservice.sap.v1.CurrencyCode;
import com.distrelec.webservice.sap.v1.CustomerPriceArticles;
import com.distrelec.webservice.sap.v1.CustomerPriceArticlesResponse;
import com.distrelec.webservice.sap.v1.CustomerPriceRequest;
import com.distrelec.webservice.sap.v1.CustomerPriceResponse;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.namics.distrelec.b2b.core.constants.DistConstants;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductPriceData;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.util.PriceValue;

/**
 * Test class for SapCustomerService logic
 * 
 * @author francesco, Distrelec AG
 * @since Distrelec 1.0
 * 
 */
@UnitTest
public class SapCustomerPriceServiceTest {

    final List<PriceInformation> PRICE_INFO_PRODUCT_20000000 = new ArrayList<PriceInformation>();

    final String PRODUCT_CODE_10000000 = "10000000";
    final String PRODUCT_CODE_20000000 = "20000000";
    final String SALES_ORGANIZATION_AT = "7350";
    final String CUSTOMER_CODE_1000150 = "1000150";

    final long PRICE_QUANTITY_1 = 1L;
    final long PRICE_QUANTITY_10 = 10L;

    @Mock
    private SIHybrisV1Out webServiceClient;

    @InjectMocks
    private final SapCustomerPriceService customerPriceService = new SapCustomerPriceService();

    @BeforeClass
    public static void initCache() {
        CacheManager.getInstance().addCacheIfAbsent(new Cache(DistConstants.CacheName.AVAILABILITY, 100000, false, false, 300, 0));
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void clearAllCaches() {
        CacheManager.getInstance().clearAll();
    }

    @Ignore
    @Test
    public void testSingleProductPrices() throws Exception {
        final List<PriceInformation> priceInfo = initPriceInfo();
        // prepare price list

        final CustomerPriceResponse mockedCustomerPriceResponseSingle = initializeCustomerPriceResponseSingle();

        // final CustomerPriceRequest request = mock(CustomerPriceRequest.class);
        final CustomerPriceRequest request = new CustomerPriceRequest();

        request.setSalesOrganization(SALES_ORGANIZATION_AT);

        request.setCustomerId(CUSTOMER_CODE_1000150);
        request.setCurrencyCode(CurrencyCode.EUR);

        final List<CustomerPriceArticles> articles = new ArrayList<CustomerPriceArticles>();
        final CustomerPriceArticles article1 = new CustomerPriceArticles();
        article1.setArticleNumber(PRODUCT_CODE_10000000);
        article1.setQuantity(PRICE_QUANTITY_1);
        articles.add(article1);

        final CustomerPriceArticles article2 = new CustomerPriceArticles();
        article2.setArticleNumber(PRODUCT_CODE_10000000);
        article2.setQuantity(PRICE_QUANTITY_10);
        articles.add(article2);

        request.getArticles().addAll(articles);
        // when(webServiceClient.if07CustomerPrice(request)).thenReturn(mockedCustomerPriceResponseSingle);
        // doReturn(mockedCustomerPriceResponseSingle).when(webServiceClient).if07CustomerPrice(request);
        // given(webServiceClient.if07CustomerPrice(request)).willReturn(mockedCustomerPriceResponseSingle);
        Mockito.when(webServiceClient.if07CustomerPrice(Mockito.any(CustomerPriceRequest.class))).thenReturn(mockedCustomerPriceResponseSingle);
        when(webServiceClient.if07CustomerPrice(any(CustomerPriceRequest.class))).thenReturn(mockedCustomerPriceResponseSingle);
        final Set<ProductPriceData>  result = customerPriceService.getPricesForPricesList(CUSTOMER_CODE_1000150, SALES_ORGANIZATION_AT, "EUR",
                PRODUCT_CODE_10000000, priceInfo);

        assertEquals("size of returned product price is wrong", 1, result.size());
        assertEquals("size of returned product price row is wrong", 2,  result.stream().filter(sapPrice -> sapPrice.getArticleNumber().equalsIgnoreCase("PRODUCT_CODE_20000000")).findAny().get().getVolumePriceData().size());

    }

    private List<PriceInformation> initPriceInfo() {

        // matchValue
        // giveAwayPrice
        // net
        // unitFactor
        // currency
        // unit
        // price
        // minqtd

        final List<PriceInformation> pricesInfo = new ArrayList<PriceInformation>();
        final Map qualifiers = new HashMap();
        final PriceRow priceRow = new PriceRow();
        priceRow.setMinqtd(1L);
        priceRow.setUnitFactor(1);
        // priceRow.setAttribute(qualifier, value);
        qualifiers.put("pricerow", priceRow);

        final PriceInformation priceInfo = new PriceInformation(qualifiers, new PriceValue("CHF", 5.0, true));
        pricesInfo.add(priceInfo);
        return pricesInfo;
    }

    // @Test
    // public void testMultipleProductPrices() throws Exception {
    //
    // final CustomerPriceResponse mockedCustomerPriceResponseMultiple = initializeCustomerPriceResponseMultiple();
    //
    // final CustomerPriceRequest request = new CustomerPriceRequest();
    //
    // request.setSalesOrganization(SALES_ORGANIZATION_AT);
    //
    // request.setCustomerId(CUSTOMER_CODE_1000150);
    // request.setCurrencyCode(CurrencyCode.EUR);
    //
    // final List<CustomerPriceArticles> articles = new ArrayList<CustomerPriceArticles>();
    // final CustomerPriceArticles article1 = new CustomerPriceArticles();
    // article1.setArticleNumber(PRODUCT_CODE_10000000);
    // article1.setQuantity(PRICE_QUANTITY_1);
    // articles.add(article1);
    //
    // final CustomerPriceArticles article2 = new CustomerPriceArticles();
    // article2.setArticleNumber(PRODUCT_CODE_20000000);
    // article2.setQuantity(PRICE_QUANTITY_1);
    // articles.add(article2);
    //
    // final CustomerPriceArticles article3 = new CustomerPriceArticles();
    // article3.setArticleNumber(PRODUCT_CODE_10000000);
    // article3.setQuantity(PRICE_QUANTITY_10);
    // articles.add(article3);
    //
    // final CustomerPriceArticles article4 = new CustomerPriceArticles();
    // article4.setArticleNumber(PRODUCT_CODE_20000000);
    // article4.setQuantity(PRICE_QUANTITY_10);
    // articles.add(article4);
    //
    // request.getArticles().addAll(articles);
    // when(webServiceClient.if07CustomerPrice(any(CustomerPriceRequest.class))).thenReturn(mockedCustomerPriceResponseMultiple);
    //
    // final Map<String, Map<Integer, Double>> result = customerPriceService.getPricesForPricesList();
    //
    // assertEquals("size of returned product prices is wrong", 2, result.size());
    // assertEquals("size of returned product price rows is wrong", 2, result.get(PRODUCT_CODE_10000000).size());
    // assertEquals("size of returned product price rows is wrong", 2, result.get(PRODUCT_CODE_20000000).size());
    //
    // }

    // private CustomerPriceResponse initializeCustomerPriceResponseMultiple() {
    //
    // final CustomerPriceResponse result = new CustomerPriceResponse();
    //
    // final CustomerPriceArticlesResponse article1 = new CustomerPriceArticlesResponse();
    // article1.setArticleNumber(PRODUCT_CODE_10000000);
    // article1.setCurrencyCode(CurrencyCode.EUR);
    // article1.setPrice(100);
    // article1.setQuantity(PRICE_QUANTITY_1);
    // result.getArticles().add(article1);
    // final CustomerPriceArticlesResponse article2 = new CustomerPriceArticlesResponse();
    // article2.setArticleNumber(PRODUCT_CODE_20000000);
    // article2.setCurrencyCode(CurrencyCode.EUR);
    // article2.setPrice(101);
    // article2.setQuantity(PRICE_QUANTITY_1);
    // result.getArticles().add(article2);
    // final CustomerPriceArticlesResponse article3 = new CustomerPriceArticlesResponse();
    // article3.setArticleNumber(PRODUCT_CODE_10000000);
    // article3.setCurrencyCode(CurrencyCode.EUR);
    // article3.setPrice(50);
    // article3.setQuantity(PRICE_QUANTITY_10);
    // result.getArticles().add(article3);
    //
    // final CustomerPriceArticlesResponse article4 = new CustomerPriceArticlesResponse();
    // article4.setArticleNumber(PRODUCT_CODE_20000000);
    // article4.setCurrencyCode(CurrencyCode.EUR);
    // article4.setPrice(51);
    // article4.setQuantity(PRICE_QUANTITY_10);
    // result.getArticles().add(article4);
    //
    // return result;
    // }

    private CustomerPriceResponse initializeCustomerPriceResponseSingle() {

        final CustomerPriceResponse result = new CustomerPriceResponse();

        final CustomerPriceArticlesResponse article1 = new CustomerPriceArticlesResponse();
        article1.setArticleNumber(PRODUCT_CODE_10000000);
        article1.setCurrencyCode(CurrencyCode.EUR);
        article1.setPriceWithoutVat(123.45);
        article1.setVatPercentage("20");
        article1.setVat(new BigDecimal(24.69));
        article1.setPriceWithVat(148.14);
        article1.setQuantity(1L);
        result.getArticles().add(article1);
        final CustomerPriceArticlesResponse article2 = new CustomerPriceArticlesResponse();
        article2.setArticleNumber(PRODUCT_CODE_10000000);
        article2.setCurrencyCode(CurrencyCode.EUR);
        article2.setPriceWithoutVat(123.45);
        article2.setVatPercentage("20");
        article2.setVat(new BigDecimal(24.69));
        article2.setPriceWithVat(148.14);
        article2.setQuantity(10L);
        result.getArticles().add(article2);

        return result;
    }

}
