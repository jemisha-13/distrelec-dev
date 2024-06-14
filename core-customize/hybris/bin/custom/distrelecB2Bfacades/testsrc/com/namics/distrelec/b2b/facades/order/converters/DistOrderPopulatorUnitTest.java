package com.namics.distrelec.b2b.facades.order.converters;

import com.namics.distrelec.b2b.core.model.promotion.SapGeneratedVoucherModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistOrderPopulatorUnitTest {

    @Mock
    private PriceDataFactory priceDataFactory;

    @Mock
    private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;

    @InjectMocks
    DistOrderPopulator distOrderPopulator = new DistOrderPopulator();

    @Test
    public void testPopulate() {
        // given
        OrderModel source = mock(OrderModel.class);
        OrderData target = new OrderData();
        CurrencyModel currencyModel = mock(CurrencyModel.class);
        PriceData priceData = mock(PriceData.class);

        // when
        when(source.getCode()).thenReturn("ORDER_CODE");
        when(source.getGuid()).thenReturn("ORDER_GUID");
        when(source.getNet()).thenReturn(true);
        when(source.getCreationtime()).thenReturn(new Date());
        when(source.getStatus()).thenReturn(OrderStatus.APPROVED);
        when(source.getRequestedDeliveryDate()).thenReturn(new Date());
        when(source.isOpenOrder()).thenReturn(false);
        when(source.getErpOpenOrderCode()).thenReturn("ERP_OPEN_ORDER_CODE");
        when(source.getCurrency()).thenReturn(currencyModel);
        when(source.getNetSubTotal()).thenReturn(100.0);

        when(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(100.0), currencyModel))
                .thenReturn(priceData);

        distOrderPopulator.populate(source, target);

        // then
        assertThat(target.getCode(), equalTo("ORDER_CODE"));
        assertThat(target.getGuid(), equalTo("ORDER_GUID"));
        assertThat(target.isNet(), is(true));
        assertThat(target.getSubTotal(), equalTo(priceData));
    }

    @Test
    public void testPopulateWithGeneratedVoucher() {
        // given
        OrderModel source = mock(OrderModel.class);
        OrderData target = new OrderData();
        SapGeneratedVoucherModel generatedVoucher = mock(SapGeneratedVoucherModel.class);
        PriceData priceData = mock(PriceData.class);

        //when
        when(source.getGeneratedVoucher()).thenReturn(generatedVoucher);
        when(generatedVoucher.getCode()).thenReturn("VOUCHER_CODE");
        when(generatedVoucher.getValidFrom()).thenReturn(new Date());
        when(generatedVoucher.getValidUntil()).thenReturn(new Date());
        when(generatedVoucher.getValue()).thenReturn(100.0);
        when(source.getCurrency()).thenReturn(mock(CurrencyModel.class));
        when(source.getCurrency().getIsocode()).thenReturn("CHF");

        when(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(100.0), "CHF"))
                .thenReturn(priceData);

        distOrderPopulator.populate(source, target);

        // then
        assertThat(target.getGeneratedVoucher().getCode(), equalTo("VOUCHER_CODE"));
    }
}
