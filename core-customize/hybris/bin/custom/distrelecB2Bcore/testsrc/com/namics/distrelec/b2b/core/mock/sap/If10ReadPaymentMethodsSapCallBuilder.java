package com.namics.distrelec.b2b.core.mock.sap;

import java.util.List;
import java.util.stream.Collectors;

import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.PaymentMethod;
import com.distrelec.webservice.sap.v1.ReadPaymentMethodsRequest;
import com.distrelec.webservice.sap.v1.ReadPaymentMethodsResponse;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import org.mockito.stubbing.OngoingStubbing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class If10ReadPaymentMethodsSapCallBuilder extends AbstractSapCallBuilder<SIHybrisV1Out, If10ReadPaymentMethods, ReadPaymentMethodsRequest, ReadPaymentMethodsResponse> {

    @Override
    protected OngoingStubbing<ReadPaymentMethodsResponse> buildStubbing(SIHybrisV1Out mock, ReadPaymentMethodsRequest request) {
        try {
            return when(mock.if10ReadPaymentMethods(request));
        } catch (P1FaultMessage e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean matchRequest(If10ReadPaymentMethods call, ReadPaymentMethodsRequest request) {
        return call.getCustomerId().equals(request.getCustomerId());
    }

    @Override
    protected ReadPaymentMethodsResponse buildResponse(If10ReadPaymentMethods call) {
        List<PaymentMethod> paymentMethods =
                call.getPaymentMethods().stream()
                        .map(this::mockReadPaymentMethodResponse)
                        .collect(Collectors.toList());

        ReadPaymentMethodsResponse mock = mock(ReadPaymentMethodsResponse.class);
        when(mock.getCustomerId()).thenReturn(call.getCustomerId());
        when(mock.getPaymentMethods()).thenReturn(paymentMethods);
        return mock;
    }

    private PaymentMethod mockReadPaymentMethodResponse(If10ReadPaymentMethod readPaymentMethod) {
        PaymentMethod mock = mock(PaymentMethod.class);
        when(mock.getPaymentMethodCode()).thenReturn(readPaymentMethod.getPaymentMethodCode());
        when(mock.isDefault()).thenReturn(readPaymentMethod.isDefault());
        return mock;
    }
}
