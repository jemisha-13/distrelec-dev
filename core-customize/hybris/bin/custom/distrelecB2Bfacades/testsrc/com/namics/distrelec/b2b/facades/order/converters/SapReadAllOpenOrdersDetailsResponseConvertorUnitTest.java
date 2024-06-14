package com.namics.distrelec.b2b.facades.order.converters;

import com.distrelec.webservice.if15.v1.AddressWithId;
import com.distrelec.webservice.if15.v1.CurrencyCode;
import com.distrelec.webservice.if15.v1.OpenOrders;
import com.distrelec.webservice.if15.v1.OrderReadEntry;
import com.distrelec.webservice.if15.v1.ShippingMethodCode;
import com.distrelec.webservice.if15.v1.VoucherResponse;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService;
import com.namics.distrelec.b2b.core.inout.erp.ShippingOptionService;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.delivery.DistDeliveryModeModel;
import com.namics.distrelec.b2b.core.model.payment.DistPaymentModeModel;
import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapReadAllOpenOrdersDetailsResponseConvertorUnitTest {

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private ProductService defaultDistProductService;

    @Mock
    private Converter<DistPaymentModeModel, DistPaymentModeData> defaultDistPaymentModeConverter;

    @Mock
    private Converter<DistDeliveryModeModel, DeliveryModeData> defaultDistDeliveryModeConverter;


    @Mock
    private DistrelecProductFacade productFacade;

    @Mock
    private ShippingOptionService shippingOptionService;

    @Mock
    private PriceDataFactory priceDataFactory;

    @Mock
    private Converter<VoucherResponse, DistErpVoucherInfoData> distVoucherToErpVoucherInfoConverter;

    @Mock
    private PaymentOptionService paymentOptionService;

    @InjectMocks
    private SapReadAllOpenOrdersDetailsResponseConvertor converter;

    @Test
    public void testPopulate() {
        // given
        OpenOrders source = mock(OpenOrders.class);
        OrderData target = new OrderData();

        // when
        when(source.getOrderId()).thenReturn("12345");
        when(source.isEditableByAllContacts()).thenReturn(true);

        converter.populate(source, target);

        // then
        assertThat(target.getOpenOrder(), is(true));
        assertThat(target.getCode(), equalTo("12345"));
    }

    @Test
    public void testPopulateWithShippingMethodCode() {
        // given
        OpenOrders source = mock(OpenOrders.class);
        PriceData shippingCostPriceData = mock(PriceData.class);
        DistDeliveryModeModel distShippingMode = mock(DistDeliveryModeModel.class);
        DeliveryModeData distDeliveryModeData = mock(DeliveryModeData.class);
        OrderData target = new OrderData();

        // when
        when(source.getShippingMethodCode()).thenReturn(ShippingMethodCode.N_1);
        when(source.getShippingPrice()).thenReturn(50.0);
        when(source.getCurrencyCode()).thenReturn(CurrencyCode.CHF);
        when(shippingOptionService.getAbstractDistDeliveryModeForDistShippingMethodCode("N1")).thenReturn(distShippingMode);
        when(defaultDistDeliveryModeConverter.convert(distShippingMode)).thenReturn(distDeliveryModeData);
        when(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(50.0), "CHF")).thenReturn(shippingCostPriceData);

        converter.populate(source, target);

        // then
        assertThat(target.getDeliveryMode(), equalTo(distDeliveryModeData));
    }

    @Test
    public void testPopulateWithVoucher() {
        // given
        OpenOrders source = mock(OpenOrders.class);
        DistErpVoucherInfoData distErpVoucherInfoData = mock(DistErpVoucherInfoData.class);
        VoucherResponse voucherResponse = mock(VoucherResponse.class);
        OrderData target = new OrderData();

        // when
        when(source.getCurrencyCode()).thenReturn(CurrencyCode.CHF);
        when(source.getVouchers()).thenReturn(Collections.singletonList(voucherResponse));
        when(distVoucherToErpVoucherInfoConverter.convert(any(VoucherResponse.class), any(DistErpVoucherInfoData.class))).thenReturn(distErpVoucherInfoData);

        converter.populate(source, target);

        // then
        assertThat(target.getErpVoucherInfoData(), equalTo(distErpVoucherInfoData));
    }

    @Test
    public void testPopulateWithPaymentMethod() {
        // given
        OpenOrders source = mock(OpenOrders.class);
        DistPaymentModeModel distPaymentMode = mock(DistPaymentModeModel.class);
        DistPaymentModeData distPaymentModeData = mock(DistPaymentModeData.class);
        OrderData target = new OrderData();

        // when
        when(source.getCurrencyCode()).thenReturn(CurrencyCode.CHF);
        when(source.getPaymentMethodCode()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(paymentOptionService.getAbstractDistPaymentModeForErpPaymentModeCode(DistConstants.PaymentMethods.CREDIT_CARD)).thenReturn(distPaymentMode);
        when(defaultDistPaymentModeConverter.convert(distPaymentMode)).thenReturn(distPaymentModeData);

        converter.populate(source, target);

        // then
        assertThat(target.getPaymentMode(), equalTo(distPaymentModeData));
    }

    @Test
    public void testPopulateWithOrderEntries() {
        // given
        OpenOrders source = mock(OpenOrders.class);
        OrderReadEntry orderReadEntry = createMockOrderReadEntry();
        OrderData target = new OrderData();
        ProductModel productModel = mock(ProductModel.class);
        ProductData productData = mock(ProductData.class);
        DistManufacturerModel distManufacturerModel = mock(DistManufacturerModel.class);

        // when
        when(source.getOrderEntries()).thenReturn(Collections.singletonList(orderReadEntry));
        when(source.getCurrencyCode()).thenReturn(CurrencyCode.CHF);
        when(productModel.getManufacturer()).thenReturn(distManufacturerModel);
        when(defaultDistProductService.getProductForCode(orderReadEntry.getMaterialNumber())).thenReturn(productModel);
        when(productFacade.getProductForCodeAndOptions(productModel.getCode(), Arrays.asList(ProductOption.BASIC,
                                                                                             ProductOption.SUMMARY,
                                                                                             ProductOption.DESCRIPTION,
                                                                                             ProductOption.PROMOTION_LABELS,
                                                                                             ProductOption.DIST_MANUFACTURER))).thenReturn(productData);

        converter.populate(source, target);

        // then
        assertThat(target.getEntries().get(0).getEntryNumber(), equalTo(12345));
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

    private AddressWithId createMockAddressWithId() {
        AddressWithId address = mock(AddressWithId.class);
        CountryModel countryModel = mock(CountryModel.class);

        when(address.getCompanyName1()).thenReturn("Company Name 1");
        when(address.getCountry()).thenReturn("Switzerland");
        when(commonI18NService.getCountry(anyString())).thenReturn(countryModel);
        when(address.getPhoneNumber()).thenReturn("123456789");

        return address;
    }

    private OrderReadEntry createMockOrderReadEntry() {
        OrderReadEntry orderReadEntry = mock(OrderReadEntry.class);
        when(orderReadEntry.getPrice()).thenReturn(10.0);
        when(orderReadEntry.getMaterialNumber()).thenReturn("12345");
        when(orderReadEntry.getOrderQuantity()).thenReturn(5L);
        when(orderReadEntry.getTotal()).thenReturn(50.0);
        when(orderReadEntry.getDeliveryDate()).thenReturn(BigInteger.valueOf(20231212));

        return orderReadEntry;
    }
}
