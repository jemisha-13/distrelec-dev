/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.suggest;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.google.common.collect.Lists;


/**
 * {@code AutocompleteTermResult}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class AutocompleteTermResult {

    private List<TermSuggestion> list;

    /**
     * Create a new instance of {@code AutocompleteTermResult}
     */
    public AutocompleteTermResult() {
        this.list = Lists.<TermSuggestion>newArrayList();
    }

    // BEGIN GENERATED CODE

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public int getCount() {
        return list.size();
    }

    public List<TermSuggestion> getList() {
        return list;
    }

    public void setList(final List<TermSuggestion> list) {
        this.list = list;
    }
}
