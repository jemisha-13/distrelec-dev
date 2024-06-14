/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.data;

/**
 * Data object to evaluate common and other attributes for compare view.
 * 
 * @author pnueesch, Namics AG
 * @since Distrelec 1.0
 * 
 */
public class DistCompareFeatureData implements Comparable<DistCompareFeatureData> {

    private Integer counter;
    private String code;
    private String name;
    private String description;
    private String visibility;
    private Integer position;

    public DistCompareFeatureData() {
        counter = Integer.valueOf(1);
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(final Integer counter) {
        this.counter = counter;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(final String visibility) {
        this.visibility = visibility;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(final Integer position) {
        this.position = position;
    }

    public void incrementCounter() {
        counter = Integer.valueOf(counter.intValue() + 1);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return code.equals(obj);
    }

    @Override
    public String toString() {
        return code;
    }

    @Override
    public int compareTo(final DistCompareFeatureData compareFeature) {
        int cmp = compareFeature.getCounter().compareTo(counter);
        if (cmp == 0 && visibility != null && compareFeature.getVisibility() != null) {
            cmp = visibility.compareTo(compareFeature.getVisibility());
        }
        if (cmp == 0 && position != null && compareFeature.getPosition() != null) {
            cmp = position.compareTo(compareFeature.getPosition());
        }
        return cmp;
    }

}
