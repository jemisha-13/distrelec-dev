/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import java.util.Date;

import de.hybris.platform.servicelayer.event.events.AbstractEvent;

/**
 * PimImportNotificationEvent.
 * 
 * @author datkuppuras, Namics AG
 * @since Distrelec 1.1
 */
public class PimImportNotificationEvent extends AbstractEvent {

    private boolean successful;
    private String logMessage;
    private Date startTime;
    private Date endTime;
    private String pimLogStatistics;

    /**
     * Create a new instance of {@code PimImportNotificationEvent}
     */
    public PimImportNotificationEvent() {
        this(false, null, null, null, null);
    }

    /**
     * Create a new instance of {@code PimImportNotificationEvent}
     * 
     * @param successful
     * @param logMessage
     * @param startTime
     * @param endTime
     * @param pimLogStatistics
     */
    public PimImportNotificationEvent(final boolean successful, final String logMessage, final Date startTime, final Date endTime, final String pimLogStatistics) {
        this.successful = successful;
        this.logMessage = logMessage;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pimLogStatistics = pimLogStatistics;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(final String logMessage) {
        this.logMessage = logMessage;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }

    public String getPimLogStatistics() {
        return pimLogStatistics;
    }

    public void setPimLogStatistics(final String pimLogStatistics) {
        this.pimLogStatistics = pimLogStatistics;
    }
}
