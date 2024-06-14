/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.batch.rowfilters;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexRowFilter;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * Filters out all unknown customers.
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 3.0
 * 
 */
public class CustomerRowFilter implements ImpexRowFilter {

    private final static Logger LOG = Logger.getLogger(CustomerRowFilter.class);

    private FlexibleSearchService flexibleSearchService;

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexRowFilter#filter(java.util.Map)
     */
    @Override
    public boolean filter(final Map<Integer, String> row) {
        return true;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    @Required
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
