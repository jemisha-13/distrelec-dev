/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.support.impl;

import com.namics.distrelec.b2b.core.event.DistSupportEvent;
import com.namics.distrelec.b2b.facades.support.DistSupportFacade;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultDistSupportFacade implements DistSupportFacade {

    @Autowired
    private EventService eventService;

    @Autowired
    private BaseStoreService baseStoreService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public void sendSupport(final DistSupportEvent support) {
        setUpAdditionalEventData(support);
        eventService.publishEvent(support);
    }

    protected void setUpAdditionalEventData(final DistSupportEvent distSupportEvent) {
        distSupportEvent.setBaseStore(baseStoreService.getCurrentBaseStore());
        distSupportEvent.setSite(baseSiteService.getCurrentBaseSite());
    }

    @Override
    public Collection<String> getContactByValues() {
        final List<String> contactByValues = new ArrayList<String>();
        contactByValues.add("phone");
        contactByValues.add("email");
        return contactByValues;
    }

    // BEGIN GENERATED CODE

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(final EventService eventService) {
        this.eventService = eventService;
    }

    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    // END GENERATED CODE
}
