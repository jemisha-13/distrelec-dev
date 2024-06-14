/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.model.process.DistQuoteProductPriceProcessModel;

import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Event listener for quote product price functionality.
 */
public class DistQuoteProductPriceEventListener extends AbstractEventListener<DistQuoteProductPriceEvent> {

    @Override
    protected void onEvent(final DistQuoteProductPriceEvent distQuoteProductPriceEvent) {
        final DistQuoteProductPriceProcessModel distQuoteProductPriceProcessModel = (DistQuoteProductPriceProcessModel) getBusinessProcessService()
                .createProcess("quoteProductPrice" + System.currentTimeMillis(), "quoteProductPriceEmailProcess");

        distQuoteProductPriceProcessModel.setSite(distQuoteProductPriceEvent.getSite());
        distQuoteProductPriceProcessModel.setQuantity(distQuoteProductPriceEvent.getQuantity());
        distQuoteProductPriceProcessModel.setComment(distQuoteProductPriceEvent.getComment());
        distQuoteProductPriceProcessModel.setProduct(distQuoteProductPriceEvent.getProduct());
        distQuoteProductPriceProcessModel.setCustomer(distQuoteProductPriceEvent.getCustomer());
        distQuoteProductPriceProcessModel.setCompany(distQuoteProductPriceEvent.getCompany());
        distQuoteProductPriceProcessModel.setFirstName(distQuoteProductPriceEvent.getFirstName());
        distQuoteProductPriceProcessModel.setLastName(distQuoteProductPriceEvent.getLastName());
        distQuoteProductPriceProcessModel.setCustomerEmail(distQuoteProductPriceEvent.getCustomerEmail());
        distQuoteProductPriceProcessModel.setPhoneNumber(distQuoteProductPriceEvent.getPhone());

        // Save and start the process
        getModelServiceViaLookup().save(distQuoteProductPriceProcessModel);
        getBusinessProcessService().startProcess(distQuoteProductPriceProcessModel);
    }

    public BusinessProcessService getBusinessProcessService() {
        return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }
}
