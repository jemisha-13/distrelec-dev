/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import static junit.framework.Assert.assertEquals;

import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.distrelec.webservice.sap.v1.AvailabilityRequest;
import com.distrelec.webservice.sap.v1.AvailabilityResponse;
import com.distrelec.webservice.sap.v1.ErpArticleAvailability;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.distrelec.webservice.sap.v1.StockLevels;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Tests the methods if06Availability of the SAP PI system.
 * 
 * These tests need a connection to the Q SAP PI system. You need an ssh tunnel to the ATOS Q environment for that. The tests check the
 * behavior of the SAP PI system, so a failing test does not necessarily indicate, that the client side implementation is wrong, but it may
 * suggest, that the client side implementation needs to be changed.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 */
@Ignore("You need an ssh tunnel to ATOS Q in order to test this locally.")
@UnitTest
public class RemoteIF06AvailabilityTest {

    private static SIHybrisV1Out webServiceClient;

    @BeforeClass
    public static void setUp() {
        final PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        final Properties properties = new Properties();
        properties.setProperty("core.sapPi.webservice.address", "http://localhost:50000");
        properties.setProperty("core.sapPi.webservice.senderService", "WSD");
        properties.setProperty("core.sapPi.webservice.enableSchemaValidation", "true");
        configurer.setProperties(properties);
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();
        context.addBeanFactoryPostProcessor(configurer);
        context.setConfigLocation("../resources/distrelecB2Bcore-SAP-PI-webservice-spring.xml");
        context.refresh();
        webServiceClient = (SIHybrisV1Out) context.getBean("core.sapPiWebserviceClientIF08");
    }

    /*
     * Test implementation of service on SAP PI: For a request with two article nunmbers and two warehouse ids we expect to receive a
     * response having two erpArticleAvailability elements each containing two stockLevels elements. This test does not use shop
     * functionality.
     */
    @Test
    public void testSapImplementationResultSize() throws Exception {
        // init
        final AvailabilityRequest availabilityRequest = new AvailabilityRequest();
        availabilityRequest.setSalesOrganization("7350");
        availabilityRequest.getArticleNumbers().add("20000000"); // existing
        availabilityRequest.getArticleNumbers().add("20000003"); // existing

        // action
        final AvailabilityResponse availabilityResponse = webServiceClient.if06Availability(availabilityRequest);

        // evaluation
        final List<ErpArticleAvailability> articleAvailabilities = availabilityResponse.getErpArticleAvailability();
        assertEquals("size of returned availability response is wrong", 2, articleAvailabilities.size());

        ErpArticleAvailability articleAvailability = articleAvailabilities.get(0);
        assertEquals("first article number is wrong", "20000000", articleAvailability.getArticleNumber());
        assertEquals("first article found is wrong", true, articleAvailability.isArticleFound());
        List<StockLevels> stockLevels = articleAvailability.getStockLevels();
        assertEquals("first stock levels size is wrong", 2, stockLevels.size());
        StockLevels stockLevel = stockLevels.get(0);
        assertEquals("first warehouse id of first article is wrong", "7371", stockLevel.getWarehouseId());
        assertEquals("article found in first warehouse id of first article is wrong", true, stockLevel.isArticleFoundInWarehouse());
        stockLevel = stockLevels.get(1);
        assertEquals("second warehouse id of first article is wrong", "7372", stockLevel.getWarehouseId());
        assertEquals("article found in second warehouse id of first article is wrong", true, stockLevel.isArticleFoundInWarehouse());

        articleAvailability = articleAvailabilities.get(1);
        assertEquals("second article number is wrong", "20000003", articleAvailability.getArticleNumber());
        assertEquals("second article found is wrong", true, articleAvailability.isArticleFound());
        stockLevels = articleAvailability.getStockLevels();
        assertEquals("second stock levels size is wrong", 2, stockLevels.size());
        stockLevel = stockLevels.get(0);
        assertEquals("first warehouse id of second article is wrong", "7371", stockLevel.getWarehouseId());
        assertEquals("article found in first warehouse id of second article is wrong", true, stockLevel.isArticleFoundInWarehouse());
        stockLevel = stockLevels.get(1);
        assertEquals("second warehouse id of second article is wrong", "7372", stockLevel.getWarehouseId());
        assertEquals("article found in second warehouse id of second article is wrong", true, stockLevel.isArticleFoundInWarehouse());
    }

    /*
     * This test checks the current implementation of article not found situations. This test does not use shop functionality.
     */
    @Test
    public void testArticleNotFound() throws Exception {
        // init
        final AvailabilityRequest availabilityRequest = new AvailabilityRequest();
        availabilityRequest.setSalesOrganization("7350");
        availabilityRequest.getArticleNumbers().add("20000001"); // not existing

        // action
        final AvailabilityResponse availabilityResponse = webServiceClient.if06Availability(availabilityRequest);

        // evaluation
        final List<ErpArticleAvailability> articleAvailabilities = availabilityResponse.getErpArticleAvailability();
        assertEquals("size of returned availability response is wrong", 1, articleAvailabilities.size());

        final ErpArticleAvailability articleAvailability = articleAvailabilities.get(0);
        assertEquals("first article number is wrong", "20000001", articleAvailability.getArticleNumber());
        assertEquals("first article found is wrong", false, articleAvailability.isArticleFound());
        final List<StockLevels> stockLevels = articleAvailability.getStockLevels();
        assertEquals("first stock levels size is wrong", 0, stockLevels.size());
    }

    /*
     * This test checks the current implementation of article not found in warehouse situations. This test does not use shop functionality.
     */
    @Test
    public void testArticleNotFoundInWarehouse() throws Exception {
        // init
        final AvailabilityRequest availabilityRequest = new AvailabilityRequest();
        availabilityRequest.setSalesOrganization("7350");
        availabilityRequest.getArticleNumbers().add("20000003"); // existing

        // action
        final AvailabilityResponse availabilityResponse = webServiceClient.if06Availability(availabilityRequest);

        // evaluation
        final List<ErpArticleAvailability> articleAvailabilities = availabilityResponse.getErpArticleAvailability();
        assertEquals("size of returned availability response is wrong", 1, articleAvailabilities.size());

        final ErpArticleAvailability articleAvailability = articleAvailabilities.get(0);
        assertEquals("first article number is wrong", "20000003", articleAvailability.getArticleNumber());
        assertEquals("first article found is wrong", true, articleAvailability.isArticleFound());
        final List<StockLevels> stockLevels = articleAvailability.getStockLevels();
        assertEquals("first stock levels size is wrong", 1, stockLevels.size());
        final StockLevels stockLevel = stockLevels.get(0);
        assertEquals("first article found in warehouse is wrong", false, stockLevel.isArticleFoundInWarehouse());
    }

}
