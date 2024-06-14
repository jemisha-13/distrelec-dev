package com.namics.distrelec.b2b.core.inout.erp.converters.response;

import com.distrelec.webservice.if11.v3.DiscountResponse;
import com.distrelec.webservice.if11.v3.OrderCalculationResponse;
import com.distrelec.webservice.if11.v3.OrderEntryResponse;
import com.distrelec.webservice.if11.v3.VoucherResponse;
import com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService;
import com.namics.distrelec.b2b.core.inout.erp.ShippingOptionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.order.PaymentModeService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@UnitTest
public class OrderCalculationResponsePopulatorTest {

    @Mock
    private ModelService modelService;
    @Mock
    private PaymentModeService paymentModeService;
    @Mock
    private DeliveryModeService deliveryModeService;
    @Mock
    private PaymentOptionService paymentOptionService;
    @Mock
    private ShippingOptionService shippingOptionService;
    @Mock
    private OrderCalculationResponseCommonMapping orderCalculationCommonMapping;
    @Mock
    private CMSSiteService cmsSiteService;
    @Mock
    private SessionService sessionService;
    @Mock
    private ConfigurationService configurationService;
    @InjectMocks
    private OrderCalculationResponsePopulator populator;

    @Before
    public void setUp() throws CalculationException {
        MockitoAnnotations.initMocks(this);

        doNothing().when(modelService).save(any(AbstractOrderModel.class));
        doNothing().when(orderCalculationCommonMapping).fillOrderEntries(anyListOf(OrderEntryResponse.class), any(AbstractOrderModel.class));
        doNothing().when(orderCalculationCommonMapping).fillVoucherInfo(anyListOf(VoucherResponse.class), any(AbstractOrderModel.class));
        when(orderCalculationCommonMapping.getTotalDiscountsValue(anyListOf(DiscountResponse.class))).thenReturn(0D);

        doNothing().when(modelService).refresh(any(AbstractOrderModel.class));
        doNothing().when(modelService).saveAll(any(AbstractOrderEntryModel.class));
    }

    @Test
    public void testMinimalPathWorks(){
        final OrderCalculationResponse source = createBaseOrderCalculationResponse();
        final AbstractOrderModel target = new OrderModel();
        populator.populate(source, target);

        final Double zero = 0D;

        assertEquals(source.getOrderId(), target.getErpOrderCode());
        assertEquals(zero, target.getNetPaymentCost());
        assertEquals(zero, target.getNetDeliveryCost());
        assertEquals(zero, target.getDeliveryCost());

        assertEquals(zero, target.getNetSubTotal());
        assertEquals(zero, target.getTotalTax());
        assertEquals(zero, target.getTotalPrice());

        assertNotNull(target.getValidDeliveryModes());
        assertEquals(0, target.getValidDeliveryModes().size());
    }

    private OrderCalculationResponse createBaseOrderCalculationResponse(){
        final OrderCalculationResponse response = new OrderCalculationResponse();
        // Skip the vouchers
        response.setFreeVoucherPromotion(null);

        final String orderId = "0011223344";
        final double zeroAmount = 0D;

        response.setOrderId(orderId);
        response.setSubtotal2(zeroAmount);
        response.setTax(zeroAmount);
        response.setTotal(zeroAmount);
        return response;
    }
}
