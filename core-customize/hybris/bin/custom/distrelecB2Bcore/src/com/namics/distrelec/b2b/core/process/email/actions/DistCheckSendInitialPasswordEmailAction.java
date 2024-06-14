package com.namics.distrelec.b2b.core.process.email.actions;

import org.apache.commons.lang3.BooleanUtils;

import com.namics.distrelec.b2b.core.model.process.SetInitialPasswordProcessModel;

import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;

public class DistCheckSendInitialPasswordEmailAction extends AbstractSimpleDecisionAction<SetInitialPasswordProcessModel> {

    @Override
    public Transition executeAction(SetInitialPasswordProcessModel businessProcessModel) throws Exception {
        return BooleanUtils.isTrue(businessProcessModel.getSkipEmailSending()) ? Transition.NOK : Transition.OK;
    }
}
