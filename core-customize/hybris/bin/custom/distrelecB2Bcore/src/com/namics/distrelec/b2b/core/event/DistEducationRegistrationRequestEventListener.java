/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.model.process.DistEducationRegistrationRequestProcessModel;

import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * {@code DistEducationRegistrationRequestEventListener}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 2.0
 */
public class DistEducationRegistrationRequestEventListener extends AbstractEventListener<DistEducationRegistrationRequestEvent> {

    @Override
    protected void onEvent(final DistEducationRegistrationRequestEvent event) {
        final DistEducationRegistrationRequestProcessModel educationRegistrationRequestProcessModel = (DistEducationRegistrationRequestProcessModel) getBusinessProcessService()
                .createProcess("educationRegistrationRequest" + System.currentTimeMillis(), "educationRegistrationRequestEmailProcess");

        // Populating the educationRegistrationRequestProcessModel from the event
        populate(event, educationRegistrationRequestProcessModel);
        // Save and start the process
        getModelServiceViaLookup().save(educationRegistrationRequestProcessModel);
        getBusinessProcessService().startProcess(educationRegistrationRequestProcessModel);
    }

    /**
     * Populate the {@code DistEducationRegistrationRequestProcessModel} with data from {@code DistEducationRegistrationRequestEvent}
     * 
     * @param event
     *            the source event
     * @param educationRegistrationRequestProcessModel
     *            the target process model
     */
    public void populate(final DistEducationRegistrationRequestEvent event,
            final DistEducationRegistrationRequestProcessModel educationRegistrationRequestProcessModel) {

        // Setting the process language, this is important for anonymous users
        educationRegistrationRequestProcessModel.setLanguage(event.getLanguage());
        educationRegistrationRequestProcessModel.setCustomer(event.getCustomer());
        educationRegistrationRequestProcessModel.setSite(event.getSite());
        educationRegistrationRequestProcessModel.setStore(event.getBaseStore());
        educationRegistrationRequestProcessModel.setEducationArea(event.getProfileArea());
        educationRegistrationRequestProcessModel.setContactFirstName(event.getContactFirstName());
        educationRegistrationRequestProcessModel.setContactLastName(event.getContactLastName());
        educationRegistrationRequestProcessModel.setContactAddress1(event.getContactAddress1());
        educationRegistrationRequestProcessModel.setContactAddress2(event.getContactAddress2());
        educationRegistrationRequestProcessModel.setContactZip(event.getContactZip());
        educationRegistrationRequestProcessModel.setContactPlace(event.getContactPlace());
        educationRegistrationRequestProcessModel.setContactCountry(event.getContactCountry());
        educationRegistrationRequestProcessModel.setContactEmail(event.getContactEmail());
        educationRegistrationRequestProcessModel.setContactPhoneNumber(event.getContactPhoneNumber());
        educationRegistrationRequestProcessModel.setContactMobileNumber(event.getContactMobileNumber());

        // Institution data
        educationRegistrationRequestProcessModel.setInstitutionName(event.getInstitutionName());
        educationRegistrationRequestProcessModel.setInstitutionAddress1(event.getInstitutionAddress1());
        educationRegistrationRequestProcessModel.setInstitutionAddress2(event.getInstitutionAddress2());
        educationRegistrationRequestProcessModel.setInstitutionZip(event.getInstitutionZip());
        educationRegistrationRequestProcessModel.setInstitutionPlace(event.getInstitutionPlace());
        educationRegistrationRequestProcessModel.setInstitutionCountry(event.getInstitutionCountry());
        educationRegistrationRequestProcessModel.setInstitutionPhoneNumber(event.getInstitutionPhoneNumber());
        educationRegistrationRequestProcessModel.setInstitutionEmail(event.getInstitutionEmail());
    }

    public BusinessProcessService getBusinessProcessService() {
        return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }
}
