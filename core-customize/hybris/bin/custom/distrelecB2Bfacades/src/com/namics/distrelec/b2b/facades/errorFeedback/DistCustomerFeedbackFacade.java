package com.namics.distrelec.b2b.facades.errorFeedback;

import com.namics.distrelec.b2b.core.customer.error.feedback.data.DistCustomerErrorFeedbackData;

public interface DistCustomerFeedbackFacade {

    /* Saves the feedbackReport to DistCustomerErrorFeedbackModel */
    public boolean saveFeedbackReport(DistCustomerErrorFeedbackData data);

    /* Sends an email to the data governance team */
    public void disseminateFeedbackRepor(DistCustomerErrorFeedbackData data);

}
