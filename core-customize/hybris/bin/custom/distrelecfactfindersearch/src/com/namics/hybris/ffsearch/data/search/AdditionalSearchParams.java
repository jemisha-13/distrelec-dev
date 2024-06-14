/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.search;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Additional search parameters filled in the custom search parameters.
 * 
 * @author DAEHUSIR, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class AdditionalSearchParams {

    private String log;

    /**
     * If true all the available Facets are returned by the search and not only the most important ones.
     */
    private Boolean disableWebUseAttributeCount;

    private String additionalFacetCode;

    private Map<String, List<String>> otherParams;

    public String getLog() {
        return log;
    }

    public void setLog(final String log) {
        this.log = log;
    }

    public Boolean getDisableWebUseAttributeCount() {
        return disableWebUseAttributeCount;
    }

    public void setDisableWebUseAttributeCount(final Boolean disableWebUseAttributeCount) {
        this.disableWebUseAttributeCount = disableWebUseAttributeCount;
    }

    public String getAdditionalFacetCode() {
        return additionalFacetCode;
    }

    public void setAdditionalFacetCode(final String additionalFacetCode) {
        this.additionalFacetCode = additionalFacetCode;
    }

    public Map<String, List<String>> getOtherParams() {
        return otherParams;
    }

    public void setOtherParams(final Map<String, List<String>> otherParams) {
        this.otherParams = otherParams;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
