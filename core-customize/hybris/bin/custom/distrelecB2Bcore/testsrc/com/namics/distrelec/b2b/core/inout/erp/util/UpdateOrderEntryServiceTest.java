package com.namics.distrelec.b2b.core.inout.erp.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.distrelec.webservice.if11.v3.OrderEntryResponse;
import com.namics.distrelec.b2b.core.inout.erp.service.impl.DefaultUpdateOrderEntryService;
import com.namics.distrelec.b2b.core.jalo.DistPriceRow;
import com.namics.distrelec.b2b.core.service.product.DistCommercePriceService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.order.price.PriceInformation;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateOrderEntryServiceTest {
    public static final String BACKORDER_FLAG = "X";

    @Mock
    DistCommercePriceService distCommercePriceService;

    @InjectMocks
    private DefaultUpdateOrderEntryService updateOrderEntryService;

    @Spy
    AbstractOrderEntryModel orderEntry;

    @Mock
    ProductModel product;

    @Mock
    PriceInformation priceInformation;

    @Mock
    DistPriceRow distPriceRow;

    @Test
    public final void testUpdateOrderEntry() {
        when(orderEntry.getProduct()).thenReturn(product);
        when(orderEntry.getConfirmed()).thenReturn(Boolean.FALSE);
        when(distCommercePriceService.getScaledPriceInformations(any(ProductModel.class), anyBoolean())).thenReturn(
                Collections.singletonList(priceInformation));
        when(priceInformation.getQualifierValue(PriceRow.PRICEROW)).thenReturn(distPriceRow);

        final OrderEntryResponse response = createResponse("00000001", 5l, 1.50, 1.25, StringUtils.EMPTY);
        final Map<String, OrderEntryResponse> additionalAvailabilityInfoMap = createAdditionalAvailabilityInfoMap();

        updateOrderEntryService.updateOrderEntry(response, orderEntry, additionalAvailabilityInfoMap);

        assertEquals(15L, orderEntry.getQuantity().longValue());
        assertEquals(1, orderEntry.getTotalListPrice().intValue());
    }

    @Test
    public final void testIsBackOrderFlag() {
        when(orderEntry.getProduct()).thenReturn(product);
        when(product.getCode()).thenReturn("00000001");

        OrderEntryResponse response = createResponse("00000001", 5L, 1.50, 1.25, BACKORDER_FLAG);
        Map<String, OrderEntryResponse> additionalAvailabilityInfoMap = createAdditionalAvailabilityInfoMap();

        updateOrderEntryService.updateOrderEntry(response, orderEntry, additionalAvailabilityInfoMap);

        assertTrue(orderEntry.getIsBackOrder());
    }

    private OrderEntryResponse createResponse(final String materialNumber, final long orderQuantity, final double listPrice, final double actualPrice,
                                              String backOrderFlag) {
        OrderEntryResponse orderEntryResponse = mock(OrderEntryResponse.class);
        when(orderEntryResponse.getMaterialNumber()).thenReturn(materialNumber);
        when(orderEntryResponse.getOrderQuantity()).thenReturn(orderQuantity);
        when(orderEntryResponse.getListPrice()).thenReturn(listPrice);
        when(orderEntryResponse.getListPriceTotal()).thenReturn(listPrice);
        when(orderEntryResponse.getActualPrice()).thenReturn(actualPrice);
        when(orderEntryResponse.getBackOrderFlag()).thenReturn(backOrderFlag);
        return orderEntryResponse;
    }

    private Map<String, OrderEntryResponse> createAdditionalAvailabilityInfoMap() {
        final Map<String, OrderEntryResponse> availabilityMap = new HashMap<>();
        availabilityMap.put("00000001", createResponse("MAT-001", 10L, 1.00, 1.25, BACKORDER_FLAG));
        return availabilityMap;
    }
}
