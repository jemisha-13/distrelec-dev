/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.model.process.DistCatalogPlusPriceRequestProcessModel;

import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * {@code DistCatalogPlusPriceRequestEventListener}
 * <p>
 * Listener class for the {@code DistCatalogPlusPriceRequestEvent}
 * </p>
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class DistCatalogPlusPriceRequestEventListener extends AbstractEventListener<DistCatalogPlusPriceRequestEvent> {

    /** {@inheritDoc} */
    @Override
    protected void onEvent(final DistCatalogPlusPriceRequestEvent event) {
        final DistCatalogPlusPriceRequestProcessModel catalogPlusPriceRequestProcessModel = (DistCatalogPlusPriceRequestProcessModel) getBusinessProcessService()
                .createProcess("catalogPlusProductPriceRequest" + System.currentTimeMillis(), "catalogPlusProductPriceRequestEmailProcess");

        catalogPlusPriceRequestProcessModel.setSite(event.getSite());
        catalogPlusPriceRequestProcessModel.setStore(event.getBaseStore());
        catalogPlusPriceRequestProcessModel.setQuantity(event.getQuantity());
        catalogPlusPriceRequestProcessModel.setComment(event.getComment());
        catalogPlusPriceRequestProcessModel.setProducts(event.getProducts());
        catalogPlusPriceRequestProcessModel.setCustomer(event.getCustomer());
        catalogPlusPriceRequestProcessModel.setCompany(event.getCompany());
        catalogPlusPriceRequestProcessModel.setFirstName(event.getFirstName());
        catalogPlusPriceRequestProcessModel.setLastName(event.getLastName());
        catalogPlusPriceRequestProcessModel.setCustomerEmail(event.getCustomerEmail());
        catalogPlusPriceRequestProcessModel.setPhoneNumber(event.getPhone());

        // Save and start the process
        getModelServiceViaLookup().save(catalogPlusPriceRequestProcessModel);
        getBusinessProcessService().startProcess(catalogPlusPriceRequestProcessModel);
    }

    public BusinessProcessService getBusinessProcessService() {
        return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }
}
