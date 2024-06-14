/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.annotation.Resource;

import com.namics.distrelec.b2b.core.util.TestCache;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.PaymentMethod;
import com.distrelec.webservice.sap.v1.ReadPaymentMethodsRequest;
import com.distrelec.webservice.sap.v1.ReadPaymentMethodsResponse;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.distrelec.webservice.sap.v1.UpdatePaymentMethodRequest;
import com.distrelec.webservice.sap.v1.UpdatePaymentMethodResponse;
import com.namics.distrelec.b2b.core.model.DistPaymentMethodModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel;
import com.namics.distrelec.b2b.core.model.payment.DistPaymentModeModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * Tests the {@link SapPaymentOptionService} class using mocks for the SAP PI system.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 */
@IntegrationTest
public class SapPaymentOptionServiceTest extends ServicelayerTransactionalTest {

    @Mock
    private SIHybrisV1Out webServiceClient;

    @Mock
    private DistSalesOrgService distSalesOrgService;

    @Mock
    private B2BCustomerService b2bCustomerService;

    @Mock
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @InjectMocks
    private final SapPaymentOptionService sapPaymentOptionService = new SapPaymentOptionService();

    @Resource
    private FlexibleSearchService flexibleSearchService;

    @Mock
    private CacheManager cacheManager;

    private Cache testCache = new TestCache();

    @Before
    public void setUp() throws Exception {
        importStream(SapPaymentOptionServiceTest.class.getResourceAsStream("/distrelecB2Bcore/test/testSapPaymentService.impex"), "UTF-8", null);
        sapPaymentOptionService.setFlexibleSearchService(flexibleSearchService);
        MockitoAnnotations.initMocks(this);
        initMockedDistSalesOrgService();
        initMockedB2BCustomerService();
        initMockedB2BUnitService();
        initMockedReadPaymentMethods();
        initMockedUpdatePaymentMethod();

        when(cacheManager.getCache(anyString()))
                .thenReturn(testCache);
    }

    @After
    public void afterTest(){
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

    private void initMockedReadPaymentMethods() throws P1FaultMessage {
        final ReadPaymentMethodsResponse mockedResponse = new ReadPaymentMethodsResponse();
        mockedResponse.setCustomerId("1000150");
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentMethodCode("WB01");
        paymentMethod.setDefault(false);
        mockedResponse.getPaymentMethods().add(paymentMethod);
        paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentMethodCode("WB02");
        paymentMethod.setDefault(true);
        mockedResponse.getPaymentMethods().add(paymentMethod);
        paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentMethodCode("WB03");
        paymentMethod.setDefault(false);
        mockedResponse.getPaymentMethods().add(paymentMethod);
        paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentMethodCode("WB04");
        paymentMethod.setDefault(false);
        mockedResponse.getPaymentMethods().add(paymentMethod);
        paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentMethodCode("WB05");
        paymentMethod.setDefault(false);
        mockedResponse.getPaymentMethods().add(paymentMethod);
        paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentMethodCode("WB06");
        paymentMethod.setDefault(false);
        mockedResponse.getPaymentMethods().add(paymentMethod);
        paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentMethodCode("WB07");
        paymentMethod.setDefault(false);
        mockedResponse.getPaymentMethods().add(paymentMethod);
        when(webServiceClient.if10ReadPaymentMethods(any(ReadPaymentMethodsRequest.class))).thenReturn(mockedResponse);
    }

    private void initMockedUpdatePaymentMethod() throws P1FaultMessage {
        when(webServiceClient.if10UpdatePaymentMethod(any(UpdatePaymentMethodRequest.class))).thenAnswer(new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocation) {
                final UpdatePaymentMethodResponse response = new UpdatePaymentMethodResponse();
                response.setSuccessful(false);
                final UpdatePaymentMethodRequest request = (UpdatePaymentMethodRequest) invocation.getArguments()[0];
                if (!"7350".equals(request.getSalesOrganization())) {
                    return response;
                }
                if (!"1000150".equals(request.getCustomerId())) {
                    return response;
                }
                if (!"WB04".equals(request.getPaymentMethodCode())) {
                    return response;
                }
                response.setSuccessful(true);
                return response;
            }
        });
    }

    @Test
    public void testGetSupportedPaymentOptionsForUser() {
        // init
        final B2BCustomerModel b2bCustomer = initB2BCustomer();

        // action
        final List<AbstractDistPaymentModeModel> paymentModes = sapPaymentOptionService.getSupportedPaymentOptionsForUser(b2bCustomer);

        // evaluation
        assertNotNull("paymentModes is null", paymentModes);
        assertEquals("wrong number of paymentModes", 7, paymentModes.size());
        AbstractDistPaymentModeModel paymentMode = paymentModes.get(0);
        assertEquals("first code wrong", "CreditCard", paymentMode.getCode());
        paymentMode = paymentModes.get(1);
        assertEquals("first code wrong", "GiroPay", paymentMode.getCode());
        paymentMode = paymentModes.get(2);
        assertEquals("first code wrong", "PayPal", paymentMode.getCode());
        paymentMode = paymentModes.get(3);
        assertEquals("first code wrong", "ClickAndBuy", paymentMode.getCode());
        paymentMode = paymentModes.get(4);
        assertEquals("first code wrong", "DirectDebit", paymentMode.getCode());
        paymentMode = paymentModes.get(5);
        assertEquals("first code wrong", "KlarnaFaktura", paymentMode.getCode());
        paymentMode = paymentModes.get(6);
        assertEquals("first code wrong", "BankTransfer", paymentMode.getCode());
    }

    @Test
    public void testGetDefaultPaymentOptionForUser() {
        // init
        final B2BCustomerModel b2bCustomer = initB2BCustomer();

        // action
        final DistPaymentModeModel defaultPaymentMode = sapPaymentOptionService.getDefaultPaymentOptionForUser(b2bCustomer);

        // evaluation
        assertNotNull("defaultPaymentMode is null", defaultPaymentMode);
        assertEquals("defaultPaymentMode must be 'GiroPay'", "GiroPay", defaultPaymentMode.getCode());
    }

    @Test
    public void testUpdateDefaultPaymentOption() {
        // init
        final DistPaymentModeModel paymentMode = new DistPaymentModeModel();
        final DistPaymentMethodModel paymentMethod = new DistPaymentMethodModel();
        paymentMethod.setCode("WB04");
        paymentMode.setErpPaymentMethod(paymentMethod);

        // action
        // final boolean success = sapPaymentOptionService.updateDefaultPaymentOption(paymentMode);

        // evaluation
        // assertTrue("update was not successful", success);
    }

    private B2BCustomerModel initB2BCustomer() {
        final B2BUnitModel b2bUnit = new B2BUnitModel();
        b2bUnit.setErpCustomerID("1000150");
        final B2BCustomerModel b2bCustomer = new B2BCustomerModel();
        b2bCustomer.setDefaultB2BUnit(b2bUnit);
        return b2bCustomer;
    }
}
