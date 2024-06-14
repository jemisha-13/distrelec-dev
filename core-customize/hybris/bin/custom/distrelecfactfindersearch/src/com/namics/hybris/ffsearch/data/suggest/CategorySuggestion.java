/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.suggest;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * POJO for a category suggestion.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
@SuppressWarnings("PMD")
public class CategorySuggestion extends AbstractSuggestion {

    private int count;
    private String l1;

    // BEGIN GENERATED CODE

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public String getL1() {
        return l1;
    }

    public void setL1(final String l1) {
        this.l1 = l1;
    }

}
