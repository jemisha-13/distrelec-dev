package com.namics.distrelec.b2b.facades.order.converters.populator;

import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.namics.distrelec.b2b.core.model.DistB2BBudgetModel;
import com.namics.distrelec.b2b.core.model.order.DistErpVoucherInfoModel;
import com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel;
import com.namics.distrelec.b2b.facades.customer.b2b.budget.DistB2BBudgetFacade;
import com.namics.distrelec.b2b.facades.order.data.DistDiscountData;
import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.order.warehouse.data.WarehouseData;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCommentData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionResultData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BBudgetData;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.basesite.data.BaseSiteData;
import de.hybris.platform.commercefacades.basestore.data.BaseStoreData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistOrderPopulatorUnitTest {

    @Mock
    private Converter<WarehouseModel, WarehouseData> warehouseConverter;

    @Mock
    private Converter<AddressModel, AddressData> addressConverter;

    @Mock
    private Converter<PaymentModeModel, DistPaymentModeData> paymentModeConverter;

    @Mock
    private Converter<PaymentInfoModel, CCPaymentInfoData> paymentConverter;

    @Mock
    private Converter<DiscountModel, DistDiscountData> discountConverter;

    @Mock
    private Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter;

    @Mock
    private Converter<BaseStoreModel, BaseStoreData> baseStoreConverter;

    @Mock
    private Converter<BaseSiteModel, BaseSiteData> baseSiteConverter;

    @Mock
    private Converter<AbstractOrderModel, DistErpVoucherInfoData> distErpVoucherInfoDataConverter;

    @Mock
    private Converter<CurrencyModel, CurrencyData> currencyConverter;

    @Mock
    private EnumerationService enumerationService;

    @Mock
    private DistB2BBudgetFacade distB2BBudgetFacade;

    @Mock
    private PriceDataFactory priceDataFactory;

    @Mock
    private Converter<CheckoutPaymentType, B2BPaymentTypeData> b2bPaymentTypeConverter;

    @Mock
    private Converter<B2BPermissionResultModel, B2BPermissionResultData> b2BPermissionResultConverter;

    @Mock
    private Converter<UserModel, CustomerData> b2bCustomerConverter;

    @Mock
    private Converter<B2BCommentModel, B2BCommentData> b2BCommentConverter;

    @InjectMocks
    private DistOrderPopulator distOrderPopulator;

    @Test
    public void testPopulateOrderDataWithErpOrderCode() {
        // given
        OrderModel orderModel = mock(OrderModel.class);
        OrderData b2BOrderData = mock(OrderData.class);
        CustomerData customerData = mock(CustomerData.class);
        CurrencyModel currencyModel = mock(CurrencyModel.class);
        WarehouseModel warehouseModel = mock(WarehouseModel.class);
        DeliveryModeModel deliverModeModel = mock(DeliveryModeModel.class);
        BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
        BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
        SalesApplication salesApplication = mock(SalesApplication.class);
        AbstractDistPaymentModeModel abstractDistPaymentModeModel = mock(AbstractDistPaymentModeModel.class);
        DistErpVoucherInfoModel distErpVoucherInfoModel = mock(DistErpVoucherInfoModel.class);

        // when
        when(b2BOrderData.getB2bCustomerData()).thenReturn(customerData);
        when(customerData.getCustomerType()).thenReturn(CustomerType.B2B);
        when(orderModel.getCurrency()).thenReturn(currencyModel);
        when(orderModel.getErpOrderCode()).thenReturn("ERP123");
        when(orderModel.getPickupLocation()).thenReturn(warehouseModel);
        when(orderModel.getDeliveryMode()).thenReturn(deliverModeModel);
        when(orderModel.getStore()).thenReturn(baseStoreModel);
        when(orderModel.getSite()).thenReturn(baseSiteModel);
        when(orderModel.getSalesApplication()).thenReturn(salesApplication);
        when(orderModel.getPaymentMode()).thenReturn(abstractDistPaymentModeModel);
        when(orderModel.getErpVoucherInfo()).thenReturn(distErpVoucherInfoModel);

        distOrderPopulator.populate(orderModel, b2BOrderData);

        // then
        verify(b2BOrderData).setErpOrderCode("ERP123");
    }

    @Test
    public void testPopulateOrderDataWithDiscounts() {
        // given
        OrderModel orderModel = mock(OrderModel.class);
        OrderData orderData = mock(OrderData.class);
        B2BCustomerModel customerModel = mock(B2BCustomerModel.class);
        DistB2BBudgetModel distB2BBudgetModel = mock(DistB2BBudgetModel.class);
        B2BBudgetData b2bBudgetData = mock(B2BBudgetData.class);
        CustomerData customerData = mock(CustomerData.class);
        PriceData priceData = mock(PriceData.class);
        CurrencyModel currencyModel = mock(CurrencyModel.class);
        DiscountModel discountModel = mock(DiscountModel.class);
        DistDiscountData distDiscountData = mock(DistDiscountData.class);

        // when
        when(orderData.getB2bCustomerData()).thenReturn(customerData);
        when(customerData.getCustomerType()).thenReturn(CustomerType.B2B);
        when(orderData.getTotalPrice()).thenReturn(priceData);
        when(customerData.getBudget()).thenReturn(b2bBudgetData);
        when(orderModel.getUser()).thenReturn(customerModel);
        when(customerModel.getBudget()).thenReturn(distB2BBudgetModel);
        when(orderModel.getCurrency()).thenReturn(currencyModel);
        when(orderModel.getDiscounts()).thenReturn(Collections.singletonList(discountModel));
        when(discountConverter.convert(discountModel)).thenReturn(distDiscountData);

        distOrderPopulator.populate(orderModel, orderData);

        // then
        verify(orderData).getDiscounts();
    }
}
