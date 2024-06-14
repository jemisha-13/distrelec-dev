/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.feedback;

import com.namics.distrelec.b2b.core.event.DistFeedbackEvent;
import com.namics.distrelec.b2b.core.event.FeedbackDataDto;

import java.io.IOException;

public interface DistFeedbackFacade {

    public void submitFeedbackData(final FeedbackDataDto feedbackData);

    public void sendFeedback(final DistFeedbackEvent feedback);

    public void sendSearchFeedback(final DistFeedbackEvent feedback);
}
