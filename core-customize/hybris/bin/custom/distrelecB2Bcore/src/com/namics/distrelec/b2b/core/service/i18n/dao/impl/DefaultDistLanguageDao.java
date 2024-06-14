/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.i18n.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.service.i18n.dao.DistLanguageDao;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.daos.impl.DefaultLanguageDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

/**
 * Interface for language DAO.
 * 
 * @author csieber, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class DefaultDistLanguageDao extends DefaultLanguageDao implements DistLanguageDao {

    private static final Logger LOG = Logger.getLogger(DefaultDistLanguageDao.class);

    @Override
    public LanguageModel findLanguageByIsocodePim(final String isocodePim) {
        validateParameterNotNull(isocodePim, "isocodePim must not be null!");

        final StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT {").append(LanguageModel.PK).append("} FROM {").append(LanguageModel._TYPECODE).append("} ").append("WHERE {")
                .append(LanguageModel.ISOCODEPIM).append("} = ?").append(LanguageModel.ISOCODEPIM);
        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());
        query.addQueryParameter(LanguageModel.ISOCODEPIM, isocodePim);
        final List<LanguageModel> result = getFlexibleSearchService().<LanguageModel> search(query).getResult();
        if (CollectionUtils.isNotEmpty(result)) {
            if (result.size() > 1) {
                LOG.debug("Found multiple LanguageModel for isocodePim '" + isocodePim + "'");
            }
            return result.get(0);
        }

        return null;
    }
}
