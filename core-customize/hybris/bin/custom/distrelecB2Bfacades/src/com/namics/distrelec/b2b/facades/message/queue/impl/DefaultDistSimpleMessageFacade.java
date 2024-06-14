/*
 * Copyright 2000-2017 Distrelec AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.message.queue.impl;

import com.namics.distrelec.b2b.core.event.DistInternalLinkEvent;
import com.namics.distrelec.b2b.core.event.DistInternalLinkEventAsync;
import com.namics.distrelec.b2b.core.message.queue.model.RowType;
import com.namics.distrelec.b2b.facades.message.queue.DistSimpleMessageFacade;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * {@code DefaultDistSimpleMessageFacade}
 * <p>
 * Facade to call queuing service
 * </p>
 *
 * @author <a href="abhinay.jadhav@distrelec.com">Abhinay Jadhav</a>, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.1
 */
public class DefaultDistSimpleMessageFacade implements DistSimpleMessageFacade {

    final static Logger LOG = LogManager.getLogger(DefaultDistSimpleMessageFacade.class);

    private CMSSiteService cmsSiteService;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private EventService eventService;

    @Override
    public void sendMessage(String code, RowType type) {
        if (code == null || type == null) {
            return;
        }

        final CMSSiteModel cmsSiteModel = getCmsSiteService().getCurrentSite();
        final String language = getI18NService().getCurrentLocale().getLanguage();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Sending related message code: {}, site: {}, type: {}, language: {}", code, cmsSiteModel.getUid(), type, language);
        }
        DistInternalLinkEvent event = new DistInternalLinkEventAsync(code, type, cmsSiteModel.getUid(), language, false);
        getEventService().publishEvent(event);
    }

    // Getters & Setters

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public I18NService getI18NService() {
        return i18NService;
    }

    public void setI18NService(final I18NService i18NService) {
        this.i18NService = i18NService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(final EventService eventService) {
        this.eventService = eventService;
    }
}
