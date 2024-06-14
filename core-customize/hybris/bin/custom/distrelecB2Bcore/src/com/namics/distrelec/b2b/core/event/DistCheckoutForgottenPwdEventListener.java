package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.commerceservices.model.process.ForgottenPasswordProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Autowired;

public class DistCheckoutForgottenPwdEventListener extends AbstractEventListener<DistCheckoutForgottenPwdEvent> {

    @Autowired
    private ModelService modelService;

    @Autowired
    private BusinessProcessService businessProcessService;

    @Override
    protected void onEvent(final DistCheckoutForgottenPwdEvent forgottenPwdEvent) {
        final ForgottenPasswordProcessModel forgottenPasswordProcess = businessProcessService.createProcess(
                "forgottenPassword" + System.currentTimeMillis(), "forgottenPasswordEmailProcess");
        forgottenPasswordProcess.setSite(forgottenPwdEvent.getSite());
        forgottenPasswordProcess.setCustomer(forgottenPwdEvent.getCustomer());
        forgottenPasswordProcess.setToken(forgottenPwdEvent.getToken());
        forgottenPasswordProcess.setInCheckout(Boolean.TRUE);
        forgottenPasswordProcess.setStorefrontRequest(forgottenPwdEvent.isStorefrontRequest());
        modelService.save(forgottenPasswordProcess);
        businessProcessService.startProcess(forgottenPasswordProcess);
    }
}
