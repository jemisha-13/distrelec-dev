package com.namics.distrelec.b2b.facades.order.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistMinCartModificationPopulatorUnitTest {

    @Mock
    private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;

    @InjectMocks
    private DistMinCartModificationPopulator populator;

    @Test
    public void testPopulateWithCartModificationData()  {
        // given
        CommerceCartModification source = mock(CommerceCartModification.class);
        CartModificationData target = new CartModificationData();
        AbstractOrderEntryModel entryModel = mock(AbstractOrderEntryModel.class);
        AbstractOrderModel orderModel = mock(AbstractOrderModel.class);
        OrderEntryData orderEntryData = mock(OrderEntryData.class);

        // when
        when(source.getEntry()).thenReturn(entryModel);
        when(entryModel.getOrder()).thenReturn(orderModel);
        when(orderModel.getCode()).thenReturn("cartCode");
        when(source.getStatusCode()).thenReturn("statusCode");
        when(source.getQuantity()).thenReturn(5L);
        when(source.getQuantityAdded()).thenReturn(3L);
        when(source.getDeliveryModeChanged()).thenReturn(true);
        when(orderEntryConverter.convert(entryModel)).thenReturn(orderEntryData);

        populator.populate(source, target);

        // then
        assertThat(target.getEntry(), equalTo(orderEntryData));
        assertThat(target.getCartCode(), equalTo("cartCode"));
        assertThat(target.getStatusCode(), equalTo("statusCode"));
        assertThat(target.getQuantity(), equalTo(5L));
        assertThat(target.getQuantityAdded(), equalTo(3L));
    }

    @Test
    public void testPopulateWithNullEntry() {
        // given
        CommerceCartModification source = mock(CommerceCartModification.class);
        CartModificationData target = new CartModificationData();

        // when
        when(source.getEntry()).thenReturn(null);

        populator.populate(source, target);

        // then
        assertThat(target.getEntry(), is(nullValue()));
        assertThat(target.getCartCode(), is(nullValue()));
    }
}
