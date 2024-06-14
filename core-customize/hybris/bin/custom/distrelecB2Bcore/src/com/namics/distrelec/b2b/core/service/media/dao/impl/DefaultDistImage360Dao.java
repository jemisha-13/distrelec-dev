/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.media.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistImage360Model;
import com.namics.distrelec.b2b.core.service.media.dao.DistImage360Dao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * Default Implementation of {@link DistImage360Dao}
 * 
 * @author pforster, Namics AG
 * @since Distrelec 3.0.0
 * 
 */
public class DefaultDistImage360Dao implements DistImage360Dao {

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Override
    public DistImage360Model findImage360ByCode(CatalogVersionModel catalogVersion, String image360Code) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT {").append(DistImage360Model.PK).append("} FROM {").append(DistImage360Model._TYPECODE).append("} ");
        query.append("WHERE {").append(DistImage360Model.CATALOGVERSION).append("} = (?").append(DistImage360Model.CATALOGVERSION).append(") ");
        query.append("AND {").append(DistImage360Model.CODE).append("} = (?").append(DistImage360Model.CODE).append(")");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(DistImage360Model.CATALOGVERSION, catalogVersion);
        params.put(DistImage360Model.CODE, image360Code);

        SearchResult<DistImage360Model> searchResult = flexibleSearchService.<DistImage360Model> search(query.toString(), params);
        List<DistImage360Model> result = searchResult.getResult();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.get(0);
    }
}
