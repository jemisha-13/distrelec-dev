package com.namics.distrelec.b2b.core.mock.sap;

import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.distrelec.webservice.sap.v1.UpdateDefaultShippingMethodRequest;
import com.distrelec.webservice.sap.v1.UpdateDefaultShippingMethodResponse;
import org.mockito.stubbing.OngoingStubbing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class If09UpdateDefaultShippingMethodCallBuilder extends AbstractSapCallBuilder<SIHybrisV1Out, If09UpdateDefaultShippingMethod, UpdateDefaultShippingMethodRequest, UpdateDefaultShippingMethodResponse> {

    @Override
    protected OngoingStubbing<UpdateDefaultShippingMethodResponse> buildStubbing(SIHybrisV1Out mock, UpdateDefaultShippingMethodRequest request) {
        try {
            return when(mock.if09UpdateDefaultShippingMethod(request));
        } catch (P1FaultMessage e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean matchRequest(If09UpdateDefaultShippingMethod call, UpdateDefaultShippingMethodRequest request) {
        return call.getCustomerId().equals(request.getCustomerId()) && call.getShippingMethodCode().equals(request.getShippingMethodCode());
    }

    @Override
    protected UpdateDefaultShippingMethodResponse buildResponse(If09UpdateDefaultShippingMethod call) {
        UpdateDefaultShippingMethodResponse mock = mock(UpdateDefaultShippingMethodResponse.class);
        when(mock.isSuccessful()).thenReturn(call.isSuccessful());
        return mock;
    }
}
