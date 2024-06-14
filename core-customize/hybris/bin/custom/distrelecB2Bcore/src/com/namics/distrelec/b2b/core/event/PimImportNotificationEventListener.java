/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.cache.DistAlternativesCacheEvictionStrategy;
import com.namics.distrelec.b2b.core.model.process.PimImportNotificationProcessModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * PimImportNotificationEventListener.
 * 
 * @author datkuppuras, Namics AG
 * @since Distrelec 1.1
 */
public class PimImportNotificationEventListener extends AbstractEventListener<PimImportNotificationEvent> {

    @Autowired
    private DistAlternativesCacheEvictionStrategy distAlternativesCacheEvictionStrategy;

    @Override
    protected void onEvent(final PimImportNotificationEvent event) {
        distAlternativesCacheEvictionStrategy.evictAllAlternativesCache();
        final PimImportNotificationProcessModel pimImportNotificationProcess = (PimImportNotificationProcessModel) getBusinessProcessService().createProcess(
                "pimImportNotification" + System.currentTimeMillis(), "pimImportNotificationEmailProcess");

        pimImportNotificationProcess.setImportSuccessful(event.isSuccessful());
        pimImportNotificationProcess.setLogMessage(event.getLogMessage());
        pimImportNotificationProcess.setStartTime(event.getStartTime());
        pimImportNotificationProcess.setEndTime(event.getEndTime());
        pimImportNotificationProcess.setPimLogStatistics(event.getPimLogStatistics());
        // Save and start the process
        getModelServiceViaLookup().save(pimImportNotificationProcess);
        getBusinessProcessService().startProcess(pimImportNotificationProcess);
    }

    public BusinessProcessService getBusinessProcessService() {
        return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }
}
