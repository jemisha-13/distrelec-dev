/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.service;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.hybris.ffsearch.model.DistFactFinderExportChannelModel;

import de.hybris.platform.core.model.c2l.LanguageModel;

/**
 * FactFinder channel interface.
 * 
 */
public interface FactFinderChannelService {

    /**
     * Detects the name of the current valid FactFinder channel defined by the current {@link DistSalesOrgModel} and the current
     * {@link LanguageModel}. </br>
     * e.g. "distrelec_7310_ch_de" for the SalesOrg "switzerland" and the language "german".
     * 
     * @return name of the current FactFinder channel as returned by {@link DistFactFinderExportChannelModel#getChannel()}, the empty string
     *         if none could be found
     */
    String getCurrentFactFinderChannel();

    String getCurrentFactFinderChannelCode();

    String getSwissGermanChannel();

    DistFactFinderExportChannelModel getChannelForCode(final String code);
}
