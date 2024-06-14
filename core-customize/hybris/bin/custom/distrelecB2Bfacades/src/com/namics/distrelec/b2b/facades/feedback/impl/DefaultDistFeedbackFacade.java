/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.feedback.impl;

import com.namics.distrelec.b2b.core.event.DistFeedbackEvent;
import com.namics.distrelec.b2b.core.event.FeedbackDataDto;
import com.namics.distrelec.b2b.core.feedback.service.DistFeedbackService;
import com.namics.distrelec.b2b.core.model.feedback.DistFeedbackModel;
import com.namics.distrelec.b2b.facades.feedback.DistFeedbackFacade;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultDistFeedbackFacade implements DistFeedbackFacade {

    @Autowired
    private EventService eventService;

    @Autowired
    private BaseStoreService baseStoreService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DistFeedbackService distFeedbackService;

    @Autowired
    private Converter<FeedbackDataDto, DistFeedbackModel> distFeedbackReverseConverter;

    @Override
    public void sendFeedback(final DistFeedbackEvent feedback) {
        setUpAdditionalEventData(feedback);
        feedback.setSearchFeedback(false);
        getEventService().publishEvent(feedback);
    }

    @Override
    public void sendSearchFeedback(final DistFeedbackEvent feedback) {
        setUpAdditionalEventData(feedback);
        feedback.setSearchFeedback(true);
        getEventService().publishEvent(feedback);
    }

    @Override
    public void submitFeedbackData(final FeedbackDataDto feedbackData) {
        DistFeedbackModel distFeedback = getDistFeedbackReverseConverter().convert(feedbackData);
        getDistFeedbackService().createFeedback(distFeedback);
    }

    protected void setUpAdditionalEventData(final DistFeedbackEvent distFeedbackEvent) {
        distFeedbackEvent.setBaseStore(getBaseStoreService().getCurrentBaseStore());
        distFeedbackEvent.setSite(getBaseSiteService().getCurrentBaseSite());
    }

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

    public DistFeedbackService getDistFeedbackService() {
        return distFeedbackService;
    }

    public void setDistFeedbackService(final DistFeedbackService distFeedbackService) {
        this.distFeedbackService = distFeedbackService;
    }

    public Converter<FeedbackDataDto, DistFeedbackModel> getDistFeedbackReverseConverter() {
        return distFeedbackReverseConverter;
    }

    public void setDistFeedbackReverseConverter(final Converter<FeedbackDataDto, DistFeedbackModel> distFeedbackReverseConverter) {
        this.distFeedbackReverseConverter = distFeedbackReverseConverter;
    }
}
