package com.namics.distrelec.b2b.facades.order.cart.converters.populator;

import com.namics.distrelec.b2b.core.model.order.DistErpVoucherInfoModel;
import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import com.namics.distrelec.b2b.facades.product.DistPriceDataFactory;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistMiniCartPopulatorUnitTest {

    @Mock
    private Converter<AbstractOrderModel, DistErpVoucherInfoData> distErpVoucherInfoDataConverter;

    @Mock
    private DistPriceDataFactory priceDataFactory;

    @Mock
    private CartModel cartModel;

    @Spy
    private CartData cartData;

    @Mock
    private CurrencyModel currencyModel;

    @Mock
    private PriceData priceData;

    @Mock
    private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;

    @Mock
    private PromotionsService promotionsService;

    @InjectMocks
    private DistMiniCartPopulator distMiniCartPopulator;


    @Before
    public void setUp() {
        when(cartModel.getCurrency()).thenReturn(currencyModel);
        when(currencyModel.getIsocode()).thenReturn("CHF");
        when(priceDataFactory.createWithoutCurrency(any(PriceDataType.class), any(BigDecimal.class), any(String.class))).thenReturn(priceData);
    }

    @Test
    public void testPopulateWithErpVoucherInfo() {
        // given
        DistErpVoucherInfoModel distErpVoucherInfoModel = mock(DistErpVoucherInfoModel.class);
        DistErpVoucherInfoData distErpVoucherInfoData = mock(DistErpVoucherInfoData.class);
        AbstractOrderEntryModel unconfirmedEntry = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel confirmedEntry = mock(AbstractOrderEntryModel.class);

        // when
        when(unconfirmedEntry.getConfirmed()).thenReturn(Boolean.FALSE);
        when(confirmedEntry.getConfirmed()).thenReturn(Boolean.TRUE);
        when(cartModel.getEntries()).thenReturn(Arrays.asList(unconfirmedEntry, confirmedEntry));
        when(cartModel.getErpVoucherInfo()).thenReturn(distErpVoucherInfoModel);
        when(distErpVoucherInfoDataConverter.convert(cartModel)).thenReturn(distErpVoucherInfoData);

        distMiniCartPopulator.populate(cartModel, cartData);

        // then
        assertThat(cartData.getErpVoucherInfoData(),equalTo(distErpVoucherInfoData));
    }


    @Test
    public void testAddEntries() {
        // given
        AbstractOrderEntryModel unconfirmedEntry = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel confirmedEntry = mock(AbstractOrderEntryModel.class);

        // when
        when(unconfirmedEntry.getConfirmed()).thenReturn(Boolean.FALSE);
        when(confirmedEntry.getConfirmed()).thenReturn(Boolean.TRUE);
        when(cartModel.getEntries()).thenReturn(Arrays.asList(unconfirmedEntry, confirmedEntry));

        distMiniCartPopulator.addEntries(cartModel, cartData);

        // then
        assertThat(cartData.getEntries(), hasSize(1));
        assertThat(cartData.getConfirmedEntries(), hasSize(1));
    }

    @Test
    public void testAddTotals() {
        // when
        when(cartModel.getNetSubTotal()).thenReturn(50D);
        when(cartModel.getNetDeliveryCost()).thenReturn(5D);
        when(cartModel.getNetPaymentCost()).thenReturn(2D);
        when(cartModel.getTotalTax()).thenReturn(10D);
        when(cartModel.getTotalPrice()).thenReturn(67D);

        distMiniCartPopulator.addTotals(cartModel, cartData);

        // then
        assertThat(cartData.getSubTotal(), equalTo(createPriceWithoutCurrency(50D, "CHF")));
        assertThat(cartData.getDeliveryCost(), equalTo(createPriceWithoutCurrency(5D, "CHF")));
        assertThat(cartData.getPaymentCost(), equalTo(createPriceWithoutCurrency(2D, "CHF")));
        assertThat(cartData.getTotalTax(), equalTo(createPriceWithoutCurrency(10D, "CHF")));
        assertThat(cartData.getTotalPrice(), equalTo(createPriceWithoutCurrency(67D, "CHF")));
    }

    private PriceData createPriceWithoutCurrency(Double price, String currency) {
        return priceDataFactory.createWithoutCurrency(PriceDataType.BUY, BigDecimal.valueOf(price), currency);
    }
}
