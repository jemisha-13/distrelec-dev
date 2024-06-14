/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.site.dao;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;

import de.hybris.platform.basecommerce.site.dao.BaseSiteDao;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;

/**
 * Dao to retrieve BaseSite related data for {@link com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService}.
 * 
 * @author rmeier, Namics AG
 * @since Distrelec 1.0
 */
public interface DistrelecBaseSiteDao extends BaseSiteDao {

    CMSSiteModel findBaseSiteByCountryAndBrand(final String country, final String brand);

    CMSSiteModel findBaseSiteByCountry(final CountryModel country);

    CMSSiteModel findBaseSiteByCountryAndSalesOrg(final CountryModel country, final DistSalesOrgModel salesOrg);

}
