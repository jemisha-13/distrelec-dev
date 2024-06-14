/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.distrelec.webservice.sap.v1.*;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistShippingMethodModel;
import com.namics.distrelec.b2b.core.model.delivery.AbstractDistDeliveryModeModel;
import com.namics.distrelec.b2b.core.model.delivery.DistDeliveryModeModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.util.TestCache;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * Tests the {@link SapShippingOptionService} class using mocks for the SAP PI system.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 */
// @IntegrationTest
// todo refactor not to mix unit test mccking and integrations, use AbstractSapWebServiceTest to mock SIHybrisV1Out
public class SapShippingOptionServiceTest extends ServicelayerTransactionalTest {

    @Mock
    private SIHybrisV1Out webServiceClient;

    @Mock
    private DistSalesOrgService distSalesOrgService;

    @Mock
    private B2BCustomerService b2bCustomerService;

    @Mock
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @InjectMocks
    private final SapShippingOptionService sapShippingOptionService = new SapShippingOptionService();

    @Resource
    private FlexibleSearchService flexibleSearchService;

    @Mock
    private CacheManager cacheManager;

    private Cache testCache = new TestCache();

    @Before
    public void setUp() throws Exception {
        importStream(SapShippingOptionServiceTest.class.getResourceAsStream("/distrelecB2Bcore/test/testSapShippingService.impex"), "UTF-8", null);
        MockitoAnnotations.initMocks(this);
        initMockedDistSalesOrgService();
        initMockedB2BCustomerService();
        initMockedB2BUnitService();
        initMockedReadShippingMethods();
        initMockedUpdateDefaultShippingMethod();

        when(cacheManager.getCache(anyString()))
                                                .thenReturn(testCache);
    }

    @After
    public void afterTest() {
        testCache.clear();
    }

    private void initMockedDistSalesOrgService() {
        final DistSalesOrgModel mockedDistSalesOrgModel = new DistSalesOrgModel();
        mockedDistSalesOrgModel.setCode("7350");
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(mockedDistSalesOrgModel);
    }

    private void initMockedB2BCustomerService() {
        final B2BCustomerModel mockB2BCustomer = new B2BCustomerModel();
        mockB2BCustomer.setName("John Doe");
        when(b2bCustomerService.getCurrentB2BCustomer()).thenReturn(mockB2BCustomer);
    }

