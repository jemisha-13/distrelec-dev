/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.data;

import java.util.Date;

/**
 * POJO for dealing with product replacement info in the FactFinder index, exported as JSON.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class DistFactFinderReplacementData {

    /**
     * Date when the product got outdated.
     */
    private Date eolDate;
    
    /**
     * Is the replacement product buyable?
     */
    private boolean buyable;
    
    /**
     * Reason why the product got replaced.
     */
    private String reason;

    // BEGIN GENERATED CODE

    public Date getEolDate() {
        return eolDate;
    }

    public void setEolDate(final Date eolDate) {
        this.eolDate = eolDate;
    }

    public boolean isBuyable() {
        return buyable;
    }

    public void setBuyable(final boolean buyable) {
        this.buyable = buyable;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(final String reason) {
        this.reason = reason;
    }

}
