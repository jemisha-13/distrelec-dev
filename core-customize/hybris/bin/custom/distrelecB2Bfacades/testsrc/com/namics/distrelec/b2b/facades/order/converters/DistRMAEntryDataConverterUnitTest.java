package com.namics.distrelec.b2b.facades.order.converters;

import com.distrelec.webservice.if19.v1.ItemList;
import com.distrelec.webservice.if19.v1.RMAList3;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.DistRMAEntryData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistRMAEntryDataConverterUnitTest {

    DistRMAEntryDataConverter distRMAEntryDataConverter = new DistRMAEntryDataConverter();

    @Test
    public void testPopulate() {
        // given
        ItemList source = createMockItemList();
        DistRMAEntryData target = new DistRMAEntryData();

        // when
        when(source.getRmas()).thenReturn(createMockRMAList());

        distRMAEntryDataConverter.populate(source, target);

        // then
        assertThat(target.getRemainingReturnQty(), equalTo(5L));
        assertThat(target.isNotAllowed(), is(false));
        assertThat(target.getNotAllowedDesc(), is("Return allowed"));
        assertThat(target.getRmas(), hasSize(2));
    }

    private ItemList createMockItemList() {
        ItemList source = mock(ItemList.class);
        when(source.getRemainingQuantity()).thenReturn(5L);
        when(source.isNotAllowedReturnFlag()).thenReturn(false);
        when(source.getNotAllowedReturnDescription()).thenReturn("Return allowed");
        return source;
    }

    private List<RMAList3> createMockRMAList() {
        List<RMAList3> rmas = new ArrayList<>();
        RMAList3 rma1 = new RMAList3();
        rma1.setRmaNumber("RMA123");
        rma1.setRmaItemStatus("Default");
        rma1.setOfficeAddress("Address1");

        RMAList3 rma2 = new RMAList3();
        rma2.setRmaNumber("RMA456");
        rma2.setRmaItemStatus("Approved");
        rma2.setOfficeAddress("Address2");

        rmas.add(rma1);
        rmas.add(rma2);

        return rmas;
    }
}
