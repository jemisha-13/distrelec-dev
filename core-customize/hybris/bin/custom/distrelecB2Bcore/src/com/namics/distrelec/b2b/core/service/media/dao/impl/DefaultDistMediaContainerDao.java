/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.media.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.namics.distrelec.b2b.core.service.media.dao.DistMediaContainerDao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.servicelayer.media.dao.impl.DefaultMediaContainerDao;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * Default implementation of {@link DistMediaContainerDao}.
 * 
 * @author daehusir, Distrelec
 * @since Distrelec 1.1
 * 
 */
public class DefaultDistMediaContainerDao extends DefaultMediaContainerDao implements DistMediaContainerDao {

    @Override
    public List<MediaContainerModel> findMediaContainersByQualifier(final CatalogVersionModel catalogVersion, final String qualifier) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put(MediaContainerModel.QUALIFIER, qualifier);
        params.put(MediaContainerModel.CATALOGVERSION, catalogVersion);

        final StringBuilder builder = new StringBuilder();
        builder.append("SELECT {").append(MediaContainerModel.PK).append("} ");
        builder.append("FROM {").append(MediaContainerModel._TYPECODE).append("} ");
        builder.append("WHERE {").append(MediaContainerModel.CATALOGVERSION).append("}=?catalogVersion ");
        builder.append("AND {").append(MediaContainerModel.QUALIFIER).append("}=?qualifier ");
        builder.append("ORDER BY {").append(MediaContainerModel.PK).append("} ASC");
        final SearchResult<MediaContainerModel> result = getFlexibleSearchService().search(builder.toString(), params);
        return result.getResult();
    }

}
