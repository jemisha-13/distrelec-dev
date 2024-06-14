package com.namics.distrelec.b2b.core.mock.sap;

import com.distrelec.webservice.sap.v1.FindContactRequest;
import com.distrelec.webservice.sap.v1.FindContactResponse;
import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import org.mockito.stubbing.OngoingStubbing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class If08FindContactSapCallBuilder extends AbstractSapCallBuilder<SIHybrisV1Out, If08FindContact, FindContactRequest, FindContactResponse> {

    @Override
    protected OngoingStubbing<FindContactResponse> buildStubbing(SIHybrisV1Out mock, FindContactRequest request) {
        try {
            return when(mock.if08FindContact(request));
        } catch (P1FaultMessage e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean matchRequest(If08FindContact call, FindContactRequest request) {
        return call.getCustomerId().equals(request.getCustomerId());
    }

    @Override
    protected FindContactResponse buildResponse(If08FindContact call) {
        FindContactResponse mock = mock(FindContactResponse.class);
        when(mock.getContactId()).thenReturn(call.getContactId());
        when(mock.getCustomerId()).thenReturn(call.getCustomerId());
        when(mock.getCustomerStatus()).thenReturn(call.getCustomerStatus());
        when(mock.getContactStatus()).thenReturn(call.getContactStatus());
        return mock;
    }
}
