/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.mail.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

public abstract class AbstractReportDao extends AbstractItemDao {

    private static final Logger LOG = LogManager.getLogger(AbstractReportDao.class);

    public List<List<String>> getQueryResult() {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(getQuery());
        final List<Class<String>> resultClassList = Collections.nCopies(getResultCount(), String.class);
        LOG.debug("resultClassList: {}", resultClassList);
        query.setResultClassList(resultClassList);
        final SearchResult<List<String>> searchResult = getFlexibleSearchService().<List<String>> search(query);
        LOG.debug("searchResult: {}", searchResult);
        final List<List<String>> data = searchResult.getResult();
        LOG.debug("New Data is {}", Arrays.toString(data.stream().map(l -> Arrays.toString(l.toArray())).toArray()));
        return data;
    }

    protected abstract String getQuery();

    protected abstract int getResultCount();
}
