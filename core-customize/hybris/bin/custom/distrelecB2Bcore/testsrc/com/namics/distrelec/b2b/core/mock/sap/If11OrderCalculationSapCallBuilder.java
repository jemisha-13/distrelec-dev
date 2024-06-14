package com.namics.distrelec.b2b.core.mock.sap;

import java.util.List;
import java.util.stream.Collectors;

import com.distrelec.webservice.sap.v1.OrderCalculationRequest;
import com.distrelec.webservice.sap.v1.OrderCalculationResponse;
import com.distrelec.webservice.sap.v1.OrderEntryResponse;
import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import org.mockito.stubbing.OngoingStubbing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class If11OrderCalculationSapCallBuilder extends AbstractSapCallBuilder<SIHybrisV1Out, If11OrderCalculation, OrderCalculationRequest, OrderCalculationResponse> {

    @Override
    protected OngoingStubbing<OrderCalculationResponse> buildStubbing(SIHybrisV1Out mock, OrderCalculationRequest request) {
        try {
            return when(mock.if11OrderCalculation(request));
        } catch (P1FaultMessage e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean matchRequest(If11OrderCalculation call, OrderCalculationRequest request) {
        return call.getCustomerId().equals(request.getCustomerData().getCustomerId());
    }

    @Override
    protected OrderCalculationResponse buildResponse(If11OrderCalculation call) {
        List<OrderEntryResponse> entries =
                call.getEntries().stream()
                        .map(orderCalculationEntry -> mockOrderCalculationEntryResponse(orderCalculationEntry))
                        .collect(Collectors.toList());

        OrderCalculationResponse response = mock(OrderCalculationResponse.class);
        when(response.getOrderEntries()).thenReturn(entries);
        return response;
    }

    private OrderEntryResponse mockOrderCalculationEntryResponse(If11OrderCalculationEntry orderCalculationEntry) {
        OrderEntryResponse response = mock(OrderEntryResponse.class);
        when(response.getMaterialNumber()).thenReturn(orderCalculationEntry.getProductCode());
        return response;
    }
}
