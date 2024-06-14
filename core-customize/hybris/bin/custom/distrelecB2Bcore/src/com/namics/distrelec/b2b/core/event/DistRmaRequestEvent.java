/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

/**
 * {@code DistRmaRequestEvent}
 * <p>
 * Event object for RMA request functionality.
 * </p>
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class DistRmaRequestEvent extends AbstractCommerceUserEvent<BaseSiteModel> {

    private String rmaCode;
    private String orderCode;
    private Date purchaseDate;
    private Date createdAt;
    private List<DistRmaRequestEventEntry> entries;
    private DistRmaRequestEventGuestEntry guestEntries;
    
    
    /**
     * Create a new instance of {@code DistRmaRequestEvent}
     */
    public DistRmaRequestEvent() {
        this(null, null, null, null);
    }

    /**
     * Create a new instance of {@code DistRmaRequestEvent}
     */
    public DistRmaRequestEvent(final String rmaCode, final String orderCode, final Date purchaseDate, final DistRmaRequestEventGuestEntry guestEntries) {
        this.rmaCode = rmaCode;
        this.orderCode = orderCode;
        this.purchaseDate = purchaseDate;
        this.entries = new ArrayList<DistRmaRequestEventEntry>();
        this.guestEntries = guestEntries;
    }

    public String getRmaCode() {
        return rmaCode;
    }

    public void setRmaCode(String rmaCode) {
        this.rmaCode = rmaCode;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(final String orderCode) {
        this.orderCode = orderCode;
    }

    public List<DistRmaRequestEventEntry> getEntries() {
        return entries;
    }

    public void setEntries(final List<DistRmaRequestEventEntry> entries) {
        this.entries = entries;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(final Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public DistRmaRequestEventGuestEntry getGuestEntries() {
        return guestEntries;
    }

    public void setGuestEntries(DistRmaRequestEventGuestEntry guestEntries) {
        this.guestEntries = guestEntries;
    }
    
}
