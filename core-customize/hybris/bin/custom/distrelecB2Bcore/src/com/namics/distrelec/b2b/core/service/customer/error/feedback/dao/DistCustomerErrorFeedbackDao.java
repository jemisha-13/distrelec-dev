/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.customer.error.feedback.dao;

import com.namics.distrelec.b2b.core.customer.error.feedback.data.DistCustomerErrorFeedbackData;

/**
 * {@code DistCustomerErrorFeedbackDao}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.13
 */
public interface DistCustomerErrorFeedbackDao {

    public boolean create(final DistCustomerErrorFeedbackData data);
}
