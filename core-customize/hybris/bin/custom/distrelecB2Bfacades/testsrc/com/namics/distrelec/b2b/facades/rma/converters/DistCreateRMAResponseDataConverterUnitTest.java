package com.namics.distrelec.b2b.facades.rma.converters;

import com.distrelec.webservice.if19.v1.RMACreateRespItem;
import com.distrelec.webservice.if19.v1.RMACreateRespOrder;
import com.namics.distrelec.b2b.core.service.rma.data.CreateRMAResponseData;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistCreateRMAResponseDataConverterUnitTest {

    @Mock
    private RMACreateRespOrder source;

    @InjectMocks
    private DistCreateRMAResponseDataConverter converter;

    private CreateRMAResponseData target;

    @Before
    public void setUp() {
        target = new CreateRMAResponseData();

        List<RMACreateRespItem> itemList = new ArrayList<>();
        RMACreateRespItem item1 = mock(RMACreateRespItem.class);
        when(item1.getRmaItemNumber()).thenReturn("Item1");
        when(item1.getRmaItemStatus()).thenReturn("Status1");
        itemList.add(item1);

        RMACreateRespItem item2 = mock(RMACreateRespItem.class);
        when(item2.getRmaItemNumber()).thenReturn("Item2");
        when(item2.getRmaItemStatus()).thenReturn("Status2");
        itemList.add(item2);

        when(source.getItems()).thenReturn(itemList);
    }

    @Test
    public void testPopulateOfficeAddress() {
        // when
        when(source.getOfficeAddress()).thenReturn("TestOfficeAddress");

        converter.populate(source, target);

        // then
        assertThat(target.getOfficeAddress(), equalTo("TestOfficeAddress"));
    }

    @Test
    public void testPopulateRmaHeaderStatus() {
        // when
        when(source.getRmaHeaderStatus()).thenReturn("TestHeaderStatus");

        converter.populate(source, target);

        // then
        assertThat(target.getRmaHeaderStatus(), equalTo("TestHeaderStatus"));
    }

    @Test
    public void testPopulateRmaNumber() {
        // when
        when(source.getRmaNumber()).thenReturn("TestRmaNumber");

        converter.populate(source, target);

        // then
        assertThat(target.getRmaNumber(), equalTo("TestRmaNumber"));
    }

    @Test
    public void testResponseItemsListSize() {
        // when
        converter.populate(source, target);

        // then
        assertThat(target.getOrderItems(), hasSize(2));
    }

    @Test
    public void testFirstResponseItemNumber() {
        // when
        converter.populate(source, target);

        // then
        assertThat(target.getOrderItems().get(0).getRmaItemNumber(), equalTo("Item1"));
    }

    @Test
    public void testFirstResponseItemStatus() {
        // when
        converter.populate(source, target);

        // then
        assertThat(target.getOrderItems().get(0).getRmaItemStatus(), equalTo("Status1"));
    }

    @Test
    public void testSecondResponseItemNumber() {
        // when
        converter.populate(source, target);

        // then
        assertThat(target.getOrderItems().get(1).getRmaItemNumber(), equalTo("Item2"));
    }

    @Test
    public void testSecondResponseItemStatus() {
        // when
        converter.populate(source, target);

        // then
        assertThat(target.getOrderItems().get(1).getRmaItemStatus(), equalTo("Status2"));
    }
}
