/*
 * Copyright 2000-2012 Namics AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.storesession;

import javax.servlet.http.HttpServletRequest;

import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;

/**
 * NamicsStoreSessionFacade extends StoreSessionFacade.
 *
 * @author rhusi, Namics AG
 * @since Namics Extensions 1.0
 *
 */
public interface NamicsStoreSessionFacade extends StoreSessionFacade {

    /**
     * Gets current country stored in session.
     *
     * @return current country for the current store.
     */
    CountryData getCurrentCountry();

    /**
     * Sets the current country.
     *
     * @param isocode
     *            country iso
     */
    void setCurrentCountry(String isocode);

    /**
     * Gets current region stored in session.
     *
     * @return current region for the current store.
     */
    RegionData getCurrentRegion();

    /**
     * Sets the current region.
     *
     * @param isocode
     *            region iso
     */
    void setCurrentRegion(String isocode);

    /**
     * Initialise the session
     */
    void initializeSession(final HttpServletRequest request);
}
