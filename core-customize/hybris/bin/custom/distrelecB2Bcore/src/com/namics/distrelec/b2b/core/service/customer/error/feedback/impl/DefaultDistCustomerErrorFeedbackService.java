/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.customer.error.feedback.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.customer.error.feedback.data.DistCustomerErrorFeedbackData;
import com.namics.distrelec.b2b.core.event.DistCustomerErrorFeedbackEvent;
import com.namics.distrelec.b2b.core.service.customer.error.feedback.DistCustomerErrorFeedbackService;
import com.namics.distrelec.b2b.core.service.customer.error.feedback.dao.DistCustomerErrorFeedbackDao;

import de.hybris.platform.servicelayer.event.EventService;

/**
 * {@code DefaultDistCustomerErrorFeedbackService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.13
 */
public class DefaultDistCustomerErrorFeedbackService implements DistCustomerErrorFeedbackService {

    private static final Logger LOG = Logger.getLogger(DistCustomerErrorFeedbackService.class);

    @Autowired
    private DistCustomerErrorFeedbackDao customerErrorFeedbackDao;

    @Autowired
    private EventService eventService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.customer.error.feedback.DistCustomerErrorFeedbackService#saveCustomerErrorFeedback(com.namics.
     * distrelec.b2b.core.customer.error.feedback.data.DistCustomerErrorFeedbackData)
     */
    @Override
    public boolean saveCustomerErrorFeedback(final DistCustomerErrorFeedbackData data) {
        return getCustomerErrorFeedbackDao().create(data);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.customer.error.feedback.DistCustomerErrorFeedbackService#disseminateCustomerErrorFeedback(com.
     * namics.distrelec.b2b.core.customer.error.feedback.data.DistCustomerErrorFeedbackData)
     */
    @Override
    public void disseminateCustomerErrorFeedback(final DistCustomerErrorFeedbackData data) {
        try {
            getEventService().publishEvent(convertFeedbackData(data));
        } catch (Exception e) {
            LOG.error("Exception sending customer error feedback info to Business Process: " + e.getMessage(), e);
        }
    }

    private DistCustomerErrorFeedbackEvent convertFeedbackData(final DistCustomerErrorFeedbackData data) {
        final DistCustomerErrorFeedbackEvent event = new DistCustomerErrorFeedbackEvent();
        event.setProductId(data.getProductId());
        event.setProductName(data.getProductName());
        event.setCustomerEmailId(data.getCustomerEmailId());
        event.setCustomerName(data.getCustomerName());
        event.setErrorDescription(data.getErrorDescription());
        event.setErrorReason(data.getErrorReason());
        event.setErpCustomerId(data.getErpCustomerId());
        event.setLanguageIso(data.getLanguageIso());
        return event;
    }

    public DistCustomerErrorFeedbackDao getCustomerErrorFeedbackDao() {
        return customerErrorFeedbackDao;
    }

    public void setCustomerErrorFeedbackDao(final DistCustomerErrorFeedbackDao customerErrorFeedbackDao) {
        this.customerErrorFeedbackDao = customerErrorFeedbackDao;
    }

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(final EventService eventService) {
        this.eventService = eventService;
    }

}
