/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.suggest;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Abstract suggestion fields.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
@SuppressWarnings("PMD")
abstract public class AbstractSuggestion {

    private String id;
    private String codeErpRelevant;
    private String name;
    private String url;
    private String img_url;

    // BEGIN GENERATED CODE

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(final String img_url) {
        this.img_url = img_url;
    }

    public String getCodeErpRelevant() {
        return codeErpRelevant;
    }

    public void setCodeErpRelevant(String codeErpRelevant) {
        this.codeErpRelevant = codeErpRelevant;
    }

}
