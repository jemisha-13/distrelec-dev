/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter;

import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.populator.common.SortCodeTranslator;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import java.util.List;

import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.*;

/**
 * Populates the query url to the {@link SearchResultValueData} list. <br>
 * Also adds the manufactorer code (if needed) to the values list, so it can be used later in the product search result populator
 * (SearchResultUrlPopulator).
 *
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 *
 * @param <ITEM>
 */
public class StateResultPopulator<ITEM> implements Populator<FactFinderProductSearchPageData<SearchStateData, ITEM>, List<SearchResultValueData>> {

    @Override
    public void populate(final FactFinderProductSearchPageData<SearchStateData, ITEM> source, final List<SearchResultValueData> target) {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notEmpty(target, "Parameter target cannot be empty.");
        final String query = getQueryValue(source);
        for (final SearchResultValueData valueData : target) {
            valueData.getValues().put(QUERY, query);
            if (DistSearchType.MANUFACTURER.equals(source.getSearchType())) {
                // set manufacturer code
                valueData.getValues().put(MANUFACTURER_CODE, source.getCode());
            }
        }

    }

    private String getQueryValue(final FactFinderProductSearchPageData<SearchStateData, ITEM> source) {
        final StringBuilder query = new StringBuilder();
        final String filters = source.getCurrentQuery().getQuery().getValue();
        if (StringUtils.isNotBlank(filters)) {
            query.append("?").append(SEARCH_QUERY_PARAMETER_NAME).append("=").append(filters);
        }
        final String[] sortKeyValue = StringUtils.split(source.getPagination().getSort(), ':');
        if (needToAppendSortParam(filters, sortKeyValue)) {
            query.append("&").append(SORT_PARAMETER_NAME).append("=").append(SortCodeTranslator.getSortName(sortKeyValue[0])).append(':')
                    .append(sortKeyValue[1]);
        }
        return query.toString();
    }

    private boolean needToAppendSortParam(final String query, final String[] sortKeyValue) {
        final String compareStringSort = SORT_PARAMETER_NAME + "=";
        return !StringUtils.contains(query, compareStringSort) && sortKeyValue != null && sortKeyValue.length == 2;
    }
}
