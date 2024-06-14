/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import java.util.LinkedHashSet;

import com.namics.distrelec.b2b.core.model.process.DistRmaRequestGuestEntryModel;
import com.namics.distrelec.b2b.core.model.process.DistRmaRequestProcessEntryModel;
import com.namics.distrelec.b2b.core.model.process.DistRmaRequestProcessModel;

import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * DistRmaRequestEventListener.
 * 
 * 
 * @author nbenothman, Distrelec
 * @since Distrelec 3.0
 */
public class DistRmaRequestEventListener extends AbstractEventListener<DistRmaRequestEvent> {

    @Override
    protected void onEvent(final DistRmaRequestEvent rmaEvent) {
        final DistRmaRequestProcessModel rmaRequestProcessModel = (DistRmaRequestProcessModel) getBusinessProcessService()
                .createProcess("rmaRequest" + System.currentTimeMillis(), "rmaRequestEmailProcess");

        rmaRequestProcessModel.setLanguage(rmaEvent.getLanguage());
        rmaRequestProcessModel.setCustomer(rmaEvent.getCustomer());
        rmaRequestProcessModel.setCreatedAt(rmaEvent.getCreatedAt());

        if (rmaEvent.getGuestEntries() != null) {
            // populate request data for Guest User
            populateRequestForGuestUser(rmaEvent, rmaRequestProcessModel);
        } else {
            // Populate data for registered User
            populateRequestForRegisteredUser(rmaEvent, rmaRequestProcessModel);
        }

        // Save and start the process
        getModelServiceViaLookup().save(rmaRequestProcessModel);
        getBusinessProcessService().startProcess(rmaRequestProcessModel);
    }

    /**
     * Populate the {@code DistRmaRequestProcessModel} from the specified RMA event for registered user.
     * 
     * @param rmaEvent
     * @param rmaRequestProcessModel
     */
    protected void populateRequestForRegisteredUser(final DistRmaRequestEvent rmaEvent, final DistRmaRequestProcessModel rmaRequestProcessModel) {
        rmaRequestProcessModel.setCustomer(rmaEvent.getCustomer());
        rmaRequestProcessModel.setSite(rmaEvent.getSite());
        rmaRequestProcessModel.setStore(rmaEvent.getBaseStore());
        rmaRequestProcessModel.setOrderCode(rmaEvent.getOrderCode());
        rmaRequestProcessModel.setOrderDate(rmaEvent.getPurchaseDate());
        rmaRequestProcessModel.setPurchaseDate(rmaEvent.getPurchaseDate());
        rmaRequestProcessModel.setRmaCode(rmaEvent.getRmaCode());
        rmaRequestProcessModel.setRmaRequestProcessEntries(new LinkedHashSet<>());
        rmaRequestProcessModel.setLanguage(rmaEvent.getLanguage());
        for (final DistRmaRequestEventEntry eventEntry : rmaEvent.getEntries()) {
            final DistRmaRequestProcessEntryModel rmaRequestProcessEntry = getModelServiceViaLookup().create(DistRmaRequestProcessEntryModel.class);
            rmaRequestProcessEntry.setAmount(eventEntry.getAmount());
            rmaRequestProcessEntry.setComment(eventEntry.getComment());
            rmaRequestProcessEntry.setReturnReason(eventEntry.getReturnReason());
            rmaRequestProcessEntry.setReturnPackaging(eventEntry.getReturnPackaging());
            rmaRequestProcessEntry.setSerialNumbers(eventEntry.getSerialNumbers());
            rmaRequestProcessEntry.setProductNumber(eventEntry.getProductNumber());
            rmaRequestProcessEntry.setProductName(eventEntry.getProductName());
            rmaRequestProcessEntry.setReplacementNote(eventEntry.getReplacement());
            rmaRequestProcessModel.getRmaRequestProcessEntries().add(rmaRequestProcessEntry);
        }
    }
    
    
    /**
     * Populate the {@code DistRmaRequestProcessModel} from the specified RMA event for guest user.
     * 
     * @param rmaEvent
     * @param rmaRequestProcessModel
     */
    protected void populateRequestForGuestUser(final DistRmaRequestEvent rmaEvent, final DistRmaRequestProcessModel rmaRequestProcessModel) {
        
        // Set data regarding Guest
        DistRmaRequestGuestEntryModel distRmaRequestGuestEntryModel = new DistRmaRequestGuestEntryModel();
        distRmaRequestGuestEntryModel.setCustomerName(rmaEvent.getGuestEntries().getCustomerName());
        distRmaRequestGuestEntryModel.setEmailAddress(rmaEvent.getGuestEntries().getEmailAddress());
        distRmaRequestGuestEntryModel.setPhoneNumber(rmaEvent.getGuestEntries().getPhoneNumber());
        rmaRequestProcessModel.setGuestEntry(distRmaRequestGuestEntryModel);
        rmaRequestProcessModel.setLanguage(rmaEvent.getLanguage());
        
        // Set data regarding site, store
        rmaRequestProcessModel.setSite(rmaEvent.getSite());
        rmaRequestProcessModel.setStore(rmaEvent.getBaseStore());
        rmaRequestProcessModel.setOrderCode(rmaEvent.getOrderCode());
        
        // Set entries details
        rmaRequestProcessModel.setRmaRequestProcessEntries(new LinkedHashSet<>());
        for (final DistRmaRequestEventEntry eventEntry : rmaEvent.getEntries()) {
            final DistRmaRequestProcessEntryModel rmaRequestProcessEntry = getModelServiceViaLookup().create(DistRmaRequestProcessEntryModel.class);
            rmaRequestProcessEntry.setAmount(eventEntry.getAmount());
            rmaRequestProcessEntry.setComment(eventEntry.getComment());
            rmaRequestProcessEntry.setReturnReason(eventEntry.getReturnReason());
            rmaRequestProcessEntry.setProductNumber(eventEntry.getProductNumber());
            rmaRequestProcessModel.getRmaRequestProcessEntries().add(rmaRequestProcessEntry);
        }
    }

    public BusinessProcessService getBusinessProcessService() {
        return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }
}
