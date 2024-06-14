/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.model.process.DistSendToFriendProcessModel;

import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Event listener for send to friend functionality.
 */
public class DistSendToFriendEventListener extends AbstractEventListener<DistSendToFriendEvent> {

    @Override
    protected void onEvent(final DistSendToFriendEvent sendToFriendEvent) {
        final DistSendToFriendProcessModel distSendToFriendProcessModel = (DistSendToFriendProcessModel) getBusinessProcessService().createProcess(
                "sendToFriend" + System.currentTimeMillis(), "sendToFriendEmailProcess");

        distSendToFriendProcessModel.setCustomer(sendToFriendEvent.getCustomer());
        distSendToFriendProcessModel.setSite(sendToFriendEvent.getSite());
        distSendToFriendProcessModel.setYourName(sendToFriendEvent.getYourName());
        distSendToFriendProcessModel.setYourEmail(sendToFriendEvent.getYourEmail());
        distSendToFriendProcessModel.setReceiverName(sendToFriendEvent.getReceiverName());
        distSendToFriendProcessModel.setReceiverEmail(sendToFriendEvent.getReceiverEmail());
        distSendToFriendProcessModel.setMessage(sendToFriendEvent.getMessage());

        // Additional attributes for different send to friend events
        distSendToFriendProcessModel.setUrl(sendToFriendEvent.getUrl());
        distSendToFriendProcessModel.setProduct(sendToFriendEvent.getProduct());
        distSendToFriendProcessModel.setAttachment(sendToFriendEvent.getAttachment());
        distSendToFriendProcessModel.setComparisonList(sendToFriendEvent.getComparisonList());
        distSendToFriendProcessModel.setCart(sendToFriendEvent.getCart());
        distSendToFriendProcessModel.setLanguage(sendToFriendEvent.getLanguage());
        // Save and start the process
        getModelServiceViaLookup().save(distSendToFriendProcessModel);
        getBusinessProcessService().startProcess(distSendToFriendProcessModel);
    }

    public BusinessProcessService getBusinessProcessService() {
        return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }
}
