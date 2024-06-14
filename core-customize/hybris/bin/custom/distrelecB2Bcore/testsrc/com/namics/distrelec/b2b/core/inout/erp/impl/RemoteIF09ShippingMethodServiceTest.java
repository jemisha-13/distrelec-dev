/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.ReadShippingMethodsRequest;
import com.distrelec.webservice.sap.v1.ReadShippingMethodsResponse;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.distrelec.webservice.sap.v1.ShippingMethodCode;
import com.distrelec.webservice.sap.v1.ShippingMethods;
import com.distrelec.webservice.sap.v1.UpdateDefaultShippingMethodRequest;
import com.distrelec.webservice.sap.v1.UpdateDefaultShippingMethodResponse;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Tests the methods if09ReadShippingMethods and if09UpdateDefaultShippingMethod of the SAP PI system.
 * 
 * These tests need a connection to the Q SAP PI system. You need an ssh tunnel to the ATOS Q environment for that. The tests check the SAP
 * PI implementation only, no shop functionality is tested here.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 */
@Ignore("You need an ssh tunnel to ATOS Q in order to test this locally.")
@UnitTest
public class RemoteIF09ShippingMethodServiceTest {

    private static SIHybrisV1Out webServiceClient;

    private static final String CUSTOMER_ID = "1000150";

    private static final String SALES_ORG = "7350";

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
     * This test corresponds to the example request in the SOAP UI project: read shipping methods for customer 1000150 at sales org 7350.
     */
    @Test
    public void testReadShippingMethods() throws Exception {
        // init
        final ReadShippingMethodsRequest readRequest = new ReadShippingMethodsRequest();
        readRequest.setCustomerId(CUSTOMER_ID);
        readRequest.setSalesOrganization(SALES_ORG);

        // action
        final ReadShippingMethodsResponse readResponse = webServiceClient.if09ReadShippingMethods(readRequest);

        // evaluation
        assertEquals("response contains wrong customerId", CUSTOMER_ID, readResponse.getCustomerId());
        final List<ShippingMethods> shippingMethods = readResponse.getShippingMethods();
        assertEquals("returned shipping method count is wrong", 2, shippingMethods.size());
        ShippingMethods shippingMethod = shippingMethods.get(0);
        assertEquals("foo first shipping method code is wrong", ShippingMethodCode.E_1, shippingMethod.getShippingMethodCode());
        shippingMethod = shippingMethods.get(1);
        assertEquals("foo first shipping method code is wrong", ShippingMethodCode.E_2, shippingMethod.getShippingMethodCode());
    }

    /*
     * set default shipping method to E1, read shipping methods and check, set default shipping method to E2, read and check again
     */
    @Test
    public void testUpdateDefaultShippingMethodRequest() throws Exception {
        // set default to E1
        ShippingMethodCode shippingMethodCode = ShippingMethodCode.E_1;
        setDefaultShippingMethod(CUSTOMER_ID, SALES_ORG, shippingMethodCode);

        // read and check
        checkDefaultShippingCode(CUSTOMER_ID, SALES_ORG, shippingMethodCode);

        // set default to E2
        shippingMethodCode = ShippingMethodCode.E_2;
        setDefaultShippingMethod(CUSTOMER_ID, SALES_ORG, shippingMethodCode);

        // read and check
        checkDefaultShippingCode(CUSTOMER_ID, SALES_ORG, shippingMethodCode);
    }

    private void setDefaultShippingMethod(final String customerId, final String salesOrg, final ShippingMethodCode defaultShippingMethodCode)
            throws P1FaultMessage {
        final UpdateDefaultShippingMethodRequest updateRequest = new UpdateDefaultShippingMethodRequest();
        updateRequest.setCustomerId(customerId);
        updateRequest.setSalesOrganization(salesOrg);
        updateRequest.setShippingMethodCode(defaultShippingMethodCode);
        final UpdateDefaultShippingMethodResponse updateResponse = webServiceClient.if09UpdateDefaultShippingMethod(updateRequest);
        assertTrue("setting default shipping method failed", updateResponse.isSuccessful());
    }

    private void checkDefaultShippingCode(final String customerId, final String salesOrg, final ShippingMethodCode defaultShippingMethodCode)
            throws P1FaultMessage {
        final ReadShippingMethodsRequest readRequest = new ReadShippingMethodsRequest();
        readRequest.setCustomerId(customerId);
        readRequest.setSalesOrganization(salesOrg);
        final ReadShippingMethodsResponse readResponse = webServiceClient.if09ReadShippingMethods(readRequest);
        for (ShippingMethods shippingMethod : readResponse.getShippingMethods()) {
            if (defaultShippingMethodCode.equals(shippingMethod.getShippingMethodCode())) {
                assertTrue("code " + defaultShippingMethodCode.value() + " is not default", shippingMethod.isDefault());
            } else {
                assertFalse("code " + defaultShippingMethodCode.value() + " is default", shippingMethod.isDefault());
            }
        }
    }

}
