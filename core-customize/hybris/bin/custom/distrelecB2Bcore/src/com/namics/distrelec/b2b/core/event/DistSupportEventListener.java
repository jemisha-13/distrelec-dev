/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.model.process.DistSupportProcessModel;

import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Event listener for send feedback functionality.
 */
public class DistSupportEventListener extends AbstractEventListener<DistSupportEvent> {

    @Override
    protected void onEvent(final DistSupportEvent supportEvent) {
        final DistSupportProcessModel supportProcessModel = getBusinessProcessService().createProcess("sendSupport" + System.currentTimeMillis(),
                "supportEmailProcess");

        supportProcessModel.setSite(supportEvent.getSite());
        supportProcessModel.setStore(supportEvent.getBaseStore());
        supportProcessModel.setTitle(supportEvent.getTitle());
        supportProcessModel.setFirstname(supportEvent.getFirstname());
        supportProcessModel.setLastname(supportEvent.getLastname());
        supportProcessModel.setEmail(supportEvent.getEmail());
        supportProcessModel.setPhone(supportEvent.getPhone());
        supportProcessModel.setContactBy(supportEvent.getContactBy());
        supportProcessModel.setComment(supportEvent.getComment());
        supportProcessModel.setEmailDisplayName(supportEvent.getEmailDisplayName());
        supportProcessModel.setEmailSubjectMsg(supportEvent.getEmailSubjectMsg());
        supportProcessModel.setFromDisplayName(supportEvent.getFromDisplayName());

        getModelServiceViaLookup().save(supportProcessModel);
        getBusinessProcessService().startProcess(supportProcessModel);
    }

    public BusinessProcessService getBusinessProcessService() {
        return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }
}
