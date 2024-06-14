/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.site;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.CountryModel;

/**
 * Distrelec specific Service that extends {@link CMSSiteService} to retrieve site depending on country.
 * 
 * @author rmeier, Namics AG
 * @since Distrelec 1.0
 * 
 */
public interface DistrelecCMSSiteService extends CMSSiteService {

    CMSSiteModel getSiteForCountryAndBrand(final String countryIsoCode, final String brand) throws CMSItemNotFoundException;

    CMSSiteModel getSiteForCountry(final CountryModel country);

    CMSSiteModel getSiteForCountryAndSalesOrg(final CountryModel country, final DistSalesOrgModel salesOrg);

    boolean isViewedInSharedInternationalSite();

    CMSSiteModel getInternationalSite();
}
