package com.namics.distrelec.b2b.facades.order.converters;

import com.distrelec.webservice.if15.v1.AddressWithId;
import com.distrelec.webservice.if15.v1.CurrencyCode;
import com.distrelec.webservice.if15.v1.Deliveries;
import com.distrelec.webservice.if15.v1.Delivery;
import com.distrelec.webservice.if15.v1.HandlingUnit;
import com.distrelec.webservice.if15.v1.Item;
import com.distrelec.webservice.if15.v1.OrderDiscounts;
import com.distrelec.webservice.if15.v1.OrderReadEntry;
import com.distrelec.webservice.if15.v1.ReadOrderResponseV2;
import com.distrelec.webservice.if15.v1.ShippingMethodCode;
import com.distrelec.webservice.if15.v1.VoucherResponse;
import com.distrelec.webservice.if19.v1.ItemList;
import com.distrelec.webservice.if19.v1.RMAGetOrderItemsResponse;
import com.distrelec.webservice.if19.v1.ReturnReasons;
import com.distrelec.webservice.if19.v1.ReturnReasonsList;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService;
import com.namics.distrelec.b2b.core.inout.erp.ShippingOptionService;
import com.namics.distrelec.b2b.core.model.DistB2BBudgetModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.DistOrderChannelModel;
import com.namics.distrelec.b2b.core.model.delivery.DistDeliveryModeModel;
import com.namics.distrelec.b2b.core.model.payment.DistPaymentModeModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.search.converter.ReadOrderResponseWraper;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.DistRMAEntryData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.namics.distrelec.b2b.facades.order.converters.SapReadOrderResponseOrderHistoryConverter.PRODUCT_OPTIONS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapReadOrderResponseOrderHistoryConverterUnitTest {

    @Mock
    private DistrelecCodelistService codeListService;

    @Mock
    private PriceDataFactory priceDataFactory;

    @Mock
    private Converter<UserModel, CustomerData> b2bCustomerConverter;

    @Mock
    private ShippingOptionService shippingOptionService;

    @Mock
    private Converter<DistDeliveryModeModel, DeliveryModeData> defaultDistDeliveryModeConverter;

    @Mock
    private Converter<VoucherResponse, DistErpVoucherInfoData> distVoucherToErpVoucherInfoConverter;

    @Mock
    private Converter<ItemList, DistRMAEntryData> defaultDistRMADataConverter;

    @Mock
    private ProductService defaultDistProductService;

    @Mock
    private DistrelecProductFacade productFacade;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private Converter<DistPaymentModeModel, DistPaymentModeData> defaultDistPaymentModeConverter;

    @Mock
    private PaymentOptionService paymentOptionService;

    @InjectMocks
    private SapReadOrderResponseOrderHistoryConverter converter;

    @Test
    public void testPopulateWithCustomerData() {
        // given
        ReadOrderResponseWraper source = createMockReadOrderResponseWrapper();
        OrderData target = new OrderData();
        B2BCustomerModel customerModel = source.getB2BCustomerModel();
        CustomerData customerData = mock(CustomerData.class);
        CustomerType customerType = mock(CustomerType.class);
        DistOrderChannelModel orderChannel = mock(DistOrderChannelModel.class);

        // when
        when(b2bCustomerConverter.convert(customerModel)).thenReturn(customerData);
        when(codeListService.getDistOrderChannel(anyString())).thenReturn(orderChannel);
        when(customerData.getCustomerType()).thenReturn(customerType);
        when(customerType.getCode()).thenReturn("B2B");

        converter.populate(source, target);

        // then
        assertThat(target.getPlacedBy(), equalTo("Distrelec Customer"));
        assertThat(target.getCustomerType(), equalTo("B2B"));
        assertThat(target.getCode(), equalTo("123456"));
    }


    @Test
    public void testPopulateWithDeliveries() {
        // given
        ReadOrderResponseWraper source = createMockReadOrderResponseWrapperWithDeliveries();
        OrderData target = new OrderData();

        // when
        converter.populate(source, target);

        // then
        assertThat(target.getDeliveryData().get(0).getDeliveryID(), equalTo("deliveryId"));
        assertThat(target.getDeliveryData().get(0).getHandlingUnit().get(0).getUnitID(), equalTo("handlingUnitID"));
        assertThat(target.getDeliveryData().get(0).getHandlingUnit().get(0).getTrackingURL(), equalTo("trackingUrl"));
        assertThat(target.getDeliveryData().get(0).getHandlingUnit().get(0).getHandlingUnitItem().get(0).getArticleName(), equalTo("articleName"));
    }

    @Test
    public void testPopulateWithValidShippingMethodCode() {
        // given
        ReadOrderResponseWraper source = createMockReadOrderResponseWrapperWithShippingMethod();
        OrderData target = new OrderData();
        DistDeliveryModeModel distDeliveryModeModel = mock(DistDeliveryModeModel.class);
        DeliveryModeData distDeliveryModeData = mock(DeliveryModeData.class);
        PriceData priceData = mock(PriceData.class);

        // when
        when(source.getReadOrderResponse().getShippingMethodCode()).thenReturn(ShippingMethodCode.N_1);
        when(shippingOptionService.getAbstractDistDeliveryModeForDistShippingMethodCode("N1")).thenReturn(distDeliveryModeModel);
        when(defaultDistDeliveryModeConverter.convert(distDeliveryModeModel)).thenReturn(distDeliveryModeData);
        when(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(source.getReadOrderResponse().getShippingPrice()), source.getReadOrderResponse().getCurrencyCode().name())).thenReturn(priceData);

        converter.populate(source, target);

        // then
        assertThat(target.getDeliveryMode(), equalTo(distDeliveryModeData));
    }

    @Test
    public void testPopulateWithVouchers() {
        // given
        ReadOrderResponseWraper source = createMockReadOrderResponseWrapperWithVouchers();
        OrderData target = new OrderData();
        DistErpVoucherInfoData distErpVoucherInfoData = mock(DistErpVoucherInfoData.class);

        // when
        when(distVoucherToErpVoucherInfoConverter.convert(any(VoucherResponse.class), any(DistErpVoucherInfoData.class))).thenReturn(distErpVoucherInfoData);

        converter.populate(source, target);

        // then
        assertThat(target.getErpVoucherInfoData(), equalTo(distErpVoucherInfoData));
    }

    @Test
    public void testPopulateWithOrderEntries() {
        // given
        ReadOrderResponseWraper source = createMockReadOrderResponseWrapperWithOrderEntries();
        OrderData target = new OrderData();

        // when
        converter.populate(source, target);

        // then
        assertThat(target.getEntries().get(0).getEntryNumber(), equalTo(1234));
        assertThat(target.getEntries().get(1).getEntryNumber(), equalTo(5678));
    }

    @Test
    public void testPopulateWithValidProductData() {
        // given
        ReadOrderResponseWraper source = createMockReadOrderResponseWrapperWithOrderEntries();
        OrderData target = new OrderData();
        ProductModel productModel = mock(ProductModel.class);
        ProductData productData = mock(ProductData.class);
        DistManufacturerModel distManufacturerModel = mock(DistManufacturerModel.class);

        // when
        when(defaultDistProductService.getProductForCode("1234")).thenReturn(productModel);
        when(productModel.getManufacturer()).thenReturn(distManufacturerModel);
        when(distManufacturerModel.getName()).thenReturn("Manufacturer Name");
        when(productModel.getCode()).thenReturn("1234");
        when(productFacade.getProductForCodeAndOptions("1234", PRODUCT_OPTIONS)).thenReturn(productData);

        converter.populate(source, target);

        // then
        assertThat(target.getEntries().get(0).getProduct(), equalTo(productData));
        assertThat(target.getEntries().get(1).getProduct(), is(nullValue()));
    }

    @Test
    public void testPopulateWithPositivePendingQuantity() {
        // given
        ReadOrderResponseWraper source = createMockReadOrderResponseWrapperWithOrderEntries();
        OrderData target = new OrderData();

        // when
        converter.populate(source, target);

        // then
        assertThat(target.getEntries().get(0).getAvailabilities().get(0).getQuantity(), equalTo(10L));
    }

    @Test
    public void testCreateAddressWithValidAddress() {
        // given
        AddressWithId address = createMockAddressWithId();
        boolean isBillingAddress = false;

        // when
        AddressData result = converter.createAddress(address, isBillingAddress);

        // then
        assertThat(result.getCompanyName(), equalTo(address.getCompanyName1()));
        assertThat(result.getPhone(), equalTo(address.getPhoneNumber()));
        assertThat(result.isBillingAddress(), is(false));
        assertThat(result.isShippingAddress(), is(true));
    }

    @Test
    public void testPopulateWithReturnReasons() {
        // given
        ReadOrderResponseWraper source = createMockReadOrderResponseWrapperWithOrderEntries();
        RMAGetOrderItemsResponse rmaResponse = mock(RMAGetOrderItemsResponse.class);
        ReturnReasonsList returnReasonsList = mock(ReturnReasonsList.class);
        ReturnReasons returnReason1 = mock(ReturnReasons.class);
        ReturnReasons returnReason2 = mock(ReturnReasons.class);
        List<ReturnReasons> returnReasons = Arrays.asList(returnReason1, returnReason2);
        OrderData distOrderData = new OrderData();

        // when
        when(source.getRmaGetOrderItemsResponse()).thenReturn(rmaResponse);
        when(rmaResponse.getReturnReasons()).thenReturn(returnReasonsList);
        when(returnReasonsList.getReturnReason()).thenReturn(returnReasons);
        when(returnReason1.getReturnReasonID()).thenReturn("001");
        when(returnReason2.getReturnReasonID()).thenReturn("002");

        converter.populate(source, distOrderData);

        // then
        assertThat(distOrderData.getReturnReason().get(0).getMainReasonText(), equalTo("cart.return.items.return.mainReason001"));
        assertThat(distOrderData.getReturnReason().get(1).getMainReasonText(), equalTo("cart.return.items.return.mainReason002"));
    }

    @Test
    public void testPopulateWithValidPaymentMethodCode() {
        // given
        ReadOrderResponseWraper source = mock(ReadOrderResponseWraper.class);
        ReadOrderResponseV2 readOrderResponse = mock(ReadOrderResponseV2.class);
        DistPaymentModeModel distPaymentModeModel = mock(DistPaymentModeModel.class);
        DistPaymentModeData distPaymentModeData = mock(DistPaymentModeData.class);
        DistOrderChannelModel orderChannel = mock(DistOrderChannelModel.class);
        OrderData target = new OrderData();

        // when
        when(source.getReadOrderResponse()).thenReturn(readOrderResponse);
        when(readOrderResponse.getPaymentMethodCode()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(paymentOptionService.getAbstractDistPaymentModeForErpPaymentModeCode(DistConstants.PaymentMethods.CREDIT_CARD)).thenReturn(distPaymentModeModel);
        when(defaultDistPaymentModeConverter.convert(distPaymentModeModel)).thenReturn(distPaymentModeData);
        when(codeListService.getDistOrderChannel(anyString())).thenReturn(orderChannel);
        when(readOrderResponse.getOrderChannel()).thenReturn("web");
        when(readOrderResponse.getCurrencyCode()).thenReturn(CurrencyCode.CHF);

        converter.populate(source, target);

        // then
        assertThat(target.getPaymentMode(), equalTo(distPaymentModeData));
    }

    private AddressWithId createMockAddressWithId() {
        AddressWithId address = mock(AddressWithId.class);
        CountryModel countryModel = mock(CountryModel.class);

        when(address.getCompanyName1()).thenReturn("Company Name 1");
        when(address.getCountry()).thenReturn("Switzerland");
        when(commonI18NService.getCountry(anyString())).thenReturn(countryModel);
        when(address.getPhoneNumber()).thenReturn("123456789");

        return address;
    }

    private ReadOrderResponseWraper createMockReadOrderResponseWrapperWithOrderEntries() {
        ReadOrderResponseWraper wrapper = mock(ReadOrderResponseWraper.class);
        ReadOrderResponseV2 response = mock(ReadOrderResponseV2.class);
        OrderReadEntry orderReadEntry1 = mock(OrderReadEntry.class);
        OrderReadEntry orderReadEntry2 = mock(OrderReadEntry.class);
        RMAGetOrderItemsResponse rmaResponse = mock(RMAGetOrderItemsResponse.class);
        DistOrderChannelModel orderChannel = mock(DistOrderChannelModel.class);

        when(wrapper.getReadOrderResponse()).thenReturn(response);
        when(wrapper.getRmaGetOrderItemsResponse()).thenReturn(rmaResponse);
        when(response.getCurrencyCode()).thenReturn(CurrencyCode.CHF);
        when(response.getOrderEntries()).thenReturn(Arrays.asList(orderReadEntry1, orderReadEntry2));
        when(codeListService.getDistOrderChannel(anyString())).thenReturn(orderChannel);
        when(response.getOrderChannel()).thenReturn("web");
        when(orderReadEntry1.getOrderPosition()).thenReturn("1");
        when(orderReadEntry2.getOrderPosition()).thenReturn("2");
        when(orderReadEntry1.getMaterialNumber()).thenReturn("1234");
        when(orderReadEntry2.getMaterialNumber()).thenReturn("5678");

        when(orderReadEntry1.getPendingQuantity()).thenReturn(10L);


        RMAGetOrderItemsResponse rmaGetOrderItemsResponse = mock(RMAGetOrderItemsResponse.class);
        when(wrapper.getRmaGetOrderItemsResponse()).thenReturn(rmaGetOrderItemsResponse);
        ItemList itemList1 = mock(ItemList.class);
        ItemList itemList2 = mock(ItemList.class);
        when(itemList1.getItemNumber()).thenReturn("1");
        when(itemList2.getItemNumber()).thenReturn("5");
        com.distrelec.webservice.if19.v1.Item item = mock(com.distrelec.webservice.if19.v1.Item.class);
        when(rmaGetOrderItemsResponse.getItems()).thenReturn(item);
        when(item.getItem()).thenReturn(Arrays.asList(itemList1, itemList2));

        return wrapper;
    }

    private ReadOrderResponseWraper createMockReadOrderResponseWrapperWithVouchers() {
        ReadOrderResponseWraper wrapper = mock(ReadOrderResponseWraper.class);
        ReadOrderResponseV2 response = mock(ReadOrderResponseV2.class);
        VoucherResponse voucher = mock(VoucherResponse.class);
        DistOrderChannelModel orderChannel = mock(DistOrderChannelModel.class);

        when(wrapper.getReadOrderResponse()).thenReturn(response);
        when(voucher.getFixedValue()).thenReturn(100.0);
        when(response.getVouchers()).thenReturn(Collections.singletonList(voucher));
        when(response.getCurrencyCode()).thenReturn(CurrencyCode.CHF);
        when(codeListService.getDistOrderChannel(anyString())).thenReturn(orderChannel);
        when(response.getOrderChannel()).thenReturn("web");


        return wrapper;
    }

    private ReadOrderResponseWraper createMockReadOrderResponseWrapperWithShippingMethod() {
        ReadOrderResponseWraper wrapper = mock(ReadOrderResponseWraper.class);
        ReadOrderResponseV2 response = mock(ReadOrderResponseV2.class);
        DistOrderChannelModel orderChannel = mock(DistOrderChannelModel.class);

        when(wrapper.getReadOrderResponse()).thenReturn(response);
        when(response.getShippingMethodCode()).thenReturn(ShippingMethodCode.N_1);
        when(response.getShippingPrice()).thenReturn(10.0);
        when(response.getCurrencyCode()).thenReturn(CurrencyCode.CHF);
        when(codeListService.getDistOrderChannel(anyString())).thenReturn(orderChannel);
        when(response.getOrderChannel()).thenReturn("web");

        return wrapper;
    }


    private ReadOrderResponseWraper createMockReadOrderResponseWrapperWithDeliveries() {
        ReadOrderResponseWraper wrapper = mock(ReadOrderResponseWraper.class);
        ReadOrderResponseV2 response = mock(ReadOrderResponseV2.class);
        Deliveries deliveries = mock(Deliveries.class);
        Delivery delivery = mock(Delivery.class);
        HandlingUnit handlingUnit = mock(HandlingUnit.class);
        Item item = mock(Item.class);
        DistOrderChannelModel orderChannel = mock(DistOrderChannelModel.class);

        when(wrapper.getReadOrderResponse()).thenReturn(response);
        when(response.getDeliveries()).thenReturn(deliveries);
        when(deliveries.getDelivery()).thenReturn(Collections.singletonList(delivery));
        when(delivery.getHandlingUnits()).thenReturn(Collections.singletonList(handlingUnit));
        when(handlingUnit.getItems()).thenReturn(Collections.singletonList(item));
        when(codeListService.getDistOrderChannel(anyString())).thenReturn(orderChannel);
        when(response.getOrderChannel()).thenReturn("web");
        when(response.getCurrencyCode()).thenReturn(CurrencyCode.CHF);
        when(delivery.getDeliveryID()).thenReturn("deliveryId");
        when(handlingUnit.getHandlingUnitID()).thenReturn("handlingUnitID");
        when(handlingUnit.getHandlingUnitTrackingURL()).thenReturn("trackingUrl");
        when(item.getArticleName()).thenReturn("articleName");
        when(response.getOrderDiscounts()).thenReturn(List.of(mock(OrderDiscounts.class)));

        return wrapper;
    }

    private ReadOrderResponseWraper createMockReadOrderResponseWrapper() {
        ReadOrderResponseWraper readOrderResponseWraper = mock(ReadOrderResponseWraper.class);
        ReadOrderResponseV2 readOrderResponse = mock(ReadOrderResponseV2.class);
        RMAGetOrderItemsResponse rmaGetOrderItemsResponse = mock(RMAGetOrderItemsResponse.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        DistB2BBudgetModel distB2BBudgetModel = mock(DistB2BBudgetModel.class);

        when(readOrderResponse.getContactName()).thenReturn("Distrelec Customer");
        when(readOrderResponse.getOrderId()).thenReturn("123456");
        when(readOrderResponse.getOrderChannel()).thenReturn("web");
        when(readOrderResponseWraper.getReadOrderResponse()).thenReturn(readOrderResponse);
        when(readOrderResponseWraper.getRmaGetOrderItemsResponse()).thenReturn(rmaGetOrderItemsResponse);
        when(readOrderResponseWraper.getB2BCustomerModel()).thenReturn(customer);
        when(customer.getBudget()).thenReturn(distB2BBudgetModel);
        when(readOrderResponse.getCurrencyCode()).thenReturn(CurrencyCode.CHF);

        return readOrderResponseWraper;
    }
}
