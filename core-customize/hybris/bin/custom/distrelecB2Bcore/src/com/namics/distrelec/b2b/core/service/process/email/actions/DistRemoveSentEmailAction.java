/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.process.email.actions;

import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.acceleratorservices.process.email.actions.RemoveSentEmailAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;

/**
 * Adds attachment deletion to RemoveSentEmailAction
 */
public class DistRemoveSentEmailAction extends RemoveSentEmailAction {
    @Override
    public void executeAction(final BusinessProcessModel businessProcessModel) {

        for (final EmailMessageModel emailMessage : businessProcessModel.getEmails()) {

            if (emailMessage.isSent()) {

                for (final EmailAttachmentModel attachment : emailMessage.getAttachments()) {
                    getModelService().remove(attachment);
                }
                getModelService().remove(emailMessage);
            }
        }
    }

}
