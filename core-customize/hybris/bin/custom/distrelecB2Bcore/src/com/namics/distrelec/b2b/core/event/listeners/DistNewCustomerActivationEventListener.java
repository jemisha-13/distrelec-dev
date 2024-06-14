package com.namics.distrelec.b2b.core.event.listeners;

import com.namics.distrelec.b2b.core.event.DistNewCustomerActivationEvent;
import com.namics.distrelec.b2b.core.model.process.DistNewCustomerActivationProcessModel;

import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

public class DistNewCustomerActivationEventListener extends AbstractEventListener<DistNewCustomerActivationEvent> {
    private static final String PROCESS_NAME = "distNewCustomerActivationEmailProcess";

    public BusinessProcessService getBusinessProcessService() {
        return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }

    @Override
    protected void onEvent(DistNewCustomerActivationEvent distNewCustomerActivationEvent) {
        DistNewCustomerActivationProcessModel distNewCustomerActivationProcess = getBusinessProcessService().createProcess(generateProcessId(), PROCESS_NAME);
        distNewCustomerActivationProcess.setSite(distNewCustomerActivationEvent.getSite());
        distNewCustomerActivationProcess.setCustomer(distNewCustomerActivationEvent.getCustomer());
        distNewCustomerActivationProcess.setToken(distNewCustomerActivationEvent.getToken());
        getModelServiceViaLookup().save(distNewCustomerActivationProcess);
        getBusinessProcessService().startProcess(distNewCustomerActivationProcess);
    }

    private String generateProcessId() {
        return "newCustomerActivation" + System.currentTimeMillis();
    }
}
