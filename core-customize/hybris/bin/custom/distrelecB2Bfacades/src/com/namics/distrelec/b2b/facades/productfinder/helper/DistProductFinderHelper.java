/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.helper;

import com.namics.distrelec.b2b.facades.productfinder.data.DistProductFinderValueData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;

/**
 * Contains ProductFinder methods needed to create and update ProductFinder data.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public interface DistProductFinderHelper {

    boolean isValueAvailable(final DistProductFinderValueData value, final FactFinderProductSearchPageData<?, ?> searchPageData);

}
