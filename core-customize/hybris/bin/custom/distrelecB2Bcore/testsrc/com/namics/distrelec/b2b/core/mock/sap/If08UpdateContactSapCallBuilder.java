package com.namics.distrelec.b2b.core.mock.sap;

import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.distrelec.webservice.sap.v1.UpdateContactRequest;
import com.distrelec.webservice.sap.v1.UpdateContactResponse;
import org.mockito.stubbing.OngoingStubbing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class If08UpdateContactSapCallBuilder extends AbstractSapCallBuilder<SIHybrisV1Out, If08UpdateContact, UpdateContactRequest, UpdateContactResponse> {
    @Override
    protected OngoingStubbing<UpdateContactResponse> buildStubbing(SIHybrisV1Out mock, UpdateContactRequest request) {
        try {
            return when(mock.if08UpdateContact(request));
        } catch (P1FaultMessage e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean matchRequest(If08UpdateContact call, UpdateContactRequest request) {
        return call.getCustomerId().equals(request.getCustomerId());
    }

    @Override
    protected UpdateContactResponse buildResponse(If08UpdateContact call) {
        UpdateContactResponse mock = mock(UpdateContactResponse.class);
        when(mock.isSuccessful()).thenReturn(call.isSuccessful());
        return mock;
    }
}
