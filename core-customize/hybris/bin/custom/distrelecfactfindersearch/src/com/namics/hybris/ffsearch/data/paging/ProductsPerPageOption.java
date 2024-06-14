/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.paging;

/**
 * {@code ProductsPerPageOption}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class ProductsPerPageOption {

    private boolean defaultOption;
    private boolean selected;
    private int value;

    /**
     * Create a new instance of {@code ProductsPerPageOption}
     */
    public ProductsPerPageOption() {
        this(0, false, false);
    }

    /**
     * Create a new instance of {@code ProductsPerPageOption}
     * 
     * @param value
     * @param defaultOption
     * @param selected
     */
    public ProductsPerPageOption(final int value, final boolean defaultOption, final boolean selected) {
        this.value = value;
        this.defaultOption = defaultOption;
        this.selected = selected;
    }

    public boolean isDefault() {
        return defaultOption;
    }

    public void setDefault(final boolean defaultOption) {
        this.defaultOption = defaultOption;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }

    public int getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }
}