    private void initMockedB2BUnitService() {
        when(b2bUnitService.getParent(any(B2BCustomerModel.class))).thenAnswer(new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocation) {
                final B2BCustomerModel b2bCustomer = (B2BCustomerModel) invocation.getArguments()[0];
                if ("John Doe".equals(b2bCustomer.getName())) {
                    final B2BUnitModel b2bUnit = new B2BUnitModel();
                    b2bUnit.setName("ACME");
                    b2bUnit.setErpCustomerID("1000150");
                    return b2bUnit;
                } else {
                    return null;
                }
            }
        });
    }

    private void initMockedReadShippingMethods() throws P1FaultMessage {
        final ReadShippingMethodsResponse mockedResponse = new ReadShippingMethodsResponse();
        mockedResponse.setCustomerId("1000150");
        ShippingMethods shippingMethod = new ShippingMethods();
        shippingMethod.setShippingMethodCode(ShippingMethodCode.A_1); // PickUp
        shippingMethod.setDefault(false);
        mockedResponse.getShippingMethods().add(shippingMethod);
        shippingMethod = new ShippingMethods();
        shippingMethod.setShippingMethodCode(ShippingMethodCode.E_1); // Express
        shippingMethod.setDefault(true);
        mockedResponse.getShippingMethods().add(shippingMethod);
        shippingMethod = new ShippingMethods();
        shippingMethod.setShippingMethodCode(ShippingMethodCode.E_2); // BulkExpress
        shippingMethod.setDefault(false);
        mockedResponse.getShippingMethods().add(shippingMethod);
        shippingMethod = new ShippingMethods();
        shippingMethod.setShippingMethodCode(ShippingMethodCode.N_1); // MailOrder
        shippingMethod.setDefault(false);
        mockedResponse.getShippingMethods().add(shippingMethod);
        shippingMethod = new ShippingMethods();
        shippingMethod.setShippingMethodCode(ShippingMethodCode.N_2); // BulkMailOrder
        shippingMethod.setDefault(false);
        mockedResponse.getShippingMethods().add(shippingMethod);
        shippingMethod = new ShippingMethods();
        shippingMethod.setShippingMethodCode(ShippingMethodCode.X_1); // Emergency
        shippingMethod.setDefault(false);
        mockedResponse.getShippingMethods().add(shippingMethod);
        when(webServiceClient.if09ReadShippingMethods(any(ReadShippingMethodsRequest.class))).thenReturn(mockedResponse);
    }

    private void initMockedUpdateDefaultShippingMethod() throws P1FaultMessage {
        when(webServiceClient.if09UpdateDefaultShippingMethod(any(UpdateDefaultShippingMethodRequest.class))).thenAnswer(new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocation) {
                final UpdateDefaultShippingMethodResponse response = new UpdateDefaultShippingMethodResponse();
                response.setSuccessful(false);
                final UpdateDefaultShippingMethodRequest request = (UpdateDefaultShippingMethodRequest) invocation.getArguments()[0];
                if (!"7350".equals(request.getSalesOrganization())) {
                    return response;
                }
                if (!"1000150".equals(request.getCustomerId())) {
                    return response;
                }
                if (!ShippingMethodCode.X_1.equals(request.getShippingMethodCode())) {
                    return response;
                }
                response.setSuccessful(true);
                return response;
            }
        });
    }

    @Test
    public void testGetSupportedShippingOptionsForUser() throws Exception {
        // init
        final B2BCustomerModel b2bCustomer = initB2BCustomer();
        final ArrayList<String> ignoredOptions = new ArrayList<>();

        // action
        final List<AbstractDistDeliveryModeModel> deliveryModes = sapShippingOptionService.getSupportedShippingOptionsForUser(b2bCustomer, ignoredOptions);

        // evaluation
        assertNotNull("deliveryModes is null", deliveryModes);
        assertEquals("wrong number of delivery modes", 6, deliveryModes.size());

        assertTrue(listContainsDeliveryMode(deliveryModes, "PickUp"));
        assertTrue(listContainsDeliveryMode(deliveryModes, "Express"));
        assertTrue(listContainsDeliveryMode(deliveryModes, "BulkExpress"));
        assertTrue(listContainsDeliveryMode(deliveryModes, "MailOrder"));
        assertTrue(listContainsDeliveryMode(deliveryModes, "BulkMailOrder"));
        assertTrue(listContainsDeliveryMode(deliveryModes, "Emergency"));
    }

    @Test
    public void testIgnoringOptions() throws Exception {
        // init
        final B2BCustomerModel b2bCustomer = initB2BCustomer();
        final ArrayList<String> ignoredOptions = new ArrayList<>();
        ignoredOptions.add("PickUp");
        ignoredOptions.add("BulkExpress");
        ignoredOptions.add("BulkMailOrder");

        // action
        final List<AbstractDistDeliveryModeModel> deliveryModes = sapShippingOptionService.getSupportedShippingOptionsForUser(b2bCustomer, ignoredOptions);

        // evaluation
        assertNotNull("deliveryModes is null", deliveryModes);
        assertEquals("wrong number of delivery modes", 3, deliveryModes.size());

        assertFalse(listContainsDeliveryMode(deliveryModes, "PickUp"));
        assertTrue(listContainsDeliveryMode(deliveryModes, "Express"));
        assertFalse(listContainsDeliveryMode(deliveryModes, "BulkExpress"));
        assertTrue(listContainsDeliveryMode(deliveryModes, "MailOrder"));
        assertFalse(listContainsDeliveryMode(deliveryModes, "BulkMailOrder"));
        assertTrue(listContainsDeliveryMode(deliveryModes, "Emergency"));
    }

    @Test
    public void testGetDefaultShippingOptionForUser() throws Exception {
        // init
        final B2BCustomerModel b2bCustomer = initB2BCustomer();

        // action
        final DistDeliveryModeModel defaultDeliveryMode = sapShippingOptionService.getDefaultShippingOptionForUser(b2bCustomer);

        // evaluation
        assertNotNull("defaultDeliveryMode is null", defaultDeliveryMode);
        assertEquals("defaultDeliveryMode must be 'Express'", "Express", defaultDeliveryMode.getCode());
    }

    @Test
    public void testUpdateDefaultShippingOption() throws Exception {
        // init
        final DistDeliveryModeModel deliveryMode = new DistDeliveryModeModel();
        final DistShippingMethodModel shippingMethod = new DistShippingMethodModel();
        shippingMethod.setCode("X1");
        deliveryMode.setErpDeliveryMethod(shippingMethod);

        // action
        final boolean success = sapShippingOptionService.updateDefaultShippingOption(deliveryMode);

        // evaluation
        assertTrue("update was not successful", success);
    }

    private B2BCustomerModel initB2BCustomer() {
        final B2BUnitModel b2bUnit = new B2BUnitModel();
        b2bUnit.setErpCustomerID("1000150");
        final B2BCustomerModel b2bCustomer = new B2BCustomerModel();
        b2bCustomer.setDefaultB2BUnit(b2bUnit);
        return b2bCustomer;
    }

    private boolean listContainsDeliveryMode(List<AbstractDistDeliveryModeModel> deliveryModes, String deliveryModeCode) {
        return deliveryModes.stream()
                            .filter(deliveryMode -> deliveryMode.getCode().equals(deliveryModeCode))
                            .findFirst()
                            .isPresent();
    }
}
