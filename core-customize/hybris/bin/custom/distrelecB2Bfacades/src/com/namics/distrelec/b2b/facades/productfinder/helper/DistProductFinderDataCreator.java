/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.helper;

import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderConfigurationModel;
import com.namics.distrelec.b2b.facades.productfinder.data.DistProductFinderData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;

/**
 * Helper class with methods to create ProductFinder data.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public interface DistProductFinderDataCreator {

    DistProductFinderData createProductFinderData(final DistProductFinderConfigurationModel configuration,
            final FactFinderProductSearchPageData<?, ?> searchPageData);

}
