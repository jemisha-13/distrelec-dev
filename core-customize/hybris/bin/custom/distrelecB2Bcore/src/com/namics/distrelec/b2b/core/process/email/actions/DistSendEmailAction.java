package com.namics.distrelec.b2b.core.process.email.actions;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DistSendEmailAction extends AbstractSimpleDecisionAction<BusinessProcessModel> {


    private static final Logger LOG = LogManager.getLogger(DistSendEmailAction.class);

    private EmailService emailService;

    protected EmailService getEmailService(){
        return emailService;
    }

    public void setEmailService(final EmailService emailService){
        this.emailService = emailService;
    }

    @Override
    public Transition executeAction(final BusinessProcessModel businessProcessModel){
        boolean hasEmailBeenSent = true;
        for (final EmailMessageModel email : businessProcessModel.getEmails()) {
            hasEmailBeenSent = getEmailService().send(email);
            if(!hasEmailBeenSent){
                businessProcessModel.setState(ProcessState.FAILED);

                final String fromAddress = null != email.getFromAddress() ? email.getFromAddress().getEmailAddress() : "NULL";
                LOG.error("{} Can not send email from {} subject {}.", DistConstants.ErrorLogCode.EMAIL_ERROR.getCode(), fromAddress, email.getSubject());
                break;
            }
        }
        return hasEmailBeenSent ? Transition.OK : Transition.NOK;
    }
}
