/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.paging;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * POJO for ascending and descending sorting.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public enum SortType {

    ASC("asc"), DSC("desc");

    private static final Logger LOG = LoggerFactory.getLogger(SortType.class);

    private String value;

    private SortType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SortType fromValue(final String value) {
        for (final SortType type : SortType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        LOG.error("Unable to parse a SortType from value {}. Returning SortType {} as default.", value, SortType.ASC);
        return ASC;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
