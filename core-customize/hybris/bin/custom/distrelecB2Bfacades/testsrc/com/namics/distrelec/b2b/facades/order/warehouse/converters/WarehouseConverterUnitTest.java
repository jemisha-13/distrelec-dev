package com.namics.distrelec.b2b.facades.order.warehouse.converters;

import com.namics.distrelec.b2b.facades.order.warehouse.data.WarehouseData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WarehouseConverterUnitTest {

    WarehouseConverter converter = new WarehouseConverter();

    @Test
    public void testPopulate() {
        // given
        WarehouseModel source = mock(WarehouseModel.class);
        WarehouseData target = new WarehouseData();

        // when
        when(source.getCode()).thenReturn("WH001");
        when(source.getPickupName()).thenReturn("Distrelec Warehouse");
        when(source.getPickupTown()).thenReturn("Zug");
        when(source.getPickupPhone()).thenReturn("+41789123412");
        when(source.getPickupOpeningHoursMoFr()).thenReturn("9:00 AM - 5:00 PM");

        converter.populate(source, target);

        // then
        assertThat(target.getCode(), equalTo("WH001"));
        assertThat(target.getName(), equalTo("Distrelec Warehouse"));
        assertThat(target.getTown(), equalTo("Zug"));
        assertThat(target.getOpeningsHourMoFr(), equalTo("9:00 AM - 5:00 PM"));
        assertThat(target.getPhone(), equalTo("+41789123412"));
    }
}
