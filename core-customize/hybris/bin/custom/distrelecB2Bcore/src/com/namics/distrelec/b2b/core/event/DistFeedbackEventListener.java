/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.model.process.DistFeedbackProcessModel;

import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Event listener for send feedback functionality.
 */
public class DistFeedbackEventListener extends AbstractEventListener<DistFeedbackEvent> {

    @Override
    protected void onEvent(final DistFeedbackEvent feedbackEvent) {
        final DistFeedbackProcessModel feedbackProcessModel = getBusinessProcessService().createProcess("sendFeedback" + System.currentTimeMillis(),
                "feedbackEmailProcess");

        feedbackProcessModel.setSite(feedbackEvent.getSite());
        feedbackProcessModel.setStore(feedbackEvent.getBaseStore());
        feedbackProcessModel.setName(feedbackEvent.getName());
        feedbackProcessModel.setEmail(feedbackEvent.getEmail());
        feedbackProcessModel.setPhone(feedbackEvent.getPhone());
        feedbackProcessModel.setFeedback(feedbackEvent.getFeedback());
        feedbackProcessModel.setSearchFeedback(Boolean.valueOf(feedbackEvent.isSearchFeedback()));
        feedbackProcessModel.setFromDisplayName(feedbackEvent.getFromDisplayName());
        feedbackProcessModel.setEmailDisplayName(feedbackEvent.getEmailDisplayName());
        feedbackProcessModel.setEmailSubjectMsg(feedbackEvent.getEmailSubjectMsg());

        getModelServiceViaLookup().save(feedbackProcessModel);
        getBusinessProcessService().startProcess(feedbackProcessModel);
    }

    public BusinessProcessService getBusinessProcessService() {
        return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }
}
