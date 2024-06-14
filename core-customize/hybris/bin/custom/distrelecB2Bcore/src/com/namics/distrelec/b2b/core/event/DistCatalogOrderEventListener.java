/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.model.process.DistCatalogOrderProcessModel;

import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

public class DistCatalogOrderEventListener extends AbstractEventListener<DistCatalogOrderEvent> {

    @Override
    protected void onEvent(final DistCatalogOrderEvent event) {
        final DistCatalogOrderProcessModel process = getBusinessProcessService().createProcess("catalogOrder" + System.currentTimeMillis(),
                "catalogOrderEmailProcess");

        process.setCatalog(event.getCatalog());
        process.setCompanyName(event.getCompanyName());
        process.setCompanyName2(event.getCompanyName2());
        process.setSalutation(event.getSalutation());
        process.setFirstName(event.getFirstName());
        process.setLastName(event.getLastName());
        process.setDepartment(event.getDepartment());
        process.setStreet(event.getStreet());
        process.setNumber(event.getNumber());
        process.setPobox(event.getPobox());
        process.setZip(event.getZip());
        process.setPlace(event.getPlace());
        process.setDirectPhone(event.getDirectPhone());
        process.setMobile(event.getMobile());
        process.setFax(event.getFax());
        process.setEMail(event.getEMail());
        process.setComment(event.getComment());
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
