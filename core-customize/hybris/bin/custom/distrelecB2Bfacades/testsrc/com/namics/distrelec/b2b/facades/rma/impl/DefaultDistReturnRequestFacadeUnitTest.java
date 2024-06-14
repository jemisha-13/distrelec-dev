package com.namics.distrelec.b2b.facades.rma.impl;

import com.distrelec.webservice.if19.v1.RMACreateRequest;
import com.distrelec.webservice.if19.v1.RMACreateRespOrder;
import com.distrelec.webservice.if19.v1.RMACreateResponse;
import com.namics.distrelec.b2b.core.event.DistRmaRequestEvent;
import com.namics.distrelec.b2b.core.inout.erp.RMAService;
import com.namics.distrelec.b2b.core.inout.erp.ReturnOnlineService;
import com.namics.distrelec.b2b.core.rma.CreateRMARequestForm;
import com.namics.distrelec.b2b.core.rma.GuestRMACreateRequestForm;
import com.namics.distrelec.b2b.core.rma.ReturnRequestData;
import com.namics.distrelec.b2b.core.rma.enums.RmaMainReturnReasonTypes;
import com.namics.distrelec.b2b.core.rma.enums.RmaSubReturnReasonTypes;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.rma.data.CreateRMAResponseData;
import com.namics.distrelec.b2b.core.service.site.DistrelecBaseStoreService;
import com.namics.distrelec.b2b.facades.rma.DistReturnRequestFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.DistMainReasonData;
import de.hybris.platform.commercefacades.order.data.DistSubReasonData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDistReturnRequestFacadeUnitTest {

    @Mock
    private RMAService rmaService;

    @Mock
    private BaseSiteService baseSiteService;

    @Mock
    private DistrelecBaseStoreService baseStoreService;

    @Mock
    private ReturnOnlineService returnOnlineService;

    @Mock
    private UserService userService;

    @Mock
    private DistProductService productService;

    @Mock
    private EventService eventService;

    @Mock
    private Converter<CreateRMARequestForm, RMACreateRequest> distCreateRMARequestConverter;

    @Mock
    private Converter<RMACreateRespOrder, CreateRMAResponseData> distCreateRMAResponseDataConverter;

    @InjectMocks
    private DefaultDistReturnRequestFacade defaultDistReturnRequestFacade;

    @Test
    public void testCreateReturnRequest() {
        // given
        ReturnRequestData requestData = mock(ReturnRequestData.class);
        String expectedResponse = "response";

        // when
        when(rmaService.createReturnRequest(requestData)).thenReturn(expectedResponse);

        String result = defaultDistReturnRequestFacade.createReturnRequest(requestData);

        // then
        assertThat(result, equalTo(expectedResponse));
    }

    @Test
    public void testGetReturnRequests() {
        // given
        List<Object> expectedList = new ArrayList<>();

        // when
        when(rmaService.getReturnRequests()).thenReturn(expectedList);

        List<Object> result = defaultDistReturnRequestFacade.getReturnRequests();

        // then
        assertThat(result, equalTo(expectedList));
    }

    @Test
    public void testGetReturnRequestsByOrder() {
        // given
        OrderModel order = mock(OrderModel.class);
        List<Object> expectedList = new ArrayList<>();

        // when
        when(rmaService.getReturnRequestsByOrder(order)).thenReturn(expectedList);

        List<Object> result = defaultDistReturnRequestFacade.getReturnRequestsByOrder(order);

        // then
        assertThat(result, equalTo(expectedList));
    }

    @Test
    public void testGetReturnRequest() {
        //
        String rmaCode = "RMA123";
        Object expectedResponse = mock(Object.class);

        // when
        when(rmaService.getReturnRequest(rmaCode)).thenReturn(expectedResponse);

        Object result = defaultDistReturnRequestFacade.getReturnRequest(rmaCode);

        assertThat(result, equalTo(expectedResponse));
    }

    @Test
    public void testCreateRMACreateRequest() {
        // given
        CreateRMARequestForm form = mock(CreateRMARequestForm.class);
        RMACreateRequest rmaCreateRequest = mock(RMACreateRequest.class);
        RMACreateResponse rmaCreateResponse = mock(RMACreateResponse.class);
        RMACreateRespOrder rmaCreateRespOrder = mock(RMACreateRespOrder.class);

        // when
        when(distCreateRMARequestConverter.convert(any(CreateRMARequestForm.class), any(RMACreateRequest.class))).thenReturn(rmaCreateRequest);
        when(returnOnlineService.rmaCreate(any(RMACreateRequest.class))).thenReturn(rmaCreateResponse);
        when(rmaCreateResponse.getRmaOrder()).thenReturn(rmaCreateRespOrder);

        CreateRMAResponseData result = defaultDistReturnRequestFacade.createRMACreateRequest(form);

        // then
        assertThat(result, is(notNullValue()));
    }

    @Test
    public void testSendGuestReturnRequestEmail() {
        // given
        GuestRMACreateRequestForm guestRMACreateRequestForm = mock(GuestRMACreateRequestForm.class);

        // when
        when(guestRMACreateRequestForm.getQuantity()).thenReturn(5L);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(mock(BaseSiteModel.class));

        defaultDistReturnRequestFacade.sendGuestReturnRequestEmail(guestRMACreateRequestForm);

        // then
        verify(eventService).publishEvent(any(DistRmaRequestEvent.class));
    }

    @Test
    public void testSendUserReturnRequestEmail() {
        // given
        DistReturnRequestFacade.UserRMARequestDataWrapper userRmaRequestDataWrapper = mock(DistReturnRequestFacade.UserRMARequestDataWrapper.class);
        CreateRMARequestForm createRMARequestForm = mock(CreateRMARequestForm.class);

        // when
        when(userRmaRequestDataWrapper.getCreateRMARequestForm()).thenReturn(createRMARequestForm);

        defaultDistReturnRequestFacade.sendUserReturnRequestEmail(userRmaRequestDataWrapper);

        // then
        verify(eventService).publishEvent(any(DistRmaRequestEvent.class));
    }

    @Test
    public void testGetReturnReasonsCorrectListSize() {
        // given
        int expectedSize = RmaMainReturnReasonTypes.values().length;

        // when
        List<DistMainReasonData> result = defaultDistReturnRequestFacade.getReturnReasons();

        // then
        assertThat(result.size(), equalTo(expectedSize));
    }

    @Test
    public void testGetReturnReasonsCorrectSubreasonMapping() {
        // when
        List<DistMainReasonData> reasons = defaultDistReturnRequestFacade.getReturnReasons();

        // when
        for (DistMainReasonData mainReason : reasons) {
            RmaMainReturnReasonTypes type = getByCode(mainReason.getMainReasonId());
            for (DistSubReasonData subReason : mainReason.getSubReasons()) {
                assertThat(type.getSubTypes().contains(RmaSubReturnReasonTypes.getByCode(subReason.getSubReasonId())), is(true));
            }
        }
    }

    private RmaMainReturnReasonTypes getByCode(String code) {
        return Stream.of(RmaMainReturnReasonTypes.values())
                     .filter(e -> code.equals(e.getCode()))
                     .findFirst()
                     .orElse(null);
    }
}
