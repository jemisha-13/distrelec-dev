package com.namics.distrelec.b2b.core.mock.sap;

import com.distrelec.webservice.if11.v3.SIHybrisIF11V1Out;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.namics.distrelec.b2b.core.inout.erp.impl.*;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import org.junit.After;
import org.junit.Before;

import javax.annotation.Resource;

public abstract class AbstractSapWebServiceTest extends ServicelayerTransactionalTest {

    @Resource(name = "sap.customerPriceService")
    private SapCustomerPriceService sapCustomerPriceService;

    @Resource(mappedName = "sap.customerService")
    private SapCustomerService sapCustomerService;

    @Resource(name = "sap.orderCalculationService")
    private SapOrderCalculationService sapOrderCalculationService;

    @Resource(mappedName = "sap.paymentOptionService")
    private SapPaymentOptionService sapPaymentOptionService;

    @Resource(mappedName = "sap.shippingOptionService")
    private SapShippingOptionService sapShippingOptionService;

    /**
     * Cached SIHybrisV1Out because it will be replaced with mocked one in tests.
     */
    private SIHybrisV1Out cachedWebServiceClient;

    @Before
    public final void before() {
        // clear caches
        sapCustomerPriceService.clearCache();
        sapOrderCalculationService.clearCache();
        sapPaymentOptionService.clearCache();
        sapShippingOptionService.clearCache();

        cachedWebServiceClient = sapCustomerPriceService.getWebServiceClient();
    }

    @After
    public final void after() {
        setWebServiceClient(cachedWebServiceClient);
    }

    protected final void setWebServiceClient(SIHybrisV1Out webServiceClient) {
        sapCustomerPriceService.setWebServiceClient(webServiceClient);
        sapCustomerService.setWebServiceClient(webServiceClient);
        sapOrderCalculationService.setWebServiceClient(webServiceClient);
        sapPaymentOptionService.setWebServiceClient(webServiceClient);
        sapShippingOptionService.setWebServiceClient(webServiceClient);
    }

    protected final void setWebServiceClientIF11(SIHybrisIF11V1Out webServiceClientIF11) {
        sapOrderCalculationService.setWebServiceClientIF11(webServiceClientIF11);
    }

    protected final SapCustomerPriceService getSapCustomerPriceService() {
        return sapCustomerPriceService;
    }

    protected final SapCustomerService getSapCustomerService() {
        return sapCustomerService;
    }

    protected final SapPaymentOptionService getSapPaymentOptionService() {
        return sapPaymentOptionService;
    }

    protected final SapShippingOptionService getSapShippingOptionService() {
        return sapShippingOptionService;
    }
}
