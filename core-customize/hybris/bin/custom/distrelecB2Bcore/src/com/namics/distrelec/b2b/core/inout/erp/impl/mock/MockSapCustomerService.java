/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl.mock;

import org.apache.logging.log4j.spi.StandardLevel;

import com.namics.distrelec.b2b.core.annotations.LogInOut;
import com.namics.distrelec.b2b.core.inout.erp.exception.ErpCommunicationException;
import com.namics.distrelec.b2b.core.inout.erp.impl.SapCustomerService;

import de.hybris.platform.b2b.model.B2BUnitModel;

public class MockSapCustomerService extends SapCustomerService {

    @Override
    @LogInOut(StandardLevel.INFO)
    public B2BUnitModel readCustomer(final B2BUnitModel customer, final boolean updateCurrentUserOnly) throws ErpCommunicationException {
        return customer;
    }


}
