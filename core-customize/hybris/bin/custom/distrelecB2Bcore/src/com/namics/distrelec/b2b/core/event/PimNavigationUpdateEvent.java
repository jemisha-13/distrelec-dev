/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.servicelayer.event.events.AbstractEvent;

/**
 * {@code PimNavigationUpdateEvent}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.12
 */
public class PimNavigationUpdateEvent extends AbstractEvent {

    private final String language;
    private final boolean masterImport;
    private final boolean successful;

    /**
     * Create a new instance of {@code PimNavigationUpdateEvent}
     */
    public PimNavigationUpdateEvent() {
        this(null, false, false);
    }

    /**
     * Create a new instance of {@code PimNavigationUpdateEvent}
     */
    public PimNavigationUpdateEvent(final String language, final boolean masterImport, final boolean successful) {
        this.language = language;
        this.masterImport = masterImport;
        this.successful = successful;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isMasterImport() {
        return masterImport;
    }
}
