/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.productfinder.dao.impl;

import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderConfigurationModel;
import com.namics.distrelec.b2b.core.service.productfinder.dao.DistProductFinderDao;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * Default implementation of product finder DAO.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DefaultDistProductFinderDao extends AbstractItemDao implements DistProductFinderDao {

    private static final Logger LOG = Logger.getLogger(DefaultDistProductFinderDao.class);

    @Override
    public DistProductFinderConfigurationModel getProductFinderConfigurationByCategory(final CategoryModel category) {
        DistProductFinderConfigurationModel result = null;

        final StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT {").append(DistProductFinderConfigurationModel.PK).append("} FROM {").append(DistProductFinderConfigurationModel._TYPECODE)
                .append("} WHERE {").append(DistProductFinderConfigurationModel.CATEGORY).append("} = ?").append(DistProductFinderConfigurationModel.CATEGORY);

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());
        query.addQueryParameter(DistProductFinderConfigurationModel.CATEGORY, category);
        final SearchResult<DistProductFinderConfigurationModel> searchResult = getFlexibleSearchService().search(query);

        if (searchResult.getCount() > 1) {
            LOG.error("More than one DistProductFinderConfiguration found for Category with pk [" + category.getPk() + "] and code [" + category.getCode()
                    + "]");
        } else if (searchResult.getCount() == 1) {
            result = searchResult.getResult().get(0);
        }

        return result;
    }
}
