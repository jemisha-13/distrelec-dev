/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.product.model.PickupStockLevelExtModel;
import com.namics.distrelec.b2b.core.service.product.model.ProductAvailabilityExtModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

/**
 * Tests the {@link SapAvailabilityService} class with the Q SAP PI system.
 * 
 * These tests need a connection to the Q SAP PI system. You need an ssh tunnel to the ATOS Q environment for that. The tests make
 * assumptions regarding the implementation of the SAP PI system, so a failing test does not necessarily indicate, that the client side
 * implementation is wrong, but it may suggest, that the client side implementation needs to be changed.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 */
@Ignore("You need an ssh tunnel to ATOS Q in order to test this locally.")
@UnitTest
public class RemoteSapAvailabilityServiceTest {

    @Mock
    private DistSalesOrgService distSalesOrgService;

    @InjectMocks
    private final SapAvailabilityService sapAvailabilityService = new SapAvailabilityService();

    @BeforeClass
    public static void initCache() {
        CacheManager.getInstance().addCacheIfAbsent(new Cache(DistConstants.CacheName.AVAILABILITY, 100000, false, false, 300, 0));
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        // init mock response for getCurrentSalesOrg
        final DistSalesOrgModel mockedDistSalesOrgModel = new DistSalesOrgModel();
        mockedDistSalesOrgModel.setCode("7310");
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(mockedDistSalesOrgModel);

        // build web service client from spring configuration
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
        final SIHybrisV1Out webServiceClient = (SIHybrisV1Out) context.getBean("core.sapPiWebserviceClientIF08");
        sapAvailabilityService.setWebServiceClient(webServiceClient);
    }

    /*
     * This test assumes that there is at least one item in stock for every requested combination of product code and warehouse in the SAP
     * PI system.
     */
    @Test
    public void testSapAvailabilityServiceWithRemoteSapSytem() {

        // init
        final List<String> productCodes = new ArrayList<String>();
        productCodes.add("20000000");
        productCodes.add("20000003");

        // action
        final List<ProductAvailabilityExtModel> availabilities = sapAvailabilityService.getAvailability(productCodes, true);

        // evaluation
        assertEquals("size of returned availabilities is wrong", 2, availabilities.size());
        ProductAvailabilityExtModel availability = availabilities.get(0);
        assertEquals("first product code is wrong", "20000000", availability.getProductCode());
        assertEquals("first detail info is wrong", Boolean.TRUE, availability.getDetailInfo());
        assertNull("first backorder quantity is not null", availability.getBackorderQuantity());
        assertNull("first backorder delivery date is not null", availability.getBackorderDeliveryDate());
        List<PickupStockLevelExtModel> pickupStockLevels = availability.getStockLevelPickup();
        assertEquals("size of returned pickup stock levels is wrong", 0, pickupStockLevels.size());

        availability = availabilities.get(1);
        assertEquals("second product code is wrong", "20000003", availability.getProductCode());
        assertEquals("second detail info is wrong", Boolean.TRUE, availability.getDetailInfo());
        assertNull("second backorder quantity is not null", availability.getBackorderQuantity());
        assertNull("second backorder delivery date is not null", availability.getBackorderDeliveryDate());
        pickupStockLevels = availability.getStockLevelPickup();
        assertEquals("size of returned pickup stock levels is wrong", 0, pickupStockLevels.size());
    }

}
