/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.wishlist;

/**
 * Transient Wishlist Data Object with minimal Information.
 * 
 * @author rlehmann, Namics AG
 * @since Distrelec 2.0.19 (DISTRELEC-4303)
 * 
 */
public class TransientMiniWishlistModel {

    private String name;
    private String uniqueId;
    private int totalUnitCount;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getTotalUnitCount() {
        return totalUnitCount;
    }

    public void setTotalUnitCount(final int totalUnitCount) {
        this.totalUnitCount = totalUnitCount;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(final String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
