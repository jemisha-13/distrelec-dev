package com.namics.distrelec.b2b.facades.order.cart.converters.populator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;

import org.apache.commons.configuration.BaseConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCommentData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.VirtualEntryGroupStrategy;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistCartPopulatorTest {

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
        PriceData priceData = getPriceData(BigDecimal.valueOf(0.0));
        PriceData priceData1 = getPriceData(BigDecimal.valueOf(50.0));
        when(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(0.0), currency)).thenReturn(priceData);
        when(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(50.0), "CHF")).thenReturn(priceData1);

        distCartPopulator.populate(cart, cartData);
        assertEquals(BigDecimal.valueOf(50.0), cartData.getFreeFreightValue().getValue());

    }

    @Test
    public void testMinFreeFreightValueLessThanFreeValuePopulate() {
        PriceData priceData = getPriceData(BigDecimal.valueOf(35.0));
        PriceData priceData1 = getPriceData(BigDecimal.valueOf(15.0));
        when(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(35.0), currency)).thenReturn(priceData);
        when(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(15.0), "CHF")).thenReturn(priceData1);
        when(cart.getNetSubTotal()).thenReturn(35.0);
        when(cart.getSubtotal()).thenReturn(35.0);

        distCartPopulator.populate(cart, cartData);
        assertEquals(BigDecimal.valueOf(15.0), cartData.getFreeFreightValue().getValue());

    }

    @Test
    public void testMinFreeFreightValueMoreThanFreeValuePopulate() {
        PriceData priceData = getPriceData(BigDecimal.valueOf(60.0));
        PriceData priceData1 = getPriceData(BigDecimal.valueOf(-10.0));
        when(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(60.0), currency)).thenReturn(priceData);
        when(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(-10.0), "CHF")).thenReturn(priceData1);
        when(cart.getNetSubTotal()).thenReturn(60.0);
        when(cart.getSubtotal()).thenReturn(60.0);

        distCartPopulator.populate(cart, cartData);
        assertEquals(BigDecimal.valueOf(-10.0), cartData.getFreeFreightValue().getValue());

    }

    private PriceData getPriceData(BigDecimal value) {
        PriceData priceData = mock(PriceData.class);
        when(priceData.getValue()).thenReturn(value);
        return priceData;
    }

}
