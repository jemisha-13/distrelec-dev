/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.service;

import java.util.List;

import com.namics.hybris.ffsearch.model.DistFactFinderExportChannelModel;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;

/**
 * Interface with DAO methods related to {@link DistFactFinderExportChannelModel}.
 * 
 * @author bhauser, Namics AG
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public interface DistFactFinderChannelDao {

    List<DistFactFinderExportChannelModel> getAllChannels();

    /**
     * Returns the FactFinder export channel for a given CMS site and a language.
     * 
     * @param cmsSite
     *            the CMS site
     * @param language
     *            the language
     * @return the FactFinder export channel
     */
    DistFactFinderExportChannelModel getChannel(CMSSiteModel cmsSite, LanguageModel language);

}
