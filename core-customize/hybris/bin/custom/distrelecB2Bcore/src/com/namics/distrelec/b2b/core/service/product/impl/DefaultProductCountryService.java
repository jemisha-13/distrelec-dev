/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.model.ProductCountryModel;
import com.namics.distrelec.b2b.core.service.product.ProductCountryService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

public class DefaultProductCountryService extends AbstractBusinessService implements ProductCountryService {

    private static final Logger LOG = Logger.getLogger(DefaultProductCountryService.class);

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private DistrelecCMSSiteService cmsSiteService;

    @Override
    public ProductCountryModel getProductCountry(final CountryModel country, final ProductModel product) {
        Assert.notNull(country, "Country cannot be null");
        Assert.notNull(product, "Product cannot be null");

        final StringBuilder query = new StringBuilder();
        query.append("SELECT {").append(ProductCountryModel._TYPECODE).append(".").append(ProductCountryModel.PK).append("} FROM {")
                .append(ProductCountryModel._TYPECODE).append("} WHERE {").append(ProductCountryModel._TYPECODE).append(".")
                .append(ProductCountryModel.COUNTRY).append("}=(?").append(ProductCountryModel.COUNTRY).append(") AND {").append(ProductCountryModel._TYPECODE)
                .append(".").append(ProductCountryModel.PRODUCT).append("}=(?").append(ProductCountryModel.PRODUCT).append(")");

        final Map<String, Object> params = new HashMap<String, Object>();
        params.put(ProductCountryModel.COUNTRY, country);
        params.put(ProductCountryModel.PRODUCT, product);

        final SearchResult<ProductCountryModel> result = flexibleSearchService.search(query.toString(), params);

        if (result.getTotalCount() > 1) {
            LOG.warn("More than one ProductCountry found for country " + country.getIsocode() + " and Product code " + product.getCode());
        } else if (result.getTotalCount() <= 0) {
            LOG.debug("No ProductCountry found for country " + country.getIsocode() + " and Product code " + product.getCode());
            return null;
        }

        return result.getResult().iterator().next();
    }

    @Override
    public ProductCountryModel getCurrentProductCountry(final ProductModel product) {
        final CMSSiteModel cmsSite = cmsSiteService.getCurrentSite();
        if (cmsSite != null && cmsSite.getCountry() != null) {
            return getProductCountry(cmsSite.getCountry(), product);
        } else if (cmsSite != null) {
            LOG.error("Country on CMSSite with uid " + cmsSite.getUid() + " has no country set.");
        }
        return null;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public DistrelecCMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final DistrelecCMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

}
