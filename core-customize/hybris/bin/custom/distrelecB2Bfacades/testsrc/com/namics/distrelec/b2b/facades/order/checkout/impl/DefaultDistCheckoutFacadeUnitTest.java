package com.namics.distrelec.b2b.facades.order.checkout.impl;

import com.namics.distrelec.b2b.core.blocking.rule.service.DistBlockedB2BCustomerRuleService;
import com.namics.distrelec.b2b.core.blocking.rule.service.DistBlockedUserAddressRuleService;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.AvailabilityService;
import com.namics.distrelec.b2b.core.inout.erp.CustomerService;
import com.namics.distrelec.b2b.core.inout.erp.OrderCalculationService;
import com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService;
import com.namics.distrelec.b2b.core.inout.erp.ShippingOptionService;
import com.namics.distrelec.b2b.core.model.DistTransportGroupModel;
import com.namics.distrelec.b2b.core.model.blocking.rule.DistBlockedUserAddressRuleModel;
import com.namics.distrelec.b2b.core.model.delivery.AbstractDistDeliveryModeModel;
import com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel;
import com.namics.distrelec.b2b.core.service.company.DistB2BCommerceUnitService;
import com.namics.distrelec.b2b.core.service.customer.DistCustomerAccountService;
import com.namics.distrelec.b2b.core.service.customer.b2b.budget.DistB2BCommerceBudgetService;
import com.namics.distrelec.b2b.core.service.order.DistCardPaymentService;
import com.namics.distrelec.b2b.core.service.order.DistCartService;
import com.namics.distrelec.b2b.core.service.order.DistCommerceCheckoutService;
import com.namics.distrelec.b2b.core.service.order.DistCommercePaymentService;
import com.namics.distrelec.b2b.core.service.order.exceptions.PurchaseBlockedException;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.site.DistrelecBaseStoreService;
import com.namics.distrelec.b2b.core.service.user.DistAddressService;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.AdvancePaymentInfoModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.PaymentModeService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDistCheckoutFacadeUnitTest {

    @Mock
    private CMSSiteService cmsSiteService;

    @Mock
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @Mock
    private WarehouseService warehouseService;

    @Mock
    private PaymentModeService paymentModeService;

    @Mock
    private SessionService sessionService;

    @Mock
    private DistCommercePaymentService distCommercePaymentService;

    @Mock
    private DistCommerceCheckoutService distCommerceCheckoutService;

    @Mock
    private DistB2BCommerceBudgetService b2bCommerceBudgetService;

    @Mock
    private OrderCalculationService orderCalculationService;

    @Mock
    private PaymentOptionService paymentOptionService;

    @Mock
    private ShippingOptionService shippingOptionService;

    @Mock
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private DistAddressService addressService;

    @Mock
    private DistUserFacade userFacade;

    @Mock
    private DistSalesOrgProductService distSalesOrgProductService;

    @Mock
    private BusinessProcessService businessProcessService;

    @Mock
    private DistB2BCommerceUnitService b2bCommerceUnitService;

    @Mock
    private CustomerService customerService;

    @Mock
    private DistB2BCartFacade b2bCartFacade;

    @Mock
    private DistrelecBaseStoreService baseStoreService;

    @Mock
    private AvailabilityService availabilityService;

    @Mock
    private DistProductService distProductService;

    @Mock
    private DistBlockedB2BCustomerRuleService distBlockedB2BCustomerRuleService;

    @Mock
    private DistBlockedUserAddressRuleService distBlockedUserAddressRuleService;

    @Mock
    private DistAddressService distAddressService;

    @Mock
    private Populator<CCPaymentInfoData, CreditCardPaymentInfoModel> cardPaymentInfoReversePopulator;

    @Mock
    private DistCartService distCartService;

    @Mock
    private DistCardPaymentService distCardPaymentService;

    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategy;

    @Mock
    private DistUserService userService;

    @Mock
    private Converter<AddressModel, AddressData> addressConverter;

    @Mock
    private ModelService modelService;

    @Mock
    private DistCustomerAccountService distCustomerAccountService;

    @Mock
    private Converter<PaymentModeModel, DistPaymentModeData> paymentModeConverter;

    @Mock
    private CustomerData customer;

    @Mock
    private AddressData contactAddress;

    @Mock
    private AddressData billingAddress;

    @Mock
    private CountryData country;

    @Mock
    private B2BUnitData unit;

    @Mock
    private DistSalesOrgService distSalesOrgService;

    @Spy
    @InjectMocks
    DefaultDistCheckoutFacade distCheckoutFacade;

    @Test
    public void testIsScheduleDeliveryAllowedForCurrentCart() {
        // given
        DeliveryModeModel deliveryMode = mock(DeliveryModeModel.class);
        CartModel cart = mock(CartModel.class);

        // when
        when(deliveryMode.getCode()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(cart.getDeliveryMode()).thenReturn(deliveryMode);
        when(shippingOptionService.getPickupDeliveryModeCode()).thenReturn(DistConstants.Shipping.METHOD_PICKUP);
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(storeSessionFacade.isCurrentShopExport()).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.isScheduleDeliveryAllowedForCurrentCart();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsScheduleDeliveryAllowedForPickupAndExport() {
        // given
        DeliveryModeModel deliveryMode = mock(DeliveryModeModel.class);
        CartModel cart = mock(CartModel.class);

        // when
        when(deliveryMode.getCode()).thenReturn(DistConstants.Shipping.METHOD_PICKUP);
        when(cart.getDeliveryMode()).thenReturn(deliveryMode);
        when(shippingOptionService.getPickupDeliveryModeCode()).thenReturn(DistConstants.Shipping.METHOD_PICKUP);
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(storeSessionFacade.isCurrentShopExport()).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.isScheduleDeliveryAllowedForCurrentCart();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsScheduleDeliveryNotAllowedForCurrentCart() {
        // given
        DeliveryModeModel deliveryMode = mock(DeliveryModeModel.class);
        CartModel cart = mock(CartModel.class);

        // when
        when(deliveryMode.getCode()).thenReturn(DistConstants.Shipping.METHOD_PICKUP);
        when(cart.getDeliveryMode()).thenReturn(deliveryMode);
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(shippingOptionService.getPickupDeliveryModeCode()).thenReturn(DistConstants.Shipping.METHOD_PICKUP);

        boolean result = distCheckoutFacade.isScheduleDeliveryAllowedForCurrentCart();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsPickupNotAvailableForCart() {
        // given
        DeliveryModeData normalDeliveryMode = mock(DeliveryModeData.class);
        DeliveryModeData economyDeliveryMode = mock(DeliveryModeData.class);
        CartData cart = mock(CartData.class);

        // when
        when(economyDeliveryMode.getCode()).thenReturn(DistConstants.Shipping.METHOD_ECONOMY);
        when(normalDeliveryMode.getCode()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(cart.getValidDeliveryModes()).thenReturn(Arrays.asList(economyDeliveryMode, normalDeliveryMode));

        boolean result = distCheckoutFacade.isPickupAvailableForCart(cart);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsPickupAvailableForCart() {
        // given
        DeliveryModeData normalDeliveryMode = mock(DeliveryModeData.class);
        DeliveryModeData economyDeliveryMode = mock(DeliveryModeData.class);
        DeliveryModeData pickupDeliveryMode = mock(DeliveryModeData.class);

        CartData cart = mock(CartData.class);

        // when
        when(pickupDeliveryMode.getCode()).thenReturn(DistConstants.Shipping.METHOD_PICKUP);
        when(economyDeliveryMode.getCode()).thenReturn(DistConstants.Shipping.METHOD_ECONOMY);
        when(normalDeliveryMode.getCode()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(cart.getValidDeliveryModes()).thenReturn(Arrays.asList(economyDeliveryMode, normalDeliveryMode, pickupDeliveryMode));

        boolean result = distCheckoutFacade.isPickupAvailableForCart(cart);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsPickupNotAllowedForGuest() {
        // given
        CartData cart = mock(CartData.class);

        // when
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.isPickupAllowedForCart(cart);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsPickupNotAllowedForB2ECustomer() {
        // given
        CartData cartData = mock(CartData.class);
        CartModel cart = mock(CartModel.class);

        // when
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.TRUE);
        boolean result = distCheckoutFacade.isPickupAllowedForCart(cartData);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsPickupNotAllowedForB2ECustomerAndExport() {
        // given
        CartData cartData = mock(CartData.class);
        CartModel cart = mock(CartModel.class);

        // when
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.TRUE);
        boolean result = distCheckoutFacade.isPickupAllowedForCart(cartData);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsPickupNotAllowedForWaldom() {
        // given
        CartData cartData = mock(CartData.class);
        CartModel cart = mock(CartModel.class);

        // when
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(cartData.getWaldom()).thenReturn(Boolean.TRUE);
        boolean result = distCheckoutFacade.isPickupAllowedForCart(cartData);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsPickupAllowedForWaldomAndExport() {
        // given
        CartData cartData = mock(CartData.class);
        CartModel cart = mock(CartModel.class);

        // when
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(cartData.getWaldom()).thenReturn(Boolean.TRUE);
        when(storeSessionFacade.isCurrentShopExport()).thenReturn(Boolean.TRUE);
        boolean result = distCheckoutFacade.isPickupAllowedForCart(cartData);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsPickupAllowed() {
        // given
        CartData cartData = mock(CartData.class);
        CartModel cart = mock(CartModel.class);

        // when
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(cartData.getWaldom()).thenReturn(Boolean.FALSE);

        boolean result = distCheckoutFacade.isPickupAllowedForCart(cartData);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsScheduleDeliveryNotAllowedForGuest() {
        // given
        CartData cart = mock(CartData.class);

        // when
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.TRUE);
        boolean result = distCheckoutFacade.isScheduleDeliveryAllowed(cart);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsScheduleDeliveryNotAllowedEconomy() {
        // given
        CartData cartData = mock(CartData.class);
        CartModel cart = mock(CartModel.class);
        DeliveryModeData economyDeliveryMode = mock(DeliveryModeData.class);

        // when
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(economyDeliveryMode.getCode()).thenReturn(DistConstants.Shipping.METHOD_ECONOMY);
        when(cartData.getValidDeliveryModes()).thenReturn(Collections.singletonList(economyDeliveryMode));
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        boolean result = distCheckoutFacade.isScheduleDeliveryAllowed(cartData);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsScheduleDeliveryAllowed() {
        // given
        CartData cartData = mock(CartData.class);
        CartModel cart = mock(CartModel.class);

        DeliveryModeData normalDeliveryMode = mock(DeliveryModeData.class);
        DeliveryModeData economyDeliveryMode = mock(DeliveryModeData.class);

        // when
        when(economyDeliveryMode.getCode()).thenReturn(DistConstants.Shipping.METHOD_ECONOMY);
        when(normalDeliveryMode.getCode()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(cartData.getValidDeliveryModes()).thenReturn(Arrays.asList(normalDeliveryMode, economyDeliveryMode));
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        boolean result = distCheckoutFacade.isScheduleDeliveryAllowed(cartData);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsLimitedUserGuest() {
        // when
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.TRUE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        boolean result = distCheckoutFacade.isNotLimitedUserType();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsLimitedUserB2E() {
        // when
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.TRUE);
        boolean result = distCheckoutFacade.isNotLimitedUserType();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsNotLimitedUser() {
        // given
        CartModel cart = mock(CartModel.class);

        // when
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        boolean result = distCheckoutFacade.isNotLimitedUserType();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testAreDeliveryModeAndAddressesSet() {
        // given
        CartModel cart = mock(CartModel.class);
        DeliveryModeModel deliveryMode = mock(DeliveryModeModel.class);
        AddressModel paymentAddress = mock(AddressModel.class);
        AddressModel deliveryAddress = mock(AddressModel.class);

        // when
        when(cart.getDeliveryMode()).thenReturn(deliveryMode);
        when(cart.getPaymentAddress()).thenReturn(paymentAddress);
        when(cart.getDeliveryAddress()).thenReturn(deliveryAddress);
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(distAddressService.isAddressValid(paymentAddress)).thenReturn(Boolean.TRUE);
        when(distAddressService.isAddressValid(deliveryAddress)).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.areDeliveryModeAndAddressesSet();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsDeliveryModeNotSetAndAddressesSet() {
        // given
        CartModel cart = mock(CartModel.class);
        AddressModel paymentAddress = mock(AddressModel.class);
        AddressModel deliveryAddress = mock(AddressModel.class);

        // when
        when(cart.getDeliveryMode()).thenReturn(null);
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);

        boolean result = distCheckoutFacade.areDeliveryModeAndAddressesSet();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsDeliveryModeSetAndPaymentAddressNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        DeliveryModeModel deliveryMode = mock(DeliveryModeModel.class);
        AddressModel paymentAddress = mock(AddressModel.class);
        AddressModel deliveryAddress = mock(AddressModel.class);

        // when
        when(cart.getDeliveryMode()).thenReturn(deliveryMode);
        when(cart.getPaymentAddress()).thenReturn(paymentAddress);
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(distAddressService.isAddressValid(paymentAddress)).thenReturn(Boolean.FALSE);

        boolean result = distCheckoutFacade.areDeliveryModeAndAddressesSet();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsDeliveryModeSetAndDeliveryAddressNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        DeliveryModeModel deliveryMode = mock(DeliveryModeModel.class);
        AddressModel paymentAddress = mock(AddressModel.class);
        AddressModel deliveryAddress = mock(AddressModel.class);

        // when
        when(deliveryMode.getCode()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(cart.getDeliveryMode()).thenReturn(deliveryMode);
        when(cart.getPaymentAddress()).thenReturn(paymentAddress);
        when(cart.getDeliveryAddress()).thenReturn(deliveryAddress);
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(distAddressService.isAddressValid(paymentAddress)).thenReturn(Boolean.TRUE);
        when(distAddressService.isAddressValid(deliveryAddress)).thenReturn(Boolean.FALSE);

        boolean result = distCheckoutFacade.areDeliveryModeAndAddressesSet();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsDeliveryModePickupAndDeliveryAddressNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        DeliveryModeModel deliveryMode = mock(DeliveryModeModel.class);
        AddressModel paymentAddress = mock(AddressModel.class);
        AddressModel deliveryAddress = mock(AddressModel.class);

        // when
        when(deliveryMode.getCode()).thenReturn(DistConstants.Shipping.METHOD_PICKUP);
        when(cart.getDeliveryMode()).thenReturn(deliveryMode);
        when(cart.getPaymentAddress()).thenReturn(paymentAddress);
        when(cart.getDeliveryAddress()).thenReturn(deliveryAddress);
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(distAddressService.isAddressValid(paymentAddress)).thenReturn(Boolean.TRUE);
        when(distAddressService.isAddressValid(deliveryAddress)).thenReturn(Boolean.FALSE);

        boolean result = distCheckoutFacade.areDeliveryModeAndAddressesSet();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsBillingAddressShippable() {
        // given
        AddressData addressData = mock(AddressData.class);
        CartModel cart = mock(CartModel.class);

        // when
        when(addressData.isShippingAddress()).thenReturn(Boolean.TRUE);
        when(addressData.isBillingAddress()).thenReturn(Boolean.TRUE);
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);

        boolean result = distCheckoutFacade.isBillingAddressShippable(addressData);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsAddressNotBilling() {
        // given
        AddressData addressData = mock(AddressData.class);

        // when
        when(addressData.isShippingAddress()).thenReturn(Boolean.FALSE);
        when(addressData.isBillingAddress()).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.isBillingAddressShippable(addressData);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsBillingAddressNotShippable() {
        // given
        AddressData addressData = mock(AddressData.class);

        // when
        when(addressData.isBillingAddress()).thenReturn(Boolean.FALSE);

        boolean result = distCheckoutFacade.isBillingAddressShippable(addressData);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsBillingAddressShippableForGuest() {
        // given
        AddressData addressData = mock(AddressData.class);

        // when
        when(addressData.isShippingAddress()).thenReturn(Boolean.TRUE);
        when(addressData.isBillingAddress()).thenReturn(Boolean.TRUE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.TRUE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);

        boolean result = distCheckoutFacade.isBillingAddressShippable(addressData);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsBillingAddressShippableForB2E() {
        // given
        AddressData addressData = mock(AddressData.class);

        // when
        when(addressData.isShippingAddress()).thenReturn(Boolean.TRUE);
        when(addressData.isBillingAddress()).thenReturn(Boolean.TRUE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.isBillingAddressShippable(addressData);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testShouldDisplayCreditWarningForExportShop() {
        // given
        CartData cartData = mock(CartData.class);
        CartModel cart = mock(CartModel.class);

        DistPaymentModeData creditCardPayment = mock(DistPaymentModeData.class);
        DistPaymentModeData paypalPayment = mock(DistPaymentModeData.class);

        // when
        when(creditCardPayment.getCode()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(paypalPayment.getCode()).thenReturn(DistConstants.PaymentMethods.PAY_PAL);
        when(cartData.getValidPaymentModes()).thenReturn(Arrays.asList(creditCardPayment, paypalPayment));
        when(storeSessionFacade.isCurrentShopExport()).thenReturn(Boolean.TRUE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.shouldDisplayCreditWarningForExportShop(cartData);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testShouldNotDisplayCreditWarningForNotExportShop() {
        // given
        CartData cartData = mock(CartData.class);
        CartModel cart = mock(CartModel.class);

        // when
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(storeSessionFacade.isCurrentShopExport()).thenReturn(Boolean.FALSE);
        boolean result = distCheckoutFacade.shouldDisplayCreditWarningForExportShop(cartData);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testShouldNotDisplayCreditWarningForExportShopForGuest() {
        // given
        CartData cartData = mock(CartData.class);
        CartModel cart = mock(CartModel.class);

        // when
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.shouldDisplayCreditWarningForExportShop(cartData);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testShouldNotDisplayCreditWarningForExportForInvoice() {
        // given
        CartData cartData = mock(CartData.class);
        CartModel cart = mock(CartModel.class);
        DistPaymentModeData creditCardPayment = mock(DistPaymentModeData.class);
        DistPaymentModeData invoicePayment = mock(DistPaymentModeData.class);

        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(creditCardPayment.getCode()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(invoicePayment.getCode()).thenReturn(DistConstants.PaymentMethods.INVOICE);
        when(cartData.getValidPaymentModes()).thenReturn(Arrays.asList(creditCardPayment, invoicePayment));
        when(storeSessionFacade.isCurrentShopExport()).thenReturn(Boolean.TRUE);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);

        boolean result = distCheckoutFacade.shouldDisplayCreditWarningForExportShop(cartData);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testShouldAllowEditBillingForB2C() {
        // given
        CartModel cart = mock(CartModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        AddressModel billingAddress = mock(AddressModel.class);
        AddressData billingAddressData = mock(AddressData.class);
        distCheckoutFacade.setAddressConverter(addressConverter);

        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getUser()).thenReturn(customer);
        when(b2bUnitService.getParent(customer)).thenReturn(b2bUnit);
        when(b2bUnit.getBillingAddresses()).thenReturn(Collections.singletonList(billingAddress));

        when(storeSessionFacade.isCurrentShopExport()).thenReturn(Boolean.FALSE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(addressConverter.convert(billingAddress)).thenReturn(billingAddressData);

        boolean result = distCheckoutFacade.shouldAllowEditBilling(CustomerType.B2C);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testShouldAllowEditBillingForB2B() {
        // given
        CartModel cart = mock(CartModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        AddressModel billingAddress = mock(AddressModel.class);
        AddressData billingAddressData = mock(AddressData.class);
        distCheckoutFacade.setAddressConverter(addressConverter);

        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getUser()).thenReturn(customer);
        when(b2bUnitService.getParent(customer)).thenReturn(b2bUnit);
        when(b2bUnit.getBillingAddresses()).thenReturn(Collections.singletonList(billingAddress));

        when(storeSessionFacade.isCurrentShopExport()).thenReturn(Boolean.FALSE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(addressConverter.convert(billingAddress)).thenReturn(billingAddressData);

        boolean result = distCheckoutFacade.shouldAllowEditBilling(CustomerType.B2B);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testShouldAllowEditBillingForGuest() {
        // when
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.TRUE);
        boolean result = distCheckoutFacade.shouldAllowEditBilling(CustomerType.GUEST);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testShouldAllowEditBillingForExportShopGuest() {
        // given
        CartModel cart = mock(CartModel.class);

        // when
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.shouldAllowEditBilling(CustomerType.GUEST);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testShouldAllowEditBillingForB2E() {
        // given
        CartModel cart = mock(CartModel.class);

        // when
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.TRUE);
        boolean result = distCheckoutFacade.shouldAllowEditBilling(CustomerType.B2E);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testShouldAllowEditBillingForExportShopB2E() {
        // given
        CartModel cart = mock(CartModel.class);

        // when
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);

        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.shouldAllowEditBilling(CustomerType.B2E);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testShouldAllowEditBillingForB2BKey() {
        // given
        CartModel cart = mock(CartModel.class);

        // when
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(storeSessionFacade.isCurrentShopExport()).thenReturn(Boolean.FALSE);

        boolean result = distCheckoutFacade.shouldAllowEditBilling(CustomerType.B2B_KEY_ACCOUNT);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testShouldAllowEditBillingForExport() {
        // given
        CartModel cart = mock(CartModel.class);

        // when
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);

        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(storeSessionFacade.isCurrentShopExport()).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.shouldAllowEditBilling(CustomerType.B2B);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testShouldAllowEditBillingForExportFirstTimeCustomerB2B() {
        // given
        CartModel cart = mock(CartModel.class);

        // when
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(storeSessionFacade.isCurrentShopExport()).thenReturn(Boolean.TRUE);
        when(sessionService.getAttribute(DistConstants.Session.IS_NEW_REGISTRATION)).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.shouldAllowEditBilling(CustomerType.B2B);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testShouldAllowEditBillingForExportFirstTimeCustomerB2C() {
        // given
        CartModel cart = mock(CartModel.class);

        // when
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(storeSessionFacade.isCurrentShopExport()).thenReturn(Boolean.TRUE);
        when(sessionService.getAttribute(DistConstants.Session.IS_NEW_REGISTRATION)).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.shouldAllowEditBilling(CustomerType.B2C);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testShouldAllowEditBillingForMultipleBillingAddresses() {
        // given
        CartModel cart = mock(CartModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        AddressModel billingAddress = mock(AddressModel.class);
        AddressModel billingAddress2 = mock(AddressModel.class);
        AddressData billingAddressData = mock(AddressData.class);
        AddressData billingAddressData2 = mock(AddressData.class);
        distCheckoutFacade.setAddressConverter(addressConverter);

        // when
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getUser()).thenReturn(customer);
        when(b2bUnitService.getParent(customer)).thenReturn(b2bUnit);
        when(b2bUnit.getBillingAddresses()).thenReturn(Arrays.asList(billingAddress, billingAddress2));

        when(storeSessionFacade.isCurrentShopExport()).thenReturn(Boolean.FALSE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(distCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(addressConverter.convert(billingAddress)).thenReturn(billingAddressData);
        when(addressConverter.convert(billingAddress2)).thenReturn(billingAddressData2);
        boolean result = distCheckoutFacade.shouldAllowEditBilling(CustomerType.B2B);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsCreditCardPaymentAllowed() {
        // given
        CartData cart = mock(CartData.class);
        DistPaymentModeData creditCardPayment = mock(DistPaymentModeData.class);
        DistPaymentModeData invoicePayment = mock(DistPaymentModeData.class);

        // when
        when(creditCardPayment.getCreditCardPayment()).thenReturn(Boolean.TRUE);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);

        when(cart.getValidPaymentModes()).thenReturn(Arrays.asList(creditCardPayment, invoicePayment));

        boolean result = distCheckoutFacade.isCreditCardPaymentAllowed(cart);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsCreditCardPaymentAllowedForB2E() {
        // given
        CartData cart = mock(CartData.class);
        DistPaymentModeData creditCardPayment = mock(DistPaymentModeData.class);
        DistPaymentModeData invoicePayment = mock(DistPaymentModeData.class);

        // when
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.TRUE);
        when(cart.getValidPaymentModes()).thenReturn(Arrays.asList(creditCardPayment, invoicePayment));

        boolean result = distCheckoutFacade.isCreditCardPaymentAllowed(cart);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsCreditCardPaymentAllowedWithNoPaymentModes() {
        // given
        CartData cart = mock(CartData.class);

        // when
        boolean result = distCheckoutFacade.isCreditCardPaymentAllowed(cart);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsCodiceDestinatarioShownForItalyAndB2B() {
        // given
        CountryData countryData = mock(CountryData.class);
        CustomerData customerData = mock(CustomerData.class);

        // when
        when(customerData.getCustomerType()).thenReturn(CustomerType.B2B);
        when(countryData.getIsocode()).thenReturn(DistConstants.CountryIsoCode.ITALY);
        when(storeSessionFacade.getCurrentCountry()).thenReturn(countryData);

        boolean result = distCheckoutFacade.isCodiceDestinatarioShown(customerData);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsCodiceDestinatarioShownForItalyAndB2BKey() {
        // given
        CountryData countryData = mock(CountryData.class);
        CustomerData customerData = mock(CustomerData.class);

        // when
        when(customerData.getCustomerType()).thenReturn(CustomerType.B2B_KEY_ACCOUNT);
        when(countryData.getIsocode()).thenReturn(DistConstants.CountryIsoCode.ITALY);
        when(storeSessionFacade.getCurrentCountry()).thenReturn(countryData);

        boolean result = distCheckoutFacade.isCodiceDestinatarioShown(customerData);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsCodiceDestinatarioShownForItalyAndB2E() {
        // given
        CountryData countryData = mock(CountryData.class);
        CustomerData customerData = mock(CustomerData.class);

        // when
        when(customerData.getCustomerType()).thenReturn(CustomerType.B2B);
        when(countryData.getIsocode()).thenReturn(DistConstants.CountryIsoCode.ITALY);
        when(storeSessionFacade.getCurrentCountry()).thenReturn(countryData);

        boolean result = distCheckoutFacade.isCodiceDestinatarioShown(customerData);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsCodiceDestinatarioShownForGermany() {
        // given
        CountryData countryData = mock(CountryData.class);
        CustomerData customerData = mock(CustomerData.class);

        // when
        when(countryData.getIsocode()).thenReturn(DistConstants.CountryIsoCode.GERMANY);
        when(storeSessionFacade.getCurrentCountry()).thenReturn(countryData);

        boolean result = distCheckoutFacade.isCodiceDestinatarioShown(customerData);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsCodiceDestinatarioShownForItalyAndB2C() {
        // given
        CountryData countryData = mock(CountryData.class);
        CustomerData customerData = mock(CustomerData.class);

        // when
        when(customerData.getCustomerType()).thenReturn(CustomerType.B2C);
        when(countryData.getIsocode()).thenReturn(DistConstants.CountryIsoCode.ITALY);
        when(storeSessionFacade.getCurrentCountry()).thenReturn(countryData);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);

        boolean result = distCheckoutFacade.isCodiceDestinatarioShown(customerData);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testisDeliveryModePriceVisible() {
        // given
        CMSSiteModel site = mock((CMSSiteModel.class));

        // when
        when(site.getCheckoutDeliveryMethodPricesShown()).thenReturn(Boolean.TRUE);
        when(cmsSiteService.getCurrentSite()).thenReturn(site);
        boolean result = distCheckoutFacade.isDeliveryModePriceVisible();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testisDeliveryModePriceNotVisible() {
        // given
        CMSSiteModel site = mock((CMSSiteModel.class));

        // when
        when(site.getCheckoutDeliveryMethodPricesShown()).thenReturn(Boolean.FALSE);
        when(cmsSiteService.getCurrentSite()).thenReturn(site);
        boolean result = distCheckoutFacade.isDeliveryModePriceVisible();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testHasUnallowedBackordersWhenNullCartReturnFalse() {
        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(false);

        boolean result = distCheckoutFacade.hasUnallowedBackorders();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testHasUnallowedBackordersBackOrderNotAllowedInCurrentStoreReturnFalse() {
        // given
        CartModel cart = mock(CartModel.class);
        BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);

        // when
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(baseStoreModel.getBackorderAllowed()).thenReturn(false);
        when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);

        boolean result = distCheckoutFacade.hasUnallowedBackorders();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testHasUnallowedBackordersNoUnallowedBackordersReturnFalse() {
        // given
        CartModel cart = mock(CartModel.class);
        BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);

        // when
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(entry1.getIsBackOrderProfitable()).thenReturn(true);
        when(entry2.getIsBackOrderProfitable()).thenReturn(true);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(baseStoreModel.getBackorderAllowed()).thenReturn(true);
        when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);

        boolean result = distCheckoutFacade.hasUnallowedBackorders();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testHasUnallowedBackordersWhenUnallowedBackordersReturnTrue() {
        // given
        CartModel cart = mock(CartModel.class);
        BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);

        // when
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(entry1.getIsBackOrderProfitable()).thenReturn(false);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(baseStoreModel.getBackorderAllowed()).thenReturn(true);
        when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);

        boolean result = distCheckoutFacade.hasUnallowedBackorders();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsSupportedExportCountry() {
        // given
        BaseStoreModel baseStore = mock(BaseStoreModel.class);
        CountryModel cyprus = mock(CountryModel.class);
        CountryModel croatia = mock(CountryModel.class);

        // when
        when(croatia.getIsocode()).thenReturn("HR");
        when(cyprus.getIsocode()).thenReturn("CY");
        when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
        when(baseStore.getRegisterCountries()).thenReturn(Arrays.asList(cyprus, croatia));

        boolean result = distCheckoutFacade.isSupportedExportCountry("HR");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsNotSupportedExportCountry() {
        // given
        BaseStoreModel baseStore = mock(BaseStoreModel.class);
        CountryModel cyprus = mock(CountryModel.class);
        CountryModel croatia = mock(CountryModel.class);

        // when
        when(croatia.getIsocode()).thenReturn("HR");
        when(cyprus.getIsocode()).thenReturn("CY");
        when(baseStore.getRegisterCountries()).thenReturn(Arrays.asList(cyprus, croatia));
        when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);

        boolean result = distCheckoutFacade.isSupportedExportCountry("XI");

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsPickupAllowedForCurrentCartWhenCartNull() {
        // when

        boolean result = distCheckoutFacade.isPickupAllowedForCurrentCart();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsPickupAllowedForCurrentCart() {
        // given
        CartModel cart = mock(CartModel.class);

        // when
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(distCartService.isWaldom(cart)).thenReturn(Boolean.FALSE);

        boolean result = distCheckoutFacade.isPickupAllowedForCurrentCart();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsPickupAllowedForCurrentCartForB2ECustomer() {
        // given
        CartModel cart = mock(CartModel.class);

        // when
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(sessionService.getOrLoadAttribute(anyString(), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.isPickupAllowedForCurrentCart();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsPickupAllowedForCurrentCartForWaldom() {
        // given
        CartModel cart = mock(CartModel.class);

        // when
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(false);
        when(distCartService.isWaldom(cart)).thenReturn(Boolean.TRUE);
        when(storeSessionFacade.isCurrentShopExport()).thenReturn(Boolean.FALSE);

        boolean result = distCheckoutFacade.isPickupAllowedForCurrentCart();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsPickupAllowedForCurrentCartForRs() {
        // given
        CartModel cart = mock(CartModel.class);

        // when
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(false);
        when(distCartService.isRs(cart)).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.isPickupAllowedForCurrentCart();

        // then
        assertThat(result, is(Boolean.FALSE));
    }

    @Test
    public void testIsPickupAllowedForCurrentCartForExport() {
        // given
        CartModel cart = mock(CartModel.class);

        // when
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(false);
        when(distCartService.isWaldom(cart)).thenReturn(Boolean.FALSE);

        boolean result = distCheckoutFacade.isPickupAllowedForCurrentCart();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsPickupAllowedForCurrentCartForWaldomAndExport() {
        // given
        CartModel cart = mock(CartModel.class);

        // when
        when(distCheckoutFacade.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(distCartService.isWaldom(cart)).thenReturn(Boolean.TRUE);
        when(storeSessionFacade.isCurrentShopExport()).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.isPickupAllowedForCurrentCart();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testCartIsNullFastCheckoutNotValid() {
        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(false);

        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));

    }

    @Test
    public void testCartEntriesNotExistFastCheckoutNotValid() {
        // given
        CartModel cart = mock(CartModel.class);

        // when
        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));

    }

    @Test
    public void testUserIsAnonymousFastCheckoutNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);

        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testCustomerTypeGuestFastCheckoutNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);

        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(cart.getUser()).thenReturn(customer);
        when(customer.getCustomerType()).thenReturn(CustomerType.GUEST);

        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testCurrentUserB2EFastCheckoutNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);

        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(cart.getUser()).thenReturn(customer);
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testCurrentUserWithoutDefaultPaymentAddressFastCheckoutNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);

        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(cart.getUser()).thenReturn(customer);
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customer);
        when(customer.getDefaultPaymentAddress()).thenReturn(null);

        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testCurrentUserWithoutBillingAddressFastCheckoutNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        AddressModel addressModel = mock(AddressModel.class);

        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(cart.getUser()).thenReturn(customer);
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customer);
        when(customer.getDefaultPaymentAddress()).thenReturn(addressModel);
        when(customer.getDefaultPaymentAddress().getBillingAddress()).thenReturn(Boolean.FALSE);

        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testSapValidDeliveryModesChangedFastCheckoutNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        AddressModel addressModel = mock(AddressModel.class);
        AbstractDistDeliveryModeModel abstractDistDeliveryModeModel = mock(AbstractDistDeliveryModeModel.class);

        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(cart.getUser()).thenReturn(customer);
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customer);
        when(customer.getDefaultPaymentAddress()).thenReturn(addressModel);
        when(customer.getDefaultPaymentAddress().getBillingAddress()).thenReturn(Boolean.TRUE);
        when(cart.getValidDeliveryModes()).thenReturn(List.of(abstractDistDeliveryModeModel));
        when(abstractDistDeliveryModeModel.getCode()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(customer.getDefaultDeliveryMethod()).thenReturn(DistConstants.Shipping.METHOD_ECONOMY);

        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testSapValidPaymentModesChangedFastCheckoutNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        AddressModel addressModel = mock(AddressModel.class);
        AbstractDistDeliveryModeModel abstractDistDeliveryModeModel = mock(AbstractDistDeliveryModeModel.class);
        AbstractDistPaymentModeModel abstractDistPaymentModeModel = mock(AbstractDistPaymentModeModel.class);

        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(cart.getUser()).thenReturn(customer);
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customer);
        when(customer.getDefaultPaymentAddress()).thenReturn(addressModel);
        when(customer.getDefaultPaymentAddress().getBillingAddress()).thenReturn(Boolean.TRUE);
        when(cart.getValidDeliveryModes()).thenReturn(List.of(abstractDistDeliveryModeModel));
        when(abstractDistDeliveryModeModel.getCode()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(customer.getDefaultDeliveryMethod()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(cart.getValidPaymentModes()).thenReturn(List.of(abstractDistPaymentModeModel));
        when(customer.getDefaultPaymentMethod()).thenReturn(DistConstants.PaymentMethods.PAY_PAL);
        when(abstractDistPaymentModeModel.getCode()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);

        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testDefaultShipmentAddressNotExistFastCheckoutNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        AddressModel addressModel = mock(AddressModel.class);
        AbstractDistDeliveryModeModel abstractDistDeliveryModeModel = mock(AbstractDistDeliveryModeModel.class);
        AbstractDistPaymentModeModel abstractDistPaymentModeModel = mock(AbstractDistPaymentModeModel.class);

        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(cart.getUser()).thenReturn(customer);
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customer);
        when(customer.getDefaultPaymentAddress()).thenReturn(addressModel);
        when(customer.getDefaultPaymentAddress().getBillingAddress()).thenReturn(Boolean.TRUE);
        when(cart.getValidDeliveryModes()).thenReturn(List.of(abstractDistDeliveryModeModel));
        when(abstractDistDeliveryModeModel.getCode()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(customer.getDefaultDeliveryMethod()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(cart.getValidPaymentModes()).thenReturn(List.of(abstractDistPaymentModeModel));
        when(customer.getDefaultPaymentMethod()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(abstractDistPaymentModeModel.getCode()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(customer.getDefaultShipmentAddress()).thenReturn(null);

        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testShippingAddressNotExistFastCheckoutNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        AddressModel addressModel = mock(AddressModel.class);
        AbstractDistDeliveryModeModel abstractDistDeliveryModeModel = mock(AbstractDistDeliveryModeModel.class);
        AbstractDistPaymentModeModel abstractDistPaymentModeModel = mock(AbstractDistPaymentModeModel.class);
        AddressModel shipmentAddress = mock(AddressModel.class);

        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(cart.getUser()).thenReturn(customer);
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customer);
        when(customer.getDefaultPaymentAddress()).thenReturn(addressModel);
        when(customer.getDefaultPaymentAddress().getBillingAddress()).thenReturn(Boolean.TRUE);
        when(cart.getValidDeliveryModes()).thenReturn(List.of(abstractDistDeliveryModeModel));
        when(abstractDistDeliveryModeModel.getCode()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(customer.getDefaultDeliveryMethod()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(cart.getValidPaymentModes()).thenReturn(List.of(abstractDistPaymentModeModel));
        when(customer.getDefaultPaymentMethod()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(abstractDistPaymentModeModel.getCode()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(customer.getDefaultShipmentAddress()).thenReturn(shipmentAddress);
        when(shipmentAddress.getShippingAddress()).thenReturn(Boolean.FALSE);

        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testDefaultPaymentModeNotExistFastCheckoutNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        AddressModel addressModel = mock(AddressModel.class);
        AbstractDistDeliveryModeModel abstractDistDeliveryModeModel = mock(AbstractDistDeliveryModeModel.class);
        AbstractDistPaymentModeModel abstractDistPaymentModeModel = mock(AbstractDistPaymentModeModel.class);
        AddressModel shipmentAddress = mock(AddressModel.class);

        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(cart.getUser()).thenReturn(customer);
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customer);
        when(customer.getDefaultPaymentAddress()).thenReturn(addressModel);
        when(customer.getDefaultPaymentAddress().getBillingAddress()).thenReturn(Boolean.TRUE);
        when(cart.getValidDeliveryModes()).thenReturn(List.of(abstractDistDeliveryModeModel));
        when(abstractDistDeliveryModeModel.getCode()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(customer.getDefaultDeliveryMethod()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(customer.getDefaultPaymentMethod()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(customer.getDefaultShipmentAddress()).thenReturn(shipmentAddress);
        when(shipmentAddress.getShippingAddress()).thenReturn(Boolean.TRUE);
        when(customer.getDefaultPaymentMethod()).thenReturn(null);

        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testDefaultPaymentInfoNotExistFastCheckoutNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        AddressModel addressModel = mock(AddressModel.class);
        AbstractDistDeliveryModeModel abstractDistDeliveryModeModel = mock(AbstractDistDeliveryModeModel.class);
        AbstractDistPaymentModeModel abstractDistPaymentModeModel = mock(AbstractDistPaymentModeModel.class);
        AddressModel shipmentAddress = mock(AddressModel.class);

        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(cart.getUser()).thenReturn(customer);
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customer);
        when(customer.getDefaultPaymentAddress()).thenReturn(addressModel);
        when(customer.getDefaultPaymentAddress().getBillingAddress()).thenReturn(Boolean.TRUE);
        when(cart.getValidDeliveryModes()).thenReturn(List.of(abstractDistDeliveryModeModel));
        when(abstractDistDeliveryModeModel.getCode()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(customer.getDefaultDeliveryMethod()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(cart.getValidPaymentModes()).thenReturn(List.of(abstractDistPaymentModeModel));
        when(customer.getDefaultPaymentMethod()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(abstractDistPaymentModeModel.getCode()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(customer.getDefaultShipmentAddress()).thenReturn(shipmentAddress);
        when(shipmentAddress.getShippingAddress()).thenReturn(Boolean.TRUE);
        when(customer.getDefaultPaymentInfo()).thenReturn(null);

        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testDefaultPaymentInfoNotValidFastCheckoutNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        AddressModel addressModel = mock(AddressModel.class);
        AbstractDistDeliveryModeModel abstractDistDeliveryModeModel = mock(AbstractDistDeliveryModeModel.class);
        AbstractDistPaymentModeModel abstractDistPaymentModeModel = mock(AbstractDistPaymentModeModel.class);
        AddressModel shipmentAddress = mock(AddressModel.class);
        CreditCardPaymentInfoModel creditCardPaymentInfoModel = mock(CreditCardPaymentInfoModel.class);

        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(cart.getUser()).thenReturn(customer);
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customer);
        when(customer.getDefaultPaymentAddress()).thenReturn(addressModel);
        when(customer.getDefaultPaymentAddress().getBillingAddress()).thenReturn(Boolean.TRUE);
        when(cart.getValidDeliveryModes()).thenReturn(List.of(abstractDistDeliveryModeModel));
        when(abstractDistDeliveryModeModel.getCode()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(customer.getDefaultDeliveryMethod()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(cart.getValidPaymentModes()).thenReturn(List.of(abstractDistPaymentModeModel));
        when(customer.getDefaultPaymentMethod()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(abstractDistPaymentModeModel.getCode()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(customer.getDefaultShipmentAddress()).thenReturn(shipmentAddress);
        when(shipmentAddress.getShippingAddress()).thenReturn(Boolean.TRUE);
        when(customer.getDefaultPaymentInfo()).thenReturn(creditCardPaymentInfoModel);
        when(distCardPaymentService.isCreditCardExpiryDateValid(any(CreditCardPaymentInfoModel.class))).thenReturn(false);

        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testDefaultDeliveryMethodNotExistFastCheckoutNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        AddressModel addressModel = mock(AddressModel.class);
        AbstractDistDeliveryModeModel abstractDistDeliveryModeModel = mock(AbstractDistDeliveryModeModel.class);
        AbstractDistPaymentModeModel abstractDistPaymentModeModel = mock(AbstractDistPaymentModeModel.class);
        AddressModel shipmentAddress = mock(AddressModel.class);
        CreditCardPaymentInfoModel paymentInfoModel = mock(CreditCardPaymentInfoModel.class);

        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(cart.getUser()).thenReturn(customer);
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customer);
        when(customer.getDefaultPaymentAddress()).thenReturn(addressModel);
        when(customer.getDefaultPaymentAddress().getBillingAddress()).thenReturn(Boolean.TRUE);
        when(customer.getDefaultDeliveryMethod()).thenReturn(null);
        when(cart.getValidPaymentModes()).thenReturn(List.of(abstractDistPaymentModeModel));
        when(customer.getDefaultPaymentMethod()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(abstractDistPaymentModeModel.getCode()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(customer.getDefaultShipmentAddress()).thenReturn(shipmentAddress);
        when(shipmentAddress.getShippingAddress()).thenReturn(Boolean.TRUE);
        when(distCardPaymentService.isCreditCardExpiryDateValid(any(CreditCardPaymentInfoModel.class))).thenReturn(true);
        when(customer.getDefaultPaymentInfo()).thenReturn(paymentInfoModel);

        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testDefaultDeliveryMethodPickupAndNoPickupWarehousesAreAvailableFastCheckoutNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        AddressModel addressModel = mock(AddressModel.class);
        AbstractDistDeliveryModeModel abstractDistDeliveryModeModel = mock(AbstractDistDeliveryModeModel.class);
        AbstractDistPaymentModeModel abstractDistPaymentModeModel = mock(AbstractDistPaymentModeModel.class);
        AddressModel shipmentAddress = mock(AddressModel.class);
        CreditCardPaymentInfoModel paymentInfoModel = mock(CreditCardPaymentInfoModel.class);
        CMSSiteModel site = mock(CMSSiteModel.class);
        B2BUnitModel unit = mock(B2BUnitModel.class);

        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(cart.getUser()).thenReturn(customer);
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customer);
        when(customer.getDefaultPaymentAddress()).thenReturn(addressModel);
        when(customer.getDefaultPaymentAddress().getBillingAddress()).thenReturn(Boolean.TRUE);
        when(cart.getValidDeliveryModes()).thenReturn(List.of(abstractDistDeliveryModeModel));
        when(abstractDistDeliveryModeModel.getCode()).thenReturn(DistConstants.Shipping.METHOD_PICKUP);
        when(customer.getDefaultDeliveryMethod()).thenReturn(DistConstants.Shipping.METHOD_PICKUP);
        when(cart.getValidPaymentModes()).thenReturn(List.of(abstractDistPaymentModeModel));
        when(customer.getDefaultPaymentMethod()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(abstractDistPaymentModeModel.getCode()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(customer.getDefaultShipmentAddress()).thenReturn(shipmentAddress);
        when(shipmentAddress.getShippingAddress()).thenReturn(Boolean.TRUE);
        when(distCardPaymentService.isCreditCardExpiryDateValid(any(CreditCardPaymentInfoModel.class))).thenReturn(true);
        when(customer.getDefaultPaymentInfo()).thenReturn(paymentInfoModel);
        when(shippingOptionService.getPickupDeliveryModeCode()).thenReturn(DistConstants.Shipping.METHOD_PICKUP);
        when(cmsSiteService.getCurrentSite()).thenReturn(site);
        when(site.getPickupWarehouses()).thenReturn(Collections.emptySet());

        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testNotValidDefaultBillingAddressFastCheckoutNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        B2BUnitModel unit = mock(B2BUnitModel.class);
        AddressModel addressModel = mock(AddressModel.class);
        AbstractDistDeliveryModeModel abstractDistDeliveryModeModel = mock(AbstractDistDeliveryModeModel.class);
        AbstractDistPaymentModeModel abstractDistPaymentModeModel = mock(AbstractDistPaymentModeModel.class);
        AddressModel shipmentAddress = mock(AddressModel.class);
        CreditCardPaymentInfoModel paymentInfoModel = mock(CreditCardPaymentInfoModel.class);
        CMSSiteModel site = mock(CMSSiteModel.class);
        WarehouseModel warehouseModel = mock(WarehouseModel.class);

        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(cart.getUser()).thenReturn(customer);
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(b2bUnitService.getParent(customer)).thenReturn(unit);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customer);
        when(customer.getDefaultPaymentAddress()).thenReturn(addressModel);
        when(customer.getDefaultPaymentAddress().getBillingAddress()).thenReturn(Boolean.TRUE);
        when(cart.getValidDeliveryModes()).thenReturn(List.of(abstractDistDeliveryModeModel));
        when(abstractDistDeliveryModeModel.getCode()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(customer.getDefaultDeliveryMethod()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(cart.getValidPaymentModes()).thenReturn(List.of(abstractDistPaymentModeModel));
        when(customer.getDefaultPaymentMethod()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(abstractDistPaymentModeModel.getCode()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(customer.getDefaultShipmentAddress()).thenReturn(shipmentAddress);
        when(shipmentAddress.getShippingAddress()).thenReturn(Boolean.TRUE);
        when(distCardPaymentService.isCreditCardExpiryDateValid(any(CreditCardPaymentInfoModel.class))).thenReturn(true);
        when(customer.getDefaultPaymentInfo()).thenReturn(paymentInfoModel);
        when(shippingOptionService.getPickupDeliveryModeCode()).thenReturn(DistConstants.Shipping.METHOD_PICKUP);
        when(distAddressService.isAddressValid(addressModel)).thenReturn(Boolean.FALSE);

        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testBlockedDefaultBillingAddressFastCheckoutNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        AddressModel addressModel = mock(AddressModel.class);
        AbstractDistDeliveryModeModel abstractDistDeliveryModeModel = mock(AbstractDistDeliveryModeModel.class);
        AbstractDistPaymentModeModel abstractDistPaymentModeModel = mock(AbstractDistPaymentModeModel.class);
        AddressModel shipmentAddress = mock(AddressModel.class);
        CreditCardPaymentInfoModel paymentInfoModel = mock(CreditCardPaymentInfoModel.class);
        CMSSiteModel site = mock(CMSSiteModel.class);
        WarehouseModel warehouseModel = mock(WarehouseModel.class);
        DistBlockedUserAddressRuleModel distBlockedUserAddressRuleModel = mock(DistBlockedUserAddressRuleModel.class);
        B2BUnitModel unit = mock(B2BUnitModel.class);
        CountryModel country = mock(CountryModel.class);

        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(cart.getUser()).thenReturn(customer);
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(b2bUnitService.getParent(customer)).thenReturn(unit);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customer);
        when(addressModel.getPostalcode()).thenReturn("Postal");
        when(addressModel.getStreetname()).thenReturn("StreetName");
        when(addressModel.getStreetnumber()).thenReturn("1");
        when(addressModel.getTown()).thenReturn("Town");
        when(country.getIsocode()).thenReturn("CH");
        when(addressModel.getCountry()).thenReturn(country);
        when(customer.getDefaultPaymentAddress()).thenReturn(addressModel);
        when(customer.getDefaultPaymentAddress().getBillingAddress()).thenReturn(Boolean.TRUE);
        when(cart.getValidDeliveryModes()).thenReturn(List.of(abstractDistDeliveryModeModel));
        when(abstractDistDeliveryModeModel.getCode()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(customer.getDefaultDeliveryMethod()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(cart.getValidPaymentModes()).thenReturn(List.of(abstractDistPaymentModeModel));
        when(customer.getDefaultPaymentMethod()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(abstractDistPaymentModeModel.getCode()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(customer.getDefaultShipmentAddress()).thenReturn(shipmentAddress);
        when(shipmentAddress.getShippingAddress()).thenReturn(Boolean.TRUE);
        when(distCardPaymentService.isCreditCardExpiryDateValid(any(CreditCardPaymentInfoModel.class))).thenReturn(true);
        when(customer.getDefaultPaymentInfo()).thenReturn(paymentInfoModel);
        when(shippingOptionService.getPickupDeliveryModeCode()).thenReturn(DistConstants.Shipping.METHOD_PICKUP);
        when(distAddressService.isAddressValid(addressModel)).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testNotValidDefaultShippingAddressFastCheckoutNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        AddressModel addressModel = mock(AddressModel.class);
        AbstractDistDeliveryModeModel abstractDistDeliveryModeModel = mock(AbstractDistDeliveryModeModel.class);
        AbstractDistPaymentModeModel abstractDistPaymentModeModel = mock(AbstractDistPaymentModeModel.class);
        AddressModel shipmentAddress = mock(AddressModel.class);
        CreditCardPaymentInfoModel paymentInfoModel = mock(CreditCardPaymentInfoModel.class);
        CMSSiteModel site = mock(CMSSiteModel.class);
        WarehouseModel warehouseModel = mock(WarehouseModel.class);
        B2BUnitModel unit = mock(B2BUnitModel.class);

        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(cart.getUser()).thenReturn(customer);
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(b2bUnitService.getParent(customer)).thenReturn(unit);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customer);
        when(customer.getDefaultPaymentAddress()).thenReturn(addressModel);
        when(customer.getDefaultPaymentAddress().getBillingAddress()).thenReturn(Boolean.TRUE);
        when(cart.getValidDeliveryModes()).thenReturn(List.of(abstractDistDeliveryModeModel));
        when(abstractDistDeliveryModeModel.getCode()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(customer.getDefaultDeliveryMethod()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(cart.getValidPaymentModes()).thenReturn(List.of(abstractDistPaymentModeModel));
        when(customer.getDefaultPaymentMethod()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(abstractDistPaymentModeModel.getCode()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(customer.getDefaultShipmentAddress()).thenReturn(shipmentAddress);
        when(shipmentAddress.getShippingAddress()).thenReturn(Boolean.TRUE);
        when(distCardPaymentService.isCreditCardExpiryDateValid(any(CreditCardPaymentInfoModel.class))).thenReturn(true);
        when(customer.getDefaultPaymentInfo()).thenReturn(paymentInfoModel);
        when(shippingOptionService.getPickupDeliveryModeCode()).thenReturn(DistConstants.Shipping.METHOD_PICKUP);
        when(distAddressService.isAddressValid(addressModel)).thenReturn(Boolean.TRUE);
        when(distAddressService.isAddressValid(shipmentAddress)).thenReturn(Boolean.FALSE);

        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testBlockedDefaultShippingAddressFastCheckoutNotValid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        AddressModel addressModel = mock(AddressModel.class);
        AbstractDistDeliveryModeModel abstractDistDeliveryModeModel = mock(AbstractDistDeliveryModeModel.class);
        AbstractDistPaymentModeModel abstractDistPaymentModeModel = mock(AbstractDistPaymentModeModel.class);
        AddressModel shipmentAddress = mock(AddressModel.class);
        CreditCardPaymentInfoModel paymentInfoModel = mock(CreditCardPaymentInfoModel.class);
        CMSSiteModel site = mock(CMSSiteModel.class);
        WarehouseModel warehouseModel = mock(WarehouseModel.class);
        B2BUnitModel unit = mock(B2BUnitModel.class);
        CountryModel countryModel = mock(CountryModel.class);
        CountryModel country = mock(CountryModel.class);


        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(cart.getUser()).thenReturn(customer);
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(b2bUnitService.getParent(customer)).thenReturn(unit);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customer);
        when(customer.getDefaultPaymentAddress()).thenReturn(addressModel);
        when(customer.getDefaultPaymentAddress().getBillingAddress()).thenReturn(Boolean.TRUE);
        when(cart.getValidDeliveryModes()).thenReturn(List.of(abstractDistDeliveryModeModel));
        when(abstractDistDeliveryModeModel.getCode()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(customer.getDefaultDeliveryMethod()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(cart.getValidPaymentModes()).thenReturn(List.of(abstractDistPaymentModeModel));
        when(customer.getDefaultPaymentMethod()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(abstractDistPaymentModeModel.getCode()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(shipmentAddress.getPostalcode()).thenReturn("Postal");
        when(shipmentAddress.getStreetname()).thenReturn("StreetName");
        when(shipmentAddress.getStreetnumber()).thenReturn("1");
        when(shipmentAddress.getTown()).thenReturn("Town");
        when(shipmentAddress.getCountry()).thenReturn(country);

        when(customer.getDefaultShipmentAddress()).thenReturn(shipmentAddress);
        when(shipmentAddress.getShippingAddress()).thenReturn(Boolean.TRUE);
        when(distCardPaymentService.isCreditCardExpiryDateValid(any(CreditCardPaymentInfoModel.class))).thenReturn(true);
        when(customer.getDefaultPaymentInfo()).thenReturn(paymentInfoModel);
        when(shippingOptionService.getPickupDeliveryModeCode()).thenReturn(DistConstants.Shipping.METHOD_PICKUP);
        when(distAddressService.isAddressValid(addressModel)).thenReturn(Boolean.TRUE);
        when(distAddressService.isAddressValid(shipmentAddress)).thenReturn(Boolean.TRUE);
        when(shipmentAddress.getPostalcode()).thenReturn("7000");
        when(shipmentAddress.getStreetname()).thenReturn("Yellow Street");
        when(shipmentAddress.getStreetnumber()).thenReturn("100");
        when(shipmentAddress.getTown()).thenReturn("Chur");
        when(shipmentAddress.getCountry()).thenReturn(countryModel);
        when(countryModel.getIsocode()).thenReturn("CH");
        doReturn(true).when(distCheckoutFacade).isUserAddressBlocked("7000", "Yellow Street", "100", "Chur", "CH");


        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testFastCheckoutIsValid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        AddressModel addressModel = mock(AddressModel.class);
        AbstractDistDeliveryModeModel abstractDistDeliveryModeModel = mock(AbstractDistDeliveryModeModel.class);
        AbstractDistPaymentModeModel abstractDistPaymentModeModel = mock(AbstractDistPaymentModeModel.class);
        AddressModel shipmentAddress = mock(AddressModel.class);
        CreditCardPaymentInfoModel paymentInfoModel = mock(CreditCardPaymentInfoModel.class);
        CMSSiteModel site = mock(CMSSiteModel.class);
        WarehouseModel warehouseModel = mock(WarehouseModel.class);
        B2BUnitModel unit = mock(B2BUnitModel.class);

        // when
        when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cart);
        when(cart.getEntries()).thenReturn(List.of(entry1, entry2));
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
        when(cart.getUser()).thenReturn(customer);
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(sessionService.getOrLoadAttribute(eq("isEShopGroup"), any(SessionService.SessionAttributeLoader.class))).thenReturn(Boolean.FALSE);
        when(b2bUnitService.getParent(customer)).thenReturn(unit);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customer);
        when(customer.getDefaultPaymentAddress()).thenReturn(addressModel);
        when(customer.getDefaultPaymentAddress().getBillingAddress()).thenReturn(Boolean.TRUE);
        when(cart.getValidDeliveryModes()).thenReturn(List.of(abstractDistDeliveryModeModel));
        when(abstractDistDeliveryModeModel.getCode()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(customer.getDefaultDeliveryMethod()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(cart.getValidPaymentModes()).thenReturn(List.of(abstractDistPaymentModeModel));
        when(customer.getDefaultPaymentMethod()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(abstractDistPaymentModeModel.getCode()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(customer.getDefaultShipmentAddress()).thenReturn(shipmentAddress);
        when(shipmentAddress.getShippingAddress()).thenReturn(Boolean.TRUE);
        when(distCardPaymentService.isCreditCardExpiryDateValid(any(CreditCardPaymentInfoModel.class))).thenReturn(true);
        when(customer.getDefaultPaymentInfo()).thenReturn(paymentInfoModel);
        when(shippingOptionService.getPickupDeliveryModeCode()).thenReturn(DistConstants.Shipping.METHOD_PICKUP);
        when(distAddressService.isAddressValid(addressModel)).thenReturn(Boolean.TRUE);
        when(distAddressService.isAddressValid(shipmentAddress)).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.isValidFastCheckout();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testGetPaymentModeForCode() {
        // given
        PaymentModeModel paymentModeModel = mock(PaymentModeModel.class);
        DistPaymentModeData distPaymentModeData = mock(DistPaymentModeData.class);

        // when
        when(paymentModeService.getPaymentModeForCode(DistConstants.PaymentMethods.PAY_PAL)).thenReturn(paymentModeModel);
        when(paymentModeConverter.convert(paymentModeModel)).thenReturn(distPaymentModeData);

        DistPaymentModeData result = distCheckoutFacade.getPaymentModeForCode(DistConstants.PaymentMethods.PAY_PAL);

        // then
        assertThat(result, equalTo(distPaymentModeData));
    }

    @Test
    public void testGetCreditCardPaymentModeWhenNoPaymentModesSupported() {
        // given
        CartModel cartModel = mock(CartModel.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();
        when(paymentOptionService.getSupportedPaymentOptions(cartModel, false)).thenReturn(Collections.emptyList());

        // when
        DistPaymentModeData result = distCheckoutFacade.getCreditCardPaymentMode();

        // then
        assertThat(result, is(nullValue()));
    }

    @Test
    public void testGetCreditCardPaymentModeNoCreditCardPaymentMode() {
        // given
        CartModel cartModel = mock(CartModel.class);
        AbstractDistPaymentModeModel paymentMode = mock(AbstractDistPaymentModeModel.class);
        ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);

        doReturn(cartModel).when(distCheckoutFacade).getCart();
        when(paymentMode.getPaymentInfoType()).thenReturn(composedTypeModel);
        when(paymentMode.getPaymentInfoType().getCode()).thenReturn(AdvancePaymentInfoModel._TYPECODE);
        when(paymentOptionService.getSupportedPaymentOptions(cartModel, false)).thenReturn(List.of((paymentMode)));

        // when
        DistPaymentModeData result = distCheckoutFacade.getCreditCardPaymentMode();

        // then
        assertThat(result, is(nullValue()));
    }

    @Test
    public void testGetCreditCardPaymentModeWhenCreditCardPaymentModeExists() {
        // given
        CartModel cartModel = mock(CartModel.class);
        AbstractDistPaymentModeModel paymentMode = mock(AbstractDistPaymentModeModel.class);
        DistPaymentModeData distPaymentModeData = mock(DistPaymentModeData.class);
        ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();
        when(paymentMode.getPaymentInfoType()).thenReturn(composedTypeModel);
        when(paymentMode.getPaymentInfoType().getCode()).thenReturn(CreditCardPaymentInfoModel._TYPECODE);
        when(paymentOptionService.getSupportedPaymentOptions(cartModel, false)).thenReturn(List.of(paymentMode));
        when(paymentModeConverter.convert(paymentMode)).thenReturn(distPaymentModeData);

        DistPaymentModeData result = distCheckoutFacade.getCreditCardPaymentMode();

        // then
        assertThat(result, is(distPaymentModeData));
    }

    @Test
    public void testSetPaymentModeWhenPaymentModeNotFound() {
        // given
        CartModel cartModel = mock(CartModel.class);
        DistPaymentModeData paymentModeData = mock(DistPaymentModeData.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();
        when(paymentModeData.getCode()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(paymentModeService.getPaymentModeForCode(paymentModeData.getCode())).thenReturn(null);

        boolean result = distCheckoutFacade.setPaymentMode(paymentModeData);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testSetPaymentModeSuccess() {
        // given
        DistPaymentModeData paymentModeData = mock(DistPaymentModeData.class);
        PaymentModeModel paymentModeModel = mock(PaymentModeModel.class);
        CartModel cartModel = mock(CartModel.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();
        when(paymentModeData.getCode()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(paymentModeService.getPaymentModeForCode(paymentModeData.getCode())).thenReturn(paymentModeModel);
        when(distCommerceCheckoutService.setPaymentMode(cartModel, paymentModeModel)).thenReturn(true);

        boolean result = distCheckoutFacade.setPaymentMode(paymentModeData);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testCalculateOrderWithSimulationTrue() throws CalculationException {
        // given
        CartModel cartModel = mock(CartModel.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();

        distCheckoutFacade.calculateOrder(true);

        // then
        verify(orderCalculationService).calculate(cartModel, true);
    }

    @Test
    public void testCalculateOrderWithSimulationFalse() throws CalculationException {
        // given
        CartModel cartModel = mock(CartModel.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();

        distCheckoutFacade.calculateOrder(false);

        // then
        verify(orderCalculationService).calculate(cartModel, false);
    }

    @Test
    public void testIsCancellationPolicyConfirmationRequiredWhenCountryIsoCodeIsBlank() {
        // given
        CountryData countryDate = mock(CountryData.class);
        Configuration configuration = mock(Configuration.class);

        // when
        when(storeSessionFacade.getCurrentCountry()).thenReturn(countryDate);
        when(countryDate.getIsocode()).thenReturn(StringUtils.EMPTY);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString(DistConstants.PropKey.Shop.COUNTRIES_WITHOUT_CANCELLATION_POLICY)).thenReturn("CH");

        boolean result = distCheckoutFacade.isCancellationPolicyConfirmationRequired();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsCancellationPolicyConfirmationRequiredCountriesWithoutCancellationPolicyNotConfigured() {
        // given
        CountryData countryDate = mock(CountryData.class);
        Configuration configuration = mock(Configuration.class);

        // when
        when(storeSessionFacade.getCurrentCountry()).thenReturn(countryDate);
        when(countryDate.getIsocode()).thenReturn("CH");
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString(DistConstants.PropKey.Shop.COUNTRIES_WITHOUT_CANCELLATION_POLICY)).thenReturn(null);

        boolean result = distCheckoutFacade.isCancellationPolicyConfirmationRequired();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsCancellationPolicyConfirmationRequiredWhenCountryIsInList() {
        // given
        CountryData countryDate = mock(CountryData.class);
        Configuration configuration = mock(Configuration.class);

        // when
        when(storeSessionFacade.getCurrentCountry()).thenReturn(countryDate);
        when(countryDate.getIsocode()).thenReturn("CH");
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString(DistConstants.PropKey.Shop.COUNTRIES_WITHOUT_CANCELLATION_POLICY)).thenReturn("CH, DE, AT");

        boolean result = distCheckoutFacade.isCancellationPolicyConfirmationRequired();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsCancellationPolicyConfirmationRequiredWhenCountryIsNotInList() {
        // given
        CountryData countryDate = mock(CountryData.class);
        Configuration configuration = mock(Configuration.class);

        // when
        when(storeSessionFacade.getCurrentCountry()).thenReturn(countryDate);
        when(countryDate.getIsocode()).thenReturn("FR");
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString(DistConstants.PropKey.Shop.COUNTRIES_WITHOUT_CANCELLATION_POLICY)).thenReturn("CH, DE, AT");

        boolean result = distCheckoutFacade.isCancellationPolicyConfirmationRequired();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testRequestDeliveryDateWhenCartIsPresent() {
        // given
        Date deliveryDate = new Date();
        CartModel cartModel = mock(CartModel.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();

        distCheckoutFacade.requestDeliveryDate(deliveryDate);

        // then
        verify(cartModel).setRequestedDeliveryDate(deliveryDate);
        verify(modelService).save(cartModel);
        verify(modelService).refresh(cartModel);
    }

    @Test
    public void testRequestDeliveryDateWhenCartIsNull() {
        // given
        Date deliveryDate = new Date();

        // when
        doReturn(null).when(distCheckoutFacade).getCart();

        distCheckoutFacade.requestDeliveryDate(deliveryDate);

        // then
        verify(modelService, never()).save(any());
        verify(modelService, never()).refresh(any());
    }

    @Test
    public void testCalculateScheduledDeliveryDateWeekend() throws ParseException {
        // given
        String dateString = "2023-09-09";
        String dateFormat = "yyyy-MM-dd";
        Date selectedDate = new SimpleDateFormat(dateFormat).parse(dateString);

        // when
        doReturn(selectedDate).when(distCheckoutFacade).getMinimumRequestedDeliveryDateForCurrentCart();
        doReturn(selectedDate).when(distCheckoutFacade).getMaximumRequestedDeliveryDateForCurrentCart();
        when(distCheckoutFacade.fetchNextWorkingDayIfWeekend(selectedDate)).thenReturn(selectedDate);

        Date result = distCheckoutFacade.calculateScheduledDeliveryDate(dateString, dateFormat);

        // then
        assertThat(result, is(notNullValue()));
    }

    @Test
    public void testCalculateScheduledDeliveryDateBeforeMinimum() throws ParseException {
        // given
        String dateString = "2023-09-01";
        String dateFormat = "yyyy-MM-dd";
        Date minimumDate = new SimpleDateFormat(dateFormat).parse("2023-09-05");

        // when
        doReturn(minimumDate).when(distCheckoutFacade).getMinimumRequestedDeliveryDateForCurrentCart();
        doReturn(minimumDate).when(distCheckoutFacade).getMaximumRequestedDeliveryDateForCurrentCart();

        Date result = distCheckoutFacade.calculateScheduledDeliveryDate(dateString, dateFormat);

        // then
        assertThat(result, equalTo(minimumDate));
    }

    @Test
    public void testCalculateScheduledDeliveryDateAfterMaximum() throws ParseException {
        // given
        String dateString = "2023-10-01";
        String dateFormat = "yyyy-MM-dd";
        Date maximumDate = new SimpleDateFormat(dateFormat).parse("2023-09-30");

        // when
        doReturn(maximumDate).when(distCheckoutFacade).getMinimumRequestedDeliveryDateForCurrentCart();
        doReturn(maximumDate).when(distCheckoutFacade).getMaximumRequestedDeliveryDateForCurrentCart();

        Date result = distCheckoutFacade.calculateScheduledDeliveryDate(dateString, dateFormat);

        // then
        assertThat(result, equalTo(maximumDate));
    }

    @Test
    public void testCalculateScheduledDeliveryDateWithinAllowedRange() throws ParseException {
        // given
        String dateString = "2023-09-20";
        String dateFormat = "yyyy-MM-dd";
        Date selectedDate = new SimpleDateFormat(dateFormat).parse(dateString);

        // when
        doReturn(selectedDate).when(distCheckoutFacade).getMinimumRequestedDeliveryDateForCurrentCart();
        doReturn(selectedDate).when(distCheckoutFacade).getMaximumRequestedDeliveryDateForCurrentCart();

        Date result = distCheckoutFacade.calculateScheduledDeliveryDate(dateString, dateFormat);

        // then
        assertThat(result, equalTo(selectedDate));
    }

    @Test
    public void testGetMaximumRequestedDeliveryDateForCurrentCartWithoutMaxScheduledOrderDeliveryDate() throws ParseException {
        // given
        String dateFormat = "yyyy-MM-dd";
        String minRequestedDateString = "2024-03-07";
        Date minRequestedDate = new SimpleDateFormat(dateFormat).parse(minRequestedDateString);
        String maxRequestedDateString = "2024-09-03";
        Date maxRequestedDate = new SimpleDateFormat(dateFormat).parse(maxRequestedDateString);
        Configuration configuration = mock(Configuration.class);
        CMSSiteModel currentSite = mock(CMSSiteModel.class);

        // when
        doReturn(minRequestedDate).when(distCheckoutFacade).getMinimumRequestedDeliveryDateForCurrentCart();
        when(cmsSiteService.getCurrentSite()).thenReturn(currentSite);
        when(currentSite.getMaxScheduledOrderDeliveryDate()).thenReturn(null);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getInt("cart.max.deliverydays", 180)).thenReturn(180);

        Date result = distCheckoutFacade.getMaximumRequestedDeliveryDateForCurrentCart();

        // then
        assertThat(result, equalTo(maxRequestedDate));
    }

    @Test
    public void testGetMaximumRequestedDeliveryDateForCurrentCartWithMaxScheduledOrderDeliveryDate() throws ParseException {
        // given
        String dateFormat = "yyyy-MM-dd";
        String minRequestedDateString = "2024-03-07";
        Date minRequestedDate = new SimpleDateFormat(dateFormat).parse(minRequestedDateString);
        String maxScheduledDateString = "2024-03-26";
        Date maxScheduledDate = new SimpleDateFormat(dateFormat).parse(maxScheduledDateString);
        CMSSiteModel currentSite = mock(CMSSiteModel.class);

        // when
        doReturn(minRequestedDate).when(distCheckoutFacade).getMinimumRequestedDeliveryDateForCurrentCart();
        when(cmsSiteService.getCurrentSite()).thenReturn(currentSite);
        when(currentSite.getMaxScheduledOrderDeliveryDate()).thenReturn(maxScheduledDate);

        Date result = distCheckoutFacade.getMaximumRequestedDeliveryDateForCurrentCart();

        // then
        assertThat(result, equalTo(maxScheduledDate));
    }

    @Test
    public void testGetMaximumRequestedDeliveryDateForCurrentCartWithMaxScheduledOrderDeliveryDateBeforeMinimum() throws ParseException {
        // given
        String dateFormat = "yyyy-MM-dd";
        String minRequestedDateString = "2024-03-07";
        Date minRequestedDate = new SimpleDateFormat(dateFormat).parse(minRequestedDateString);
        String maxScheduledDateString = "2024-03-03";
        Date maxScheduledDate = new SimpleDateFormat(dateFormat).parse(maxScheduledDateString);
        CMSSiteModel currentSite = mock(CMSSiteModel.class);

        // when
        doReturn(minRequestedDate).when(distCheckoutFacade).getMinimumRequestedDeliveryDateForCurrentCart();
        when(cmsSiteService.getCurrentSite()).thenReturn(currentSite);
        when(currentSite.getMaxScheduledOrderDeliveryDate()).thenReturn(maxScheduledDate);

        Date result = distCheckoutFacade.getMaximumRequestedDeliveryDateForCurrentCart();

        // then
        assertThat(result, equalTo(minRequestedDate));
    }

    @Test
    public void shouldNotDisplayVatWarningForNonExportShop() {
        // when
        when(storeSessionFacade.isCurrentShopExport()).thenReturn(false);

        boolean result = distCheckoutFacade.shouldDisplayVatWarningForExportShop(customer);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void shouldNotDisplayVatWarningForExportShopWithNoContactAddress() {
        // when
        when(storeSessionFacade.isCurrentShopExport()).thenReturn(true);
        when(customer.getContactAddress()).thenReturn(null);

        boolean result = distCheckoutFacade.shouldDisplayVatWarningForExportShop(customer);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void shouldNotDisplayVatWarningForExportShopWithNonEuropeanContactAddress() {
        // when
        when(storeSessionFacade.isCurrentShopExport()).thenReturn(true);
        when(customer.getContactAddress()).thenReturn(contactAddress);
        when(contactAddress.getCountry()).thenReturn(country);
        when(country.isEuropean()).thenReturn(false);

        boolean result = distCheckoutFacade.shouldDisplayVatWarningForExportShop(customer);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void shouldNotDisplayVatWarningForExportShopWithNonEuropeanBillingAddress() {
        // when
        when(storeSessionFacade.isCurrentShopExport()).thenReturn(true);
        when(customer.getContactAddress()).thenReturn(null);

        boolean result = distCheckoutFacade.shouldDisplayVatWarningForExportShop(customer);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void shouldDisplayVatWarningForExportShopWithEuropeanContactAddress() {
        // when
        when(storeSessionFacade.isCurrentShopExport()).thenReturn(true);
        when(customer.getContactAddress()).thenReturn(contactAddress);
        when(contactAddress.getCountry()).thenReturn(country);
        when(country.isEuropean()).thenReturn(true);

        boolean result = distCheckoutFacade.shouldDisplayVatWarningForExportShop(customer);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void shouldDisplayVatWarningForExportShopWithEuropeanBillingAddress() {
        // given
        AddressData addressData = mock(AddressData.class);

        // when
        when(storeSessionFacade.isCurrentShopExport()).thenReturn(true);
        when(customer.getContactAddress()).thenReturn(addressData);
        when(customer.getBillingAddress()).thenReturn(billingAddress);
        when(customer.getUnit()).thenReturn(unit);
        when(unit.getCountry()).thenReturn(country);
        when(country.isEuropean()).thenReturn(true);

        boolean result = distCheckoutFacade.shouldDisplayVatWarningForExportShop(customer);

        // then
        assertThat(result, is(true));
    }

    @Test(expected = PurchaseBlockedException.class)
    public void shouldThrowPurchaseBlockedExceptionWhenBlockedProductsExist() {
        // given
        List<String> blockedProducts = Arrays.asList("P123", "P456");

        // when
        when(availabilityService.getPurchaseBlockedProductCodes()).thenReturn(blockedProducts);

        distCheckoutFacade.checkForPurchaseBlockedProducts();
    }

    @Test
    public void shouldNotThrowExceptionWhenNoBlockedProductsExist() {
        // when
        when(availabilityService.getPurchaseBlockedProductCodes()).thenReturn(Collections.emptyList());

        distCheckoutFacade.checkForPurchaseBlockedProducts();
    }

    @Test
    public void shouldNotThrowExceptionWhenBlockedProductsIsNull() {
        // when
        when(availabilityService.getPurchaseBlockedProductCodes()).thenReturn(null);

        distCheckoutFacade.checkForPurchaseBlockedProducts();
    }

    @Test
    public void testValidWarehouse() {
        // given
        CMSSiteModel site = mock((CMSSiteModel.class));
        WarehouseModel warehouse = mock(WarehouseModel.class);

        // when
        when(cmsSiteService.getCurrentSite()).thenReturn(site);
        when(site.getCheckoutPickupWarehouses()).thenReturn(Set.of(warehouse));

        boolean isValid = distCheckoutFacade.isWarehouseValid(warehouse);

        // then
        assertThat(isValid, is(true));
    }

    @Test
    public void testInvalidWarehouse() {
        // given
        CMSSiteModel site = mock((CMSSiteModel.class));
        WarehouseModel warehouse = mock(WarehouseModel.class);

        // when
        when(cmsSiteService.getCurrentSite()).thenReturn(site);
        when(site.getCheckoutPickupWarehouses()).thenReturn(Collections.emptySet());

        boolean isValid = distCheckoutFacade.isWarehouseValid(warehouse);

        // then
        assertThat(isValid, is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPaymentAddressModelForCodeThrowExceptionWhenCodeIsNull() {
        // given
        String code = null;

        // when
        distCheckoutFacade.getPaymentAddressModelForCode(code);
    }

    @Test
    public void testGetPaymentAddressModelForCodeCartIsNull() {
        // given
        String validCode = "validCode";
        CartModel cartModel = mock(CartModel.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();

        AddressModel result = distCheckoutFacade.getPaymentAddressModelForCode(validCode);

        // then
        assertThat(result, is(nullValue()));
    }

    @Test
    public void testGetPaymentAddressModelForCodeWhenCustomerIsNull() {
        // given
        String validCode = "validCode";
        CartModel cartModel = mock(CartModel.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();
        when(cartModel.getUser()).thenReturn(null);

        AddressModel result = distCheckoutFacade.getPaymentAddressModelForCode(validCode);

        // then
        assertThat(result, is(nullValue()));
    }

    @Test
    public void testGetPaymentAddressModelForCodeAddressesAreEmpty() {
        // given
        String validCode = "123";
        CartModel cartModel = mock(CartModel.class);
        B2BCustomerModel customerModel = mock(B2BCustomerModel.class);
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();
        when(cartModel.getUser()).thenReturn(customerModel);
        when(b2bUnitService.getParent(customerModel)).thenReturn(b2BUnitModel);
        when(b2BUnitModel.getBillingAddresses()).thenReturn(Collections.emptyList());

        AddressModel result = distCheckoutFacade.getPaymentAddressModelForCode(validCode);

        // then
        assertThat(result, is(nullValue()));
    }

    @Test
    public void testGetPaymentAddressModelForCodeWhenCodeMatches() {
        // given
        String validCode = "123";
        CartModel cartModel = mock(CartModel.class);
        B2BCustomerModel customerModel = mock(B2BCustomerModel.class);
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);
        PK pk = PK.parse("123");
        AddressModel addressModel = mock(AddressModel.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();
        when(cartModel.getUser()).thenReturn(customerModel);
        when(b2bUnitService.getParent(customerModel)).thenReturn(b2BUnitModel);
        when(addressModel.getPk()).thenReturn(pk);
        when(b2BUnitModel.getBillingAddresses()).thenReturn(List.of(addressModel));

        AddressModel result = distCheckoutFacade.getPaymentAddressModelForCode(validCode);

        // then
        assertThat(result, equalTo(addressModel));
    }

    @Test
    public void testGetPaymentAddressModelForCodeWhenCodeDoesNotMatch() {
        // given
        String invalidCode = "567";
        CartModel cartModel = mock(CartModel.class);
        B2BCustomerModel customerModel = mock(B2BCustomerModel.class);
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);
        PK pk = PK.parse("123");
        AddressModel addressModel = mock(AddressModel.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();
        when(cartModel.getUser()).thenReturn(customerModel);
        when(b2bUnitService.getParent(customerModel)).thenReturn(b2BUnitModel);
        when(addressModel.getPk()).thenReturn(pk);
        when(b2BUnitModel.getBillingAddresses()).thenReturn(List.of(addressModel));

        AddressModel result = distCheckoutFacade.getPaymentAddressModelForCode(invalidCode);

        // then
        assertThat(result, is(nullValue()));
    }

    @Test
    public void testSavePaymentAddressSetPaymentAddressAndSaveCart() {
        // given
        CartModel cartModel = mock(CartModel.class);
        AddressModel addressModel = mock(AddressModel.class);

        // when
        distCheckoutFacade.savePaymentAddress(cartModel, addressModel);

        // then
        verify(cartModel).setPaymentAddress(addressModel);
        verify(modelService).save(cartModel);
    }

    @Test
    public void testSaveDeliveryAddressSetDeliveryAddressAndSaveCart() {
        // given
        CartModel cartModel = mock(CartModel.class);
        AddressModel addressModel = mock(AddressModel.class);

        // when
        distCheckoutFacade.saveDeliveryAddress(cartModel, addressModel);

        // then
        verify(cartModel).setDeliveryAddress(addressModel);
        verify(modelService).save(cartModel);
    }

    @Test
    public void testSortCountriesByNameAndIsoCode() {
        // given
        CountryModel country1 = mock(CountryModel.class);
        when(country1.getName()).thenReturn("Switzerland");

        CountryModel country2 = mock(CountryModel.class);
        when(country2.getName()).thenReturn("Germany");

        CountryModel country3 = mock(CountryModel.class);
        when(country3.getName()).thenReturn("Austria");

        // when
        List<CountryModel> sortedCountries = distCheckoutFacade.sortCountries(Arrays.asList(country1, country2, country3));

        // then
        assertThat(country3, equalTo(sortedCountries.get(0)));
        assertThat(country2, equalTo(sortedCountries.get(1)));
        assertThat(country1, equalTo(sortedCountries.get(2)));
    }

    @Test
    public void testSortCountriesHandleEqualNamesByIso() {
        // given
        CountryModel country1 = mock(CountryModel.class);
        CountryModel country2 = mock(CountryModel.class);

        // when
        when(country1.getName()).thenReturn("Switzerland");
        when(country1.getIsocode()).thenReturn("LI");
        when(country2.getName()).thenReturn("Switzerland");
        when(country2.getIsocode()).thenReturn("CH");

        List<CountryModel> sortedCountries = distCheckoutFacade.sortCountries(Arrays.asList(country1, country2));

        // then
        assertThat(country2, equalTo(sortedCountries.get(0)));
        assertThat(country1, equalTo(sortedCountries.get(1)));
    }

    @Test
    public void testIsCompleteDeliveryPossible() {
        // given
        CartModel cart = mock(CartModel.class);

        // when
        doReturn(cart).when(distCheckoutFacade).getCart();
        when(cart.getCompleteDelivery()).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.isCompleteDeliveryPossible();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsCompleteDeliveryPossibleIfCartIsNull() {
        // given
        doReturn(null).when(distCheckoutFacade).getCart();

        // when
        boolean result = distCheckoutFacade.isCompleteDeliveryPossible();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsCompleteDeliveryPossibleCartIsCreditBlocked() {
        // given
        CartModel cart = mock(CartModel.class);

        // when
        doReturn(cart).when(distCheckoutFacade).getCart();
        when(cart.getCompleteDelivery()).thenReturn(Boolean.FALSE);
        when(distCartService.isCreditBlocked(cart)).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.isCompleteDeliveryPossible();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsCompleteDeliveryPossibleHasLessThanTwoEntries() {
        // given
        CartModel cart = mock(CartModel.class);

        // when
        doReturn(cart).when(distCheckoutFacade).getCart();
        when(cart.getCompleteDelivery()).thenReturn(Boolean.FALSE);
        when(distCartService.isCreditBlocked(cart)).thenReturn(Boolean.FALSE);
        when(cart.getEntries()).thenReturn(Collections.singletonList(mock(AbstractOrderEntryModel.class)));

        boolean result = distCheckoutFacade.isCompleteDeliveryPossible();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsCompleteDeliveryPossibleCartEntryIsInvalid() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry = mock(AbstractOrderEntryModel.class);
        ProductModel product = mock(ProductModel.class);
        DistTransportGroupModel distTransportGroupModel = mock(DistTransportGroupModel.class);

        // when
        doReturn(cart).when(distCheckoutFacade).getCart();
        when(cart.getCompleteDelivery()).thenReturn(Boolean.FALSE);
        when(distCartService.isCreditBlocked(cart)).thenReturn(Boolean.FALSE);
        when(cart.getEntries()).thenReturn(Arrays.asList(entry, entry));
        when(entry.getProduct()).thenReturn(product);
        when(product.getTransportGroup()).thenReturn(distTransportGroupModel);
        when(distTransportGroupModel.getCode()).thenReturn("1005");

        boolean result = distCheckoutFacade.isCompleteDeliveryPossible();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsCompleteDeliveryPossibleCartEntryIsWaldom() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry = mock(AbstractOrderEntryModel.class);
        ProductModel product = mock(ProductModel.class);
        DistTransportGroupModel distTransportGroupModel = mock(DistTransportGroupModel.class);

        // when
        doReturn(cart).when(distCheckoutFacade).getCart();
        when(cart.getCompleteDelivery()).thenReturn(Boolean.FALSE);
        when(distCartService.isCreditBlocked(cart)).thenReturn(Boolean.FALSE);
        when(cart.getEntries()).thenReturn(Arrays.asList(entry, entry));
        when(entry.getProduct()).thenReturn(product);
        when(entry.getMview()).thenReturn(DistConstants.Product.WALDOM);
        when(product.getTransportGroup()).thenReturn(distTransportGroupModel);
        when(distTransportGroupModel.getCode()).thenReturn("22222");

        boolean result = distCheckoutFacade.isCompleteDeliveryPossible();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsCompleteDeliveryPossibleCartEntryIsBackOrder() {
        // given
        CartModel cart = mock(CartModel.class);
        AbstractOrderEntryModel entry = mock(AbstractOrderEntryModel.class);

        // when
        doReturn(cart).when(distCheckoutFacade).getCart();
        when(cart.getCompleteDelivery()).thenReturn(Boolean.FALSE);
        when(distCartService.isCreditBlocked(cart)).thenReturn(Boolean.FALSE);
        when(cart.getEntries()).thenReturn(Arrays.asList(entry, entry));
        when(entry.getIsBackOrder()).thenReturn(Boolean.TRUE);

        boolean result = distCheckoutFacade.isCompleteDeliveryPossible();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsErrorHandledReturnTrue() {
        // given
        String paymentErrorDescription = "DECLINED";
        String listOfErrorsHandled = "AUTHENTICATION FAILED,INVALID CC NUMBER,PARAM INVALID-CCCVC,PARAM INVALID-CCNR,DECLINED";
        Configuration configuration = mock(Configuration.class);

        // when
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString("payment.errors.handled.list")).thenReturn(listOfErrorsHandled);

        boolean result = distCheckoutFacade.isErrorHandled(paymentErrorDescription);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsErrorHandledReturnFalse() {
        // given
        String paymentErrorDescription = "CARD_DECLINED";
        String listOfErrorsHandled = "AUTHENTICATION FAILED,INVALID CC NUMBER,PARAM INVALID-CCCVC,PARAM INVALID-CCNR,DECLINED";
        Configuration configuration = mock(Configuration.class);

        // when
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString("payment.errors.handled.list")).thenReturn(listOfErrorsHandled);

        boolean result = distCheckoutFacade.isErrorHandled(paymentErrorDescription);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsErrorHandledHandleCaseInsensitivity() {
        // given
        String paymentErrorDescription = "DeClInEd";
        String listOfErrorsHandled = "AUTHENTICATION FAILED,INVALID CC NUMBER,PARAM INVALID-CCCVC,PARAM INVALID-CCNR,DECLINED";
        Configuration configuration = mock(Configuration.class);

        // when
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString("payment.errors.handled.list")).thenReturn(listOfErrorsHandled);

        boolean result = distCheckoutFacade.isErrorHandled(paymentErrorDescription);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void isErrorHandled_shouldHandleSpecialCharacters() {
        // given
        String paymentErrorDescription = "invalid-cvv!";
        String listOfErrorsHandled = "card_declined, invalid_cvv";
        Configuration configuration = mock(Configuration.class);

        // when
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString("payment.errors.handled.list")).thenReturn(listOfErrorsHandled);

        boolean result = distCheckoutFacade.isErrorHandled(paymentErrorDescription);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testSetCompleteDeliveryOnCartShouldSetCompleteDeliveryWhenCartExists() {
        // given
        boolean completeDelivery = true;
        CartModel cartModel = mock(CartModel.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();
        distCheckoutFacade.setCompleteDeliveryOnCart(completeDelivery);

        // then
        verify(cartModel).setCompleteDelivery(completeDelivery);
        verify(modelService).save(cartModel);
    }

    @Test
    public void testSetCompleteDeliveryOnCartShouldDoNothingWhenCartIsNull() {
        // given
        boolean completeDelivery = true;

        // when
        doReturn(null).when(distCheckoutFacade).getCart();
        distCheckoutFacade.setCompleteDeliveryOnCart(completeDelivery);

        // then
        verify(modelService, never()).save(any(CartModel.class));
    }

    @Test
    public void testSaveOrderCodiceDetailsShouldSaveCodiceCIGAndCodiceCUPWhenBothAreNotNull() {
        // given
        String codiceCUP = "CUP123";
        String codiceCIG = "CIG123";
        CartModel cartModel = mock(CartModel.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();
        distCheckoutFacade.saveOrderCodiceDetails(codiceCUP, codiceCIG);

        // then
        verify(cartModel).setCodiceCIG(codiceCIG);
        verify(cartModel).setCodiceCUP(codiceCUP);
        verify(modelService).save(cartModel);
        verify(modelService).refresh(cartModel);
    }

    @Test
    public void testSaveOrderCodiceDetailsShouldNotSaveCodiceCIGWhenItIsNull() {
        // given
        String codiceCUP = "CUP123";
        CartModel cartModel = mock(CartModel.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();
        distCheckoutFacade.saveOrderCodiceDetails(codiceCUP, null);

        // then
        verify(cartModel, never()).setCodiceCIG(anyString());
        verify(cartModel).setCodiceCUP(codiceCUP);
        verify(modelService).save(cartModel);
        verify(modelService).refresh(cartModel);
    }

    @Test
    public void testSaveOrderCodiceDetailsShouldNotSaveCodiceCUPWhenItIsNull() {
        // given
        String codiceCIG = "CIG123";
        CartModel cartModel = mock(CartModel.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();
        distCheckoutFacade.saveOrderCodiceDetails(null, codiceCIG);

        // then
        verify(cartModel).setCodiceCIG(codiceCIG);
        verify(cartModel, never()).setCodiceCUP(anyString());
        verify(modelService).save(cartModel);
        verify(modelService).refresh(cartModel);
    }

    @Test
    public void testSaveOrderCodiceDetailsShouldDoNothingWhenCartIsNull() {
        // given
        String codiceCUP = "CUP123";
        String codiceCIG = "CIG123";

        // when
        doReturn(null).when(distCheckoutFacade).getCart();
        distCheckoutFacade.saveOrderCodiceDetails(codiceCUP, codiceCIG);

        // then
        verify(modelService, never()).save(any(CartModel.class));
        verify(modelService, never()).refresh(any(CartModel.class));
    }

    @Test
    public void testSaveReevooEligibleFlagShouldSaveWhenFlagIsNotNull() {
        // given
        Boolean isReevooEligible = Boolean.TRUE;
        CartModel cartModel = mock(CartModel.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();
        distCheckoutFacade.saveReevooEligibleFlag(isReevooEligible);

        // then
        verify(cartModel).setReevooEligible(isReevooEligible);
        verify(modelService).save(cartModel);
    }

    @Test
    public void testSaveReevooEligibleFlagShouldNotSaveWhenFlagIsNull() {
        // given
        CartModel cartModel = mock(CartModel.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();
        distCheckoutFacade.saveReevooEligibleFlag(null);

        // then
        verify(cartModel, never()).setReevooEligible(anyBoolean());
        verify(modelService, never()).save(cartModel);
    }

    @Test
    public void testSaveReevooEligibleFlagShouldNotSaveWhenCartIsNull() {
        // given
        Boolean isReevooEligible = Boolean.TRUE;

        // when
        doReturn(null).when(distCheckoutFacade).getCart();
        distCheckoutFacade.saveReevooEligibleFlag(isReevooEligible);

        // then
        verify(modelService, never()).save(any(CartModel.class));
    }

    @Test
    public void testIsGuestCheckoutEnabledForSiteShouldReturnTrueWhenEnabled() {
        // given
        CMSSiteModel currentSite = mock(CMSSiteModel.class);

        // when
        when(currentSite.getGuestCheckoutEnabled()).thenReturn(Boolean.TRUE);
        when(cmsSiteService.getCurrentSite()).thenReturn(currentSite);

        boolean result = distCheckoutFacade.isGuestCheckoutEnabledForSite();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsGuestCheckoutEnabledForSiteShouldReturnFalseWhenDisabled() {
        // given
        CMSSiteModel currentSite = mock(CMSSiteModel.class);

        // when
        when(currentSite.getGuestCheckoutEnabled()).thenReturn(Boolean.FALSE);
        when(cmsSiteService.getCurrentSite()).thenReturn(currentSite);

        boolean result = distCheckoutFacade.isGuestCheckoutEnabledForSite();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsAddressValidShouldReturnTrueWhenAddressDataIsValid() {
        // given
        AddressData addressData = new AddressData();
        addressData.setCountry(new CountryData());
        addressData.setLine1("123 Main St");
        addressData.setPostalCode("12345");
        addressData.setTown("Anytown");
        addressData.setPhone1("1234567890");

        // when
        boolean result = distCheckoutFacade.isAddressValid(addressData);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsAddressValidShouldReturnFalseWhenAddressDataIsNull() {
        // given
        AddressData addressData = null;

        // when
        boolean result = distCheckoutFacade.isAddressValid(addressData);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsAddressValidShouldReturnFalseWhenMandatoryFieldsAreBlank() {
        // given
        AddressData addressData = new AddressData();
        addressData.setCountry(new CountryData());
        addressData.setLine1(" ");
        addressData.setPostalCode(" ");
        addressData.setTown(" ");
        addressData.setPhone1(" ");

        // when
        boolean result = distCheckoutFacade.isAddressValid(addressData);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsAddressValidShouldReturnFalseWhenCountryIsNull() {
        // given
        AddressData addressData = new AddressData();
        addressData.setCountry(null);
        addressData.setLine1("123 Main St");
        addressData.setPostalCode("12345");
        addressData.setTown("Anytown");
        addressData.setPhone1("1234567890");

        // when
        boolean result = distCheckoutFacade.isAddressValid(addressData);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsDeliveryAddressNotSetForCurrentCartReturnsTrue() {
        // given
        CartModel cartModel = mock(CartModel.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();
        when(cartModel.getDeliveryAddress()).thenReturn(null);

        boolean result = distCheckoutFacade.isDeliveryAddressNotSetForCurrentCart();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsDeliveryAddressNotSetForCurrentCartReturnsFalse() {
        // given
        CartModel cartModel = mock(CartModel.class);
        AddressModel addressModel = mock(AddressModel.class);

        // when
        doReturn(cartModel).when(distCheckoutFacade).getCart();
        when(cartModel.getDeliveryAddress()).thenReturn(addressModel);

        boolean result = distCheckoutFacade.isDeliveryAddressNotSetForCurrentCart();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsDeliveryAddressNotSetForCurrentCartWhenCartIsNull() {
        // when
        doReturn(null).when(distCheckoutFacade).getCart();

        boolean result = distCheckoutFacade.isDeliveryAddressNotSetForCurrentCart();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testUpdateGuestAddressWhenSalesOrgIsExport() throws CalculationException {
        // given
        AddressData addressData = new AddressData();
        addressData.setCodiceFiscale("ABC123XYZ");
        when(distSalesOrgService.isCurrentSalesOrgExport()).thenReturn(Boolean.TRUE);
        when(userFacade.editAddress(addressData, Boolean.FALSE)).thenReturn(addressData);

        // when
        distCheckoutFacade.updateGuestAddress(addressData);

        // then
        verify(distCommerceCheckoutService).updateVatIdForGuest(addressData.getCodiceFiscale());
        verify(userFacade).editAddress(addressData, Boolean.FALSE);
    }

    @Test
    public void testUpdateGuestAddressWhenSalesOrgIsNotExport() throws CalculationException {
        // given
        AddressData addressData = new AddressData();
        addressData.setCodiceFiscale("ABC123XYZ");
        when(distSalesOrgService.isCurrentSalesOrgExport()).thenReturn(Boolean.FALSE);
        when(userFacade.editAddress(addressData, Boolean.FALSE)).thenReturn(addressData);

        // when
        distCheckoutFacade.updateGuestAddress(addressData);

        // then
        verify(distCommerceCheckoutService).updateVatIdForGuest(addressData.getCodiceFiscale());
        verify(userFacade).editAddress(addressData, Boolean.FALSE);
    }
}
