package com.namics.distrelec.b2b.core.mock.sap;

import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.ReadCustomerRequest;
import com.distrelec.webservice.sap.v1.ReadCustomerResponse;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import org.mockito.stubbing.OngoingStubbing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class If08ReadCustomerSapCallBuilder extends AbstractSapCallBuilder<SIHybrisV1Out, If08ReadCustomer, ReadCustomerRequest, ReadCustomerResponse> {

    @Override
    protected OngoingStubbing<ReadCustomerResponse> buildStubbing(SIHybrisV1Out mock, ReadCustomerRequest request) {
        try {
            return when(mock.if08ReadCustomer(request));
        } catch (P1FaultMessage e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean matchRequest(If08ReadCustomer call, ReadCustomerRequest request) {
        return call.getCustomerId().equals(request.getCustomerId());
    }

    @Override
    protected ReadCustomerResponse buildResponse(If08ReadCustomer call) {
        ReadCustomerResponse mock = mock(ReadCustomerResponse.class);
        when(mock.getCustomerId()).thenReturn(call.getCustomerId());
        when(mock.getSalesOrganization()).thenReturn(call.getSalesOrganization());
        when(mock.getCustomerType()).thenReturn(call.getCustomerType());
        when(mock.getCurrency()).thenReturn(call.getCurrency());
        when(mock.getPriceList()).thenReturn(call.getPriceList());
        when(mock.isActive()).thenReturn(call.isActive());
        return mock;
    }
}
