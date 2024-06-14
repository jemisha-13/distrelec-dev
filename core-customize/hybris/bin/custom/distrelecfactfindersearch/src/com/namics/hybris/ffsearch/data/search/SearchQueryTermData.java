/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.search;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class SearchQueryTermData {

    private String key;
    private String value;

    private Boolean substring;
    private Boolean exclude;

    // / BEGIN GENERATED CODE

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public Boolean getSubstring() {
        return substring;
    }

    public void setSubstring(final Boolean substring) {
        this.substring = substring;
    }

    public Boolean getExclude() {
        return exclude;
    }

    public void setExclude(final Boolean exclude) {
        this.exclude = exclude;
    }

}
