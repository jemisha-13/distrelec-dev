/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.response;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.namics.hybris.ffsearch.data.search.AdvisorStatusData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;
import com.namics.hybris.ffsearch.populator.common.PriceFilterTranslator;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfFilter;
import de.factfinder.webservice.ws71.FFsearch.Filter;
import de.factfinder.webservice.ws71.FFsearch.FilterValue;
import de.factfinder.webservice.ws71.FFsearch.Params;
import de.hybris.platform.converters.Populator;

/**
 * Populator for the query and filter terms.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class ResponseQueryDataPopulator implements Populator<Params, SearchQueryData> {

    @Override
    public void populate(final Params source, final SearchQueryData target) {
        Assert.notNull(target, "Target should not be null.");
        target.setFreeTextSearch(getFreeTextSearch(source));
        buildSearchQueryTerms(source.getFilters(), target);
        target.setAdvisorStatus(buildAdvisorStatus(source));
    }

    private String getFreeTextSearch(final Params source) {
        String query = source.getQuery();
        if (StringUtils.isBlank(query)) {
            query = "*";
        }
        return query;
    }

    private void buildSearchQueryTerms(final ArrayOfFilter filters, final SearchQueryData result) {
        final List<SearchQueryTermData> terms = new ArrayList<SearchQueryTermData>();
        if (filters == null) {
            result.setFilterTerms(terms);
            return;
        }
        for (final Filter filter : filters.getFilter()) {
            if (filter.getValueList().getFilterValue().isEmpty()) {
                continue;
            }

            for (final FilterValue filterValue : filter.getValueList().getFilterValue()) {
                final SearchQueryTermData term = createSearchQueryTermData();
                term.setKey(PriceFilterTranslator.getPriceSensitiveFacetName(filter.getName()));
                term.setValue(filterValue.getValue());
                term.setSubstring(filter.isSubstring());
                terms.add(term);
            }
        }
        result.setFilterTerms(terms);
    }

    protected SearchQueryTermData createSearchQueryTermData() {
        return new SearchQueryTermData();
    }

    private AdvisorStatusData buildAdvisorStatus(final Params source) {
        AdvisorStatusData advisorStatus = null;

        if (source.getAdvisorStatus() != null && source.getAdvisorStatus().getAnswerPath() != null && source.getAdvisorStatus().getCampaignId() != null) {

            advisorStatus = new AdvisorStatusData();
            advisorStatus.setAnswerPath(source.getAdvisorStatus().getAnswerPath());
            advisorStatus.setCampaignId(source.getAdvisorStatus().getCampaignId());
        }

        return advisorStatus;
    }

}
