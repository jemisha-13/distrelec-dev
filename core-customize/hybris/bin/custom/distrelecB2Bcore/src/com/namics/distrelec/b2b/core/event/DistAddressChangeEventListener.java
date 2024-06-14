/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.event.DistAddressChangeEvent.Address;
import com.namics.distrelec.b2b.core.model.process.DistAddressChangeProcessModel;

import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

public class DistAddressChangeEventListener extends AbstractEventListener<DistAddressChangeEvent> {

    @Override
    protected void onEvent(final DistAddressChangeEvent event) {
        final DistAddressChangeProcessModel process = getBusinessProcessService().createProcess("addressChange" + System.currentTimeMillis(),
                "addressChangeEmailProcess");

        process.setCustomerNumber(event.getCustomerNumber());
        process.setComment(event.getComment());
        final Address oldAddress = event.getOldAddress();
        if (oldAddress != null) {
            process.setOldCompanyName(oldAddress.getCompanyName());
            process.setOldFirstName(oldAddress.getFirstName());
            process.setOldLastName(oldAddress.getLastName());
            process.setOldDepartment(oldAddress.getDepartment());
            process.setOldStreet(oldAddress.getStreet());
            process.setOldNumber(oldAddress.getNumber());
            process.setOldPobox(oldAddress.getPobox());
            process.setOldZip(oldAddress.getZip());
            process.setOldPlace(oldAddress.getPlace());
            process.setOldCountry(oldAddress.getCountry());
        }

        final Address newAddress = event.getNewAddress();
        if (newAddress != null) {
            process.setNewCompanyName(newAddress.getCompanyName());
            process.setNewFirstName(newAddress.getFirstName());
            process.setNewLastName(newAddress.getLastName());
            process.setNewDepartment(newAddress.getDepartment());
            process.setNewStreet(newAddress.getStreet());
            process.setNewNumber(newAddress.getNumber());
            process.setNewPobox(newAddress.getPobox());
            process.setNewZip(newAddress.getZip());
            process.setNewPlace(newAddress.getPlace());
            process.setNewCountry(newAddress.getCountry());
        }

        process.setSite(event.getSite());
        process.setStore(event.getBaseStore());
        process.setEmailDisplayName(event.getEmailDisplayName());

        getModelServiceViaLookup().save(process);
        getBusinessProcessService().startProcess(process);
    }

    public BusinessProcessService getBusinessProcessService() {
        return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }

}
