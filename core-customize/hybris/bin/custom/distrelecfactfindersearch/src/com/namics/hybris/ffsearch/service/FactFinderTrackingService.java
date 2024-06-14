/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.service;

import com.namics.hybris.ffsearch.data.tracking.FactFinderTrackingData;

/**
 * Tracking interface. Its purpose is to log tracking information in the factfinder instance.
 */
public interface FactFinderTrackingService {

    /**
     * Add the given key/value map of data to the FactFinder tracking instance.
     * 
     * @param data
     *            FactFinderTrackingData to add. See https://wiki.namics.com/display/distrelint/506-003+-+trackUserAction for details.
     * @return true, if the tracking information could successfully be added. False, otherwise.
     */
    boolean trackData(FactFinderTrackingData data);
}
