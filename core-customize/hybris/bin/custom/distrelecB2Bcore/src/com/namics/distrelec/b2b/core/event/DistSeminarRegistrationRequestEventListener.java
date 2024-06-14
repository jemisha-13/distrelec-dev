/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.model.process.DistSeminarRegistrationRequestProcessModel;

import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * {@code DistSeminarRegistrationRequestEventListener}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 2.0
 */
public class DistSeminarRegistrationRequestEventListener extends AbstractEventListener<DistSeminarRegistrationRequestEvent> {

    @Override
    protected void onEvent(final DistSeminarRegistrationRequestEvent event) {
        final DistSeminarRegistrationRequestProcessModel seminarRegistrationRequestProcessModel = (DistSeminarRegistrationRequestProcessModel) getBusinessProcessService()
                .createProcess("seminarRegistrationRequest" + System.currentTimeMillis(), "seminarRegistrationRequestEmailProcess");

        // Populating the seminarRegistrationRequestProcessModel from the event
        populate(event, seminarRegistrationRequestProcessModel);
        // Save and start the process
        getModelServiceViaLookup().save(seminarRegistrationRequestProcessModel);
        getBusinessProcessService().startProcess(seminarRegistrationRequestProcessModel);
    }

    /**
     * Populate the {@code DistSeminarRegistrationRequestProcessModel} from the vent
     * 
     * @param source
     * @param target
     */
    protected void populate(final DistSeminarRegistrationRequestEvent source, final DistSeminarRegistrationRequestProcessModel target) {
        target.setCustomer(source.getCustomer());
        target.setSite(source.getSite());
        target.setTopic(source.getTopic());
        target.setSeminarDate(source.getDate());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setCustomerNumber(source.getCustomerNumber());
        target.setCompanyName(source.getCompanyName());
        target.setSalutation(source.getSalutation());
        target.setStreet(source.getStreet());
        target.setNumber(source.getNumber());
        target.setPobox(source.getPobox());
        target.setZip(source.getZip());
        target.setPlace(source.getPlace());
        target.setCountry(source.getCountry());
        target.setDirectPhone(source.getDirectPhone());
        target.setDepartment(source.getDepartment());
        target.setEMail(source.geteMail());
        target.setFax(source.getFax());
        target.setComment(source.getComment());
    }

    public BusinessProcessService getBusinessProcessService() {
        return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }
}
