/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.suggest;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.google.common.collect.Lists;

public class AutocompleteCategoryResult {

    private List<CategorySuggestion> list;

    public AutocompleteCategoryResult() {
        this.list = Lists.newArrayList();
    }

    // BEGIN GENERATED CODE

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public int getCount() {
        return list.size();
    }

    public List<CategorySuggestion> getList() {
        return list;
    }

    public void setList(final List<CategorySuggestion> list) {
        this.list = list;
    }

}
