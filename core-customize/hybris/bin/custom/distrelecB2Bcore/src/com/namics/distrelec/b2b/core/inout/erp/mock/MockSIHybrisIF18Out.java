/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.mock;

import com.distrelec.webservice.if18.v1.CreateQuotationRequest;
import com.distrelec.webservice.if18.v1.CreateQuotationResponse;
import com.distrelec.webservice.if18.v1.P1FaultMessage;
import com.distrelec.webservice.if18.v1.ReadQuotationsRequest;
import com.distrelec.webservice.if18.v1.ReadQuotationsResponse;
import com.distrelec.webservice.if18.v1.SIHybrisIF18V1Out;
import com.distrelec.webservice.if18.v1.SearchQuotationsRequest;
import com.distrelec.webservice.if18.v1.SearchQuotationsResponse;
import com.namics.distrelec.b2b.core.annotations.LogInOut;
import org.apache.logging.log4j.spi.StandardLevel;

import java.math.BigInteger;

public class MockSIHybrisIF18Out implements SIHybrisIF18V1Out {

    @Override
    @LogInOut(StandardLevel.INFO)
    public ReadQuotationsResponse if18V1ReadQuotation(final ReadQuotationsRequest readQuotationsRequest) throws P1FaultMessage {
        // YTODO Auto-generated method stub
        return null;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public SearchQuotationsResponse if18V1SearchQuotations(final SearchQuotationsRequest searchQuotationsRequest) throws P1FaultMessage {
        final SearchQuotationsResponse searchQuotationsResponse = new SearchQuotationsResponse();
        searchQuotationsResponse.setCustomerId(searchQuotationsRequest.getCustomerId());
        searchQuotationsResponse.setResultTotalSize(BigInteger.ZERO);
        return searchQuotationsResponse;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public CreateQuotationResponse if18V1CreateQuotation(final CreateQuotationRequest createQuotationRequest) throws P1FaultMessage {
        throw new UnsupportedOperationException();
    }

}
