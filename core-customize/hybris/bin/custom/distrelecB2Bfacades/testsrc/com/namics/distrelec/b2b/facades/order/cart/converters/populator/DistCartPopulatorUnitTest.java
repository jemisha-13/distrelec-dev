package com.namics.distrelec.b2b.facades.order.cart.converters.populator;

import com.namics.distrelec.b2b.core.enums.DistCreditBlockEnum;
import com.namics.distrelec.b2b.core.inout.erp.OrderService;
import com.namics.distrelec.b2b.core.model.delivery.AbstractDistDeliveryModeModel;
import com.namics.distrelec.b2b.core.model.order.DistErpVoucherInfoModel;
import com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel;
import com.namics.distrelec.b2b.core.service.order.model.ErpOpenOrderExtModel;
import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.order.warehouse.data.WarehouseData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCommentData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.VirtualEntryGroupStrategy;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.BaseConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistCartPopulatorUnitTest {

    @Mock
    private CMSSiteService cmsSiteService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private BaseConfiguration baseConfiguration;

    @Mock
    private PriceDataFactory priceDataFactory;

    @Mock
    private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;

    @Mock
    private PromotionsService promotionsService;

    @Mock
    private Converter<PrincipalModel, PrincipalData> principalConverter;

    @Mock
    private Converter<B2BCostCenterModel, B2BCostCenterData> b2BCostCenterConverter;

    @Mock
    private Converter<CheckoutPaymentType, B2BPaymentTypeData> b2bPaymentTypeConverter;

    @Mock
    private Converter<B2BCommentModel, B2BCommentData> b2BCommentConverter;

    @Mock
    private Converter<UserModel, CustomerData> b2bCustomerConverter;

    @Mock
    private B2BOrderService b2bOrderService;

    @Mock
    private Converter<CurrencyModel, CurrencyData> currencyConverter;

    @InjectMocks
    private DistCartPopulator distCartPopulator;

    @Mock
    private CMSSiteModel cmsSiteModel;

    @Mock
    private VirtualEntryGroupStrategy virtualEntryGroupStrategy;

    @Mock
    private CurrencyModel currency;

    @Mock
    private PrincipalData user;

    @Mock
    private CartModel cart;

    @Mock
    private AbstractOrderEntryModel orderEntry;

    @Mock
    private OrderEntryData orderEntryData;

    @Mock
    private Converter<AbstractDistDeliveryModeModel, DeliveryModeData> distDeliveryModeConverter;

    @Mock
    private Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter;

    @Mock
    private Converter<PaymentModeModel, DistPaymentModeData> paymentModeConverter;

    @Mock
    private Converter<AddressModel, AddressData> addressConverter;

    @Mock
    private OrderService erpOrderService;

    @Mock
    private ModelService modelService;

    @Mock
    private Converter<PromotionResultModel, PromotionResultData> promotionResultConverter;

    @Mock
    private Converter<WarehouseModel, WarehouseData> warehouseConverter;

    @Mock
    private Converter<AbstractOrderModel, DistErpVoucherInfoData> distErpVoucherInfoDataConverter;

    @Spy
    private CartData cartData;

    @Before
    public void setUp() {
        when(currency.getIsocode()).thenReturn("CHF");
        when(cmsSiteModel.getUid()).thenReturn("testsite");
        when(cmsSiteService.getCurrentSite()).thenReturn(cmsSiteModel);
        when(configurationService.getConfiguration()).thenReturn(baseConfiguration);
        when(configurationService.getConfiguration().getDouble("cart.free.freight.min.value." + cmsSiteModel.getUid(), 0.0)).thenReturn(50.0);
        when(principalConverter.convert(any(UserModel.class))).thenReturn(user);
        when(orderEntry.getEntryNumber()).thenReturn(1);
        when(orderEntryData.getEntryNumber()).thenReturn(1);
        when(cart.getEntries()).thenReturn(Collections.singletonList(orderEntry));
        when(cartData.getEntries()).thenReturn(Collections.singletonList(orderEntryData));
        when(cart.getNetSubTotal()).thenReturn(0.0);
        when(cart.getSubtotal()).thenReturn(0.0);
        when(cart.getCurrency()).thenReturn(currency);
        when(cart.getEntryGroups()).thenReturn(null);
    }

    @Test
    public void testMinFreeFreightValueZeroValuePopulate() {
        // given
        PriceData priceData = getPriceData(BigDecimal.valueOf(0.0));
        PriceData priceData1 = getPriceData(BigDecimal.valueOf(50.0));

        // when
        when(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(0.0), currency)).thenReturn(priceData);
        when(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(50.0), "CHF")).thenReturn(priceData1);

        distCartPopulator.populate(cart, cartData);

        // then
        assertThat(cartData.getFreeFreightValue().getValue(), equalTo(BigDecimal.valueOf(50.0)));
    }

    @Test
    public void testMinFreeFreightValueLessThanFreeValuePopulate() {
        // given
        PriceData priceData = getPriceData(BigDecimal.valueOf(35.0));
        PriceData priceData1 = getPriceData(BigDecimal.valueOf(15.0));

        // when
        when(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(35.0), currency)).thenReturn(priceData);
        when(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(15.0), "CHF")).thenReturn(priceData1);
        when(cart.getNetSubTotal()).thenReturn(35.0);
        when(cart.getSubtotal()).thenReturn(35.0);

        distCartPopulator.populate(cart, cartData);

        // then
        assertThat(cartData.getFreeFreightValue().getValue(), equalTo(BigDecimal.valueOf(15.0)));
    }

    @Test
    public void testMinFreeFreightValueMoreThanFreeValuePopulate() {
        // given
        PriceData priceData = getPriceData(BigDecimal.valueOf(60.0));
        PriceData priceData1 = getPriceData(BigDecimal.valueOf(-10.0));

        // when
        when(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(60.0), currency)).thenReturn(priceData);
        when(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(-10.0), "CHF")).thenReturn(priceData1);
        when(cart.getNetSubTotal()).thenReturn(60.0);
        when(cart.getSubtotal()).thenReturn(60.0);

        distCartPopulator.populate(cart, cartData);

        // then
        assertThat(cartData.getFreeFreightValue().getValue(), equalTo(BigDecimal.valueOf(-10.0)));
    }

    @Test
    public void testSetValidDeliveryDates() {
        // given
        AbstractDistDeliveryModeModel abstractDistDeliveryModeModel = mock(AbstractDistDeliveryModeModel.class);
        DeliveryModeData deliveryModeData = mock(DeliveryModeData.class);
        PaymentModeModel paymentModeModel = mock(PaymentModeModel.class);

        // when
        when(cart.getValidDeliveryModes()).thenReturn(Arrays.asList(abstractDistDeliveryModeModel));
        when(cart.getDeliveryMode()).thenReturn(abstractDistDeliveryModeModel);
        when(deliveryModeConverter.convert(abstractDistDeliveryModeModel)).thenReturn(deliveryModeData);
        when(distDeliveryModeConverter.convert(abstractDistDeliveryModeModel)).thenReturn(deliveryModeData);
        when(cart.getPaymentMode()).thenReturn(paymentModeModel);

        distCartPopulator.populate(cart, cartData);

        // then
        assertThat(cartData.getValidDeliveryModes().size(), is(1));
        assertThat(cartData.getValidDeliveryModes().get(0), is(deliveryModeData));
    }

    @Test
    public void testSetValidDeliveryDatesWithSelectedSetToFalse() {
        // given
        AbstractDistDeliveryModeModel abstractDistDeliveryModeModel = mock(AbstractDistDeliveryModeModel.class);
        DeliveryModeData deliveryModeData = mock(DeliveryModeData.class);
        PaymentModeModel paymentModeModel = mock(PaymentModeModel.class);

        // when
        when(cart.getValidDeliveryModes()).thenReturn(Arrays.asList(abstractDistDeliveryModeModel));
        when(cart.getDeliveryMode()).thenReturn(null);
        when(distDeliveryModeConverter.convert(abstractDistDeliveryModeModel)).thenReturn(deliveryModeData);
        when(cart.getPaymentMode()).thenReturn(paymentModeModel);

        distCartPopulator.populate(cart, cartData);

        // then
        assertThat(cartData.getValidDeliveryModes().size(), is(1));
        assertThat(cartData.getValidDeliveryModes().get(0), is(deliveryModeData));
        assertThat(cartData.getValidDeliveryModes().get(0).getSelected(), is(Boolean.FALSE));
    }

    @Test
    public void testIsCreditBlocked() {
        // when
        when(cart.getCreditBlocked()).thenReturn(DistCreditBlockEnum.B.getValue());

        distCartPopulator.populate(cart, cartData);

        // then
        assertThat(cartData.getCreditBlocked(), is(Boolean.TRUE));
    }

    @Test
    public void testCreditIsNotBlocked() {
        // when
        when(cart.getCreditBlocked()).thenReturn(DistCreditBlockEnum.A.getValue());

        distCartPopulator.populate(cart, cartData);

        // then
        assertThat(cartData.getCreditBlocked(), is(Boolean.FALSE));
    }

    @Test
    public void testGetValidPaymentModes() {
        // given
        AbstractDistPaymentModeModel abstractDistPaymentModeModel = mock(AbstractDistPaymentModeModel.class);
        DistPaymentModeData distPaymentModeData = mock(DistPaymentModeData.class);

        // when
        when(cart.getValidPaymentModes()).thenReturn(Arrays.asList(abstractDistPaymentModeModel));
        when(paymentModeConverter.convert(abstractDistPaymentModeModel)).thenReturn(distPaymentModeData);

        distCartPopulator.populate(cart, cartData);

        // then
        assertThat(cartData.getValidPaymentModes().size(), equalTo(1));
        assertThat(cartData.getValidPaymentModes().get(0), equalTo(distPaymentModeData));
    }

    @Test
    public void testGetAddressData() {
        // given
        AddressModel addressModel = mock(AddressModel.class);
        B2BUnitModel unitModel = mock(B2BUnitModel.class);
        AddressData addressData = mock(AddressData.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        when(cart.getUser()).thenReturn(customer);
        when(customer.getDefaultB2BUnit()).thenReturn(unitModel);
        when(cart.getPaymentAddress()).thenReturn(addressModel);
        when(unitModel.getVatID()).thenReturn("123456");
        when(addressConverter.convert(addressModel)).thenReturn(addressData);

        // when
        distCartPopulator.populate(cart, cartData);

        // then
        verify(addressData).setCodiceFiscale("123456");
        assertThat(cartData.getBillingAddress(), equalTo(addressData));
    }

    @Test
    public void testOpenOrderIsTrueAndOpenOrderNewIsFalse() {
        // given
        ErpOpenOrderExtModel erpOpenOrderExtModel = mock(ErpOpenOrderExtModel.class);
        Date orderDate = new Date();
        Date orderCloseDate = new Date();
        String erpOpenOrderCode = "12345";

        // when
        when(cart.isOpenOrder()).thenReturn(true);
        when(cart.getErpOpenOrderCode()).thenReturn(erpOpenOrderCode);
        when(erpOrderService.getOpenOrderForErpOrderCode(erpOpenOrderCode)).thenReturn(erpOpenOrderExtModel);
        when(erpOpenOrderExtModel.getOrderDate()).thenReturn(orderDate);
        when(erpOpenOrderExtModel.getOrderCloseDate()).thenReturn(orderCloseDate);

        distCartPopulator.populate(cart, cartData);

        // then
        assertThat(cartData.getOpenOrder(), is(true));
        assertThat(cartData.getOpenOrderNew(), is(false));
        assertThat(cartData.getOpenOrderErpCode(), is(erpOpenOrderCode));
        assertThat(cartData.getOpenOrderCreationDate(), is(orderDate));
        assertThat(cartData.getOpenOrderSelectedClosingDate(), is(orderCloseDate));
    }

    @Test
    public void testOpenOrderIsTrueAndOpenOrderNewIsTrue() {
        // given
        Date orderCloseDate = new Date();

        // when
        when(cart.isOpenOrder()).thenReturn(true);
        when(cart.getErpOpenOrderCode()).thenReturn("");
        when(cart.getOrderCloseDate()).thenReturn(orderCloseDate);

        distCartPopulator.populate(cart, cartData);

        // then
        assertThat(cartData.getOpenOrder(), is(true));
        assertThat(cartData.getOpenOrderNew(), is(true));
        assertThat(cartData.getOpenOrderSelectedClosingDate(), is(orderCloseDate));
    }

    @Test
    public void testOpenOrderIsNull() {
        // when
        when(cart.isOpenOrder()).thenReturn(false);

        distCartPopulator.populate(cart, cartData);

        // then
        assertThat(cartData.getOpenOrder(), is(nullValue()));
        assertThat(cartData.getOpenOrderErpCode(), is(nullValue()));
    }

    @Test
    public void testSubtractDeliveryCostWhenDeliveryCostIsNotZeroAndTotalPriceNotNull() {
        // given
        PriceData priceData = mock(PriceData.class);
        when(priceData.getValue()).thenReturn(BigDecimal.valueOf(100));

        // when
        when(cart.getDeliveryMode()).thenReturn(null);
        when(cartData.getTotalPrice()).thenReturn(priceData);
        when(cart.getNetDeliveryCost()).thenReturn(10.0);

        distCartPopulator.populate(cart, cartData);

        // then
        verify(priceData).setValue(BigDecimal.valueOf(90));
        assertThat(cartData.getDeliveryCostExcluded(), is(true));
    }

    @Test
    public void testDoNotSubtractDeliveryCostWhenDeliveryCostIsZero() {
        // given
        PriceData priceData = new PriceData();
        priceData.setValue(BigDecimal.valueOf(100));

        // when
        when(cart.getDeliveryMode()).thenReturn(null);
        when(cartData.getTotalPrice()).thenReturn(priceData);
        when(cart.getNetDeliveryCost()).thenReturn(0.0);

        distCartPopulator.populate(cart, cartData);

        // then
        assertThat(cartData.getTotalPrice().getValue(), is(BigDecimal.valueOf(100)));
        assertThat(cartData.getDeliveryCostExcluded(), is(nullValue()));
    }

    @Test
    public void testDoNotSubtractDeliveryCostWhenTotalPriceIsNull() {
        // when
        when(cart.getDeliveryMode()).thenReturn(null);

        distCartPopulator.populate(cart, cartData);

        assertThat(cartData.getTotalPrice(), is(nullValue()));
        assertThat(cartData.getDeliveryCostExcluded(), is(nullValue()));
    }

    @Test
    public void testAddPromotionsWhenPromoOrderResultsIsNull() {
        // when
        distCartPopulator.addPromotions(cart, null, cartData);

        // then
        assertThat(cartData.getPotentialOrderPromotions(), is(nullValue()));
        assertThat(cartData.getPotentialProductPromotions(), is(nullValue()));
    }

    @Test
    public void testAddPromotionsWhenPromoOrderResultsIsNotNull() {
        // given
        PromotionOrderResults promoOrderResults = mock(PromotionOrderResults.class);

        List<PromotionResult> orderPromotionResults = new ArrayList<>(Arrays.asList(mock(PromotionResult.class)));
        List<PromotionResult> productPromotionResults = new ArrayList<>(Arrays.asList(mock(PromotionResult.class)));
        List<PromotionResult> appliedOrderPromotionResults = new ArrayList<>(Arrays.asList(mock(PromotionResult.class)));
        List<PromotionResult> appliedProductPromotionResults = new ArrayList<>(Arrays.asList(mock(PromotionResult.class)));
        List<PromotionResultModel> appliedOrderPromotionResultsModel = new ArrayList<>(Arrays.asList(mock(PromotionResultModel.class)));

        List<PromotionResultData> orderPromotionResultsData = new ArrayList<>(Arrays.asList(mock(PromotionResultData.class)));
        List<PromotionResultData> productPromotionResultsData = new ArrayList<>(Arrays.asList(mock(PromotionResultData.class)));
        List<PromotionResultData> appliedOrderPromotionResultsData = new ArrayList<>(Arrays.asList(mock(PromotionResultData.class)));
        List<PromotionResultData> appliedProductPromotionResultsData = new ArrayList<>(Arrays.asList(mock(PromotionResultData.class)));

        // when
        when(promoOrderResults.getPotentialProductPromotions()).thenReturn(productPromotionResults);
        when(promoOrderResults.getPotentialOrderPromotions()).thenReturn(orderPromotionResults);
        when(promoOrderResults.getAppliedOrderPromotions()).thenReturn(appliedOrderPromotionResults);
        when(promoOrderResults.getAppliedProductPromotions()).thenReturn(appliedProductPromotionResults);

        when(modelService.getAll(anyList(), anyList())).thenReturn(Collections.singletonList(appliedOrderPromotionResultsModel))
                                                       .thenReturn(Collections.singletonList(appliedOrderPromotionResultsData))
                                                       .thenReturn(Collections.singletonList(orderPromotionResultsData))
                                                       .thenReturn(Collections.singletonList(productPromotionResultsData));

        when(promotionResultConverter.convertAll(anyList())).thenReturn(appliedOrderPromotionResultsData)
                                                            .thenReturn(appliedProductPromotionResultsData)
                                                            .thenReturn(orderPromotionResultsData)
                                                            .thenReturn(productPromotionResultsData);

        distCartPopulator.addPromotions(cart, promoOrderResults, cartData);

        // then
        assertThat(cartData.getPotentialOrderPromotions(), is(orderPromotionResultsData));
        assertThat(cartData.getPotentialProductPromotions(), is(productPromotionResultsData));
    }

    @Test
    public void testAddSavedCartDataName() {
        // when
        when(cart.getName()).thenReturn("TestName");

        distCartPopulator.addSavedCartData(cart, cartData);

        // then
        assertThat(cartData.getName(), equalTo("TestName"));
    }

    @Test
    public void testAddSavedCartDataDescription() {
        // when
        when(cart.getDescription()).thenReturn("TestDescription");

        distCartPopulator.addSavedCartData(cart, cartData);

        // then
        assertThat(cartData.getDescription(), equalTo("TestDescription"));
    }

    @Test
    public void testAddSavedCartDataSaveTime() {
        // given
        Date saveTime = new Date();

        // when
        when(cart.getSaveTime()).thenReturn(saveTime);

        distCartPopulator.addSavedCartData(cart, cartData);

        // then
        assertThat(cartData.getSaveTime(), equalTo(saveTime));
    }

    @Test
    public void testAddSavedCartDataExpirationTime() {
        // given
        Date expirationTime = new Date();

        // when
        when(cart.getExpirationTime()).thenReturn(expirationTime);

        distCartPopulator.addSavedCartData(cart, cartData);

        // then
        assertThat(cartData.getExpirationTime(), equalTo(expirationTime));
    }

    @Test
    public void testAddSavedCartDataSavedBy() {
        // given
        UserModel user = mock(UserModel.class);
        String userName = "TestUser";
        String uid = "123";

        // when
        when(user.getName()).thenReturn(userName);
        when(user.getUid()).thenReturn(uid);
        when(cart.getSavedBy()).thenReturn(user);

        distCartPopulator.addSavedCartData(cart, cartData);

        // then
        assertThat(cartData.getSavedBy().getName(), equalTo(userName));
        assertThat(cartData.getSavedBy().getUid(), equalTo(uid));
    }

    @Test
    public void testAddSavedCartDataNoName() {
        // when
        when(cart.getName()).thenReturn("");

        distCartPopulator.addSavedCartData(cart, cartData);

        // then
        assertThat(cartData.getName(), is(nullValue()));
    }

    @Test
    public void testPickupLocationNotNull() {
        // given
        WarehouseModel warehouseModel = mock(WarehouseModel.class);
        WarehouseData warehouseData = mock(WarehouseData.class);

        // when
        when(cart.getPickupLocation()).thenReturn(warehouseModel);
        when(warehouseConverter.convert(warehouseModel)).thenReturn(warehouseData);

        distCartPopulator.populate(cart, cartData);

        // then
        assertThat(cartData.getPickupLocation(), equalTo(warehouseData));
    }

    @Test
    public void testRequestedDeliveryDateNotNull() {
        // given
        Date requestedDeliveryDate = new Date();

        // when
        when(cart.getRequestedDeliveryDate()).thenReturn(requestedDeliveryDate);

        distCartPopulator.populate(cart, cartData);

        // then
        assertThat(cartData.getReqDeliveryDateHeaderLevel(), equalTo(requestedDeliveryDate));
    }

    @Test
    public void testErpVoucherInfoNotNull() {
        // given
        DistErpVoucherInfoModel distErpVoucherInfoModel = mock(DistErpVoucherInfoModel.class);
        DistErpVoucherInfoData distErpVoucherInfoData = mock(DistErpVoucherInfoData.class);

        // when
        when(cart.getErpVoucherInfo()).thenReturn(distErpVoucherInfoModel);
        when(distErpVoucherInfoDataConverter.convert(cart)).thenReturn(distErpVoucherInfoData);

        distCartPopulator.populate(cart, cartData);

        // then
        assertThat(cartData.getErpVoucherInfoData(), equalTo(distErpVoucherInfoData));
    }

    @Test
    public void testCompleteDeliveryFalse() {
        // when
        when(cart.getCompleteDelivery()).thenReturn(null);

        distCartPopulator.populate(cart, cartData);

        // then
        assertThat(cartData.getCompleteDelivery(), equalTo(false));
    }

    @Test
    public void testSkipMovCheckFalse() {
        // when
        when(cart.getSkipMovCheck()).thenReturn(null);

        distCartPopulator.populate(cart, cartData);

        // then
        assertThat(cartData.getSkipMovCheck(), equalTo(false));
    }

    private PriceData getPriceData(BigDecimal value) {
        PriceData priceData = mock(PriceData.class);
        when(priceData.getValue()).thenReturn(value);
        return priceData;
    }
}
