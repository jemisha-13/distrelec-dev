/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.salesorg.impl;

import static java.util.Objects.nonNull;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.enums.DistSalesOrgEnum;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.core.service.site.dao.DistrelecBaseSiteDao;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * Default implementation of {@link DistSalesOrgService}.
 * 
 * @author dsivakumaran, Namics AG
 * 
 */
public class DefaultDistSalesOrgService implements DistSalesOrgService {

    @Autowired
    private DistrelecCMSSiteService cmsSiteService;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private DistrelecBaseSiteDao distrelecBaseSiteDao;

    @Override
    public DistSalesOrgModel getCurrentSalesOrg() {
        final CMSSiteModel currSite = cmsSiteService.getCurrentSite();
        if (currSite != null) {
            return currSite.getSalesOrg();
        }
        return null;
    }

    @Override
    public DistSalesOrgModel getSalesOrgForCode(final String code) throws ModelNotFoundException, AmbiguousIdentifierException {
        final DistSalesOrgModel salesOrgExample = new DistSalesOrgModel();
        salesOrgExample.setCode(code);
        return flexibleSearchService.getModelByExample(salesOrgExample);
    }

    @Override
    public DistSalesOrgModel getSalesOrgForCountryCodeAndBrandCode(final String countryCode, final String brandCode) throws ModelNotFoundException {
        final CMSSiteModel cmsSite = distrelecBaseSiteDao.findBaseSiteByCountryAndBrand(countryCode, brandCode);

        if (cmsSite != null && cmsSite.getSalesOrg() != null) {
            return cmsSite.getSalesOrg();
        } else {
            throw new ModelNotFoundException("Could not find DistSalesOrgModel for country [" + countryCode + "] and brand [" + brandCode + "]");
        }
    }

    @Override
    public boolean isCurrentSalesOrgItaly() {
        DistSalesOrgModel salesOrg = getCurrentSalesOrg();
        return nonNull(salesOrg) && StringUtils.equalsIgnoreCase(DistSalesOrgEnum.ITALY.getCode(), salesOrg.getCode());
    }

    @Override
    public boolean isCurrentSalesOrgExport() {
        DistSalesOrgModel salesOrg = getCurrentSalesOrg();
        return nonNull(salesOrg) && StringUtils.equalsIgnoreCase(DistSalesOrgEnum.EXPORT.getCode(), salesOrg.getCode());
    }

}
