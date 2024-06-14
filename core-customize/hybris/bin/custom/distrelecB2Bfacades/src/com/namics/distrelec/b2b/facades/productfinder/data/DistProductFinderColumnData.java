/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.data;

import java.util.List;

/**
 * Product finder column data.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DistProductFinderColumnData {

    private List<DistProductFinderGroupData> groups;

    public List<DistProductFinderGroupData> getGroups() {
        return groups;
    }

    public void setGroups(final List<DistProductFinderGroupData> groups) {
        this.groups = groups;
    }

}
