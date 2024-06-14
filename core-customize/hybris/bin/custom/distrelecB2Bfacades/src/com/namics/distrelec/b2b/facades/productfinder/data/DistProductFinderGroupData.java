/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.data;

import java.util.List;

/**
 * Product finder group data.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DistProductFinderGroupData {

    private String name;
    private DistProductFinderGroupType type;
    private List<DistProductFinderValueData> values;
    private DistProductFinderValueData customValue;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<DistProductFinderValueData> getValues() {
        return values;
    }

    public void setValues(final List<DistProductFinderValueData> values) {
        this.values = values;
    }

    public DistProductFinderGroupType getType() {
        return type;
    }

    public void setType(final DistProductFinderGroupType type) {
        this.type = type;
    }

    public DistProductFinderValueData getCustomValue() {
        return customValue;
    }

    public void setCustomValue(final DistProductFinderValueData customValue) {
        this.customValue = customValue;
    }

}
