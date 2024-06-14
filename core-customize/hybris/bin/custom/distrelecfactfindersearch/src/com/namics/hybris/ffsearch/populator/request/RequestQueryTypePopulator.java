/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.request;

import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;

import de.hybris.platform.converters.Populator;

/**
 * Populator for the search type and a additional code if one exist.
 * 
 * @author rlehmann, Namics AG
 * @since Distrelec 2.0.22
 * 
 */
public class RequestQueryTypePopulator implements Populator<SearchQueryData, SearchRequest> {

    @Override
    public void populate(final SearchQueryData source, final SearchRequest target) {
        target.setSearchType(source.getSearchType());
        target.setCode(source.getCode());
    }

}
