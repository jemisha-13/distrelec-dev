/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.site.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.core.service.site.dao.DistrelecBaseSiteDao;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSSiteService;
import de.hybris.platform.core.model.c2l.CountryModel;

/**
 * Default implementation of {@link DistrelecCMSSiteService}.
 * 
 * @author rmeier, Namics AG
 * @since Distrelec 1.0
 * 
 */
public class DefaultDistrelecCMSSiteService extends DefaultCMSSiteService implements DistrelecCMSSiteService {

    private static final String INTERNATIONAL_SITE_UID = "distrelec";

    @Autowired
    private DistrelecBaseSiteDao distrelecBaseSiteDao;

    @Override
    public CMSSiteModel getSiteForCountryAndBrand(final String countryIsoCode, final String brand) throws CMSItemNotFoundException {

        final CMSSiteModel site = getDistrelecBaseSiteDao().findBaseSiteByCountryAndBrand(countryIsoCode, brand);
        if (site != null) {
            return site;
        } else {
            throw new CMSItemNotFoundException("No site found for country with isoCode [" + countryIsoCode + "] and brand [" + brand + "]");
        }
    }

    @Override
    public CMSSiteModel getSiteForCountry(final CountryModel country) {
        return getDistrelecBaseSiteDao().findBaseSiteByCountry(country);
    }

    @Override
    public CMSSiteModel getSiteForCountryAndSalesOrg(CountryModel country, DistSalesOrgModel salesOrg) {
        return getDistrelecBaseSiteDao().findBaseSiteByCountryAndSalesOrg(country, salesOrg);
    }

    public DistrelecBaseSiteDao getDistrelecBaseSiteDao() {
        return distrelecBaseSiteDao;
    }

    public void setDistrelecBaseSiteDao(DistrelecBaseSiteDao distrelecBaseSiteDao) {
        this.distrelecBaseSiteDao = distrelecBaseSiteDao;
    }

    @Override
    public boolean isViewedInSharedInternationalSite() {
        return getCurrentSite().getUid().equals(INTERNATIONAL_SITE_UID);
    }

    @Override
    public CMSSiteModel getInternationalSite() {
        return (CMSSiteModel) getDistrelecBaseSiteDao().findBaseSiteByUID(INTERNATIONAL_SITE_UID);
    }
}
