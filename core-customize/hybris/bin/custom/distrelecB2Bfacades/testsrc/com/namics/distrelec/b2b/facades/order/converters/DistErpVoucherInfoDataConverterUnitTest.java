package com.namics.distrelec.b2b.facades.order.converters;

import com.namics.distrelec.b2b.core.model.order.DistErpVoucherInfoModel;
import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import com.namics.distrelec.b2b.facades.product.DistPriceDataFactory;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistErpVoucherInfoDataConverterUnitTest {

    @Mock
    private DistPriceDataFactory priceDataFactory;

    @InjectMocks
    private DistErpVoucherInfoDataConverter distErpVoucherInfoDataConverter;

    @Test
    public void testPopulate() {
        // given
        AbstractOrderModel abstractOrder = mock(AbstractOrderModel.class);
        DistErpVoucherInfoModel voucherInfo = mock(DistErpVoucherInfoModel.class);
        CurrencyModel currencyModel = mock(CurrencyModel.class);
        DistErpVoucherInfoData voucherInfoData = new DistErpVoucherInfoData();
        PriceData priceData = mock(PriceData.class);

        // when
        when(abstractOrder.getErpVoucherInfo()).thenReturn(voucherInfo);
        when(voucherInfo.getCode()).thenReturn("VOUCHER_CODE");
        when(voucherInfo.isValidInERP()).thenReturn(true);
        when(voucherInfo.isCalculatedInERP()).thenReturn(false);
        when(voucherInfo.getReturnERPCode()).thenReturn("RETURN_CODE");
        when(voucherInfo.getFixedValue()).thenReturn(50.0);
        when(abstractOrder.getCurrency()).thenReturn(currencyModel);
        when(currencyModel.getIsocode()).thenReturn("CHF");

        when(priceDataFactory.createWithoutCurrency(PriceDataType.BUY, BigDecimal.valueOf(50.0), "CHF"))
                .thenReturn(priceData);

        distErpVoucherInfoDataConverter.populate(abstractOrder, voucherInfoData);

        // then
        assertThat(voucherInfoData.getCode(), equalTo("VOUCHER_CODE"));
        assertThat(voucherInfoData.getValid(), is(true));
        assertThat(voucherInfoData.getCalculatedInERP(), is(false));
        assertThat(voucherInfoData.getReturnERPCode(), equalTo("RETURN_CODE"));
        assertThat(voucherInfoData.getFixedFormattedValue(), equalTo(priceData));
    }
}
