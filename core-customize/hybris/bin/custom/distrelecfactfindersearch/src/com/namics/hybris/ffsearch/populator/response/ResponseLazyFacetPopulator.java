/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.response;

import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.CATEGORY_CODE_PATTERN;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.namics.hybris.ffsearch.data.facet.FactFinderFacetSearchPageData;
import com.namics.hybris.ffsearch.data.facet.FactFinderLazyFacetData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchResponse;

import de.factfinder.webservice.ws71.FFsearch.Group;
import de.factfinder.webservice.ws71.FFsearch.Result;
import de.hybris.platform.converters.Populator;

/**
 * Populates facets which can be manually reloaded.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 * 
 * @param <ITEM>
 */
public class ResponseLazyFacetPopulator<ITEM> implements Populator<SearchResponse, FactFinderFacetSearchPageData<SearchQueryData, ITEM>> {

    @Override
    public void populate(final SearchResponse source, final FactFinderFacetSearchPageData<SearchQueryData, ITEM> target) {
        final Result searchResult = source.getSearchResult();
        final SearchQueryData queryData = target.getCurrentQuery();
        target.setLazyFacets(buildLazyFacets(searchResult, queryData));
    }

    private List<FactFinderLazyFacetData<SearchQueryData>> buildLazyFacets(final Result searchResult, final SearchQueryData queryData) {
        final List<FactFinderLazyFacetData<SearchQueryData>> lazyFacets = new ArrayList<FactFinderLazyFacetData<SearchQueryData>>();

        final List<Group> lazyGroups = getLazyGroups(searchResult);
        for (final Group lazyGroup : lazyGroups) {
            final FactFinderLazyFacetData<SearchQueryData> lazyFacet = new FactFinderLazyFacetData<SearchQueryData>();
            lazyFacet.setName(lazyGroup.getName());
            lazyFacet.setUnit(lazyGroup.getUnit());
            lazyFacet.setQuery(queryData);
            lazyFacets.add(lazyFacet);
        }

        return lazyFacets;
    }

    private List<Group> getLazyGroups(final Result searchResult) {
        final List<Group> lazyGroups = new ArrayList<Group>();
        if (searchResult != null && searchResult.getGroups() != null && searchResult.getGroups().getGroup() != null) {
            for (final Group group : searchResult.getGroups().getGroup()) {
                if (isLazyGroup(group)) {
                    lazyGroups.add(group);
                }
            }
        }
        return lazyGroups;
    }

    /**
     * A lazy facet group contains a single empty Group element.
     */
    private boolean isLazyGroup(final Group group) {
        if (CATEGORY_CODE_PATTERN.matcher(group.getName()).matches()) {
            return false;
        }

        if (group.getElements() == null || group.getElements().getGroupElement() == null || group.getElements().getGroupElement().size() != 1) {
            return false;
        }

        return group.getElements().getGroupElement().get(0) != null && StringUtils.isBlank(group.getElements().getGroupElement().get(0).getName());
    }

}
