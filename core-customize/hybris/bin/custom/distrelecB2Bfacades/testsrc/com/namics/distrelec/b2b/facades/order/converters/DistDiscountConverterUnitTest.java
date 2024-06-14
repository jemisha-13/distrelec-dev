package com.namics.distrelec.b2b.facades.order.converters;

import com.namics.distrelec.b2b.facades.order.data.DistDiscountData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistDiscountConverterUnitTest {

    @Mock
    private CurrencyModel currencyModel;

    @Mock
    private CurrencyData currencyData;

    @Mock
    private Populator<CurrencyModel, CurrencyData> currencyPopulator;

    @InjectMocks
    private DistDiscountConverter distDiscountConverter;

    @Test
    public void testPopulate() {
        // given
        DiscountModel source = mock(DiscountModel.class);
        DistDiscountData target = new DistDiscountData();

        // when
        when(source.getAbsolute()).thenReturn(true);
        when(source.getCode()).thenReturn("DISCOUNT_CODE");
        when(source.getDiscountString()).thenReturn("20%");
        when(source.getGlobal()).thenReturn(true);
        when(source.getPriority()).thenReturn(1);
        when(source.getValue()).thenReturn(0.5);
        when(source.getCurrency()).thenReturn(currencyModel);

        distDiscountConverter.populate(source, target);

        // then
        assertThat(target.getAbsolute(), is(true));
        assertThat(target.getCode(), equalTo("DISCOUNT_CODE"));
        assertThat(target.getDiscountString(), equalTo("20%"));
        assertThat(target.getGlobal(), is(true));
        assertThat(target.getPriority(), equalTo(1));
        assertThat(target.getValue(), equalTo(0.5));
    }

}
