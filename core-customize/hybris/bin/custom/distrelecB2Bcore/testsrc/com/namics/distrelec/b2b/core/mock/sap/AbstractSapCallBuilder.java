package com.namics.distrelec.b2b.core.mock.sap;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.mockito.ArgumentMatcher;
import org.mockito.stubbing.OngoingStubbing;

import static org.mockito.ArgumentMatchers.argThat;

abstract class AbstractSapCallBuilder<M, C, REQ, RES> {

    protected abstract OngoingStubbing<RES> buildStubbing(M mock, REQ request);

    protected abstract boolean matchRequest(C call, REQ request);

    protected abstract RES buildResponse(C call);

    public final void build(M mock, List<C> calls) {
        if (CollectionUtils.isEmpty(calls)) {
            // calls are not expected
            return;
        }

        // requests
        ArgumentMatcher<REQ> argumentMatcher = new ArgumentMatcher<REQ>() {
            private int callNo;
            @Override
            public boolean matches(Object o) {
                C call = calls.get(callNo++);
                return matchRequest(call, (REQ) o);
            }
        };

        // responses
        List<RES> responses = calls.stream().map(call -> buildResponse(call))
                .collect(Collectors.toList());

        OngoingStubbing<RES> stubbing = buildStubbing(mock, argThat(argumentMatcher));

        if (responses.size() > 1) {
            stubbing.thenReturn(responses.get(0), (RES[]) responses.subList(1, responses.size()).toArray());
        } else {
            stubbing.thenReturn(responses.get(0));
        }
    }
}
