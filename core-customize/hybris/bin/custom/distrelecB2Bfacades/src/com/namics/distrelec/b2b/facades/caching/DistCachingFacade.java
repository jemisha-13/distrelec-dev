/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.caching;


public interface DistCachingFacade {

    /**
     * .
     * 
     * @return Returns navigation caching key
     */
    String getCachingKeyMainnav();

    /**
     * .
     * 
     * @return Returns footer caching key
     */
    String getCachingKeyFooter();

    /**
     * .
     * 
     * @return Returns homepage caching key
     */
    String getCachingKeyHomepage();

    /**
     * .
     * 
     * @return Returns footer caching duration in seconds
     */
    int getCachingTimeFooter();

    /**
     * .
     * 
     * @return Returns homepage caching duration in seconds
     */
    int getCachingTimeHomepage();

    /**
     * .
     * 
     * @return Returns navigation caching duration in seconds
     */
    int getCachingTimeMainnav();

    int getCachingTimeCategoryLink();
}
