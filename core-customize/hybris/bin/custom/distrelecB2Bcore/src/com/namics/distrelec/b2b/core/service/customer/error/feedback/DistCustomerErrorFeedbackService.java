/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.customer.error.feedback;

import com.namics.distrelec.b2b.core.customer.error.feedback.data.DistCustomerErrorFeedbackData;

/**
 * {@code DistCustomerErrorFeedbackService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.13
 */
public interface DistCustomerErrorFeedbackService {

    public boolean saveCustomerErrorFeedback(final DistCustomerErrorFeedbackData data);

    public void disseminateCustomerErrorFeedback(final DistCustomerErrorFeedbackData data);
}
