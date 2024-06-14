/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * Default implementation of {@link DistSalesOrgProductService}
 * 
 * @author sivakumaran, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class DefaultDistSalesOrgProductService extends AbstractBusinessService implements DistSalesOrgProductService {

    private static final Logger LOG = Logger.getLogger(DefaultDistSalesOrgProductService.class);

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    /**
     * {@inheritDoc}
     */
    @Override
    public DistSalesOrgProductModel getSalesOrgProduct(final ProductModel product, final DistSalesOrgModel salesOrg) {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT {").append(DistSalesOrgProductModel._TYPECODE).append(".").append(DistSalesOrgProductModel.PK).append("} FROM {")
                .append(DistSalesOrgProductModel._TYPECODE).append("} WHERE {").append(DistSalesOrgProductModel._TYPECODE).append(".")
                .append(DistSalesOrgProductModel.PRODUCT).append("} = (?").append(ProductModel._TYPECODE).append(") AND {")
                .append(DistSalesOrgProductModel._TYPECODE).append(".").append(DistSalesOrgProductModel.SALESORG).append("} = (?")
                .append(DistSalesOrgModel._TYPECODE).append(")");

        LOG.debug(query.toString());
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put(ProductModel._TYPECODE, product);
        params.put(DistSalesOrgModel._TYPECODE, salesOrg);

        final FlexibleSearchQuery fsQuery = new FlexibleSearchQuery(query.toString());
        fsQuery.addQueryParameters(params);
        final SearchResult<DistSalesOrgProductModel> result = getFlexibleSearchService().search(fsQuery);
        if (CollectionUtils.isNotEmpty(result.getResult())) {
            return result.getResult().iterator().next();
        }
        return null;
    }

    @Override
    public DistSalesOrgProductModel getCurrentSalesOrgProduct(final ProductModel product) {
        final DistSalesOrgModel salesOrg = distSalesOrgService.getCurrentSalesOrg();
        if (salesOrg != null) {
            return getSalesOrgProduct(product, salesOrg);
        }
        return null;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

}
