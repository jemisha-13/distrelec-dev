package com.namics.distrelec.b2b.core.mock.sap;

import java.util.List;
import java.util.stream.Collectors;

import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.ReadShippingMethodsRequest;
import com.distrelec.webservice.sap.v1.ReadShippingMethodsResponse;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.distrelec.webservice.sap.v1.ShippingMethods;
import org.mockito.stubbing.OngoingStubbing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class If09ReadShippingMethodsCallBuilder extends AbstractSapCallBuilder<SIHybrisV1Out, If09ReadShippingMethods, ReadShippingMethodsRequest, ReadShippingMethodsResponse> {

    @Override
    protected OngoingStubbing<ReadShippingMethodsResponse> buildStubbing(SIHybrisV1Out mock, ReadShippingMethodsRequest request) {
        try {
            return when(mock.if09ReadShippingMethods(request));
        } catch (P1FaultMessage e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean matchRequest(If09ReadShippingMethods call, ReadShippingMethodsRequest request) {
        return call.getCustomerId().equals(request.getCustomerId());
    }

    @Override
    protected ReadShippingMethodsResponse buildResponse(If09ReadShippingMethods call) {
        List<ShippingMethods> shippingMethods =
                call.getShippingMethods().stream()
                        .map(this::mockReadShippingMethodResponse)
                        .collect(Collectors.toList());

        ReadShippingMethodsResponse mock = mock(ReadShippingMethodsResponse.class);
        when(mock.getCustomerId()).thenReturn(call.getCustomerId());
        when(mock.getShippingMethods()).thenReturn(shippingMethods);
        return mock;
    }

    private ShippingMethods mockReadShippingMethodResponse(If09ReadShippingMethod readShippingMethod) {
        ShippingMethods mock = mock(ShippingMethods.class);
        when(mock.getShippingMethodCode()).thenReturn(readShippingMethod.getShippingMethodCode());
        when(mock.isDefault()).thenReturn(readShippingMethod.isDefault());
        return mock;
    }
}
