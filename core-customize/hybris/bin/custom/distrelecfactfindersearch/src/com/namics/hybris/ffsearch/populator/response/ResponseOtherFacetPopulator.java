/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.response;

import com.namics.hybris.ffsearch.data.facet.FactFinderFacetData;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchResponse;
import de.factfinder.webservice.ws71.FFsearch.ArrayOfGroupElement;
import de.factfinder.webservice.ws71.FFsearch.Group;
import de.factfinder.webservice.ws71.FFsearch.GroupElement;
import de.factfinder.webservice.ws71.FFsearch.Result;
import de.hybris.platform.converters.Populator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.CATEGORY_CODE_PATTERN;

/**
 * Populates other than category facets.
 * 
 * @author ceberle, Namics AG
 * @since Namics Extensions 1.0
 * 
 * @param <ITEM>
 */
public class ResponseOtherFacetPopulator<ITEM> implements Populator<SearchResponse, FactFinderFacetSearchPageData<SearchQueryData, ITEM>> {

    private ResponseFacetPopulator responseFacetPopulator;

    @Override
    public void populate(final SearchResponse source, final FactFinderFacetSearchPageData<SearchQueryData, ITEM> target) {
        final Result searchResult = source.getSearchResult();
        final SearchQueryData queryData = target.getCurrentQuery();
        target.setOtherFacets(buildOtherFacets(searchResult, queryData));
    }

    protected List<FactFinderFacetData<SearchQueryData>> buildOtherFacets(final Result searchResult, final SearchQueryData queryData) {
        final List<FactFinderFacetData<SearchQueryData>> target = new ArrayList<>();
        if (searchResult.getFilters() == null) {
            return target;
        }
        final List<Group> source = getOtherFacets(searchResult);
        getResponseFacetPopulator().populate(searchResult.getResultCount().longValue(), source, queryData, target);
        return target;
    }

    private List<Group> getOtherFacets(final Result searchResult) {
        final List<Group> otherFacets = new ArrayList<>();

        for (final Group group : searchResult.getGroups().getGroup()) {
            // Check if associatedFieldName of first GroupElement does NOT match the category pattern
            final String associatedFieldName = getAssociatedFieldName(group);
            if (StringUtils.isNotEmpty(associatedFieldName)) {
                if (StringUtils.isNotBlank(associatedFieldName) && !CATEGORY_CODE_PATTERN.matcher(associatedFieldName).matches()) {
                    otherFacets.add(group);
                }
            }
        }
        return otherFacets;
    }

    /**
     * returns the associatedFieldName from the first group entry.<br>
     * if there is no unselected element, check the selected elements.
     * 
     * @param group
     *            group to check
     * @return the associatedFieldName of the group or an empty String if the name could not be retrieved
     * 
     * @since Distrelec 2.0.17 (DISTRELEC-4627)
     */
    private String getAssociatedFieldName(final Group group) {
        if (group != null) {
            // check for unselected elements
            final ArrayOfGroupElement unselectedElements = group.getElements();
            if (unselectedElements != null) {
                final List<GroupElement> groupElements = unselectedElements.getGroupElement();
                if (CollectionUtils.isNotEmpty(groupElements) && groupElements.get(0) != null) {
                    return groupElements.get(0).getAssociatedFieldName();
                }
            }

            // check for selected elements
            final ArrayOfGroupElement selectedElements = group.getSelectedElements();
            if (selectedElements != null) {
                final List<GroupElement> groupElements = selectedElements.getGroupElement();
                if (CollectionUtils.isNotEmpty(groupElements) && groupElements.get(0) != null) {
                    return groupElements.get(0).getAssociatedFieldName();
                }
            }

        }
        return StringUtils.EMPTY;
    }

    // BEGIN GENERATED CODE

    public ResponseFacetPopulator getResponseFacetPopulator() {
        return responseFacetPopulator;
    }

    public void setResponseFacetPopulator(final ResponseFacetPopulator responseFacetPopulator) {
        this.responseFacetPopulator = responseFacetPopulator;
    }

}
